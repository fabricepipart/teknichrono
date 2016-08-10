package org.trd.app.teknichrono.rest.dto;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.trd.app.teknichrono.model.Ping;

public class NestedPingDTO implements Serializable {

  private int id;
  private int version;
  private Timestamp dateTime;
  private int power;

  public NestedPingDTO() {
  }

  public NestedPingDTO(final Ping entity) {
    if (entity != null) {
      this.id = entity.getId();
      this.version = entity.getVersion();
      this.dateTime = entity.getDateTime();
      this.power = entity.getPower();
    }
  }

  public Ping fromDTO(Ping entity, EntityManager em) {
    if (entity == null) {
      entity = new Ping();
    }
    if (((Integer) this.id) != null) {
      TypedQuery<Ping> findByIdQuery = em.createQuery("SELECT DISTINCT p FROM Ping p WHERE p.id = :entityId",
          Ping.class);
      findByIdQuery.setParameter("entityId", this.id);
      try {
        entity = findByIdQuery.getSingleResult();
      } catch (javax.persistence.NoResultException nre) {
        entity = null;
      }
      return entity;
    }
    entity.setVersion(this.version);
    entity.setDateTime(this.dateTime);
    entity.setPower(this.power);
    entity = em.merge(entity);
    return entity;
  }

  public int getId() {
    return this.id;
  }

  public void setId(final int id) {
    this.id = id;
  }

  public int getVersion() {
    return this.version;
  }

  public void setVersion(final int version) {
    this.version = version;
  }

  public Timestamp getDateTime() {
    return this.dateTime;
  }

  public void setDateTime(final Timestamp dateTime) {
    this.dateTime = dateTime;
  }

  public int getPower() {
    return this.power;
  }

  public void setPower(final int power) {
    this.power = power;
  }
}