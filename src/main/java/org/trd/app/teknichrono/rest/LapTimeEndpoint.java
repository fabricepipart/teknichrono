package org.trd.app.teknichrono.rest;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import org.trd.app.teknichrono.model.LapTime;
import org.trd.app.teknichrono.rest.dto.LapTimeDTO;
import org.trd.app.teknichrono.rest.dto.NestedEventDTO;
import org.trd.app.teknichrono.rest.dto.NestedPilotDTO;

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
  public List<LapTimeDTO> listAll(@QueryParam("pilotId") Integer pilotId, @QueryParam("start") Integer startPosition,
      @QueryParam("max") Integer maxResult) {
    String whereClause;
    if (pilotId != null) {
      whereClause = " WHERE l.pilot.id = :entityId";
    } else {
      whereClause = "";
    }

    TypedQuery<LapTime> findAllQuery = em.createQuery("SELECT DISTINCT l FROM LapTime l"
        + " LEFT JOIN FETCH l.pilot LEFT JOIN FETCH l.event LEFT JOIN FETCH l.intermediates" + whereClause
        + " ORDER BY l.startDate", LapTime.class);
    if (startPosition != null) {
      findAllQuery.setFirstResult(startPosition);
    }
    if (maxResult != null) {
      findAllQuery.setMaxResults(maxResult);
    }
    if (!whereClause.isEmpty()) {
      findAllQuery.setParameter("entityId", pilotId);
    }
    // Check if we are in a loop event
    // Keep a map of last pilot laps to set new laptime when next lap is reached
    Map<Integer, LapTimeDTO> lastLapPerPilot = new HashMap<Integer, LapTimeDTO>();
    final List<LapTime> searchResults = findAllQuery.getResultList();
    final List<LapTimeDTO> results = new ArrayList<LapTimeDTO>();
    for (LapTime searchResult : searchResults) {
      LapTimeDTO dto = new LapTimeDTO(searchResult);
      NestedEventDTO event = dto.getEvent();
      NestedPilotDTO pilot = dto.getPilot();
      LapTimeDTO lastPilotLap = lastLapPerPilot.get(pilot.getId());
      if (event.isLoopTrack() && lastPilotLap != null && lastPilotLap.getEvent().getId() == event.getId()) {
        Timestamp startDate = dto.getStartDate();
        if (startDate.getTime() > 0) {
          lastPilotLap.addLastSector(startDate);
        }
      }
      lastLapPerPilot.put(dto.getPilot().getId(), dto);
      results.add(dto);
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
