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
  public <F extends PanacheEntity> D addToOneToManyRelationship(long entityId, long fieldId,
                                                                Repository<F, ?> elementRepository,
                                                                BiConsumer<F, E> fieldEntitySetter,
                                                                Function<E, ? extends Collection<F>> entityListGetter)
      throws NotFoundException {
    E entity = ensureFindById(entityId);
    F fieldEntity = elementRepository.findById(fieldId);
    if (fieldEntity == null) {
      throw new NotFoundException("Field of " + getEntityName() + " not found with ID=" + fieldId);
    }
    fieldEntitySetter.accept(fieldEntity, entity);
    entityListGetter.apply(entity).add(fieldEntity);
    persist(entity);
    elementRepository.persist(fieldEntity);
    return toDTO(entity);
  }

  protected <F extends PanacheEntity> F addToOneToManyRelationship(E entity, Long fieldDtoId,
                                                                   Function<E, Set<F>> entityCollectionGetter,
                                                                   BiConsumer<F, E> setterFieldEntity,
                                                                   PanacheRepository<F> fieldRepository)
      throws NotFoundException {
    if (fieldDtoId > 0) {
      F field = ensureFindFieldById(fieldDtoId, fieldRepository);
      entityCollectionGetter.apply(entity).add(field);
      setterFieldEntity.accept(field, entity);
      return field;
    } else {
      throw new NotFoundException("Tried to set an element of collection field of a " + getEntityName() +
          " without specifying ID");
    }
  }

  protected <F extends PanacheEntity> void setOneToOneRelationship(E entity, EntityDTO fieldDto,
                                                                   BiConsumer<E, F> setterEntity,
                                                                   BiConsumer<F, E> setterFieldEntity,
                                                                   PanacheRepository<F> fieldRepository)
      throws NotFoundException {
    if (fieldDto != null && fieldDto.getId() > 0) {
      setOneToOneRelationship(entity, fieldDto, setterEntity, setterFieldEntity, fieldRepository);
    }
  }

  protected <F extends PanacheEntity> void setOneToOneRelationship(E entity, Long fieldId,
                                                                   BiConsumer<E, F> setterEntity,
                                                                   BiConsumer<F, E> setterFieldEntity,
                                                                   PanacheRepository<F> fieldRepository)
      throws NotFoundException {
    F field = ensureFindFieldById(fieldId, fieldRepository);
    setterEntity.accept(entity, field);
    setterFieldEntity.accept(field, entity);
  }

  protected <F extends PanacheEntity> void setOneToManyRelationship(E entity, Collection<? extends EntityDTO> fieldDtos,
                                                                    Function<E, Set<F>> entityCollectionGetter,
                                                                    BiConsumer<F, E> setterFieldEntity,
                                                                    PanacheRepository<F> fieldRepository)
      throws NotFoundException {
    entityCollectionGetter.apply(entity).clear();
    if (fieldDtos != null) {
      for (EntityDTO fieldDto : fieldDtos) {
        addToOneToManyRelationship(entity, fieldDto.getId(), entityCollectionGetter, setterFieldEntity, fieldRepository);
      }
    }
  }

  protected <F extends PanacheEntity> void setManyToOneRelationship(E entity, EntityDTO fieldDto,
                                                                    BiConsumer<E, F> setterEntity,
                                                                    Function<F, Collection<E>> entityCollectionGetter,
                                                                    PanacheRepository<F> fieldRepository)
      throws NotFoundException {
    if (fieldDto != null && fieldDto.getId() > 0) {
      F field = ensureFindFieldById(fieldDto.getId(), fieldRepository);
      entityCollectionGetter.apply(field).add(entity);
      setterEntity.accept(entity, field);
    }
  }

  protected <F extends PanacheEntity> void updateOneToOneRelationship(E entity, EntityDTO fieldDto,
                                                                      BiConsumer<E, F> setterEntity,
                                                                      BiConsumer<F, E> setterFieldEntity,
                                                                      PanacheRepository<F> fieldRepository)
      throws NotFoundException {
    setterEntity.accept(entity, null);
    if (fieldDto != null && fieldDto.getId() > 0) {
      setOneToOneRelationship(entity, fieldDto, setterEntity, setterFieldEntity, fieldRepository);
    }
  }

  protected <F extends PanacheEntity> void updateManyToOneRelationship(E entity, EntityDTO fieldDto,
                                                                       BiConsumer<E, F> setterEntity,
                                                                       Function<F, Collection<E>> entityCollectionGetter,
                                                                       PanacheRepository<F> fieldRepository)
      throws NotFoundException {
    setterEntity.accept(entity, null);
    if (fieldDto != null && fieldDto.getId() > 0) {
      setManyToOneRelationship(entity, fieldDto, setterEntity, entityCollectionGetter, fieldRepository);
    }
  }

  protected <F extends PanacheEntity> void nullifyOneToOneRelationship(F field, BiConsumer<F, E> setterFieldEntity,
                                                                       PanacheRepository<F> fieldRepository) {
    if (field != null) {
      setterFieldEntity.accept(field, null);
      fieldRepository.persist(field);
    }
  }

  protected <F extends PanacheEntity> void nullifyOneToManyRelationship(Collection<F> fields,
                                                                        BiConsumer<F, E> setterFieldEntity,
                                                                        PanacheRepository<F> fieldRepository) {
    if (fields != null) {
      Set<F> fieldsSet = new HashSet<>(fields);
      for (F field : fieldsSet) {
        setterFieldEntity.accept(field, null);
        fieldRepository.persist(field);
      }
    }
  }

  protected <F extends PanacheEntity> void removeFromManyToOneRelationship(Long entityId, F field,
                                                                           Function<F, Collection<E>> entityCollectionGetter,
                                                                           PanacheRepository<F> fieldRepository) {
    if (field != null) {
      entityCollectionGetter.apply(field).removeIf(e -> e.id == entityId);
      fieldRepository.persist(field);
    }
  }

  protected <F extends PanacheEntity> void removeFromManyToManyRelationship(Long entityId, Collection<F> fields,
                                                                            Function<F, Collection<E>> getEntityFromFieldEntity,
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

  protected <F extends PanacheEntity> F ensureFindFieldById(Long fieldDtoId, PanacheRepository<F> fieldRepository)
      throws NotFoundException {
    F field = fieldRepository.findById(fieldDtoId);
    if (field == null) {
      throw new NotFoundException("Field of " + getEntityName() + " not found with ID=" + fieldDtoId);
    }
    return field;
  }

}
