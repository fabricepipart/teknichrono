package org.trd.app.teknichrono.it;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.trd.app.teknichrono.model.dto.EventDTO;
import org.trd.app.teknichrono.model.dto.NestedSessionDTO;
import org.trd.app.teknichrono.model.dto.SessionDTO;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.*;

@Tag("integration")
@QuarkusTest
public class TestRestEventEndpoint extends TestRestEndpoint<EventDTO> {

  private TestRestSessionEndpoint restSession;

  public TestRestEventEndpoint() {
    super("events", EventDTO.class, new ArrayList<EventDTO>() {
      private static final long serialVersionUID = 7417190788513355372L;
    }.getClass().getGenericSuperclass());
  }

  @BeforeEach
  public void prepare() {
    restSession = new TestRestSessionEndpoint();
  }


  /**
   * ******************** Tests *********************
   **/

  @Test
  public void testLists() {
    List<EventDTO> events = getAll();
    Assertions.assertThat(events.size()).isEqualTo(0);
    create("E1");
    events = getAll();
    Assertions.assertThat(events.size()).isEqualTo(1);
    create("E2");
    events = getAll();
    Assertions.assertThat(events.size()).isEqualTo(2);

    List<EventDTO> someEvents = getAllInWindow(1, 1);
    Assertions.assertThat(someEvents.size()).isEqualTo(1);

    events = getAll();
    for (EventDTO event : events) {
      delete(event.getId());
    }
    events = getAll();
    Assertions.assertThat(events.size()).isEqualTo(0);
    assertTestCleanedEverything();
  }

  @Test
  public void testCreateModifyDelete() {
    create("Event1");
    EventDTO eventDto = getByName("Event1");
    assertThat(eventDto.getSessions()).isNullOrEmpty();
    long id = eventDto.getId();
    getById(id);

    EventDTO modifiedLocation = new EventDTO();
    modifiedLocation.setName("Event2");
    modifiedLocation.setId(id);
    update(id, modifiedLocation);
    List<EventDTO> locations = getAll();
    Assertions.assertThat(locations.size()).isEqualTo(1);
    getByName("Event1", NOT_FOUND);
    EventDTO newLocation = getByName("Event2");
    Assertions.assertThat(newLocation.getId()).isEqualTo(id);

    delete(id);
    assertTestCleanedEverything();
  }

  @Test
  public void testCreateWithSession() {
    createWithSession("Event1", "SessionName");
    EventDTO event = getByName("Event1");
    assertThat(event.getSessions()).isNotNull();
    assertThat(event.getSessions()).hasSize(1);
    assertThat(event.getName()).isEqualTo("Event1");
    assertThat(event.getSessions().iterator().next().getName()).isEqualTo("SessionName");

    long id = event.getId();
    event = getById(id);
    assertThat(event.getSessions()).isNotNull();
    assertThat(event.getSessions()).hasSize(1);
    assertThat(event.getName()).isEqualTo("Event1");
    assertThat(event.getSessions().iterator().next().getName()).isEqualTo("SessionName");
    long sessionId = event.getSessions().iterator().next().getId();

    deleteWithSessions(id, sessionId);
    assertTestCleanedEverything();
  }

  @Test
  public void testAddSessionViaUpdate() {
    create("E1");
    EventDTO event = getByName("E1");
    assertThat(event.getSessions()).isNullOrEmpty();

    long id = event.getId();

    EventDTO modifiedEvent = new EventDTO();
    modifiedEvent.setName("E2");
    modifiedEvent.setId(id);

    restSession.create("Session Name");
    SessionDTO sessionDto1 = restSession.getByName("Session Name");
    long session1Id = sessionDto1.getId();
    NestedSessionDTO nestedSession1dto = new NestedSessionDTO();
    nestedSession1dto.setId(session1Id);
    modifiedEvent.getSessions().add(nestedSession1dto);

    update(id, modifiedEvent);
    // Update twice has no impact
    update(id, modifiedEvent);

    getByName("E1", NOT_FOUND);
    EventDTO newReturnedEvent = getByName("E2");
    assertThat(newReturnedEvent.getId()).isEqualTo(id);
    assertThat(newReturnedEvent.getSessions()).isNotNull();
    assertThat(newReturnedEvent.getSessions()).hasSize(1);
    NestedSessionDTO sessionDtoFound = newReturnedEvent.getSessions().iterator().next();
    assertThat(sessionDtoFound.getId()).isEqualTo(session1Id);
    assertThat(sessionDtoFound.getName()).isEqualTo("Session Name");

    modifiedEvent = new EventDTO();
    modifiedEvent.setName("E2");
    modifiedEvent.setId(id);

    restSession.create("Other Session Name");
    SessionDTO sessionDto2 = restSession.getByName("Other Session Name");
    long session2Id = sessionDto2.getId();
    NestedSessionDTO nestedSession2dto = new NestedSessionDTO();
    nestedSession2dto.setId(session2Id);
    modifiedEvent.getSessions().add(nestedSession1dto);
    modifiedEvent.getSessions().add(nestedSession2dto);

    update(id, modifiedEvent);

    newReturnedEvent = getByName("E2");
    assertThat(newReturnedEvent.getId()).isEqualTo(id);
    assertThat(newReturnedEvent.getSessions()).isNotNull();
    assertThat(newReturnedEvent.getSessions()).hasSize(2);

    deleteWithSessions(id, session1Id, session2Id);
    assertTestCleanedEverything();
  }

  @Test
  public void testAddSessionViaAdd() {
    create("E1");
    EventDTO event = getByName("E1");
    long id = event.getId();

    restSession.create("Session Name");
    SessionDTO sessionDto1 = restSession.getByName("Session Name");
    long session1Id = sessionDto1.getId();

    addSession(id, session1Id);
    // Adding twice has no impact
    addSession(id, session1Id);

    EventDTO newReturnedEvent = getByName("E1");
    assertThat(newReturnedEvent.getId()).isEqualTo(id);
    assertThat(newReturnedEvent.getSessions()).isNotNull();
    assertThat(newReturnedEvent.getSessions()).hasSize(1);
    NestedSessionDTO sessionDtoFound = newReturnedEvent.getSessions().iterator().next();
    assertThat(sessionDtoFound.getId()).isEqualTo(session1Id);
    assertThat(sessionDtoFound.getName()).isEqualTo("Session Name");

    restSession.create("Other Session Name");
    SessionDTO sessionDto2 = restSession.getByName("Other Session Name");
    long session2Id = sessionDto2.getId();

    addSession(id, session2Id);

    newReturnedEvent = getByName("E1");
    assertThat(newReturnedEvent.getId()).isEqualTo(id);
    assertThat(newReturnedEvent.getSessions()).isNotNull();
    assertThat(newReturnedEvent.getSessions()).hasSize(2);

    deleteWithSessions(id, session1Id, session2Id);
    assertTestCleanedEverything();
  }

  @Test
  public void testRemoveSession() {
    createWithSession("Event1", "SessionName");
    EventDTO event = getByName("Event1");
    long id = event.getId();
    long sessionId = event.getSessions().iterator().next().getId();

    EventDTO modifiedEvent = new EventDTO();
    modifiedEvent.setName("Event1");
    modifiedEvent.setId(id);

    update(id, modifiedEvent);

    EventDTO newReturnedEvent = getByName("Event1");
    assertThat(newReturnedEvent.getId()).isEqualTo(id);
    assertThat(newReturnedEvent.getSessions()).isNotNull();
    assertThat(newReturnedEvent.getSessions()).hasSize(0);

    deleteWithSessions(id, sessionId);
    assertTestCleanedEverything();
  }


  /**
   * ******************** Reusable *********************
   **/

  public void create(String name) {
    EventDTO p = new EventDTO();
    p.setName(name);
    create(p);
  }

  public void createWithSession(String name, String sessionName) {
    restSession.create(sessionName);
    SessionDTO session = restSession.getByName(sessionName);
    NestedSessionDTO nestedSessionDTO = new NestedSessionDTO();
    nestedSessionDTO.setId(session.getId());
    nestedSessionDTO.setName(session.getName());

    EventDTO eventDto = new EventDTO();
    eventDto.setName(name);
    eventDto.getSessions().add(nestedSessionDTO);

    create(eventDto);
  }

  public void assertTestCleanedEverything() {
    assertThat(getAll()).isNullOrEmpty();
    assertThat(restSession.getAll()).isNullOrEmpty();
  }

  public void deleteWithSessions(long id, long... sessionIds) {
    for (Long sessionId : sessionIds) {
      restSession.delete(sessionId);
    }
    delete(id);
  }

  public static void addSession(long id, long sessionId) {
    given().pathParam("id", id).queryParam("sessionId", sessionId)
        .when().contentType(ContentType.JSON).post("/rest/events/{id}/addSession")
        .then()
        .statusCode(200);
  }
}
