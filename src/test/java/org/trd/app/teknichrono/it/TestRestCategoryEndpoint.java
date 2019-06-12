package org.trd.app.teknichrono.it;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.trd.app.teknichrono.model.dto.CategoryDTO;
import org.trd.app.teknichrono.model.dto.NestedPilotDTO;
import org.trd.app.teknichrono.model.dto.PilotDTO;
import org.trd.app.teknichrono.model.jpa.Category;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.*;

@Tag("integration")
@QuarkusTest
public class TestRestCategoryEndpoint {

  private static Jsonb jsonb = JsonbBuilder.create();

  /**
   * ******************** Tests *********************
   **/

  @Test
  public void testLists() {
    List<CategoryDTO> categories = getAllCategories();
    Assertions.assertThat(categories.size()).isEqualTo(0);
    create("Newbie");
    categories = getAllCategories();
    Assertions.assertThat(categories.size()).isEqualTo(1);
    create("Expert");
    categories = getAllCategories();
    Assertions.assertThat(categories.size()).isEqualTo(2);

    List<CategoryDTO> someBeacons = getAllCategoriesWindow(1, 1);
    Assertions.assertThat(someBeacons.size()).isEqualTo(1);

    categories = getAllCategories();
    for (CategoryDTO beacon : categories) {
      delete(beacon.getId());
    }
    categories = getAllCategories();
    Assertions.assertThat(categories.size()).isEqualTo(0);
  }


  @Test
  public void testCreateModifyDelete() {
    create("Newbie");
    CategoryDTO b = getByName("Newbie");
    assertThat(b.getPilots()).isNullOrEmpty();
    long id = b.getId();
    getById(id);

    CategoryDTO modifiedCategory = new CategoryDTO();
    modifiedCategory.setName("Expert");
    modifiedCategory.setId(id);
    update(id, modifiedCategory);
    List<CategoryDTO> categories = getAllCategories();
    Assertions.assertThat(categories.size()).isEqualTo(1);
    getByNameFails("Newbie");
    CategoryDTO newReturnedBeacon = getByName("Expert");
    Assertions.assertThat(newReturnedBeacon.getId()).isEqualTo(id);

    delete(id);
    assertTestCleanedEverything();
  }

  @Test
  public void testCreateWithPilot() {
    createWithPilot("Testers", "Pilot", "OfCategory");
    CategoryDTO c = getByName("Testers");
    assertThat(c.getPilots()).isNotNull();
    assertThat(c.getPilots()).hasSize(1);
    assertThat(c.getName()).isEqualTo("Testers");

    long id = c.getId();
    c = getById(id);
    assertThat(c.getPilots()).isNotNull();
    assertThat(c.getPilots()).hasSize(1);
    assertThat(c.getName()).isEqualTo("Testers");
    long pilotId = c.getPilots().iterator().next().getId();

    deleteWithPilots(id, pilotId);
    assertTestCleanedEverything();
  }

  @Test
  public void testAddPilotViaUpdate() {
    create("Newbie");
    CategoryDTO b = getByName("Newbie");
    assertThat(b.getPilots()).isNullOrEmpty();

    long id = b.getId();

    CategoryDTO modifiedCategory = new CategoryDTO();
    modifiedCategory.setName("Expert");
    modifiedCategory.setId(id);

    TestRestPilotEndPoint.create("Pilot1", "OfCategory");
    PilotDTO pilot = TestRestPilotEndPoint.getByName("Pilot1", "OfCategory");
    long pilot1Id = pilot.getId();
    NestedPilotDTO pilot1Dto = new NestedPilotDTO();
    pilot1Dto.setId(pilot.getId());
    modifiedCategory.getPilots().add(pilot1Dto);

    update(id, modifiedCategory);
    // Update twice has no impact
    update(id, modifiedCategory);

    getByNameFails("Newbie");
    CategoryDTO newReturnedCategory = getByName("Expert");
    assertThat(newReturnedCategory.getId()).isEqualTo(id);
    assertThat(newReturnedCategory.getPilots()).isNotNull();
    assertThat(newReturnedCategory.getPilots()).hasSize(1);
    NestedPilotDTO pilotDtoFound = newReturnedCategory.getPilots().iterator().next();
    assertThat(pilotDtoFound.getId()).isEqualTo(pilot1Id);
    assertThat(pilotDtoFound.getFirstName()).isEqualTo("Pilot1");
    assertThat(pilotDtoFound.getLastName()).isEqualTo("OfCategory");

    modifiedCategory = new CategoryDTO();
    modifiedCategory.setName("Expert");
    modifiedCategory.setId(id);

    TestRestPilotEndPoint.create("Pilot2", "OfCategory");
    PilotDTO pilot2 = TestRestPilotEndPoint.getByName("Pilot2", "OfCategory");
    long pilot2Id = pilot2.getId();
    NestedPilotDTO pilot2Dto = new NestedPilotDTO();
    pilot2Dto.setId(pilot2.getId());
    modifiedCategory.getPilots().add(pilot1Dto);
    modifiedCategory.getPilots().add(pilot2Dto);

    update(id, modifiedCategory);

    newReturnedCategory = getByName("Expert");
    assertThat(newReturnedCategory.getId()).isEqualTo(id);
    assertThat(newReturnedCategory.getPilots()).isNotNull();
    assertThat(newReturnedCategory.getPilots()).hasSize(2);

    deleteWithPilots(id, pilot1Id, pilot2Id);
    assertTestCleanedEverything();
  }

  @Test
  public void testAddPilotViaAdd() {
    create("Newbie");
    CategoryDTO b = getByName("Newbie");
    long id = b.getId();

    TestRestPilotEndPoint.create("Pilot1", "OfCategory");
    PilotDTO pilot = TestRestPilotEndPoint.getByName("Pilot1", "OfCategory");
    long pilot1Id = pilot.getId();

    addPilot(id, pilot1Id);
    // Adding twice has no impact
    addPilot(id, pilot1Id);

    CategoryDTO newReturnedCategory = getByName("Newbie");
    assertThat(newReturnedCategory.getId()).isEqualTo(id);
    assertThat(newReturnedCategory.getPilots()).isNotNull();
    assertThat(newReturnedCategory.getPilots()).hasSize(1);
    NestedPilotDTO pilotDtoFound = newReturnedCategory.getPilots().iterator().next();
    assertThat(pilotDtoFound.getId()).isEqualTo(pilot1Id);
    assertThat(pilotDtoFound.getFirstName()).isEqualTo("Pilot1");
    assertThat(pilotDtoFound.getLastName()).isEqualTo("OfCategory");

    TestRestPilotEndPoint.create("Pilot2", "OfCategory");
    PilotDTO pilot2 = TestRestPilotEndPoint.getByName("Pilot2", "OfCategory");
    long pilot2Id = pilot2.getId();

    addPilot(id, pilot2Id);

    newReturnedCategory = getByName("Newbie");
    assertThat(newReturnedCategory.getId()).isEqualTo(id);
    assertThat(newReturnedCategory.getPilots()).isNotNull();
    assertThat(newReturnedCategory.getPilots()).hasSize(2);

    deleteWithPilots(id, pilot1Id, pilot2Id);
    assertTestCleanedEverything();
  }

  @Test
  public void testRemovePilot() {
    createWithPilot("Testers", "Pilot", "OfCategory");
    CategoryDTO c = getByName("Testers");
    long id = c.getId();
    long pilotId = c.getPilots().iterator().next().getId();

    CategoryDTO modifiedCategory = new CategoryDTO();
    modifiedCategory.setName("Expert");
    modifiedCategory.setId(id);

    update(id, modifiedCategory);

    CategoryDTO newReturnedCategory = getByName("Expert");
    assertThat(newReturnedCategory.getId()).isEqualTo(id);
    assertThat(newReturnedCategory.getPilots()).isNotNull();
    assertThat(newReturnedCategory.getPilots()).hasSize(0);

    deleteWithPilots(id, pilotId);
    assertTestCleanedEverything();
  }

  /**
   * ******************** Reusable *********************
   **/

  public static void update(long id, CategoryDTO modifiedCategory) {
    given().pathParam("id", id)
        .when().contentType(ContentType.JSON).body(jsonb.toJson(modifiedCategory)).put("/rest/categories/{id}")
        .then()
        .statusCode(204);
  }

  public static void addPilot(long id, long pilotId) {
    given().pathParam("id", id).queryParam("pilotId", pilotId)
        .when().contentType(ContentType.JSON).post("/rest/categories/{id}/addPilot")
        .then()
        .statusCode(200);
  }

  public static List<CategoryDTO> getAllCategories() {
    Response r = given()
        .when().get("/rest/categories")
        .then()
        .statusCode(200)
        .extract().response();
    return jsonb.fromJson(r.asString(), new ArrayList<CategoryDTO>() {
    }.getClass().getGenericSuperclass());
  }

  public static List<CategoryDTO> getAllCategoriesWindow(int page, int pageSize) {
    Response r = given().queryParam("page", page).queryParam("pageSize", pageSize)
        .when().get("/rest/categories")
        .then()
        .statusCode(200)
        .extract().response();
    return jsonb.fromJson(r.asString(), new ArrayList<CategoryDTO>() {
    }.getClass().getGenericSuperclass());
  }

  public static void delete(long id) {
    given().pathParam("id", id)
        .when().delete("/rest/categories/{id}")
        .then()
        .statusCode(204);
  }

  public static void deleteWithPilots(long id, Long... pilotIds) {
    for (Long pilotId : pilotIds) {
      TestRestPilotEndPoint.delete(pilotId);
    }
    given().pathParam("id", id)
        .when().delete("/rest/categories/{id}")
        .then()
        .statusCode(204);
  }

  public static CategoryDTO getById(long id) {
    Response r = given().pathParam("id", id)
        .when().get("/rest/categories/{id}")
        .then()
        .statusCode(200)
        .extract().response();
    return jsonb.fromJson(r.asString(), CategoryDTO.class);
  }

  public static void getByNameFails(String name) {
    given().queryParam("name", name)
        .when().get("/rest/categories/name")
        .then()
        .statusCode(404);
  }

  public static CategoryDTO getByName(String name) {
    Response r = given().queryParam("name", name)
        .when().get("/rest/categories/name")
        .then()
        .statusCode(200)
        .extract().response();
    return jsonb.fromJson(r.asString(), CategoryDTO.class);
  }

  public static void create(String name) {
    Category c = new Category();
    c.setName(name);
    given()
        .when().contentType(ContentType.JSON).body(jsonb.toJson(c)).post("/rest/categories")
        .then()
        .statusCode(204);
  }

  public static void createWithPilot(String name, String firstName, String lastName) {
    TestRestPilotEndPoint.create(firstName, lastName);
    PilotDTO pilot = TestRestPilotEndPoint.getByName(firstName, lastName);
    NestedPilotDTO p = new NestedPilotDTO();
    p.setId(pilot.getId());
    p.setFirstName(pilot.getFirstName());
    p.setLastName(pilot.getLastName());

    CategoryDTO c = new CategoryDTO();
    c.setName(name);
    c.getPilots().add(p);

    given()
        .when().contentType(ContentType.JSON).body(jsonb.toJson(c)).post("/rest/categories")
        .then()
        .statusCode(204);
  }

  public static void assertTestCleanedEverything() {
    Assertions.assertThat(getAllCategories()).isNullOrEmpty();
    Assertions.assertThat(TestRestPilotEndPoint.getAllPilots()).isNullOrEmpty();
  }

}
