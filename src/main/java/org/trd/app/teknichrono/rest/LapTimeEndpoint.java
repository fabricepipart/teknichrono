package org.trd.app.teknichrono.rest;

import com.opencsv.exceptions.CsvException;
import io.quarkus.panache.common.Page;
import org.jboss.logging.Logger;
import org.trd.app.teknichrono.business.view.LapTimeConverter;
import org.trd.app.teknichrono.business.view.LapTimeDisplay;
import org.trd.app.teknichrono.business.view.LapTimeManager;
import org.trd.app.teknichrono.model.dto.LapTimeDTO;
import org.trd.app.teknichrono.model.dto.NestedPilotDTO;
import org.trd.app.teknichrono.model.jpa.Category;
import org.trd.app.teknichrono.model.jpa.CategoryRepository;
import org.trd.app.teknichrono.model.jpa.Event;
import org.trd.app.teknichrono.model.jpa.EventRepository;
import org.trd.app.teknichrono.model.jpa.LapTime;
import org.trd.app.teknichrono.model.jpa.LapTimeRepository;
import org.trd.app.teknichrono.model.jpa.Location;
import org.trd.app.teknichrono.model.jpa.LocationRepository;
import org.trd.app.teknichrono.model.jpa.Pilot;
import org.trd.app.teknichrono.model.jpa.Session;
import org.trd.app.teknichrono.model.jpa.SessionRepository;
import org.trd.app.teknichrono.model.jpa.SessionType;
import org.trd.app.teknichrono.util.csv.CSVConverter;
import org.trd.app.teknichrono.util.sql.OrderByClauseBuilder;
import org.trd.app.teknichrono.util.sql.WhereClauseBuilder;

import javax.inject.Inject;
import javax.persistence.EntityManager;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Path("/laptimes")
public class LapTimeEndpoint {

  private static final Logger LOGGER = Logger.getLogger(LapTimeEndpoint.class);

  // TODO, get rid of the em (still needed for the fromDTO)
  private final EntityManager em;

  private final LapTimeRepository.Panache lapTimeRepository;

  private final SessionRepository sessionRepository;

  private final CategoryRepository categoryRepository;

  private final EventRepository eventRepository;

  private final LocationRepository locationRepository;

  private final LapTimeManager lapTimeManager = new LapTimeManager();

  private final LapTimeConverter lapTimeConverter = new LapTimeConverter();

  private final CSVConverter csvConverter = new CSVConverter();

  @Inject
  public LapTimeEndpoint(EntityManager em, LapTimeRepository.Panache lapTimeRepository, SessionRepository sessionRepository,
                         CategoryRepository categoryRepository, EventRepository eventRepository,
                         LocationRepository locationRepository) {
    this.em = em;
    this.lapTimeRepository = lapTimeRepository;
    this.sessionRepository = sessionRepository;
    this.categoryRepository = categoryRepository;
    this.eventRepository = eventRepository;
    this.locationRepository = locationRepository;
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Transactional
  public Response create(LapTimeDTO dto) {
    LapTime entity = dto.fromDTO(null, em);
    lapTimeRepository.persist(entity);
    return Response.created(UriBuilder.fromResource(LapTimeEndpoint.class).path(String.valueOf(entity.id)).build())
        .build();
  }

  @DELETE
  @Path("/{id:[0-9][0-9]*}")
  @Transactional
  public Response deleteById(@PathParam("id") long id) {
    LapTime entity = lapTimeRepository.findById(id);
    if (entity == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    lapTimeRepository.delete(entity);
    return Response.noContent().build();
  }

  @GET
  @Path("/{id:[0-9][0-9]*}")
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public Response findById(@PathParam("id") long id) {
    LapTime entity = lapTimeRepository.findById(id);
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
  public Response bestToCsv(@QueryParam("pilotId") Long pilotId, @QueryParam("sessionId") Long sessionId,
                            @QueryParam("locationId") Long locationId, @QueryParam("eventId") Long eventId,
                            @QueryParam("categoryId") Long categoryId) {
    try {
      List<LapTimeDTO> results = bestLapTimes(pilotId, sessionId, locationId, eventId, categoryId, null, null);
      String csvResults = csvConverter.convertToCsv(results);
      return Response.ok().entity(csvResults).build();
    } catch (IllegalArgumentException e) {
      return Response.status(Status.BAD_REQUEST).entity(e.toString()).build();
    } catch (CsvException | IOException e) {
      return Response.serverError().build();
    }
  }

  @GET
  @Path("/best")
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public Response best(@QueryParam("pilotId") Long pilotId, @QueryParam("sessionId") Long sessionId,
                       @QueryParam("locationId") Long locationId, @QueryParam("eventId") Long eventId,
                       @QueryParam("categoryId") Long categoryId, @QueryParam("page") Integer pageIndex,
                       @QueryParam("pageSize") Integer pageSize) {
    try {
      List<LapTimeDTO> lapTimes = bestLapTimes(pilotId, sessionId, locationId, eventId, categoryId, pageIndex, pageSize);
      return Response.ok().entity(lapTimes).build();
    } catch (IllegalArgumentException e) {
      return Response.status(Status.BAD_REQUEST).entity(e.toString()).build();
    }
  }

  private List<LapTimeDTO> bestLapTimes(Long pilotId, Long sessionId, Long locationId, Long eventId, Long categoryId,
                                        Integer pageIndex, Integer pageSize) {
    if (sessionId == null && locationId == null && eventId == null) {
      LOGGER.error("Please define a sessiondId, locationId or eventId to get best laps.");
      throw new IllegalArgumentException("Please define a sessiondId, locationId or eventId to get best laps.");
    }
    List<LapTimeDTO> results = getAllLapsDTOOrderedByStartDate(pilotId, sessionId, locationId, eventId, categoryId,
            pageIndex, pageSize);
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
  public Response resultsToCsv(@QueryParam("pilotId") Long pilotId, @QueryParam("sessionId") Long sessionId,
                               @QueryParam("locationId") Long locationId, @QueryParam("eventId") Long eventId,
                               @QueryParam("categoryId") Long categoryId) {
    try {
      List<LapTimeDTO> results = resultsList(pilotId, sessionId, locationId, eventId, categoryId, null, null);
      String csvResults = csvConverter.convertToCsv(results);
      return Response.ok().entity(csvResults).build();
    } catch (IllegalArgumentException e) {
      return Response.status(Status.BAD_REQUEST).entity(e.toString()).build();
    } catch (CsvException | IOException e) {
      return Response.serverError().build();
    }
  }

  @GET
  @Path("/results")
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public Response results(@QueryParam("pilotId") Long pilotId, @QueryParam("sessionId") Long sessionId,
                          @QueryParam("locationId") Long locationId, @QueryParam("eventId") Long eventId,
                          @QueryParam("categoryId") Long categoryId, @QueryParam("page") Integer pageIndex,
                          @QueryParam("pageSize") Integer pageSize) {
    try {
      List<LapTimeDTO> lapTimes = resultsList(pilotId, sessionId, locationId, eventId, categoryId, pageIndex, pageSize);
      return Response.ok().entity(lapTimes).build();
    } catch (IllegalArgumentException e) {
      return Response.status(Status.BAD_REQUEST).entity(e.toString()).build();
    }
  }

  private List<LapTimeDTO> resultsList(Long pilotId, Long sessionId, Long locationId, Long eventId, Long categoryId,
                                       Integer startPosition, Integer maxResult) {
    if (sessionId == null) {
      LOGGER.error("Please define a sessiondId to get race laps.");
      throw new IllegalArgumentException("Please define a sessiondId to get race laps.");
    }
    Session session = sessionRepository.findById(sessionId);

    List<LapTimeDTO> results = getAllLapsDTOOrderedByStartDate(pilotId, sessionId, locationId, eventId, categoryId,
        startPosition, maxResult);

    Set<NestedPilotDTO> pilots = session.getPilots().stream().map(NestedPilotDTO::fromPilot)
        .collect(Collectors.toSet());
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
  public Response listAllToCsv(@QueryParam("pilotId") Long pilotId, @QueryParam("sessionId") Long sessionId,
                               @QueryParam("locationId") Long locationId, @QueryParam("eventId") Long eventId,
                               @QueryParam("categoryId") Long categoryId) {
    try {
      List<LapTimeDTO> results = all(pilotId, sessionId, locationId, eventId, categoryId, null, null);
      String csvResults = csvConverter.convertToCsv(results);
      return Response.ok().entity(csvResults).build();
    } catch (IllegalArgumentException e) {
      return Response.status(Status.BAD_REQUEST).entity(e.toString()).build();
    } catch (CsvException | IOException e) {
      return Response.serverError().build();
    }
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public Response listAll(@QueryParam("pilotId") Long pilotId, @QueryParam("sessionId") Long sessionId,
                          @QueryParam("locationId") Long locationId, @QueryParam("eventId") Long eventId,
                          @QueryParam("categoryId") Long categoryId, @QueryParam("page") Integer pageIndex,
                          @QueryParam("pageSize") Integer pageSize) {
    try {
      List<LapTimeDTO> lapTimes = all(pilotId, sessionId, locationId, eventId, categoryId, pageIndex, pageSize);
      return Response.ok().entity(lapTimes).build();
    } catch (IllegalArgumentException e) {
      return Response.status(Status.BAD_REQUEST).entity(e.toString()).build();
    }
  }

  private List<LapTimeDTO> all(Long pilotId, Long sessionId, Long locationId, Long eventId, Long categoryId,
                               Integer startPosition, Integer maxResult) {
    if (sessionId == null && locationId == null && eventId == null) {
      LOGGER.error("Please define a sessiondId, locationId or eventId to get laps.");
      throw new IllegalArgumentException("Please define a sessiondId, locationId or eventId to get laps.");
    }

    List<LapTimeDTO> results = getAllLapsDTOOrderedByStartDate(pilotId, sessionId, locationId, eventId, categoryId,
        startPosition, maxResult);
    // TODO Remove LapTimeDisplay.KEEP_COMPLETE if session is ongoing
    lapTimeManager.arrangeDisplay(results, LapTimeDisplay.KEEP_COMPLETE, LapTimeDisplay.ORDER_BY_LAST_SEEN);
    return results;
  }

  private List<LapTimeDTO> getAllLapsDTOOrderedByStartDate(Long pilotId, Long sessionId, Long locationId, Long eventId,
                                                           Long categoryId, Integer pageIndex, Integer pageSize) {
    List<LapTime> searchResults = getAllLapsOrderedByStartDate(pilotId, sessionId, locationId, eventId, categoryId,
            pageIndex, pageSize);
    return lapTimeConverter.convert(searchResults);
  }

  private List<LapTime> getAllLapsOrderedByStartDate(Long pilotId, Long sessionId, Long locationId, Long eventId,
                                                     Long categoryId, Integer pageIndex, Integer pageSize) {
    WhereClauseBuilder whereClauseBuilder = buildWhereClause(pilotId, sessionId, locationId, eventId, categoryId);
    OrderByClauseBuilder orderByClauseBuilder = new OrderByClauseBuilder();
    // Necessary to have the lapTimeManager.manage working
    orderByClauseBuilder.add("l.startDate");

    TypedQuery<LapTime> findAllQuery = em.createQuery("SELECT DISTINCT l FROM LapTime l"
        + " LEFT JOIN FETCH l.pilot LEFT JOIN FETCH l.session LEFT JOIN FETCH l.intermediates"
        + whereClauseBuilder.build() + orderByClauseBuilder.build(), LapTime.class);
    Page page = Paging.from(pageIndex, pageSize);
    findAllQuery.setFirstResult(page.index * page.size);
    findAllQuery.setMaxResults(page.size);
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
        Category category = categoryRepository.findById(categoryId);
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
        Event event = eventRepository.findById(eventId);
        if (event != null) {
          List<Long> eventSessionIds = new ArrayList<>();
          for (Session s : event.getSessions()) {
            eventSessionIds.add(s.id);
          }
          whereClauseBuilder.addInClause("l.session.id", "eventSessionIds", eventSessionIds);
        }
      }
      if (locationId != null) {
        Location location = locationRepository.findById(locationId);
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
    LapTime entity = lapTimeRepository.findById(id);
    if (entity == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    entity = dto.fromDTO(entity, em);
    try {
      lapTimeRepository.persist(entity);
    } catch (OptimisticLockException e) {
      return Response.status(Status.CONFLICT).entity(e.getEntity()).build();
    }
    return Response.noContent().build();
  }
}
