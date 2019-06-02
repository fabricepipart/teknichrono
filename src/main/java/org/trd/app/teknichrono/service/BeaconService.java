package org.trd.app.teknichrono.service;

import org.trd.app.teknichrono.model.dto.BeaconDTO;
import org.trd.app.teknichrono.model.jpa.Beacon;
import org.trd.app.teknichrono.model.jpa.BeaconRepository;
import org.trd.app.teknichrono.model.jpa.Pilot;
import org.trd.app.teknichrono.model.jpa.PilotRepository;
import org.trd.app.teknichrono.model.jpa.Ping;
import org.trd.app.teknichrono.model.jpa.PingRepository;
import org.trd.app.teknichrono.rest.Paging;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@ApplicationScoped
public class BeaconService {

  @Inject
  PilotRepository pilotRepository;

  @Inject
  PingRepository pingRepository;

  @Inject
  BeaconRepository beaconRepository;


  public Beacon findById(Long aLong) {
    return beaconRepository.findById(aLong);
  }

  public Beacon findByNumber(long number) {
    return beaconRepository.findByNumber(number);
  }

  public List<BeaconDTO> findAll(Integer startPosition, Integer maxResult) {
    return beaconRepository.findAll()
        .page(Paging.from(startPosition, maxResult))
        .stream()
        .map(BeaconDTO::fromBeacon)
        .collect(Collectors.toList());
  }

  public void updateBeacon(long id, BeaconDTO entity) throws IllegalArgumentException, NoSuchElementException {
    if (id != entity.getId()) {
      throw new IllegalArgumentException();
    }
    Beacon beacon = beaconRepository.findById(id);
    if (beacon == null) {
      throw new NoSuchElementException();
    }

    // Update of pilot
    if (entity.getPilot() != null && entity.getPilot().getId() > 0) {
      Pilot pilot = pilotRepository.findById(entity.getPilot().getId());
      beacon.setPilot(pilot);
    }
    beacon.setNumber(entity.getNumber());
    beaconRepository.persist(beacon);
  }

  public void create(Beacon entity) {
    if (entity.getPilot() != null && entity.getPilot().id > 0) {
      Pilot pilot = pilotRepository.findById(entity.getPilot().id);
      entity.setPilot(pilot);
      pilotRepository.persist(pilot);
    }
    beaconRepository.persist(entity);
  }

  public void deleteById(long id) throws NoSuchElementException {
    Beacon entity = beaconRepository.findById(id);
    if (entity == null) {
      throw new NoSuchElementException();
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
    beaconRepository.delete(entity);
  }
}
