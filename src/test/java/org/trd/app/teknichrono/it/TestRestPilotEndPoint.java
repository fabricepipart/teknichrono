package org.trd.app.teknichrono.it;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.trd.app.teknichrono.model.dto.BeaconDTO;
import org.trd.app.teknichrono.model.dto.CategoryDTO;
import org.trd.app.teknichrono.model.dto.NestedBeaconDTO;
import org.trd.app.teknichrono.model.dto.NestedCategoryDTO;
import org.trd.app.teknichrono.model.dto.PilotDTO;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.*;

@Tag("integration")
@QuarkusTest
public class TestRestPilotEndPoint extends TestRestEndpoint<PilotDTO> {


  private TestRestBeaconEndpoint restBeacon = new TestRestBeaconEndpoint();
  private TestRestCategoryEndpoint restCategory = new TestRestCategoryEndpoint();

  public TestRestPilotEndPoint() {
    super("pilots", PilotDTO.class, new ArrayList<PilotDTO>() {
      private static final long serialVersionUID = -5888949647537408775L;
    }.getClass().getGenericSuperclass());
  }

  @BeforeEach
  public void prepare() {

  }

  /**
   * ******************** Tests *********************
   **/

  @Test
  public void testLists() {
    List<PilotDTO> pilots = getAll();
    assertThat(pilots.size()).isEqualTo(0);
    create("Pilot", "One");
    pilots = getAll();
    assertThat(pilots.size()).isEqualTo(1);
    create("Pilot", "Two");
    pilots = getAll();
    assertThat(pilots.size()).isEqualTo(2);

    List<PilotDTO> somePilots = getAllInWindow(1, 1);
    assertThat(somePilots.size()).isEqualTo(1);

    pilots = getAll();
    for (PilotDTO pilot : pilots) {
      delete(pilot.getId());
    }
    pilots = getAll();
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
    List<PilotDTO> pilots = getAll();
    assertThat(pilots.size()).isEqualTo(1);
    getByName("Pilot", "A", NOT_FOUND);
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

    restBeacon.create(210);
    BeaconDTO beacon = restBeacon.getByNumber(210);
    long beaconId = beacon.getId();
    NestedBeaconDTO nestedBeacon = new NestedBeaconDTO();
    nestedBeacon.setId(beaconId);
    modifiedPilot.setCurrentBeacon(nestedBeacon);

    update(id, modifiedPilot);
    // Update twice has no impact
    update(id, modifiedPilot);

    getByName("Pilot", "NoBeacon", NOT_FOUND);
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

    restCategory.create("Expert");
    CategoryDTO category = restCategory.getByName("Expert");
    long categoryId = category.getId();
    NestedCategoryDTO nestedCategory = new NestedCategoryDTO();
    nestedCategory.setId(categoryId);
    modifiedPilot.setCategory(nestedCategory);

    update(id, modifiedPilot);
    // Update twice has no impact
    update(id, modifiedPilot);

    getByName("Pilot", "NoCategory", NOT_FOUND);
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
    restBeacon.create(220);
    BeaconDTO beacon = restBeacon.getByNumber(220);
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


  @Test
  public void testLapsAreDeletedWhenPilotDeleted() {
    //TODO
  }

  /**
   * ******************** Reusable *********************
   **/


  public void associateBeacon(long id, long beaconId) {
    given().pathParam("id", id).queryParam("beaconId", beaconId)
        .when().contentType(ContentType.JSON).post("/rest/pilots/{id}/setBeacon")
        .then()
        .statusCode(200);
  }

  public PilotDTO getByName(String first, String last) {
    return getByQuery("name", "firstname", first, "lastname", last);
  }

  public Response getByName(String first, String last, int expected) {
    return getByQuery("name", "firstname", first, "lastname", last, expected);
  }

  public void deleteWithBeacon(long id, long beaconId) {
    restBeacon.delete(beaconId);
    delete(id);
  }

  public void deleteWithCategory(long id, long catId) {
    restCategory.delete(catId);
    delete(id);
  }


  public void create(String first, String last) {
    PilotDTO p = new PilotDTO();
    p.setFirstName(first);
    p.setLastName(last);
    create(p);
  }

  public void createWithBeacon(String first, String last, int beaconNumber) {
    restBeacon.create(beaconNumber);
    BeaconDTO beacon = restBeacon.getByNumber(beaconNumber);
    NestedBeaconDTO nestedBeacon = new NestedBeaconDTO();
    nestedBeacon.setNumber(beaconNumber);
    nestedBeacon.setId(beacon.getId());

    PilotDTO p = new PilotDTO();
    p.setFirstName(first);
    p.setLastName(last);
    p.setCurrentBeacon(nestedBeacon);

    create(p);
  }

  public void createWithCategory(String first, String last, String categoryName) {
    restCategory.create(categoryName);
    CategoryDTO category = restCategory.getByName(categoryName);
    NestedCategoryDTO nestedCategory = new NestedCategoryDTO();
    nestedCategory.setName(categoryName);
    nestedCategory.setId(category.getId());

    PilotDTO p = new PilotDTO();
    p.setFirstName(first);
    p.setLastName(last);
    p.setCategory(nestedCategory);

    create(p);
  }

  public void assertTestCleanedEverything() {
    assertThat(getAll()).isNullOrEmpty();
    assertThat(restBeacon.getAll()).isNullOrEmpty();
    assertThat(restCategory.getAll()).isNullOrEmpty();
  }
}
