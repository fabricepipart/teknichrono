package org.trd.app.teknichrono.rest;

import org.jboss.logging.Logger;
import org.trd.app.teknichrono.model.dto.EventDTO;
import org.trd.app.teknichrono.model.jpa.Event;
import org.trd.app.teknichrono.model.jpa.EventRepository;
import org.trd.app.teknichrono.model.jpa.Session;
import org.trd.app.teknichrono.model.jpa.SessionRepository;
import org.trd.app.teknichrono.util.DurationLogger;

import javax.inject.Inject;
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
import java.util.List;
import java.util.stream.Collectors;

@Path("/events")
public class EventEndpoint {

  private static final Logger LOGGER = Logger.getLogger(EventEndpoint.class);

  private final EventRepository eventRepository;

  private final SessionRepository sessionRepository;

  @Inject
  public EventEndpoint(EventRepository eventRepository, SessionRepository sessionRepository) {
    this.eventRepository = eventRepository;
    this.sessionRepository = sessionRepository;
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Transactional
  public Response create(Event entity) {
    eventRepository.persist(entity);
    return Response.created(UriBuilder.fromResource(EventEndpoint.class).path(String.valueOf(entity.id)).build())
        .build();
  }

  @DELETE
  @Path("/{id:[0-9][0-9]*}")
  @Transactional
  public Response deleteById(@PathParam("id") long id) {
    Event entity = eventRepository.findById(id);
    if (entity == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    eventRepository.delete(entity);
    return Response.noContent().build();
  }

  @GET
  @Path("/{id:[0-9][0-9]*}")
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public Response findById(@PathParam("id") long id) {
    Event entity = eventRepository.findById(id);
    if (entity == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    return Response.ok(entity).build();
  }

  @GET
  @Path("/name")
  @Produces(MediaType.APPLICATION_JSON)
  public Response findEventByName(@QueryParam("name") String name) {
    Event entity = eventRepository.findByName(name);
    if (entity == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    return Response.ok(entity).build();
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public List<EventDTO> listAll(@QueryParam("start") Integer startPosition, @QueryParam("max") Integer maxResult) {
    return eventRepository.findAll()
            .page(Paging.from(startPosition, maxResult))
            .stream()
            .map(EventDTO::fromEvent)
            .collect(Collectors.toList());
  }

  @POST
  @Path("{eventId:[0-9][0-9]*}/addSession")
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public Response addSession(@PathParam("eventId") long eventId, @QueryParam("sessionId") Long sessionId) {
    try (DurationLogger dl = new DurationLogger(LOGGER, "Add session session ID=" + sessionId + " to event ID=" + eventId)) {
      Event event = eventRepository.findById(eventId);
      if (event == null) {
        return Response.status(Status.NOT_FOUND).build();
      }
      Session session = sessionRepository.findById(sessionId);
      if (session == null) {
        return Response.status(Status.NOT_FOUND).build();
      }
      session.setEvent(event);
      event.getSessions().add(session);
      eventRepository.persist(event);
      sessionRepository.persist(session);
      EventDTO dto = EventDTO.fromEvent(event);
      return Response.ok(dto).build();
    }
  }

  @PUT
  @Path("/{id:[0-9][0-9]*}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Transactional
  public Response update(@PathParam("id") long id, Event dto) {
    if (dto == null) {
      return Response.status(Status.BAD_REQUEST).build();
    }
    if (id != dto.id) {
      return Response.status(Status.CONFLICT).entity(dto).build();
    }
    if (eventRepository.findById(id) == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    try {
      eventRepository.persist(dto);
    } catch (OptimisticLockException e) {
      return Response.status(Response.Status.CONFLICT).entity(e.getEntity()).build();
    }

    return Response.noContent().build();
  }
}
