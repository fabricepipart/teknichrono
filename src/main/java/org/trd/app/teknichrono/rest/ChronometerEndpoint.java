package org.trd.app.teknichrono.rest;

import java.util.List;

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

import org.trd.app.teknichrono.model.Chronometer;
import org.trd.app.teknichrono.model.Ping;

/**
 * 
 */
@Stateless
@Path("/chronometers")
public class ChronometerEndpoint {
  @PersistenceContext(unitName = "teknichrono-persistence-unit")
  private EntityManager em;

  @POST
  @Consumes("application/json")
  public Response create(Chronometer entity) {
    em.persist(entity);
    return Response
        .created(UriBuilder.fromResource(ChronometerEndpoint.class).path(String.valueOf(entity.getId())).build())
        .build();
  }

  @DELETE
  @Path("/{id:[0-9][0-9]*}")
  public Response deleteById(@PathParam("id") int id) {
    Chronometer entity = em.find(Chronometer.class, id);
    if (entity == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    List<Ping> pings = entity.getPings();
    if (pings != null) {
      for (Ping ping : pings) {
        ping.setChrono(null);
        em.persist(ping);
      }
    }
    em.remove(entity);
    return Response.noContent().build();
  }

  @GET
  @Path("/{id:[0-9][0-9]*}")
  @Produces("application/json")
  public Response findById(@PathParam("id") int id) {
    TypedQuery<Chronometer> findByIdQuery = em
        .createQuery("SELECT DISTINCT c FROM Chronometer c WHERE c.id = :entityId ORDER BY c.id", Chronometer.class);
    findByIdQuery.setParameter("entityId", id);
    Chronometer entity;
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
  @Produces("application/json")
  public Response findChronometerByName(@QueryParam("name") String name) {
    TypedQuery<Chronometer> findByNameQuery = em
        .createQuery("SELECT DISTINCT c FROM Chronometer c WHERE c.name = :name ORDER BY c.id", Chronometer.class);
    findByNameQuery.setParameter("name", name);
    Chronometer entity;
    try {
      entity = findByNameQuery.getSingleResult();
    } catch (NoResultException nre) {
      entity = null;
    }
    if (entity == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    return Response.ok(entity).build();
  }

  @GET
  @Produces("application/json")
  public List<Chronometer> listAll(@QueryParam("start") Integer startPosition, @QueryParam("max") Integer maxResult) {
    TypedQuery<Chronometer> findAllQuery = em.createQuery("SELECT DISTINCT c FROM Chronometer c ORDER BY c.id",
        Chronometer.class);
    if (startPosition != null) {
      findAllQuery.setFirstResult(startPosition);
    }
    if (maxResult != null) {
      findAllQuery.setMaxResults(maxResult);
    }
    final List<Chronometer> results = findAllQuery.getResultList();
    return results;
  }

  @PUT
  @Path("/{id:[0-9][0-9]*}")
  @Consumes("application/json")
  public Response update(@PathParam("id") int id, Chronometer entity) {
    if (entity == null) {
      return Response.status(Status.BAD_REQUEST).build();
    }
    if (id != entity.getId()) {
      return Response.status(Status.CONFLICT).entity(entity).build();
    }
    if (em.find(Chronometer.class, id) == null) {
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
