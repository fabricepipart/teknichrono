package org.trd.app.teknichrono.business.view;

import org.jboss.logging.Logger;
import org.trd.app.teknichrono.model.dto.LapTimeDTO;
import org.trd.app.teknichrono.model.dto.NestedPilotDTO;
import org.trd.app.teknichrono.model.jpa.Category;
import org.trd.app.teknichrono.model.jpa.Event;
import org.trd.app.teknichrono.model.jpa.LapTime;
import org.trd.app.teknichrono.model.jpa.Location;
import org.trd.app.teknichrono.model.jpa.Pilot;
import org.trd.app.teknichrono.model.jpa.Session;
import org.trd.app.teknichrono.model.jpa.SessionType;
import org.trd.app.teknichrono.model.repository.CategoryRepository;
import org.trd.app.teknichrono.model.repository.EventRepository;
import org.trd.app.teknichrono.model.repository.LocationRepository;
import org.trd.app.teknichrono.model.repository.SessionRepository;
import org.trd.app.teknichrono.util.sql.OrderByClauseBuilder;
import org.trd.app.teknichrono.util.sql.WhereClauseBuilder;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class LapTimeManager {

  private static final Logger LOGGER = Logger.getLogger(LapTimeManager.class);
  private final SessionRepository sessionRepository;
  private final CategoryRepository categoryRepository;
  private final EventRepository eventRepository;
  private final LocationRepository locationRepository;
  private final EntityManager em; //TODO Get rid of it

  private final LapTimeFilter filter = new LapTimeFilter();
  private final LapTimeFiller filler = new LapTimeFiller();
  private final LapTimeOrder order = new LapTimeOrder();
  private final LapTimeConverter lapTimeConverter = new LapTimeConverter();

  public LapTimeManager(EntityManager em, SessionRepository sessionRepository, CategoryRepository categoryRepository,
                        EventRepository eventRepository, LocationRepository locationRepository) {
    this.em = em;
    this.sessionRepository = sessionRepository;
    this.categoryRepository = categoryRepository;
    this.eventRepository = eventRepository;
    this.locationRepository = locationRepository;

  }


  public List<LapTimeDTO> all(Long pilotId, Long sessionId, Long locationId, Long eventId, Long categoryId,
                              Integer pageIndex, Integer pageSize) {
    if (sessionId == null && locationId == null && eventId == null) {
      LOGGER.error("Please define a sessiondId, locationId or eventId to get laps.");
      throw new IllegalArgumentException("Please define a sessiondId, locationId or eventId to get laps.");
    }

    List<LapTimeDTO> results = getAllLapsDTOOrderedByStartDate(pilotId, sessionId, locationId, eventId, categoryId);
    // TODO Remove LapTimeDisplay.KEEP_COMPLETE if session is ongoing
    arrangeDisplay(results, LapTimeDisplay.KEEP_COMPLETE, LapTimeDisplay.ORDER_BY_LAST_SEEN);

    // Cant't filter during the query since we filter, rearrange the list afterwards
    results = page(pageIndex, pageSize, results);
    return results;
  }

  public List<LapTimeDTO> bestLapTimes(Long pilotId, Long sessionId, Long locationId, Long eventId, Long categoryId,
                                       Integer pageIndex, Integer pageSize) {
    if (sessionId == null && locationId == null && eventId == null) {
      LOGGER.error("Please define a sessiondId, locationId or eventId to get best laps.");
      throw new IllegalArgumentException("Please define a sessiondId, locationId or eventId to get best laps.");
    }
    List<LapTimeDTO> results = getAllLapsDTOOrderedByStartDate(pilotId, sessionId, locationId, eventId, categoryId);
    if (pilotId != null) {
      arrangeDisplay(results, LapTimeDisplay.ORDER_BY_DURATION, LapTimeDisplay.KEEP_COMPLETE);
    } else {
      arrangeDisplay(results, LapTimeDisplay.KEEP_COMPLETE, LapTimeDisplay.KEEP_BEST,
          LapTimeDisplay.ORDER_BY_DURATION);
    }
    // Cant't filter during the query since we filter, rearrange the list afterwards
    results = page(pageIndex, pageSize, results);
    return results;
  }

  public List<LapTimeDTO> resultsList(Long pilotId, Long sessionId, Long locationId, Long eventId, Long categoryId,
                                      Integer startPosition, Integer maxResult) {
    if (sessionId == null) {
      LOGGER.error("Please define a sessiondId to get race laps.");
      throw new IllegalArgumentException("Please define a sessiondId to get race laps.");
    }
    Session session = sessionRepository.findById(sessionId);

    List<LapTimeDTO> results = getAllLapsDTOOrderedByStartDate(pilotId, sessionId, locationId, eventId, categoryId);

    Set<NestedPilotDTO> pilots = session.getPilots().stream().map(NestedPilotDTO::fromPilot)
        .collect(Collectors.toSet());
    if (session.getSessionType() == SessionType.RACE) {
      if (pilotId != null) {
        arrangeDisplay(results, LapTimeDisplay.KEEP_COMPLETE);
      } else {
        if (session.isCurrent()) {
          arrangeDisplay(results, pilots, LapTimeDisplay.KEEP_LAST, LapTimeDisplay.ORDER_FOR_RACE);
        } else {
          arrangeDisplay(results, pilots, LapTimeDisplay.KEEP_COMPLETE, LapTimeDisplay.KEEP_LAST, LapTimeDisplay.ORDER_FOR_RACE);
        }

      }
    } else {
      if (pilotId != null) {
        arrangeDisplay(results, pilots, LapTimeDisplay.KEEP_COMPLETE);
      } else {
        arrangeDisplay(results, pilots, LapTimeDisplay.KEEP_COMPLETE, LapTimeDisplay.KEEP_BEST,
            LapTimeDisplay.ORDER_BY_DURATION);
      }
    }

    // Cant't filter during the query since we filter, rearrange the list afterwards
    results = page(startPosition, maxResult, results);
    return results;
  }

  private List<LapTimeDTO> page(Integer pageIndex, Integer pageSize, List<LapTimeDTO> results) {
    if (pageIndex != null && pageSize != null && results != null) {
      if (results.size() > (pageIndex * pageSize)) {
        return results.subList(pageIndex * pageSize, pageIndex * (pageSize + 1));
      }
    }
    return results;
  }

  private void arrangeDisplay(List<LapTimeDTO> results, LapTimeDisplay... displays) {
    arrangeDisplay(results, null, displays);
  }

  private void arrangeDisplay(List<LapTimeDTO> results, Set<NestedPilotDTO> pilots, LapTimeDisplay... displays) {
    filter.filterExtreme(results);
    filler.fillLapsNumber(results);
    for (LapTimeDisplay display : displays) {
      switch (display) {
        case KEEP_COMPLETE:
          filter.filterNoDuration(results);
          // Adjust
          filler.fillLapsNumber(results);
          break;
        case KEEP_LAST:
          // TODO Should probably be merged with Best (best and last info present
          // in DTO) and then its just a matter of order
          filter.keepOnlyLast(results);
          break;
        case ORDER_FOR_RACE:
          order.orderForRace(results);
          break;
        case KEEP_BEST:
          filter.keepOnlyBest(results);
          break;
        case ORDER_BY_DURATION:
          order.orderByDuration(results);
          break;
        case ORDER_BY_LAST_SEEN:
          order.orderbyLastSeen(results);
        default:
          break;
      }
    }
    filler.ensureAllPilotsPresent(results, pilots);
  }

  private List<LapTimeDTO> getAllLapsDTOOrderedByStartDate(Long pilotId, Long sessionId, Long locationId, Long eventId,
                                                           Long categoryId) {
    List<LapTime> searchResults = getAllLapsOrderedByStartDate(pilotId, sessionId, locationId, eventId, categoryId);
    return lapTimeConverter.convert(searchResults);
  }

  private List<LapTime> getAllLapsOrderedByStartDate(Long pilotId, Long sessionId, Long locationId, Long eventId,
                                                     Long categoryId) {
    WhereClauseBuilder whereClauseBuilder = buildWhereClause(pilotId, sessionId, locationId, eventId, categoryId);
    OrderByClauseBuilder orderByClauseBuilder = new OrderByClauseBuilder();
    // Necessary to have the lapTimeManager.manage working
    orderByClauseBuilder.add("l.startDate");

    TypedQuery<LapTime> findAllQuery = em.createQuery("SELECT DISTINCT l FROM LapTime l"
        + " LEFT JOIN FETCH l.pilot LEFT JOIN FETCH l.session LEFT JOIN FETCH l.intermediates"
        + whereClauseBuilder.build() + orderByClauseBuilder.build(), LapTime.class);

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

}
