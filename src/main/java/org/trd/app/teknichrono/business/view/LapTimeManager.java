package org.trd.app.teknichrono.business.view;

import org.jboss.logging.Logger;
import org.trd.app.teknichrono.model.dto.LapTimeDTO;
import org.trd.app.teknichrono.model.dto.NestedPilotDTO;
import org.trd.app.teknichrono.model.jpa.LapTime;
import org.trd.app.teknichrono.model.jpa.Session;
import org.trd.app.teknichrono.model.jpa.SessionType;
import org.trd.app.teknichrono.model.repository.CategoryRepository;
import org.trd.app.teknichrono.model.repository.EventRepository;
import org.trd.app.teknichrono.model.repository.LapTimeRepository;
import org.trd.app.teknichrono.model.repository.LocationRepository;
import org.trd.app.teknichrono.model.repository.SessionRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class LapTimeManager {

  private static final Logger LOGGER = Logger.getLogger(LapTimeManager.class);
  private final SessionRepository sessionRepository;
  private final CategoryRepository categoryRepository;
  private final EventRepository eventRepository;
  private final LocationRepository locationRepository;
  private final LapTimeRepository laptimeRepository;

  private final LapTimeFilter filter = new LapTimeFilter();
  private final LapTimeFiller filler = new LapTimeFiller();
  private final LapTimeOrder order = new LapTimeOrder();
  private final LapTimeConverter lapTimeConverter = new LapTimeConverter();

  public LapTimeManager(LapTimeRepository laptimeRepository, SessionRepository sessionRepository, CategoryRepository categoryRepository,
                        EventRepository eventRepository, LocationRepository locationRepository) {
    this.laptimeRepository = laptimeRepository;
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
    Session session = this.sessionRepository.findById(sessionId);

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
    this.filter.filterExtreme(results);
    this.filler.fillLapsNumber(results);
    for (LapTimeDisplay display : displays) {
      switch (display) {
        case KEEP_COMPLETE:
          this.filter.filterNoDuration(results);
          // Adjust
          this.filler.fillLapsNumber(results);
          break;
        case KEEP_LAST:
          // TODO Should probably be merged with Best (best and last info present
          // in DTO) and then its just a matter of order
          this.filter.keepOnlyLast(results);
          break;
        case ORDER_FOR_RACE:
          this.order.orderForRace(results);
          break;
        case KEEP_BEST:
          this.filter.keepOnlyBest(results);
          break;
        case ORDER_BY_DURATION:
          this.order.orderByDuration(results);
          break;
        case ORDER_BY_LAST_SEEN:
          this.order.orderbyLastSeen(results);
        default:
          break;
      }
    }
    this.filler.ensureAllPilotsPresent(results, pilots);
  }

  private List<LapTimeDTO> getAllLapsDTOOrderedByStartDate(Long pilotId, Long sessionId, Long locationId, Long eventId,
                                                           Long categoryId) {
    List<LapTime> searchResults = getAllLapsOrderedByStartDate(pilotId, sessionId, locationId, eventId, categoryId);
    return this.lapTimeConverter.convert(searchResults);
  }

  private List<LapTime> getAllLapsOrderedByStartDate(Long pilotId, Long sessionId, Long locationId, Long eventId,
                                                     Long categoryId) {
    return this.laptimeRepository.getAllLapsOrderedByStartDate(pilotId, sessionId, locationId, eventId, categoryId);
  }

}
