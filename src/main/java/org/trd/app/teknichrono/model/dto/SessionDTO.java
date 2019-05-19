package org.trd.app.teknichrono.model.dto;

import org.trd.app.teknichrono.model.jpa.Chronometer;
import org.trd.app.teknichrono.model.jpa.Pilot;
import org.trd.app.teknichrono.model.jpa.Session;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SessionDTO {

  private long id;
  private int version;
  private Instant start;
  private long inactivity = 60000L;
  private Instant end;
  private String type;
  private boolean current = false;
  private List<NestedChronometerDTO> chronometers = new ArrayList<NestedChronometerDTO>();
  private String name;
  private NestedLocationDTO location;
  private NestedEventDTO event = null;
  private Set<NestedPilotDTO> pilots = new HashSet<NestedPilotDTO>();

  public SessionDTO() {
  }

  public SessionDTO(final Session entity) {
    if (entity != null) {
      this.id = entity.id;
      this.version = entity.getVersion();
      this.start = entity.getStart();
      this.inactivity = entity.getInactivity();
      this.end = entity.getEnd();
      this.type = entity.getType();
      this.current = entity.isCurrent();
      this.name = entity.getName();
      if (entity.getLocation() != null) {
        this.location = new NestedLocationDTO(entity.getLocation());
      }
      if (entity.getEvent() != null) {
        this.event = new NestedEventDTO(entity.getEvent());
      }
      this.pilots = new HashSet<>();
      if (entity.getPilots() != null) {
        for (Pilot p : entity.getPilots()) {
          pilots.add(new NestedPilotDTO(p));
        }
      }
      this.chronometers = new ArrayList<>();
      if (entity.getChronometers() != null) {
        for (Chronometer c : entity.getChronometers()) {
          chronometers.add(new NestedChronometerDTO(c));
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
  public Session fromDTO(Session entity, EntityManager em) {
    if (entity == null) {
      entity = new Session();
    }
    entity.setVersion(this.version);
    entity.setStart(this.start);
    entity.setInactivity(this.inactivity);
    entity.setEnd(this.end);
    entity.setType(this.type);
    entity.setCurrent(this.current);
    entity.setName(this.name);
    if (this.location != null) {
      entity.setLocation(this.location.fromDTO(entity.getLocation(), em));
    }
    if (this.event != null) {
      entity.setEvent(this.event.fromDTO(entity.getEvent(), em));
    }

    entity.getPilots().clear();
    if (this.pilots != null) {
      for (NestedPilotDTO nestedPilotDTO : pilots) {
        entity.getPilots().add(nestedPilotDTO.fromDTO(new Pilot(), em));
      }
    }
    entity.getChronometers().clear();
    if (this.chronometers != null) {
      for (NestedChronometerDTO nestedChronoDTO : chronometers) {
        entity.getChronometers().add(nestedChronoDTO.fromDTO(new Chronometer(), em));
      }
    }
    entity = em.merge(entity);
    return entity;
  }

  public static List<SessionDTO> convert(List<Session> results) {
    List<SessionDTO> converted = new ArrayList<>();
    if (results != null) {
      for (Session session : results) {
        converted.add(new SessionDTO(session));
      }
    }
    return converted;
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

  public Instant getStart() {
    return start;
  }

  public void setStart(Instant start) {
    this.start = start;
  }

  public long getInactivity() {
    return inactivity;
  }

  public void setInactivity(long inactivity) {
    this.inactivity = inactivity;
  }

  public Instant getEnd() {
    return end;
  }

  public void setEnd(Instant end) {
    this.end = end;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public boolean isCurrent() {
    return current;
  }

  public void setCurrent(boolean current) {
    this.current = current;
  }

  public List<NestedChronometerDTO> getChronometers() {
    return chronometers;
  }

  public void setChronometers(List<NestedChronometerDTO> chronometers) {
    this.chronometers = chronometers;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public NestedLocationDTO getLocation() {
    return location;
  }

  public void setLocation(NestedLocationDTO location) {
    this.location = location;
  }

  public NestedEventDTO getEvent() {
    return event;
  }

  public void setEvent(NestedEventDTO event) {
    this.event = event;
  }

  public Set<NestedPilotDTO> getPilots() {
    return pilots;
  }

  public void setPilots(Set<NestedPilotDTO> pilots) {
    this.pilots = pilots;
  }

}
