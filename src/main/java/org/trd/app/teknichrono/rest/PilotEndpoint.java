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

import org.trd.app.teknichrono.model.Beacon;
import org.trd.app.teknichrono.model.Pilot;

/**
 * 
 */
@Stateless
@Path("/pilots")
public class PilotEndpoint {
  @PersistenceContext(unitName = "teknichrono-persistence-unit")
  private EntityManager em;

  @POST
  @Consumes("application/json")
  public Response create(Pilot entity) {
    em.persist(entity);
    return Response.created(UriBuilder.fromResource(PilotEndpoint.class).path(String.valueOf(entity.getId())).build())
        .build();
  }

  @DELETE
  @Path("/{id:[0-9][0-9]*}")
  public Response deleteById(@PathParam("id") int id) {
    Pilot entity = em.find(Pilot.class, id);
    if (entity == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    em.remove(entity);
    return Response.noContent().build();
  }

  @GET
  @Path("/{id:[0-9][0-9]*}")
  @Produces("application/json")
  public Response findById(@PathParam("id") int id) {
    TypedQuery<Pilot> findByIdQuery = em.createQuery(
        "SELECT DISTINCT p FROM Pilot p LEFT JOIN FETCH p.currentBeacon WHERE p.id = :entityId ORDER BY p.id",
        Pilot.class);
    findByIdQuery.setParameter("entityId", id);
    Pilot entity;
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
  public Response findByName(@QueryParam("firstname") String firstname, @QueryParam("lastname") String lastname) {
    TypedQuery<Pilot> findByIdQuery = em
        .createQuery("SELECT DISTINCT p FROM Pilot p LEFT JOIN FETCH p.currentBeacon WHERE"
            + " p.firstName = :firstname AND p.lastName = :lastname ORDER BY p.id", Pilot.class);
    findByIdQuery.setParameter("firstname", firstname);
    findByIdQuery.setParameter("lastname", lastname);
    Pilot entity;
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
  @Produces("application/json")
  public List<Pilot> listAll(@QueryParam("start") Integer startPosition, @QueryParam("max") Integer maxResult) {
    TypedQuery<Pilot> findAllQuery = em
        .createQuery("SELECT DISTINCT p FROM Pilot p LEFT JOIN FETCH p.currentBeacon ORDER BY p.id", Pilot.class);
    if (startPosition != null) {
      findAllQuery.setFirstResult(startPosition);
    }
    if (maxResult != null) {
      findAllQuery.setMaxResults(maxResult);
    }
    final List<Pilot> results = findAllQuery.getResultList();
    return results;
  }

  @POST
  @Path("{pilotId:[0-9][0-9]*}/setBeacon")
  @Produces("application/json")
  public Response associateBeacon(@PathParam("pilotId") int pilotId, @QueryParam("beaconId") int beaconId) {
    Pilot pilot = em.find(Pilot.class, pilotId);
    if (pilot == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    Beacon beacon = em.find(Beacon.class, beaconId);
    if (beacon == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    pilot.setCurrentBeacon(beacon);
    em.persist(pilot);
    em.persist(beacon);
    return Response.ok(pilot).build();
  }

  @PUT
  @Path("/{id:[0-9][0-9]*}")
  @Consumes("application/json")
  public Response update(@PathParam("id") int id, Pilot entity) {
    if (entity == null) {
      return Response.status(Status.BAD_REQUEST).build();
    }
    if (id != entity.getId()) {
      return Response.status(Status.CONFLICT).entity(entity).build();
    }
    if (em.find(Pilot.class, id) == null) {
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
