package org.trd.app.teknichrono.rest;

import org.jboss.logging.Logger;
import org.trd.app.teknichrono.business.client.PingManager;
import org.trd.app.teknichrono.business.client.SessionSelector;
import org.trd.app.teknichrono.model.dto.SessionDTO;
import org.trd.app.teknichrono.model.jpa.Chronometer;
import org.trd.app.teknichrono.model.jpa.Event;
import org.trd.app.teknichrono.model.jpa.Location;
import org.trd.app.teknichrono.model.jpa.Pilot;
import org.trd.app.teknichrono.model.jpa.Ping;
import org.trd.app.teknichrono.model.jpa.Session;
import org.trd.app.teknichrono.model.jpa.SessionType;
import org.trd.app.teknichrono.model.repository.ChronometerRepository;
import org.trd.app.teknichrono.model.repository.EventRepository;
import org.trd.app.teknichrono.model.repository.LocationRepository;
import org.trd.app.teknichrono.model.repository.PilotRepository;
import org.trd.app.teknichrono.model.repository.PingRepository;
import org.trd.app.teknichrono.model.repository.SessionRepository;
import org.trd.app.teknichrono.util.DurationLogger;
import org.trd.app.teknichrono.util.exception.NotFoundException;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Path("/sessions")
public class SessionEndpoint {

  private Logger LOGGER = Logger.getLogger(SessionEndpoint.class);

  // TODO get rid of this
  private final EntityManager em;

  private final SessionRepository sessionRepository;

  private final EventRepository eventRepository;

  private final LocationRepository locationRepository;

  private final ChronometerRepository chronometerRepository;

  private final PilotRepository pilotRepository;

  private final PingRepository pingRepository;

  @Inject
  public SessionEndpoint(EntityManager em, SessionRepository sessionRepository, EventRepository eventRepository,
                         LocationRepository locationRepository, ChronometerRepository chronometerRepository,
                         PilotRepository pilotRepository, PingRepository pingRepository) {
    this.em = em;
    this.sessionRepository = sessionRepository;
    this.eventRepository = eventRepository;
    this.locationRepository = locationRepository;
    this.chronometerRepository = chronometerRepository;
    this.pilotRepository = pilotRepository;
    this.pingRepository = pingRepository;
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Transactional
  public Response create(Session entity) {
    DurationLogger perf = DurationLogger.get(LOGGER).start("Create session " + entity.getName());
    if (entity.getEvent() != null && entity.getEvent().id > 0) {
      Event event = eventRepository.findById(entity.getEvent().id);
      entity.setEvent(event);
    }
    if (entity.getLocation() != null && entity.getLocation().id > 0) {
      Location loc = locationRepository.findById(entity.getLocation().id);
      entity.setLocation(loc);
    }
    sessionRepository.persist(entity);
    Response response = Response
        .created(UriBuilder.fromResource(SessionEndpoint.class).path(String.valueOf(entity.id)).build()).build();
    perf.end();
    return response;
  }

  @DELETE
  @Path("/{id:[0-9][0-9]*}")
  @Transactional
  public Response deleteById(@PathParam("id") long id) {
    try (DurationLogger perf = DurationLogger.get(LOGGER).start("Delete session id=" + id)) {
      try {
        sessionRepository.deleteById(id);
      } catch (NotFoundException e) {
        return Response.status(Status.NOT_FOUND).build();
      }
      return Response.noContent().build();
    }
  }

  @GET
  @Path("/{id:[0-9][0-9]*}")
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public Response findById(@PathParam("id") long id) {
    DurationLogger perf = DurationLogger.get(LOGGER).start("Find session id " + id);
    Session entity = sessionRepository.findById(id);
    if (entity == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    perf.end();
    SessionDTO dto = SessionDTO.fromSession(entity);
    return Response.ok(dto).build();
  }

  @GET
  @Path("/current")
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public Response findCurrent() {
    List<Session> allSessions = sessionRepository.findAll(null, null).collect(Collectors.toList());
    SessionSelector selector = new SessionSelector();
    Session session = selector.pickMostRelevantCurrent(allSessions);
    if (session == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    SessionDTO dto = SessionDTO.fromSession(session);
    return Response.ok(dto).build();
  }

  @GET
  @Path("/name")
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public Response findSessionByName(@QueryParam("name") String name) {
    DurationLogger perf = DurationLogger.get(LOGGER).start("Find session named " + name);
    Session entity = sessionRepository.findByName(name);
    if (entity == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    perf.end();
    SessionDTO dto = SessionDTO.fromSession(entity);
    return Response.ok(dto).build();
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public List<SessionDTO> listAll(@QueryParam("page") Integer pageIndex, @QueryParam("pageSize") Integer pageSize) {
    try (DurationLogger perf = DurationLogger.get(LOGGER).start("Find all sessions")) {
      return listAllSessions(pageIndex, pageSize)
          .map(SessionDTO::fromSession)
          .collect(Collectors.toList());
    }
  }

  private Stream<Session> listAllSessions(Integer pageIndex, Integer pageSize) {
    return sessionRepository.findAll(pageIndex, pageSize);
  }

  @POST
  @Path("{sessionId:[0-9][0-9]*}/addChronometer")
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public Response addChronometer(@PathParam("sessionId") long sessionId, @QueryParam("chronoId") Long chronoId,
                                 @QueryParam("index") Integer index) {
    DurationLogger perf = DurationLogger.get(LOGGER).start("Add chrono " + chronoId + " to session " + sessionId);
    Session session = sessionRepository.findById(sessionId);
    if (session == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    Chronometer chronometer = chronometerRepository.findById(chronoId);
    if (chronometer == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    chronometer.getSessions().add(session);
    if (index != null) {
      session.addChronometer(chronometer, index);
    } else {
      session.addChronometer(chronometer);
    }
    sessionRepository.persist(session);
    for (Chronometer c : session.getChronometers()) {
      chronometerRepository.persist(c);
    }
    perf.end();

    SessionDTO dto = SessionDTO.fromSession(session);
    return Response.ok(dto).build();
  }

  @POST
  @Path("{sessionId:[0-9][0-9]*}/addPilot")
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public Response addPilot(@PathParam("sessionId") long sessionId, @QueryParam("pilotId") Long pilotId) {
    DurationLogger perf = DurationLogger.get(LOGGER).start("Add pilot " + pilotId + " to session " + sessionId);
    Session session = sessionRepository.findById(sessionId);
    if (session == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    Pilot pilot = pilotRepository.findById(pilotId);
    if (pilot == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    session.getPilots().add(pilot);
    pilot.getSessions().add(session);
    sessionRepository.persist(session);
    pilotRepository.persist(pilot);
    perf.end();
    SessionDTO dto = SessionDTO.fromSession(session);
    return Response.ok(dto).build();
  }

  @POST
  @Path("{sessionId:[0-9][0-9]*}/start")
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public Response start(Ping start, @PathParam("sessionId") long sessionId) {
    DurationLogger perf = DurationLogger.get(LOGGER).start("Start session " + sessionId);
    Session session = sessionRepository.findById(sessionId);
    if (session == null) {
      return Response.status(Status.NOT_FOUND).build();
    }

    if (!session.isCurrent()) {
      startSession(session, start.getInstant());
      if (session.getSessionType() == SessionType.RACE) {
        startRace(session, start.getInstant());
      }
    }

    SessionDTO dto = SessionDTO.fromSession(session);
    perf.end();

    return Response.ok(dto).build();
  }

  @POST
  @Path("{sessionId:[0-9][0-9]*}/end")
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public Response end(Ping end, @PathParam("sessionId") long sessionId) {
    DurationLogger perf = DurationLogger.get(LOGGER).start("End session " + sessionId + " @ " + end.getInstant());
    Session session = sessionRepository.findById(sessionId);
    if (session == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    endSession(session, end.getInstant());
    SessionDTO dto = SessionDTO.fromSession(session);
    perf.end();

    return Response.ok(dto).build();
  }

  private void startSession(Session session, Instant timestamp) {
    // Stop all other sessions of the event that are not concurrent
    Event event = session.getEvent();
    if (event != null) {
      for (Session otherSession : event.getSessions()) {
        if (otherSession.id != session.id && otherSession.isCurrent() && !intersect(otherSession, session)) {
          endSession(otherSession, timestamp);
        }
      }
    }
    // Start this one
    if (session.getStart().isAfter(timestamp)) {
      session.setStart(timestamp);
    }
    session.setCurrent(true);
    sessionRepository.persist(session);
  }

  private boolean intersect(Session otherSession, Session session) {
    if (otherSession.getStart().compareTo(session.getStart()) >= 0 && otherSession.getStart().compareTo(otherSession.getEnd()) <= 0) {
      return true;
    }
    // FIXME otherSession.getEnd().compareTo(otherSession.getEnd()) :: one of the two sessions should be 'session'
    return otherSession.getEnd().compareTo(otherSession.getStart()) >= 0 && otherSession.getEnd().compareTo(otherSession.getEnd()) <= 0;
  }

  private void endSession(Session session, Instant end) {
    if (session.getEnd().isBefore(end)) {
      LOGGER.warn("Session ID=" + session.id + " has been stopped after expected end");
    }
    session.setEnd(end);
    session.setCurrent(false);
    sessionRepository.persist(session);
  }

  private void startRace(Session session, Instant timestamp) {
    Chronometer chronometer = session.getChronometers().get(0);
    Set<Pilot> pilots = session.getPilots();
    PingManager cm = new PingManager(em);
    for (Pilot pilot : pilots) {
      Ping ping = new Ping();
      ping.setInstant(timestamp);
      ping.setBeacon(pilot.getCurrentBeacon());
      ping.setChrono(chronometer);
      pingRepository.persist(ping);
      cm.addPing(ping, pilot, chronometer, session);
    }
  }

  @PUT
  @Path("/{id:[0-9][0-9]*}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Transactional
  public Response update(@PathParam("id") long id, Session entity) {
    if (entity == null) {
      return Response.status(Status.BAD_REQUEST).build();
    }
    DurationLogger perf = DurationLogger.get(LOGGER).start("Update session id " + entity.id);
    if (id != entity.id) {
      perf.end();
      return Response.status(Status.CONFLICT).entity(entity).build();
    }
    Session session = sessionRepository.findById(id);
    if (session == null) {
      perf.end();
      return Response.status(Status.NOT_FOUND).build();
    }

    if (entity.getLocation() != null && entity.getLocation().id > 0) {
      Location location = locationRepository.findById(entity.getLocation().id);
      session.setLocation(location);
    }
    if (entity.getEvent() != null && entity.getEvent().id > 0) {
      Event event = eventRepository.findById(entity.getEvent().id);
      session.setEvent(event);
    }
    if (entity.getPilots() != null && entity.getPilots().size() > 0) {
      Set<Pilot> pilotsToSet = new HashSet<>();
      for (Pilot p : entity.getPilots()) {
        if (p != null && p.id > 0) {
          Pilot pilot = pilotRepository.findById(p.id);
          pilotsToSet.add(pilot);
        }
      }
      session.setPilots(pilotsToSet);
    }
    if (entity.getChronometers() != null && entity.getChronometers().size() > 0) {
      LOGGER.warn("Session ID=" + session.id + " Chronometers list has not been updated to avoid messing order");
      List<Chronometer> chronosToSet = new ArrayList<>();
      for (Chronometer c : entity.getChronometers()) {
        if (c != null && c.id > 0) {
          Chronometer chrono = chronometerRepository.findById(c.id);
          chronosToSet.add(chrono);
        }
      }
      chronosToSet.sort(Comparator.comparing(Chronometer::getName));
      session.setChronometers(chronosToSet);
    }
    session.setName(entity.getName());
    session.setStart(entity.getStart());
    session.setEnd(entity.getEnd());
    session.setType(entity.getType());
    session.setInactivity(entity.getInactivity());
    session.setCurrent(entity.isCurrent());

    try {
      sessionRepository.persist(session);
    } catch (OptimisticLockException e) {
      return Response.status(Response.Status.CONFLICT).entity(e.getEntity()).build();
    }

    perf.end();
    return Response.noContent().build();
  }
}
