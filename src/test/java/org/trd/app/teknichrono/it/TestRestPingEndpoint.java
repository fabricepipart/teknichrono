package org.trd.app.teknichrono.it;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.trd.app.teknichrono.model.dto.BeaconDTO;
import org.trd.app.teknichrono.model.dto.ChronometerDTO;
import org.trd.app.teknichrono.model.dto.NestedBeaconDTO;
import org.trd.app.teknichrono.model.dto.NestedChronometerDTO;
import org.trd.app.teknichrono.model.dto.PingDTO;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.*;

@Tag("integration")
@QuarkusTest
public class TestRestPingEndpoint extends TestRestEndpoint<PingDTO> {


  private TestRestBeaconEndpoint restBeacon = new TestRestBeaconEndpoint();
  private TestRestChronometerEndpoint restChronometer = new TestRestChronometerEndpoint();

  public TestRestPingEndpoint() {
    super("pings", PingDTO.class, new ArrayList<PingDTO>() {
      private static final long serialVersionUID = 312796608362747945L;
    }.getClass().getGenericSuperclass());
  }

  @AfterEach
  public void cleanup() {
    restChronometer.deleteAll();
    restBeacon.deleteAll();
    deleteAll();
  }

  /**
   * ******************** Tests *********************
   **/


  @Test
  public void cantCreateSamePingsTwice() {
    restBeacon.create(100);
    BeaconDTO beacon = restBeacon.getByNumber(100);
    restChronometer.create("C1");
    ChronometerDTO chronometer = restChronometer.getByName("C1");

    List<PingDTO> pings = getAll();
    assertThat(pings.size()).isEqualTo(0);
    Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    createPing(now, beacon.getId(), chronometer.getId());
    pings = getAll();
    assertThat(pings.size()).isEqualTo(1);
    createPing(now, beacon.getId(), chronometer.getId());
    pings = getAll();
    assertThat(pings.size()).isEqualTo(1);
  }

  @Test
  public void testLists() {
    restBeacon.create(100);
    BeaconDTO beacon = restBeacon.getByNumber(100);
    restChronometer.create("C1");
    ChronometerDTO chronometer = restChronometer.getByName("C1");

    List<PingDTO> pings = getAll();
    assertThat(pings.size()).isEqualTo(0);
    createPing(Instant.now(), beacon.getId(), chronometer.getId());
    pings = getAll();
    assertThat(pings.size()).isEqualTo(1);
    createPing(Instant.now(), beacon.getId(), chronometer.getId());
    pings = getAll();
    assertThat(pings.size()).isEqualTo(2);

    List<PingDTO> somePings = getAllInWindow(1, 1);
    assertThat(somePings.size()).isEqualTo(1);

    pings = getAll();
    for (PingDTO ping : pings) {
      delete(ping.getId());
    }
    pings = getAll();
    assertThat(pings.size()).isEqualTo(0);
  }

  @Test
  public void createWithEventThrowsErrorIfEventDoesNotExist() {
    createPing(Instant.now(), 666, 666, NOT_FOUND);
  }

  @Test
  public void latestFailsWith404IfChronometerDoesNotExist() {
    latestFails(987, NOT_FOUND);
  }

  @Test
  public void latestFailsWith404IfNoPing() {
    restChronometer.create("C1");
    ChronometerDTO chronometer = restChronometer.getByName("C1");
    latestFails(chronometer.getId(), NOT_FOUND);
  }

  @Test
  public void latestReturnsPingsOfLessThanOneDay() {
    restBeacon.create(100);
    BeaconDTO beacon = restBeacon.getByNumber(100);
    restChronometer.create("C1");
    ChronometerDTO chronometer = restChronometer.getByName("C1");

    List<PingDTO> pings = getAll();
    assertThat(pings.size()).isEqualTo(0);
    Instant fiveDaysAgo = Instant.now().truncatedTo(ChronoUnit.MILLIS).minus(5, ChronoUnit.DAYS);
    createPing(fiveDaysAgo, beacon.getId(), chronometer.getId());
    pings = getAll();
    assertThat(pings.size()).isEqualTo(1);

    latestFails(chronometer.getId(), NOT_FOUND);
  }

  @Test
  public void latestReturnsPingMostRecent() {
    restBeacon.create(100);
    BeaconDTO beacon = restBeacon.getByNumber(100);
    restChronometer.create("C1");
    ChronometerDTO chronometer = restChronometer.getByName("C1");

    List<PingDTO> pings = getAll();
    assertThat(pings.size()).isEqualTo(0);

    Instant twoMinutesAgo = Instant.now().truncatedTo(ChronoUnit.MILLIS).minus(2, ChronoUnit.MINUTES);
    createPing(twoMinutesAgo, beacon.getId(), chronometer.getId());

    Instant oneMinuteAgo = Instant.now().truncatedTo(ChronoUnit.MILLIS).minus(1, ChronoUnit.MINUTES);
    createPing(oneMinuteAgo, beacon.getId(), chronometer.getId());

    Instant threeMinutesAgo = Instant.now().truncatedTo(ChronoUnit.MILLIS).minus(3, ChronoUnit.MINUTES);
    createPing(threeMinutesAgo, beacon.getId(), chronometer.getId());

    pings = getAll();
    assertThat(pings.size()).isEqualTo(3);

    PingDTO latest = latest(chronometer.getId());
    assertThat(latest).isNotNull();
    assertThat(latest.getInstant()).isEqualTo(oneMinuteAgo);
  }


  @Test
  public void testCreateModifyDelete() {
    restBeacon.create(100);
    BeaconDTO beacon = restBeacon.getByNumber(100);
    restChronometer.create("C1");
    ChronometerDTO chronometer = restChronometer.getByName("C1");

    createPing(Instant.now(), beacon.getId(), chronometer.getId());

    PingDTO ping = getAll().iterator().next();
    assertThat(ping.getPower()).isEqualTo(0);
    assertThat(ping.getBeacon().getNumber()).isEqualTo(100);
    assertThat(ping.getChronometer().getName()).isEqualTo("C1");
    long id = ping.getId();
    getById(id);

    PingDTO modifiedPing = new PingDTO();
    Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    modifiedPing.setInstant(now);
    modifiedPing.setPower(-1);
    modifiedPing.setId(id);
    update(id, modifiedPing);

    List<PingDTO> pings = getAll();
    assertThat(pings.size()).isEqualTo(1);
    PingDTO newReturnedPing = pings.iterator().next();
    assertThat(newReturnedPing.getId()).isEqualTo(id);
    assertThat(newReturnedPing.getPower()).isEqualTo(-1);
    assertThat(newReturnedPing.getBeacon()).isNull();
    assertThat(newReturnedPing.getChronometer()).isNull();

  }

  @Test
  public void badRequestIfNoPingPassed() {
    createPings(null, BAD_REQUEST);
  }

  @Test
  public void badRequestIfPingDoesNotContainChronoOrBeacon() {
    List<PingDTO> pings = new ArrayList<>();
    pings.add(createDto(Instant.now()));
    createPings(pings, BAD_REQUEST);
  }

  @Test
  public void canCreatePingsInBatch() {
    List<PingDTO> pings = new ArrayList<>();

    restBeacon.create(100);
    BeaconDTO beacon = restBeacon.getByNumber(100);
    long beaconId = beacon.getId();
    restChronometer.create("C1");
    ChronometerDTO chronometer = restChronometer.getByName("C1");
    long chronoId = chronometer.getId();

    Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    pings.add(createDto(now.plusSeconds(10), beaconId, chronoId));
    pings.add(createDto(now.plusSeconds(20), beaconId, chronoId));
    pings.add(createDto(now.plusSeconds(30), beaconId, chronoId));

    List<PingDTO> allPings = getAll();
    assertThat(allPings.size()).isEqualTo(0);

    createPings(pings);

    allPings = getAll();
    assertThat(allPings.size()).isEqualTo(3);
  }

  /**
   * ******************** Reusable *********************
   **/

  public void createPing(Instant instant, long beaconId, long chronometerId) {
    createPing(instant, beaconId, chronometerId, NO_CONTENT);
  }

  public void createPing(Instant instant, long beaconId, long chronometerId, int statusCode) {
    Jsonb jsonb = JsonbBuilder.create();
    PingDTO dto = createDto(instant);
    given().queryParam("chronoId", chronometerId).queryParam("beaconId", beaconId)
        .when().contentType(ContentType.JSON).body(jsonb.toJson(dto)).post("/rest/pings/create")
        .then()
        .statusCode(statusCode);
  }

  public void latestFails(long chronometerId, int status){
    given().queryParam("chronoId", chronometerId)
        .when().get("/rest/pings/latest")
        .then()
        .statusCode(status);
  }

  public PingDTO latest(long chronometerId){
    Jsonb jsonb = JsonbBuilder.create();
    Response r = given().queryParam("chronoId", chronometerId)
        .when().get("/rest/pings/latest")
        .then()
        .statusCode(OK)
        .extract().response();
    return jsonb.fromJson(r.asString(), PingDTO.class);
  }

  public void createPings(List<PingDTO> dtos) {
    createPings(dtos, NO_CONTENT);
  }

  public void createPings(List<PingDTO> dtos, int statusCode) {
    Jsonb jsonb = JsonbBuilder.create();
    given()
        .when().contentType(ContentType.JSON).body(jsonb.toJson(dtos)).post("/rest/pings/create-multi")
        .then()
        .statusCode(statusCode);
  }

  private PingDTO createDto(Instant instant) {
    PingDTO dto = new PingDTO();
    dto.setInstant(instant);
    dto.setPower(0);
    return dto;
  }

  private PingDTO createDto(Instant instant, long beaconId, long chronometerId) {
    PingDTO dto = createDto(instant);
    NestedBeaconDTO beacon = new NestedBeaconDTO();
    beacon.setId(beaconId);
    dto.setBeacon(beacon);
    NestedChronometerDTO chrono = new NestedChronometerDTO();
    chrono.setId(chronometerId);
    dto.setChronometer(chrono);
    return dto;
  }

}
