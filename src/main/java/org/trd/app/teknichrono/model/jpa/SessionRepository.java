package org.trd.app.teknichrono.model.jpa;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.trd.app.teknichrono.util.exception.NotFoundException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.Set;

@ApplicationScoped
public class SessionRepository extends PanacheRepositoryWrapper<Session> {

  @ApplicationScoped
  public static class Panache implements PanacheRepository<Session> {
  }

  private final Panache panacheRepository;

  private final ChronometerRepository.Panache chronometerRepository;

  private final LocationRepository.Panache locationRepository;

  private final EventRepository.Panache eventRepository;

  private final PilotRepository.Panache pilotRepository;

  protected SessionRepository() {
    // Only needed because of Weld proxy being a subtype of current type: https://stackoverflow.com/a/48418256/2989857
    this(null, null, null, null, null);
  }

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
}