package org.trd.app.teknichrono.it;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.trd.app.teknichrono.model.dto.SessionDTO;
import org.trd.app.teknichrono.model.jpa.SessionType;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;

public class TestRestSessionEndpoint {


  private static Jsonb jsonb = JsonbBuilder.create();

  /**
   * ******************** Tests *********************
   **/

  /**
   * ******************** Reusable *********************
   **/
  public static List<SessionDTO> getAllSessions() {
    Response r = given()
        .when().get("/rest/sessions")
        .then()
        .statusCode(200)
        .extract().response();
    return jsonb.fromJson(r.asString(), new ArrayList<SessionDTO>() {
    }.getClass().getGenericSuperclass());
  }

  public static void delete(long id) {
    given().pathParam("id", id)
        .when().delete("/rest/sessions/{id}")
        .then()
        .statusCode(204);
  }

  public static SessionDTO getByName(String name) {
    Response r = given().queryParam("name", name)
        .when().get("/rest/sessions/name")
        .then()
        .statusCode(200)
        .extract().response();
    return jsonb.fromJson(r.asString(), SessionDTO.class);
  }

  public static void create(String name) {
    SessionDTO sessionDTO = new SessionDTO();
    sessionDTO.setName(name);
    sessionDTO.setType(SessionType.TIME_TRIAL.getIdentifier());
    sessionDTO.setStart(Instant.now().minusSeconds(60 * 60L));
    sessionDTO.setEnd(Instant.now().minusSeconds(120 * 60L));
    given()
        .when().contentType(ContentType.JSON).body(jsonb.toJson(sessionDTO)).post("/rest/sessions")
        .then()
        .statusCode(201);
  }
}
