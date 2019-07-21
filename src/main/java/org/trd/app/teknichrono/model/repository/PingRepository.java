package org.trd.app.teknichrono.model.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.trd.app.teknichrono.model.dto.PingDTO;
import org.trd.app.teknichrono.model.jpa.Ping;
import org.trd.app.teknichrono.util.exception.ConflictingIdException;
import org.trd.app.teknichrono.util.exception.NotFoundException;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class PingRepository extends PanacheRepositoryWrapper<Ping, PingDTO> {

  @ApplicationScoped
  public static class Panache implements PanacheRepository<Ping> {
  }

  private final Panache panacheRepository;

  @Inject
  public PingRepository(Panache panacheRepository) {
    super(panacheRepository);
    this.panacheRepository = panacheRepository;
  }

  @Override
  public String getEntityName() {
    return Ping.class.getName();
  }

  @Override
  public void create(PingDTO entity) throws ConflictingIdException, NotFoundException {

  }

  @Override
  public Ping fromDTO(PingDTO dto) throws ConflictingIdException, NotFoundException {
    return null;
  }

  @Override
  public PingDTO toDTO(Ping dto) {
    return null;
  }

  @Override
  public void update(long id, PingDTO dto) throws ConflictingIdException, NotFoundException {

  }

}
