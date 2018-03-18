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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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

  @Column(nullable = false)
  private Date end;

  /**
   * <pre>
   * List of chrono points used for the event. From start to finish line.
   * 
   * For a rally. The Raspberry number are as follows
   *     _________________
   *     | 0 | 1 | 2 | 3 |
   * For a track
   *     _________________
   *     | 0 | 1 | 2 | 0 |
   * </pre>
   */
  @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true)
  @OrderColumn(name = "chronoIndex")
  @JoinColumn(name = "sessionId")
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

  @Override
  public String toString() {
    String result = getClass().getSimpleName() + " ";
    if (name != null && !name.trim().isEmpty())
      result += "name: " + name;
    return result;
  }

}
