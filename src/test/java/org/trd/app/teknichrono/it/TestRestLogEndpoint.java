package org.trd.app.teknichrono.it;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.trd.app.teknichrono.model.dto.ChronometerDTO;
import org.trd.app.teknichrono.model.dto.LogDTO;
import org.trd.app.teknichrono.model.dto.NestedChronometerDTO;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static io.restassured.RestAssured.given;

@Tag("integration")
@QuarkusTest
public class TestRestLogEndpoint extends TestRestEndpoint<LogDTO> {

  private TestRestChronometerEndpoint restChronometer;
  private ChronometerDTO chrono;
  private ChronometerDTO chrono2;

  public TestRestLogEndpoint() {
    super("logs", LogDTO.class, new ArrayList<LogDTO>() {
      private static final long serialVersionUID = 8895406210684323830L;
    }.getClass().getGenericSuperclass());
  }


  @BeforeEach
  public void prepare() {
    this.restChronometer = new TestRestChronometerEndpoint();
    this.restChronometer.create("Chrono");
    this.chrono = this.restChronometer.getByName("Chrono");
    this.restChronometer.create("Chrono2");
    this.chrono2 = this.restChronometer.getByName("Chrono2");
  }

  @AfterEach
  public void cleanup() {
    this.restChronometer.deleteAll();
    List<LogDTO> all = getLogs();
    for (LogDTO d : all) {
      delete(d.getId());
    }
  }


  /**
   * ******************** Tests *********************
   **/

  @Test
  public void testLists() {
    List<LogDTO> logs = getLogs();
    Assertions.assertThat(logs.size()).isEqualTo(0);
    create("Something happened", this.chrono.getId());
    logs = getLogs();
    Assertions.assertThat(logs.size()).isEqualTo(1);
    create("Something else", this.chrono.getId());
    logs = getLogs();
    Assertions.assertThat(logs.size()).isEqualTo(2);

    List<LogDTO> someLogs = getLogsInWindow(1, 1);
    Assertions.assertThat(someLogs.size()).isEqualTo(1);

    logs = getLogs();
    for (LogDTO log : logs) {
      delete(log.getId());
    }
    logs = getLogs();
    Assertions.assertThat(logs.size()).isEqualTo(0);
  }


  @Test
  public void testCreateModifyDelete() {
    create("Something logged", this.chrono.getId());
    LogDTO logDto = getLogs().get(0);
    long id = logDto.getId();
    getById(id);

    LogDTO modifiedLog = createLogDTO("Something else logged");
    modifiedLog.setId(id);
    NestedChronometerDTO nestedChronometer = new NestedChronometerDTO();
    nestedChronometer.setId(this.chrono.getId());
    modifiedLog.setChronometer(nestedChronometer);

    update(id, modifiedLog);
    List<LogDTO> logs = getLogs();
    Assertions.assertThat(logs.size()).isEqualTo(1);
    LogDTO newLog = getLogs().get(0);
    Assertions.assertThat(newLog.getId()).isEqualTo(id);

    delete(id);
  }

  @Test
  public void onlyReturnsLogsOfMentionedChronometer() {
    create("Something happened", this.chrono.getId());
    create("Something else", this.chrono.getId());
    List<LogDTO> logs = getLogs();
    Assertions.assertThat(logs.size()).isEqualTo(2);

    create("Something 2", this.chrono2.getId());

    logs = getLogs();
    Assertions.assertThat(logs.size()).isEqualTo(2);
  }

  @Test
  public void logsDeletingInCascade() {
    create("Something happened", this.chrono.getId());
    create("Something else", this.chrono.getId());
    List<LogDTO> logs = getLogs();
    Assertions.assertThat(logs.size()).isEqualTo(2);

    this.restChronometer.delete(this.chrono.getId());

    logs = getLogs();
    Assertions.assertThat(logs.size()).isEqualTo(0);
  }

  @Test
  public void returnsLogsInReverseChronologicalOrder() {
    create("Something happened", this.chrono.getId());
    List<LogDTO> logs = getLogs();
    Assertions.assertThat(logs.size()).isEqualTo(1);
    LogDTO firstLog = logs.get(0);

    create("Something else", this.chrono.getId());
    logs = getLogs();
    Assertions.assertThat(logs.size()).isEqualTo(2);

    Assertions.assertThat(logs.get(1).getId()).isEqualTo(firstLog.getId());
  }

  @Test
  public void returnsLogsOfWindowOnly() throws InterruptedException {

    Instant refMoment0 = Instant.now();
    Thread.sleep(3);
    create("Something happened", this.chrono.getId());
    create("Something else", this.chrono.getId());
    List<LogDTO> logs = getLogs();
    Assertions.assertThat(logs.size()).isEqualTo(2);

    Thread.sleep(3);
    Instant refMoment1 = Instant.now();
    Thread.sleep(3);

    create("Something more", this.chrono.getId());
    logs = getLogs();
    Assertions.assertThat(logs.size()).isEqualTo(3);

    Thread.sleep(3);
    Instant refMoment2 = Instant.now();

    logs = getLogs(refMoment0, refMoment1);
    Assertions.assertThat(logs.size()).isEqualTo(2);

    logs = getLogs(refMoment1, refMoment2);
    Assertions.assertThat(logs.size()).isEqualTo(1);

    logs = getLogs(refMoment0, refMoment2);
    Assertions.assertThat(logs.size()).isEqualTo(3);

  }

  @Test
  public void cantCreateOrFindLogsWithoutChrono() {
    create("This should fail", 123123L, NOT_FOUND);
    create("This should fail", -1L, BAD_REQUEST);
    getLogsWithoutChronoId();

  }

  /**
   * ******************** Reusable *********************
   **/


  public List<LogDTO> getLogs() {
    Response r = given().queryParam("chronoId", this.chrono.getId())
        .when().get("/rest/" + this.restEntrypointName)
        .then()
        .statusCode(OK)
        .extract().response();

    return jsonb.fromJson(r.asString(), this.dtoListClass);
  }

  public void getLogsWithoutChronoId() {
    given()
        .when().get("/rest/" + this.restEntrypointName)
        .then()
        .statusCode(BAD_REQUEST);
  }

  public List<LogDTO> getLogs(Instant from, Instant to) {
    Response r = given().queryParam("chronoId", this.chrono.getId()).queryParam("from", from.toString())
        .queryParam("to", to.toString())
        .when().get("/rest/" + this.restEntrypointName)
        .then()
        .statusCode(OK)
        .extract().response();

    return jsonb.fromJson(r.asString(), this.dtoListClass);
  }

  public List<LogDTO> getLogsInWindow(int page, int pageSize) {
    Response r = given().queryParam("page", page).queryParam("pageSize", pageSize).queryParam("chronoId", this.chrono.getId())
        .when().get("/rest/" + this.restEntrypointName)
        .then()
        .statusCode(OK)
        .extract().response();
    return jsonb.fromJson(r.asString(), this.dtoListClass);
  }

  public void create(String message, Long chronoId) {
    create(message, chronoId, NO_CONTENT);
  }

  public void create(String message, Long chronoId, int status) {
    LogDTO dto = createLogDTO(message);

    given().queryParam("chronoId", chronoId)
        .when().contentType(ContentType.JSON).body(jsonb.toJson(dto)).post("/rest/" + this.restEntrypointName + "/create")
        .then()
        .statusCode(status);

  }

  private LogDTO createLogDTO(String message) {
    LogDTO dto = new LogDTO();
    dto.getMeta().setCreated(Instant.now().toEpochMilli() / 1000.0);
    dto.setMessage(message);
    dto.setLog("Logger");
    dto.setLevel("Info");

    //Useless but could be present
    dto.getMeta().setFuncName("sendAll");
    dto.getMeta().setLine(27);
    dto.setDetails(new HashMap());
    dto.setPid("p-41699");
    dto.setTid("t-123145344335872");
    return dto;
  }

}
