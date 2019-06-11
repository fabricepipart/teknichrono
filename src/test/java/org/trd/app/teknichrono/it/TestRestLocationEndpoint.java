package org.trd.app.teknichrono.it;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.trd.app.teknichrono.model.dto.LocationDTO;
import org.trd.app.teknichrono.model.dto.NestedSessionDTO;
import org.trd.app.teknichrono.model.dto.SessionDTO;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.*;

@Tag("integration")
@QuarkusTest
public class TestRestLocationEndpoint {

  private static Jsonb jsonb = JsonbBuilder.create();

  /**
   * ******************** Tests *********************
   **/

  @Test
  public void testLists() {
    List<LocationDTO> locations = getAllLocations();
    Assertions.assertThat(locations.size()).isEqualTo(0);
    create("Newbie");
    locations = getAllLocations();
    Assertions.assertThat(locations.size()).isEqualTo(1);
    create("Expert");
    locations = getAllLocations();
    Assertions.assertThat(locations.size()).isEqualTo(2);

    List<LocationDTO> someBeacons = getAllLocationsWindow(1, 1);
    Assertions.assertThat(someBeacons.size()).isEqualTo(1);

    locations = getAllLocations();
    for (LocationDTO beacon : locations) {
      delete(beacon.getId());
    }
    locations = getAllLocations();
    Assertions.assertThat(locations.size()).isEqualTo(0);
  }


  @Test
  public void testCreateModifyDelete() {
    create("Somewhere");
    LocationDTO locationDTO = getByName("Somewhere");
    assertThat(locationDTO.getSessions()).isNullOrEmpty();
    long id = locationDTO.getId();
    getById(id);

    LocationDTO modifiedLocation = new LocationDTO();
    modifiedLocation.setName("Elsewhere");
    modifiedLocation.setId(id);
    update(id, modifiedLocation);
    List<LocationDTO> locations = getAllLocations();
    Assertions.assertThat(locations.size()).isEqualTo(1);
    getByNameFails("Somewhere");
    LocationDTO newLocation = getByName("Elsewhere");
    Assertions.assertThat(newLocation.getId()).isEqualTo(id);

    delete(id);
    assertTestCleanedEverything();
  }

  @Test
  public void testCreateWithSession() {
    createWithSession("Somewhere", "SessionName");
    LocationDTO location = getByName("Somewhere");
    assertThat(location.getSessions()).isNotNull();
    assertThat(location.getSessions()).hasSize(1);
    assertThat(location.getName()).isEqualTo("Somewhere");
    assertThat(location.getSessions().iterator().next().getName()).isEqualTo("SessionName");

    long id = location.getId();
    location = getById(id);
    assertThat(location.getSessions()).isNotNull();
    assertThat(location.getSessions()).hasSize(1);
    assertThat(location.getName()).isEqualTo("Somewhere");
    assertThat(location.getSessions().iterator().next().getName()).isEqualTo("SessionName");
    long sessionId = location.getSessions().iterator().next().getId();

    deleteWithSessions(id, sessionId);
    assertTestCleanedEverything();
  }

  @Test
  public void testAddSessionViaUpdate() {
    create("Here");
    LocationDTO b = getByName("Here");
    assertThat(b.getSessions()).isNullOrEmpty();

    long id = b.getId();

    LocationDTO modifiedLocation = new LocationDTO();
    modifiedLocation.setName("Elswere");
    modifiedLocation.setLoopTrack(true);
    modifiedLocation.setId(id);

    TestRestSessionEndpoint.create("Session Name");
    SessionDTO sessionDto1 = TestRestSessionEndpoint.getByName("Session Name");
    long session1Id = sessionDto1.getId();
    NestedSessionDTO nestedSession1dto = new NestedSessionDTO();
    nestedSession1dto.setId(session1Id);
    modifiedLocation.getSessions().add(nestedSession1dto);

    update(id, modifiedLocation);
    // Update twice has no impact
    update(id, modifiedLocation);

    getByNameFails("Here");
    LocationDTO newReturnedLocation = getByName("Elswere");
    assertThat(newReturnedLocation.getId()).isEqualTo(id);
    assertThat(newReturnedLocation.isLoopTrack()).isEqualTo(true);
    assertThat(newReturnedLocation.getSessions()).isNotNull();
    assertThat(newReturnedLocation.getSessions()).hasSize(1);
    NestedSessionDTO sessionDtoFound = newReturnedLocation.getSessions().iterator().next();
    assertThat(sessionDtoFound.getId()).isEqualTo(session1Id);
    assertThat(sessionDtoFound.getName()).isEqualTo("Session Name");

    modifiedLocation = new LocationDTO();
    modifiedLocation.setName("Elswere");
    modifiedLocation.setId(id);
    modifiedLocation.setLoopTrack(false);

    TestRestSessionEndpoint.create("Other Session Name");
    SessionDTO sessionDto2 = TestRestSessionEndpoint.getByName("Other Session Name");
    long session2Id = sessionDto2.getId();
    NestedSessionDTO nestedSession2dto = new NestedSessionDTO();
    nestedSession2dto.setId(session2Id);
    modifiedLocation.getSessions().add(nestedSession1dto);
    modifiedLocation.getSessions().add(nestedSession2dto);

    update(id, modifiedLocation);

    newReturnedLocation = getByName("Elswere");
    assertThat(newReturnedLocation.getId()).isEqualTo(id);
    assertThat(newReturnedLocation.isLoopTrack()).isEqualTo(false);
    assertThat(newReturnedLocation.getSessions()).isNotNull();
    assertThat(newReturnedLocation.getSessions()).hasSize(2);

    deleteWithSessions(id, session1Id, session2Id);
    assertTestCleanedEverything();
  }

  @Test
  public void testAddSessionViaAdd() {
    create("Heaven");
    LocationDTO b = getByName("Heaven");
    long id = b.getId();

    TestRestSessionEndpoint.create("Session Name");
    SessionDTO sessionDto1 = TestRestSessionEndpoint.getByName("Session Name");
    long session1Id = sessionDto1.getId();

    addSession(id, session1Id);
    // Adding twice has no impact
    addSession(id, session1Id);

    LocationDTO newReturnedLocation = getByName("Heaven");
    assertThat(newReturnedLocation.getId()).isEqualTo(id);
    assertThat(newReturnedLocation.getSessions()).isNotNull();
    assertThat(newReturnedLocation.getSessions()).hasSize(1);
    NestedSessionDTO sessionDtoFound = newReturnedLocation.getSessions().iterator().next();
    assertThat(sessionDtoFound.getId()).isEqualTo(session1Id);
    assertThat(sessionDtoFound.getName()).isEqualTo("Session Name");

    TestRestSessionEndpoint.create("Other Session Name");
    SessionDTO sessionDto2 = TestRestSessionEndpoint.getByName("Other Session Name");
    long session2Id = sessionDto2.getId();

    addSession(id, session2Id);

    newReturnedLocation = getByName("Heaven");
    assertThat(newReturnedLocation.getId()).isEqualTo(id);
    assertThat(newReturnedLocation.getSessions()).isNotNull();
    assertThat(newReturnedLocation.getSessions()).hasSize(2);

    deleteWithSessions(id, session1Id, session2Id);
    assertTestCleanedEverything();
  }

  @Test
  public void testRemoveSession() {
    createWithSession("Where?", "SessionName");
    LocationDTO c = getByName("Where?");
    long id = c.getId();
    long sessionId = c.getSessions().iterator().next().getId();

    LocationDTO modifiedLocation = new LocationDTO();
    modifiedLocation.setName("Where?");
    modifiedLocation.setId(id);

    update(id, modifiedLocation);

    LocationDTO newReturnedLocation = getByName("Where?");
    assertThat(newReturnedLocation.getId()).isEqualTo(id);
    assertThat(newReturnedLocation.getSessions()).isNotNull();
    assertThat(newReturnedLocation.getSessions()).hasSize(0);

    deleteWithSessions(id, sessionId);
    assertTestCleanedEverything();
  }

  /**
   * ******************** Reusable *********************
   **/

  public static void update(long id, LocationDTO modifiedLocation) {
    given().pathParam("id", id)
        .when().contentType(ContentType.JSON).body(jsonb.toJson(modifiedLocation)).put("/rest/locations/{id}")
        .then()
        .statusCode(204);
  }

  public static void addSession(long id, long sessionId) {
    given().pathParam("id", id).queryParam("sessionId", sessionId)
        .when().contentType(ContentType.JSON).post("/rest/locations/{id}/addSession")
        .then()
        .statusCode(200);
  }


  public static List<LocationDTO> getAllLocations() {
    Response r = given()
        .when().get("/rest/locations")
        .then()
        .statusCode(200)
        .extract().response();
    return jsonb.fromJson(r.asString(), new ArrayList<LocationDTO>() {
    }.getClass().getGenericSuperclass());
  }

  public static List<LocationDTO> getAllLocationsWindow(int page, int pageSize) {
    Response r = given().queryParam("page", page).queryParam("pageSize", pageSize)
        .when().get("/rest/locations")
        .then()
        .statusCode(200)
        .extract().response();
    return jsonb.fromJson(r.asString(), new ArrayList<LocationDTO>() {
    }.getClass().getGenericSuperclass());
  }

  public static void delete(long id) {
    given().pathParam("id", id)
        .when().delete("/rest/locations/{id}")
        .then()
        .statusCode(204);
  }

  public static void deleteWithSessions(long id, long... sessionIds) {
    for (Long sessionId : sessionIds) {
      TestRestSessionEndpoint.delete(sessionId);
    }
    delete(id);
  }

  public static LocationDTO getById(long id) {
    Response r = given().pathParam("id", id)
        .when().get("/rest/locations/{id}")
        .then()
        .statusCode(200)
        .extract().response();
    return jsonb.fromJson(r.asString(), LocationDTO.class);
  }

  public static void getByNameFails(String name) {
    given().queryParam("name", name)
        .when().get("/rest/locations/name")
        .then()
        .statusCode(404);
  }

  public static LocationDTO getByName(String name) {
    Response r = given().queryParam("name", name)
        .when().get("/rest/locations/name")
        .then()
        .statusCode(200)
        .extract().response();
    return jsonb.fromJson(r.asString(), LocationDTO.class);
  }

  public static void create(String name) {
    LocationDTO p = new LocationDTO();
    p.setName(name);
    given()
        .when().contentType(ContentType.JSON).body(jsonb.toJson(p)).post("/rest/locations")
        .then()
        .statusCode(201);
  }

  public static void createWithSession(String name, String sessionName) {
    TestRestSessionEndpoint.create(sessionName);
    SessionDTO session = TestRestSessionEndpoint.getByName(sessionName);
    NestedSessionDTO nestedSessionDTO = new NestedSessionDTO();
    nestedSessionDTO.setId(session.getId());
    nestedSessionDTO.setName(session.getName());

    LocationDTO locationDTO = new LocationDTO();
    locationDTO.setName(name);
    locationDTO.getSessions().add(nestedSessionDTO);

    given()
        .when().contentType(ContentType.JSON).body(jsonb.toJson(locationDTO)).post("/rest/locations")
        .then()
        .statusCode(201);
  }

  public void assertTestCleanedEverything() {
    assertThat(getAllLocations()).isNullOrEmpty();
    assertThat(TestRestSessionEndpoint.getAllSessions()).isNullOrEmpty();
  }
}
