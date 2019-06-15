package org.trd.app.teknichrono.model.dto;

import lombok.Data;
import org.trd.app.teknichrono.model.jpa.Session;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.Instant;

@Data
public class NestedSessionDTO implements EntityDTO {

  private Long id;
  private int version;
  private Instant start;
  private Instant end;
  private String name;
  private boolean loopTrack;
  private long chronometersCount;
  private NestedLocationDTO location;

  Session fromDTO(Session entity, EntityManager em) {
    if (entity == null) {
      entity = new Session();
    }
    if (id != null) {
      TypedQuery<Session> findByIdQuery = em.createQuery("SELECT DISTINCT e FROM Session e WHERE e.id = :entityId",
          Session.class);
      findByIdQuery.setParameter("entityId", this.id);
      try {
        entity = findByIdQuery.getSingleResult();
      } catch (javax.persistence.NoResultException nre) {
        entity = null;
      }
      return entity;
    }
    entity.setVersion(this.version);
    entity.setEnd(end);
    entity.setStart(start);
    entity.setName(name);
    // entity.setLoopTrack(loopTrack);
    entity = em.merge(entity);
    return entity;
  }
}