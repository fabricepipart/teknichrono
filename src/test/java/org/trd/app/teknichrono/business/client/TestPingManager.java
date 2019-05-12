package org.trd.app.teknichrono.business.client;

import org.jboss.logging.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.trd.app.teknichrono.model.jpa.Beacon;
import org.trd.app.teknichrono.model.jpa.Chronometer;
import org.trd.app.teknichrono.model.jpa.LapTime;
import org.trd.app.teknichrono.model.jpa.Pilot;
import org.trd.app.teknichrono.model.jpa.Ping;
import org.trd.app.teknichrono.model.jpa.Session;

import javax.persistence.EntityManager;
import java.sql.Timestamp;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TestPingManager {

  private long laptimeIndex = 1L;

  private final Pilot pilot = new Pilot();
  private long start = System.currentTimeMillis();

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
  private EntityManager em;

  @InjectMocks
  private PingManager testMe = new PingManager(em);
  private final Beacon beacon = new Beacon();

  @Before
  public void prepare() {
    beacon.setPilot(pilot);
    session.setStart(new Timestamp(start));
    c0.id = 10L;
    c1.id = 11L;
    c2.id = 12L;
    c3.id = 13L;
    session.getChronometers().add(c0);
    session.getChronometers().add(c1);
    session.getChronometers().add(c2);
    session.getChronometers().add(c3);
    when(selector.pickMostRelevant(any(Ping.class))).thenReturn(session);
  }

  private Ping createPing(long timeFromStart, Chronometer c) {
    Ping p = new Ping();
    p.setDateTime(new Timestamp(start + timeFromStart));
    p.setBeacon(beacon);
    p.setChrono(c);
    p.id = c.id + (int) timeFromStart;
    return p;
  }

  private void pilotHasLapWith(long l0, long l1, long l2, long l3) {
    LapTime l = new LapTime();
    l.id = laptimeIndex++;
    l.setPilot(pilot);
    l.setSession(session);
    if (l0 >= 0) {
      l.addIntermediates(createPing(l0, c0));
    }
    if (l1 >= 0) {
      l.addIntermediates(createPing(l1, c1));
    }
    if (l2 >= 0) {
      l.addIntermediates(createPing(l2, c2));
    }
    if (l3 >= 0) {
      l.addIntermediates(createPing(l3, c3));
    }
    pilot.getLaps().add(l);
  }

  @Test
  public void pingMustReferBeacon() {
    Ping p = new Ping();
    testMe.addPing(p);
    verifyZeroInteractions(em);
    verify(logger, atLeastOnce()).error(any());
  }

  @Test
  public void pingMustReferPilot() {
    Ping p = new Ping();
    p.setBeacon(new Beacon());
    testMe.addPing(p);
    verifyZeroInteractions(em);
    verify(logger, atLeastOnce()).error(any());
  }

  @Test
  public void pingMustReferChronometer() {
    Ping p = new Ping();
    Beacon b = new Beacon();
    b.setPilot(new Pilot());
    p.setBeacon(b);
    testMe.addPing(p);
    verifyZeroInteractions(em);
    verify(logger, atLeastOnce()).error(any());
  }

  @Test
  public void failsIfNoSessionMatches() {
    Ping p = createPing(0, c0);
    when(selector.pickMostRelevant(any(Ping.class))).thenReturn(null);
    testMe.addPing(p);
    verifyZeroInteractions(em);
    verify(logger, atLeastOnce()).error(any());
  }

  @Test
  public void canAddFirstPing() {
    Ping p = createPing(0, c0);
    testMe.addPing(p);
    ArgumentCaptor<LapTime> captor = ArgumentCaptor.forClass(LapTime.class);
    verify(em).persist(captor.capture());
    verify(logger, never()).error(any());
    LapTime lap = captor.getValue();
    Assert.assertTrue(lap != null);
    Assert.assertEquals(pilot, lap.getPilot());
    Assert.assertEquals(start, lap.getStartDate().getTime());
    Assert.assertEquals(session, lap.getSession());
    Assert.assertTrue(lap.getIntermediates().contains(p));
    Assert.assertEquals(1, lap.getIntermediates().size());

  }

  @Test
  public void pingMustReferChronometerThatIsPartOfSession() {
    Chronometer c = new Chronometer();
    c.id = 666L;
    Ping p = createPing(0, c);
    testMe.addPing(p);
    verifyZeroInteractions(em);
    verify(logger, atLeastOnce()).error(any());
  }

  @Test
  public void ignorePingIfWithinInactivity() {
    session.setInactivity(10000);
    Ping p = createPing(5000, c0);
    testMe.addPing(p);
    verifyZeroInteractions(em);
  }

  @Test
  public void failsIfPingSentTwice() {
    LapTime l = new LapTime();
    Ping p = createPing(0, c0);
    l.addIntermediates(p);
    pilot.getLaps().add(l);
    testMe.addPing(p);
    verify(em, never()).persist(any());
    verify(logger, atLeastOnce()).error(any());
  }

  @Test
  public void pingsCanBeAddedInRightOrder1() {
    Ping p1 = createPing(10000, c1);
    pilotHasLapWith(0, -1, -1, -1);
    testMe.addPing(p1);
    ArgumentCaptor<LapTime> captor = ArgumentCaptor.forClass(LapTime.class);
    verify(em).persist(captor.capture());
    verify(logger, never()).error(any());
    LapTime lap = captor.getValue();
    Assert.assertTrue(lap != null);
    Assert.assertEquals(pilot, lap.getPilot());
    Assert.assertEquals(start, lap.getStartDate().getTime());
    Assert.assertEquals(session, lap.getSession());
    Assert.assertEquals(2, lap.getIntermediates().size());
    Assert.assertTrue(lap.getIntermediates().contains(p1));
    Assert.assertEquals(1, lap.getIntermediates().indexOf(p1));
  }

  @Test
  public void pingsCanBeAddedInRightOrder2() {
    Ping p2 = createPing(20000, c2);
    pilotHasLapWith(0, 10000, -1, -1);
    testMe.addPing(p2);
    ArgumentCaptor<LapTime> captor = ArgumentCaptor.forClass(LapTime.class);
    verify(em).persist(captor.capture());
    verify(logger, never()).error(any());
    LapTime lap = captor.getValue();
    Assert.assertTrue(lap != null);
    Assert.assertEquals(pilot, lap.getPilot());
    Assert.assertEquals(start, lap.getStartDate().getTime());
    Assert.assertEquals(session, lap.getSession());
    Assert.assertTrue(lap.getIntermediates().contains(p2));
    Assert.assertEquals(3, lap.getIntermediates().size());
    Assert.assertEquals(2, lap.getIntermediates().indexOf(p2));
  }

  @Test
  public void pingsCanBeAddedInRightOrder3() {
    pilotHasLapWith(0, 10000, 20000, -1);
    Ping p3 = createPing(30000, c3);
    testMe.addPing(p3);
    ArgumentCaptor<LapTime> captor = ArgumentCaptor.forClass(LapTime.class);
    verify(em).persist(captor.capture());
    verify(logger, never()).error(any());
    LapTime lap = captor.getValue();
    Assert.assertTrue(lap != null);
    Assert.assertEquals(pilot, lap.getPilot());
    Assert.assertEquals(start, lap.getStartDate().getTime());
    Assert.assertEquals(session, lap.getSession());
    Assert.assertTrue(lap.getIntermediates().contains(p3));
    Assert.assertEquals(4, lap.getIntermediates().size());
    Assert.assertEquals(3, lap.getIntermediates().indexOf(p3));
  }

  @Test
  public void pingsCanBeAddedInReverseOrder1() {
    Ping p3 = createPing(30000, c3);
    testMe.addPing(p3);
    ArgumentCaptor<LapTime> captor = ArgumentCaptor.forClass(LapTime.class);
    verify(em).persist(captor.capture());
    verify(logger, never()).error(any());
    LapTime lap = captor.getValue();
    Assert.assertTrue(lap != null);
    Assert.assertEquals(pilot, lap.getPilot());
    Assert.assertEquals(p3.getDateTime().getTime(), lap.getStartDate().getTime());
    Assert.assertEquals(session, lap.getSession());
    Assert.assertTrue(lap.getIntermediates().contains(p3));
    Assert.assertEquals(1, lap.getIntermediates().size());
    Assert.assertEquals(0, lap.getIntermediates().indexOf(p3));
  }

  @Test
  public void pingsCanBeAddedInReverseOrder2() {
    pilotHasLapWith(-1, -1, -1, 30000);
    Ping p2 = createPing(20000, c2);
    testMe.addPing(p2);
    ArgumentCaptor<LapTime> captor = ArgumentCaptor.forClass(LapTime.class);
    verify(em).persist(captor.capture());
    verify(logger, never()).error(any());
    LapTime lap = captor.getValue();
    Assert.assertTrue(lap != null);
    Assert.assertEquals(pilot, lap.getPilot());
    Assert.assertEquals(p2.getDateTime().getTime(), lap.getStartDate().getTime());
    Assert.assertEquals(session, lap.getSession());
    Assert.assertTrue(lap.getIntermediates().contains(p2));
    Assert.assertEquals(2, lap.getIntermediates().size());
    Assert.assertEquals(0, lap.getIntermediates().indexOf(p2));
  }

  @Test
  public void pingsCanBeAddedInReverseOrder3() {
    pilotHasLapWith(-1, -1, 20000, 30000);
    Ping p1 = createPing(10000, c1);
    testMe.addPing(p1);
    ArgumentCaptor<LapTime> captor = ArgumentCaptor.forClass(LapTime.class);
    verify(em).persist(captor.capture());
    verify(logger, never()).error(any());
    LapTime lap = captor.getValue();
    Assert.assertTrue(lap != null);
    Assert.assertEquals(pilot, lap.getPilot());
    Assert.assertEquals(p1.getDateTime().getTime(), lap.getStartDate().getTime());
    Assert.assertEquals(session, lap.getSession());
    Assert.assertTrue(lap.getIntermediates().contains(p1));
    Assert.assertEquals(3, lap.getIntermediates().size());
    Assert.assertEquals(0, lap.getIntermediates().indexOf(p1));
  }

  @Test
  public void pingsCanBeAddedInReverseOrder4() {
    pilotHasLapWith(-1, 10000, 20000, 30000);
    Ping p0 = createPing(0, c0);
    testMe.addPing(p0);
    ArgumentCaptor<LapTime> captor = ArgumentCaptor.forClass(LapTime.class);
    verify(em).persist(captor.capture());
    verify(logger, never()).error(any());
    LapTime lap = captor.getValue();
    Assert.assertTrue(lap != null);
    Assert.assertEquals(pilot, lap.getPilot());
    Assert.assertEquals(p0.getDateTime().getTime(), lap.getStartDate().getTime());
    Assert.assertEquals(session, lap.getSession());
    Assert.assertTrue(lap.getIntermediates().contains(p0));
    Assert.assertEquals(4, lap.getIntermediates().size());
    Assert.assertEquals(0, lap.getIntermediates().indexOf(p0));
  }

  @Test
  public void pingsCanBeAddedInRandomOrder1() {
    Ping p2 = createPing(20000, c2);
    testMe.addPing(p2);
    ArgumentCaptor<LapTime> captor = ArgumentCaptor.forClass(LapTime.class);
    verify(em).persist(captor.capture());
    verify(logger, never()).error(any());
    LapTime lap = captor.getValue();
    Assert.assertTrue(lap != null);
    Assert.assertEquals(pilot, lap.getPilot());
    Assert.assertEquals(p2.getDateTime().getTime(), lap.getStartDate().getTime());
    Assert.assertEquals(session, lap.getSession());
    Assert.assertTrue(lap.getIntermediates().contains(p2));
    Assert.assertEquals(1, lap.getIntermediates().size());
    Assert.assertEquals(0, lap.getIntermediates().indexOf(p2));
  }

  @Test
  public void pingsCanBeAddedInRandomOrder2() {
    pilotHasLapWith(-1, -1, 20000, -1);
    Ping p0 = createPing(0, c0);
    testMe.addPing(p0);
    ArgumentCaptor<LapTime> captor = ArgumentCaptor.forClass(LapTime.class);
    verify(em).persist(captor.capture());
    verify(logger, never()).error(any());
    LapTime lap = captor.getValue();
    Assert.assertTrue(lap != null);
    Assert.assertEquals(pilot, lap.getPilot());
    Assert.assertEquals(p0.getDateTime().getTime(), lap.getStartDate().getTime());
    Assert.assertEquals(session, lap.getSession());
    Assert.assertTrue(lap.getIntermediates().contains(p0));
    Assert.assertEquals(2, lap.getIntermediates().size());
    Assert.assertEquals(0, lap.getIntermediates().indexOf(p0));
  }

  @Test
  public void pingsCanBeAddedInRandomOrder3() {
    pilotHasLapWith(0, -1, 20000, -1);
    Ping p3 = createPing(30000, c3);
    testMe.addPing(p3);
    ArgumentCaptor<LapTime> captor = ArgumentCaptor.forClass(LapTime.class);
    verify(em).persist(captor.capture());
    verify(logger, never()).error(any());
    LapTime lap = captor.getValue();
    Assert.assertTrue(lap != null);
    Assert.assertEquals(pilot, lap.getPilot());
    Assert.assertEquals(start, lap.getStartDate().getTime());
    Assert.assertEquals(session, lap.getSession());
    Assert.assertTrue(lap.getIntermediates().contains(p3));
    Assert.assertEquals(3, lap.getIntermediates().size());
    Assert.assertEquals(2, lap.getIntermediates().indexOf(p3));
  }

  @Test
  public void pingsCanBeAddedInRandomOrder4() {
    pilotHasLapWith(0, -1, 20000, 30000);
    Ping p1 = createPing(10000, c1);
    testMe.addPing(p1);
    ArgumentCaptor<LapTime> captor = ArgumentCaptor.forClass(LapTime.class);
    verify(em).persist(captor.capture());
    verify(logger, never()).error(any());
    LapTime lap = captor.getValue();
    Assert.assertTrue(lap != null);
    Assert.assertEquals(pilot, lap.getPilot());
    Assert.assertEquals(start, lap.getStartDate().getTime());
    Assert.assertEquals(session, lap.getSession());
    Assert.assertTrue(lap.getIntermediates().contains(p1));
    Assert.assertEquals(4, lap.getIntermediates().size());
    Assert.assertEquals(1, lap.getIntermediates().indexOf(p1));
  }


  @Test
  public void pingsCanStartNewLap() {
    pilotHasLapWith(0, 10000, 20000, 30000);
    Ping p1 = createPing(50000, c1);
    testMe.addPing(p1);
    ArgumentCaptor<LapTime> captor = ArgumentCaptor.forClass(LapTime.class);
    verify(em).persist(captor.capture());
    verify(logger, never()).error(any());
    LapTime lap = captor.getValue();
    Assert.assertTrue(lap != null);
    Assert.assertEquals(pilot, lap.getPilot());
    Assert.assertEquals(p1.getDateTime().getTime(), lap.getStartDate().getTime());
    Assert.assertEquals(session, lap.getSession());
    Assert.assertTrue(lap.getIntermediates().contains(p1));
    Assert.assertEquals(1, lap.getIntermediates().size());
    Assert.assertEquals(0, lap.getIntermediates().indexOf(p1));
  }


  @Test
  public void pingsCanStartNewLapBefore() {
    pilotHasLapWith(-1, 50000, -1, 70000);
    Ping p1 = createPing(10000, c1);
    testMe.addPing(p1);
    ArgumentCaptor<LapTime> captor = ArgumentCaptor.forClass(LapTime.class);
    verify(em).persist(captor.capture());
    verify(logger, never()).error(any());
    LapTime lap = captor.getValue();
    Assert.assertTrue(lap != null);
    Assert.assertEquals(pilot, lap.getPilot());
    Assert.assertEquals(p1.getDateTime().getTime(), lap.getStartDate().getTime());
    Assert.assertEquals(session, lap.getSession());
    Assert.assertTrue(lap.getIntermediates().contains(p1));
    Assert.assertEquals(1, lap.getIntermediates().size());
    Assert.assertEquals(0, lap.getIntermediates().indexOf(p1));
  }


  @Test
  public void pingsCanStartNewLapInBetween() {
    pilotHasLapWith(0, 10000, -1, -1);
    pilotHasLapWith(80000, 90000, -1, 110000);
    Ping p1 = createPing(50000, c1);
    testMe.addPing(p1);
    ArgumentCaptor<LapTime> captor = ArgumentCaptor.forClass(LapTime.class);
    verify(em).persist(captor.capture());
    verify(logger, never()).error(any());
    LapTime lap = captor.getValue();
    Assert.assertTrue(lap != null);
    Assert.assertEquals(pilot, lap.getPilot());
    Assert.assertEquals(p1.getDateTime().getTime(), lap.getStartDate().getTime());
    Assert.assertEquals(session, lap.getSession());
    Assert.assertTrue(lap.getIntermediates().contains(p1));
    Assert.assertEquals(1, lap.getIntermediates().size());
    Assert.assertEquals(0, lap.getIntermediates().indexOf(p1));
  }


  @Test
  public void pingsCanFillLapInBetween() {
    pilotHasLapWith(0, 10000, -1, -1);
    pilotHasLapWith(40000, -1, 60000, 70000);
    pilotHasLapWith(80000, 90000, -1, 110000);
    Ping p1 = createPing(50000, c1);
    testMe.addPing(p1);
    ArgumentCaptor<LapTime> captor = ArgumentCaptor.forClass(LapTime.class);
    verify(em).persist(captor.capture());
    verify(logger, never()).error(any());
    LapTime lap = captor.getValue();
    Assert.assertTrue(lap != null);
    Assert.assertEquals(pilot, lap.getPilot());
    Assert.assertEquals(start + 40000, lap.getStartDate().getTime());
    Assert.assertEquals(session, lap.getSession());
    Assert.assertTrue(lap.getIntermediates().contains(p1));
    Assert.assertEquals(4, lap.getIntermediates().size());
    Assert.assertEquals(1, lap.getIntermediates().indexOf(p1));
  }


  @Test
  public void pingsCanSplitLap() {
    pilotHasLapWith(0, 50000, 60000, 70000);
    Ping p1 = createPing(10000, c1);
    testMe.addPing(p1);
    ArgumentCaptor<LapTime> captor = ArgumentCaptor.forClass(LapTime.class);
    verify(em, times(2)).persist(captor.capture());
    verify(logger, never()).error(any());
    List<LapTime> laps = captor.getAllValues();
    boolean foundLapOne = false;
    boolean foundLapTwo = false;
    for (LapTime lap : laps) {
      Assert.assertTrue(lap != null);
      Assert.assertEquals(pilot, lap.getPilot());
      Assert.assertEquals(session, lap.getSession());
      if (lap.getStartDate().getTime() == start) {
        foundLapOne = true;
        Assert.assertTrue(lap.getIntermediates().contains(p1));
        Assert.assertEquals(2, lap.getIntermediates().size());
        Assert.assertEquals(1, lap.getIntermediates().indexOf(p1));
      } else if (lap.getStartDate().getTime() == (start + 50000)) {
        foundLapTwo = true;
        Assert.assertTrue(!lap.getIntermediates().contains(p1));
        Assert.assertEquals(3, lap.getIntermediates().size());
      }
    }
    Assert.assertTrue(foundLapOne);
    Assert.assertTrue(foundLapTwo);
  }

  @Test
  public void pingCanBeAddedToExistingLap() {
    pilotHasLapWith(-1, -1, -1, 30000);
    pilotHasLapWith(-1, -1, 100000, -1);
    Ping p1 = createPing(50000, c1);
    testMe.addPing(p1);
    ArgumentCaptor<LapTime> captor = ArgumentCaptor.forClass(LapTime.class);
    verify(em).persist(captor.capture());
    LapTime lap = captor.getValue();
    Assert.assertTrue(lap != null);
    Assert.assertEquals(pilot, lap.getPilot());
    Assert.assertEquals(p1.getDateTime().getTime(), lap.getStartDate().getTime());
    Assert.assertEquals(session, lap.getSession());
    Assert.assertTrue(lap.getIntermediates().contains(p1));
    Assert.assertEquals(2, lap.getIntermediates().size());
    Assert.assertEquals(0, lap.getIntermediates().indexOf(p1));
  }


}