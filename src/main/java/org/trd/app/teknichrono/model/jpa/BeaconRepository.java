package org.trd.app.teknichrono.model.jpa;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.trd.app.teknichrono.model.dto.BeaconDTO;
import org.trd.app.teknichrono.util.exception.ConflictingIdException;
import org.trd.app.teknichrono.util.exception.MissingIdException;
import org.trd.app.teknichrono.util.exception.NotFoundException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class BeaconRepository extends PanacheRepositoryWrapper<Beacon> {

  @ApplicationScoped
  public static class Panache implements PanacheRepository<Beacon> {
  }

  private final Panache panacheRepository;

  private final PilotRepository pilotRepository;

  private final PingRepository pingRepository;

  protected BeaconRepository() {
    // Only needed because of Weld proxy being a subtype of current type: https://stackoverflow.com/a/48418256/2989857
    this(null, null, null);
  }

  @Inject
  public BeaconRepository(Panache panacheRepository, PilotRepository pilotRepository, PingRepository pingRepository) {
    super(panacheRepository);
    this.panacheRepository = panacheRepository;
    this.pilotRepository = pilotRepository;
    this.pingRepository = pingRepository;
  }

  public Beacon findByNumber(long number) {
    return panacheRepository.find("number", number).firstResult();
  }

  public void create(BeaconDTO entity) throws NotFoundException, ConflictingIdException {
    Beacon beacon = fromDTO(entity);
    panacheRepository.persist(beacon);
  }

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


  public void updateBeacon(long id, BeaconDTO entity) throws MissingIdException, NotFoundException {
    if (id != entity.getId()) {
      throw new MissingIdException();
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
