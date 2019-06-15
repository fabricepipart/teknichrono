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
import java.util.ArrayList;
import java.util.List;

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

  public void create(BeaconDTO entity) throws NotFoundException, ConflictingIdException {
    Beacon beacon = fromDTO(entity);
    panacheRepository.persist(beacon);
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
    Beacon beacon = new Beacon();
    if (entity.getId() > 0) {
      throw new ConflictingIdException("Can't create Beacon with already an ID");
    }
    beacon.setNumber(entity.getNumber());
    if (entity.getPilot() != null && entity.getPilot().getId() > 0) {
      Pilot pilot = pilotRepository.findById(entity.getPilot().getId());
      if (pilot == null) {
        throw new NotFoundException("Pilot not found with ID=" + entity.getPilot().getId());
      }
      beacon.setPilot(pilot);
      pilot.setCurrentBeacon(beacon);
    }
    return beacon;
  }


  public void update(long id, BeaconDTO entity) throws ConflictingIdException, NotFoundException {
    if (id != entity.getId()) {
      throw new ConflictingIdException();
    }
    Beacon beacon = findById(id);
    if (beacon == null) {
      throw new NotFoundException();
    }

    // Update of pilot
    beacon.setPilot(null);
    if (entity.getPilot() != null && entity.getPilot().getId() > 0) {
      Pilot pilot = pilotRepository.findById(entity.getPilot().getId());
      beacon.setPilot(pilot);
    }
    beacon.setNumber(entity.getNumber());
    panacheRepository.persist(beacon);
  }

  public void deleteById(long id) throws NotFoundException {
    Beacon entity = findById(id);
    if (entity == null) {
      throw new NotFoundException();
    }
    Pilot associatedPilot = entity.getPilot();
    if (associatedPilot != null) {
      associatedPilot.setCurrentBeacon(null);
      pilotRepository.persist(associatedPilot);
    }
    List<Ping> pings = new ArrayList<>(entity.getPings());
    for (Ping ping : pings) {
      ping.setBeacon(null);
      pingRepository.persist(ping);
    }
    panacheRepository.delete(entity);
  }
}
