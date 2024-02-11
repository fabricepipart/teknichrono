package org.trd.app.teknichrono.model.jpa;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.trd.app.teknichrono.model.dto.BeaconDTO;
import org.trd.app.teknichrono.model.repository.BeaconRepository;
import org.trd.app.teknichrono.model.repository.PilotRepository;
import org.trd.app.teknichrono.model.repository.PingRepository;
import org.trd.app.teknichrono.util.exception.ConflictingIdException;
import org.trd.app.teknichrono.util.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TestBeaconRepository {

  private long id = 1L;

  @Mock
  private PilotRepository.Panache pilotRepository;

  @Mock
  private PingRepository.Panache pingRepository;

  @Mock
  private BeaconRepository.Panache beaconPanacheRepository;

  @InjectMocks
  private BeaconRepository beaconRepository;

  Beacon newBeacon(long number, long pilotId) {
    Beacon beacon = new Beacon();
    beacon.id = this.id++;
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
      p.id = this.id++;
      p.setBeacon(beacon);
      beacon.getPings().add(p);
    }
    return beacon;
  }

  @Test
  void createsBeaconWithCompletePilotIfIdProvided() throws NotFoundException, ConflictingIdException {
    Beacon entity = newBeacon(999, 9);
    BeaconDTO dto = BeaconDTO.fromBeacon(entity);
    dto.setId(0L);
    when(this.pilotRepository.findById(9L)).thenReturn(entity.getPilot());

    this.beaconRepository.create(dto);

    verify(this.pilotRepository).findById(9L);

    ArgumentCaptor<Beacon> captor = ArgumentCaptor.forClass(Beacon.class);
    verify(this.beaconPanacheRepository).persist(captor.capture());
    assertThat(captor.getValue().getNumber()).isEqualTo(entity.getNumber());
  }

  @Test
  void deleteByIdRemovesBeaconFromPilot() throws NotFoundException {
    Beacon entity = newBeacon(999, 9);
    Pilot pilot = entity.getPilot();

    when(this.beaconPanacheRepository.findById(entity.id)).thenReturn(entity);

    this.beaconRepository.deleteById(entity.id);

    verify(this.beaconPanacheRepository).delete(entity);
    verify(this.pilotRepository).persist(pilot);
    assertThat(pilot.getCurrentBeacon()).isNull();
  }

  @Test
  void deleteByIdRemovesBeaconFromPings() throws NotFoundException {
    Beacon entity = newBeacon(999, 9);
    List<Ping> pings = entity.getPings();

    when(this.beaconPanacheRepository.findById(anyLong())).thenReturn(entity);

    this.beaconRepository.deleteById(entity.id);

    verify(this.beaconPanacheRepository).delete(entity);

    for (Ping ping : pings) {
      verify(this.pingRepository).persist(ping);
      assertThat(ping.getBeacon()).isNull();
    }
  }

  @Test
  void deleteByIdReturnsErrorIfBeaconDoesNotExist() {
    assertThrows(NotFoundException.class, () -> this.beaconRepository.deleteById(42L));
    verify(this.beaconPanacheRepository, never()).delete(any());
  }


  @Test
  @SuppressWarnings("unchecked")
  void listAll() {
    List<Beacon> entities = new ArrayList<>();
    Beacon entity1 = newBeacon(999, 9);
    Beacon entity2 = newBeacon(-1, 10);
    Beacon entity3 = newBeacon(46, -1);
    entities.add(entity1);
    entities.add(entity2);
    entities.add(entity3);

    PanacheQuery<Beacon> query = mock(PanacheQuery.class);
    when(this.beaconPanacheRepository.findAll()).thenReturn(query);
    when(query.page(any())).thenReturn(query);
    when(query.stream()).thenReturn(entities.stream());

    List<Beacon> beacons = this.beaconRepository.findAll(null, null).toList();
    assertThat(beacons).isNotNull();
    assertThat(beacons).hasSize(3);

    assertThat(beacons.stream().filter(b -> (b.getNumber() == 999 && b.getPilot() != null && b.getPilot().getId() == 9)).count()).isEqualTo(1);
    assertThat(beacons.stream().filter(b -> (b.getPilot() != null && b.getPilot().getId() == 10)).count()).isEqualTo(1);
    assertThat(beacons.stream().filter(b -> (b.getNumber() == 46 && b.getPilot() == null)).count()).isEqualTo(1);

  }

  @Test
  @SuppressWarnings("unchecked")
  void listAllCanUseWindows() {
    List<Beacon> entities = new ArrayList<>();
    Beacon entity1 = newBeacon(999, 9);
    entities.add(entity1);

    PanacheQuery<Beacon> query = mock(PanacheQuery.class);
    when(this.beaconPanacheRepository.findAll()).thenReturn(query);
    when(query.page(any())).thenReturn(query);
    when(query.stream()).thenReturn(entities.stream());

    List<Beacon> beacons = this.beaconRepository.findAll(1, 1).toList();
    assertThat(beacons).isNotNull();
    assertThat(beacons).hasSize(1);
  }
}
