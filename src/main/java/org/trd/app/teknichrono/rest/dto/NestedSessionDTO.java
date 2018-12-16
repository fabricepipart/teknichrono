package org.trd.app.teknichrono.rest.dto;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.trd.app.teknichrono.model.Session;

public class NestedSessionDTO implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 4519531688362345387L;
  private int id;
  private int version;
  private Timestamp start;
  private Timestamp end;
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


  @Override
  public String toString() {
    return name;
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

  public Timestamp getStart() {
    return this.start;
  }

  public void setStart(final Timestamp start) {
    this.start = start;
  }

  public Timestamp getEnd() {
    return this.end;
  }

  public void setEnd(final Timestamp end) {
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