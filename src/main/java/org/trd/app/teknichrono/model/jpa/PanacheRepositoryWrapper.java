package org.trd.app.teknichrono.model.jpa;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.trd.app.teknichrono.rest.Paging;
import org.trd.app.teknichrono.util.exception.NotFoundException;

import java.util.stream.Stream;

abstract class PanacheRepositoryWrapper<T> implements Repository<T> {

  private final PanacheRepository<T> panacheRepository;

  PanacheRepositoryWrapper(PanacheRepository<T> panacheRepository) {
    this.panacheRepository = panacheRepository;
  }

  @Override
  public T findById(Long id) {
    return panacheRepository.findById(id);
  }

  @Override
  public Stream<T> findAll(Integer pageIndex, Integer pageSize) {
    return panacheRepository.findAll()
        .page(Paging.from(pageIndex, pageSize))
        .stream();
  }

  @Override
  public void persist(T entity) {
    panacheRepository.persist(entity);
  }

  @Override
  public void deleteById(long id) throws NotFoundException {
    T entity = findById(id);
    if (entity == null) {
      throw new NotFoundException();
    }
    panacheRepository.delete(entity);
  }

}
