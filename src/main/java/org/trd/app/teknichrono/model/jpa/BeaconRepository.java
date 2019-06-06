package org.trd.app.teknichrono.model.jpa;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.trd.app.teknichrono.model.dto.BeaconDTO;
import org.trd.app.teknichrono.rest.Paging;
import org.trd.app.teknichrono.util.exception.MissingIdException;
import org.trd.app.teknichrono.util.exception.NotFoundException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class BeaconRepository implements PanacheRepository<Beacon> {

  @Inject
  PilotRepository pilotRepository;

  @Inject
  PingRepository pingRepository;

  public Beacon findByNumber(long number) {
    return find("number", number).firstResult();
  }

  public List<BeaconDTO> findAll(Integer startPosition, Integer maxResult) {
    return findAll()
            .page(Paging.from(startPosition, maxResult))
            .stream()
            .map(BeaconDTO::fromBeacon)
            .collect(Collectors.toList());
  }

  public void create(Beacon entity) throws NotFoundException {
    if (entity.getPilot() != null && entity.getPilot().id > 0) {
      // TODO is that needed?
      Pilot pilot = pilotRepository.findById(entity.getPilot().id);
      if (pilot == null) {
        throw new NotFoundException("Pilot not found with ID=" + entity.getPilot().id);
      }
      entity.setPilot(pilot);
      pilot.setCurrentBeacon(entity);
      pilotRepository.persist(pilot);
    }
    persist(entity);
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
    if (entity.getPilot() != null && entity.getPilot().getId() > 0) {
      Pilot pilot = pilotRepository.findById(entity.getPilot().getId());
      beacon.setPilot(pilot);
    }
    beacon.setNumber(entity.getNumber());
    persist(beacon);
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
    if (pings != null) {
      for (Ping ping : pings) {
        ping.setBeacon(null);
        pingRepository.persist(ping);
      }
    }
    delete(entity);
  }
}
