package org.trd.app.teknichrono.model.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.trd.app.teknichrono.model.dto.BeaconDTO;
import org.trd.app.teknichrono.model.jpa.Beacon;
import org.trd.app.teknichrono.model.jpa.Pilot;
import org.trd.app.teknichrono.model.jpa.Ping;
import org.trd.app.teknichrono.util.exception.ConflictingIdException;
import org.trd.app.teknichrono.util.exception.NotFoundException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

@Dependent
public class BeaconRepository extends PanacheRepositoryWrapper<Beacon, BeaconDTO> {

  @ApplicationScoped
  public static class Panache implements PanacheRepository<Beacon> {
  }

  private final Panache panacheRepository;

  private final PilotRepository.Panache pilotRepository;

  private final PingRepository.Panache pingRepository;

  @Inject
  public BeaconRepository(Panache panacheRepository, PilotRepository.Panache pilotRepository,
                          PingRepository.Panache pingRepository) {
    super(panacheRepository);
    this.panacheRepository = panacheRepository;
    this.pilotRepository = pilotRepository;
    this.pingRepository = pingRepository;
  }

  @Override
  public Beacon create(BeaconDTO entity) throws NotFoundException, ConflictingIdException {
    Beacon beacon = fromDTO(entity);
    panacheRepository.persist(beacon);
    return beacon;
  }

  @Override
  public String getEntityName() {
    return Beacon.class.getSimpleName();
  }

  @Override
  public BeaconDTO toDTO(Beacon dto) {
    return BeaconDTO.fromBeacon(dto);
  }

  @Override
  public Beacon fromDTO(BeaconDTO entity) throws ConflictingIdException, NotFoundException {
    checkNoId(entity);
    Beacon beacon = new Beacon();
    beacon.setNumber(entity.getNumber());
    setOneToOneRelationship(beacon, entity.getPilot(), Beacon::setPilot, Pilot::setCurrentBeacon, pilotRepository);
    // Create create with Pings (not even in DTO)
    return beacon;
  }

  @Override
  public void update(long id, BeaconDTO entity) throws ConflictingIdException, NotFoundException {
    checkIdsMatch(id, entity);
    Beacon beacon = ensureFindById(id);
    beacon.setNumber(entity.getNumber());

    // Update of pilot
    updateOneToOneRelationship(beacon, entity.getPilot(), Beacon::setPilot, Pilot::setCurrentBeacon, pilotRepository);
    // Create update with Pings (not even in DTO)
    panacheRepository.persist(beacon);
  }

  @Override
  public void deleteById(long id) throws NotFoundException {
    Beacon entity = ensureFindById(id);
    nullifyOneToOneRelationship(entity.getPilot(), Pilot::setCurrentBeacon, pilotRepository);
    nullifyOneToManyRelationship(entity.getPings(), Ping::setBeacon, pingRepository);
    panacheRepository.delete(entity);
  }
}
