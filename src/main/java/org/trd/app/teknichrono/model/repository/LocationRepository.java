package org.trd.app.teknichrono.model.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.trd.app.teknichrono.model.dto.LocationDTO;
import org.trd.app.teknichrono.model.jpa.Location;
import org.trd.app.teknichrono.model.jpa.Session;
import org.trd.app.teknichrono.util.exception.ConflictingIdException;
import org.trd.app.teknichrono.util.exception.NotFoundException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

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
  public Location create(LocationDTO entity) throws ConflictingIdException, NotFoundException {
    Location location = fromDTO(entity);
    this.panacheRepository.persist(location);
    return location;
  }

  @Override
  public String getEntityName() {
    return Location.class.getSimpleName();
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
    location.setMinimum(entity.getMinimum());
    location.setMaximum(entity.getMaximum());
    setOneToManyRelationship(location, entity.getSessions(), Location::getSessions, Session::setLocation, this.sessionRepository);
    return location;
  }

  @Override
  public void deleteById(long id) throws NotFoundException {
    Location entity = ensureFindById(id);
    nullifyOneToManyRelationship(entity.getSessions(), Session::setLocation, this.sessionRepository);
    this.panacheRepository.delete(entity);
  }

  @Override
  public void update(long id, LocationDTO entity) throws ConflictingIdException, NotFoundException {
    checkIdsMatch(id, entity);
    Location location = ensureFindById(id);
    location.setName(entity.getName());
    location.setLoopTrack(entity.isLoopTrack());
    location.setMinimum(entity.getMinimum());
    location.setMaximum(entity.getMaximum());

    // Update of pilots
    setOneToManyRelationship(location, entity.getSessions(), Location::getSessions, Session::setLocation, this.sessionRepository);
    this.panacheRepository.persist(location);
  }

  public PanacheRepository getSessionRepository() {
    return this.sessionRepository;
  }

}
