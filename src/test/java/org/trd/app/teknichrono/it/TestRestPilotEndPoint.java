package org.trd.app.teknichrono.it;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.trd.app.teknichrono.model.dto.BeaconDTO;
import org.trd.app.teknichrono.model.dto.CategoryDTO;
import org.trd.app.teknichrono.model.dto.NestedBeaconDTO;
import org.trd.app.teknichrono.model.dto.NestedCategoryDTO;
import org.trd.app.teknichrono.model.dto.PilotDTO;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.*;

@Tag("integration")
@QuarkusTest
public class TestRestPilotEndPoint {


  private static Jsonb jsonb = JsonbBuilder.create();

  /**
   * ******************** Tests *********************
   **/

  @Test
  public void testLists() {
    List<PilotDTO> pilots = getAllPilots();
    assertThat(pilots.size()).isEqualTo(0);
    create("Pilot", "One");
    pilots = getAllPilots();
    assertThat(pilots.size()).isEqualTo(1);
    create("Pilot", "Two");
    pilots = getAllPilots();
    assertThat(pilots.size()).isEqualTo(2);

    List<PilotDTO> somePilots = getAllPilotsWindow(1, 1);
    assertThat(somePilots.size()).isEqualTo(1);

    pilots = getAllPilots();
    for (PilotDTO pilot : pilots) {
      delete(pilot.getId());
    }
    pilots = getAllPilots();
    assertThat(pilots.size()).isEqualTo(0);
  }


  @Test
  public void testCreateModifyDelete() {
    create("Pilot", "A");
    PilotDTO b = getByName("Pilot", "A");
    long id = b.getId();
    getById(id);

    PilotDTO modifiedPilot = new PilotDTO();
    modifiedPilot.setFirstName("Pilot");
    modifiedPilot.setLastName("B");
    modifiedPilot.setId(id);
    update(id, modifiedPilot);
    List<PilotDTO> pilots = getAllPilots();
    assertThat(pilots.size()).isEqualTo(1);
    getByNameFails("Pilot", "A");
    PilotDTO newReturnedPilot = getByName("Pilot", "B");
    assertThat(newReturnedPilot.getId()).isEqualTo(id);

    delete(id);
    assertTestCleanedEverything();
  }

  @Test
  public void testCreateWithBeacon() {
    createWithBeacon("Pilot", "Name", 210);
    PilotDTO pilot = getByName("Pilot", "Name");
    assertThat(pilot.getCurrentBeacon()).isNotNull();
    assertThat(pilot.getCurrentBeacon().getNumber()).isEqualTo(210);

    long id = pilot.getId();
    pilot = getById(id);
    assertThat(pilot.getCurrentBeacon()).isNotNull();
    assertThat(pilot.getCurrentBeacon().getNumber()).isEqualTo(210);
    long beaconId = pilot.getCurrentBeacon().getId();

    deleteWithBeacon(id, beaconId);
    assertTestCleanedEverything();
  }

  @Test
  public void testCreateWithCategory() {
    createWithCategory("Pilot", "OfCategory", "Testers");
    PilotDTO pilot = getByName("Pilot", "OfCategory");
    assertThat(pilot.getCategory()).isNotNull();
    assertThat(pilot.getCategory().getName()).isEqualTo("Testers");

    long id = pilot.getId();
    pilot = getById(id);
    assertThat(pilot.getCategory()).isNotNull();
    assertThat(pilot.getCategory().getName()).isEqualTo("Testers");
    long categoryId = pilot.getCategory().getId();

    deleteWithCategory(id, categoryId);
    assertTestCleanedEverything();
  }

  @Test
  public void testSetBeaconViaUpdate() {
    create("Pilot", "NoBeacon");
    PilotDTO pilot = getByName("Pilot", "NoBeacon");
    assertThat(pilot.getCurrentBeacon()).isNull();

    long id = pilot.getId();

    PilotDTO modifiedPilot = new PilotDTO();
    modifiedPilot.setFirstName("Pilot");
    modifiedPilot.setLastName("Expert");
    modifiedPilot.setId(id);

    TestRestBeaconEndpoint.create(210);
    BeaconDTO beacon = TestRestBeaconEndpoint.getByNumber(210);
    long beaconId = beacon.getId();
    NestedBeaconDTO nestedBeacon = new NestedBeaconDTO();
    nestedBeacon.setId(beaconId);
    modifiedPilot.setCurrentBeacon(nestedBeacon);

    update(id, modifiedPilot);
    // Update twice has no impact
    update(id, modifiedPilot);

    getByNameFails("Pilot", "NoBeacon");
    PilotDTO newReturnedPilot = getByName("Pilot", "Expert");
    assertThat(newReturnedPilot.getId()).isEqualTo(id);
    assertThat(newReturnedPilot.getFirstName()).isEqualTo("Pilot");
    assertThat(newReturnedPilot.getLastName()).isEqualTo("Expert");
    assertThat(newReturnedPilot.getCurrentBeacon()).isNotNull();
    assertThat(newReturnedPilot.getCurrentBeacon().getId()).isEqualTo(beaconId);
    assertThat(newReturnedPilot.getCurrentBeacon().getNumber()).isEqualTo(210);

    deleteWithBeacon(id, beaconId);
    assertTestCleanedEverything();
  }

  @Test
  public void testSetCategoryViaUpdate() {
    create("Pilot", "NoCategory");
    PilotDTO pilot = getByName("Pilot", "NoCategory");
    assertThat(pilot.getCategory()).isNull();

    long id = pilot.getId();

    PilotDTO modifiedPilot = new PilotDTO();
    modifiedPilot.setFirstName("Pilot");
    modifiedPilot.setLastName("Expert");
    modifiedPilot.setId(id);

    TestRestCategoryEndpoint.create("Expert");
    CategoryDTO category = TestRestCategoryEndpoint.getByName("Expert");
    long categoryId = category.getId();
    NestedCategoryDTO nestedCategory = new NestedCategoryDTO();
    nestedCategory.setId(categoryId);
    modifiedPilot.setCategory(nestedCategory);

    update(id, modifiedPilot);
    // Update twice has no impact
    update(id, modifiedPilot);

    getByNameFails("Pilot", "NoCategory");
    PilotDTO newReturnedPilot = getByName("Pilot", "Expert");
    assertThat(newReturnedPilot.getId()).isEqualTo(id);
    assertThat(newReturnedPilot.getFirstName()).isEqualTo("Pilot");
    assertThat(newReturnedPilot.getLastName()).isEqualTo("Expert");
    assertThat(newReturnedPilot.getCategory()).isNotNull();
    assertThat(newReturnedPilot.getCategory().getId()).isEqualTo(categoryId);
    assertThat(newReturnedPilot.getCategory().getName()).isEqualTo("Expert");

    deleteWithCategory(id, categoryId);
    assertTestCleanedEverything();
  }

  @Test
  public void testSetBeaconViaSpecificMethod() {
    create("Pilot", "NoBeacon");
    PilotDTO pilot = getByName("Pilot", "NoBeacon");
    assertThat(pilot.getCurrentBeacon()).isNull();

    long id = pilot.getId();
    TestRestBeaconEndpoint.create(220);
    BeaconDTO beacon = TestRestBeaconEndpoint.getByNumber(220);
    long beaconId = beacon.getId();

    associateBeacon(id, beaconId);

    PilotDTO newReturnedPilot = getByName("Pilot", "NoBeacon");
    assertThat(newReturnedPilot.getId()).isEqualTo(id);
    assertThat(newReturnedPilot.getFirstName()).isEqualTo("Pilot");
    assertThat(newReturnedPilot.getLastName()).isEqualTo("NoBeacon");
    assertThat(newReturnedPilot.getCurrentBeacon()).isNotNull();
    assertThat(newReturnedPilot.getCurrentBeacon().getId()).isEqualTo(beaconId);
    assertThat(newReturnedPilot.getCurrentBeacon().getNumber()).isEqualTo(220);

    deleteWithBeacon(id, beaconId);
    assertTestCleanedEverything();
  }

  @Test
  public void testRemoveBeaconViaUpdate() {
    createWithBeacon("Pilot", "Name", 210);
    PilotDTO pilot = getByName("Pilot", "Name");
    assertThat(pilot.getCurrentBeacon()).isNotNull();
    assertThat(pilot.getCurrentBeacon().getNumber()).isEqualTo(210);

    long id = pilot.getId();
    long beaconId = pilot.getCurrentBeacon().getId();

    PilotDTO modifiedPilot = new PilotDTO();
    modifiedPilot.setFirstName("Pilot");
    modifiedPilot.setLastName("Name");
    modifiedPilot.setId(id);

    update(id, modifiedPilot);

    PilotDTO newReturnedPilot = getByName("Pilot", "Name");
    assertThat(newReturnedPilot.getId()).isEqualTo(id);
    assertThat(newReturnedPilot.getFirstName()).isEqualTo("Pilot");
    assertThat(newReturnedPilot.getLastName()).isEqualTo("Name");
    assertThat(newReturnedPilot.getCurrentBeacon()).isNull();

    deleteWithBeacon(id, beaconId);
    assertTestCleanedEverything();
  }

  @Test
  public void testRemoveCategoryViaUpdate() {
    createWithCategory("Pilot", "OfCategory", "Testers");
    PilotDTO pilot = getByName("Pilot", "OfCategory");
    assertThat(pilot.getCategory()).isNotNull();
    assertThat(pilot.getCategory().getName()).isEqualTo("Testers");

    long id = pilot.getId();
    long categoryId = pilot.getCategory().getId();

    PilotDTO modifiedPilot = new PilotDTO();
    modifiedPilot.setFirstName("Pilot");
    modifiedPilot.setLastName("OfCategory");
    modifiedPilot.setId(id);

    update(id, modifiedPilot);

    PilotDTO newReturnedPilot = getByName("Pilot", "OfCategory");
    assertThat(newReturnedPilot.getId()).isEqualTo(id);
    assertThat(newReturnedPilot.getFirstName()).isEqualTo("Pilot");
    assertThat(newReturnedPilot.getLastName()).isEqualTo("OfCategory");
    assertThat(newReturnedPilot.getCategory()).isNull();

    deleteWithCategory(id, categoryId);
    assertTestCleanedEverything();
  }

  /**
   * ******************** Reusable *********************
   **/

  public static void update(long id, PilotDTO modifiedPilot) {
    given().pathParam("id", id)
        .when().contentType(ContentType.JSON).body(jsonb.toJson(modifiedPilot)).put("/rest/pilots/{id}")
        .then()
        .statusCode(204);
  }

  public static void associateBeacon(long id, long beaconId) {
    given().pathParam("id", id).queryParam("beaconId", beaconId)
        .when().contentType(ContentType.JSON).post("/rest/pilots/{id}/setBeacon")
        .then()
        .statusCode(200);
  }


  public static List<PilotDTO> getAllPilots() {
    Response r = given()
        .when().get("/rest/pilots")
        .then()
        .statusCode(200)
        .extract().response();
    return jsonb.fromJson(r.asString(), new ArrayList<PilotDTO>() {
    }.getClass().getGenericSuperclass());
  }

  public static List<PilotDTO> getAllPilotsWindow(int start, int max) {
    Response r = given().queryParam("start", Integer.valueOf(start)).queryParam("max", Integer.valueOf(max))
        .when().get("/rest/pilots")
        .then()
        .statusCode(200)
        .extract().response();
    return jsonb.fromJson(r.asString(), new ArrayList<PilotDTO>() {
    }.getClass().getGenericSuperclass());
  }

  public static void delete(long id) {
    given().pathParam("id", id)
        .when().delete("/rest/pilots/{id}")
        .then()
        .statusCode(204);
  }

  public static void deleteWithBeacon(long id, long beaconId) {
    TestRestBeaconEndpoint.delete(beaconId);
    delete(id);
  }

  public static void deleteWithCategory(long id, long catId) {
    TestRestCategoryEndpoint.delete(catId);
    delete(id);
  }

  public static PilotDTO getById(long id) {
    Response r = given().pathParam("id", id)
        .when().get("/rest/pilots/{id}")
        .then()
        .statusCode(200)
        .extract().response();
    return jsonb.fromJson(r.asString(), PilotDTO.class);
  }

  public static void getByNameFails(String first, String last) {
    given().queryParam("firstname", first).queryParam("lastname", last)
        .when().get("/rest/pilots/name")
        .then()
        .statusCode(404);
  }

  public static PilotDTO getByName(String first, String last) {
    Response r = given().queryParam("firstname", first).queryParam("lastname", last)
        .when().get("/rest/pilots/name")
        .then()
        .statusCode(200)
        .extract().response();
    return jsonb.fromJson(r.asString(), PilotDTO.class);
  }

  public static void create(String first, String last) {
    PilotDTO p = new PilotDTO();
    p.setFirstName(first);
    p.setLastName(last);
    given()
        .when().contentType(ContentType.JSON).body(jsonb.toJson(p)).post("/rest/pilots")
        .then()
        .statusCode(201);
  }

  public static void createWithBeacon(String first, String last, int beaconNumber) {
    TestRestBeaconEndpoint.create(beaconNumber);
    BeaconDTO beacon = TestRestBeaconEndpoint.getByNumber(beaconNumber);
    NestedBeaconDTO nestedBeacon = new NestedBeaconDTO();
    nestedBeacon.setNumber(beaconNumber);
    nestedBeacon.setId(beacon.getId());

    PilotDTO p = new PilotDTO();
    p.setFirstName(first);
    p.setLastName(last);
    p.setCurrentBeacon(nestedBeacon);
    given()
        .when().contentType(ContentType.JSON).body(jsonb.toJson(p)).post("/rest/pilots")
        .then()
        .statusCode(201);
  }

  public static void createWithCategory(String first, String last, String categoryName) {
    TestRestCategoryEndpoint.create(categoryName);
    CategoryDTO category = TestRestCategoryEndpoint.getByName(categoryName);
    NestedCategoryDTO nestedCategory = new NestedCategoryDTO();
    nestedCategory.setName(categoryName);
    nestedCategory.setId(category.getId());

    PilotDTO p = new PilotDTO();
    p.setFirstName(first);
    p.setLastName(last);
    p.setCategory(nestedCategory);
    given()
        .when().contentType(ContentType.JSON).body(jsonb.toJson(p)).post("/rest/pilots")
        .then()
        .statusCode(201);
  }

  public void assertTestCleanedEverything() {
    assertThat(getAllPilots()).isNullOrEmpty();
    assertThat(TestRestBeaconEndpoint.getAllBeacons()).isNullOrEmpty();
    assertThat(TestRestCategoryEndpoint.getAllCategories()).isNullOrEmpty();
  }
}
