package org.trd.app.teknichrono.rest;

import org.trd.app.teknichrono.model.dto.EventDTO;
import org.trd.app.teknichrono.model.jpa.Event;
import org.trd.app.teknichrono.model.jpa.Session;
import org.trd.app.teknichrono.model.repository.EventRepository;

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
import java.util.List;

@Path("/events")
public class EventEndpoint {

  private final EntityEndpoint<Event, EventDTO> entityEndpoint;
  private final EventRepository eventRepository;


  @Inject
  public EventEndpoint(EventRepository eventRepository) {
    this.entityEndpoint = new EntityEndpoint<>(eventRepository);
    this.eventRepository = eventRepository;
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Transactional
  public Response create(EventDTO entity) {
    return entityEndpoint.create(entity, entity.getName());
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
  public Response findEventByName(@QueryParam("name") String name) {
    return entityEndpoint.findByField("name", name);
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public List<EventDTO> listAll(@QueryParam("page") Integer pageIndex, @QueryParam("pageSize") Integer pageSize) {
    return entityEndpoint.listAll(pageIndex, pageSize);
  }

  @POST
  @Path("{eventId:[0-9][0-9]*}/addSession")
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public Response addSession(@PathParam("eventId") long eventId, @QueryParam("sessionId") Long sessionId) {
    return entityEndpoint.addToOneToManyField(eventId, sessionId, eventRepository.getSessionRepository(),
        Session::setEvent, Event::getSessions);
  }

  @PUT
  @Path("/{id:[0-9][0-9]*}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Transactional
  public Response update(@PathParam("id") long id, EventDTO dto) {
    return entityEndpoint.update(id, dto);
  }
}
