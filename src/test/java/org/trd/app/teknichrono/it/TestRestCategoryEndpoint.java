package org.trd.app.teknichrono.it;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.trd.app.teknichrono.model.dto.CategoryDTO;
import org.trd.app.teknichrono.model.jpa.Category;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;

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
    long id = b.getId();
    getById(id);

    CategoryDTO modifiedBeacon = new CategoryDTO();
    modifiedBeacon.setName("Expert");
    modifiedBeacon.setId(id);
    update(id, modifiedBeacon);
    List<CategoryDTO> categories = getAllCategories();
    Assertions.assertThat(categories.size()).isEqualTo(1);
    getByNumberFails("Newbie");
    CategoryDTO newReturnedBeacon = getByName("Expert");
    Assertions.assertThat(newReturnedBeacon.getId()).isEqualTo(id);

    delete(id);
  }

  /**
   * ******************** Reusable *********************
   **/

  private void update(long id, CategoryDTO modifiedCategory) {
    given().pathParam("id", id)
        .when().contentType(ContentType.JSON).body(jsonb.toJson(modifiedCategory)).put("/rest/categories/{id}")
        .then()
        .statusCode(204);
  }


  public List<CategoryDTO> getAllCategories() {
    Response r = given()
        .when().get("/rest/categories")
        .then()
        .statusCode(200)
        .extract().response();
    return jsonb.fromJson(r.asString(), new ArrayList<CategoryDTO>() {
    }.getClass().getGenericSuperclass());
  }

  public List<CategoryDTO> getAllCategoriesWindow(int start, int max) {
    Response r = given().queryParam("start", Integer.valueOf(start)).queryParam("max", Integer.valueOf(max))
        .when().get("/rest/categories")
        .then()
        .statusCode(200)
        .extract().response();
    return jsonb.fromJson(r.asString(), new ArrayList<CategoryDTO>() {
    }.getClass().getGenericSuperclass());
  }

  public void delete(long id) {
    given().pathParam("id", id)
        .when().delete("/rest/categories/{id}")
        .then()
        .statusCode(204);
  }

  public CategoryDTO getById(long id) {
    Response r = given().pathParam("id", id)
        .when().get("/rest/categories/{id}")
        .then()
        .statusCode(200)
        .extract().response();
    return jsonb.fromJson(r.asString(), CategoryDTO.class);
  }

  public void getByNumberFails(String name) {
    given().queryParam("name", name)
        .when().get("/rest/categories/name")
        .then()
        .statusCode(404);
  }

  public CategoryDTO getByName(String name) {
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
        .statusCode(201);
  }
}
