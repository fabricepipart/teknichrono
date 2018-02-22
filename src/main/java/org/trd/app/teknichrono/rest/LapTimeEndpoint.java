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

import org.trd.app.teknichrono.business.LapTimeManager;
import org.trd.app.teknichrono.model.LapTime;
import org.trd.app.teknichrono.rest.dto.LapTimeDTO;
import org.trd.app.teknichrono.rest.sql.OrderByClauseBuilder;
import org.trd.app.teknichrono.rest.sql.WhereClauseBuilder;

/**
 * 
 */
@Stateless
@Path("/laptimes")
public class LapTimeEndpoint {
  @PersistenceContext(unitName = "teknichrono-persistence-unit")
  private EntityManager em;

  @POST
  @Consumes("application/json")
  public Response create(LapTimeDTO dto) {
    LapTime entity = dto.fromDTO(null, em);
    em.persist(entity);
    return Response.created(UriBuilder.fromResource(LapTimeEndpoint.class).path(String.valueOf(entity.getId())).build())
        .build();
  }

  @DELETE
  @Path("/{id:[0-9][0-9]*}")
  public Response deleteById(@PathParam("id") int id) {
    LapTime entity = em.find(LapTime.class, id);
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
    TypedQuery<LapTime> findByIdQuery = em.createQuery("SELECT DISTINCT l FROM LapTime"
        + " l LEFT JOIN FETCH l.pilot LEFT JOIN FETCH l.event LEFT JOIN FETCH l.intermediates"
        + " WHERE l.id = :entityId ORDER BY l.id", LapTime.class);
    findByIdQuery.setParameter("entityId", id);
    LapTime entity;
    try {
      entity = findByIdQuery.getSingleResult();
    } catch (NoResultException nre) {
      entity = null;
    }
    if (entity == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    LapTimeDTO dto = new LapTimeDTO(entity);
    return Response.ok(dto).build();
  }

  @GET
  @Produces("application/json")
  public List<LapTimeDTO> listAll(@QueryParam("pilotId") Integer pilotId, @QueryParam("eventId") Integer eventId,
      @QueryParam("start") Integer startPosition, @QueryParam("max") Integer maxResult) {
    WhereClauseBuilder whereClauseBuilder = new WhereClauseBuilder();
    if (pilotId != null) {
      whereClauseBuilder.addEqualsClause("l.pilot.id", "pilotId", pilotId);
    }
    if (eventId != null) {
      whereClauseBuilder.addEqualsClause("l.event.id", "eventId", eventId);
    }
    OrderByClauseBuilder orderByClauseBuilder = new OrderByClauseBuilder();
    // Necessary to have the lapTimeManager.convert working
    orderByClauseBuilder.add("l.startDate");

    TypedQuery<LapTime> findAllQuery = em.createQuery("SELECT DISTINCT l FROM LapTime l"
        + " LEFT JOIN FETCH l.pilot LEFT JOIN FETCH l.event LEFT JOIN FETCH l.intermediates"
        + whereClauseBuilder.build() + orderByClauseBuilder.build(), LapTime.class);
    if (startPosition != null) {
      findAllQuery.setFirstResult(startPosition);
    }
    if (maxResult != null) {
      findAllQuery.setMaxResults(maxResult);
    }
    whereClauseBuilder.applyClauses(findAllQuery);

    final List<LapTime> searchResults = findAllQuery.getResultList();

    LapTimeManager lapTimeManager = new LapTimeManager();
    final List<LapTimeDTO> results = lapTimeManager.convert(searchResults);
    if (pilotId == null) {
      lapTimeManager.orderByDuration(results);
    }
    if (eventId != null) {
      lapTimeManager.keepOnlyBest(results);
    }

    return results;
  }

  @PUT
  @Path("/{id:[0-9][0-9]*}")
  @Consumes("application/json")
  public Response update(@PathParam("id") int id, LapTimeDTO dto) {
    if (dto == null) {
      return Response.status(Status.BAD_REQUEST).build();
    }
    if (id != dto.getId()) {
      return Response.status(Status.CONFLICT).entity(dto).build();
    }
    LapTime entity = em.find(LapTime.class, id);
    if (entity == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    entity = dto.fromDTO(entity, em);
    try {
      entity = em.merge(entity);
    } catch (OptimisticLockException e) {
      return Response.status(Status.CONFLICT).entity(e.getEntity()).build();
    }
    return Response.noContent().build();
  }
}
