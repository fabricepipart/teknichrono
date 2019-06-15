package org.trd.app.teknichrono.model.repository;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.trd.app.teknichrono.rest.Paging;
import org.trd.app.teknichrono.util.exception.NotFoundException;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

abstract class PanacheRepositoryWrapper<E extends PanacheEntity, D> implements Repository<E, D> {

  private final PanacheRepository<E> panacheRepository;

  PanacheRepositoryWrapper(PanacheRepository<E> panacheRepository) {
    this.panacheRepository = panacheRepository;
  }

  @Override
  public E findById(Long id) {
    return panacheRepository.findById(id);
  }

  @Override
  public Stream<E> findAll(Integer pageIndex, Integer pageSize) {
    return panacheRepository.findAll()
        .page(Paging.from(pageIndex, pageSize))
        .stream();
  }

  @Override
  public void persist(E entity) {
    panacheRepository.persist(entity);
  }

  @Override
  public void deleteById(long id) throws NotFoundException {
    E entity = findById(id);
    if (entity == null) {
      throw new NotFoundException();
    }
    panacheRepository.delete(entity);
  }

  public E findByField(String fieldName, Object fieldValue) {
    return panacheRepository.find(fieldName, fieldValue).firstResult();
  }


  public <F extends PanacheEntity> D addToCollectionField(long entityId, long fieldId, Repository<F, ?> elementRepository,
                                                          BiConsumer<F, E> fieldEntitySetter,
                                                          Function<E, ? extends Collection<F>> entityListGetter) throws NotFoundException {
    E entity = findById(entityId);
    if (entity == null) {
      throw new NotFoundException("Entity not found with ID=" + entityId);
    }
    F fieldEntity = elementRepository.findById(fieldId);
    if (fieldEntity == null) {
      throw new NotFoundException("List Field entity not found with ID=" + fieldId);
    }
    fieldEntitySetter.accept(fieldEntity, entity);
    entityListGetter.apply(entity).add(fieldEntity);
    persist(entity);
    elementRepository.persist(fieldEntity);
    return toDTO(entity);
  }

}
