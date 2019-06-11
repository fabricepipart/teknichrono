package org.trd.app.teknichrono.model.jpa;

import org.trd.app.teknichrono.util.exception.ConflictingIdException;
import org.trd.app.teknichrono.util.exception.NotFoundException;

import java.util.stream.Stream;

public interface EntityRepository<E, D> {

  String getEntityName();

  E findById(Long id);

  Stream<E> findAll(Integer pageIndex, Integer pageSize);

  void create(D entity) throws ConflictingIdException, NotFoundException;

  E fromDTO(D dto) throws ConflictingIdException, NotFoundException;

  D toDTO(E dto);

  void deleteById(long id) throws NotFoundException;

  void update(long id, D dto) throws ConflictingIdException, NotFoundException;

  E findByField(String fieldName, Object fieldValue);

}
