package org.trd.app.teknichrono.model.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.trd.app.teknichrono.model.dto.LocationDTO;
import org.trd.app.teknichrono.model.jpa.Location;
import org.trd.app.teknichrono.model.jpa.Session;
import org.trd.app.teknichrono.util.exception.ConflictingIdException;
import org.trd.app.teknichrono.util.exception.NotFoundException;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class LocationRepository extends PanacheRepositoryWrapper<Location, LocationDTO> {

  @ApplicationScoped
  public static class Panache implements PanacheRepository<Location> {
  }

  private final Panache panacheRepository;

  private final SessionRepository.Panache sessionRepository;

  @Inject
  public LocationRepository(Panache panacheRepository, SessionRepository.Panache sessionRepository) {
    super(panacheRepository);
    this.panacheRepository = panacheRepository;
    this.sessionRepository = sessionRepository;
  }

  @Override
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
    checkNoId(entity);
    Location location = new Location();
    location.setLoopTrack(entity.isLoopTrack());
    location.setName(entity.getName());
    setOneToManyRelationship(location, entity.getSessions(), Location::getSessions, Session::setLocation, sessionRepository);
    return location;
  }

  public LocationDTO addSession(long locationId, long sessionId) throws NotFoundException {
    Location location = ensureFindById(locationId);
    Session session = addToOneToManyRelationship(location, sessionId, Location::getSessions, Session::setLocation, sessionRepository);
    sessionRepository.persist(session);
    persist(location);
    return LocationDTO.fromLocation(location);
  }

  @Override
  public void deleteById(long id) throws NotFoundException {
    Location entity = ensureFindById(id);
    nullifyOneToManyRelationship(entity.getSessions(), Session::setLocation, sessionRepository);
    panacheRepository.delete(entity);
  }

  @Override
  public void update(long id, LocationDTO entity) throws ConflictingIdException, NotFoundException {
    checkIdsMatch(id, entity);
    Location location = ensureFindById(id);
    location.setName(entity.getName());
    location.setLoopTrack(entity.isLoopTrack());

    // Update of pilots
    setOneToManyRelationship(location, entity.getSessions(), Location::getSessions, Session::setLocation, sessionRepository);
    panacheRepository.persist(location);
  }
}
