package org.trd.app.teknichrono.model.repository;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.jboss.logging.Logger;
import org.trd.app.teknichrono.model.dto.EntityDTO;
import org.trd.app.teknichrono.rest.Paging;
import org.trd.app.teknichrono.util.exception.ConflictingIdException;
import org.trd.app.teknichrono.util.exception.NotFoundException;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

abstract class PanacheRepositoryWrapper<E extends PanacheEntity, D> implements Repository<E, D> {


  private static final Logger LOGGER = Logger.getLogger(PanacheRepositoryWrapper.class);

  private final PanacheRepository<E> panacheRepository;

  PanacheRepositoryWrapper(PanacheRepository<E> panacheRepository) {
    this.panacheRepository = panacheRepository;
  }

  @Override
  public E findById(Long id) {
    return this.panacheRepository.findById(id);
  }

  @Override
  public Stream<E> findAll(Integer pageIndex, Integer pageSize) {
    return this.panacheRepository.findAll()
        .page(Paging.from(pageIndex, pageSize))
        .stream();
  }

  @Override
  public void persist(E entity) {
    this.panacheRepository.persist(entity);
  }

  @Override
  public void deleteById(long id) throws NotFoundException {
    E entity = findById(id);
    if (entity == null) {
      throw new NotFoundException();
    }
    this.panacheRepository.delete(entity);
  }

  @Override
  public E findByField(String fieldName, Object fieldValue) {
    return this.panacheRepository.find(fieldName, fieldValue).firstResult();
  }


  @Override
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

  protected <F extends PanacheEntity> void setField(E entity, EntityDTO fieldDto,
                                                    BiConsumer<E, F> setterEntity,
                                                    BiConsumer<F, E> setterFieldEntity,
                                                    PanacheRepository<F> fieldRepository)
      throws NotFoundException {
    if (fieldDto != null && fieldDto.getId() > 0) {
      F field = fieldRepository.findById(fieldDto.getId());
      if (field == null) {
        throw new NotFoundException("Field of " + getEntityName() + " not found with ID=" + fieldDto.getId());
      }
      setterEntity.accept(entity, field);
      setterFieldEntity.accept(field, entity);
    }
  }

  protected <F extends PanacheEntity> void setCollectionField(E entity, Collection<? extends EntityDTO> fieldDtos,
                                                              Function<E, Set<F>> entityCollectionGetter,
                                                              BiConsumer<F, E> setterFieldEntity,
                                                              PanacheRepository<F> fieldRepository)
      throws NotFoundException {
    entityCollectionGetter.apply(entity).clear();
    if (fieldDtos != null) {
      for (EntityDTO fieldDto : fieldDtos) {
        addToCollectionField(entity, fieldDto.getId(), entityCollectionGetter, setterFieldEntity, fieldRepository);
      }
    }
  }

  protected <F extends PanacheEntity> F addToCollectionField(E entity, Long fieldDtoId,
                                                             Function<E, Set<F>> entityCollectionGetter,
                                                             BiConsumer<F, E> setterFieldEntity,
                                                             PanacheRepository<F> fieldRepository) throws NotFoundException {
    if (fieldDtoId > 0) {
      F field = fieldRepository.findById(fieldDtoId);
      if (field == null) {
        throw new NotFoundException("Field of " + getEntityName() + " not found with ID=" + fieldDtoId);
      }
      entityCollectionGetter.apply(entity).add(field);
      setterFieldEntity.accept(field, entity);
      return field;
    } else {
      throw new NotFoundException("Tried to set an element of collection field of a " + getEntityName() +
          " without specifying ID");
    }
  }


  protected <F extends PanacheEntity> void updateField(E entity, EntityDTO fieldDto,
                                                       BiConsumer<E, F> setterEntity,
                                                       BiConsumer<F, E> setterFieldEntity,
                                                       PanacheRepository<F> fieldRepository) throws NotFoundException {
    setterEntity.accept(entity, null);
    if (fieldDto != null && fieldDto.getId() > 0) {
      setField(entity, fieldDto, setterEntity, setterFieldEntity, fieldRepository);
    }
  }

  protected <F extends PanacheEntity> void nullifyField(F field, BiConsumer<F, E> setterFieldEntity,
                                                        PanacheRepository<F> fieldRepository) {
    if (field != null) {
      setterFieldEntity.accept(field, null);
      fieldRepository.persist(field);
    }
  }

  protected <F extends PanacheEntity> void nullifyInCollectionField(Collection<F> fields, BiConsumer<F, E> setterFieldEntity,
                                                                    PanacheRepository<F> fieldRepository) {
    if (fields != null) {
      Set<F> fieldsSet = new HashSet<>(fields);
      for (F field : fieldsSet) {
        setterFieldEntity.accept(field, null);
        fieldRepository.persist(field);
      }
    }
  }

  protected <F extends PanacheEntity> void removeFromCollectionField(Long entityId, Collection<F> fields,
                                                                     Function<F, List<E>> getEntityFromFieldEntity,
                                                                     PanacheRepository<F> fieldRepository) {
    if (fields != null) {
      for (F field : fields) {
        Collection<E> entities = getEntityFromFieldEntity.apply(field);
        entities.removeIf(e -> e.id == entityId);
        fieldRepository.persist(field);
      }
    }
  }


  protected void checkNoId(EntityDTO entity) throws ConflictingIdException {
    if (entity.getId() != null && entity.getId() > 0) {
      throw new ConflictingIdException("Can't create " + getEntityName() + " with already an ID");
    }
  }

  protected void checkIdsMatch(long id, EntityDTO entity) throws ConflictingIdException {
    if (id != entity.getId()) {
      throw new ConflictingIdException();
    }
  }

  protected E ensureFindById(long id) throws NotFoundException {
    E entity = findById(id);
    if (entity == null) {
      throw new NotFoundException();
    }
    return entity;
  }

}
