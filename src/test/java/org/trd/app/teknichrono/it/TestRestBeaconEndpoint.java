package org.trd.app.teknichrono.it;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.trd.app.teknichrono.model.dto.BeaconDTO;
import org.trd.app.teknichrono.model.jpa.Beacon;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;

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
    Assertions.assertThat(beacons.size()).isEqualTo(0);
    create(123);
    beacons = getAllBeacons();
    Assertions.assertThat(beacons.size()).isEqualTo(1);
    create(234);
    beacons = getAllBeacons();
    Assertions.assertThat(beacons.size()).isEqualTo(2);

    List<BeaconDTO> someBeacons = getAllBeaconsWindow(1, 1);
    Assertions.assertThat(someBeacons.size()).isEqualTo(1);

    beacons = getAllBeacons();
    for (BeaconDTO beacon : beacons) {
      delete(beacon.getId());
    }
    beacons = getAllBeacons();
    Assertions.assertThat(beacons.size()).isEqualTo(0);
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
    Assertions.assertThat(beacons.size()).isEqualTo(1);
    getByNumberFails(120);
    BeaconDTO newReturnedBeacon = getByNumber(140);
    Assertions.assertThat(newReturnedBeacon.getId()).isEqualTo(id);

    delete(id);
  }

  /**
   * ******************** Reusable *********************
   **/

  private void update(long id, BeaconDTO modifiedBeacon) {
    given().pathParam("id", id)
        .when().contentType(ContentType.JSON).body(jsonb.toJson(modifiedBeacon)).put("/rest/beacons/{id}")
        .then()
        .statusCode(204);
  }


  public List<BeaconDTO> getAllBeacons() {
    Response r = given()
        .when().get("/rest/beacons")
        .then()
        .statusCode(200)
        .extract().response();
    return jsonb.fromJson(r.asString(), new ArrayList<BeaconDTO>() {
    }.getClass().getGenericSuperclass());
  }

  public List<BeaconDTO> getAllBeaconsWindow(int start, int max) {
    Response r = given().queryParam("start", Integer.valueOf(start)).queryParam("max", Integer.valueOf(max))
        .when().get("/rest/beacons")
        .then()
        .statusCode(200)
        .extract().response();
    return jsonb.fromJson(r.asString(), new ArrayList<BeaconDTO>() {
    }.getClass().getGenericSuperclass());
  }

  public void delete(long id) {
    given().pathParam("id", id)
        .when().delete("/rest/beacons/{id}")
        .then()
        .statusCode(204);
  }

  public BeaconDTO getById(long id) {
    Response r = given().pathParam("id", id)
        .when().get("/rest/beacons/{id}")
        .then()
        .statusCode(200)
        .extract().response();
    return jsonb.fromJson(r.asString(), BeaconDTO.class);
  }

  public void getByNumberFails(int beaconNumber) {
    given().pathParam("number", beaconNumber)
        .when().get("/rest/beacons/number/{number}")
        .then()
        .statusCode(404);
  }

  public BeaconDTO getByNumber(int beaconNumber) {
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
        .statusCode(201);
  }

}
