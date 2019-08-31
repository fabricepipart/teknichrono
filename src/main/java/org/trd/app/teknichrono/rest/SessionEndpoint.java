package org.trd.app.teknichrono.rest;

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

import javax.inject.Inject;
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
import java.util.List;
import java.util.stream.Collectors;

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
      List<Session> allSessions = sessionRepository.findAll(null, null).collect(Collectors.toList());
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
