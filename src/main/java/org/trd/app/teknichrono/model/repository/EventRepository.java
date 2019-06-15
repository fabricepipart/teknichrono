package org.trd.app.teknichrono.model.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.trd.app.teknichrono.model.dto.EventDTO;
import org.trd.app.teknichrono.model.jpa.Event;
import org.trd.app.teknichrono.model.jpa.Session;
import org.trd.app.teknichrono.util.exception.ConflictingIdException;
import org.trd.app.teknichrono.util.exception.NotFoundException;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.List;

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

  public Event findByName(String name) {
    return panacheRepository.find("name", name).firstResult();
  }


  public void deleteById(long id) throws NotFoundException {
    Event entity = panacheRepository.findById(id);
    if (entity == null) {
      throw new NotFoundException();
    }

    List<Session> sessions = entity.getSessions();
    if (sessions != null) {
      for (Session session : sessions) {
        session.setEvent(null);
        sessionRepository.persist(session);
      }
    }
    panacheRepository.delete(entity);
  }

  @Override
  public String getEntityName() {
    return Event.class.getName();
  }

  @Override
  public void create(EventDTO entity) throws ConflictingIdException, NotFoundException {

  }

  @Override
  public Event fromDTO(EventDTO dto) throws ConflictingIdException, NotFoundException {
    return null;
  }

  @Override
  public EventDTO toDTO(Event dto) {
    return null;
  }

  @Override
  public void update(long id, EventDTO dto) throws ConflictingIdException, NotFoundException {

  }

}
