package org.trd.app.teknichrono.model.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.trd.app.teknichrono.model.dto.ChronometerDTO;
import org.trd.app.teknichrono.model.jpa.Chronometer;
import org.trd.app.teknichrono.model.jpa.Ping;
import org.trd.app.teknichrono.model.jpa.Session;
import org.trd.app.teknichrono.util.exception.ConflictingIdException;
import org.trd.app.teknichrono.util.exception.NotFoundException;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class ChronometerRepository extends PanacheRepositoryWrapper<Chronometer, ChronometerDTO> {

  @ApplicationScoped
  public static class Panache implements PanacheRepository<Chronometer> {
  }

  private final Panache panacheRepository;

  private final SessionRepository.Panache sessionRepository;

  private final PingRepository.Panache pingRepository;

  @Inject
  public ChronometerRepository(Panache panacheRepository, SessionRepository.Panache sessionRepository,
                               PingRepository.Panache pingRepository) {
    super(panacheRepository);
    this.panacheRepository = panacheRepository;
    this.sessionRepository = sessionRepository;
    this.pingRepository = pingRepository;
  }

  @Override
  public void deleteById(long id) throws NotFoundException {
    Chronometer entity = ensureFindById(id);
    nullifyOneToManyRelationship(entity.getPings(), Ping::setChrono, pingRepository);
    removeFromManyToManyRelationship(id, entity.getSessions(), Session::getChronometers, sessionRepository);
    panacheRepository.delete(entity);
  }

  @Override
  public String getEntityName() {
    return Chronometer.class.getSimpleName();
  }

  @Override
  public Chronometer create(ChronometerDTO entity) throws ConflictingIdException, NotFoundException {
    Chronometer chronometer = fromDTO(entity);
    panacheRepository.persist(chronometer);
    return chronometer;
  }

  @Override
  public Chronometer fromDTO(ChronometerDTO dto) throws ConflictingIdException, NotFoundException {
    checkNoId(dto);
    Chronometer chronometer = new Chronometer();
    setFromDto(dto, chronometer);

    // Sessions are not updated (not part of DTO)
    // Pings are not updated (not part of DTO)
    return chronometer;
  }

  private void setFromDto(ChronometerDTO dto, Chronometer chronometer) {
    chronometer.setName(dto.getName());
    if (dto.getSelectionStrategy() != null) {
      chronometer.setSelectionStrategy(Chronometer.PingSelectionStrategy.valueOf(dto.getSelectionStrategy()));
    }
    if (dto.getSendStrategy() != null) {
      chronometer.setSendStrategy(Chronometer.PingSendStrategy.valueOf(dto.getSendStrategy()));
    }
    chronometer.setInactivityWindow(dto.getInactivityWindow());
    chronometer.setBluetoothDebug(dto.isBluetoothDebug());
    chronometer.setDebug(dto.isDebug());
    if (dto.getOrderToExecute() != null) {
      chronometer.setOrderToExecute(Chronometer.ChronometerOrder.valueOf(dto.getOrderToExecute()));
    }
  }

  @Override
  public ChronometerDTO toDTO(Chronometer dto) {
    return ChronometerDTO.fromChronometer(dto);
  }


  public ChronometerDTO ack(long id) throws NotFoundException {
    Chronometer entity = ensureFindById(id);
    entity.setOrderToExecute(null);
    panacheRepository.persist(entity);
    return ChronometerDTO.fromChronometer(entity);
  }

  @Override
  public void update(long id, ChronometerDTO dto) throws ConflictingIdException, NotFoundException {
    checkIdsMatch(id, dto);
    Chronometer chronometer = ensureFindById(id);
    setFromDto(dto, chronometer);
    // Sessions are not updated (not part of DTO)
    // Pings are not updated (not part of DTO)
    panacheRepository.persist(chronometer);
  }
}
