package org.trd.app.teknichrono.it;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.trd.app.teknichrono.model.dto.BeaconDTO;
import org.trd.app.teknichrono.model.dto.CategoryDTO;
import org.trd.app.teknichrono.model.dto.ChronometerDTO;
import org.trd.app.teknichrono.model.dto.LapTimeDTO;
import org.trd.app.teknichrono.model.dto.LocationDTO;
import org.trd.app.teknichrono.model.dto.NestedPilotDTO;
import org.trd.app.teknichrono.model.dto.PingDTO;
import org.trd.app.teknichrono.model.dto.SessionDTO;
import org.trd.app.teknichrono.model.jpa.SessionType;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.*;

@Tag("integration")
@QuarkusTest
public class TestRestLapTimeEndpoint extends TestRestEndpoint<LapTimeDTO> {


  private TestRestBeaconEndpoint restBeacon = new TestRestBeaconEndpoint();
  private TestRestChronometerEndpoint restChronometer = new TestRestChronometerEndpoint();
  private TestRestPilotEndPoint restPilot = new TestRestPilotEndPoint();
  private TestRestSessionEndpoint restSession = new TestRestSessionEndpoint();
  private TestRestLocationEndpoint restLocation = new TestRestLocationEndpoint();
  private TestRestEventEndpoint restEvent = new TestRestEventEndpoint();
  private TestRestPingEndpoint restPing = new TestRestPingEndpoint();
  private TestRestCategoryEndpoint restCategory = new TestRestCategoryEndpoint();

  private List<BeaconDTO> beacons = new ArrayList<>();
  private List<Long> chronos = new ArrayList<>();
  private List<Long> locations = new ArrayList<>();
  private List<Long> events = new ArrayList<>();
  private List<SessionDTO> sessions = new ArrayList<>();
  private List<Long> pilots = new ArrayList<>();

  public TestRestLapTimeEndpoint() {
    super("laptimes", LapTimeDTO.class, new ArrayList<LapTimeDTO>() {
      private static final long serialVersionUID = -446912818292504311L;
    }.getClass().getGenericSuperclass());
  }

  @AfterEach
  public void cleanup() {
    if (sessions != null) {
      for (SessionDTO session : sessions) {
        deleteAllOfSession(session.getId());
      }
    }
    restPilot.deleteAll();
    restSession.deleteAll();
    restLocation.deleteAll();
    restBeacon.deleteAll();
    restChronometer.deleteAll();
    restPing.deleteAll();
    restCategory.deleteAll();
    restEvent.deleteAll();
  }


  /**
   * ******************** Tests *********************
   **/
  @Test
  public void testListsRequiresAtLeastSessionLocationOrEvent() {
    getAllFails();
  }

  @Test
  public void testLists() {
    createWithAllNeeded();
    addLap(Instant.now().plusSeconds(11), beacons.get(0).getId(), chronos.get(0));

    long sessionId = sessions.get(0).getId();
    List<LapTimeDTO> laptimes = getAllOfSession(sessionId);
    assertThat(laptimes.size()).isEqualTo(1);

    addLap(Instant.now().plusSeconds(23), beacons.get(0).getId(), chronos.get(0));
    laptimes = getAllOfSession(sessionId);
    assertThat(laptimes.size()).isEqualTo(2);

    List<LapTimeDTO> someLaptimes = getAllOfSessionInWindow(sessionId, 1, 1);
    assertThat(someLaptimes.size()).isEqualTo(1);

    laptimes = getAllOfSession(sessionId);
    for (LapTimeDTO laptime : laptimes) {
      delete(laptime.getId());
    }
    laptimes = getAllOfSession(sessionId);
    assertThat(laptimes.size()).isEqualTo(0);
  }

  @Test
  public void allLaptimesOfPilotInOrderOfOccurence() {
    addBeacon(101);
    BeaconDTO beacon1 = restBeacon.getByNumber(101);
    NestedPilotDTO pilot1 = beacon1.getPilot();
    addBeacon(102);
    BeaconDTO beacon2 = restBeacon.getByNumber(102);
    NestedPilotDTO pilot2 = beacon2.getPilot();
    createSession("Session", "C1");
    long sessionId = sessions.get(0).getId();
    Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    restPing.createPing(now.plusSeconds(0), beacons.get(0).getId(), chronos.get(0));
    restPing.createPing(now.plusSeconds(0), beacons.get(1).getId(), chronos.get(0));
    restPing.createPing(now.plusSeconds(101), beacons.get(0).getId(), chronos.get(0));
    restPing.createPing(now.plusSeconds(102), beacons.get(1).getId(), chronos.get(0));
    restPing.createPing(now.plusSeconds(212), beacons.get(0).getId(), chronos.get(0));
    restPing.createPing(now.plusSeconds(214), beacons.get(1).getId(), chronos.get(0));
    restPing.createPing(now.plusSeconds(336), beacons.get(1).getId(), chronos.get(0));

    List<LapTimeDTO> laptimes = getAllOfSession(sessionId);
    assertThat(laptimes.size()).isEqualTo(5);

    laptimes = getAllOfPilot(pilot1.getId(), sessionId);
    assertThat(laptimes.size()).isEqualTo(2);
    assertThat(laptimes.stream().allMatch(lap -> lap.getPilot().getId().equals(pilot1.getId()))).isTrue();
    assertThat(laptimes.get(0).getDuration()).isEqualTo(Duration.ofSeconds(101));
    assertThat(laptimes.get(1).getDuration()).isEqualTo(Duration.ofSeconds(111));

    laptimes = getAllOfPilot(pilot2.getId(), sessionId);
    assertThat(laptimes.size()).isEqualTo(3);
    assertThat(laptimes.stream().allMatch(lap -> lap.getPilot().getId().equals(pilot2.getId()))).isTrue();
    assertThat(laptimes.get(0).getDuration()).isEqualTo(Duration.ofSeconds(102));
    assertThat(laptimes.get(1).getDuration()).isEqualTo(Duration.ofSeconds(112));
    assertThat(laptimes.get(2).getDuration()).isEqualTo(Duration.ofSeconds(122));
  }

  @Test
  public void allLaptimesOfCategoryInOrderOfOccurence() {
    addBeacon(101);
    BeaconDTO beacon1 = restBeacon.getByNumber(101);
    NestedPilotDTO pilot1 = beacon1.getPilot();
    restCategory.create("One");
    CategoryDTO one = restCategory.getByName("One");
    restCategory.addPilot(one.getId(), pilot1.getId());

    addBeacon(102);
    BeaconDTO beacon2 = restBeacon.getByNumber(102);
    NestedPilotDTO pilot2 = beacon2.getPilot();
    restCategory.create("Two");
    CategoryDTO two = restCategory.getByName("Two");
    restCategory.addPilot(two.getId(), pilot2.getId());

    createSession("Session", "C1");
    long sessionId = sessions.get(0).getId();
    Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    restPing.createPing(now.plusSeconds(336), beacons.get(1).getId(), chronos.get(0));
    restPing.createPing(now.plusSeconds(0), beacons.get(0).getId(), chronos.get(0));
    restPing.createPing(now.plusSeconds(102), beacons.get(1).getId(), chronos.get(0));
    restPing.createPing(now.plusSeconds(212), beacons.get(0).getId(), chronos.get(0));
    restPing.createPing(now.plusSeconds(214), beacons.get(1).getId(), chronos.get(0));
    restPing.createPing(now.plusSeconds(0), beacons.get(1).getId(), chronos.get(0));
    restPing.createPing(now.plusSeconds(101), beacons.get(0).getId(), chronos.get(0));

    List<LapTimeDTO> laptimes = getAllOfSession(sessionId);
    assertThat(laptimes.size()).isEqualTo(5);

    laptimes = getAllOfCategory(one.getId(), sessionId);
    assertThat(laptimes.size()).isEqualTo(2);
    assertThat(laptimes.stream().allMatch(lap -> lap.getPilot().getId().equals(pilot1.getId()))).isTrue();
    assertThat(laptimes.get(0).getDuration()).isEqualTo(Duration.ofSeconds(101));
    assertThat(laptimes.get(1).getDuration()).isEqualTo(Duration.ofSeconds(111));

    laptimes = getAllOfCategory(two.getId(), sessionId);
    assertThat(laptimes.size()).isEqualTo(3);
    assertThat(laptimes.stream().allMatch(lap -> lap.getPilot().getId().equals(pilot2.getId()))).isTrue();
    assertThat(laptimes.get(0).getDuration()).isEqualTo(Duration.ofSeconds(102));
    assertThat(laptimes.get(1).getDuration()).isEqualTo(Duration.ofSeconds(112));
    assertThat(laptimes.get(2).getDuration()).isEqualTo(Duration.ofSeconds(122));
  }

  @Test
  public void allLaptimesOfPilotAndLocationInOrderOfOccurence() {
    addBeacon(101);
    BeaconDTO beacon1 = restBeacon.getByNumber(101);
    NestedPilotDTO pilot1 = beacon1.getPilot();
    addBeacon(102);
    BeaconDTO beacon2 = restBeacon.getByNumber(102);
    NestedPilotDTO pilot2 = beacon2.getPilot();
    createSession("Session", "C1");

    Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    restPing.createPing(now.plusSeconds(214), beacons.get(1).getId(), chronos.get(0));
    restPing.createPing(now.plusSeconds(336), beacons.get(1).getId(), chronos.get(0));
    restPing.createPing(now.plusSeconds(212), beacons.get(0).getId(), chronos.get(0));
    restPing.createPing(now.plusSeconds(101), beacons.get(0).getId(), chronos.get(0));
    restPing.createPing(now.plusSeconds(102), beacons.get(1).getId(), chronos.get(0));
    restPing.createPing(now.plusSeconds(0), beacons.get(0).getId(), chronos.get(0));
    restPing.createPing(now.plusSeconds(0), beacons.get(1).getId(), chronos.get(0));

    List<LapTimeDTO> laptimes = getAllOfLocation(locations.get(0));
    assertThat(laptimes.size()).isEqualTo(5);

    laptimes = getAllOfPilotAndLocation(pilot1.getId(), locations.get(0));
    assertThat(laptimes.size()).isEqualTo(2);
    assertThat(laptimes.stream().allMatch(lap -> lap.getPilot().getId().equals(pilot1.getId()))).isTrue();
    assertThat(laptimes.get(0).getDuration()).isEqualTo(Duration.ofSeconds(101));
    assertThat(laptimes.get(1).getDuration()).isEqualTo(Duration.ofSeconds(111));

    laptimes = getAllOfPilotAndLocation(pilot2.getId(), locations.get(0));
    assertThat(laptimes.size()).isEqualTo(3);
    assertThat(laptimes.stream().allMatch(lap -> lap.getPilot().getId().equals(pilot2.getId()))).isTrue();
    assertThat(laptimes.get(0).getDuration()).isEqualTo(Duration.ofSeconds(102));
    assertThat(laptimes.get(1).getDuration()).isEqualTo(Duration.ofSeconds(112));
    assertThat(laptimes.get(2).getDuration()).isEqualTo(Duration.ofSeconds(122));
  }

  @Test
  public void allLaptimesOfPilotAndEventInOrderOfOccurence() {
    addBeacon(101);
    BeaconDTO beacon1 = restBeacon.getByNumber(101);
    NestedPilotDTO pilot1 = beacon1.getPilot();
    addBeacon(102);
    BeaconDTO beacon2 = restBeacon.getByNumber(102);
    NestedPilotDTO pilot2 = beacon2.getPilot();
    createSession("Session", "C1");

    Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    restPing.createPing(now.plusSeconds(336), beacons.get(1).getId(), chronos.get(0));
    restPing.createPing(now.plusSeconds(214), beacons.get(1).getId(), chronos.get(0));
    restPing.createPing(now.plusSeconds(0), beacons.get(0).getId(), chronos.get(0));
    restPing.createPing(now.plusSeconds(212), beacons.get(0).getId(), chronos.get(0));
    restPing.createPing(now.plusSeconds(101), beacons.get(0).getId(), chronos.get(0));
    restPing.createPing(now.plusSeconds(0), beacons.get(1).getId(), chronos.get(0));
    restPing.createPing(now.plusSeconds(102), beacons.get(1).getId(), chronos.get(0));

    List<LapTimeDTO> laptimes = getAllOfEvent(events.get(0));
    assertThat(laptimes.size()).isEqualTo(5);

    laptimes = getAllOfPilotAndEvent(pilot1.getId(), events.get(0));
    assertThat(laptimes.size()).isEqualTo(2);
    assertThat(laptimes.stream().allMatch(lap -> lap.getPilot().getId().equals(pilot1.getId()))).isTrue();
    assertThat(laptimes.get(0).getDuration()).isEqualTo(Duration.ofSeconds(101));
    assertThat(laptimes.get(1).getDuration()).isEqualTo(Duration.ofSeconds(111));

    laptimes = getAllOfPilotAndEvent(pilot2.getId(), events.get(0));
    assertThat(laptimes.size()).isEqualTo(3);
    assertThat(laptimes.stream().allMatch(lap -> lap.getPilot().getId().equals(pilot2.getId()))).isTrue();
    assertThat(laptimes.get(0).getDuration()).isEqualTo(Duration.ofSeconds(102));
    assertThat(laptimes.get(1).getDuration()).isEqualTo(Duration.ofSeconds(112));
    assertThat(laptimes.get(2).getDuration()).isEqualTo(Duration.ofSeconds(122));
  }

  @Test
  public void allLaptimesWithIntermediatesOfPilotInOrderOfOccurence() {
    long sessionId = createTwoPilotsTimeTrial();

    List<LapTimeDTO> laptimes = getAllOfSession(sessionId);
    assertThat(laptimes.size()).isEqualTo(5);

    laptimes = getAllOfPilot(pilots.get(0), sessionId);
    assertThat(laptimes.size()).isEqualTo(3);
    assertThat(laptimes.stream().allMatch(lap -> lap.getPilot().getId().equals(pilots.get(0)))).isTrue();

    laptimes = getAllOfPilot(pilots.get(1), sessionId);
    assertThat(laptimes.size()).isEqualTo(2);
    assertThat(laptimes.stream().allMatch(lap -> lap.getPilot().getId().equals(pilots.get(1)))).isTrue();
  }


  @Test
  public void allLapsOfRace() {
    long sessionId = createThreePilotsRace();

    List<LapTimeDTO> laptimes = getAllOfSession(sessionId);
    assertThat(laptimes.size()).isEqualTo(7);

    laptimes = getAllOfPilot(pilots.get(0), sessionId);
    assertThat(laptimes.size()).isEqualTo(3);
    assertThat(laptimes.stream().allMatch(lap -> lap.getPilot().getId().equals(pilots.get(0)))).isTrue();

    laptimes = getAllOfPilot(pilots.get(1), sessionId);
    assertThat(laptimes.size()).isEqualTo(3);
    assertThat(laptimes.stream().allMatch(lap -> lap.getPilot().getId().equals(pilots.get(1)))).isTrue();

    laptimes = getAllOfPilot(pilots.get(2), sessionId);
    assertThat(laptimes.size()).isEqualTo(1);
    assertThat(laptimes.stream().allMatch(lap -> lap.getPilot().getId().equals(pilots.get(2)))).isTrue();
    assertThat(laptimes.get(0).getDuration()).isEqualTo(Duration.ofSeconds(102));
    assertThat(laptimes.get(0).getLapIndex()).isEqualTo(1);
    assertThat(laptimes.get(0).getLapNumber()).isEqualTo(1);
  }


  @Test
  public void bestLapTimesOfPilotReturnsThemInOrder() {
    createTwoPilotsTimeTrial();

    List<LapTimeDTO> laptimes = getBestOfPilot(pilots.get(0), sessions.get(0).getId());
    assertThat(laptimes.size()).isEqualTo(3);
    assertThat(laptimes.stream().allMatch(lap -> lap.getPilot().getId().equals(pilots.get(0)))).isTrue();
    Duration maxFoundUntilNow = Duration.ZERO;
    for (LapTimeDTO laptime : laptimes) {
      assertThat(laptime.getDuration().compareTo(maxFoundUntilNow)).isGreaterThanOrEqualTo(0);
    }

    laptimes = getBestOfPilot(pilots.get(1), sessions.get(0).getId());
    assertThat(laptimes.size()).isEqualTo(2);
    assertThat(laptimes.stream().allMatch(lap -> lap.getPilot().getId().equals(pilots.get(1)))).isTrue();
    maxFoundUntilNow = Duration.ZERO;
    for (LapTimeDTO laptime : laptimes) {
      assertThat(laptime.getDuration().compareTo(maxFoundUntilNow)).isGreaterThanOrEqualTo(0);
    }


  }

  @Test
  public void bestRaceLapTimesOfPilotReturnsThemInOrder() {
    createThreePilotsRace();

    List<LapTimeDTO> laptimes = getBestOfPilot(pilots.get(0), sessions.get(0).getId());
    assertThat(laptimes.size()).isEqualTo(3);
    assertThat(laptimes.stream().allMatch(lap -> lap.getPilot().getId().equals(pilots.get(0)))).isTrue();
    Duration maxFoundUntilNow = Duration.ZERO;
    for (LapTimeDTO laptime : laptimes) {
      assertThat(laptime.getDuration().compareTo(maxFoundUntilNow)).isGreaterThanOrEqualTo(0);
    }

    laptimes = getBestOfPilot(pilots.get(1), sessions.get(0).getId());
    assertThat(laptimes.size()).isEqualTo(3);
    assertThat(laptimes.stream().allMatch(lap -> lap.getPilot().getId().equals(pilots.get(1)))).isTrue();
    maxFoundUntilNow = Duration.ZERO;
    for (LapTimeDTO laptime : laptimes) {
      assertThat(laptime.getDuration().compareTo(maxFoundUntilNow)).isGreaterThanOrEqualTo(0);
    }

    laptimes = getBestOfPilot(pilots.get(2), sessions.get(0).getId());
    assertThat(laptimes.size()).isEqualTo(1);
    assertThat(laptimes.stream().allMatch(lap -> lap.getPilot().getId().equals(pilots.get(2)))).isTrue();
    maxFoundUntilNow = Duration.ZERO;
    for (LapTimeDTO laptime : laptimes) {
      assertThat(laptime.getDuration().compareTo(maxFoundUntilNow)).isGreaterThanOrEqualTo(0);
    }

  }

  @Test
  public void bestLapTimesReturnsBestPerPilotInOrder() {
    createTwoPilotsTimeTrial();

    List<LapTimeDTO> laptimes = getBestOfSession(sessions.get(0).getId());
    assertThat(laptimes.size()).isEqualTo(2);
    Duration maxFoundUntilNow = Duration.ZERO;
    for (LapTimeDTO laptime : laptimes) {
      assertThat(laptime.getDuration().compareTo(maxFoundUntilNow)).isGreaterThanOrEqualTo(0);
    }
  }

  @Test
  public void bestRaceLapTimesReturnsBestPerPilotInOrder() {
    createThreePilotsRace();

    List<LapTimeDTO> laptimes = getBestOfSession(sessions.get(0).getId());
    assertThat(laptimes.size()).isEqualTo(3);
    Duration maxFoundUntilNow = Duration.ZERO;
    for (LapTimeDTO laptime : laptimes) {
      assertThat(laptime.getDuration().compareTo(maxFoundUntilNow)).isGreaterThanOrEqualTo(0);
    }
  }

  @Test
  public void bestsRequiresAtLeastSessionLocationOrEvent() {
    getBestFails();
  }


  @Test
  public void resultsOfPilotReturnsAll() {
    createTwoPilotsTimeTrial();

    List<LapTimeDTO> laptimes = getResultsOfPilot(pilots.get(0), sessions.get(0).getId());
    assertThat(laptimes.size()).isEqualTo(3);
    assertThat(laptimes.stream().allMatch(lap -> lap.getPilot().getId().equals(pilots.get(0)))).isTrue();
    Duration maxFoundUntilNow = Duration.ZERO;
    for (LapTimeDTO laptime : laptimes) {
      assertThat(laptime.getDuration().compareTo(maxFoundUntilNow)).isGreaterThanOrEqualTo(0);
    }

    laptimes = getResultsOfPilot(pilots.get(1), sessions.get(0).getId());
    assertThat(laptimes.size()).isEqualTo(2);
    assertThat(laptimes.stream().allMatch(lap -> lap.getPilot().getId().equals(pilots.get(1)))).isTrue();
    maxFoundUntilNow = Duration.ZERO;
    for (LapTimeDTO laptime : laptimes) {
      assertThat(laptime.getDuration().compareTo(maxFoundUntilNow)).isGreaterThanOrEqualTo(0);
    }

  }

  @Test
  public void raceResultsOfPilotReturnsSomething() {
    createThreePilotsRace();

    List<LapTimeDTO> laptimes = getResultsOfPilot(pilots.get(0), sessions.get(0).getId());
    assertThat(laptimes.size()).isEqualTo(3);
    assertThat(laptimes.stream().allMatch(lap -> lap.getPilot().getId().equals(pilots.get(0)))).isTrue();
    Duration maxFoundUntilNow = Duration.ZERO;
    for (LapTimeDTO laptime : laptimes) {
      assertThat(laptime.getDuration().compareTo(maxFoundUntilNow)).isGreaterThanOrEqualTo(0);
    }

    laptimes = getResultsOfPilot(pilots.get(1), sessions.get(0).getId());
    assertThat(laptimes.size()).isEqualTo(3);
    assertThat(laptimes.stream().allMatch(lap -> lap.getPilot().getId().equals(pilots.get(1)))).isTrue();
    maxFoundUntilNow = Duration.ZERO;
    for (LapTimeDTO laptime : laptimes) {
      assertThat(laptime.getDuration().compareTo(maxFoundUntilNow)).isGreaterThanOrEqualTo(0);
    }

    laptimes = getResultsOfPilot(pilots.get(2), sessions.get(0).getId());
    assertThat(laptimes.size()).isEqualTo(1);
    assertThat(laptimes.stream().allMatch(lap -> lap.getPilot().getId().equals(pilots.get(2)))).isTrue();
    maxFoundUntilNow = Duration.ZERO;
    for (LapTimeDTO laptime : laptimes) {
      assertThat(laptime.getDuration().compareTo(maxFoundUntilNow)).isGreaterThanOrEqualTo(0);
    }
  }

  @Test
  public void resultsReturnsBestPerPilotInOrder() {
    createTwoPilotsTimeTrial();

    List<LapTimeDTO> laptimes = getResultsOfSession(sessions.get(0).getId());
    assertThat(laptimes.size()).isEqualTo(2);
    Duration maxFoundUntilNow = Duration.ZERO;
    for (LapTimeDTO laptime : laptimes) {
      assertThat(laptime.getDuration().compareTo(maxFoundUntilNow)).isGreaterThanOrEqualTo(0);
    }

  }

  @Test
  public void raceResults() {
    createThreePilotsRace();

    List<LapTimeDTO> laptimes = getResultsOfSession(sessions.get(0).getId());
    assertThat(laptimes.size()).isEqualTo(3);

    assertThat(laptimes.get(0).getPilot().getId()).isEqualTo(pilots.get(0));
    assertThat(laptimes.get(0).getLapIndex()).isEqualTo(3);
    assertThat(laptimes.get(0).getLapNumber()).isEqualTo(3);
    assertThat(laptimes.get(0).getGapWithBest()).isEqualTo(Duration.ZERO);
    assertThat(laptimes.get(0).getGapWithPrevious()).isEqualTo(Duration.ZERO);


    assertThat(laptimes.get(1).getPilot().getId()).isEqualTo(pilots.get(1));
    assertThat(laptimes.get(1).getLapIndex()).isEqualTo(3);
    assertThat(laptimes.get(1).getLapNumber()).isEqualTo(3);
    assertThat(laptimes.get(1).getGapWithBest()).isGreaterThan(Duration.ZERO);
    assertThat(laptimes.get(1).getGapWithPrevious()).isGreaterThan(Duration.ZERO);


    assertThat(laptimes.get(2).getPilot().getId()).isEqualTo(pilots.get(2));
    assertThat(laptimes.get(2).getLapIndex()).isEqualTo(1);
    assertThat(laptimes.get(2).getLapNumber()).isEqualTo(1);

  }

  @Test
  public void raceResultsOfOnGoingRace() {
    // Makes it considered 4th lap of an unfinished race
    createThreePilotsRace(false);

    List<LapTimeDTO> laptimes = getResultsOfSession(sessions.get(0).getId());
    assertThat(laptimes.size()).isEqualTo(3);

    assertThat(laptimes.get(0).getPilot().getId()).isEqualTo(pilots.get(0));
    assertThat(laptimes.get(0).getLapIndex()).isEqualTo(4);
    assertThat(laptimes.get(0).getLapNumber()).isEqualTo(4);
    assertThat(laptimes.get(0).getGapWithBest()).isEqualTo(Duration.ZERO);
    assertThat(laptimes.get(0).getGapWithPrevious()).isEqualTo(Duration.ZERO);

    assertThat(laptimes.get(1).getPilot().getId()).isEqualTo(pilots.get(1));
    assertThat(laptimes.get(1).getLapIndex()).isEqualTo(4);
    assertThat(laptimes.get(1).getLapNumber()).isEqualTo(4);
    assertThat(laptimes.get(1).getGapWithBest()).isGreaterThan(Duration.ZERO);
    assertThat(laptimes.get(1).getGapWithPrevious()).isGreaterThan(Duration.ZERO);

    assertThat(laptimes.get(2).getPilot().getId()).isEqualTo(pilots.get(2));
    assertThat(laptimes.get(2).getLapIndex()).isEqualTo(2);
    assertThat(laptimes.get(2).getLapNumber()).isEqualTo(2);
  }

  @Test
  public void resultsRequiresSession() {
    getResultsFails();
  }


  /**
   * ******************** Reusable *********************
   **/


  public void getAllFails() {
    given()
        .when().get("/rest/laptimes")
        .then()
        .statusCode(BAD_REQUEST);
  }

  public void getResultsFails() {
    given()
        .when().get("/rest/laptimes/results")
        .then()
        .statusCode(BAD_REQUEST);
  }

  public void getBestFails() {
    given()
        .when().get("/rest/laptimes/best")
        .then()
        .statusCode(BAD_REQUEST);
  }

  private void addChronometer(String name, int sessionIndex) {
    restChronometer.create(name);
    ChronometerDTO chronometer = restChronometer.getByName(name);
    chronos.add(chronometer.getId());
    restSession.addChronometer(sessions.get(sessionIndex).getId(), chronometer.getId());
  }

  public List<LapTimeDTO> getAllOfSession(long sessionId) {
    Jsonb jsonb = JsonbBuilder.create();
    Response r = given().queryParam("sessionId", sessionId)
        .when().get("/rest/laptimes")
        .then()
        .statusCode(OK)
        .extract().response();
    return jsonb.fromJson(r.asString(), dtoListClass);
  }

  public List<LapTimeDTO> getBestOfSession(long sessionId) {
    Jsonb jsonb = JsonbBuilder.create();
    Response r = given().queryParam("sessionId", sessionId)
        .when().get("/rest/laptimes/best")
        .then()
        .statusCode(OK)
        .extract().response();
    return jsonb.fromJson(r.asString(), dtoListClass);
  }

  public List<LapTimeDTO> getResultsOfSession(long sessionId) {
    Jsonb jsonb = JsonbBuilder.create();
    Response r = given().queryParam("sessionId", sessionId)
        .when().get("/rest/laptimes/results")
        .then()
        .statusCode(OK)
        .extract().response();
    return jsonb.fromJson(r.asString(), dtoListClass);
  }

  public List<LapTimeDTO> getAllOfLocation(long locationId) {
    Jsonb jsonb = JsonbBuilder.create();
    Response r = given().queryParam("locationId", locationId)
        .when().get("/rest/laptimes")
        .then()
        .statusCode(OK)
        .extract().response();
    return jsonb.fromJson(r.asString(), dtoListClass);
  }

  public List<LapTimeDTO> getAllOfEvent(long eventId) {
    Jsonb jsonb = JsonbBuilder.create();
    Response r = given().queryParam("eventId", eventId)
        .when().get("/rest/laptimes")
        .then()
        .statusCode(OK)
        .extract().response();
    return jsonb.fromJson(r.asString(), dtoListClass);
  }

  public List<LapTimeDTO> getAllOfPilot(long pilotId, long sessionId) {
    Jsonb jsonb = JsonbBuilder.create();
    Response r = given().queryParam("pilotId", pilotId).queryParam("sessionId", sessionId)
        .when().get("/rest/laptimes")
        .then()
        .statusCode(OK)
        .extract().response();
    return jsonb.fromJson(r.asString(), dtoListClass);
  }

  public List<LapTimeDTO> getBestOfPilot(long pilotId, long sessionId) {
    Jsonb jsonb = JsonbBuilder.create();
    Response r = given().queryParam("pilotId", pilotId).queryParam("sessionId", sessionId)
        .when().get("/rest/laptimes/best")
        .then()
        .statusCode(OK)
        .extract().response();
    return jsonb.fromJson(r.asString(), dtoListClass);
  }

  public List<LapTimeDTO> getResultsOfPilot(long pilotId, long sessionId) {
    Jsonb jsonb = JsonbBuilder.create();
    Response r = given().queryParam("pilotId", pilotId).queryParam("sessionId", sessionId)
        .when().get("/rest/laptimes/results")
        .then()
        .statusCode(OK)
        .extract().response();
    return jsonb.fromJson(r.asString(), dtoListClass);
  }

  public List<LapTimeDTO> getAllOfPilotAndLocation(long pilotId, long locationId) {
    Jsonb jsonb = JsonbBuilder.create();
    Response r = given().queryParam("pilotId", pilotId).queryParam("locationId", locationId)
        .when().get("/rest/laptimes")
        .then()
        .statusCode(OK)
        .extract().response();
    return jsonb.fromJson(r.asString(), dtoListClass);
  }

  public List<LapTimeDTO> getAllOfPilotAndEvent(long pilotId, long eventId) {
    Jsonb jsonb = JsonbBuilder.create();
    Response r = given().queryParam("pilotId", pilotId).queryParam("eventId", eventId)
        .when().get("/rest/laptimes")
        .then()
        .statusCode(OK)
        .extract().response();
    return jsonb.fromJson(r.asString(), dtoListClass);
  }

  public List<LapTimeDTO> getAllOfCategory(long categoryId, long sessionId) {
    Jsonb jsonb = JsonbBuilder.create();
    Response r = given().queryParam("categoryId", categoryId).queryParam("sessionId", sessionId)
        .when().get("/rest/laptimes")
        .then()
        .statusCode(OK)
        .extract().response();
    return jsonb.fromJson(r.asString(), dtoListClass);
  }

  public List<LapTimeDTO> getAllOfSessionInWindow(long sessionId, int page, int pageSize) {
    Jsonb jsonb = JsonbBuilder.create();
    Response r = given().queryParam("sessionId", sessionId).queryParam("page", page).queryParam("pageSize", pageSize)
        .when().get("/rest/" + restEntrypointName)
        .then()
        .statusCode(OK)
        .extract().response();
    return jsonb.fromJson(r.asString(), dtoListClass);
  }

  public void deleteAllOfSession(long sessionId) {
    List<LapTimeDTO> all = getAllOfSession(sessionId);
    for (LapTimeDTO d : all) {
      delete(d.getId());
    }
  }

  public void createWithAllNeeded() {
    addBeacon(101);
    createSession("Session", "C1");
    Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    restPing.createPing(now.plusSeconds(0), beacons.get(0).getId(), chronos.get(0));
  }

  public void addLap(Instant instant, long beaconId, long chronoId) {
    restPing.createPing(instant, beaconId, chronoId);
  }

  private void createSession(String sessionName, String chronoName) {
    restSession.createWith(sessionName, null, null, chronoName, "L1", "E1");
    chronos.add(restChronometer.getByName("C1").getId());
    events.add(restEvent.getByName("E1").getId());
    locations.add(restLocation.getByName("L1").getId());
    sessions.add(restSession.getByName(sessionName));
  }

  private void addBeacon(int beaconNumber) {
    restPilot.createWithBeacon("Pilot", "" + beaconNumber, beaconNumber);
    beacons.add(restBeacon.getByNumber(beaconNumber));
  }

  private long createThreePilotsRace() {
    return createThreePilotsRace(true);
  }

  private long createThreePilotsRace(boolean endIt) {
    addBeacon(101);
    BeaconDTO beacon1 = restBeacon.getByNumber(101);
    NestedPilotDTO pilot1 = beacon1.getPilot();
    pilots.add(pilot1.getId());

    addBeacon(102);
    BeaconDTO beacon2 = restBeacon.getByNumber(102);
    NestedPilotDTO pilot2 = beacon2.getPilot();
    pilots.add(pilot2.getId());

    addBeacon(103);
    BeaconDTO beacon3 = restBeacon.getByNumber(103);
    NestedPilotDTO pilot3 = beacon3.getPilot();
    pilots.add(pilot3.getId());

    createSession("Session", "C1");
    long sessionId = sessions.get(0).getId();
    sessions.get(0).setInactivity(40000L);
    sessions.get(0).setType(SessionType.RACE.getIdentifier());
    addChronometer("C2", 0);
    addChronometer("C3", 0);
    restSession.update(sessionId, sessions.get(0));
    LocationDTO location = restLocation.getById(locations.get(0));
    location.setLoopTrack(true);
    restLocation.update(locations.get(0), location);

    Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    PingDTO startPing = new PingDTO();
    startPing.setInstant(now);
    startPing.setPower(0);
    restSession.addPilot(sessionId, pilot1.getId());
    restSession.addPilot(sessionId, pilot2.getId());
    restSession.addPilot(sessionId, pilot3.getId());
    restSession.start(startPing, sessionId);

    // Lap 1 starts
    // Ignored because of inactivity
    restPing.createPing(now.plusSeconds(3), beacons.get(1).getId(), chronos.get(0));
    restPing.createPing(now.plusSeconds(4), beacons.get(2).getId(), chronos.get(0));
    // First intermediate in order
    restPing.createPing(now.plusSeconds(30), beacons.get(0).getId(), chronos.get(1));
    restPing.createPing(now.plusSeconds(31), beacons.get(1).getId(), chronos.get(1));
    restPing.createPing(now.plusSeconds(32), beacons.get(2).getId(), chronos.get(1));
    // Second intermediate in other order
    restPing.createPing(now.plusSeconds(62), beacons.get(0).getId(), chronos.get(2));
    restPing.createPing(now.plusSeconds(61), beacons.get(1).getId(), chronos.get(2));
    restPing.createPing(now.plusSeconds(60), beacons.get(2).getId(), chronos.get(2));
    // Lap 2 starts
    // Received in wrong order
    restPing.createPing(now.plusSeconds(102), beacons.get(2).getId(), chronos.get(0));
    restPing.createPing(now.plusSeconds(101), beacons.get(1).getId(), chronos.get(0));
    restPing.createPing(now.plusSeconds(100), beacons.get(0).getId(), chronos.get(0));
    // Third does not finish - falls during lap
    restPing.createPing(now.plusSeconds(130), beacons.get(0).getId(), chronos.get(1));
    restPing.createPing(now.plusSeconds(131), beacons.get(1).getId(), chronos.get(1));
    // Last intermediate of lap 2 received late
    // Last lap
    restPing.createPing(now.plusSeconds(200), beacons.get(0).getId(), chronos.get(0));
    restPing.createPing(now.plusSeconds(201), beacons.get(1).getId(), chronos.get(0));
    restPing.createPing(now.plusSeconds(230), beacons.get(1).getId(), chronos.get(1));
    restPing.createPing(now.plusSeconds(231), beacons.get(0).getId(), chronos.get(1));
    restPing.createPing(now.plusSeconds(260), beacons.get(0).getId(), chronos.get(2));
    restPing.createPing(now.plusSeconds(261), beacons.get(1).getId(), chronos.get(2));
    // Last intermediate of lap 2 received late
    restPing.createPing(now.plusSeconds(160), beacons.get(0).getId(), chronos.get(2));
    restPing.createPing(now.plusSeconds(161), beacons.get(1).getId(), chronos.get(2));
    // Arrival
    restPing.createPing(now.plusSeconds(300), beacons.get(0).getId(), chronos.get(0));
    restPing.createPing(now.plusSeconds(301), beacons.get(1).getId(), chronos.get(0));

    if (endIt) {
      PingDTO endPing = new PingDTO();
      endPing.setInstant(now.plusSeconds(305));
      endPing.setPower(0);
      restSession.end(endPing, sessionId);
    }
    return sessionId;
  }

  private long createTwoPilotsTimeTrial() {
    addBeacon(101);
    BeaconDTO beacon1 = restBeacon.getByNumber(101);
    NestedPilotDTO pilot1 = beacon1.getPilot();
    pilots.add(pilot1.getId());
    addBeacon(102);
    BeaconDTO beacon2 = restBeacon.getByNumber(102);
    NestedPilotDTO pilot2 = beacon2.getPilot();
    pilots.add(pilot2.getId());
    createSession("Session", "C1");
    long sessionId = sessions.get(0).getId();
    addChronometer("C2", 0);
    addChronometer("C3", 0);

    Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    restPing.createPing(now.plusSeconds(0), beacons.get(0).getId(), chronos.get(0));
    restPing.createPing(now.plusSeconds(30), beacons.get(0).getId(), chronos.get(1));
    restPing.createPing(now.plusSeconds(60), beacons.get(0).getId(), chronos.get(2));

    restPing.createPing(now.plusSeconds(62), beacons.get(1).getId(), chronos.get(2));
    restPing.createPing(now.plusSeconds(31), beacons.get(1).getId(), chronos.get(1));
    restPing.createPing(now.plusSeconds(0), beacons.get(1).getId(), chronos.get(0));

    restPing.createPing(now.plusSeconds(100), beacons.get(0).getId(), chronos.get(0));
    restPing.createPing(now.plusSeconds(130), beacons.get(0).getId(), chronos.get(1));
    restPing.createPing(now.plusSeconds(160), beacons.get(0).getId(), chronos.get(2));

    restPing.createPing(now.plusSeconds(200), beacons.get(0).getId(), chronos.get(0));
    restPing.createPing(now.plusSeconds(230), beacons.get(0).getId(), chronos.get(1));
    restPing.createPing(now.plusSeconds(260), beacons.get(0).getId(), chronos.get(2));

    restPing.createPing(now.plusSeconds(202), beacons.get(1).getId(), chronos.get(0));
    restPing.createPing(now.plusSeconds(231), beacons.get(1).getId(), chronos.get(1));

    restPing.createPing(now.plusSeconds(300), beacons.get(0).getId(), chronos.get(0));

    restPing.createPing(now.plusSeconds(102), beacons.get(1).getId(), chronos.get(0));
    restPing.createPing(now.plusSeconds(132), beacons.get(1).getId(), chronos.get(1));
    restPing.createPing(now.plusSeconds(162), beacons.get(1).getId(), chronos.get(2));
    return sessionId;
  }


}
