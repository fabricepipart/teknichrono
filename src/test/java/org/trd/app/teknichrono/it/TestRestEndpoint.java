package org.trd.app.teknichrono.it;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;

public abstract class TestRestEndpoint<D> {

  public static final int NO_CONTENT = javax.ws.rs.core.Response.Status.NO_CONTENT.getStatusCode();
  public static final int OK = javax.ws.rs.core.Response.Status.OK.getStatusCode();
  public static final int NOT_FOUND = javax.ws.rs.core.Response.Status.NOT_FOUND.getStatusCode();

  private static Jsonb jsonb = JsonbBuilder.create();

  private final String restEntrypointName;
  private final Class<D> dtoClass;

  public TestRestEndpoint(String restEntrypointName, Class<D> dtoClass) {
    this.restEntrypointName = restEntrypointName;
    this.dtoClass = dtoClass;
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
    return jsonb.fromJson(r.asString(), new ArrayList<D>() {
    }.getClass().getGenericSuperclass());
  }

  public List<D> getAllInWindow(int page, int pageSize) {
    Response r = given().queryParam("page", page).queryParam("pageSize", pageSize)
        .when().get("/rest/" + restEntrypointName)
        .then()
        .statusCode(OK)
        .extract().response();
    return jsonb.fromJson(r.asString(), new ArrayList<D>() {
    }.getClass().getGenericSuperclass());
  }

  public void delete(long id) {
    given().pathParam("id", id)
        .when().delete("/rest/" + restEntrypointName + "/{id}")
        .then()
        .statusCode(NO_CONTENT);
  }

  public D getById(long id) {
    Response r = given().pathParam("id", id)
        .when().get("/rest/" + restEntrypointName + "/{id}")
        .then()
        .statusCode(OK)
        .extract().response();
    return jsonb.fromJson(r.asString(), dtoClass);
  }

  public D getBy(String restPath, String fieldName, Object fieldValue) {
    return getBy(restPath, fieldName, fieldValue, OK);
  }

  public D getBy(String restPath, String fieldName, Object fieldValue, int expectedStatus) {
    Response r = given().pathParam(fieldName, fieldValue)
        .when().get("/rest/" + restEntrypointName + "/" + restPath + "/{fieldName}")
        .then()
        .statusCode(expectedStatus)
        .extract().response();
    return jsonb.fromJson(r.asString(), dtoClass);
  }

  public D getByQuery(String restPath, String field1Name, Object field1Value, String field2Name, Object field2Value, int expectedStatus) {
    Response r = given().queryParam(field1Name, field1Value).queryParam(field2Name, field2Value)
        .when().get("/rest/" + restEntrypointName + "/" + restPath)
        .then()
        .statusCode(expectedStatus)
        .extract().response();
    return jsonb.fromJson(r.asString(), dtoClass);
  }

  public void create(D dto) {
    given()
        .when().contentType(ContentType.JSON).body(jsonb.toJson(dto)).post("/rest/" + restEntrypointName + "/")
        .then()
        .statusCode(NO_CONTENT);
  }

  public D getByName(String name, int expectedStatus) {
    return getBy("name", "name", name, expectedStatus);
  }

  public D getByName(String name) {
    return getBy("name", "name", name, OK);
  }

}
