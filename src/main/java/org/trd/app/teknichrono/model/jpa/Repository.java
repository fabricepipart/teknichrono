package org.trd.app.teknichrono.model.jpa;

import org.trd.app.teknichrono.util.exception.NotFoundException;

import java.util.stream.Stream;

public interface Repository<T> {

    T findById(Long id);

    /**
     * Returns up to <code>pageSize</code> results starting from <code>pageIndex * pageSize</code>
     *
     * @param pageIndex the index (0-based) of the result page you want to get.
     *                  If <code>null</code>, then the first page (index 0) is assumed.
     * @param pageSize  the size of each page.
     *                  If <code>null</code>, <code>Integer.MAX_VALUE</code> is assumed.
     * @return up to <code>pageSize</code> results starting from <code>pageIndex * pageSize</code>
     */
    Stream<T> findAll(Integer pageIndex, Integer pageSize);

    void persist(T entity);

    void deleteById(long id) throws NotFoundException;
}
