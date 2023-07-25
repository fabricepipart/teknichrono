package org.trd.app.teknichrono.model.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.trd.app.teknichrono.model.dto.LapTimeDTO;
import org.trd.app.teknichrono.model.jpa.Category;
import org.trd.app.teknichrono.model.jpa.Event;
import org.trd.app.teknichrono.model.jpa.LapTime;
import org.trd.app.teknichrono.model.jpa.Location;
import org.trd.app.teknichrono.model.jpa.Pilot;
import org.trd.app.teknichrono.model.jpa.Session;
import org.trd.app.teknichrono.util.exception.ConflictingIdException;
import org.trd.app.teknichrono.util.exception.NotFoundException;
import org.trd.app.teknichrono.util.sql.OrderByClauseBuilder;
import org.trd.app.teknichrono.util.sql.WhereClauseBuilder;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Dependent
public class LapTimeRepository extends PanacheRepositoryWrapper<LapTime, LapTimeDTO> {

  @ApplicationScoped
  public static class Panache implements PanacheRepository<LapTime> {
  }

  private final Panache panacheRepository;

  private final PilotRepository.Panache pilotRepository;
  private final SessionRepository.Panache sessionRepository;
  private final LocationRepository.Panache locationRepository;
  private final EventRepository.Panache eventRepository;
  private final CategoryRepository.Panache categoryRepository;

  @Inject
  public LapTimeRepository(Panache panacheRepository, PilotRepository.Panache pilotRepository,
                           SessionRepository.Panache sessionRepository, LocationRepository.Panache locationRepository,
                           EventRepository.Panache eventRepository, CategoryRepository.Panache categoryRepository) {
    super(panacheRepository);
    this.panacheRepository = panacheRepository;
    this.pilotRepository = pilotRepository;
    this.sessionRepository = sessionRepository;
    this.locationRepository = locationRepository;
    this.eventRepository = eventRepository;
    this.categoryRepository = categoryRepository;
  }

  @Override
  public String getEntityName() {
    return LapTime.class.getSimpleName();
  }

  @Override
  public LapTime create(LapTimeDTO dto) throws ConflictingIdException, NotFoundException {
    LapTime laptime = fromDTO(dto);
    this.panacheRepository.persist(laptime);
    return laptime;
  }

  @Override
  public LapTime fromDTO(LapTimeDTO dto) throws ConflictingIdException, NotFoundException {
    checkNoId(dto);
    LapTime laptime = new LapTime();
    laptime.setStartDate(dto.getStartDate());
    setManyToOneRelationship(laptime, dto.getPilot(), LapTime::setPilot, Pilot::getLaps, this.pilotRepository);
    // No reverse relationship
    if (dto.getSession() != null) {
      Session s = ensureFindFieldById(dto.getSession().getId(), this.sessionRepository);
      laptime.setSession(s);
    }
    // Create create with Pings (not even in DTO)
    return laptime;
  }

  @Override
  public LapTimeDTO toDTO(LapTime entity) {
    return LapTimeDTO.fromLapTime(entity);
  }

  @Override
  public void update(long id, LapTimeDTO dto) throws ConflictingIdException, NotFoundException {
    checkIdsMatch(id, dto);
    LapTime laptime = ensureFindById(id);
    laptime.setStartDate(dto.getStartDate());
    setManyToOneRelationship(laptime, dto.getPilot(), LapTime::setPilot, Pilot::getLaps, this.pilotRepository);
    // No reverse relationship
    if (dto.getSession() != null) {
      Session s = ensureFindFieldById(dto.getSession().getId(), this.sessionRepository);
      laptime.setSession(s);
    }
    this.panacheRepository.persist(laptime);
  }

  public List<LapTime> getAllLapsOrderedByStartDate(Long pilotId, Long sessionId, Long locationId, Long eventId, Long categoryId) {
    WhereClauseBuilder whereClauseBuilder = buildWhereClause(pilotId, sessionId, locationId, eventId, categoryId);
    OrderByClauseBuilder orderByClauseBuilder = new OrderByClauseBuilder();
    // Necessary to have the lapTimeManager.manage working
    orderByClauseBuilder.add("l.startDate");

    String query = "SELECT DISTINCT l FROM LapTime l"
        + " LEFT JOIN FETCH l.pilot LEFT JOIN FETCH l.session LEFT JOIN FETCH l.intermediates"
        + whereClauseBuilder.build() + orderByClauseBuilder.build();

    Map<String, Object> parametersMap = whereClauseBuilder.getParametersMap();
    return this.panacheRepository.find(query, parametersMap).list();
  }

  private WhereClauseBuilder buildWhereClause(Long pilotId, Long sessionId, Long locationId, Long eventId,
                                              Long categoryId) {
    WhereClauseBuilder whereClauseBuilder = new WhereClauseBuilder();
    if (pilotId != null) {
      whereClauseBuilder.addEqualsClause("l.pilot.id", "pilotId", pilotId);
    } else {
      if (categoryId != null) {
        Category category = this.categoryRepository.findById(categoryId);
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
        Event event = this.eventRepository.findById(eventId);
        if (event != null) {
          List<Long> eventSessionIds = new ArrayList<>();
          for (Session s : event.getSessions()) {
            eventSessionIds.add(s.id);
          }
          whereClauseBuilder.addInClause("l.session.id", "eventSessionIds", eventSessionIds);
        }
      }
      if (locationId != null) {
        Location location = this.locationRepository.findById(locationId);
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
