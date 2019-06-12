package org.trd.app.teknichrono.it;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.trd.app.teknichrono.model.dto.BeaconDTO;
import org.trd.app.teknichrono.model.dto.NestedPilotDTO;
import org.trd.app.teknichrono.model.dto.PilotDTO;
import org.trd.app.teknichrono.model.jpa.Beacon;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.*;

@Tag("integration")
@QuarkusTest
public class TestRestBeaconEndpoint {

  private static Jsonb jsonb = JsonbBuilder.create();

  /**
   * ******************** Tests *********************
   **/

  @Test
  public void testLists() {
    List<BeaconDTO> beacons = getAllBeacons();
    assertThat(beacons.size()).isEqualTo(0);
    create(123);
    beacons = getAllBeacons();
    assertThat(beacons.size()).isEqualTo(1);
    create(234);
    beacons = getAllBeacons();
    assertThat(beacons.size()).isEqualTo(2);

    List<BeaconDTO> someBeacons = getAllBeaconsWindow(1, 1);
    assertThat(someBeacons.size()).isEqualTo(1);

    beacons = getAllBeacons();
    for (BeaconDTO beacon : beacons) {
      delete(beacon.getId());
    }
    beacons = getAllBeacons();
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
    List<BeaconDTO> beacons = getAllBeacons();
    assertThat(beacons.size()).isEqualTo(1);
    getByNumberFails(120);
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

    TestRestPilotEndPoint.create("Pilot", "OfBeacon");
    PilotDTO pilot = TestRestPilotEndPoint.getByName("Pilot", "OfBeacon");
    long pilotId = pilot.getId();
    NestedPilotDTO pilotDto = new NestedPilotDTO();
    pilotDto.setId(pilot.getId());
    modifiedBeacon.setPilot(pilotDto);

    update(id, modifiedBeacon);

    List<BeaconDTO> beacons = getAllBeacons();
    assertThat(beacons.size()).isEqualTo(1);
    getByNumberFails(120);
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
    List<BeaconDTO> beacons = getAllBeacons();
    assertThat(beacons.size()).isEqualTo(1);
    getByNumberFails(120);
    BeaconDTO newReturnedBeacon = getByNumber(140);
    assertThat(newReturnedBeacon.getId()).isEqualTo(id);
    assertThat(newReturnedBeacon.getPilot()).isNull();

    deleteWithPilot(id, pilotId);
  }

  /**
   * ******************** Reusable *********************
   **/

  public static void update(long id, BeaconDTO modifiedBeacon) {
    given().pathParam("id", id)
        .when().contentType(ContentType.JSON).body(jsonb.toJson(modifiedBeacon)).put("/rest/beacons/{id}")
        .then()
        .statusCode(204);
  }


  public static List<BeaconDTO> getAllBeacons() {
    Response r = given()
        .when().get("/rest/beacons")
        .then()
        .statusCode(200)
        .extract().response();
    return jsonb.fromJson(r.asString(), new ArrayList<BeaconDTO>() {
    }.getClass().getGenericSuperclass());
  }

  public static List<BeaconDTO> getAllBeaconsWindow(int page, int pageSize) {
    Response r = given().queryParam("page", page).queryParam("pageSize", pageSize)
        .when().get("/rest/beacons")
        .then()
        .statusCode(200)
        .extract().response();
    return jsonb.fromJson(r.asString(), new ArrayList<BeaconDTO>() {
    }.getClass().getGenericSuperclass());
  }

  public static void delete(long id) {
    given().pathParam("id", id)
        .when().delete("/rest/beacons/{id}")
        .then()
        .statusCode(204);
  }

  public static void deleteWithPilot(long id, Long aLong) {

    TestRestPilotEndPoint.delete(aLong);
    given().pathParam("id", id)
        .when().delete("/rest/beacons/{id}")
        .then()
        .statusCode(204);
  }

  public static BeaconDTO getById(long id) {
    Response r = given().pathParam("id", id)
        .when().get("/rest/beacons/{id}")
        .then()
        .statusCode(200)
        .extract().response();
    return jsonb.fromJson(r.asString(), BeaconDTO.class);
  }

  public static void getByNumberFails(int beaconNumber) {
    given().pathParam("number", beaconNumber)
        .when().get("/rest/beacons/number/{number}")
        .then()
        .statusCode(404);
  }

  public static BeaconDTO getByNumber(int beaconNumber) {
    Response r = given().pathParam("number", beaconNumber)
        .when().get("/rest/beacons/number/{number}")
        .then()
        .statusCode(200)
        .extract().response();
    return jsonb.fromJson(r.asString(), BeaconDTO.class);
  }

  public static void create(int beaconNumber) {
    Beacon b = new Beacon();
    b.setNumber(beaconNumber);
    given()
        .when().contentType(ContentType.JSON).body(jsonb.toJson(b)).post("/rest/beacons")
        .then()
        .statusCode(204);
  }

  public static void createWithPilot(int beaconNumber, String firstName, String lastName) {
    TestRestPilotEndPoint.create(firstName, lastName);
    PilotDTO pilot = TestRestPilotEndPoint.getByName(firstName, lastName);

    BeaconDTO b = new BeaconDTO();
    b.setNumber(beaconNumber);

    NestedPilotDTO p = new NestedPilotDTO();
    p.setId(pilot.getId());
    p.setFirstName(pilot.getFirstName());
    p.setLastName(pilot.getLastName());
    b.setPilot(p);

    given()
        .when().contentType(ContentType.JSON).body(jsonb.toJson(b)).post("/rest/beacons")
        .then()
        .statusCode(204);
  }

}
