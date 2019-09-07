package org.trd.app.teknichrono.it;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.trd.app.teknichrono.model.dto.CategoryDTO;
import org.trd.app.teknichrono.model.dto.NestedPilotDTO;
import org.trd.app.teknichrono.model.dto.PilotDTO;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.*;

@Tag("integration")
@QuarkusTest
public class TestRestCategoryEndpoint extends TestRestEndpoint<CategoryDTO> {

  private TestRestPilotEndPoint restPilot;

  public TestRestCategoryEndpoint() {
    super("categories", CategoryDTO.class, new ArrayList<CategoryDTO>() {
      private static final long serialVersionUID = 4376809210010796602L;
    }.getClass().getGenericSuperclass());
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
    List<CategoryDTO> categories = getAll();
    Assertions.assertThat(categories.size()).isEqualTo(0);
    create("Newbie");
    categories = getAll();
    Assertions.assertThat(categories.size()).isEqualTo(1);
    create("Expert");
    categories = getAll();
    Assertions.assertThat(categories.size()).isEqualTo(2);

    List<CategoryDTO> someBeacons = getAllInWindow(1, 1);
    Assertions.assertThat(someBeacons.size()).isEqualTo(1);

    categories = getAll();
    for (CategoryDTO beacon : categories) {
      delete(beacon.getId());
    }
    categories = getAll();
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
    List<CategoryDTO> categories = getAll();
    Assertions.assertThat(categories.size()).isEqualTo(1);
    getByName("Newbie", NOT_FOUND);
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

    restPilot.create("Pilot1", "OfCategory");
    PilotDTO pilot = restPilot.getByName("Pilot1", "OfCategory");
    long pilot1Id = pilot.getId();
    NestedPilotDTO pilot1Dto = new NestedPilotDTO();
    pilot1Dto.setId(pilot.getId());
    modifiedCategory.getPilots().add(pilot1Dto);

    update(id, modifiedCategory);
    // Update twice has no impact
    update(id, modifiedCategory);

    getByName("Newbie", NOT_FOUND);
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

    restPilot.create("Pilot2", "OfCategory");
    PilotDTO pilot2 = restPilot.getByName("Pilot2", "OfCategory");
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

    restPilot.create("Pilot1", "OfCategory");
    PilotDTO pilot = restPilot.getByName("Pilot1", "OfCategory");
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

    restPilot.create("Pilot2", "OfCategory");
    PilotDTO pilot2 = restPilot.getByName("Pilot2", "OfCategory");
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
    List<CategoryDTO> categories = getAll();
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


  public void addPilot(long id, long pilotId) {
    given().pathParam("id", id).queryParam("pilotId", pilotId)
        .when().contentType(ContentType.JSON).post("/rest/categories/{id}/addPilot")
        .then()
        .statusCode(200);
  }

  public void deleteWithPilots(long id, Long... pilotIds) {
    for (Long pilotId : pilotIds) {
      restPilot.delete(pilotId);
    }
    given().pathParam("id", id)
        .when().delete("/rest/categories/{id}")
        .then()
        .statusCode(204);
  }

  public void create(String name) {
    CategoryDTO c = new CategoryDTO();
    c.setName(name);
    create(c);
  }

  public void createWithPilot(String name, String firstName, String lastName) {
    restPilot.create(firstName, lastName);
    PilotDTO pilot = restPilot.getByName(firstName, lastName);
    NestedPilotDTO p = new NestedPilotDTO();
    p.setId(pilot.getId());
    p.setFirstName(pilot.getFirstName());
    p.setLastName(pilot.getLastName());

    CategoryDTO c = new CategoryDTO();
    c.setName(name);
    c.getPilots().add(p);

    create(c);
  }

  public void assertTestCleanedEverything() {
    Assertions.assertThat(getAll()).isNullOrEmpty();
    Assertions.assertThat(restPilot.getAll()).isNullOrEmpty();
  }

}
