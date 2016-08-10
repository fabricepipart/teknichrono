package org.trd.app.teknichrono.rest.dto;

import java.io.Serializable;
import org.trd.app.teknichrono.model.Pilot;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

public class NestedPilotDTO implements Serializable {

  private int id;
  private int version;
  private String firstName;
  private String lastName;

  public NestedPilotDTO() {
  }

  public NestedPilotDTO(final Pilot entity) {
    if (entity != null) {
      this.id = entity.getId();
      this.version = entity.getVersion();
      this.firstName = entity.getFirstName();
      this.lastName = entity.getLastName();
    }
  }

  public Pilot fromDTO(Pilot entity, EntityManager em) {
    if (entity == null) {
      entity = new Pilot();
    }
    if (((Integer) this.id) != null) {
      TypedQuery<Pilot> findByIdQuery = em.createQuery("SELECT DISTINCT p FROM Pilot p WHERE p.id = :entityId",
          Pilot.class);
      findByIdQuery.setParameter("entityId", this.id);
      try {
        entity = findByIdQuery.getSingleResult();
      } catch (javax.persistence.NoResultException nre) {
        entity = null;
      }
      return entity;
    }
    entity.setVersion(this.version);
    entity.setFirstName(this.firstName);
    entity.setLastName(this.lastName);
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

  public String getFirstName() {
    return this.firstName;
  }

  public void setFirstName(final String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return this.lastName;
  }

  public void setLastName(final String lastName) {
    this.lastName = lastName;
  }
}