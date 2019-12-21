package org.trd.app.teknichrono.model.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Sort;
import org.jboss.logging.Logger;
import org.trd.app.teknichrono.model.dto.LogDTO;
import org.trd.app.teknichrono.model.jpa.Chronometer;
import org.trd.app.teknichrono.model.jpa.Log;
import org.trd.app.teknichrono.rest.Paging;
import org.trd.app.teknichrono.util.exception.ConflictingIdException;
import org.trd.app.teknichrono.util.exception.NotFoundException;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.time.Instant;
import java.util.stream.Stream;

@Dependent
public class LogRepository extends PanacheRepositoryWrapper<Log, LogDTO> {

  private static final Logger LOGGER = Logger.getLogger(LogRepository.class);


  @ApplicationScoped
  public static class Panache implements PanacheRepository<Log> {
  }

  private final LogRepository.Panache panacheRepository;

  private final ChronometerRepository.Panache chronometerRepository;

  @Inject
  public LogRepository(LogRepository.Panache panacheRepository, ChronometerRepository.Panache chronometerRepository) {
    super(panacheRepository);
    this.panacheRepository = panacheRepository;
    this.chronometerRepository = chronometerRepository;
  }

  @Override
  public String getEntityName() {
    return Log.class.getSimpleName();
  }

  @Override
  public Log create(LogDTO dto) throws ConflictingIdException, NotFoundException {
    Log log = fromDTO(dto);
    this.panacheRepository.persist(log);
    return log;
  }

  @Override
  public Log fromDTO(LogDTO dto) throws ConflictingIdException, NotFoundException {
    Log log = new Log();
    setFromDto(dto, log);
    return log;
  }

  private void setFromDto(LogDTO dto, Log log) throws NotFoundException {
    Instant date = dto.getDate();
    log.setDate(date);
    log.setLoggerName(dto.getLog());
    log.setLevel(dto.getLevel());
    log.setMessage(dto.getMessage());
    //setManyToOneRelationship(log, dto.getChronometer(), Log::setChronometer, Chronometer::getLogs, this.chronometerRepository);
    // If we use setManyToOneRelationship , we can end up with OptimisticLockException if two logs stored at the
    // same time because the version of the Chronometer will be increased by first log write
    if (dto.getChronometer() != null && dto.getChronometer().getId() != null) {
      Chronometer c = new Chronometer();
      // To throw a 404 if not found
      Long id = dto.getChronometer().getId();
      Chronometer byId = this.chronometerRepository.findById(id);
      if (byId == null) {
        throw new NotFoundException("Chronometer not found with ID=" + id);
      }
      c.setId(id);
      log.setChronometer(c);
    }
  }

  @Override
  public LogDTO toDTO(Log log) {
    return LogDTO.fromLog(log);
  }

  @Override
  public void update(long id, LogDTO dto) throws ConflictingIdException, NotFoundException {
    checkIdsMatch(id, dto);
    Log log = ensureFindById(id);
    setFromDto(dto, log);
    this.panacheRepository.persist(log);
  }

  public Stream<Log> findLogs(Chronometer chronometer, Instant fromInstant, Instant toInstant, Integer pageIndex,
                              Integer pageSize) {
    return this.panacheRepository
        .find("chronometer = ?1 and date >= ?2 and date <= ?3", Sort.by("date").descending(),
            chronometer, fromInstant, toInstant)
        .page(Paging.from(pageIndex, pageSize)).stream();
  }

}
