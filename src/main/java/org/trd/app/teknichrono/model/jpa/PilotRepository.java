package org.trd.app.teknichrono.model.jpa;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.trd.app.teknichrono.model.dto.PilotDTO;
import org.trd.app.teknichrono.util.exception.ConflictingIdException;
import org.trd.app.teknichrono.util.exception.NotFoundException;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.List;
import java.util.Set;

@Dependent
public class PilotRepository extends PanacheRepositoryWrapper<Pilot> implements EntityRepository<Pilot, PilotDTO> {

  @ApplicationScoped
  public static class Panache implements PanacheRepository<Pilot> {
  }

  private final Panache panacheRepository;

  private final BeaconRepository.Panache beaconRepository;

  private final CategoryRepository.Panache categoryRepository;

  private final LapTimeRepository.Panache laptimeRepository;

  private final SessionRepository.Panache sessionRepository;

  @Inject
  public PilotRepository(Panache panacheRepository, BeaconRepository.Panache beaconRepository,
                         CategoryRepository.Panache categoryRepository, LapTimeRepository.Panache laptimeRepository,
                         SessionRepository.Panache sessionRepository) {
    super(panacheRepository);
    this.panacheRepository = panacheRepository;
    this.beaconRepository = beaconRepository;
    this.categoryRepository = categoryRepository;
    this.laptimeRepository = laptimeRepository;
    this.sessionRepository = sessionRepository;
  }

  public Pilot findByName(String firstname, String lastname) {
    return panacheRepository.find("firstName = ?1 AND lastName = ?2", firstname, lastname).firstResult();
  }

  @Override
  public String getEntityName() {
    return Pilot.class.getName();
  }

  public void create(PilotDTO entity) throws ConflictingIdException, NotFoundException {
    Pilot pilot = fromDTO(entity);
    panacheRepository.persist(pilot);
  }

  @Override
  public Pilot fromDTO(PilotDTO entity) throws ConflictingIdException, NotFoundException {
    Pilot pilot = new Pilot();
    if (entity.getId() > 0) {
      throw new ConflictingIdException("Can't create Pilot with already an ID");
    }
    pilot.setFirstName(entity.getFirstName());
    pilot.setLastName(entity.getLastName());
    if (entity.getCurrentBeacon() != null && entity.getCurrentBeacon().getId() > 0) {
      Beacon beacon = beaconRepository.findById(entity.getCurrentBeacon().getId());
      if (beacon == null) {
        throw new NotFoundException("Beacon not found with ID=" + entity.getCurrentBeacon().getId());
      }
      beacon.setPilot(pilot);
      pilot.setCurrentBeacon(beacon);
    }
    if (entity.getCategory() != null && entity.getCategory().getId() > 0) {
      Category category = categoryRepository.findById(entity.getCategory().getId());
      if (category == null) {
        throw new NotFoundException("Category not found with ID=" + entity.getCategory().getId());
      }
      category.getPilots().add(pilot);
      pilot.setCategory(category);
    }
    return pilot;
  }

  @Override
  public PilotDTO toDTO(Pilot p) {
    return PilotDTO.fromPilot(p);
  }


  public void deleteById(long id) throws NotFoundException {
    Pilot entity = findById(id);
    if (entity == null) {
      throw new NotFoundException();
    }

    Category associatedCategory = entity.getCategory();
    if (associatedCategory != null) {
      associatedCategory.getPilots().removeIf(p -> p.getId() == id);
      categoryRepository.persist(associatedCategory);
    }
    Beacon associatedBeacon = entity.getCurrentBeacon();
    if (associatedBeacon != null) {
      associatedBeacon.setPilot(null);
      beaconRepository.persist(associatedBeacon);
    }
    List<LapTime> laps = entity.getLaps();
    if (laps != null) {
      for (LapTime lap : laps) {
        lap.setPilot(null);
        laptimeRepository.persist(lap);
      }
    }
    Set<Session> sessions = entity.getSessions();
    if (sessions != null) {
      for (Session session : sessions) {
        session.getPilots().removeIf(p -> p.getId() == id);
        sessionRepository.persist(session);
      }
    }
    panacheRepository.delete(entity);
  }


  public PilotDTO associateBeacon(long pilotId, long beaconId) throws NotFoundException {
    Pilot pilot = findById(pilotId);
    if (pilot == null) {
      throw new NotFoundException("Pilot not found with ID=" + pilotId);
    }
    Beacon beacon = beaconRepository.findById(beaconId);
    if (beacon == null) {
      throw new NotFoundException("Beacon not found with ID=" + beaconId);
    }
    pilot.setCurrentBeacon(beacon);
    beacon.setPilot(pilot);
    persist(pilot);
    beaconRepository.persist(beacon);
    return PilotDTO.fromPilot(pilot);
  }


  public void update(long id, PilotDTO dto) throws ConflictingIdException, NotFoundException {
    if (id != dto.getId()) {
      throw new ConflictingIdException();
    }
    Pilot pilot = findById(id);
    if (pilot == null) {
      throw new NotFoundException("Pilot not found with ID=" + id);
    }

    pilot.setFirstName(dto.getFirstName());
    pilot.setLastName(dto.getLastName());
    // Update of category
    pilot.setCategory(null);
    if (dto.getCategory() != null && dto.getCategory().getId() > 0) {
      Category category = categoryRepository.findById(dto.getCategory().getId());
      if (category == null) {
        throw new NotFoundException("Category not found with ID=" + id);
      }
      category.getPilots().add(pilot);
      pilot.setCategory(category);
      categoryRepository.persist(category);
    }
    // Update of beacon
    pilot.setCurrentBeacon(null);
    if (dto.getCurrentBeacon() != null && dto.getCurrentBeacon().getId() > 0) {
      Beacon beacon = beaconRepository.findById(dto.getCurrentBeacon().getId());
      if (beacon == null) {
        throw new NotFoundException("Beacon not found with ID=" + id);
      }
      beacon.setPilot(pilot);
      pilot.setCurrentBeacon(beacon);
      beaconRepository.persist(beacon);
    }
    panacheRepository.persist(pilot);
  }
}
