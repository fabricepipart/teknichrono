package org.trd.app.teknichrono.rest;

import java.util.ArrayList;
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

import org.jboss.logging.Logger;
import org.trd.app.teknichrono.business.LapTimeDisplay;
import org.trd.app.teknichrono.business.LapTimeManager;
import org.trd.app.teknichrono.model.Category;
import org.trd.app.teknichrono.model.Event;
import org.trd.app.teknichrono.model.LapTime;
import org.trd.app.teknichrono.model.Location;
import org.trd.app.teknichrono.model.Pilot;
import org.trd.app.teknichrono.model.Session;
import org.trd.app.teknichrono.model.SessionType;
import org.trd.app.teknichrono.rest.dto.LapTimeDTO;
import org.trd.app.teknichrono.rest.sql.OrderByClauseBuilder;
import org.trd.app.teknichrono.rest.sql.WhereClauseBuilder;
import org.trd.app.teknichrono.util.InvalidArgumentException;

/**
 * 
 */
@Stateless
@Path("/laptimes")
public class LapTimeEndpoint {
  @PersistenceContext(unitName = "teknichrono-persistence-unit")
  private EntityManager em;

  private Logger logger = Logger.getLogger(LapTimeEndpoint.class);
  private LapTimeManager lapTimeManager = new LapTimeManager();

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
        + " l LEFT JOIN FETCH l.pilot LEFT JOIN FETCH l.session LEFT JOIN FETCH l.intermediates"
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
  @Path("/best")
  @Produces("application/json")
  public List<LapTimeDTO> best(@QueryParam("pilotId") Integer pilotId, @QueryParam("sessionId") Integer sessionId,
      @QueryParam("locationId") Integer locationId, @QueryParam("eventId") Integer eventId,
      @QueryParam("categoryId") Integer categoryId, @QueryParam("start") Integer startPosition,
      @QueryParam("max") Integer maxResult) {
    if (sessionId == null && locationId == null && eventId == null) {
      logger.error("Please define a sessiondId, locationId or eventId to get best laps.");
      throw new InvalidArgumentException();
    }
    final List<LapTimeDTO> results = getAllLapsDTOOrderedByStartDate(pilotId, sessionId, locationId, eventId,
        categoryId, startPosition, maxResult, lapTimeManager);
    if (pilotId != null) {
      lapTimeManager.arrangeDisplay(results, LapTimeDisplay.ORDER_BY_DURATION);
    } else {
      lapTimeManager.arrangeDisplay(results, LapTimeDisplay.KEEP_COMPLETE, LapTimeDisplay.KEEP_BEST,
          LapTimeDisplay.ORDER_BY_DURATION);
    }
    return results;
  }

  @GET
  @Path("/race")
  @Produces("application/json")
  public List<LapTimeDTO> race(@QueryParam("pilotId") Integer pilotId, @QueryParam("sessionId") Integer sessionId,
      @QueryParam("locationId") Integer locationId, @QueryParam("eventId") Integer eventId,
      @QueryParam("categoryId") Integer categoryId, @QueryParam("start") Integer startPosition,
      @QueryParam("max") Integer maxResult) {
    if (sessionId == null) {
      logger.error("Please define a sessiondId to get race laps.");
      throw new InvalidArgumentException();
    }
    Session session = em.find(Session.class, sessionId);
    if (session.getSessionType() != SessionType.RACE) {
      logger.error("Session ID (" + sessionId + ") mentioned is not a race.");
      throw new InvalidArgumentException();
    }
    final List<LapTimeDTO> results = getAllLapsDTOOrderedByStartDate(pilotId, sessionId, locationId, eventId,
        categoryId, startPosition, maxResult, lapTimeManager);

    if (pilotId != null) {
      lapTimeManager.arrangeDisplay(results, LapTimeDisplay.ORDER_BY_DURATION);
    } else {
      // TODO Remove LapTimeDisplay.KEEP_COMPLETE if race is ongoing
      lapTimeManager.arrangeDisplay(results, LapTimeDisplay.KEEP_COMPLETE, LapTimeDisplay.KEEP_LAST,
          LapTimeDisplay.ORDER_FOR_RACE);
    }
    return results;

  }

  @GET
  @Produces("application/json")
  public List<LapTimeDTO> listAll(@QueryParam("pilotId") Integer pilotId, @QueryParam("sessionId") Integer sessionId,
      @QueryParam("locationId") Integer locationId, @QueryParam("eventId") Integer eventId,
      @QueryParam("categoryId") Integer categoryId, @QueryParam("start") Integer startPosition,
      @QueryParam("max") Integer maxResult) {
    if (sessionId == null && locationId == null && eventId == null) {
      logger.error("Please define a sessiondId, locationId or eventId to get laps.");
      throw new InvalidArgumentException();
    }

    final List<LapTimeDTO> results = getAllLapsDTOOrderedByStartDate(pilotId, sessionId, locationId, eventId,
        categoryId, startPosition, maxResult, lapTimeManager);
    // TODO Remove LapTimeDisplay.KEEP_COMPLETE if session is ongoing
    lapTimeManager.arrangeDisplay(results, LapTimeDisplay.KEEP_COMPLETE);

    return results;
  }

  private List<LapTimeDTO> getAllLapsDTOOrderedByStartDate(Integer pilotId, Integer sessionId, Integer locationId,
      Integer eventId, Integer categoryId, Integer startPosition, Integer maxResult, LapTimeManager lapTimeManager) {
    final List<LapTime> searchResults = getAllLapsOrderedByStartDate(pilotId, sessionId, locationId, eventId,
        categoryId, startPosition, maxResult);
    final List<LapTimeDTO> results = lapTimeManager.convert(searchResults);
    return results;
  }

  private List<LapTime> getAllLapsOrderedByStartDate(Integer pilotId, Integer sessionId, Integer locationId,
      Integer eventId, Integer categoryId, Integer startPosition, Integer maxResult) {
    WhereClauseBuilder whereClauseBuilder = buildWhereClause(pilotId, sessionId, locationId, eventId, categoryId);
    OrderByClauseBuilder orderByClauseBuilder = new OrderByClauseBuilder();
    // Necessary to have the lapTimeManager.convert working
    orderByClauseBuilder.add("l.startDate");

    TypedQuery<LapTime> findAllQuery = em.createQuery("SELECT DISTINCT l FROM LapTime l"
        + " LEFT JOIN FETCH l.pilot LEFT JOIN FETCH l.session LEFT JOIN FETCH l.intermediates"
        + whereClauseBuilder.build() + orderByClauseBuilder.build(), LapTime.class);
    if (startPosition != null) {
      findAllQuery.setFirstResult(startPosition);
    }
    if (maxResult != null) {
      findAllQuery.setMaxResults(maxResult);
    }
    whereClauseBuilder.applyClauses(findAllQuery);
    final List<LapTime> searchResults = findAllQuery.getResultList();
    return searchResults;
  }

  private WhereClauseBuilder buildWhereClause(Integer pilotId, Integer sessionId, Integer locationId, Integer eventId,
      Integer categoryId) {
    WhereClauseBuilder whereClauseBuilder = new WhereClauseBuilder();
    if (pilotId != null) {
      whereClauseBuilder.addEqualsClause("l.pilot.id", "pilotId", pilotId);
    } else {
      if (categoryId != null) {
        Category category = em.find(Category.class, categoryId);
        if (category != null) {
          List<Integer> categoryPilotsIds = new ArrayList<>();
          for (Pilot p : category.getPilots()) {
            categoryPilotsIds.add(p.getId());
          }
          whereClauseBuilder.addInClause("l.pilot.id", "categoryPilotsIds", categoryPilotsIds);
        }
      }
    }
    if (sessionId != null) {
      whereClauseBuilder.addEqualsClause("l.session.id", "sessionId", sessionId);
    } else {
      if (eventId != null) {
        Event event = em.find(Event.class, eventId);
        if (event != null) {
          List<Integer> eventSessionIds = new ArrayList<>();
          for (Session s : event.getSessions()) {
            eventSessionIds.add(s.getId());
          }
          whereClauseBuilder.addInClause("l.session.id", "eventSessionIds", eventSessionIds);
        }
      }
      if (locationId != null) {
        Location location = em.find(Location.class, locationId);
        if (location != null) {
          List<Integer> locationSessionIds = new ArrayList<>();
          for (Session s : location.getSessions()) {
            locationSessionIds.add(s.getId());
          }
          whereClauseBuilder.addInClause("l.session.id", "locationSessionIds", locationSessionIds);
        }
      }
    }
    return whereClauseBuilder;
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
