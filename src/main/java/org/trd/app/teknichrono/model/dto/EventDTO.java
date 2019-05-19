package org.trd.app.teknichrono.model.dto;

import org.trd.app.teknichrono.model.jpa.Event;

import java.util.ArrayList;
import java.util.List;

public class EventDTO {

  private long id;
  private int version;
  private String name;
  private List<NestedSessionDTO> sessions = new ArrayList<NestedSessionDTO>();

  public EventDTO(final Event entity) {
    if (entity != null) {
      this.id = entity.id;
      this.version = entity.getVersion();
      this.name = entity.getName();
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

  public static List<EventDTO> convert(List<Event> results) {
    List<EventDTO> converted = new ArrayList<>();
    if (results != null) {
      for (Event Event : results) {
        converted.add(new EventDTO(Event));
      }
    }
    return converted;
  }
}
