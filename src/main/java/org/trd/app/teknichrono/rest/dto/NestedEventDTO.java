package org.trd.app.teknichrono.rest.dto;

import java.io.Serializable;
import org.trd.app.teknichrono.model.Event;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.sql.Date;

public class NestedEventDTO implements Serializable {

  private int id;
  private int version;
  private Date start;
  private Date end;
  private String name;
  private boolean loopTrack;

  public NestedEventDTO() {
  }

  public NestedEventDTO(final Event entity) {
    if (entity != null) {
      this.id = entity.getId();
      this.version = entity.getVersion();
      this.start = entity.getStart();
      this.end = entity.getEnd();
      this.name = entity.getName();
      this.loopTrack = entity.isLoopTrack();
    }
  }

  public Event fromDTO(Event entity, EntityManager em) {
    if (entity == null) {
      entity = new Event();
    }
    if (((Integer) this.id) != null) {
      TypedQuery<Event> findByIdQuery = em.createQuery("SELECT DISTINCT e FROM Event e WHERE e.id = :entityId",
          Event.class);
      findByIdQuery.setParameter("entityId", this.id);
      try {
        entity = findByIdQuery.getSingleResult();
      } catch (javax.persistence.NoResultException nre) {
        entity = null;
      }
      return entity;
    }
    entity.setVersion(this.version);
    entity.setStart(this.start);
    entity.setEnd(this.end);
    entity.setName(this.name);
    entity.setLoopTrack(this.loopTrack);
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

  public Date getStart() {
    return this.start;
  }

  public void setStart(final Date start) {
    this.start = start;
  }

  public Date getEnd() {
    return this.end;
  }

  public void setEnd(final Date end) {
    this.end = end;
  }

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public boolean getLoopTrack() {
    return this.loopTrack;
  }

  public void setLoopTrack(final boolean loopTrack) {
    this.loopTrack = loopTrack;
  }
}