package org.trd.app.teknichrono.model.jpa;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Version;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Session extends PanacheEntity {

  @Version
  @Column(name = "version")
  private int version;

  @Column(columnDefinition = "TIMESTAMP(3)", nullable = false)
  private Instant start;

  @Column
  private long inactivity = 0L;

  @Column(columnDefinition = "TIMESTAMP(3)", nullable = false)
  private Instant end;

  @Column(nullable = false)
  private String type;

  @Column
  private boolean current = false;

  /**
   * <pre>
   * List of chrono points used for the event. From start to finish line.
   * </pre>
   */
  @ManyToMany(fetch = FetchType.EAGER)
  @OrderColumn(name = "chronoIndex")
  private List<Chronometer> chronometers = new ArrayList<Chronometer>();

  @Column(nullable = false)
  private String name;

  // Can be null if after event, items are reassociated
  @ManyToOne(optional = true)
  @JoinColumn(name = "locationId")
  private Location location;

  @ManyToOne(optional = true)
  @JoinColumn(name = "eventId")
  private Event event = null;

  @ManyToMany(fetch = FetchType.EAGER)
  private Set<Pilot> pilots = new HashSet<Pilot>();

  public Set<Pilot> getPilots() {
    return pilots;
  }

  public void setPilots(Set<Pilot> pilots) {
    this.pilots = pilots;
  }

  public Event getEvent() {
    return event;
  }

  public void setEvent(Event event) {
    this.event = event;
  }

  public Location getLocation() {
    return location;
  }

  public void setLocation(Location location) {
    this.location = location;
  }

  public void setId(long id) {
    this.id = id;
  }

  public int getVersion() {
    return this.version;
  }

  public void setVersion(final int version) {
    this.version = version;
  }

  public Instant getStart() {
    return start;
  }

  public void setStart(Instant beginning) {
    this.start = beginning;
  }

  public Instant getEnd() {
    return end;
  }

  public void setEnd(Instant end) {
    this.end = end;
  }

  public List<Chronometer> getChronometers() {
    return this.chronometers;
  }

  public void setChronometers(final List<Chronometer> chronometers) {
    this.chronometers = chronometers;
  }

  public long getChronoIndex(Chronometer chronometer) {
    if (getChronometers() != null) {
      long index = 0;
      for (Chronometer c : getChronometers()) {
        if (c.id == chronometer.id) {
          return index;
        }
        index++;
      }
    }
    return -1;
  }

  public void addChronometer(Chronometer chronometer, int insertAtIndex) {
    this.chronometers.add(insertAtIndex, chronometer);
  }

  public void addChronometer(Chronometer chronometer) {
    this.chronometers.add(chronometer);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public SessionType getSessionType() {
    for (SessionType sessionType : SessionType.values()) {
      if (type != null && type.equals(sessionType.getIdentifier())) {
        return sessionType;
      }
    }
    return SessionType.TIME_TRIAL;
  }

  public void setSessionType(SessionType type) {
    this.type = type.getIdentifier();
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public long getInactivity() {
    return inactivity;
  }

  public void setInactivity(long inactivity) {
    this.inactivity = inactivity;
  }

  public boolean isCurrent() {
    return current;
  }

  public void setCurrent(boolean current) {
    this.current = current;
  }

  @Override
  public String toString() {
    String result = getClass().getSimpleName() + " ";
    if (name != null && !name.trim().isEmpty()) {
      result += "name: " + name;
      result += " from: " + start;
      result += " to: " + end;
    }
    return result;
  }

}
