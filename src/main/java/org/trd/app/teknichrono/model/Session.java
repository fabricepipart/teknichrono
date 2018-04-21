package org.trd.app.teknichrono.model;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@XmlRootElement
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Session implements java.io.Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 8436060292277716599L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", unique = true, nullable = false)
  private int id;

  @Version
  @Column(name = "version")
  private int version;

  @Column(nullable = false)
  private Date start;

  @Column
  private long inactivity = 60000L;

  @Column(nullable = false)
  private Date end;

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

  @ManyToOne
  @JsonIgnoreProperties
  private Event event = null;

  @ManyToMany(fetch = FetchType.EAGER)
  private List<Pilot> pilots = new ArrayList<Pilot>();

  public List<Pilot> getPilots() {
    return pilots;
  }

  public void setPilots(List<Pilot> pilots) {
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

  public int getId() {
    return this.id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getVersion() {
    return this.version;
  }

  public void setVersion(final int version) {
    this.version = version;
  }

  public Date getStart() {
    return start;
  }

  public void setStart(Date beginning) {
    this.start = beginning;
  }

  public Date getEnd() {
    return end;
  }

  public void setEnd(Date end) {
    this.end = end;
  }

  public List<Chronometer> getChronometers() {
    return this.chronometers;
  }

  public void setChronometers(final List<Chronometer> chronometers) {
    this.chronometers = chronometers;
  }

  public void addChronometer(Chronometer chronometer) {
    int insertAtIndex = 0;
    Integer expectedIndex = chronometer.getChronoIndex();
    if (expectedIndex == null) {
      this.chronometers.add(chronometer);
    } else {
      int index = 0;
      for (Chronometer c : this.chronometers) {
        if (c.getChronoIndex() == null || c.getChronoIndex() >= expectedIndex) {
          insertAtIndex = index;
          break;
        }
        index++;
      }
      this.chronometers.add(insertAtIndex, chronometer);
    }

    // Reset indexes
    int index = 0;
    for (Chronometer c : this.chronometers) {
      c.setChronoIndex(index);
      index++;
    }
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
    if (name != null && !name.trim().isEmpty())
      result += "name: " + name;
    return result;
  }

}
