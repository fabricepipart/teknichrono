package org.trd.app.teknichrono.rest.dto;

import javax.persistence.EntityManager;
import javax.xml.bind.annotation.XmlRootElement;

import org.trd.app.teknichrono.model.Beacon;

@XmlRootElement
public class NestedBeaconDTO {

  private int id;
  private int version;

  private int number;

  public NestedBeaconDTO() {
  }

  public NestedBeaconDTO(final Beacon entity) {
    if (entity != null) {
      this.id = entity.getId();
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

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getVersion() {
    return version;
  }

  public void setVersion(int version) {
    this.version = version;
  }

  public int getNumber() {
    return number;
  }

  public void setNumber(int number) {
    this.number = number;
  }

}
