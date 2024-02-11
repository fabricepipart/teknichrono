package org.trd.app.teknichrono.model.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.trd.app.teknichrono.model.dto.EventDTO;
import org.trd.app.teknichrono.model.jpa.Event;
import org.trd.app.teknichrono.model.jpa.Session;
import org.trd.app.teknichrono.util.exception.ConflictingIdException;
import org.trd.app.teknichrono.util.exception.NotFoundException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

@Dependent
public class EventRepository extends PanacheRepositoryWrapper<Event, EventDTO> {


  @ApplicationScoped
  public static class Panache implements PanacheRepository<Event> {
  }

  private final Panache panacheRepository;

  private final SessionRepository.Panache sessionRepository;

  @Inject
  public EventRepository(Panache panacheRepository, SessionRepository.Panache sessionRepository) {
    super(panacheRepository);
    this.panacheRepository = panacheRepository;
    this.sessionRepository = sessionRepository;
  }

  @Override
  public void deleteById(long id) throws NotFoundException {
    Event entity = ensureFindById(id);
    nullifyOneToManyRelationship(entity.getSessions(), Session::setEvent, sessionRepository);
    panacheRepository.delete(entity);
  }

  @Override
  public String getEntityName() {
    return Event.class.getSimpleName();
  }

  @Override
  public Event create(EventDTO entity) throws ConflictingIdException, NotFoundException {
    Event event = fromDTO(entity);
    panacheRepository.persist(event);
    return event;
  }

  @Override
  public Event fromDTO(EventDTO dto) throws ConflictingIdException, NotFoundException {
    checkNoId(dto);
    Event event = new Event();
    event.setName(dto.getName());
    setOneToManyRelationship(event, dto.getSessions(), Event::getSessions, Session::setEvent, sessionRepository);
    return event;
  }

  @Override
  public EventDTO toDTO(Event event) {
    return EventDTO.fromEvent(event);
  }

  @Override
  public void update(long id, EventDTO dto) throws ConflictingIdException, NotFoundException {
    checkIdsMatch(id, dto);
    Event event = ensureFindById(id);
    event.setName(dto.getName());
    // Update of sessions
    setOneToManyRelationship(event, dto.getSessions(), Event::getSessions, Session::setEvent, sessionRepository);
    panacheRepository.persist(event);
  }

  public PanacheRepository getSessionRepository() {
    return sessionRepository;
  }

}
