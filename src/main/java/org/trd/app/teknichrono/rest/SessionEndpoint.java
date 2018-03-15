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
import org.trd.app.teknichrono.model.Session;

/**
 * 
 */
@Stateless
@Path("/sessions")
public class SessionEndpoint {
  @PersistenceContext(unitName = "teknichrono-persistence-unit")
  private EntityManager em;

  @POST
  @Consumes("application/json")
  public Response create(Session entity) {
    em.persist(entity);
    return Response.created(UriBuilder.fromResource(SessionEndpoint.class).path(String.valueOf(entity.getId())).build())
        .build();
  }

  @DELETE
  @Path("/{id:[0-9][0-9]*}")
  public Response deleteById(@PathParam("id") int id) {
    Session entity = em.find(Session.class, id);
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
    return Response.ok(entity).build();
  }

  @GET
  @Path("/name")
  @Produces("application/json")
  public Session findSessionByName(@QueryParam("name") String name) {
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
    return entity;
  }

  @GET
  @Produces("application/json")
  public List<Session> listAll(@QueryParam("start") Integer startPosition, @QueryParam("max") Integer maxResult) {
    TypedQuery<Session> findAllQuery = em
        .createQuery("SELECT DISTINCT e FROM Session e LEFT JOIN FETCH e.chronometers ORDER BY e.id", Session.class);
    if (startPosition != null) {
      findAllQuery.setFirstResult(startPosition);
    }
    if (maxResult != null) {
      findAllQuery.setMaxResults(maxResult);
    }
    final List<Session> results = findAllQuery.getResultList();
    return results;
  }

  @POST
  @Path("{sessionId:[0-9][0-9]*}/addChronometer")
  @Produces("application/json")
  public Response addChronometer(@PathParam("sessionId") int sessionId, @QueryParam("chronoId") Integer chronoId,
      @QueryParam("index") Integer index) {
    Session session = em.find(Session.class, sessionId);
    if (session == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    Chronometer chronometer = em.find(Chronometer.class, chronoId);
    if (chronometer == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    chronometer.setSession(session);
    if (index != null) {
      chronometer.setChronoIndex(index);
    }
    session.addChronometer(chronometer);
    em.persist(session);
    for (Chronometer c : session.getChronometers()) {
      em.persist(c);
    }

    return Response.ok(session).build();
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
