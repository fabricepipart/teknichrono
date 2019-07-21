package org.trd.app.teknichrono.model.repository;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import org.trd.app.teknichrono.util.exception.ConflictingIdException;
import org.trd.app.teknichrono.util.exception.NotFoundException;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

public interface Repository<E extends PanacheEntity, D> {

  E findById(Long id);

  /**
   * Returns up to <code>pageSize</code> results starting from <code>pageIndex * pageSize</code>
   *
   * @param pageIndex the index (0-based) of the result page you want to get.
   *                  If <code>null</code>, then the first page (index 0) is assumed.
   * @param pageSize  the size of each page.
   *                  If <code>null</code>, <code>Integer.MAX_VALUE</code> is assumed.
   * @return up to <code>pageSize</code> results starting from <code>pageIndex * pageSize</code>
   */
  Stream<E> findAll(Integer pageIndex, Integer pageSize);

  void persist(E entity);

  void deleteById(long id) throws NotFoundException;

  String getEntityName();

  void create(D entity) throws ConflictingIdException, NotFoundException;

  E fromDTO(D dto) throws ConflictingIdException, NotFoundException;

  D toDTO(E dto);

  void update(long id, D dto) throws ConflictingIdException, NotFoundException;

  E findByField(String fieldName, Object fieldValue);

  <F extends PanacheEntity> D addToOneToManyRelationship(long entityId, long fieldId, Repository<F, ?> elementRepository,
                                                         BiConsumer<F, E> fieldEntitySetter,
                                                         Function<E, ? extends Collection<F>> entityListGetter)
      throws NotFoundException;
}
