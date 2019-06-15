package org.trd.app.teknichrono.model.dto;

import lombok.Data;
import org.trd.app.teknichrono.model.jpa.Pilot;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

@Data
public class NestedPilotDTO implements EntityDTO {

  private Long id;
  private String firstName;
  private String lastName;
  private long beaconNumber;

  public static NestedPilotDTO fromPilot(Pilot pilot) {
    return DtoMapper.INSTANCE.asNestedPilotDto(pilot);
  }

  Pilot fromDTO(Pilot entity, EntityManager em) {
    if (entity == null) {
      entity = new Pilot();
    }
    if (id != null) {
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
}