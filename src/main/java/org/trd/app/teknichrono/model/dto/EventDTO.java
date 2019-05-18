package org.trd.app.teknichrono.model.dto;

import org.trd.app.teknichrono.model.jpa.Event;
import org.trd.app.teknichrono.model.jpa.Session;

import javax.persistence.EntityManager;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
public class EventDTO {

  private long id;
  private int version;
  private String name;
  private List<NestedSessionDTO> sessions = new ArrayList<NestedSessionDTO>();

  public EventDTO() {
  }

  public EventDTO(final Event entity) {
    if (entity != null) {
      this.id = entity.id;
      this.version = entity.getVersion();
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
  public Event fromDTO(Event entity, EntityManager em) {
    if (entity == null) {
      entity = new Event();
    }
    entity.setVersion(this.version);
    entity.setName(this.name);
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
