package org.trd.app.teknichrono.rest;

import org.trd.app.teknichrono.model.dto.EventDTO;
import org.trd.app.teknichrono.model.jpa.Event;
import org.trd.app.teknichrono.model.jpa.Session;
import org.trd.app.teknichrono.model.repository.EventRepository;

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
