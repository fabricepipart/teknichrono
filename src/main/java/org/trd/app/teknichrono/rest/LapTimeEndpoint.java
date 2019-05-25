package org.trd.app.teknichrono.rest;

import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.jboss.logging.Logger;
import org.trd.app.teknichrono.business.view.LapTimeConverter;
import org.trd.app.teknichrono.business.view.LapTimeDisplay;
import org.trd.app.teknichrono.business.view.LapTimeManager;
import org.trd.app.teknichrono.model.dto.LapTimeDTO;
import org.trd.app.teknichrono.model.dto.NestedPilotDTO;
import org.trd.app.teknichrono.model.jpa.Category;
import org.trd.app.teknichrono.model.jpa.Event;
import org.trd.app.teknichrono.model.jpa.LapTime;
import org.trd.app.teknichrono.model.jpa.Location;
import org.trd.app.teknichrono.model.jpa.Pilot;
import org.trd.app.teknichrono.model.jpa.Session;
import org.trd.app.teknichrono.model.jpa.SessionType;
import org.trd.app.teknichrono.util.InvalidArgumentException;
import org.trd.app.teknichrono.util.sql.OrderByClauseBuilder;
import org.trd.app.teknichrono.util.sql.WhereClauseBuilder;

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
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 */
@Path("/laptimes")
public class LapTimeEndpoint {

  private final EntityManager em;

  @Inject
  public LapTimeEndpoint(EntityManager em) {
    this.em = em;
  }

  private Logger logger = Logger.getLogger(LapTimeEndpoint.class);
  private LapTimeManager lapTimeManager = new LapTimeManager();
  private LapTimeConverter lapTimeConverter = new LapTimeConverter();

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Transactional
  public Response create(LapTimeDTO dto) {
    LapTime entity = dto.fromDTO(null, em);
    em.persist(entity);
    return Response.created(UriBuilder.fromResource(LapTimeEndpoint.class).path(String.valueOf(entity.id)).build())
        .build();
  }

  @DELETE
  @Path("/{id:[0-9][0-9]*}")
  @Transactional
  public Response deleteById(@PathParam("id") long id) {
    LapTime entity = em.find(LapTime.class, id);
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
    LapTimeDTO dto = LapTimeDTO.fromLapTime(entity);
    return Response.ok(dto).build();
  }

  @GET
  @Path("/csv/best")
  @Produces("text/csv")
  @Transactional
  public String bestToCsv(@QueryParam("pilotId") Long pilotId, @QueryParam("sessionId") Long sessionId,
                          @QueryParam("locationId") Long locationId, @QueryParam("eventId") Long eventId,
                          @QueryParam("categoryId") Long categoryId) {
    List<LapTimeDTO> results = best(pilotId, sessionId, locationId, eventId, categoryId, null, null);
    return convertToCsv(results);
  }

  @GET
  @Path("/best")
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public List<LapTimeDTO> best(@QueryParam("pilotId") Long pilotId, @QueryParam("sessionId") Long sessionId,
                               @QueryParam("locationId") Long locationId, @QueryParam("eventId") Long eventId,
                               @QueryParam("categoryId") Long categoryId, @QueryParam("start") Integer startPosition,
                               @QueryParam("max") Integer maxResult) {
    if (sessionId == null && locationId == null && eventId == null) {
      logger.error("Please define a sessiondId, locationId or eventId to get best laps.");
      throw new InvalidArgumentException();
    }
    final List<LapTimeDTO> results = getAllLapsDTOOrderedByStartDate(pilotId, sessionId, locationId, eventId,
        categoryId, startPosition, maxResult);
    if (pilotId != null) {
      lapTimeManager.arrangeDisplay(results, LapTimeDisplay.ORDER_BY_DURATION);
    } else {
      lapTimeManager.arrangeDisplay(results, LapTimeDisplay.KEEP_COMPLETE, LapTimeDisplay.KEEP_BEST,
          LapTimeDisplay.ORDER_BY_DURATION);
    }
    return results;
  }

  @GET
  @Path("/csv/results")
  @Produces("text/csv")
  @Transactional
  public String resultsToCsv(@QueryParam("pilotId") Long pilotId, @QueryParam("sessionId") Long sessionId,
                             @QueryParam("locationId") Long locationId, @QueryParam("eventId") Long eventId,
                             @QueryParam("categoryId") Long categoryId) {

    List<LapTimeDTO> results = results(pilotId, sessionId, locationId, eventId, categoryId, null, null);
    return convertToCsv(results);
  }

  private String convertToCsv(List<LapTimeDTO> results) {
    String csvResult = null;
    StringWriter writer = new StringWriter();
    // TODO StatefulBeanToCsv beanToCsv = new StatefulBeanToCsvBuilder<LapTimeDTO>(writer).withMappingStrategy(new LapTimeMappingStrategy()).build();
    StatefulBeanToCsv<LapTimeDTO> beanToCsv = new StatefulBeanToCsvBuilder<LapTimeDTO>(writer).build();
    try {
      beanToCsv.write(results);
      csvResult = writer.toString();
      writer.close();
    } catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException | IOException e) {
      e.printStackTrace();
    }
    return csvResult;
  }

  @GET
  @Path("/results")
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public List<LapTimeDTO> results(@QueryParam("pilotId") Long pilotId, @QueryParam("sessionId") Long sessionId,
                                  @QueryParam("locationId") Long locationId, @QueryParam("eventId") Long eventId,
                                  @QueryParam("categoryId") Long categoryId, @QueryParam("start") Integer startPosition,
                                  @QueryParam("max") Integer maxResult) {
    if (sessionId == null) {
      logger.error("Please define a sessiondId to get race laps.");
      throw new InvalidArgumentException();
    }
    Session session = em.find(Session.class, sessionId);

    final List<LapTimeDTO> results = getAllLapsDTOOrderedByStartDate(pilotId, sessionId, locationId, eventId,
        categoryId, startPosition, maxResult);

    Set<NestedPilotDTO> pilots = session.getPilots().stream().map(NestedPilotDTO::fromPilot).collect(Collectors.toSet());
    if (session.getSessionType() == SessionType.RACE) {
      if (pilotId != null) {
        lapTimeManager.arrangeDisplay(results, LapTimeDisplay.KEEP_COMPLETE);
      } else {
        if (session.isCurrent()) {
          lapTimeManager.arrangeDisplay(results, pilots, LapTimeDisplay.KEEP_LAST, LapTimeDisplay.ORDER_FOR_RACE);
        } else {
          lapTimeManager.arrangeDisplay(results, pilots, LapTimeDisplay.KEEP_COMPLETE, LapTimeDisplay.KEEP_LAST,
              LapTimeDisplay.ORDER_FOR_RACE);
        }
      }
    } else {
      if (pilotId != null) {
        lapTimeManager.arrangeDisplay(results, pilots, LapTimeDisplay.KEEP_COMPLETE);
      } else {
        lapTimeManager.arrangeDisplay(results, pilots, LapTimeDisplay.KEEP_COMPLETE, LapTimeDisplay.KEEP_BEST,
            LapTimeDisplay.ORDER_BY_DURATION);
      }
    }
    return results;

  }

  @GET
  @Path("/csv")
  @Produces("text/csv")
  @Transactional
  public String listAllToCsv(@QueryParam("pilotId") Long pilotId, @QueryParam("sessionId") Long sessionId,
                             @QueryParam("locationId") Long locationId, @QueryParam("eventId") Long eventId,
                             @QueryParam("categoryId") Long categoryId) {
    List<LapTimeDTO> results = listAll(pilotId, sessionId, locationId, eventId, categoryId, null, null);
    return convertToCsv(results);
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public List<LapTimeDTO> listAll(@QueryParam("pilotId") Long pilotId, @QueryParam("sessionId") Long sessionId,
                                  @QueryParam("locationId") Long locationId, @QueryParam("eventId") Long eventId,
                                  @QueryParam("categoryId") Long categoryId, @QueryParam("start") Integer startPosition,
                                  @QueryParam("max") Integer maxResult) {
    if (sessionId == null && locationId == null && eventId == null) {
      logger.error("Please define a sessiondId, locationId or eventId to get laps.");
      throw new InvalidArgumentException();
    }

    final List<LapTimeDTO> results = getAllLapsDTOOrderedByStartDate(pilotId, sessionId, locationId, eventId,
        categoryId, startPosition, maxResult);
    // TODO Remove LapTimeDisplay.KEEP_COMPLETE if session is ongoing
    lapTimeManager.arrangeDisplay(results, LapTimeDisplay.KEEP_COMPLETE, LapTimeDisplay.ORDER_BY_LAST_SEEN);

    return results;
  }

  private List<LapTimeDTO> getAllLapsDTOOrderedByStartDate(Long pilotId, Long sessionId, Long locationId,
                                                           Long eventId, Long categoryId, Integer startPosition, Integer maxResult) {
    final List<LapTime> searchResults = getAllLapsOrderedByStartDate(pilotId, sessionId, locationId, eventId,
        categoryId, startPosition, maxResult);
    return lapTimeConverter.convert(searchResults);
  }

  private List<LapTime> getAllLapsOrderedByStartDate(Long pilotId, Long sessionId, Long locationId,
                                                     Long eventId, Long categoryId, Integer startPosition, Integer maxResult) {
    WhereClauseBuilder whereClauseBuilder = buildWhereClause(pilotId, sessionId, locationId, eventId, categoryId);
    OrderByClauseBuilder orderByClauseBuilder = new OrderByClauseBuilder();
    // Necessary to have the lapTimeManager.manage working
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
    return findAllQuery.getResultList();
  }

  private WhereClauseBuilder buildWhereClause(Long pilotId, Long sessionId, Long locationId, Long eventId,
                                              Long categoryId) {
    WhereClauseBuilder whereClauseBuilder = new WhereClauseBuilder();
    if (pilotId != null) {
      whereClauseBuilder.addEqualsClause("l.pilot.id", "pilotId", pilotId);
    } else {
      if (categoryId != null) {
        Category category = em.find(Category.class, categoryId);
        if (category != null) {
          List<Long> categoryPilotsIds = new ArrayList<>();
          for (Pilot p : category.getPilots()) {
            categoryPilotsIds.add(p.id);
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
          List<Long> eventSessionIds = new ArrayList<>();
          for (Session s : event.getSessions()) {
            eventSessionIds.add(s.id);
          }
          whereClauseBuilder.addInClause("l.session.id", "eventSessionIds", eventSessionIds);
        }
      }
      if (locationId != null) {
        Location location = em.find(Location.class, locationId);
        if (location != null) {
          List<Long> locationSessionIds = new ArrayList<>();
          for (Session s : location.getSessions()) {
            locationSessionIds.add(s.id);
          }
          whereClauseBuilder.addInClause("l.session.id", "locationSessionIds", locationSessionIds);
        }
      }
    }
    return whereClauseBuilder;
  }

  @PUT
  @Path("/{id:[0-9][0-9]*}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Transactional
  public Response update(@PathParam("id") long id, LapTimeDTO dto) {
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
      em.merge(entity);
    } catch (OptimisticLockException e) {
      return Response.status(Status.CONFLICT).entity(e.getEntity()).build();
    }
    return Response.noContent().build();
  }
}
