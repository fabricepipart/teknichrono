package org.trd.app.teknichrono.it;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.trd.app.teknichrono.model.dto.EntityDTO;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import java.lang.reflect.Type;
import java.util.List;

import static io.restassured.RestAssured.given;

public abstract class TestRestEndpoint<D extends EntityDTO> {

  public static final int NO_CONTENT = jakarta.ws.rs.core.Response.Status.NO_CONTENT.getStatusCode();
  public static final int OK = jakarta.ws.rs.core.Response.Status.OK.getStatusCode();
  public static final int NOT_FOUND = jakarta.ws.rs.core.Response.Status.NOT_FOUND.getStatusCode();
  public static final int BAD_REQUEST = jakarta.ws.rs.core.Response.Status.BAD_REQUEST.getStatusCode();

  protected static Jsonb jsonb = JsonbBuilder.create();

  protected final String restEntrypointName;
  protected final Class<D> dtoClass;
  protected final Type dtoListClass;

  public TestRestEndpoint(String restEntrypointName, Class<D> dtoClass, Type dtoListClass) {
    this.restEntrypointName = restEntrypointName;
    this.dtoClass = dtoClass;
    this.dtoListClass = dtoListClass;
  }


  public void update(long id, D modifiedDto) {
    given().pathParam("id", id)
        .when().contentType(ContentType.JSON).body(jsonb.toJson(modifiedDto)).put("/rest/" + restEntrypointName + "/{id}")
        .then()
        .statusCode(NO_CONTENT);
  }

  public List<D> getAll() {
    Response r = given()
        .when().get("/rest/" + restEntrypointName)
        .then()
        .statusCode(OK)
        .extract().response();

    return jsonb.fromJson(r.asString(), dtoListClass);
  }

  public List<D> getAllInWindow(int page, int pageSize) {
    Response r = given().queryParam("page", page).queryParam("pageSize", pageSize)
        .when().get("/rest/" + restEntrypointName)
        .then()
        .statusCode(OK)
        .extract().response();
    return jsonb.fromJson(r.asString(), dtoListClass);
  }

  public void delete(long id) {
    delete(id, NO_CONTENT);
  }

  public void delete(long id, int statusCode) {
    given().pathParam("id", id)
        .when().delete("/rest/" + restEntrypointName + "/{id}")
        .then()
        .statusCode(statusCode);
  }


  public void deleteAll() {
    List<D> all = getAll();
    for (D d : all) {
      delete(d.getId());
    }
  }

  public D getById(long id) {
    D dto = getById(id, OK);
    return dto;
  }

  public D getById(long id, int statusCode) {
    Response r = given().pathParam("id", id)
        .when().get("/rest/" + restEntrypointName + "/{id}")
        .then()
        .statusCode(statusCode)
        .extract().response();
    return jsonb.fromJson(r.asString(), dtoClass);
  }

  public D getBy(String restPath, String fieldName, Object fieldValue) {
    Response r = getBy(restPath, fieldName, fieldValue, OK);
    return jsonb.fromJson(r.asString(), dtoClass);
  }

  public Response getBy(String restPath, String fieldName, Object fieldValue, int expectedStatus) {
    return given().pathParam(fieldName, fieldValue)
        .when().get("/rest/" + restEntrypointName + "/" + restPath + "/{" + fieldName + "}")
        .then()
        .statusCode(expectedStatus)
        .extract().response();
  }

  public Response getByQuery(String restPath, String fieldName, Object fieldValue, int expectedStatus) {
    return given().queryParam(fieldName, fieldValue)
        .when().get("/rest/" + restEntrypointName + "/" + restPath)
        .then()
        .statusCode(expectedStatus)
        .extract().response();
  }

  public D getByQuery(String restPath, String fieldName, Object fieldValue) {
    Response r = getByQuery(restPath, fieldName, fieldValue, OK);
    return jsonb.fromJson(r.asString(), dtoClass);
  }

  public Response getByQuery(String restPath, String field1Name, Object field1Value, String field2Name, Object field2Value, int expectedStatus) {
    return given().queryParam(field1Name, field1Value).queryParam(field2Name, field2Value)
        .when().get("/rest/" + restEntrypointName + "/" + restPath)
        .then()
        .statusCode(expectedStatus)
        .extract().response();
  }

  public D getByQuery(String restPath, String field1Name, Object field1Value, String field2Name, Object field2Value) {
    Response r = getByQuery(restPath, field1Name, field1Value, field2Name, field2Value, OK);
    return jsonb.fromJson(r.asString(), dtoClass);
  }

  public void create(D dto) {
    create(dto, NO_CONTENT);
  }

  public void create(D dto, int statusCode) {
    given()
        .when().contentType(ContentType.JSON).body(jsonb.toJson(dto)).post("/rest/" + restEntrypointName + "/")
        .then()
        .statusCode(statusCode);
  }

  public Response getByName(String name, int expectedStatus) {
    return getByQuery("name", "name", name, expectedStatus);
  }

  public D getByName(String name) {
    return getByQuery("name", "name", name);
  }

}
