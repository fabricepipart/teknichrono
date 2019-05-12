package org.trd.app.teknichrono.model.dto;

import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.trd.app.teknichrono.model.jpa.Location;

public class NestedLocationDTO implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = -1377429742860544608L;

  private Long id;
  private int version;
  private String name;

  public NestedLocationDTO() {
  }

  public NestedLocationDTO(final Location entity) {
    if (entity != null) {
      this.id = entity.id;
      this.version = entity.getVersion();
      this.name = entity.getName();
    }
  }

  public Location fromDTO(Location entity, EntityManager em) {
    if (entity == null) {
      entity = new Location();
    }
    if (id != null) {
      TypedQuery<Location> findByIdQuery = em.createQuery("SELECT DISTINCT e FROM Location e WHERE e.id = :entityId",
          Location.class);
      findByIdQuery.setParameter("entityId", this.id);
      try {
        entity = findByIdQuery.getSingleResult();
      } catch (javax.persistence.NoResultException nre) {
        entity = null;
      }
      return entity;
    }
    entity.setVersion(this.version);
    entity.setName(name);
    entity = em.merge(entity);
    return entity;
  }

  public long getId() {
    return this.id;
  }

  public void setId(final long id) {
    this.id = id;
  }

  public int getVersion() {
    return this.version;
  }

  public void setVersion(final int version) {
    this.version = version;
  }

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }

}