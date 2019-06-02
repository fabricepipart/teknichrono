package org.trd.app.teknichrono.service;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.trd.app.teknichrono.model.dto.BeaconDTO;
import org.trd.app.teknichrono.model.jpa.Beacon;
import org.trd.app.teknichrono.model.jpa.BeaconRepository;
import org.trd.app.teknichrono.model.jpa.Pilot;
import org.trd.app.teknichrono.model.jpa.PilotRepository;
import org.trd.app.teknichrono.model.jpa.Ping;
import org.trd.app.teknichrono.model.jpa.PingRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TestBeaconService {


  private long id = 1L;

  @Mock
  private PilotRepository pilotRepository;

  @Mock
  private PingRepository pingRepository;

  @Mock
  private BeaconRepository beaconRepository;

  @InjectMocks
  private BeaconService service;


  public Beacon newBeacon(long number, long pilotId) {
    Beacon beacon = new Beacon();
    beacon.id = id++;
    beacon.setNumber(number);
    if (pilotId >= 0) {
      Pilot p = new Pilot();
      p.id = pilotId;
      beacon.setPilot(p);
      p.setFirstName("First");
      p.setLastName("Last");
    }
    for (int i = 0; i < 10; i++) {
      Ping p = new Ping();
      p.id = id++;
      p.setBeacon(beacon);
      beacon.getPings().add(p);
    }
    return beacon;
  }

  @Test
  public void createsBeaconWithCompletePilotIfIdProvided() {
    Beacon entity = newBeacon(999, 9);
    service.create(entity);
    verify(pilotRepository).findById(9L);

    ArgumentCaptor<Beacon> captor = ArgumentCaptor.forClass(Beacon.class);
    verify(beaconRepository).persist(captor.capture());

    assertThat(captor.getValue().getId()).isEqualTo(entity.getId());
    assertThat(captor.getValue().getNumber()).isEqualTo(entity.getNumber());
  }

  @Test
  public void deleteByIdRemovesBeaconFromPilot() {
    Beacon entity = newBeacon(999, 9);
    when(beaconRepository.findById(anyLong())).thenReturn(entity);
    service.deleteById(entity.id);
    ArgumentCaptor<Beacon> bCaptor = ArgumentCaptor.forClass(Beacon.class);
    verify(beaconRepository).delete(bCaptor.capture());
    assertThat(bCaptor.getValue().getId()).isEqualTo(entity.id);

    ArgumentCaptor<Pilot> captor = ArgumentCaptor.forClass(Pilot.class);
    verify(pilotRepository, atLeastOnce()).persist(captor.capture());
    List<Pilot> pilots = captor.getAllValues();
    for (Pilot p : pilots) {
      assertThat(p.getCurrentBeacon()).isNull();
    }
  }

  @Test
  public void deleteByIdRemovesBeaconFromPings() {
    Beacon entity = newBeacon(999, 9);
    when(beaconRepository.findById(anyLong())).thenReturn(entity);
    service.deleteById(entity.getId());
    ArgumentCaptor<Ping> captor = ArgumentCaptor.forClass(Ping.class);
    verify(pingRepository, atLeastOnce()).persist(captor.capture());
    List<Ping> pings = captor.getAllValues();
    assertThat(pings.size()).isEqualTo(entity.getPings().size());
    for (Ping p : pings) {
      assertThat(p.getBeacon()).isNull();
    }
  }

  @Test
  public void deleteByIdReturnsErrorIfBeaconDoesNotExist() {
    Beacon entity = newBeacon(999, 9);
    org.junit.jupiter.api.Assertions.assertThrows(NoSuchElementException.class, () -> service.deleteById(entity.id));
    verify(beaconRepository, never()).delete(any());
  }


  @Test
  public void listAll() {
    List<Beacon> entities = new ArrayList<>();
    Beacon entity1 = newBeacon(999, 9);
    Beacon entity2 = newBeacon(-1, 10);
    Beacon entity3 = newBeacon(46, -1);
    entities.add(entity1);
    entities.add(entity2);
    entities.add(entity3);

    PanacheQuery<Beacon> query = mock(PanacheQuery.class);
    when(beaconRepository.findAll()).thenReturn(query);
    when(query.page(any())).thenReturn(query);
    when(query.stream()).thenReturn(entities.stream());

    List<BeaconDTO> beacons = service.findAll(null, null);
    assertThat(beacons).isNotNull();
    assertThat(beacons).hasSize(3);

    assertThat(beacons.stream().filter(b -> (b.getNumber() == 999 && b.getPilot() != null && b.getPilot().getId() == 9)).count()).isEqualTo(1);
    assertThat(beacons.stream().filter(b -> (b.getPilot() != null && b.getPilot().getId() == 10)).count()).isEqualTo(1);
    assertThat(beacons.stream().filter(b -> (b.getNumber() == 46 && b.getPilot() == null)).count()).isEqualTo(1);

  }

  @Test
  public void listAllCanUseWindows() {
    List<Beacon> entities = new ArrayList<>();
    Beacon entity1 = newBeacon(999, 9);
    entities.add(entity1);

    PanacheQuery<Beacon> query = mock(PanacheQuery.class);
    when(beaconRepository.findAll()).thenReturn(query);
    when(query.page(any())).thenReturn(query);
    when(query.stream()).thenReturn(entities.stream());

    List<BeaconDTO> beacons = service.findAll(1, 1);
    assertThat(beacons).isNotNull();
    assertThat(beacons).hasSize(1);
  }
}
