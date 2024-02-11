package org.trd.app.teknichrono.rest;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import org.jboss.logging.Logger;
import org.trd.app.teknichrono.business.client.SessionManagement;
import org.trd.app.teknichrono.business.client.SessionSelector;
import org.trd.app.teknichrono.model.dto.PingDTO;
import org.trd.app.teknichrono.model.dto.SessionDTO;
import org.trd.app.teknichrono.model.jpa.Pilot;
import org.trd.app.teknichrono.model.jpa.Session;
import org.trd.app.teknichrono.model.repository.LapTimeRepository;
import org.trd.app.teknichrono.model.repository.PingRepository;
import org.trd.app.teknichrono.model.repository.SessionRepository;
import org.trd.app.teknichrono.util.DurationLogger;
import org.trd.app.teknichrono.util.exception.ConflictingIdException;
import org.trd.app.teknichrono.util.exception.NotFoundException;

import java.util.List;

@Path("/sessions")
public class SessionEndpoint {

  private Logger LOGGER = Logger.getLogger(SessionEndpoint.class);

  private final EntityEndpoint<Session, SessionDTO> entityEndpoint;
  private final SessionRepository sessionRepository;
  private final SessionManagement sessionManagement;

  @Inject
  public SessionEndpoint(SessionRepository sessionRepository, LapTimeRepository lapTimeRepository, PingRepository pingRepository) {
    this.sessionRepository = sessionRepository;
    this.sessionManagement = new SessionManagement(lapTimeRepository, pingRepository, sessionRepository);
    this.entityEndpoint = new EntityEndpoint(sessionRepository);
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Transactional
  public Response create(SessionDTO entity) {
    return entityEndpoint.create(entity, String.valueOf(entity.getName()));
  }

  @DELETE
  @Path("/{id:[0-9][0-9]*}")
  @Transactional
  public Response deleteById(@PathParam("id") long id) {
    return entityEndpoint.deleteById(id);
  }

  @GET
  @Path("/{id:[0-9][0-9]*}")
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public Response findById(@PathParam("id") long id) {
    return entityEndpoint.findById(id);
  }

  @GET
  @Path("/name")
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public Response findSessionByName(@QueryParam("name") String name) {
    return entityEndpoint.findByField("name", name);
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public List<SessionDTO> listAll(@QueryParam("page") Integer pageIndex, @QueryParam("pageSize") Integer pageSize) {
    return entityEndpoint.listAll(pageIndex, pageSize);
  }

  @POST
  @Path("{sessionId:[0-9][0-9]*}/addChronometer")
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public Response addChronometer(@PathParam("sessionId") long sessionId, @QueryParam("chronoId") Long chronoId,
                                 @QueryParam("index") Integer index) {
    try (DurationLogger perf = DurationLogger.get(LOGGER).start("Add chrono " + chronoId + " to session " + sessionId)) {
      try {
        Session session = sessionRepository.addChronometerAtIndex(sessionId, chronoId, index);
        SessionDTO dto = SessionDTO.fromSession(session);
        return Response.ok(dto).build();
      } catch (NotFoundException e) {
        return Response.status(Response.Status.NOT_FOUND).build();
      }
    }
  }

  @POST
  @Path("{sessionId:[0-9][0-9]*}/addPilot")
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public Response addPilot(@PathParam("sessionId") long sessionId, @QueryParam("pilotId") Long pilotId) {
    return entityEndpoint.addToManyToManyField(sessionId, pilotId, sessionRepository.getPilotRepository(),
        Pilot::getSessions, Session::getPilots);
  }

  @GET
  @Path("/current")
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public Response findCurrent() {
    try (DurationLogger perf = DurationLogger.get(LOGGER).start("Find current session")) {
      List<Session> allSessions = sessionRepository.findAll(null, null).toList();
      SessionSelector selector = new SessionSelector();
      Session session = selector.pickMostRelevantCurrent(allSessions);
      if (session == null) {
        return Response.status(Status.NOT_FOUND).build();
      }
      SessionDTO dto = SessionDTO.fromSession(session);
      return Response.ok(dto).build();
    }
  }

  @POST
  @Path("{sessionId:[0-9][0-9]*}/start")
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public Response start(PingDTO start, @PathParam("sessionId") long sessionId) {
    try (DurationLogger perf = DurationLogger.get(LOGGER).start("Start session " + sessionId)) {
      Session session = sessionRepository.findById(sessionId);
      if (session == null) {
        return Response.status(Status.NOT_FOUND).build();
      }
      try {
        sessionManagement.startSession(session, start.getInstant());
      } catch (NotFoundException e) {
        return Response.status(Response.Status.NOT_FOUND).build();
      } catch (ConflictingIdException e) {
        return Response.status(Response.Status.CONFLICT).build();
      }
      SessionDTO dto = SessionDTO.fromSession(session);
      return Response.ok(dto).build();
    }
  }

  @POST
  @Path("{sessionId:[0-9][0-9]*}/end")
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public Response end(PingDTO end, @PathParam("sessionId") long sessionId) {
    try (DurationLogger perf = DurationLogger.get(LOGGER).start("End session " + sessionId + " @ " + end.getInstant())) {
      Session session = sessionRepository.findById(sessionId);
      if (session == null) {
        return Response.status(Status.NOT_FOUND).build();
      }
      sessionManagement.endSession(session, end.getInstant());
      SessionDTO dto = SessionDTO.fromSession(session);
      return Response.ok(dto).build();
    }
  }


  @PUT
  @Path("/{id:[0-9][0-9]*}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Transactional
  public Response update(@PathParam("id") long id, SessionDTO dto) {
    return entityEndpoint.update(id, dto);
  }
}
