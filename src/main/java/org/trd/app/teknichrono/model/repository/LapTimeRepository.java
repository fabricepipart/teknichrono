package org.trd.app.teknichrono.model.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.trd.app.teknichrono.model.dto.LapTimeDTO;
import org.trd.app.teknichrono.model.jpa.LapTime;
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

  @Inject
  public LapTimeRepository(Panache panacheRepository) {
    super(panacheRepository);
    this.panacheRepository = panacheRepository;
  }

  @Override
  public String getEntityName() {
    return LapTime.class.getName();
  }

  @Override
  public void create(LapTimeDTO entity) throws ConflictingIdException, NotFoundException {

  }

  @Override
  public LapTime fromDTO(LapTimeDTO dto) throws ConflictingIdException, NotFoundException {
    return null;
  }

  @Override
  public LapTimeDTO toDTO(LapTime dto) {
    return null;
  }

  @Override
  public void update(long id, LapTimeDTO dto) throws ConflictingIdException, NotFoundException {

  }
}
