package org.trd.app.teknichrono.model.dto;

import org.trd.app.teknichrono.model.jpa.Location;
import org.trd.app.teknichrono.model.jpa.Session;

import javax.persistence.EntityManager;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
public class LocationDTO {

  private long id;
  private int version;
  private String name;
  private boolean loopTrack;
  private List<NestedSessionDTO> sessions = new ArrayList<NestedSessionDTO>();

  public LocationDTO() {
  }

  public LocationDTO(final Location entity) {
    if (entity != null) {
      this.id = entity.id;
      this.version = entity.getVersion();
      this.loopTrack = entity.isLoopTrack();
      this.name = entity.getName();
      this.sessions = new ArrayList<>();
      if (entity.getSessions() != null) {
        for (Session s : entity.getSessions()) {
          sessions.add(new NestedSessionDTO(s));
        }
      }
    }
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("[id=" + getId());
    sb.append(",name=" + name + "]");
    return sb.toString();
  }

  //
  public Location fromDTO(Location entity, EntityManager em) {
    if (entity == null) {
      entity = new Location();
    }
    entity.setVersion(this.version);
    entity.setName(this.name);
    entity.setLoopTrack(this.isLoopTrack());
    entity.getSessions().clear();
    if (this.sessions != null) {
      for (NestedSessionDTO s : sessions) {
        entity.getSessions().add(s.fromDTO(new Session(), em));
      }
    }
    entity = em.merge(entity);
    return entity;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public int getVersion() {
    return version;
  }

  public void setVersion(int version) {
    this.version = version;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<NestedSessionDTO> getsessions() {
    return sessions;
  }

  public void setsessions(List<NestedSessionDTO> sessions) {
    this.sessions = sessions;
  }

  public static List<LocationDTO> convert(List<Location> results) {
    List<LocationDTO> converted = new ArrayList<>();
    if (results != null) {
      for (Location Location : results) {
        converted.add(new LocationDTO(Location));
      }
    }
    return converted;
  }

  public boolean isLoopTrack() {
    return loopTrack;
  }

  public void setLoopTrack(boolean loopTrack) {
    this.loopTrack = loopTrack;
  }
}
