package org.trd.app.teknichrono.rest;

import org.trd.app.teknichrono.model.dto.LocationDTO;
import org.trd.app.teknichrono.model.jpa.Location;
import org.trd.app.teknichrono.model.jpa.Session;

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
@Path("/locations")
public class LocationEndpoint {

  EntityManager em;

  @Inject
  public LocationEndpoint(EntityManager em) {
    this.em = em;
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Transactional
  public Response create(Location entity) {
    em.persist(entity);
    return Response
        .created(UriBuilder.fromResource(LocationEndpoint.class).path(String.valueOf(entity.id)).build()).build();
  }

  @DELETE
  @Path("/{id:[0-9][0-9]*}")
  @Transactional
  public Response deleteById(@PathParam("id") long id) {
    Location entity = em.find(Location.class, id);
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
    TypedQuery<Location> findByIdQuery = em.createQuery(
        "SELECT DISTINCT e FROM Location e LEFT JOIN FETCH e.sessions WHERE e.id = :entityId ORDER BY e.id",
        Location.class);
    findByIdQuery.setParameter("entityId", id);
    Location entity;
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
  @Transactional
  public Location findLocationByName(@QueryParam("name") String name) {
    TypedQuery<Location> findByNameQuery = em.createQuery(
        "SELECT DISTINCT e FROM Location e LEFT JOIN FETCH e.sessions WHERE e.name = :name ORDER BY e.id",
        Location.class);
    findByNameQuery.setParameter("name", name);
    Location entity;
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
  public List<Location> listAll(@QueryParam("start") Integer startPosition, @QueryParam("max") Integer maxResult) {
    TypedQuery<Location> findAllQuery = em
        .createQuery("SELECT DISTINCT e FROM Location e LEFT JOIN FETCH e.sessions ORDER BY e.id", Location.class);
    if (startPosition != null) {
      findAllQuery.setFirstResult(startPosition);
    }
    if (maxResult != null) {
      findAllQuery.setMaxResults(maxResult);
    }
    final List<Location> results = findAllQuery.getResultList();
    return results;
  }

  @POST
  @Path("{locationId:[0-9][0-9]*}/addSession")
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public Response addSession(@PathParam("locationId") long locationId, @QueryParam("sessionId") Long sessionId) {
    Location location = em.find(Location.class, locationId);
    if (location == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    Session session = em.find(Session.class, sessionId);
    if (session == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    session.setLocation(location);
    location.getSessions().add(session);
    em.persist(location);
    em.persist(session);
    LocationDTO dto = LocationDTO.fromLocation(location);
    return Response.ok(dto).build();
  }

  @PUT
  @Path("/{id:[0-9][0-9]*}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Transactional
  public Response update(@PathParam("id") long id, Location entity) {
    if (entity == null) {
      return Response.status(Status.BAD_REQUEST).build();
    }
    if (id != entity.id) {
      return Response.status(Status.CONFLICT).entity(entity).build();
    }
    if (em.find(Location.class, id) == null) {
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
