package org.trd.app.teknichrono.rest.dto;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.trd.app.teknichrono.model.Session;

public class NestedSessionDTO implements Serializable {

  private int id;
  private int version;
  private Date start;
  private Date end;
  private String name;
  private boolean loopTrack;
  private int chronometersCount;
  private NestedLocationDTO location;

  public NestedSessionDTO() {
  }

  public NestedSessionDTO(final Session entity) {
    if (entity != null) {
      this.id = entity.getId();
      this.version = entity.getVersion();
      this.start = entity.getStart();
      this.end = entity.getEnd();
      this.name = entity.getName();
      this.loopTrack = entity.getLocation().isLoopTrack();
      this.chronometersCount = entity.getChronometers().size();
      this.location = new NestedLocationDTO(entity.getLocation());
    }
  }

  public Session fromDTO(Session entity, EntityManager em) {
    if (entity == null) {
      entity = new Session();
    }
    if (((Integer) this.id) != null) {
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

  public boolean isLoopTrack() {
    return this.loopTrack;
  }

  public void setLoopTrack(final boolean loopTrack) {
    this.loopTrack = loopTrack;
  }

  public int getChronometersCount() {
    return chronometersCount;
  }

  public void setChronometersCount(int chronometersCount) {
    this.chronometersCount = chronometersCount;
  }

  public NestedLocationDTO getLocation() {
    return location;
  }

  public void setLocation(NestedLocationDTO location) {
    this.location = location;
  }
}