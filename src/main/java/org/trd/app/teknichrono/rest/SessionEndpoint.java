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
import org.trd.app.teknichrono.util.DurationLogger;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.OptimisticLockException;
import javax.persistence.TypedQuery;
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

/**
 *
 */
@Path("/sessions")
public class SessionEndpoint {

  private Logger logger = Logger.getLogger(SessionEndpoint.class);

  private final EntityManager em;

  @Inject
  public SessionEndpoint(EntityManager em) {
    this.em = em;
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Transactional
  public Response create(Session entity) {
    DurationLogger perf = DurationLogger.get(logger).start("Create session " + entity.getName());
    if (entity.getEvent() != null && entity.getEvent().id > 0) {
      Event event = em.find(Event.class, entity.getEvent().id);
      entity.setEvent(event);
    }
    if (entity.getLocation() != null && entity.getLocation().id > 0) {
      Location loc = em.find(Location.class, entity.getLocation().id);
      entity.setLocation(loc);
    }
    em.persist(entity);
    Response response = Response
        .created(UriBuilder.fromResource(SessionEndpoint.class).path(String.valueOf(entity.id)).build()).build();
    perf.end();
    return response;
  }

  @DELETE
  @Path("/{id:[0-9][0-9]*}")
  @Transactional
  public Response deleteById(@PathParam("id") long id) {
    DurationLogger perf = DurationLogger.get(logger).start("Delete session " + id);
    Session entity = em.find(Session.class, id);
    if (entity == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    for (Chronometer c : entity.getChronometers()) {
      c.getSessions().remove(entity);
    }

    for (Pilot p : entity.getPilots()) {
      p.getSessions().remove(entity);
    }
    em.remove(entity);
    perf.end();
    return Response.noContent().build();
  }

  @GET
  @Path("/{id:[0-9][0-9]*}")
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public Response findById(@PathParam("id") long id) {
    DurationLogger perf = DurationLogger.get(logger).start("Find session id " + id);
    TypedQuery<Session> findByIdQuery = em.createQuery(
        "SELECT DISTINCT e FROM Session e LEFT JOIN FETCH e.chronometers WHERE e.id = :entityId ORDER BY e.id",
        Session.class);
    findByIdQuery.setParameter("entityId", id);
    Session entity;
    try {
      entity = findByIdQuery.getSingleResult();
    } catch (NoResultException nre) {
      entity = null;
    }
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
    List<Session> allSessions = listAllSessions(null, null)
            .collect(Collectors.toList());
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
  public SessionDTO findSessionByName(@QueryParam("name") String name) {
    DurationLogger perf = DurationLogger.get(logger).start("Find session named " + name);
    TypedQuery<Session> findByNameQuery = em.createQuery(
        "SELECT DISTINCT e FROM Session e LEFT JOIN FETCH e.chronometers WHERE e.name = :name ORDER BY e.id",
        Session.class);
    findByNameQuery.setParameter("name", name);
    Session entity;
    try {
      entity = findByNameQuery.getSingleResult();
    } catch (NoResultException nre) {
      entity = null;
    }
    perf.end();
    SessionDTO dto = SessionDTO.fromSession(entity);
    return dto;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public List<SessionDTO> listAll(@QueryParam("start") Integer startPosition, @QueryParam("max") Integer maxResult) {
    DurationLogger perf = DurationLogger.get(logger).start("List sessions");
    List<SessionDTO> sessions = listAllSessions(startPosition, maxResult)
            .map(SessionDTO::fromSession)
            .collect(Collectors.toList());
    perf.end();
    return sessions;
  }

  private Stream<Session> listAllSessions(Integer startPosition, Integer maxResult) {
    TypedQuery<Session> findAllQuery = em
        .createQuery("SELECT DISTINCT e FROM Session e LEFT JOIN FETCH e.chronometers ORDER BY e.id", Session.class);
    if (startPosition != null) {
      findAllQuery.setFirstResult(startPosition);
    }
    if (maxResult != null) {
      findAllQuery.setMaxResults(maxResult);
    }
    return findAllQuery.getResultStream();
  }

  @POST
  @Path("{sessionId:[0-9][0-9]*}/addChronometer")
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public Response addChronometer(@PathParam("sessionId") long sessionId, @QueryParam("chronoId") Long chronoId,
                                 @QueryParam("index") Integer index) {
    DurationLogger perf = DurationLogger.get(logger).start("Add chrono " + chronoId + " to session " + sessionId);
    Session session = em.find(Session.class, sessionId);
    if (session == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    Chronometer chronometer = em.find(Chronometer.class, chronoId);
    if (chronometer == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    chronometer.getSessions().add(session);
    if (index != null) {
      session.addChronometer(chronometer, index);
    } else {
      session.addChronometer(chronometer);
    }
    em.persist(session);
    for (Chronometer c : session.getChronometers()) {
      em.persist(c);
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
    DurationLogger perf = DurationLogger.get(logger).start("Add pilot " + pilotId + " to session " + sessionId);
    Session session = em.find(Session.class, sessionId);
    if (session == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    Pilot pilot = em.find(Pilot.class, pilotId);
    if (pilot == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    session.getPilots().add(pilot);
    pilot.getSessions().add(session);
    em.persist(session);
    em.persist(pilot);
    perf.end();
    return Response.ok(session).build();
  }

  @POST
  @Path("{sessionId:[0-9][0-9]*}/start")
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public Response start(Ping start, @PathParam("sessionId") long sessionId) {
    DurationLogger perf = DurationLogger.get(logger).start("Start session " + sessionId);
    Session session = em.find(Session.class, sessionId);
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
    DurationLogger perf = DurationLogger.get(logger).start("End session " + sessionId + " @ " + end.getInstant());
    Session session = em.find(Session.class, sessionId);
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
    em.persist(session);
  }

  private boolean intersect(Session otherSession, Session session) {
    if (otherSession.getStart().compareTo(session.getStart()) >= 0 && otherSession.getStart().compareTo(otherSession.getEnd()) <= 0) {
      return true;
    }
    if (otherSession.getEnd().compareTo(otherSession.getStart()) >= 0 && otherSession.getEnd().compareTo(otherSession.getEnd()) <= 0) {
      return true;
    }
    return false;
  }

  private void endSession(Session session, Instant end) {
    if (session.getEnd().isBefore(end)) {
      logger.warn("Session ID=" + session.id + " has been stopped after expected end");
    }
    session.setEnd(end);
    session.setCurrent(false);
    em.persist(session);
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
      em.persist(ping);
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
    DurationLogger perf = DurationLogger.get(logger).start("Update session id " + entity.id);
    if (id != entity.id) {
      perf.end();
      return Response.status(Status.CONFLICT).entity(entity).build();
    }
    Session session = em.find(Session.class, id);
    if (session == null) {
      perf.end();
      return Response.status(Status.NOT_FOUND).build();
    }

    if (entity.getLocation() != null && entity.getLocation().id > 0) {
      Location location = em.find(Location.class, entity.getLocation().id);
      session.setLocation(location);
    }
    if (entity.getEvent() != null && entity.getEvent().id > 0) {
      Event event = em.find(Event.class, entity.getEvent().id);
      session.setEvent(event);
    }
    if (entity.getPilots() != null && entity.getPilots().size() > 0) {
      Set<Pilot> pilotsToSet = new HashSet<>();
      for (Pilot p : entity.getPilots()) {
        if (p != null && p.id > 0) {
          Pilot pilot = em.find(Pilot.class, p.id);
          pilotsToSet.add(pilot);
        }
      }
      session.setPilots(pilotsToSet);
    }
    if (entity.getChronometers() != null && entity.getChronometers().size() > 0) {
      logger.warn("Session ID=" + session.id + " Chronometers list has not been updated to avoid messing order");
      List<Chronometer> chronosToSet = new ArrayList<>();
      for (Chronometer c : entity.getChronometers()) {
        if (c != null && c.id > 0) {
          Chronometer chrono = em.find(Chronometer.class, c.id);
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
      em.persist(session);
    } catch (OptimisticLockException e) {
      return Response.status(Response.Status.CONFLICT).entity(e.getEntity()).build();
    }

    perf.end();
    return Response.noContent().build();
  }
}
