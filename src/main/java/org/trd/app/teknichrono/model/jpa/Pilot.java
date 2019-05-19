package org.trd.app.teknichrono.model.jpa;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Version;

@Entity
public class Pilot extends PanacheEntity {

  @Version
  @Column(name = "version")
  private int version;

  /* =============================== Fields =============================== */
  @Column(nullable = false)
  private String firstName;

  @Column(nullable = false)
  private String lastName;

  @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
  @JoinColumn(name = "currentBeaconId")
  private Beacon currentBeacon;

  @OneToMany(cascade = CascadeType.REMOVE)
  @OrderBy(value = "startDate")
  @JoinColumn(name = "pilotId")
  private List<LapTime> laps = new ArrayList<>();

  @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
  @JoinColumn(name = "categoryId")
  private Category category;

  @ManyToMany(mappedBy = "pilots")
  private Set<Session> sessions = new HashSet<>();

  /* ===================== Getters and setters ======================== */
  public Long getId() {
    return this.id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Set<Session> getSessions() {
    return sessions;
  }

  public void setSessions(Set<Session> sessions) {
    this.sessions = sessions;
  }

  public Category getCategory() {
    return category;
  }

  public void setCategory(Category category) {
    this.category = category;
  }

  public int getVersion() {
    return this.version;
  }

  public void setVersion(final int version) {
    this.version = version;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public Beacon getCurrentBeacon() {
    return currentBeacon;
  }

  public void setCurrentBeacon(Beacon currentBeacon) {
    // prevent endless loop
    if (sameAsFormer(currentBeacon)) {
      return;
    }
    Beacon oldBeacon = this.currentBeacon;
    // Set new pilot
    this.currentBeacon = currentBeacon;
    // This beacon is not associated to the previous Pilot
    if (oldBeacon != null) {
      oldBeacon.setPilot(null);
    }
    // Set reverse relationship
    if (currentBeacon != null) {
      currentBeacon.setPilot(this);
    }
  }

  public List<LapTime> getLaps() {
    return laps;
  }

  public void setLaps(List<LapTime> laps) {
    this.laps = laps;
  }

  private boolean sameAsFormer(Beacon newBeacon) {
    return currentBeacon == null ? newBeacon == null : currentBeacon.equals(newBeacon);
  }

  @Override
  public String toString() {
    String result = getClass().getSimpleName() + "[#" + id;
    if (firstName != null && !firstName.trim().isEmpty())
      result += ", firstName: " + firstName;
    if (lastName != null && !lastName.trim().isEmpty())
      result += ", lastName: " + lastName;
    result += "]";
    return result;
  }
}