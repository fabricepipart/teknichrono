package org.trd.app.teknichrono.model.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.trd.app.teknichrono.model.dto.LapTimeDTO;
import org.trd.app.teknichrono.model.jpa.LapTime;
import org.trd.app.teknichrono.model.jpa.Pilot;
import org.trd.app.teknichrono.model.jpa.Session;
import org.trd.app.teknichrono.util.exception.ConflictingIdException;
import org.trd.app.teknichrono.util.exception.NotFoundException;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class LapTimeRepository extends PanacheRepositoryWrapper<LapTime, LapTimeDTO> {

  @ApplicationScoped
  public static class Panache implements PanacheRepository<LapTime> {
  }

  private final Panache panacheRepository;

  private final PilotRepository.Panache pilotRepository;
  private final SessionRepository.Panache sessionRepository;

  @Inject
  public LapTimeRepository(Panache panacheRepository, PilotRepository.Panache pilotRepository, SessionRepository.Panache sessionRepository) {
    super(panacheRepository);
    this.panacheRepository = panacheRepository;
    this.pilotRepository = pilotRepository;
    this.sessionRepository = sessionRepository;
  }

  @Override
  public String getEntityName() {
    return LapTime.class.getSimpleName();
  }

  @Override
  public LapTime create(LapTimeDTO dto) throws ConflictingIdException, NotFoundException {
    LapTime laptime = fromDTO(dto);
    panacheRepository.persist(laptime);
    return laptime;
  }

  @Override
  public LapTime fromDTO(LapTimeDTO dto) throws ConflictingIdException, NotFoundException {
    checkNoId(dto);
    LapTime laptime = new LapTime();
    laptime.setStartDate(dto.getStartDate());
    setManyToOneRelationship(laptime, dto.getPilot(), LapTime::setPilot, Pilot::getLaps, pilotRepository);
    // No reverse relationship
    if (dto.getSession() != null) {
      Session s = ensureFindFieldById(dto.getSession().getId(), sessionRepository);
      laptime.setSession(s);
    }
    // Create create with Pings (not even in DTO)
    return laptime;
  }

  @Override
  public LapTimeDTO toDTO(LapTime entity) {
    return LapTimeDTO.fromLapTime(entity);
  }

  @Override
  public void update(long id, LapTimeDTO dto) throws ConflictingIdException, NotFoundException {
    checkIdsMatch(id, dto);
    LapTime laptime = ensureFindById(id);
    laptime.setStartDate(dto.getStartDate());
    setManyToOneRelationship(laptime, dto.getPilot(), LapTime::setPilot, Pilot::getLaps, pilotRepository);
    // No reverse relationship
    if (dto.getSession() != null) {
      Session s = ensureFindFieldById(dto.getSession().getId(), sessionRepository);
      laptime.setSession(s);
    }
    panacheRepository.persist(laptime);
  }
}
