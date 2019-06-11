package org.trd.app.teknichrono.model.jpa;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.trd.app.teknichrono.model.dto.LocationDTO;
import org.trd.app.teknichrono.model.dto.NestedSessionDTO;
import org.trd.app.teknichrono.util.exception.ConflictingIdException;
import org.trd.app.teknichrono.util.exception.NotFoundException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Set;

@ApplicationScoped
public class LocationRepository extends PanacheRepositoryWrapper<Location> implements EntityRepository<Location, LocationDTO> {

  @ApplicationScoped
  public static class Panache implements PanacheRepository<Location> {
  }

  private final Panache panacheRepository;

  private final SessionRepository.Panache sessionRepository;

  protected LocationRepository() {
    // Only needed because of Weld proxy being a subtype of current type: https://stackoverflow.com/a/48418256/2989857
    this(null, null);
  }

  @Inject
  public LocationRepository(Panache panacheRepository, SessionRepository.Panache sessionRepository) {
    super(panacheRepository);
    this.panacheRepository = panacheRepository;
    this.sessionRepository = sessionRepository;
  }

  public void create(LocationDTO entity) throws ConflictingIdException, NotFoundException {
    Location location = fromDTO(entity);
    panacheRepository.persist(location);
  }

  @Override
  public String getEntityName() {
    return Location.class.getName();
  }

  @Override
  public LocationDTO toDTO(Location dto) {
    return LocationDTO.fromLocation(dto);
  }

  @Override
  public Location fromDTO(LocationDTO entity) throws ConflictingIdException, NotFoundException {
    Location location = new Location();
    if (entity.getId() > 0) {
      throw new ConflictingIdException("Can't create Location with already an ID");
    }
    location.setLoopTrack(entity.isLoopTrack());
    location.setName(entity.getName());

    Set<NestedSessionDTO> associatedSessions = entity.getSessions();
    if (associatedSessions != null) {
      for (NestedSessionDTO associatedSession : associatedSessions) {
        Session session = sessionRepository.findById(associatedSession.getId());
        if (session == null) {
          throw new NotFoundException("Session not found with ID=" + associatedSession.getId());
        }
        session.setLocation(location);
        location.getSessions().add(session);
      }
    }
    return location;
  }

  public LocationDTO addSession(long locationId, long sessionId) throws NotFoundException {
    Location location = findById(locationId);
    if (location == null) {
      throw new NotFoundException("Location not found with ID=" + locationId);
    }
    Session session = sessionRepository.findById(sessionId);
    if (session == null) {
      throw new NotFoundException("Session not found with ID=" + sessionId);
    }
    session.setLocation(location);
    location.getSessions().add(session);
    persist(location);
    sessionRepository.persist(session);
    return LocationDTO.fromLocation(location);
  }

  public Location findByName(String name) {
    return panacheRepository.find("name", name).firstResult();
  }


  public void deleteById(long id) throws NotFoundException {
    Location entity = panacheRepository.findById(id);
    if (entity == null) {
      throw new NotFoundException();
    }

    Set<Session> sessions = entity.getSessions();
    if (sessions != null) {
      for (Session session : sessions) {
        session.setLocation(null);
        sessionRepository.persist(session);
      }
    }
    panacheRepository.delete(entity);
  }

  public void update(long id, LocationDTO entity) throws ConflictingIdException, NotFoundException {
    if (id != entity.getId()) {
      throw new ConflictingIdException();
    }
    Location location = findById(id);
    if (location == null) {
      throw new NotFoundException("Location not found with ID=" + id);
    }

    location.setName(entity.getName());
    location.setLoopTrack(entity.isLoopTrack());
    // Update of pilots
    location.getSessions().clear();
    if (entity.getSessions() != null) {
      for (NestedSessionDTO sessionDto : entity.getSessions()) {
        Session session = sessionRepository.findById(sessionDto.getId());
        if (session == null) {
          throw new NotFoundException("Session not found with ID=" + sessionDto.getId());
        }
        location.getSessions().add(session);
        session.setLocation(location);
        sessionRepository.persist(session);
      }
    }
    panacheRepository.persist(location);
  }
}
