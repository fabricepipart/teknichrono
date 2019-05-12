package org.trd.app.teknichrono.rest;

import java.util.List;

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

import org.jboss.logging.Logger;
import org.trd.app.teknichrono.model.jpa.Chronometer;
import org.trd.app.teknichrono.model.jpa.Ping;
import org.trd.app.teknichrono.model.jpa.Session;
import org.trd.app.teknichrono.model.dto.ChronometerDTO;
import org.trd.app.teknichrono.util.DurationLogger;

/**
 * 
 */
@Path("/chronometers")
public class ChronometerEndpoint {
  private Logger logger = Logger.getLogger(ChronometerEndpoint.class);

  EntityManager em;

  @Inject
  public ChronometerEndpoint(EntityManager em) {
    this.em = em;
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Transactional
  public Response create(Chronometer entity) {
    try(DurationLogger dl = new DurationLogger(logger, "Create chronometer " + entity.getName())) {
        em.persist(entity);
        return Response
            .created(UriBuilder.fromResource(ChronometerEndpoint.class).path(String.valueOf(entity.id)).build())
            .build();
    }
  }

  @DELETE
  @Path("/{id:[0-9][0-9]*}")
  public Response deleteById(@PathParam("id") long id) {
    try(DurationLogger dl = new DurationLogger(logger, "Delete chronometer ID=" + id)) {
      Chronometer entity = em.find(Chronometer.class, id);
      if (entity == null) {
        return Response.status(Status.NOT_FOUND).build();
      }
      for (Session s : entity.getSessions()) {
        s.getChronometers().remove(entity);
      }
      List<Ping> pings = entity.getPings();
      if (pings != null) {
        for (Ping ping : pings) {
          ping.setChrono(null);
          em.persist(ping);
        }
      }
      em.remove(entity);
    }
    return Response.noContent().build();
  }

  @GET
  @Path("/{id:[0-9][0-9]*}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response findById(@PathParam("id") long id) {
    try(DurationLogger dl = new DurationLogger(logger, "Find chronometer ID=" + id)) {
      TypedQuery<Chronometer> findByIdQuery = em
          .createQuery("SELECT DISTINCT c FROM Chronometer c LEFT JOIN FETCH c.pings WHERE c.id = :entityId ORDER BY c.id", Chronometer.class);
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
      ChronometerDTO dto = new ChronometerDTO(entity);
      return Response.ok(dto).build();
    }
  }

  @GET
  @Path("/name")
  @Produces(MediaType.APPLICATION_JSON)
  public Response findChronometerByName(@QueryParam("name") String name) {
    try(DurationLogger dl = new DurationLogger(logger, "Find chronometer " + name)) {
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
      ChronometerDTO dto = new ChronometerDTO(entity);
      return Response.ok(dto).build();
    }
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public List<ChronometerDTO> listAll(@QueryParam("start") Integer startPosition, @QueryParam("max") Integer maxResult) {
    try(DurationLogger dl = new DurationLogger(logger, "Get all chronometers")) {
      TypedQuery<Chronometer> findAllQuery = em.createQuery("SELECT DISTINCT c FROM Chronometer c ORDER BY c.id",
          Chronometer.class);
      if (startPosition != null) {
        findAllQuery.setFirstResult(startPosition);
      }
      if (maxResult != null) {
        findAllQuery.setMaxResults(maxResult);
      }
      final List<Chronometer> results = findAllQuery.getResultList();
      final List<ChronometerDTO> converted = ChronometerDTO.convert(results);
      return converted;
    }
  }

  @PUT
  @Path("/{id:[0-9][0-9]*}")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response update(@PathParam("id") long id, Chronometer entity) {
    try(DurationLogger dl = new DurationLogger(logger, "Update chronometer ID=" + id)) {
      if (entity == null) {
        return Response.status(Status.BAD_REQUEST).build();
      }
      if (id != entity.id) {
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
}
