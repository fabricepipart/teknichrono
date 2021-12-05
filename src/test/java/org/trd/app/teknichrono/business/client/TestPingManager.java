package org.trd.app.teknichrono.business.client;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.trd.app.teknichrono.model.jpa.Beacon;
import org.trd.app.teknichrono.model.jpa.Chronometer;
import org.trd.app.teknichrono.model.jpa.LapTime;
import org.trd.app.teknichrono.model.jpa.Pilot;
import org.trd.app.teknichrono.model.jpa.Ping;
import org.trd.app.teknichrono.model.jpa.Session;
import org.trd.app.teknichrono.model.repository.LapTimeRepository;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class TestPingManager {

  private long laptimeIndex = 1L;

  private final Pilot pilot = new Pilot();
  private Instant start = Instant.now();

  private Session session = new Session();
  private Chronometer c0 = new Chronometer();
  private Chronometer c1 = new Chronometer();
  private Chronometer c2 = new Chronometer();
  private Chronometer c3 = new Chronometer();

  @Mock
  private Logger logger;

  @Mock
  private SessionSelector selector;

  @Mock
  private LapTimeRepository lapTimeRepository;

  @InjectMocks
  private PingManager testMe = new PingManager(this.lapTimeRepository);
  private final Beacon beacon = new Beacon();

  @BeforeEach
  public void prepare() {
    this.beacon.setPilot(this.pilot);
    this.session.setStart(this.start);
    this.c0.id = 10L;
    this.c1.id = 11L;
    this.c2.id = 12L;
    this.c3.id = 13L;
    this.session.getChronometers().add(this.c0);
    this.session.getChronometers().add(this.c1);
    this.session.getChronometers().add(this.c2);
    this.session.getChronometers().add(this.c3);
    when(this.selector.pickMostRelevant(any(Ping.class))).thenReturn(this.session);
  }

  private Ping createPing(long timeFromStart, Chronometer c) {
    Ping p = new Ping();
    p.setInstant(this.start.plus(Duration.ofMillis(timeFromStart)));
    p.setBeacon(this.beacon);
    p.setChrono(c);
    p.id = c.id + timeFromStart;
    return p;
  }

  private void pilotHasLapWith(long l0, long l1, long l2, long l3) {
    LapTime l = new LapTime();
    l.id = this.laptimeIndex++;
    l.setPilot(this.pilot);
    l.setSession(this.session);
    if (l0 >= 0) {
      l.addIntermediates(createPing(l0, this.c0));
    }
    if (l1 >= 0) {
      l.addIntermediates(createPing(l1, this.c1));
    }
    if (l2 >= 0) {
      l.addIntermediates(createPing(l2, this.c2));
    }
    if (l3 >= 0) {
      l.addIntermediates(createPing(l3, this.c3));
    }
    this.pilot.getLaps().add(l);
  }

  @Test
  public void pingMustReferBeacon() {
    Ping p = new Ping();
    this.testMe.addPing(p);
    verifyNoInteractions(this.lapTimeRepository);
    verify(this.logger, atLeastOnce()).error(any());
  }

  @Test
  public void pingMustReferPilot() {
    Ping p = new Ping();
    p.setBeacon(new Beacon());
    this.testMe.addPing(p);
    verifyNoInteractions(this.lapTimeRepository);
    verify(this.logger, atLeastOnce()).error(any());
  }

  @Test
  public void pingMustReferChronometer() {
    Ping p = new Ping();
    Beacon b = new Beacon();
    b.setPilot(new Pilot());
    p.setBeacon(b);
    this.testMe.addPing(p);
    verifyNoInteractions(this.lapTimeRepository);
    verify(this.logger, atLeastOnce()).error(any());
  }

  @Test
  public void failsIfNoSessionMatches() {
    Ping p = createPing(0, this.c0);
    when(this.selector.pickMostRelevant(any(Ping.class))).thenReturn(null);
    this.testMe.addPing(p);
    verifyNoInteractions(this.lapTimeRepository);
    verify(this.logger, atLeastOnce()).error(any());
  }

  @Test
  public void canAddFirstPing() {
    Ping p = createPing(0, this.c0);
    this.testMe.addPing(p);
    ArgumentCaptor<LapTime> captor = ArgumentCaptor.forClass(LapTime.class);
    verify(this.lapTimeRepository).persist(captor.capture());
    verify(this.logger, never()).error(any());
    LapTime lap = captor.getValue();
    assertThat(lap).isNotNull();
    assertThat(lap.getPilot()).isEqualTo(this.pilot);
    assertThat(lap.getStartDate()).isEqualTo(this.start);
    assertThat(lap.getSession()).isEqualTo(this.session);
    assertThat(lap.getIntermediates()).contains(p);
    assertThat(lap.getIntermediates().size()).isEqualTo(1);
  }

  @Test
  public void pingMustReferChronometerThatIsPartOfSession() {
    Chronometer c = new Chronometer();
    c.id = 666L;
    Ping p = createPing(0, c);
    this.testMe.addPing(p);
    verifyNoInteractions(this.lapTimeRepository);
    verify(this.logger, atLeastOnce()).error(any());
  }

  @Test
  public void ignorePingIfWithinInactivity() {
    this.session.setInactivity(10000);
    Ping p = createPing(5000, this.c0);
    this.testMe.addPing(p);
    verifyNoInteractions(this.lapTimeRepository);
  }

  @Test
  public void failsIfPingSentTwice() {
    LapTime l = new LapTime();
    Ping p = createPing(0, this.c0);
    l.addIntermediates(p);
    this.pilot.getLaps().add(l);
    this.testMe.addPing(p);
    verify(this.lapTimeRepository, never()).persist(any());
    verify(this.logger, atLeastOnce()).error(any());
  }

  @Test
  public void pingsCanBeAddedInRightOrder1() {
    Ping p1 = createPing(10000, this.c1);
    pilotHasLapWith(0, -1, -1, -1);
    this.testMe.addPing(p1);
    ArgumentCaptor<LapTime> captor = ArgumentCaptor.forClass(LapTime.class);
    verify(this.lapTimeRepository).persist(captor.capture());
    verify(this.logger, never()).error(any());
    LapTime lap = captor.getValue();
    assertThat(lap).isNotNull();
    assertThat(lap.getPilot()).isEqualTo(this.pilot);
    assertThat(lap.getStartDate()).isEqualTo(this.start);
    assertThat(lap.getSession()).isEqualTo(this.session);
    assertThat(lap.getIntermediates().size()).isEqualTo(2);
    assertThat(lap.getIntermediates()).contains(p1);
    assertThat(lap.getIntermediates().indexOf(p1)).isEqualTo(1);
  }

  @Test
  public void pingsCanBeAddedInRightOrder2() {
    Ping p2 = createPing(20000, this.c2);
    pilotHasLapWith(0, 10000, -1, -1);
    this.testMe.addPing(p2);
    ArgumentCaptor<LapTime> captor = ArgumentCaptor.forClass(LapTime.class);
    verify(this.lapTimeRepository).persist(captor.capture());
    verify(this.logger, never()).error(any());
    LapTime lap = captor.getValue();
    assertThat(lap).isNotNull();
    assertThat(lap.getPilot()).isEqualTo(this.pilot);
    assertThat(lap.getStartDate()).isEqualTo(this.start);
    assertThat(lap.getSession()).isEqualTo(this.session);
    assertThat(lap.getIntermediates()).contains(p2);
    assertThat(lap.getIntermediates().size()).isEqualTo(3);
    assertThat(lap.getIntermediates().indexOf(p2)).isEqualTo(2);
  }

  @Test
  public void pingsCanBeAddedInRightOrder3() {
    pilotHasLapWith(0, 10000, 20000, -1);
    Ping p3 = createPing(30000, this.c3);
    this.testMe.addPing(p3);
    ArgumentCaptor<LapTime> captor = ArgumentCaptor.forClass(LapTime.class);
    verify(this.lapTimeRepository).persist(captor.capture());
    verify(this.logger, never()).error(any());
    LapTime lap = captor.getValue();
    assertThat(lap).isNotNull();
    assertThat(lap.getPilot()).isEqualTo(this.pilot);
    assertThat(lap.getStartDate()).isEqualTo(this.start);
    assertThat(lap.getSession()).isEqualTo(this.session);
    assertThat(lap.getIntermediates()).contains(p3);
    assertThat(lap.getIntermediates().size()).isEqualTo(4);
    assertThat(lap.getIntermediates().indexOf(p3)).isEqualTo(3);
  }

  @Test
  public void pingsCanBeAddedInReverseOrder1() {
    Ping p3 = createPing(30000, this.c3);
    this.testMe.addPing(p3);
    ArgumentCaptor<LapTime> captor = ArgumentCaptor.forClass(LapTime.class);
    verify(this.lapTimeRepository).persist(captor.capture());
    verify(this.logger, never()).error(any());
    LapTime lap = captor.getValue();
    assertThat(lap).isNotNull();
    assertThat(lap.getPilot()).isEqualTo(this.pilot);
    assertThat(lap.getStartDate()).isEqualTo(p3.getInstant());
    assertThat(lap.getSession()).isEqualTo(this.session);
    assertThat(lap.getIntermediates()).contains(p3);
    assertThat(lap.getIntermediates().size()).isEqualTo(1);
    assertThat(lap.getIntermediates().indexOf(p3)).isEqualTo(0);
  }

  @Test
  public void pingsCanBeAddedInReverseOrder2() {
    pilotHasLapWith(-1, -1, -1, 30000);
    Ping p2 = createPing(20000, this.c2);
    this.testMe.addPing(p2);
    ArgumentCaptor<LapTime> captor = ArgumentCaptor.forClass(LapTime.class);
    verify(this.lapTimeRepository).persist(captor.capture());
    verify(this.logger, never()).error(any());
    LapTime lap = captor.getValue();
    assertThat(lap).isNotNull();
    assertThat(lap.getPilot()).isEqualTo(this.pilot);
    assertThat(lap.getStartDate()).isEqualTo(p2.getInstant());
    assertThat(lap.getSession()).isEqualTo(this.session);
    assertThat(lap.getIntermediates()).contains(p2);
    assertThat(lap.getIntermediates().size()).isEqualTo(2);
    assertThat(lap.getIntermediates().indexOf(p2)).isEqualTo(0);
  }

  @Test
  public void pingsCanBeAddedInReverseOrder3() {
    pilotHasLapWith(-1, -1, 20000, 30000);
    Ping p1 = createPing(10000, this.c1);
    this.testMe.addPing(p1);
    ArgumentCaptor<LapTime> captor = ArgumentCaptor.forClass(LapTime.class);
    verify(this.lapTimeRepository).persist(captor.capture());
    verify(this.logger, never()).error(any());
    LapTime lap = captor.getValue();
    assertThat(lap).isNotNull();
    assertThat(lap.getPilot()).isEqualTo(this.pilot);
    assertThat(lap.getStartDate()).isEqualTo(p1.getInstant());
    assertThat(lap.getSession()).isEqualTo(this.session);
    assertThat(lap.getIntermediates()).contains(p1);
    assertThat(lap.getIntermediates().size()).isEqualTo(3);
    assertThat(lap.getIntermediates().indexOf(p1)).isEqualTo(0);
  }

  @Test
  public void pingsCanBeAddedInReverseOrder4() {
    pilotHasLapWith(-1, 10000, 20000, 30000);
    Ping p0 = createPing(0, this.c0);
    this.testMe.addPing(p0);
    ArgumentCaptor<LapTime> captor = ArgumentCaptor.forClass(LapTime.class);
    verify(this.lapTimeRepository).persist(captor.capture());
    verify(this.logger, never()).error(any());
    LapTime lap = captor.getValue();
    assertThat(lap).isNotNull();
    assertThat(lap.getPilot()).isEqualTo(this.pilot);
    assertThat(lap.getStartDate()).isEqualTo(p0.getInstant());
    assertThat(lap.getSession()).isEqualTo(this.session);
    assertThat(lap.getIntermediates()).contains(p0);
    assertThat(lap.getIntermediates().size()).isEqualTo(4);
    assertThat(lap.getIntermediates().indexOf(p0)).isEqualTo(0);
  }

  @Test
  public void pingsCanBeAddedInRandomOrder1() {
    Ping p2 = createPing(20000, this.c2);
    this.testMe.addPing(p2);
    ArgumentCaptor<LapTime> captor = ArgumentCaptor.forClass(LapTime.class);
    verify(this.lapTimeRepository).persist(captor.capture());
    verify(this.logger, never()).error(any());
    LapTime lap = captor.getValue();
    assertThat(lap).isNotNull();
    assertThat(lap.getPilot()).isEqualTo(this.pilot);
    assertThat(lap.getStartDate()).isEqualTo(p2.getInstant());
    assertThat(lap.getSession()).isEqualTo(this.session);
    assertThat(lap.getIntermediates()).contains(p2);
    assertThat(lap.getIntermediates().size()).isEqualTo(1);
    assertThat(lap.getIntermediates().indexOf(p2)).isEqualTo(0);
  }

  @Test
  public void pingsCanBeAddedInRandomOrder2() {
    pilotHasLapWith(-1, -1, 20000, -1);
    Ping p0 = createPing(0, this.c0);
    this.testMe.addPing(p0);
    ArgumentCaptor<LapTime> captor = ArgumentCaptor.forClass(LapTime.class);
    verify(this.lapTimeRepository).persist(captor.capture());
    verify(this.logger, never()).error(any());
    LapTime lap = captor.getValue();
    assertThat(lap).isNotNull();
    assertThat(lap.getPilot()).isEqualTo(this.pilot);
    assertThat(lap.getStartDate()).isEqualTo(p0.getInstant());
    assertThat(lap.getSession()).isEqualTo(this.session);
    assertThat(lap.getIntermediates()).contains(p0);
    assertThat(lap.getIntermediates().size()).isEqualTo(2);
    assertThat(lap.getIntermediates().indexOf(p0)).isEqualTo(0);
  }

  @Test
  public void pingsCanBeAddedInRandomOrder3() {
    pilotHasLapWith(0, -1, 20000, -1);
    Ping p3 = createPing(30000, this.c3);
    this.testMe.addPing(p3);
    ArgumentCaptor<LapTime> captor = ArgumentCaptor.forClass(LapTime.class);
    verify(this.lapTimeRepository).persist(captor.capture());
    verify(this.logger, never()).error(any());
    LapTime lap = captor.getValue();
    assertThat(lap).isNotNull();
    assertThat(lap.getPilot()).isEqualTo(this.pilot);
    assertThat(lap.getStartDate()).isEqualTo(this.start);
    assertThat(lap.getSession()).isEqualTo(this.session);
    assertThat(lap.getIntermediates()).contains(p3);
    assertThat(lap.getIntermediates().size()).isEqualTo(3);
    assertThat(lap.getIntermediates().indexOf(p3)).isEqualTo(2);
  }

  @Test
  public void pingsCanBeAddedInRandomOrder4() {
    pilotHasLapWith(0, -1, 20000, 30000);
    Ping p1 = createPing(10000, this.c1);
    this.testMe.addPing(p1);
    ArgumentCaptor<LapTime> captor = ArgumentCaptor.forClass(LapTime.class);
    verify(this.lapTimeRepository).persist(captor.capture());
    verify(this.logger, never()).error(any());
    LapTime lap = captor.getValue();
    assertThat(lap).isNotNull();
    assertThat(lap.getPilot()).isEqualTo(this.pilot);
    assertThat(lap.getStartDate()).isEqualTo(this.start);
    assertThat(lap.getSession()).isEqualTo(this.session);
    assertThat(lap.getIntermediates()).contains(p1);
    assertThat(lap.getIntermediates().size()).isEqualTo(4);
    assertThat(lap.getIntermediates().indexOf(p1)).isEqualTo(1);
  }


  @Test
  public void pingsCanStartNewLap() {
    pilotHasLapWith(0, 10000, 20000, 30000);
    Ping p1 = createPing(50000, this.c1);
    this.testMe.addPing(p1);
    ArgumentCaptor<LapTime> captor = ArgumentCaptor.forClass(LapTime.class);
    verify(this.lapTimeRepository).persist(captor.capture());
    verify(this.logger, never()).error(any());
    LapTime lap = captor.getValue();
    assertThat(lap).isNotNull();
    assertThat(lap.getPilot()).isEqualTo(this.pilot);
    assertThat(lap.getStartDate()).isEqualTo(p1.getInstant());
    assertThat(lap.getSession()).isEqualTo(this.session);
    assertThat(lap.getIntermediates()).contains(p1);
    assertThat(lap.getIntermediates().size()).isEqualTo(1);
    assertThat(lap.getIntermediates().indexOf(p1)).isEqualTo(0);
  }


  @Test
  public void pingsCanStartNewLapBefore() {
    pilotHasLapWith(-1, 50000, -1, 70000);
    Ping p1 = createPing(10000, this.c1);
    this.testMe.addPing(p1);
    ArgumentCaptor<LapTime> captor = ArgumentCaptor.forClass(LapTime.class);
    verify(this.lapTimeRepository).persist(captor.capture());
    verify(this.logger, never()).error(any());
    LapTime lap = captor.getValue();
    assertThat(lap).isNotNull();
    assertThat(lap.getPilot()).isEqualTo(this.pilot);
    assertThat(lap.getStartDate()).isEqualTo(p1.getInstant());
    assertThat(lap.getSession()).isEqualTo(this.session);
    assertThat(lap.getIntermediates()).contains(p1);
    assertThat(lap.getIntermediates().size()).isEqualTo(1);
    assertThat(lap.getIntermediates().indexOf(p1)).isEqualTo(0);
  }


  @Test
  public void pingsCanStartNewLapInBetween() {
    pilotHasLapWith(0, 10000, -1, -1);
    pilotHasLapWith(80000, 90000, -1, 110000);
    Ping p1 = createPing(50000, this.c1);
    this.testMe.addPing(p1);
    ArgumentCaptor<LapTime> captor = ArgumentCaptor.forClass(LapTime.class);
    verify(this.lapTimeRepository).persist(captor.capture());
    verify(this.logger, never()).error(any());
    LapTime lap = captor.getValue();
    assertThat(lap).isNotNull();
    assertThat(lap.getPilot()).isEqualTo(this.pilot);
    assertThat(lap.getStartDate()).isEqualTo(p1.getInstant());
    assertThat(lap.getSession()).isEqualTo(this.session);
    assertThat(lap.getIntermediates()).contains(p1);
    assertThat(lap.getIntermediates().size()).isEqualTo(1);
    assertThat(lap.getIntermediates().indexOf(p1)).isEqualTo(0);
  }


  @Test
  public void pingsCanFillLapInBetween() {
    pilotHasLapWith(0, 10000, -1, -1);
    pilotHasLapWith(40000, -1, 60000, 70000);
    pilotHasLapWith(80000, 90000, -1, 110000);
    Ping p1 = createPing(50000, this.c1);
    this.testMe.addPing(p1);
    ArgumentCaptor<LapTime> captor = ArgumentCaptor.forClass(LapTime.class);
    verify(this.lapTimeRepository).persist(captor.capture());
    verify(this.logger, never()).error(any());
    LapTime lap = captor.getValue();
    assertThat(lap).isNotNull();
    assertThat(lap.getPilot()).isEqualTo(this.pilot);
    assertThat(lap.getStartDate()).isEqualTo(this.start.plus(Duration.ofMillis(40000)));
    assertThat(lap.getSession()).isEqualTo(this.session);
    assertThat(lap.getIntermediates()).contains(p1);
    assertThat(lap.getIntermediates().size()).isEqualTo(4);
    assertThat(lap.getIntermediates().indexOf(p1)).isEqualTo(1);
  }


  @Test
  public void pingsCanSplitLap() {
    pilotHasLapWith(0, 50000, 60000, 70000);
    Ping p1 = createPing(10000, this.c1);
    this.testMe.addPing(p1);
    ArgumentCaptor<LapTime> captor = ArgumentCaptor.forClass(LapTime.class);
    verify(this.lapTimeRepository, times(2)).persist(captor.capture());
    verify(this.logger, never()).error(any());
    List<LapTime> laps = captor.getAllValues();
    boolean foundLapOne = false;
    boolean foundLapTwo = false;
    for (LapTime lap : laps) {
      assertThat(lap).isNotNull();
      assertThat(lap.getPilot()).isEqualTo(this.pilot);
      assertThat(lap.getSession()).isEqualTo(this.session);
      if (lap.getStartDate().equals(this.start)) {
        foundLapOne = true;
        assertThat(lap.getIntermediates()).contains(p1);
        assertThat(lap.getIntermediates().size()).isEqualTo(2);
        assertThat(lap.getIntermediates().indexOf(p1)).isEqualTo(1);
      } else if (lap.getStartDate().equals(this.start.plus(Duration.ofMillis(50000)))) {
        foundLapTwo = true;
        assertThat(lap.getIntermediates()).doesNotContain(p1);
        assertThat(lap.getIntermediates().size()).isEqualTo(3);
      }
    }
    assertThat(foundLapOne).isTrue();
    assertThat(foundLapTwo).isTrue();
  }

  @Test
  public void pingCanBeAddedToExistingLap() {
    pilotHasLapWith(-1, -1, -1, 30000);
    pilotHasLapWith(-1, -1, 100000, -1);
    Ping p1 = createPing(50000, this.c1);
    this.testMe.addPing(p1);
    ArgumentCaptor<LapTime> captor = ArgumentCaptor.forClass(LapTime.class);
    verify(this.lapTimeRepository).persist(captor.capture());
    LapTime lap = captor.getValue();
    assertThat(lap).isNotNull();
    assertThat(lap.getPilot()).isEqualTo(this.pilot);
    assertThat(lap.getStartDate()).isEqualTo(p1.getInstant());
    assertThat(lap.getSession()).isEqualTo(this.session);
    assertThat(lap.getIntermediates()).contains(p1);
    assertThat(lap.getIntermediates().size()).isEqualTo(2);
    assertThat(lap.getIntermediates().indexOf(p1)).isEqualTo(0);
  }


}