package org.trd.app.teknichrono.rest;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;

import org.jboss.logging.Logger;
import org.trd.app.teknichrono.business.ChronoManager;
import org.trd.app.teknichrono.model.Chronometer;
import org.trd.app.teknichrono.model.Event;
import org.trd.app.teknichrono.model.Pilot;
import org.trd.app.teknichrono.model.Ping;
import org.trd.app.teknichrono.model.Session;
import org.trd.app.teknichrono.model.SessionType;
import org.trd.app.teknichrono.rest.dto.SessionDTO;
import org.trd.app.teknichrono.util.DurationLogger;

/**
 * 
 */
@Stateless
@Path("/sessions")
public class SessionEndpoint {

  private Logger logger = Logger.getLogger(SessionEndpoint.class);

  @PersistenceContext(unitName = "teknichrono-persistence-unit")
  private EntityManager em;

  @POST
  @Consumes("application/json")
  public Response create(Session entity) {
    DurationLogger perf = DurationLogger.get(logger).start("Create session " + entity.getName());
    em.persist(entity);
    Response response = Response
        .created(UriBuilder.fromResource(SessionEndpoint.class).path(String.valueOf(entity.getId())).build()).build();
    perf.end();
    return response;
  }

  @DELETE
  @Path("/{id:[0-9][0-9]*}")
  public Response deleteById(@PathParam("id") int id) {
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
  @Produces("application/json")
  public Response findById(@PathParam("id") int id) {
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
    SessionDTO dto = new SessionDTO(entity);
    return Response.ok(dto).build();
  }

  @GET
  @Path("/name")
  @Produces("application/json")
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
    SessionDTO dto = new SessionDTO(entity);
    return dto;
  }

  @GET
  @Produces("application/json")
  public List<SessionDTO> listAll(@QueryParam("start") Integer startPosition, @QueryParam("max") Integer maxResult) {
    DurationLogger perf = DurationLogger.get(logger).start("List sessions");
    TypedQuery<Session> findAllQuery = em
        .createQuery("SELECT DISTINCT e FROM Session e LEFT JOIN FETCH e.chronometers ORDER BY e.id", Session.class);
    if (startPosition != null) {
      findAllQuery.setFirstResult(startPosition);
    }
    if (maxResult != null) {
      findAllQuery.setMaxResults(maxResult);
    }
    final List<Session> results = findAllQuery.getResultList();
    final List<SessionDTO> converted = SessionDTO.convert(results);
    perf.end();
    return converted;
  }

  @POST
  @Path("{sessionId:[0-9][0-9]*}/addChronometer")
  @Produces("application/json")
  public Response addChronometer(@PathParam("sessionId") int sessionId, @QueryParam("chronoId") Integer chronoId,
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

    return Response.ok(session).build();
  }

  @POST
  @Path("{sessionId:[0-9][0-9]*}/addPilot")
  @Produces("application/json")
  public Response addPilot(@PathParam("sessionId") int sessionId, @QueryParam("pilotId") Integer pilotId) {
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
  @Produces("application/json")
  public Response start(Ping start, @PathParam("sessionId") int sessionId) {
    DurationLogger perf = DurationLogger.get(logger).start("Start session " + sessionId);
    Session session = em.find(Session.class, sessionId);
    if (session == null) {
      return Response.status(Status.NOT_FOUND).build();
    }

    startSession(session, start.getDateTime());

    if (session.getSessionType() == SessionType.RACE) {
      startRace(session, start.getDateTime());
    }
    SessionDTO dto = new SessionDTO(session);
    perf.end();

    return Response.ok(dto).build();
  }

  @POST
  @Path("{sessionId:[0-9][0-9]*}/end")
  @Produces("application/json")
  public Response end(Ping end, @PathParam("sessionId") int sessionId) {
    DurationLogger perf = DurationLogger.get(logger).start("End session " + sessionId);
    Session session = em.find(Session.class, sessionId);
    if (session == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    endSession(session, new Date(end.getDateTime().getTime()));
    SessionDTO dto = new SessionDTO(session);
    perf.end();

    return Response.ok(dto).build();
  }

  private void startSession(Session session, Timestamp timestamp) {
    Date start = new Date(timestamp.getTime());
    // Stop all other sessions of the event
    Event event = session.getEvent();
    if (event != null) {
      for (Session otherSession : event.getSessions()) {
        if (otherSession.getId() != session.getId() && otherSession.isCurrent()) {
          endSession(otherSession, start);
        }
      }
    }
    // Start this one
    session.setStart(start);
    session.setCurrent(true);
    em.persist(session);
  }

  private void endSession(Session otherSession, Date start) {
    otherSession.setEnd(start);
    otherSession.setCurrent(false);
    em.persist(otherSession);
  }

  private void startRace(Session session, Timestamp timestamp) {
    Chronometer chronometer = session.getChronometers().get(0);
    Set<Pilot> pilots = session.getPilots();
    ChronoManager cm = new ChronoManager(em);
    for (Pilot pilot : pilots) {
      Ping ping = new Ping();
      ping.setDateTime(timestamp);
      ping.setBeacon(pilot.getCurrentBeacon());
      ping.setChrono(chronometer);
      em.persist(ping);
      cm.addPing(ping);
    }
  }

  @PUT
  @Path("/{id:[0-9][0-9]*}")
  @Consumes("application/json")
  public Response update(@PathParam("id") int id, Session entity) {
    if (entity == null) {
      return Response.status(Status.BAD_REQUEST).build();
    }
    if (id != entity.getId()) {
      return Response.status(Status.CONFLICT).entity(entity).build();
    }
    if (em.find(Session.class, id) == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    try {
      entity = em.merge(entity);
    } catch (OptimisticLockException e) {
      return Response.status(Response.Status.CONFLICT).entity(e.getEntity()).build();
    }

    return Response.noContent().build();
  }
}
