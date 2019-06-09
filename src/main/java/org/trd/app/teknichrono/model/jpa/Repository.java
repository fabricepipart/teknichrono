package org.trd.app.teknichrono.model.jpa;

import org.trd.app.teknichrono.util.exception.NotFoundException;

import java.util.stream.Stream;

public interface Repository<T> {

    T findById(Long id);

    Stream<T> findAll(Integer startPosition, Integer maxResult);

    void persist(T entity);

    void deleteById(long id) throws NotFoundException;
}
