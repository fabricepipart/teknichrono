package org.trd.app.teknichrono.model.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.trd.app.teknichrono.model.dto.BeaconDTO;
import org.trd.app.teknichrono.model.jpa.Beacon;
import org.trd.app.teknichrono.model.jpa.Pilot;
import org.trd.app.teknichrono.model.jpa.Ping;
import org.trd.app.teknichrono.util.exception.ConflictingIdException;
import org.trd.app.teknichrono.util.exception.NotFoundException;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

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
  public void create(BeaconDTO entity) throws NotFoundException, ConflictingIdException {
    Beacon beacon = fromDTO(entity);
    this.panacheRepository.persist(beacon);
  }

  @Override
  public String getEntityName() {
    return Beacon.class.getName();
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
    setField(beacon, entity.getPilot(), Beacon::setPilot, Pilot::setCurrentBeacon, this.pilotRepository);
    // Create create with Pings (not even in DTO)
    return beacon;
  }

  @Override
  public void update(long id, BeaconDTO entity) throws ConflictingIdException, NotFoundException {
    checkIdsMatch(id, entity);
    Beacon beacon = ensureFindById(id);
    beacon.setNumber(entity.getNumber());

    // Update of pilot
    updateField(beacon, entity.getPilot(), Beacon::setPilot, Pilot::setCurrentBeacon, this.pilotRepository);
    // Create update with Pings (not even in DTO)
    this.panacheRepository.persist(beacon);
  }

  @Override
  public void deleteById(long id) throws NotFoundException {
    Beacon entity = ensureFindById(id);
    nullifyField(entity.getPilot(), Pilot::setCurrentBeacon, this.pilotRepository);
    nullifyInCollectionField(entity.getPings(), Ping::setBeacon, this.pingRepository);
    this.panacheRepository.delete(entity);
  }
}
