package org.trd.app.teknichrono.it;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.trd.app.teknichrono.model.dto.ChronometerDTO;
import org.trd.app.teknichrono.model.dto.EventDTO;
import org.trd.app.teknichrono.model.dto.LocationDTO;
import org.trd.app.teknichrono.model.dto.NestedChronometerDTO;
import org.trd.app.teknichrono.model.dto.NestedEventDTO;
import org.trd.app.teknichrono.model.dto.NestedLocationDTO;
import org.trd.app.teknichrono.model.dto.NestedPilotDTO;
import org.trd.app.teknichrono.model.dto.PilotDTO;
import org.trd.app.teknichrono.model.dto.PingDTO;
import org.trd.app.teknichrono.model.dto.SessionDTO;
import org.trd.app.teknichrono.model.jpa.SessionType;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.*;

@Tag("integration")
@QuarkusTest
public class TestRestSessionEndpoint extends TestRestEndpoint<SessionDTO> {

  private TestRestPilotEndPoint restPilot = new TestRestPilotEndPoint();
  private TestRestChronometerEndpoint restChronometer = new TestRestChronometerEndpoint();
  private TestRestEventEndpoint restEvent = new TestRestEventEndpoint();
  private TestRestLocationEndpoint restLocation = new TestRestLocationEndpoint();
  private TestRestPingEndpoint restPing = new TestRestPingEndpoint();
  private TestRestBeaconEndpoint restBeacon = new TestRestBeaconEndpoint();

  public TestRestSessionEndpoint() {
    super("sessions", SessionDTO.class, new ArrayList<SessionDTO>() {
      private static final long serialVersionUID = -4469128182942504311L;
    }.getClass().getGenericSuperclass());
  }

  @AfterEach
  public void cleanup() {
    restPilot.deleteAll();
    restChronometer.deleteAll();
    restEvent.deleteAll();
    restLocation.deleteAll();
    restPing.deleteAll();
    restBeacon.deleteAll();
    deleteAll();
  }


  /**
   * ******************** Tests *********************
   **/

  @Test
  public void testLists() {
    List<SessionDTO> sessions = getAll();
    Assertions.assertThat(sessions.size()).isEqualTo(0);
    create("S1");
    sessions = getAll();
    Assertions.assertThat(sessions.size()).isEqualTo(1);
    create("S2");
    sessions = getAll();
    Assertions.assertThat(sessions.size()).isEqualTo(2);

    List<SessionDTO> someSessions = getAllInWindow(1, 1);
    Assertions.assertThat(someSessions.size()).isEqualTo(1);

    sessions = getAll();
    for (SessionDTO beacon : sessions) {
      delete(beacon.getId());
    }
    sessions = getAll();
    Assertions.assertThat(sessions.size()).isEqualTo(0);
    assertTestCleanedEverything();
  }


  @Test
  public void testCreateModifyDelete() {
    create("S1");
    SessionDTO session = getByName("S1");
    assertThat(session.getPilots()).isNullOrEmpty();
    assertThat(session.getChronometers()).isNullOrEmpty();
    assertThat(session.getEvent()).isNull();
    assertThat(session.getLocation()).isNull();
    long id = session.getId();
    getById(id);

    SessionDTO modifiedSession = new SessionDTO();
    modifiedSession.setName("S2");
    Instant now = Instant.now();
    modifiedSession.setStart(now);
    modifiedSession.setEnd(now.plusSeconds(55));
    modifiedSession.setInactivity(10L);
    modifiedSession.setType(SessionType.RACE.getIdentifier());
    modifiedSession.setCurrent(true);
    modifiedSession.setId(id);
    update(id, modifiedSession);
    List<SessionDTO> sessions = getAll();
    Assertions.assertThat(sessions.size()).isEqualTo(1);
    getByName("S1", NOT_FOUND);
    SessionDTO newReturnedBeacon = getByName("S2");
    Assertions.assertThat(newReturnedBeacon.getId()).isEqualTo(id);
    Assertions.assertThat(newReturnedBeacon.getStart()).isEqualTo(now);
    Assertions.assertThat(newReturnedBeacon.getEnd()).isEqualTo(now.plusSeconds(55));
    Assertions.assertThat(newReturnedBeacon.getInactivity()).isEqualTo(10L);
    Assertions.assertThat(newReturnedBeacon.getType()).isEqualTo(SessionType.RACE.getIdentifier());
    Assertions.assertThat(newReturnedBeacon.isCurrent()).isEqualTo(true);

    SessionDTO modifiedAgainSession = new SessionDTO();
    modifiedAgainSession.setName("S3");
    Instant newStart = Instant.now();
    modifiedAgainSession.setStart(newStart);
    modifiedAgainSession.setEnd(newStart.plusSeconds(20));
    modifiedAgainSession.setInactivity(20L);
    modifiedAgainSession.setType(SessionType.TIME_TRIAL.getIdentifier());
    modifiedAgainSession.setCurrent(false);
    modifiedAgainSession.setId(id);
    update(id, modifiedAgainSession);
    sessions = getAll();
    Assertions.assertThat(sessions.size()).isEqualTo(1);
    getByName("S1", NOT_FOUND);
    getByName("S2", NOT_FOUND);
    newReturnedBeacon = getByName("S3");
    Assertions.assertThat(newReturnedBeacon.getId()).isEqualTo(id);
    Assertions.assertThat(newReturnedBeacon.getStart()).isEqualTo(newStart);
    Assertions.assertThat(newReturnedBeacon.getEnd()).isEqualTo(newStart.plusSeconds(20));
    Assertions.assertThat(newReturnedBeacon.getInactivity()).isEqualTo(20L);
    Assertions.assertThat(newReturnedBeacon.getType()).isEqualTo(SessionType.TIME_TRIAL.getIdentifier());
    Assertions.assertThat(newReturnedBeacon.isCurrent()).isEqualTo(false);
  }

  @Test
  public void testCreateWithPilot() {
    createWithPilot("Session", "Pilot", "OfSession");
    SessionDTO session = getByName("Session");
    assertThat(session.getPilots()).isNotNull();
    assertThat(session.getPilots()).hasSize(1);
    assertThat(session.getName()).isEqualTo("Session");

    long id = session.getId();
    session = getById(id);
    assertThat(session.getPilots()).isNotNull();
    assertThat(session.getPilots()).hasSize(1);
    assertThat(session.getName()).isEqualTo("Session");
    long pilotId = session.getPilots().iterator().next().getId();

  }

  @Test
  public void testCreateWithChronometer() {
    createWithChronometer("Session", "C1");
    SessionDTO session = getByName("Session");
    assertThat(session.getChronometers()).isNotNull();
    assertThat(session.getChronometers()).hasSize(1);
    assertThat(session.getName()).isEqualTo("Session");

    long id = session.getId();
    session = getById(id);
    assertThat(session.getChronometers()).isNotNull();
    assertThat(session.getChronometers()).hasSize(1);
    assertThat(session.getName()).isEqualTo("Session");
    long chronoId = session.getChronometers().iterator().next().getId();
  }

  @Test
  public void testCreateWithLocation() {
    createWithLocation("Session", "L1");
    SessionDTO session = getByName("Session");
    assertThat(session.getLocation()).isNotNull();
    assertThat(session.getLocation().getName()).isEqualTo("L1");
    assertThat(session.getName()).isEqualTo("Session");

    long id = session.getId();
    session = getById(id);
    assertThat(session.getLocation()).isNotNull();
    assertThat(session.getLocation().getName()).isEqualTo("L1");
    assertThat(session.getName()).isEqualTo("Session");
    long locationId = session.getLocation().getId();

  }

  @Test
  public void createWithEventThrowsErrorIfEventDoesNotExist() {
    SessionDTO session = createDto("Session");
    NestedEventDTO nestedEvent = new NestedEventDTO();
    nestedEvent.setId(666L);
    nestedEvent.setName("No");
    session.setEvent(nestedEvent);
    create(session, NOT_FOUND);
  }

  @Test
  public void testCreateWithEvent() {
    createWithEvent("Session", "E1");
    SessionDTO session = getByName("Session");
    assertThat(session.getEvent()).isNotNull();
    assertThat(session.getEvent().getName()).isEqualTo("E1");
    assertThat(session.getName()).isEqualTo("Session");

    long id = session.getId();
    session = getById(id);
    assertThat(session.getEvent()).isNotNull();
    assertThat(session.getEvent().getName()).isEqualTo("E1");
    assertThat(session.getName()).isEqualTo("Session");
    long eventId = session.getEvent().getId();

  }

  @Test
  public void testAddChronometerViaUpdate() {
    create("Session");
    SessionDTO session = getByName("Session");
    assertThat(session.getChronometers()).isNullOrEmpty();

    long id = session.getId();

    SessionDTO modifiedSession = createDto("Modified");
    modifiedSession.setId(id);

    restChronometer.create("C1");
    ChronometerDTO chronometer = restChronometer.getByName("C1");
    long chronoId = chronometer.getId();
    NestedChronometerDTO nestedChronometer = new NestedChronometerDTO();
    nestedChronometer.setId(chronometer.getId());
    modifiedSession.getChronometers().add(nestedChronometer);

    update(id, modifiedSession);
    // Update twice has no impact
    update(id, modifiedSession);

    getByName("Session", NOT_FOUND);
    SessionDTO newReturnedSession = getByName("Modified");
    assertThat(newReturnedSession.getId()).isEqualTo(id);
    assertThat(newReturnedSession.getChronometers()).isNotNull();
    assertThat(newReturnedSession.getChronometers()).hasSize(1);
    NestedChronometerDTO nestedChronometerFound = newReturnedSession.getChronometers().iterator().next();
    assertThat(nestedChronometerFound.getId()).isEqualTo(chronoId);
    assertThat(nestedChronometerFound.getName()).isEqualTo("C1");

    modifiedSession = createDto("ModifiedAgain");
    modifiedSession.setId(id);

    restChronometer.create("C2");
    ChronometerDTO chronometer2 = restChronometer.getByName("C2");
    long chronometer2Id = chronometer2.getId();
    NestedChronometerDTO nestedChronometer2 = new NestedChronometerDTO();
    nestedChronometer2.setId(chronometer2.getId());
    modifiedSession.getChronometers().add(nestedChronometer);
    modifiedSession.getChronometers().add(nestedChronometer2);

    update(id, modifiedSession);

    newReturnedSession = getByName("ModifiedAgain");
    assertThat(newReturnedSession.getId()).isEqualTo(id);
    assertThat(newReturnedSession.getChronometers()).isNotNull();
    assertThat(newReturnedSession.getChronometers()).hasSize(2);

  }

  @Test
  public void testSetLocationViaUpdate() {
    create("Session");
    SessionDTO session = getByName("Session");
    assertThat(session.getLocation()).isNull();

    long sessionId = session.getId();

    SessionDTO modifiedSession = createDto("Modified");
    modifiedSession.setId(sessionId);

    restLocation.create("L1");
    LocationDTO locationDto = restLocation.getByName("L1");
    long locationId = locationDto.getId();
    NestedLocationDTO nestedLocation = new NestedLocationDTO();
    nestedLocation.setId(locationId);
    modifiedSession.setLocation(nestedLocation);

    update(sessionId, modifiedSession);
    // Update twice has no impact
    update(sessionId, modifiedSession);

    getByName("Session", NOT_FOUND);
    SessionDTO newReturnedSession = getByName("Modified");
    assertThat(newReturnedSession.getId()).isEqualTo(sessionId);
    assertThat(newReturnedSession.getName()).isEqualTo("Modified");
    assertThat(newReturnedSession.getLocation()).isNotNull();
    assertThat(newReturnedSession.getLocation().getId()).isEqualTo(locationId);
    assertThat(newReturnedSession.getLocation().getName()).isEqualTo("L1");
  }

  @Test
  public void testSetEventViaUpdate() {
    create("Session");
    SessionDTO session = getByName("Session");
    assertThat(session.getLocation()).isNull();

    long sessionId = session.getId();

    SessionDTO modifiedSession = createDto("Modified");
    modifiedSession.setId(sessionId);

    restEvent.create("E1");
    EventDTO eventDto = restEvent.getByName("E1");
    long eventId = eventDto.getId();
    NestedEventDTO nestedEvent = new NestedEventDTO();
    nestedEvent.setId(eventId);
    modifiedSession.setEvent(nestedEvent);

    update(sessionId, modifiedSession);
    // Update twice has no impact
    update(sessionId, modifiedSession);

    getByName("Session", NOT_FOUND);
    SessionDTO newReturnedSession = getByName("Modified");
    assertThat(newReturnedSession.getId()).isEqualTo(sessionId);
    assertThat(newReturnedSession.getName()).isEqualTo("Modified");
    assertThat(newReturnedSession.getEvent()).isNotNull();
    assertThat(newReturnedSession.getEvent().getId()).isEqualTo(eventId);
    assertThat(newReturnedSession.getEvent().getName()).isEqualTo("E1");
  }


  @Test
  public void testAddPilotViaUpdate() {
    create("Session");
    SessionDTO b = getByName("Session");
    assertThat(b.getPilots()).isNullOrEmpty();

    long id = b.getId();

    SessionDTO modifiedSession = createDto("Modified");
    modifiedSession.setId(id);

    restPilot.create("Pilot1", "OfSession");
    PilotDTO pilot = restPilot.getByName("Pilot1", "OfSession");
    long pilot1Id = pilot.getId();
    NestedPilotDTO pilot1Dto = new NestedPilotDTO();
    pilot1Dto.setId(pilot.getId());
    modifiedSession.getPilots().add(pilot1Dto);

    update(id, modifiedSession);
    // Update twice has no impact
    update(id, modifiedSession);

    getByName("Session", NOT_FOUND);
    SessionDTO newReturnedSession = getByName("Modified");
    assertThat(newReturnedSession.getId()).isEqualTo(id);
    assertThat(newReturnedSession.getPilots()).isNotNull();
    assertThat(newReturnedSession.getPilots()).hasSize(1);
    NestedPilotDTO pilotDtoFound = newReturnedSession.getPilots().iterator().next();
    assertThat(pilotDtoFound.getId()).isEqualTo(pilot1Id);
    assertThat(pilotDtoFound.getFirstName()).isEqualTo("Pilot1");
    assertThat(pilotDtoFound.getLastName()).isEqualTo("OfSession");

    modifiedSession = createDto("Modified");
    modifiedSession.setId(id);

    restPilot.create("Pilot2", "OfSession");
    PilotDTO pilot2 = restPilot.getByName("Pilot2", "OfSession");
    long pilot2Id = pilot2.getId();
    NestedPilotDTO pilot2Dto = new NestedPilotDTO();
    pilot2Dto.setId(pilot2.getId());
    modifiedSession.getPilots().add(pilot1Dto);
    modifiedSession.getPilots().add(pilot2Dto);

    update(id, modifiedSession);

    newReturnedSession = getByName("Modified");
    assertThat(newReturnedSession.getId()).isEqualTo(id);
    assertThat(newReturnedSession.getPilots()).isNotNull();
    assertThat(newReturnedSession.getPilots()).hasSize(2);
  }

  @Test
  public void testAddPilotViaAdd() {
    create("Session");
    SessionDTO session = getByName("Session");
    long id = session.getId();

    restPilot.create("Pilot1", "OfSession");
    PilotDTO pilot = restPilot.getByName("Pilot1", "OfSession");
    long pilot1Id = pilot.getId();

    addPilot(id, pilot1Id);
    // Adding twice has no impact
    addPilot(id, pilot1Id);

    SessionDTO newReturnedSession = getByName("Session");
    assertThat(newReturnedSession.getId()).isEqualTo(id);
    assertThat(newReturnedSession.getPilots()).isNotNull();
    assertThat(newReturnedSession.getPilots()).hasSize(1);
    NestedPilotDTO pilotDtoFound = newReturnedSession.getPilots().iterator().next();
    assertThat(pilotDtoFound.getId()).isEqualTo(pilot1Id);
    assertThat(pilotDtoFound.getFirstName()).isEqualTo("Pilot1");
    assertThat(pilotDtoFound.getLastName()).isEqualTo("OfSession");

    restPilot.create("Pilot2", "OfSession");
    PilotDTO pilot2 = restPilot.getByName("Pilot2", "OfSession");
    long pilot2Id = pilot2.getId();

    addPilot(id, pilot2Id);

    newReturnedSession = getByName("Session");
    assertThat(newReturnedSession.getId()).isEqualTo(id);
    assertThat(newReturnedSession.getPilots()).isNotNull();
    assertThat(newReturnedSession.getPilots()).hasSize(2);
  }

  @Test
  public void thowsErrorIfAddsChronometerThatDoesNotExist() {
    create("Session");
    SessionDTO session = getByName("Session");
    long id = session.getId();

    addChronometer(id, 666, NOT_FOUND);
  }

  @Test
  public void thowsErrorIfAddsPilotThatDoesNotExist() {
    create("Session");
    SessionDTO session = getByName("Session");
    long id = session.getId();

    addPilot(id, 666, NOT_FOUND);
  }

  @Test
  public void testAddChronometerViaAdd() {
    create("Session");
    SessionDTO session = getByName("Session");
    long id = session.getId();

    restChronometer.create("C1");
    ChronometerDTO chronometer = restChronometer.getByName("C1");
    long chronometer1Id = chronometer.getId();

    addChronometer(id, chronometer1Id);
    // Adding twice has no impact
    addChronometer(id, chronometer1Id);

    SessionDTO newReturnedSession = getByName("Session");
    assertThat(newReturnedSession.getId()).isEqualTo(id);
    assertThat(newReturnedSession.getChronometers()).isNotNull();
    assertThat(newReturnedSession.getChronometers()).hasSize(1);
    NestedChronometerDTO chronometerDtoFound = newReturnedSession.getChronometers().iterator().next();
    assertThat(chronometerDtoFound.getId()).isEqualTo(chronometer1Id);
    assertThat(chronometerDtoFound.getName()).isEqualTo("C1");

    restChronometer.create("C2");
    ChronometerDTO chronometer2 = restChronometer.getByName("C2");
    long chronometer2Id = chronometer2.getId();

    addChronometer(id, chronometer2Id);

    newReturnedSession = getByName("Session");
    assertThat(newReturnedSession.getId()).isEqualTo(id);
    assertThat(newReturnedSession.getChronometers()).isNotNull();
    assertThat(newReturnedSession.getChronometers()).hasSize(2);
  }

  @Test
  public void testRemovePilot() {
    createWithPilot("Session", "P1", "OfSession");
    SessionDTO c = getByName("Session");
    long id = c.getId();
    long pilotId = c.getPilots().iterator().next().getId();

    SessionDTO modifiedSession = createDto("ModifiedSession");
    modifiedSession.setId(id);

    update(id, modifiedSession);

    SessionDTO newReturnedSession = getByName("ModifiedSession");
    assertThat(newReturnedSession.getId()).isEqualTo(id);
    assertThat(newReturnedSession.getChronometers()).isNotNull();
    assertThat(newReturnedSession.getChronometers()).hasSize(0);
  }

  @Test
  public void testRemoveChronometer() {
    createWithChronometer("Session", "C1");
    SessionDTO c = getByName("Session");
    long id = c.getId();
    long chronoId = c.getChronometers().iterator().next().getId();

    SessionDTO modifiedSession = createDto("ModifiedSession");
    modifiedSession.setId(id);

    update(id, modifiedSession);

    SessionDTO newReturnedSession = getByName("ModifiedSession");
    assertThat(newReturnedSession.getId()).isEqualTo(id);
    assertThat(newReturnedSession.getChronometers()).isNotNull();
    assertThat(newReturnedSession.getChronometers()).hasSize(0);
  }

  @Test
  public void deleteThrowsErrorIfDoesNotExist() {
    delete(666L, NOT_FOUND);
  }

  @Test
  public void picksMostRelevantSessionByDistanceWhenAfter() {
    SessionDTO sessionDTO = new SessionDTO();
    sessionDTO.setName("S1");
    sessionDTO.setType(SessionType.TIME_TRIAL.getIdentifier());
    sessionDTO.setStart(Instant.now().minusSeconds(120 * 60L));
    sessionDTO.setEnd(Instant.now().minusSeconds(60 * 60L));
    create(sessionDTO);
    SessionDTO s1 = getByName("S1");

    sessionDTO = new SessionDTO();
    sessionDTO.setName("S2");
    sessionDTO.setType(SessionType.TIME_TRIAL.getIdentifier());
    sessionDTO.setStart(Instant.now().minusSeconds(60 * 60L));
    sessionDTO.setEnd(Instant.now().minusSeconds(20 * 60L));
    create(sessionDTO);
    SessionDTO s2 = getByName("S2");

    sessionDTO = new SessionDTO();
    sessionDTO.setName("S3");
    sessionDTO.setType(SessionType.TIME_TRIAL.getIdentifier());
    sessionDTO.setStart(Instant.now().plusSeconds(10 * 60L));
    sessionDTO.setEnd(Instant.now().plusSeconds(60 * 60L));
    create(sessionDTO);
    SessionDTO s3 = getByName("S3");

    SessionDTO current = findCurrent();
    assertThat(current.getName()).isEqualTo("S3");
  }

  @Test
  public void picksMostRelevantSessionByDistanceWhenInside() {
    SessionDTO sessionDTO = new SessionDTO();
    sessionDTO.setName("S1");
    sessionDTO.setType(SessionType.TIME_TRIAL.getIdentifier());
    sessionDTO.setStart(Instant.now().minusSeconds(120 * 60L));
    sessionDTO.setEnd(Instant.now().minusSeconds(60 * 60L));
    create(sessionDTO);
    SessionDTO s1 = getByName("S1");

    sessionDTO = new SessionDTO();
    sessionDTO.setName("S2");
    sessionDTO.setType(SessionType.TIME_TRIAL.getIdentifier());
    sessionDTO.setStart(Instant.now().minusSeconds(60 * 60L));
    sessionDTO.setEnd(Instant.now().plusSeconds(20 * 60L));
    create(sessionDTO);
    SessionDTO s2 = getByName("S2");

    sessionDTO = new SessionDTO();
    sessionDTO.setName("S3");
    sessionDTO.setType(SessionType.TIME_TRIAL.getIdentifier());
    sessionDTO.setStart(Instant.now().plusSeconds(20 * 60L));
    sessionDTO.setEnd(Instant.now().plusSeconds(60 * 60L));
    create(sessionDTO);
    SessionDTO s3 = getByName("S3");

    SessionDTO current = findCurrent();
    assertThat(current.getName()).isEqualTo("S2");
  }

  @Test
  public void picksMostRelevantSessionByDistanceWhenBefore() {
    SessionDTO sessionDTO = new SessionDTO();
    sessionDTO.setName("S2");
    sessionDTO.setType(SessionType.TIME_TRIAL.getIdentifier());
    sessionDTO.setStart(Instant.now().minusSeconds(60 * 60L));
    sessionDTO.setEnd(Instant.now().minusSeconds(20 * 60L));
    create(sessionDTO);
    SessionDTO s2 = getByName("S2");

    sessionDTO = new SessionDTO();
    sessionDTO.setName("S1");
    sessionDTO.setType(SessionType.TIME_TRIAL.getIdentifier());
    sessionDTO.setStart(Instant.now().minusSeconds(120 * 60L));
    sessionDTO.setEnd(Instant.now().minusSeconds(60 * 60L));
    create(sessionDTO);
    SessionDTO s1 = getByName("S1");

    SessionDTO current = findCurrent();
    assertThat(current.getName()).isEqualTo("S2");
  }

  @Test
  public void picksMostRelevantReturnsNullIfNone() {
    findCurrentFails(NOT_FOUND);
  }

  @Test
  public void startSessionSetsSessionAsCurrent() {
    create("Session");
    SessionDTO c = getByName("Session");
    assertThat(c.isCurrent()).isFalse();
    long id = c.getId();

    PingDTO ping = new PingDTO();
    ping.setInstant(Instant.now());
    start(ping, id);

    SessionDTO newReturnedSession = getByName("Session");
    assertThat(newReturnedSession.getName()).isEqualTo("Session");
    assertThat(newReturnedSession.getId()).isEqualTo(id);
    assertThat(newReturnedSession.isCurrent()).isTrue();
  }

  @Test
  public void endSessionSetsSessionAsNotCurrent() {
    create("Session");
    SessionDTO c = getByName("Session");
    assertThat(c.isCurrent()).isFalse();
    long id = c.getId();

    PingDTO ping = new PingDTO();
    ping.setInstant(Instant.now());
    start(ping, id);

    ping = new PingDTO();
    ping.setInstant(Instant.now());
    end(ping, id);

    SessionDTO newReturnedSession = getByName("Session");
    assertThat(newReturnedSession.isCurrent()).isFalse();
  }

  @Test
  public void startSessionSetsOtherSessionOfTheEventAsNotCurrent() {
    restEvent.create("Event");
    EventDTO eventDto = restEvent.getByName("Event");
    NestedEventDTO nestedEvent = new NestedEventDTO();
    nestedEvent.setId(eventDto.getId());
    nestedEvent.setName("Event");

    SessionDTO sessionDTO = new SessionDTO();
    sessionDTO.setName("S1");
    sessionDTO.setType(SessionType.TIME_TRIAL.getIdentifier());
    sessionDTO.setStart(Instant.now().minusSeconds(120 * 60L));
    sessionDTO.setEnd(Instant.now().minusSeconds(60 * 60L));
    sessionDTO.setEvent(nestedEvent);
    create(sessionDTO);

    sessionDTO = new SessionDTO();
    sessionDTO.setName("S2");
    sessionDTO.setType(SessionType.TIME_TRIAL.getIdentifier());
    sessionDTO.setStart(Instant.now().minusSeconds(60 * 60L));
    sessionDTO.setEnd(Instant.now().plusSeconds(20 * 60L));
    sessionDTO.setEvent(nestedEvent);
    create(sessionDTO);
    SessionDTO s2 = getByName("S2");

    sessionDTO = new SessionDTO();
    sessionDTO.setName("S3");
    sessionDTO.setType(SessionType.TIME_TRIAL.getIdentifier());
    sessionDTO.setStart(Instant.now().plusSeconds(20 * 60L));
    sessionDTO.setEnd(Instant.now().plusSeconds(60 * 60L));
    sessionDTO.setEvent(nestedEvent);
    create(sessionDTO);
    SessionDTO s3 = getByName("S3");

    PingDTO ping = new PingDTO();
    ping.setInstant(Instant.now());
    start(ping, s2.getId());

    SessionDTO current = findCurrent();
    assertThat(current.getName()).isEqualTo("S2");
    assertThat(current.getId()).isEqualTo(s2.getId());
    assertThat(current.isCurrent()).isTrue();

    ping = new PingDTO();
    ping.setInstant(Instant.now());
    start(ping, s3.getId());

    current = findCurrent();
    assertThat(current.getName()).isEqualTo("S3");
    assertThat(current.getId()).isEqualTo(s3.getId());
    assertThat(current.isCurrent()).isTrue();

    s2 = getByName("S2");
    assertThat(s2.isCurrent()).isFalse();
  }

  @Test
  public void startRaceSessionWithPilotsRequiresAssociatedChronometer() {
    SessionDTO sessionWithPilots = createDto("SessionWithPilots");
    sessionWithPilots.setType(SessionType.RACE.getIdentifier());

    List<PingDTO> allPings = restPing.getAll();
    assertThat(allPings).isNotNull();
    assertThat(allPings.size()).isEqualTo(0);

    restPilot.create("P1", "OfSession");
    PilotDTO p1 = restPilot.getByName("P1", "OfSession");
    NestedPilotDTO nestedP1 = new NestedPilotDTO();
    nestedP1.setId(p1.getId());
    sessionWithPilots.getPilots().add(nestedP1);

    restPilot.create("P2", "OfSession");
    PilotDTO p2 = restPilot.getByName("P2", "OfSession");
    NestedPilotDTO nestedP2 = new NestedPilotDTO();
    nestedP2.setId(p2.getId());
    sessionWithPilots.getPilots().add(nestedP2);

    create(sessionWithPilots);
    SessionDTO sessionFound = getByName("SessionWithPilots");

    PingDTO ping = new PingDTO();
    ping.setInstant(Instant.now());
    startFails(ping, sessionFound.getId(), NOT_FOUND);
  }

  @Test
  public void startRaceSessionWithPilotsAddsPingForEachPilot() {
    SessionDTO sessionWithPilots = createDto("SessionWithPilots");
    sessionWithPilots.setType(SessionType.RACE.getIdentifier());
    restChronometer.create("C1");
    ChronometerDTO chronometer = restChronometer.getByName("C1");
    NestedChronometerDTO nestedChronometerDTO = new NestedChronometerDTO();
    nestedChronometerDTO.setId(chronometer.getId());
    sessionWithPilots.getChronometers().add(nestedChronometerDTO);

    List<PingDTO> allPings = restPing.getAll();
    assertThat(allPings).isNotNull();
    assertThat(allPings.size()).isEqualTo(0);

    restPilot.createWithBeacon("P1", "OfSession", 101);
    PilotDTO p1 = restPilot.getByName("P1", "OfSession");
    NestedPilotDTO nestedP1 = new NestedPilotDTO();
    nestedP1.setId(p1.getId());
    sessionWithPilots.getPilots().add(nestedP1);

    restPilot.createWithBeacon("P2", "OfSession", 102);
    PilotDTO p2 = restPilot.getByName("P2", "OfSession");
    NestedPilotDTO nestedP2 = new NestedPilotDTO();
    nestedP2.setId(p2.getId());
    sessionWithPilots.getPilots().add(nestedP2);

    create(sessionWithPilots);
    SessionDTO sessionFound = getByName("SessionWithPilots");

    PingDTO ping = new PingDTO();
    ping.setInstant(Instant.now());
    start(ping, sessionFound.getId());

    allPings = restPing.getAll();
    assertThat(allPings).isNotNull();
    assertThat(allPings.size()).isEqualTo(2);

  }

  /**
   * ******************** Reusable *********************
   **/

  public void create(String name) {
    SessionDTO sessionDTO = createDto(name);
    create(sessionDTO);
  }

  private SessionDTO createDto(String name) {
    SessionDTO sessionDTO = new SessionDTO();
    sessionDTO.setName(name);
    sessionDTO.setType(SessionType.TIME_TRIAL.getIdentifier());
    sessionDTO.setStart(Instant.now().minusSeconds(30 * 60L));
    sessionDTO.setEnd(Instant.now().minusSeconds(10 * 60L));
    return sessionDTO;
  }

  public void createWithPilot(String name, String firstName, String lastName) {
    createWith(name, firstName, lastName, null, null, null);
  }

  public void createWithChronometer(String name, String chronoName) {
    createWith(name, null, null, chronoName, null, null);
  }

  public void createWithLocation(String name, String locationName) {
    createWith(name, null, null, null, locationName, null);
  }

  private void createWithEvent(String name, String eventName) {
    createWith(name, null, null, null, null, eventName);
  }

  public void createWith(String name, String firstName, String lastName, String chronoName, String locationName, String eventName) {

    SessionDTO session = createDto(name);

    if (firstName != null && lastName != null) {
      restPilot.create(firstName, lastName);
      PilotDTO pilot = restPilot.getByName(firstName, lastName);
      NestedPilotDTO p = new NestedPilotDTO();
      p.setId(pilot.getId());
      p.setFirstName(pilot.getFirstName());
      p.setLastName(pilot.getLastName());
      session.getPilots().add(p);
    }

    if (chronoName != null) {
      restChronometer.create(chronoName);
      ChronometerDTO chrono = restChronometer.getByName(chronoName);
      NestedChronometerDTO nestedChrono = new NestedChronometerDTO();
      nestedChrono.setId(chrono.getId());
      nestedChrono.setName(chronoName);
      session.getChronometers().add(nestedChrono);
    }

    if (locationName != null) {
      restLocation.create(locationName);
      LocationDTO locationDto = restLocation.getByName(locationName);
      NestedLocationDTO nestedLocation = new NestedLocationDTO();
      nestedLocation.setId(locationDto.getId());
      nestedLocation.setName(locationName);
      session.setLocation(nestedLocation);
    }

    if (eventName != null) {
      restEvent.create(eventName);
      EventDTO eventDto = restEvent.getByName(eventName);
      NestedEventDTO nestedEvent = new NestedEventDTO();
      nestedEvent.setId(eventDto.getId());
      nestedEvent.setName(eventName);
      session.setEvent(nestedEvent);
    }

    create(session);
  }

  public void assertTestCleanedEverything() {
    Assertions.assertThat(getAll()).isNullOrEmpty();
    Assertions.assertThat(restPilot.getAll()).isNullOrEmpty();
    Assertions.assertThat(restChronometer.getAll()).isNullOrEmpty();
    Assertions.assertThat(restEvent.getAll()).isNullOrEmpty();
    Assertions.assertThat(restLocation.getAll()).isNullOrEmpty();
    Assertions.assertThat(restPing.getAll()).isNullOrEmpty();
  }

  public void addPilot(long id, long pilotId) {
    addPilot(id, pilotId, OK);
  }

  public void addPilot(long id, long pilotId, int statusCode) {
    given().pathParam("id", id).queryParam("pilotId", pilotId)
        .when().contentType(ContentType.JSON).post("/rest/sessions/{id}/addPilot")
        .then()
        .statusCode(statusCode);
  }

  public void addChronometer(long id, long chronoId) {
    addChronometer(id, chronoId, OK);
  }

  public void addChronometer(long id, long chronoId, int statusCode) {
    given().pathParam("id", id).queryParam("chronoId", chronoId)
        .when().contentType(ContentType.JSON).post("/rest/sessions/{id}/addChronometer")
        .then()
        .statusCode(statusCode);
  }

  public void findCurrentFails(int statusCode) {
    given().when().get("/rest/sessions/current")
        .then()
        .statusCode(NOT_FOUND);
  }

  public SessionDTO findCurrent() {
    Jsonb jsonb = JsonbBuilder.create();
    Response r = given()
        .when().get("/rest/sessions/current")
        .then()
        .statusCode(OK)
        .extract().response();
    return jsonb.fromJson(r.asString(), SessionDTO.class);
  }


  public void startFails(PingDTO ping, long id, int statusCode) {
    Jsonb jsonb = JsonbBuilder.create();
    given().pathParam("id", id)
        .when().contentType(ContentType.JSON).body(jsonb.toJson(ping)).post("/rest/sessions/{id}/start")
        .then()
        .statusCode(statusCode);
  }

  public SessionDTO start(PingDTO ping, long id) {
    Jsonb jsonb = JsonbBuilder.create();
    Response r = given().pathParam("id", id)
        .when().contentType(ContentType.JSON).body(jsonb.toJson(ping)).post("/rest/sessions/{id}/start")
        .then()
        .statusCode(OK)
        .extract().response();
    return jsonb.fromJson(r.asString(), SessionDTO.class);
  }

  public SessionDTO end(PingDTO ping, long id) {
    Jsonb jsonb = JsonbBuilder.create();
    Response r = given().pathParam("id", id)
        .when().contentType(ContentType.JSON).body(jsonb.toJson(ping)).post("/rest/sessions/{id}/end")
        .then()
        .statusCode(OK)
        .extract().response();
    return jsonb.fromJson(r.asString(), SessionDTO.class);
  }

}
