package org.trd.app.teknichrono.it;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
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
import org.trd.app.teknichrono.model.jpa.Chronometer;
import org.trd.app.teknichrono.model.jpa.SessionType;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

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
  private List<LocationDTO> locations = new ArrayList<>();
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
    if (this.sessions != null) {
      for (SessionDTO session : this.sessions) {
        deleteAllOfSession(session.getId());
      }
    }
    this.restPilot.deleteAll();
    this.restSession.deleteAll();
    this.restLocation.deleteAll();
    this.restBeacon.deleteAll();
    this.restChronometer.deleteAll();
    this.restPing.deleteAll();
    this.restCategory.deleteAll();
    this.restEvent.deleteAll();
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
    addLap(Instant.now().plusSeconds(11), this.beacons.get(0).getId(), this.chronos.get(0));

    long sessionId = this.sessions.get(0).getId();
    List<LapTimeDTO> laptimes = getAllOfSession(sessionId);
    assertThat(laptimes.size()).isEqualTo(1);

    addLap(Instant.now().plusSeconds(23), this.beacons.get(0).getId(), this.chronos.get(0));
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
  public void testCsv() {
    createWithAllNeeded();
    Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    addLap(now.plusSeconds(11), this.beacons.get(0).getId(), this.chronos.get(0));
    addLap(now.plusSeconds(23), this.beacons.get(0).getId(), this.chronos.get(0));

    long sessionId = this.sessions.get(0).getId();
    String csvOfSession = getCsvOfSession(sessionId);

    assertThat(csvOfSession).isNotNull();
    String[] lines = csvOfSession.split("\n");
    assertThat(lines.length).isEqualTo(3);

    Stream<String> stream = Arrays.stream(lines[0].split(","));
    List<String> header = stream.map(h -> h.replaceAll("\"", "")).toList();
    assertThat(header).contains("DURATION", "ENDDATE", "PILOT", "SESSION");
  }


  @Test
  public void noLapCreatedWhenChronoInProximityMode() {
    createWithAllNeeded();

    ChronometerDTO dto = this.restChronometer.getById(this.chronos.get(0));
    dto.setSelectionStrategy(Chronometer.PingSelectionStrategy.PROXIMITY.toString());
    this.restChronometer.update(this.chronos.get(0), dto);

    addLap(Instant.now().plusSeconds(11), this.beacons.get(0).getId(), this.chronos.get(0));

    long sessionId = this.sessions.get(0).getId();
    List<LapTimeDTO> laptimes = getAllOfSession(sessionId);
    assertThat(laptimes.size()).isEqualTo(0);
  }

  @Test
  public void allLaptimesOfPilotInOrderOfOccurence() {
    addBeacon(101);
    BeaconDTO beacon1 = this.restBeacon.getByNumber(101);
    NestedPilotDTO pilot1 = beacon1.getPilot();
    addBeacon(102);
    BeaconDTO beacon2 = this.restBeacon.getByNumber(102);
    NestedPilotDTO pilot2 = beacon2.getPilot();
    createSession("Session", "C1");
    long sessionId = this.sessions.get(0).getId();
    Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    this.restPing.createPing(now.plusSeconds(0), this.beacons.get(0).getId(), this.chronos.get(0));
    this.restPing.createPing(now.plusSeconds(0), this.beacons.get(1).getId(), this.chronos.get(0));
    this.restPing.createPing(now.plusSeconds(101), this.beacons.get(0).getId(), this.chronos.get(0));
    this.restPing.createPing(now.plusSeconds(102), this.beacons.get(1).getId(), this.chronos.get(0));
    this.restPing.createPing(now.plusSeconds(212), this.beacons.get(0).getId(), this.chronos.get(0));
    this.restPing.createPing(now.plusSeconds(214), this.beacons.get(1).getId(), this.chronos.get(0));
    this.restPing.createPing(now.plusSeconds(336), this.beacons.get(1).getId(), this.chronos.get(0));

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
    BeaconDTO beacon1 = this.restBeacon.getByNumber(101);
    NestedPilotDTO pilot1 = beacon1.getPilot();
    this.restCategory.create("One");
    CategoryDTO one = this.restCategory.getByName("One");
    this.restCategory.addPilot(one.getId(), pilot1.getId());

    addBeacon(102);
    BeaconDTO beacon2 = this.restBeacon.getByNumber(102);
    NestedPilotDTO pilot2 = beacon2.getPilot();
    this.restCategory.create("Two");
    CategoryDTO two = this.restCategory.getByName("Two");
    this.restCategory.addPilot(two.getId(), pilot2.getId());

    createSession("Session", "C1");
    long sessionId = this.sessions.get(0).getId();
    Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    this.restPing.createPing(now.plusSeconds(336), this.beacons.get(1).getId(), this.chronos.get(0));
    this.restPing.createPing(now.plusSeconds(0), this.beacons.get(0).getId(), this.chronos.get(0));
    this.restPing.createPing(now.plusSeconds(102), this.beacons.get(1).getId(), this.chronos.get(0));
    this.restPing.createPing(now.plusSeconds(212), this.beacons.get(0).getId(), this.chronos.get(0));
    this.restPing.createPing(now.plusSeconds(214), this.beacons.get(1).getId(), this.chronos.get(0));
    this.restPing.createPing(now.plusSeconds(0), this.beacons.get(1).getId(), this.chronos.get(0));
    this.restPing.createPing(now.plusSeconds(101), this.beacons.get(0).getId(), this.chronos.get(0));

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
  public void laptimesFilteredComparedToLocationMinAndMax() {
    addBeacon(101);
    BeaconDTO beacon1 = this.restBeacon.getByNumber(101);
    NestedPilotDTO pilot1 = beacon1.getPilot();
    createSession("Session", "C1");

    LocationDTO location = this.restLocation.getById(this.locations.get(0).getId());
    location.setMinimum(Duration.ofSeconds(80));
    location.setMaximum(Duration.ofSeconds(120));
    this.restLocation.update(this.locations.get(0).getId(), location);

    Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    this.restPing.createPing(now.plusSeconds(600), this.beacons.get(0).getId(), this.chronos.get(0));
    this.restPing.createPing(now.plusSeconds(500), this.beacons.get(0).getId(), this.chronos.get(0));
    this.restPing.createPing(now.plusSeconds(200), this.beacons.get(0).getId(), this.chronos.get(0));
    this.restPing.createPing(now.plusSeconds(100), this.beacons.get(0).getId(), this.chronos.get(0));
    this.restPing.createPing(now.plusSeconds(0), this.beacons.get(0).getId(), this.chronos.get(0));

    List<LapTimeDTO> laptimes = getAllOfLocation(this.locations.get(0).getId());
    assertThat(laptimes.size()).isEqualTo(3);
  }

  @Test
  public void allLaptimesOfPilotAndLocationInOrderOfOccurence() {
    addBeacon(101);
    BeaconDTO beacon1 = this.restBeacon.getByNumber(101);
    NestedPilotDTO pilot1 = beacon1.getPilot();
    addBeacon(102);
    BeaconDTO beacon2 = this.restBeacon.getByNumber(102);
    NestedPilotDTO pilot2 = beacon2.getPilot();
    createSession("Session", "C1");

    Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    this.restPing.createPing(now.plusSeconds(214), this.beacons.get(1).getId(), this.chronos.get(0));
    this.restPing.createPing(now.plusSeconds(336), this.beacons.get(1).getId(), this.chronos.get(0));
    this.restPing.createPing(now.plusSeconds(212), this.beacons.get(0).getId(), this.chronos.get(0));
    this.restPing.createPing(now.plusSeconds(101), this.beacons.get(0).getId(), this.chronos.get(0));
    this.restPing.createPing(now.plusSeconds(102), this.beacons.get(1).getId(), this.chronos.get(0));
    this.restPing.createPing(now.plusSeconds(0), this.beacons.get(0).getId(), this.chronos.get(0));
    this.restPing.createPing(now.plusSeconds(0), this.beacons.get(1).getId(), this.chronos.get(0));

    List<LapTimeDTO> laptimes = getAllOfLocation(this.locations.get(0).getId());
    assertThat(laptimes.size()).isEqualTo(5);

    laptimes = getAllOfPilotAndLocation(pilot1.getId(), this.locations.get(0).getId());
    assertThat(laptimes.size()).isEqualTo(2);
    assertThat(laptimes.stream().allMatch(lap -> lap.getPilot().getId().equals(pilot1.getId()))).isTrue();
    assertThat(laptimes.get(0).getDuration()).isEqualTo(Duration.ofSeconds(101));
    assertThat(laptimes.get(1).getDuration()).isEqualTo(Duration.ofSeconds(111));

    laptimes = getAllOfPilotAndLocation(pilot2.getId(), this.locations.get(0).getId());
    assertThat(laptimes.size()).isEqualTo(3);
    assertThat(laptimes.stream().allMatch(lap -> lap.getPilot().getId().equals(pilot2.getId()))).isTrue();
    assertThat(laptimes.get(0).getDuration()).isEqualTo(Duration.ofSeconds(102));
    assertThat(laptimes.get(1).getDuration()).isEqualTo(Duration.ofSeconds(112));
    assertThat(laptimes.get(2).getDuration()).isEqualTo(Duration.ofSeconds(122));
  }

  @Test
  public void allLaptimesOfPilotAndEventInOrderOfOccurence() {
    addBeacon(101);
    BeaconDTO beacon1 = this.restBeacon.getByNumber(101);
    NestedPilotDTO pilot1 = beacon1.getPilot();
    addBeacon(102);
    BeaconDTO beacon2 = this.restBeacon.getByNumber(102);
    NestedPilotDTO pilot2 = beacon2.getPilot();
    createSession("Session", "C1");

    Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    this.restPing.createPing(now.plusSeconds(336), this.beacons.get(1).getId(), this.chronos.get(0));
    this.restPing.createPing(now.plusSeconds(214), this.beacons.get(1).getId(), this.chronos.get(0));
    this.restPing.createPing(now.plusSeconds(0), this.beacons.get(0).getId(), this.chronos.get(0));
    this.restPing.createPing(now.plusSeconds(212), this.beacons.get(0).getId(), this.chronos.get(0));
    this.restPing.createPing(now.plusSeconds(101), this.beacons.get(0).getId(), this.chronos.get(0));
    this.restPing.createPing(now.plusSeconds(0), this.beacons.get(1).getId(), this.chronos.get(0));
    this.restPing.createPing(now.plusSeconds(102), this.beacons.get(1).getId(), this.chronos.get(0));

    List<LapTimeDTO> laptimes = getAllOfEvent(this.events.get(0));
    assertThat(laptimes.size()).isEqualTo(5);

    laptimes = getAllOfPilotAndEvent(pilot1.getId(), this.events.get(0));
    assertThat(laptimes.size()).isEqualTo(2);
    assertThat(laptimes.stream().allMatch(lap -> lap.getPilot().getId().equals(pilot1.getId()))).isTrue();
    assertThat(laptimes.get(0).getDuration()).isEqualTo(Duration.ofSeconds(101));
    assertThat(laptimes.get(1).getDuration()).isEqualTo(Duration.ofSeconds(111));

    laptimes = getAllOfPilotAndEvent(pilot2.getId(), this.events.get(0));
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

    laptimes = getAllOfPilot(this.pilots.get(0), sessionId);
    assertThat(laptimes.size()).isEqualTo(3);
    assertThat(laptimes.stream().allMatch(lap -> lap.getPilot().getId().equals(this.pilots.get(0)))).isTrue();

    laptimes = getAllOfPilot(this.pilots.get(1), sessionId);
    assertThat(laptimes.size()).isEqualTo(2);
    assertThat(laptimes.stream().allMatch(lap -> lap.getPilot().getId().equals(this.pilots.get(1)))).isTrue();
  }


  @Test
  public void allLapsOfRace() {
    long sessionId = createThreePilotsRace();

    List<LapTimeDTO> laptimes = getAllOfSession(sessionId);
    assertThat(laptimes.size()).isEqualTo(7);

    laptimes = getAllOfPilot(this.pilots.get(0), sessionId);
    assertThat(laptimes.size()).isEqualTo(3);
    assertThat(laptimes.stream().allMatch(lap -> lap.getPilot().getId().equals(this.pilots.get(0)))).isTrue();

    laptimes = getAllOfPilot(this.pilots.get(1), sessionId);
    assertThat(laptimes.size()).isEqualTo(3);
    assertThat(laptimes.stream().allMatch(lap -> lap.getPilot().getId().equals(this.pilots.get(1)))).isTrue();

    laptimes = getAllOfPilot(this.pilots.get(2), sessionId);
    assertThat(laptimes.size()).isEqualTo(1);
    assertThat(laptimes.stream().allMatch(lap -> lap.getPilot().getId().equals(this.pilots.get(2)))).isTrue();
    assertThat(laptimes.get(0).getDuration()).isEqualTo(Duration.ofSeconds(102));
    assertThat(laptimes.get(0).getLapIndex()).isEqualTo(1);
    assertThat(laptimes.get(0).getLapNumber()).isEqualTo(1);
  }

  @Test
  public void bestLapTimesOfPilotReturnsThemInOrder() {
    createTwoPilotsTimeTrial();

    List<LapTimeDTO> laptimes = getBestOfPilot(this.pilots.get(0), this.sessions.get(0).getId());
    assertThat(laptimes.size()).isEqualTo(3);
    assertThat(laptimes.stream().allMatch(lap -> lap.getPilot().getId().equals(this.pilots.get(0)))).isTrue();
    Duration maxFoundUntilNow = Duration.ZERO;
    for (LapTimeDTO laptime : laptimes) {
      assertThat(laptime.getDuration().compareTo(maxFoundUntilNow)).isGreaterThanOrEqualTo(0);
    }

    laptimes = getBestOfPilot(this.pilots.get(1), this.sessions.get(0).getId());
    assertThat(laptimes.size()).isEqualTo(2);
    assertThat(laptimes.stream().allMatch(lap -> lap.getPilot().getId().equals(this.pilots.get(1)))).isTrue();
    maxFoundUntilNow = Duration.ZERO;
    for (LapTimeDTO laptime : laptimes) {
      assertThat(laptime.getDuration().compareTo(maxFoundUntilNow)).isGreaterThanOrEqualTo(0);
    }
  }

  @Test
  public void bestLapTimesToCsv() {
    createTwoPilotsTimeTrial();

    String csvOfPilot = getCsvOfBestOfPilot(this.pilots.get(0), this.sessions.get(0).getId());
    assertThat(csvOfPilot).isNotNull();
    String[] lines = csvOfPilot.split("\n");
    assertThat(lines.length).isEqualTo(1 + 3);

    Stream<String> stream = Arrays.stream(lines[0].split(","));
    List<String> header = stream.map(h -> h.replaceAll("\"", "")).toList();
    assertThat(header).contains("DURATION", "ENDDATE", "PILOT", "SESSION");

    csvOfPilot = getCsvOfBestOfPilot(this.pilots.get(1), this.sessions.get(0).getId());
    assertThat(csvOfPilot).isNotNull();
    lines = csvOfPilot.split("\n");
    assertThat(lines.length).isEqualTo(1 + 2);
  }

  @Test
  public void bestRaceLapTimesOfPilotReturnsThemInOrder() {
    createThreePilotsRace();

    List<LapTimeDTO> laptimes = getBestOfPilot(this.pilots.get(0), this.sessions.get(0).getId());
    assertThat(laptimes.size()).isEqualTo(3);
    assertThat(laptimes.stream().allMatch(lap -> lap.getPilot().getId().equals(this.pilots.get(0)))).isTrue();
    Duration maxFoundUntilNow = Duration.ZERO;
    for (LapTimeDTO laptime : laptimes) {
      assertThat(laptime.getDuration().compareTo(maxFoundUntilNow)).isGreaterThanOrEqualTo(0);
    }

    laptimes = getBestOfPilot(this.pilots.get(1), this.sessions.get(0).getId());
    assertThat(laptimes.size()).isEqualTo(3);
    assertThat(laptimes.stream().allMatch(lap -> lap.getPilot().getId().equals(this.pilots.get(1)))).isTrue();
    maxFoundUntilNow = Duration.ZERO;
    for (LapTimeDTO laptime : laptimes) {
      assertThat(laptime.getDuration().compareTo(maxFoundUntilNow)).isGreaterThanOrEqualTo(0);
    }

    laptimes = getBestOfPilot(this.pilots.get(2), this.sessions.get(0).getId());
    assertThat(laptimes.size()).isEqualTo(1);
    assertThat(laptimes.stream().allMatch(lap -> lap.getPilot().getId().equals(this.pilots.get(2)))).isTrue();
    maxFoundUntilNow = Duration.ZERO;
    for (LapTimeDTO laptime : laptimes) {
      assertThat(laptime.getDuration().compareTo(maxFoundUntilNow)).isGreaterThanOrEqualTo(0);
    }

  }

  @Test
  public void bestLapTimesReturnsBestPerPilotInOrder() {
    createTwoPilotsTimeTrial();

    List<LapTimeDTO> laptimes = getBestOfSession(this.sessions.get(0).getId());
    assertThat(laptimes.size()).isEqualTo(2);
    Duration maxFoundUntilNow = Duration.ZERO;
    for (LapTimeDTO laptime : laptimes) {
      assertThat(laptime.getDuration().compareTo(maxFoundUntilNow)).isGreaterThanOrEqualTo(0);
    }
  }

  @Test
  public void bestRaceLapTimesReturnsBestPerPilotInOrder() {
    createThreePilotsRace();

    List<LapTimeDTO> laptimes = getBestOfSession(this.sessions.get(0).getId());
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

    List<LapTimeDTO> laptimes = getResultsOfPilot(this.pilots.get(0), this.sessions.get(0).getId());
    assertThat(laptimes.size()).isEqualTo(3);
    assertThat(laptimes.stream().allMatch(lap -> lap.getPilot().getId().equals(this.pilots.get(0)))).isTrue();
    Duration maxFoundUntilNow = Duration.ZERO;
    for (LapTimeDTO laptime : laptimes) {
      assertThat(laptime.getDuration().compareTo(maxFoundUntilNow)).isGreaterThanOrEqualTo(0);
    }

    laptimes = getResultsOfPilot(this.pilots.get(1), this.sessions.get(0).getId());
    assertThat(laptimes.size()).isEqualTo(2);
    assertThat(laptimes.stream().allMatch(lap -> lap.getPilot().getId().equals(this.pilots.get(1)))).isTrue();
    maxFoundUntilNow = Duration.ZERO;
    for (LapTimeDTO laptime : laptimes) {
      assertThat(laptime.getDuration().compareTo(maxFoundUntilNow)).isGreaterThanOrEqualTo(0);
    }

  }

  @Test
  public void csvResultsOfPilotReturnsAll() {
    createTwoPilotsTimeTrial();

    String csvOfPilot = getResultsOfPilotAsCsv(this.pilots.get(0), this.sessions.get(0).getId());
    assertThat(csvOfPilot).isNotNull();
    String[] lines = csvOfPilot.split("\n");
    assertThat(lines.length).isEqualTo(1 + 3);

    Stream<String> stream = Arrays.stream(lines[0].split(","));
    List<String> header = stream.map(h -> h.replaceAll("\"", "")).toList();
    assertThat(header).contains("DURATION", "ENDDATE", "PILOT", "SESSION");

    csvOfPilot = getResultsOfPilotAsCsv(this.pilots.get(1), this.sessions.get(0).getId());
    assertThat(csvOfPilot).isNotNull();
    lines = csvOfPilot.split("\n");
    assertThat(lines.length).isEqualTo(1 + 2);
  }

  @Test
  public void raceResultsOfPilotReturnsSomething() {
    createThreePilotsRace();

    List<LapTimeDTO> laptimes = getResultsOfPilot(this.pilots.get(0), this.sessions.get(0).getId());
    assertThat(laptimes.size()).isEqualTo(3);
    assertThat(laptimes.stream().allMatch(lap -> lap.getPilot().getId().equals(this.pilots.get(0)))).isTrue();
    Duration maxFoundUntilNow = Duration.ZERO;
    for (LapTimeDTO laptime : laptimes) {
      assertThat(laptime.getDuration().compareTo(maxFoundUntilNow)).isGreaterThanOrEqualTo(0);
    }

    laptimes = getResultsOfPilot(this.pilots.get(1), this.sessions.get(0).getId());
    assertThat(laptimes.size()).isEqualTo(3);
    assertThat(laptimes.stream().allMatch(lap -> lap.getPilot().getId().equals(this.pilots.get(1)))).isTrue();
    maxFoundUntilNow = Duration.ZERO;
    for (LapTimeDTO laptime : laptimes) {
      assertThat(laptime.getDuration().compareTo(maxFoundUntilNow)).isGreaterThanOrEqualTo(0);
    }

    laptimes = getResultsOfPilot(this.pilots.get(2), this.sessions.get(0).getId());
    assertThat(laptimes.size()).isEqualTo(1);
    assertThat(laptimes.stream().allMatch(lap -> lap.getPilot().getId().equals(this.pilots.get(2)))).isTrue();
    maxFoundUntilNow = Duration.ZERO;
    for (LapTimeDTO laptime : laptimes) {
      assertThat(laptime.getDuration().compareTo(maxFoundUntilNow)).isGreaterThanOrEqualTo(0);
    }
  }

  @Test
  public void resultsReturnsBestPerPilotInOrder() {
    createTwoPilotsTimeTrial();

    List<LapTimeDTO> laptimes = getResultsOfSession(this.sessions.get(0).getId());
    assertThat(laptimes.size()).isEqualTo(2);
    Duration maxFoundUntilNow = Duration.ZERO;
    for (LapTimeDTO laptime : laptimes) {
      assertThat(laptime.getDuration().compareTo(maxFoundUntilNow)).isGreaterThanOrEqualTo(0);
    }

  }

  @Test
  public void raceResults() {
    createThreePilotsRace();

    List<LapTimeDTO> laptimes = getResultsOfSession(this.sessions.get(0).getId());
    assertThat(laptimes.size()).isEqualTo(3);

    assertThat(laptimes.get(0).getPilot().getId()).isEqualTo(this.pilots.get(0));
    assertThat(laptimes.get(0).getLapIndex()).isEqualTo(3);
    assertThat(laptimes.get(0).getLapNumber()).isEqualTo(3);
    assertThat(laptimes.get(0).getGapWithBest()).isEqualTo(Duration.ZERO);
    assertThat(laptimes.get(0).getGapWithPrevious()).isEqualTo(Duration.ZERO);


    assertThat(laptimes.get(1).getPilot().getId()).isEqualTo(this.pilots.get(1));
    assertThat(laptimes.get(1).getLapIndex()).isEqualTo(3);
    assertThat(laptimes.get(1).getLapNumber()).isEqualTo(3);
    assertThat(laptimes.get(1).getGapWithBest()).isGreaterThan(Duration.ZERO);
    assertThat(laptimes.get(1).getGapWithPrevious()).isGreaterThan(Duration.ZERO);


    assertThat(laptimes.get(2).getPilot().getId()).isEqualTo(this.pilots.get(2));
    assertThat(laptimes.get(2).getLapIndex()).isEqualTo(1);
    assertThat(laptimes.get(2).getLapNumber()).isEqualTo(1);

  }

  @Test
  public void raceResultsOfOnGoingRace() {
    // Makes it considered 4th lap of an unfinished race
    createThreePilotsRace(false);

    List<LapTimeDTO> laptimes = getResultsOfSession(this.sessions.get(0).getId());
    assertThat(laptimes.size()).isEqualTo(3);

    assertThat(laptimes.get(0).getPilot().getId()).isEqualTo(this.pilots.get(0));
    assertThat(laptimes.get(0).getLapIndex()).isEqualTo(4);
    assertThat(laptimes.get(0).getLapNumber()).isEqualTo(4);
    assertThat(laptimes.get(0).getGapWithBest()).isEqualTo(Duration.ZERO);
    assertThat(laptimes.get(0).getGapWithPrevious()).isEqualTo(Duration.ZERO);

    assertThat(laptimes.get(1).getPilot().getId()).isEqualTo(this.pilots.get(1));
    assertThat(laptimes.get(1).getLapIndex()).isEqualTo(4);
    assertThat(laptimes.get(1).getLapNumber()).isEqualTo(4);
    assertThat(laptimes.get(1).getGapWithBest()).isGreaterThan(Duration.ZERO);
    assertThat(laptimes.get(1).getGapWithPrevious()).isGreaterThan(Duration.ZERO);

    assertThat(laptimes.get(2).getPilot().getId()).isEqualTo(this.pilots.get(2));
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
    this.restChronometer.create(name);
    ChronometerDTO chronometer = this.restChronometer.getByName(name);
    this.chronos.add(chronometer.getId());
    this.restSession.addChronometer(this.sessions.get(sessionIndex).getId(), chronometer.getId());
  }

  public List<LapTimeDTO> getAllOfSession(long sessionId) {
    Jsonb jsonb = JsonbBuilder.create();
    Response r = given().queryParam("sessionId", sessionId)
        .when().get("/rest/laptimes")
        .then()
        .statusCode(OK)
        .extract().response();
    return jsonb.fromJson(r.asString(), this.dtoListClass);
  }

  public String getCsvOfSession(long sessionId) {
    Jsonb jsonb = JsonbBuilder.create();
    Response r = given().queryParam("sessionId", sessionId)
        .when().get("/rest/laptimes/csv")
        .then()
        .statusCode(OK)
        .extract().response();
    return r.asString();
  }

  public String getCsvOfBestOfPilot(long pilotId, long sessionId) {
    Jsonb jsonb = JsonbBuilder.create();
    Response r = given().queryParam("pilotId", pilotId).queryParam("sessionId", sessionId)
        .when().get("/rest/laptimes/csv/best")
        .then()
        .statusCode(OK)
        .extract().response();
    return r.asString();
  }

  public List<LapTimeDTO> getBestOfSession(long sessionId) {
    Jsonb jsonb = JsonbBuilder.create();
    Response r = given().queryParam("sessionId", sessionId)
        .when().get("/rest/laptimes/best")
        .then()
        .statusCode(OK)
        .extract().response();
    return jsonb.fromJson(r.asString(), this.dtoListClass);
  }

  public List<LapTimeDTO> getResultsOfSession(long sessionId) {
    Jsonb jsonb = JsonbBuilder.create();
    Response r = given().queryParam("sessionId", sessionId)
        .when().get("/rest/laptimes/results")
        .then()
        .statusCode(OK)
        .extract().response();
    return jsonb.fromJson(r.asString(), this.dtoListClass);
  }

  public List<LapTimeDTO> getAllOfLocation(long locationId) {
    Jsonb jsonb = JsonbBuilder.create();
    Response r = given().queryParam("locationId", locationId)
        .when().get("/rest/laptimes")
        .then()
        .statusCode(OK)
        .extract().response();
    return jsonb.fromJson(r.asString(), this.dtoListClass);
  }

  public List<LapTimeDTO> getAllOfEvent(long eventId) {
    Jsonb jsonb = JsonbBuilder.create();
    Response r = given().queryParam("eventId", eventId)
        .when().get("/rest/laptimes")
        .then()
        .statusCode(OK)
        .extract().response();
    return jsonb.fromJson(r.asString(), this.dtoListClass);
  }

  public List<LapTimeDTO> getAllOfPilot(long pilotId, long sessionId) {
    Jsonb jsonb = JsonbBuilder.create();
    Response r = given().queryParam("pilotId", pilotId).queryParam("sessionId", sessionId)
        .when().get("/rest/laptimes")
        .then()
        .statusCode(OK)
        .extract().response();
    return jsonb.fromJson(r.asString(), this.dtoListClass);
  }

  public List<LapTimeDTO> getBestOfPilot(long pilotId, long sessionId) {
    Jsonb jsonb = JsonbBuilder.create();
    Response r = given().queryParam("pilotId", pilotId).queryParam("sessionId", sessionId)
        .when().get("/rest/laptimes/best")
        .then()
        .statusCode(OK)
        .extract().response();
    return jsonb.fromJson(r.asString(), this.dtoListClass);
  }

  public List<LapTimeDTO> getResultsOfPilot(long pilotId, long sessionId) {
    Jsonb jsonb = JsonbBuilder.create();
    Response r = given().queryParam("pilotId", pilotId).queryParam("sessionId", sessionId)
        .when().get("/rest/laptimes/results")
        .then()
        .statusCode(OK)
        .extract().response();
    return jsonb.fromJson(r.asString(), this.dtoListClass);
  }

  public String getResultsOfPilotAsCsv(long pilotId, long sessionId) {
    Jsonb jsonb = JsonbBuilder.create();
    Response r = given().queryParam("pilotId", pilotId).queryParam("sessionId", sessionId)
        .when().get("/rest/laptimes/csv/results")
        .then()
        .statusCode(OK)
        .extract().response();
    return r.asString();
  }

  public List<LapTimeDTO> getAllOfPilotAndLocation(long pilotId, long locationId) {
    Jsonb jsonb = JsonbBuilder.create();
    Response r = given().queryParam("pilotId", pilotId).queryParam("locationId", locationId)
        .when().get("/rest/laptimes")
        .then()
        .statusCode(OK)
        .extract().response();
    return jsonb.fromJson(r.asString(), this.dtoListClass);
  }

  public List<LapTimeDTO> getAllOfPilotAndEvent(long pilotId, long eventId) {
    Jsonb jsonb = JsonbBuilder.create();
    Response r = given().queryParam("pilotId", pilotId).queryParam("eventId", eventId)
        .when().get("/rest/laptimes")
        .then()
        .statusCode(OK)
        .extract().response();
    return jsonb.fromJson(r.asString(), this.dtoListClass);
  }

  public List<LapTimeDTO> getAllOfCategory(long categoryId, long sessionId) {
    Jsonb jsonb = JsonbBuilder.create();
    Response r = given().queryParam("categoryId", categoryId).queryParam("sessionId", sessionId)
        .when().get("/rest/laptimes")
        .then()
        .statusCode(OK)
        .extract().response();
    return jsonb.fromJson(r.asString(), this.dtoListClass);
  }

  public List<LapTimeDTO> getAllOfSessionInWindow(long sessionId, int page, int pageSize) {
    Jsonb jsonb = JsonbBuilder.create();
    Response r = given().queryParam("sessionId", sessionId).queryParam("page", page).queryParam("pageSize", pageSize)
        .when().get("/rest/" + this.restEntrypointName)
        .then()
        .statusCode(OK)
        .extract().response();
    return jsonb.fromJson(r.asString(), this.dtoListClass);
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
    this.restPing.createPing(now.plusSeconds(0), this.beacons.get(0).getId(), this.chronos.get(0));
  }

  public void addLap(Instant instant, long beaconId, long chronoId) {
    this.restPing.createPing(instant, beaconId, chronoId);
  }

  private void createSession(String sessionName, String chronoName) {
    this.restSession.createWith(sessionName, null, null, chronoName, "L1", "E1");
    this.chronos.add(this.restChronometer.getByName("C1").getId());
    this.events.add(this.restEvent.getByName("E1").getId());
    this.locations.add(this.restLocation.getByName("L1"));
    this.sessions.add(this.restSession.getByName(sessionName));
  }

  private void addBeacon(int beaconNumber) {
    this.restPilot.createWithBeacon("Pilot", "" + beaconNumber, beaconNumber);
    this.beacons.add(this.restBeacon.getByNumber(beaconNumber));
  }

  private long createThreePilotsRace() {
    return createThreePilotsRace(true);
  }

  private long createThreePilotsRace(boolean endIt) {
    addBeacon(101);
    BeaconDTO beacon1 = this.restBeacon.getByNumber(101);
    NestedPilotDTO pilot1 = beacon1.getPilot();
    this.pilots.add(pilot1.getId());

    addBeacon(102);
    BeaconDTO beacon2 = this.restBeacon.getByNumber(102);
    NestedPilotDTO pilot2 = beacon2.getPilot();
    this.pilots.add(pilot2.getId());

    addBeacon(103);
    BeaconDTO beacon3 = this.restBeacon.getByNumber(103);
    NestedPilotDTO pilot3 = beacon3.getPilot();
    this.pilots.add(pilot3.getId());

    createSession("Session", "C1");
    long sessionId = this.sessions.get(0).getId();
    this.sessions.get(0).setInactivity(40000L);
    this.sessions.get(0).setType(SessionType.RACE.getIdentifier());
    addChronometer("C2", 0);
    addChronometer("C3", 0);
    this.restSession.update(sessionId, this.sessions.get(0));
    LocationDTO location = this.restLocation.getById(this.locations.get(0).getId());
    location.setLoopTrack(true);
    this.restLocation.update(this.locations.get(0).getId(), location);

    Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    PingDTO startPing = new PingDTO();
    startPing.setInstant(now);
    startPing.setPower(0);
    this.restSession.addPilot(sessionId, pilot1.getId());
    this.restSession.addPilot(sessionId, pilot2.getId());
    this.restSession.addPilot(sessionId, pilot3.getId());
    this.restSession.start(startPing, sessionId);

    // Lap 1 starts
    // Ignored because of inactivity
    this.restPing.createPing(now.plusSeconds(3), this.beacons.get(1).getId(), this.chronos.get(0));
    this.restPing.createPing(now.plusSeconds(4), this.beacons.get(2).getId(), this.chronos.get(0));
    // First intermediate in order
    this.restPing.createPing(now.plusSeconds(30), this.beacons.get(0).getId(), this.chronos.get(1));
    this.restPing.createPing(now.plusSeconds(31), this.beacons.get(1).getId(), this.chronos.get(1));
    this.restPing.createPing(now.plusSeconds(32), this.beacons.get(2).getId(), this.chronos.get(1));
    // Second intermediate in other order
    this.restPing.createPing(now.plusSeconds(62), this.beacons.get(0).getId(), this.chronos.get(2));
    this.restPing.createPing(now.plusSeconds(61), this.beacons.get(1).getId(), this.chronos.get(2));
    this.restPing.createPing(now.plusSeconds(60), this.beacons.get(2).getId(), this.chronos.get(2));
    // Lap 2 starts
    // Received in wrong order
    this.restPing.createPing(now.plusSeconds(102), this.beacons.get(2).getId(), this.chronos.get(0));
    this.restPing.createPing(now.plusSeconds(101), this.beacons.get(1).getId(), this.chronos.get(0));
    this.restPing.createPing(now.plusSeconds(100), this.beacons.get(0).getId(), this.chronos.get(0));
    // Third does not finish - falls during lap
    this.restPing.createPing(now.plusSeconds(130), this.beacons.get(0).getId(), this.chronos.get(1));
    this.restPing.createPing(now.plusSeconds(131), this.beacons.get(1).getId(), this.chronos.get(1));
    // Last intermediate of lap 2 received late
    // Last lap
    this.restPing.createPing(now.plusSeconds(200), this.beacons.get(0).getId(), this.chronos.get(0));
    this.restPing.createPing(now.plusSeconds(201), this.beacons.get(1).getId(), this.chronos.get(0));
    this.restPing.createPing(now.plusSeconds(230), this.beacons.get(1).getId(), this.chronos.get(1));
    this.restPing.createPing(now.plusSeconds(231), this.beacons.get(0).getId(), this.chronos.get(1));
    this.restPing.createPing(now.plusSeconds(260), this.beacons.get(0).getId(), this.chronos.get(2));
    this.restPing.createPing(now.plusSeconds(261), this.beacons.get(1).getId(), this.chronos.get(2));
    // Last intermediate of lap 2 received late
    this.restPing.createPing(now.plusSeconds(160), this.beacons.get(0).getId(), this.chronos.get(2));
    this.restPing.createPing(now.plusSeconds(161), this.beacons.get(1).getId(), this.chronos.get(2));
    // Arrival
    this.restPing.createPing(now.plusSeconds(300), this.beacons.get(0).getId(), this.chronos.get(0));
    this.restPing.createPing(now.plusSeconds(301), this.beacons.get(1).getId(), this.chronos.get(0));

    if (endIt) {
      PingDTO endPing = new PingDTO();
      endPing.setInstant(now.plusSeconds(305));
      endPing.setPower(0);
      this.restSession.end(endPing, sessionId);
    }
    return sessionId;
  }

  private long createTwoPilotsTimeTrial() {
    addBeacon(101);
    BeaconDTO beacon1 = this.restBeacon.getByNumber(101);
    NestedPilotDTO pilot1 = beacon1.getPilot();
    this.pilots.add(pilot1.getId());
    addBeacon(102);
    BeaconDTO beacon2 = this.restBeacon.getByNumber(102);
    NestedPilotDTO pilot2 = beacon2.getPilot();
    this.pilots.add(pilot2.getId());
    createSession("Session", "C1");
    long sessionId = this.sessions.get(0).getId();
    addChronometer("C2", 0);
    addChronometer("C3", 0);

    Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    this.restPing.createPing(now.plusSeconds(0), this.beacons.get(0).getId(), this.chronos.get(0));
    this.restPing.createPing(now.plusSeconds(30), this.beacons.get(0).getId(), this.chronos.get(1));
    this.restPing.createPing(now.plusSeconds(60), this.beacons.get(0).getId(), this.chronos.get(2));

    this.restPing.createPing(now.plusSeconds(62), this.beacons.get(1).getId(), this.chronos.get(2));
    this.restPing.createPing(now.plusSeconds(31), this.beacons.get(1).getId(), this.chronos.get(1));
    this.restPing.createPing(now.plusSeconds(0), this.beacons.get(1).getId(), this.chronos.get(0));

    this.restPing.createPing(now.plusSeconds(100), this.beacons.get(0).getId(), this.chronos.get(0));
    this.restPing.createPing(now.plusSeconds(130), this.beacons.get(0).getId(), this.chronos.get(1));
    this.restPing.createPing(now.plusSeconds(160), this.beacons.get(0).getId(), this.chronos.get(2));

    this.restPing.createPing(now.plusSeconds(200), this.beacons.get(0).getId(), this.chronos.get(0));
    this.restPing.createPing(now.plusSeconds(230), this.beacons.get(0).getId(), this.chronos.get(1));
    this.restPing.createPing(now.plusSeconds(260), this.beacons.get(0).getId(), this.chronos.get(2));

    this.restPing.createPing(now.plusSeconds(202), this.beacons.get(1).getId(), this.chronos.get(0));
    this.restPing.createPing(now.plusSeconds(231), this.beacons.get(1).getId(), this.chronos.get(1));

    this.restPing.createPing(now.plusSeconds(300), this.beacons.get(0).getId(), this.chronos.get(0));

    this.restPing.createPing(now.plusSeconds(102), this.beacons.get(1).getId(), this.chronos.get(0));
    this.restPing.createPing(now.plusSeconds(132), this.beacons.get(1).getId(), this.chronos.get(1));
    this.restPing.createPing(now.plusSeconds(162), this.beacons.get(1).getId(), this.chronos.get(2));
    return sessionId;
  }


}
