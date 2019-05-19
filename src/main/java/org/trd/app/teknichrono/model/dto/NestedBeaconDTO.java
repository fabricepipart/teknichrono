package org.trd.app.teknichrono.model.dto;

import org.trd.app.teknichrono.model.jpa.Beacon;

import javax.persistence.EntityManager;

public class NestedBeaconDTO {

  private long id;
  private int version;

  private long number;

  public NestedBeaconDTO() {
  }

  public NestedBeaconDTO(final Beacon entity) {
    if (entity != null) {
      this.id = entity.id;
      this.version = entity.getVersion();
      this.number = entity.getNumber();
    }
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("[id=" + getId());
    sb.append(",number=" + number + "]");
    return sb.toString();
  }

  //
  public Beacon fromDTO(Beacon entity, EntityManager em) {
    if (entity == null) {
      entity = new Beacon();
    }
    entity.setVersion(this.version);
    entity.setNumber(this.number);
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

  public long getNumber() {
    return number;
  }

  public void setNumber(long number) {
    this.number = number;
  }

}
