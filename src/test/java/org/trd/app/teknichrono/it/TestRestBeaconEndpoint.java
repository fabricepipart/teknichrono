package org.trd.app.teknichrono.it;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.trd.app.teknichrono.model.dto.BeaconDTO;
import org.trd.app.teknichrono.model.dto.NestedPilotDTO;
import org.trd.app.teknichrono.model.dto.PilotDTO;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Tag("integration")
@QuarkusTest
public class TestRestBeaconEndpoint extends TestRestEndpoint<BeaconDTO> {

  private TestRestPilotEndPoint restPilot;

  public TestRestBeaconEndpoint() {
    super("beacons", BeaconDTO.class);
  }

  @BeforeEach
  public void prepare() {
    restPilot = new TestRestPilotEndPoint();
  }

  /**
   * ******************** Tests *********************
   **/

  @Test
  public void testLists() {
    List<BeaconDTO> beacons = getAll();
    assertThat(beacons.size()).isEqualTo(0);
    create(123);
    beacons = getAll();
    assertThat(beacons.size()).isEqualTo(1);
    create(234);
    beacons = getAll();
    assertThat(beacons.size()).isEqualTo(2);

    List<BeaconDTO> someBeacons = getAllInWindow(1, 1);
    assertThat(someBeacons.size()).isEqualTo(1);

    beacons = getAll();
    for (BeaconDTO beacon : beacons) {
      delete(beacon.getId());
    }
    beacons = getAll();
    assertThat(beacons.size()).isEqualTo(0);
  }


  @Test
  public void testCreateModifyDelete() {
    int beaconNumber = 120;
    create(beaconNumber);
    BeaconDTO b = getByNumber(beaconNumber);
    long id = b.getId();
    getById(id);

    BeaconDTO modifiedBeacon = new BeaconDTO();
    modifiedBeacon.setNumber(140);
    modifiedBeacon.setId(id);
    update(id, modifiedBeacon);
    List<BeaconDTO> beacons = getAll();
    assertThat(beacons.size()).isEqualTo(1);
    getByNumber(120, NOT_FOUND);
    BeaconDTO newReturnedBeacon = getByNumber(140);
    assertThat(newReturnedBeacon.getId()).isEqualTo(id);

    delete(id);
  }


  @Test
  public void testCreateWithPilot() {
    createWithPilot(120, "Pilot", "OfBeacon");
    BeaconDTO b = getByNumber(120);
    assertThat(b.getPilot()).isNotNull();
    assertThat(b.getPilot().getFirstName()).isNotNull();
    assertThat(b.getPilot().getLastName()).isNotNull();
    assertThat(b.getPilot().getId()).isNotNull();
    assertThat(b.getPilot().getBeaconNumber()).isEqualTo(120);
    long id = b.getId();
    getById(id);
    deleteWithPilot(id, b.getPilot().getId());
  }

  @Test
  public void testAddPilot() {
    create(120);
    BeaconDTO b = getByNumber(120);
    assertThat(b.getPilot()).isNull();
    long id = b.getId();

    BeaconDTO modifiedBeacon = new BeaconDTO();
    modifiedBeacon.setNumber(160);
    modifiedBeacon.setId(id);

    restPilot.create("Pilot", "OfBeacon");
    PilotDTO pilot = restPilot.getByName("Pilot", "OfBeacon");
    long pilotId = pilot.getId();
    NestedPilotDTO pilotDto = new NestedPilotDTO();
    pilotDto.setId(pilot.getId());
    modifiedBeacon.setPilot(pilotDto);

    update(id, modifiedBeacon);

    List<BeaconDTO> beacons = getAll();
    assertThat(beacons.size()).isEqualTo(1);
    getByNumber(120, NOT_FOUND);
    BeaconDTO newReturnedBeacon = getByNumber(160);
    assertThat(newReturnedBeacon.getId()).isEqualTo(id);
    assertThat(newReturnedBeacon.getPilot()).isNotNull();
    assertThat(newReturnedBeacon.getPilot().getId()).isEqualTo(pilotId);
    assertThat(newReturnedBeacon.getPilot().getFirstName()).isEqualTo("Pilot");
    assertThat(newReturnedBeacon.getPilot().getLastName()).isEqualTo("OfBeacon");
    assertThat(newReturnedBeacon.getPilot().getBeaconNumber()).isEqualTo(160);
    deleteWithPilot(id, pilotId);
  }

  @Test
  public void testRemovePilot() {
    createWithPilot(120, "Pilot", "OfBeacon");
    BeaconDTO b = getByNumber(120);
    long id = b.getId();
    long pilotId = b.getPilot().getId();

    BeaconDTO modifiedBeacon = new BeaconDTO();
    modifiedBeacon.setNumber(140);
    modifiedBeacon.setId(id);
    update(id, modifiedBeacon);
    List<BeaconDTO> beacons = getAll();
    assertThat(beacons.size()).isEqualTo(1);
    getByNumber(120, NOT_FOUND);
    BeaconDTO newReturnedBeacon = getByNumber(140);
    assertThat(newReturnedBeacon.getId()).isEqualTo(id);
    assertThat(newReturnedBeacon.getPilot()).isNull();

    deleteWithPilot(id, pilotId);
  }

  /**
   * ******************** Utilities *********************
   **/

  public BeaconDTO getByNumber(int i) {
    return getBy("number", "number", i);
  }

  private BeaconDTO getByNumber(int i, int expectedStatus) {
    return getBy("number", "number", i, expectedStatus);
  }


  public void create(int beaconNumber) {
    BeaconDTO b = new BeaconDTO();
    b.setNumber(beaconNumber);
    create(b);
  }

  public void createWithPilot(int beaconNumber, String firstName, String lastName) {
    restPilot.create(firstName, lastName);
    PilotDTO pilot = restPilot.getByName(firstName, lastName);

    BeaconDTO b = new BeaconDTO();
    b.setNumber(beaconNumber);

    NestedPilotDTO p = new NestedPilotDTO();
    p.setId(pilot.getId());
    p.setFirstName(pilot.getFirstName());
    p.setLastName(pilot.getLastName());
    b.setPilot(p);

    create(b);
  }

  public void deleteWithPilot(long id, Long aLong) {
    restPilot.delete(aLong);
    delete(id);
  }

}
