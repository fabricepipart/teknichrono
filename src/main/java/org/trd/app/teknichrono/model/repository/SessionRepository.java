package org.trd.app.teknichrono.model.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.jboss.logging.Logger;
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
import java.time.Instant;

@Dependent
public class SessionRepository extends PanacheRepositoryWrapper<Session, SessionDTO> {

  private Logger LOGGER = Logger.getLogger(SessionRepository.class);

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

  @Override
  public void deleteById(long id) throws NotFoundException {
    Session entity = ensureFindById(id);

    removeFromManyToManyRelationship(id, entity.getChronometers(), Chronometer::getSessions, chronometerRepository);
    removeFromManyToManyRelationship(id, entity.getPilots(), Pilot::getSessions, pilotRepository);
    removeFromManyToOneRelationship(id, entity.getLocation(), Location::getSessions, locationRepository);
    removeFromManyToOneRelationship(id, entity.getEvent(), Event::getSessions, eventRepository);

    panacheRepository.delete(entity);
  }

  @Override
  public String getEntityName() {
    return Session.class.getSimpleName();
  }

  @Override
  public Session create(SessionDTO entity) throws ConflictingIdException, NotFoundException {
    Session session = fromDTO(entity);
    panacheRepository.persist(session);
    return session;
  }

  @Override
  public Session fromDTO(SessionDTO dto) throws ConflictingIdException, NotFoundException {
    checkNoId(dto);
    Session session = new Session();
    session.setInactivity(dto.getInactivity());
    session.setStart(dto.getStart());
    session.setEnd(dto.getEnd());
    session.setType(dto.getType());
    session.setCurrent(dto.isCurrent());
    session.setName(dto.getName());

    setManyToManyRelationship(session, dto.getChronometers(), Session::getChronometers, Chronometer::getSessions, chronometerRepository);
    setManyToManyRelationship(session, dto.getPilots(), Session::getPilots, Pilot::getSessions, pilotRepository);
    setManyToOneRelationship(session, dto.getLocation(), Session::setLocation, Location::getSessions, locationRepository);
    setManyToOneRelationship(session, dto.getEvent(), Session::setEvent, Event::getSessions, eventRepository);

    // Create create with Pings (not even in DTO)
    return session;
  }

  @Override
  public SessionDTO toDTO(Session dto) {
    return SessionDTO.fromSession(dto);
  }

  @Override
  public void update(long id, SessionDTO dto) throws ConflictingIdException, NotFoundException {
    checkIdsMatch(id, dto);
    Session session = ensureFindById(id);

    session.setInactivity(dto.getInactivity());
    session.setStart(dto.getStart());
    session.setEnd(dto.getEnd());
    session.setType(dto.getType());
    session.setCurrent(dto.isCurrent());
    session.setName(dto.getName());

    setManyToManyRelationship(session, dto.getChronometers(), Session::getChronometers, Chronometer::getSessions, chronometerRepository);
    setManyToManyRelationship(session, dto.getPilots(), Session::getPilots, Pilot::getSessions, pilotRepository);
    setManyToOneRelationship(session, dto.getLocation(), Session::setLocation, Location::getSessions, locationRepository);
    setManyToOneRelationship(session, dto.getEvent(), Session::setEvent, Event::getSessions, eventRepository);

    panacheRepository.persist(session);
  }


  public void endSession(Session session, Instant end) {
    session.setEnd(end);
    session.setCurrent(false);
    panacheRepository.persist(session);
  }

  public void startSession(Session session, Instant start) {
    session.setStart(start);
    session.setCurrent(true);
    panacheRepository.persist(session);

  }

  public PilotRepository.Panache getPilotRepository() {
    return pilotRepository;
  }

  public Session addChronometerAtIndex(Long sessionId, Long chronoId, Integer index) throws NotFoundException {
    Session session = ensureFindById(sessionId);
    boolean alreadyPresent = session.getChronometers().stream().anyMatch(chrono -> (chrono.getId().longValue() == chronoId.longValue()));
    if (alreadyPresent) {
      LOGGER.warn("Did not add Chrono #" + chronoId + " to Session #" + sessionId + " since it was already present");
      return session;
    }
    Chronometer chronometer = ensureFindFieldById(chronoId, chronometerRepository);
    chronometer.getSessions().add(session);
    if (index != null) {
      session.addChronometer(chronometer, index);
    } else {
      session.addChronometer(chronometer);
    }
    persist(session);
    for (Chronometer c : session.getChronometers()) {
      chronometerRepository.persist(c);
    }
    return session;
  }

}