package org.trd.app.teknichrono.model.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.trd.app.teknichrono.model.dto.ChronometerDTO;
import org.trd.app.teknichrono.model.jpa.Chronometer;
import org.trd.app.teknichrono.model.jpa.Session;
import org.trd.app.teknichrono.util.exception.ConflictingIdException;
import org.trd.app.teknichrono.util.exception.NotFoundException;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.List;

@Dependent
public class ChronometerRepository extends PanacheRepositoryWrapper<Chronometer, ChronometerDTO> {

  @ApplicationScoped
  public static class Panache implements PanacheRepository<Chronometer> {
  }

  private final Panache panacheRepository;

  private final SessionRepository.Panache sessionRepository;

  @Inject
  public ChronometerRepository(Panache panacheRepository, SessionRepository.Panache sessionRepository) {
    super(panacheRepository);
    this.panacheRepository = panacheRepository;
    this.sessionRepository = sessionRepository;
  }


  public Chronometer findByName(String name) {
    return panacheRepository.find("name", name).firstResult();
  }


  public void deleteById(long id) throws NotFoundException {
    Chronometer entity = panacheRepository.findById(id);
    if (entity == null) {
      throw new NotFoundException();
    }

    List<Session> sessions = entity.getSessions();
    if (sessions != null) {
      for (Session session : sessions) {
        session.getChronometers().removeIf(c -> c.getId() == id);
        sessionRepository.persist(session);
      }
    }
    panacheRepository.delete(entity);
  }

  @Override
  public String getEntityName() {
    return Chronometer.class.getName();
  }

  @Override
  public void create(ChronometerDTO entity) throws ConflictingIdException, NotFoundException {

  }

  @Override
  public Chronometer fromDTO(ChronometerDTO dto) throws ConflictingIdException, NotFoundException {
    return null;
  }

  @Override
  public ChronometerDTO toDTO(Chronometer dto) {
    return null;
  }

  @Override
  public void update(long id, ChronometerDTO dto) throws ConflictingIdException, NotFoundException {

  }
}
