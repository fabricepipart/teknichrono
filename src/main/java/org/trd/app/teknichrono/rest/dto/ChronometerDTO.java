package org.trd.app.teknichrono.rest.dto;

import org.trd.app.teknichrono.model.Chronometer;
import org.trd.app.teknichrono.model.Ping;

import javax.persistence.EntityManager;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ChronometerDTO {


  private int id;
  private int version;
  private String name;
  private NestedPingDTO lastSeen;

  public ChronometerDTO() {
  }

  public ChronometerDTO(final Chronometer entity) {
    if (entity != null) {
      this.id = entity.getId();
      this.version = entity.getVersion();
      this.name = entity.getName();
      if (entity.getPings() != null) {
        for (Ping p : entity.getPings()){
          if(p.getDateTime() != null){
            boolean moreRecent = this.lastSeen != null && this.lastSeen.getDateTime().getTime() < p.getDateTime().getTime();
            boolean isFirst = this.lastSeen == null;
            if(moreRecent || isFirst){
              this.lastSeen = new NestedPingDTO(p);
            }
          }
        }
      }
    }
  }

  public static List<ChronometerDTO> convert(List<Chronometer> results) {
    List<ChronometerDTO> converted = new ArrayList<>();
    if (results != null) {
      for (Chronometer chrono : results) {
        converted.add(new ChronometerDTO(chrono));
      }
    }
    return converted;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("[id=" + getId());
    sb.append(",name=" + name + "]");
    return sb.toString();
  }

  //
  public Chronometer fromDTO(Chronometer entity, EntityManager em) {
    if (entity == null) {
      entity = new Chronometer();
    }
    entity.setVersion(this.version);
    entity.setName(this.name);
    entity = em.merge(entity);
    return entity;
  }

  /* ===================== Getters and setters ======================== */
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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public NestedPingDTO getLastSeen() {
    return lastSeen;
  }

  public void setLastSeen(NestedPingDTO lastSeen) {
    this.lastSeen = lastSeen;
  }

}
