package org.trd.app.teknichrono.rest.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.trd.app.teknichrono.model.Pilot;

public class NestedPilotDTO implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = -6351473312188582718L;
  private int id;
  private String firstName;
  private String lastName;
  private int beaconNumber;

  public NestedPilotDTO() {
  }

  public NestedPilotDTO(final Pilot entity) {
    if (entity != null) {
      this.id = entity.getId();
      this.firstName = entity.getFirstName();
      this.lastName = entity.getLastName();
      if (entity.getCurrentBeacon() != null) {
        this.beaconNumber = entity.getCurrentBeacon().getNumber();
      }
    }
  }

  public static Set<NestedPilotDTO> fromPilots(Set<Pilot> pilots) {
    Set<NestedPilotDTO> toReturn = new HashSet<NestedPilotDTO>();
    if (pilots != null) {
      for (Pilot p : pilots) {
        toReturn.add(new NestedPilotDTO(p));
      }
    }
    return toReturn;
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

  public int getBeaconNumber() {
    return beaconNumber;
  }

  public void setBeaconNumber(int beaconNumber) {
    this.beaconNumber = beaconNumber;
  }
}