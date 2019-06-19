package org.trd.app.teknichrono.rest;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import org.jboss.logging.Logger;
import org.trd.app.teknichrono.model.repository.Repository;
import org.trd.app.teknichrono.util.DurationLogger;
import org.trd.app.teknichrono.util.exception.ConflictingIdException;
import org.trd.app.teknichrono.util.exception.NotFoundException;

import javax.persistence.OptimisticLockException;
import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EntityEndpoint<E extends PanacheEntity, D> {

  private static final Logger LOGGER = Logger.getLogger(EntityEndpoint.class);
  private final Repository<E, D> repository;

  public EntityEndpoint(Repository<E, D> repository) {
    this.repository = repository;
  }


  public Response create(D entity, String identifier) {
    try (DurationLogger dl = DurationLogger.get(LOGGER).start("Create " + repository.getEntityName() + " " + identifier)) {
      try {
        repository.create(entity);
      } catch (NotFoundException e) {
        return Response.status(Response.Status.NOT_FOUND).build();
      } catch (ConflictingIdException e) {
        return Response.status(Response.Status.CONFLICT).build();
      }
      return Response.noContent().build();
    }
  }

  public Response deleteById(long id) {
    try (DurationLogger dl = DurationLogger.get(LOGGER).start("Delete " + repository.getEntityName() + " id=" + id)) {
      try {
        repository.deleteById(id);
      } catch (NotFoundException e) {
        return Response.status(Response.Status.NOT_FOUND).build();
      }
      return Response.noContent().build();
    }
  }

  public Response findById(long id) {
    try (DurationLogger dl = DurationLogger.get(LOGGER).start("Find " + repository.getEntityName() + " id=" + id)) {
      E entity = repository.findById(id);
      if (entity == null) {
        return Response.status(Response.Status.NOT_FOUND).build();
      }
      D dto = repository.toDTO(entity);
      return Response.ok(dto).build();
    }
  }

  public List<D> listAll(Integer pageIndex, Integer pageSize) {
    try (DurationLogger dl = DurationLogger.get(LOGGER).start("Find all " + repository.getEntityName())) {
      return repository.findAll(pageIndex, pageSize).map(repository::toDTO).collect(Collectors.toList());
    }
  }


  public <F extends PanacheEntity> Response addToCollectionField(long entityId, long fieldEntityId, Repository<F, ?> elementRepository,
                                                                 BiConsumer<F, E> fieldEntitySetter,
                                                                 Function<E, ? extends Collection<F>> entityListGetter) {
    try (DurationLogger dl = DurationLogger.get(LOGGER).start("Add entity id=" + fieldEntityId +
        " to list field of entity id=" + entityId)) {
      try {
        D dto = repository.addToOneToManyRelationship(entityId, fieldEntityId, elementRepository,
            fieldEntitySetter, entityListGetter);
        return Response.ok(dto).build();
      } catch (NotFoundException e) {
        return Response.status(Response.Status.NOT_FOUND).build();
      }
    }
  }


  public Response update(long id, D dto) {
    try (DurationLogger dl = DurationLogger.get(LOGGER).start("Update " + repository.getEntityName() + " id=" + id)) {
      if (dto == null) {
        return Response.status(Response.Status.BAD_REQUEST).build();
      }
      try {
        repository.update(id, dto);
      } catch (OptimisticLockException e) {
        return Response.status(Response.Status.CONFLICT).entity(e.getEntity()).build();
      } catch (NotFoundException e) {
        return Response.status(Response.Status.NOT_FOUND).build();
      } catch (ConflictingIdException e) {
        return Response.status(Response.Status.CONFLICT).entity(dto).build();
      }
      return Response.noContent().build();
    }
  }

  public Response findByField(String fieldName, Object fieldValue) {
    try (DurationLogger dl = DurationLogger.get(LOGGER).start("Find " + repository.getEntityName() + " " +
        fieldName + "=" + fieldValue)) {
      E entity = repository.findByField(fieldName, fieldValue);
      if (entity == null) {
        LOGGER.warn(repository.getEntityName() + " " + fieldName + "=" + fieldValue + " not found");
        return Response.status(Response.Status.NOT_FOUND).build();
      }
      D dto = repository.toDTO(entity);
      return Response.ok(dto).build();
    }
  }
}
