package org.trd.app.teknichrono.rest;

import org.jboss.logging.Logger;
import org.trd.app.teknichrono.model.dto.EventDTO;
import org.trd.app.teknichrono.model.jpa.Event;
import org.trd.app.teknichrono.model.jpa.Session;
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
import java.util.List;

/**
 *
 */
@Path("/events")
public class EventEndpoint {

  EntityManager em;

  @Inject
  public EventEndpoint(EntityManager em) {
    this.em = em;
  }

  private Logger logger = Logger.getLogger(EventEndpoint.class);

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Transactional
  public Response create(Event entity) {
    em.persist(entity);
    return Response.created(UriBuilder.fromResource(EventEndpoint.class).path(String.valueOf(entity.id)).build())
        .build();
  }

  @DELETE
  @Path("/{id:[0-9][0-9]*}")
  @Transactional
  public Response deleteById(@PathParam("id") long id) {
    Event entity = em.find(Event.class, id);
    if (entity == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    em.remove(entity);
    return Response.noContent().build();
  }

  @GET
  @Path("/{id:[0-9][0-9]*}")
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public Response findById(@PathParam("id") long id) {
    TypedQuery<Event> findByIdQuery = em.createQuery(
        "SELECT DISTINCT e FROM Event e LEFT JOIN FETCH e.sessions WHERE e.id = :entityId ORDER BY e.id", Event.class);
    findByIdQuery.setParameter("entityId", id);
    Event entity;
    try {
      entity = findByIdQuery.getSingleResult();
    } catch (NoResultException nre) {
      entity = null;
    }
    if (entity == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    return Response.ok(entity).build();
  }

  @GET
  @Path("/name")
  @Produces(MediaType.APPLICATION_JSON)
  public Event findEventByName(@QueryParam("name") String name) {
    TypedQuery<Event> findByNameQuery = em.createQuery(
        "SELECT DISTINCT e FROM Event e LEFT JOIN FETCH e.sessions WHERE e.name = :name ORDER BY e.id", Event.class);
    findByNameQuery.setParameter("name", name);
    Event entity;
    try {
      entity = findByNameQuery.getSingleResult();
    } catch (NoResultException nre) {
      entity = null;
    }
    return entity;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public List<Event> listAll(@QueryParam("start") Integer startPosition, @QueryParam("max") Integer maxResult) {
    TypedQuery<Event> findAllQuery = em
        .createQuery("SELECT DISTINCT e FROM Event e LEFT JOIN FETCH e.sessions ORDER BY e.id", Event.class);
    if (startPosition != null) {
      findAllQuery.setFirstResult(startPosition);
    }
    if (maxResult != null) {
      findAllQuery.setMaxResults(maxResult);
    }
    final List<Event> results = findAllQuery.getResultList();
    return results;
  }

  @POST
  @Path("{eventId:[0-9][0-9]*}/addSession")
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public Response addSession(@PathParam("eventId") long eventId, @QueryParam("sessionId") Long sessionId) {
    try (DurationLogger dl = new DurationLogger(logger, "Add session session ID=" + sessionId + " to event ID=" + eventId)) {
      Event event = em.find(Event.class, eventId);
      if (event == null) {
        return Response.status(Status.NOT_FOUND).build();
      }
      Session session = em.find(Session.class, sessionId);
      if (session == null) {
        return Response.status(Status.NOT_FOUND).build();
      }
      session.setEvent(event);
      event.getSessions().add(session);
      em.persist(event);
      em.persist(session);

      EventDTO dto = new EventDTO(event);
      return Response.ok(dto).build();
    }
  }

  @PUT
  @Path("/{id:[0-9][0-9]*}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Transactional
  public Response update(@PathParam("id") long id, Event entity) {
    if (entity == null) {
      return Response.status(Status.BAD_REQUEST).build();
    }
    if (id != entity.id) {
      return Response.status(Status.CONFLICT).entity(entity).build();
    }
    if (em.find(Event.class, id) == null) {
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
