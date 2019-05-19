package org.trd.app.teknichrono.model.dto;

import org.trd.app.teknichrono.model.jpa.Chronometer;

import javax.persistence.EntityManager;

public class NestedChronometerDTO {

  private long id;
  private int version;

  private String name;

  public NestedChronometerDTO() {
  }

  public NestedChronometerDTO(final Chronometer entity) {
    if (entity != null) {
      this.id = entity.id;
      this.version = entity.getVersion();
      this.name = entity.getName();
    }
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("[id=" + getId());
    sb.append(",name=" + name + "]");
    return sb.toString();
  }

  //
  public Chronometer fromDTO(Chronometer entity, EntityManager em) {
    if (entity == null) {
      entity = new Chronometer();
    }
    entity.setVersion(this.version);
    entity.setName(this.name);
    entity = em.merge(entity);
    return entity;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public int getVersion() {
    return version;
  }

  public void setVersion(int version) {
    this.version = version;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

}
