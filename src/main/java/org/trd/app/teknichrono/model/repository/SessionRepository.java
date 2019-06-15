package org.trd.app.teknichrono.model.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.trd.app.teknichrono.model.dto.SessionDTO;
import org.trd.app.teknichrono.model.jpa.Chronometer;
import org.trd.app.teknichrono.model.jpa.Event;
import org.trd.app.teknichrono.model.jpa.Location;
import org.trd.app.teknichrono.model.jpa.Pilot;
import org.trd.app.teknichrono.model.jpa.Session;
import org.trd.app.teknichrono.util.exception.ConflictingIdException;
import org.trd.app.teknichrono.util.exception.NotFoundException;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.List;
import java.util.Set;

@Dependent
public class SessionRepository extends PanacheRepositoryWrapper<Session, SessionDTO> {

  @ApplicationScoped
  public static class Panache implements PanacheRepository<Session> {
  }

  private final Panache panacheRepository;

  private final ChronometerRepository.Panache chronometerRepository;

  private final LocationRepository.Panache locationRepository;

  private final EventRepository.Panache eventRepository;

  private final PilotRepository.Panache pilotRepository;

  @Inject
  public SessionRepository(Panache panacheRepository, ChronometerRepository.Panache chronometerRepository,
                           LocationRepository.Panache locationRepository, EventRepository.Panache eventRepository,
                           PilotRepository.Panache pilotRepository) {
    super(panacheRepository);
    this.panacheRepository = panacheRepository;
    this.chronometerRepository = chronometerRepository;
    this.locationRepository = locationRepository;
    this.eventRepository = eventRepository;
    this.pilotRepository = pilotRepository;
  }

  public Session findByName(String name) {
    return panacheRepository.find("name", name).firstResult();
  }


  public void deleteById(long id) throws NotFoundException {
    Session entity = panacheRepository.findById(id);
    if (entity == null) {
      throw new NotFoundException();
    }

    List<Chronometer> chronometers = entity.getChronometers();
    if (chronometers != null) {
      for (Chronometer chrono : chronometers) {
        chrono.getSessions().removeIf(p -> p.getId() == id);
        chronometerRepository.persist(chrono);
      }
    }
    Set<Pilot> pilots = entity.getPilots();
    if (pilots != null) {
      for (Pilot pilot : pilots) {
        pilot.getSessions().removeIf(p -> p.getId() == id);
        pilotRepository.persist(pilot);
      }
    }
    Location associatedLocation = entity.getLocation();
    if (associatedLocation != null) {
      associatedLocation.getSessions().removeIf(p -> p.getId() == id);
      locationRepository.persist(associatedLocation);
    }
    Event associatedEvent = entity.getEvent();
    if (associatedEvent != null) {
      associatedEvent.getSessions().removeIf(p -> p.getId() == id);
      eventRepository.persist(associatedEvent);
    }
    panacheRepository.delete(entity);
  }

  @Override
  public String getEntityName() {
    return Session.class.getName();
  }

  @Override
  public void create(SessionDTO entity) throws ConflictingIdException, NotFoundException {

  }

  @Override
  public Session fromDTO(SessionDTO dto) throws ConflictingIdException, NotFoundException {
    return null;
  }

  @Override
  public SessionDTO toDTO(Session dto) {
    return null;
  }

  @Override
  public void update(long id, SessionDTO dto) throws ConflictingIdException, NotFoundException {

  }
}