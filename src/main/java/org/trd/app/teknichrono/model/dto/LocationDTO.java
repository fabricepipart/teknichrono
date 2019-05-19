package org.trd.app.teknichrono.model.dto;

import org.trd.app.teknichrono.model.jpa.Location;

import java.util.ArrayList;
import java.util.List;

public class LocationDTO {

  private long id;
  private int version;
  private String name;
  private boolean loopTrack;
  private List<NestedSessionDTO> sessions = new ArrayList<NestedSessionDTO>();

  public LocationDTO(final Location entity) {
    if (entity != null) {
      this.id = entity.id;
      this.version = entity.getVersion();
      this.loopTrack = entity.isLoopTrack();
      this.name = entity.getName();
      this.sessions = new ArrayList<>();
      if (entity.getSessions() != null) {
        entity.getSessions().forEach(s -> sessions.add(new NestedSessionDTO(s)));
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

  public List<NestedSessionDTO> getSessions() {
    return sessions;
  }

  public void setSessions(List<NestedSessionDTO> sessions) {
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
