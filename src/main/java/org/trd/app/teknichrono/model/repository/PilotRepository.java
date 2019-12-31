package org.trd.app.teknichrono.model.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.trd.app.teknichrono.model.dto.PilotDTO;
import org.trd.app.teknichrono.model.jpa.Beacon;
import org.trd.app.teknichrono.model.jpa.Category;
import org.trd.app.teknichrono.model.jpa.Pilot;
import org.trd.app.teknichrono.model.jpa.Session;
import org.trd.app.teknichrono.util.exception.ConflictingIdException;
import org.trd.app.teknichrono.util.exception.NotFoundException;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class PilotRepository extends PanacheRepositoryWrapper<Pilot, PilotDTO> {

  @ApplicationScoped
  public static class Panache implements PanacheRepository<Pilot> {
  }

  private final Panache panacheRepository;
  private final BeaconRepository.Panache beaconRepository;
  private final CategoryRepository.Panache categoryRepository;
  private final SessionRepository.Panache sessionRepository;

  @Inject
  public PilotRepository(Panache panacheRepository, BeaconRepository.Panache beaconRepository,
                         CategoryRepository.Panache categoryRepository,
                         SessionRepository.Panache sessionRepository) {
    super(panacheRepository);
    this.panacheRepository = panacheRepository;
    this.beaconRepository = beaconRepository;
    this.categoryRepository = categoryRepository;
    this.sessionRepository = sessionRepository;
  }

  public Pilot findByName(String firstname, String lastname) {
    return this.panacheRepository.find("firstName = ?1 AND lastName = ?2", firstname, lastname).firstResult();
  }

  @Override
  public String getEntityName() {
    return Pilot.class.getSimpleName();
  }

  @Override
  public Pilot create(PilotDTO entity) throws ConflictingIdException, NotFoundException {
    Pilot pilot = fromDTO(entity);
    this.panacheRepository.persist(pilot);
    return pilot;
  }

  @Override
  public Pilot fromDTO(PilotDTO entity) throws ConflictingIdException, NotFoundException {
    checkNoId(entity);

    Pilot pilot = new Pilot();
    pilot.setFirstName(entity.getFirstName());
    pilot.setLastName(entity.getLastName());
    pilot.setNickname(entity.getNickname());

    setOneToOneRelationship(pilot, entity.getCurrentBeacon(), Pilot::setCurrentBeacon, Beacon::setPilot, this.beaconRepository);
    setManyToOneRelationship(pilot, entity.getCategory(), Pilot::setCategory, Category::getPilots, this.categoryRepository);
    // Don't create with Laps
    // Don't create with Sessions

    return pilot;
  }

  @Override
  public PilotDTO toDTO(Pilot p) {
    return PilotDTO.fromPilot(p);
  }


  @Override
  public void deleteById(long id) throws NotFoundException {
    Pilot entity = ensureFindById(id);

    removeFromManyToOneRelationship(id, entity.getCategory(), Category::getPilots, this.categoryRepository);
    nullifyOneToOneRelationship(entity.getCurrentBeacon(), Beacon::setPilot, this.beaconRepository);
    // Cascading should do its job for Laps or we should create a deleteFromOneToManyRelationship
    removeFromManyToManyRelationship(id, entity.getSessions(), Session::getPilots, this.sessionRepository);

    this.panacheRepository.delete(entity);
  }

  @Override
  public void update(long id, PilotDTO dto) throws ConflictingIdException, NotFoundException {
    checkIdsMatch(id, dto);
    Pilot pilot = ensureFindById(id);

    pilot.setFirstName(dto.getFirstName());
    pilot.setLastName(dto.getLastName());
    pilot.setNickname(dto.getNickname());
    // Don't update Laps
    // Don't update Sessions

    // Update of category
    setManyToOneRelationship(pilot, dto.getCategory(), Pilot::setCategory, Category::getPilots, this.categoryRepository);
    updateOneToOneRelationship(pilot, dto.getCurrentBeacon(), Pilot::setCurrentBeacon, Beacon::setPilot, this.beaconRepository);

    this.panacheRepository.persist(pilot);
  }


  public PilotDTO associateBeacon(long pilotId, long beaconId) throws NotFoundException {
    Pilot entity = ensureFindById(pilotId);
    Beacon beacon = setOneToOneRelationship(entity, beaconId, Pilot::setCurrentBeacon, Beacon::setPilot, this.beaconRepository);
    persist(entity);
    this.beaconRepository.persist(beacon);
    PilotDTO dto = toDTO(entity);
    return dto;
  }
}
