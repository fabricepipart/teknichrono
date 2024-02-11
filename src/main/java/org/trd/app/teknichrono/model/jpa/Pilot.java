package org.trd.app.teknichrono.model.jpa;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Version;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

  @Column
  private String nickname;

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
    return this.sessions;
  }

  public void setSessions(Set<Session> sessions) {
    this.sessions = sessions;
  }

  public Category getCategory() {
    return this.category;
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
    return this.firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return this.lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getNickname() {
    return this.nickname;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  public Beacon getCurrentBeacon() {
    return this.currentBeacon;
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
    return this.laps;
  }

  public void setLaps(List<LapTime> laps) {
    this.laps = laps;
  }

  private boolean sameAsFormer(Beacon newBeacon) {
    return this.currentBeacon == null ? newBeacon == null : this.currentBeacon.equals(newBeacon);
  }

  public String getFullname() {
    List<String> elements = new ArrayList<>();
    if (this.nickname != null) {
      elements.add("(" + this.nickname + ")");
    }
    if (this.firstName != null) {
      elements.add(this.firstName);
    }
    if (this.lastName != null) {
      elements.add(this.lastName);
    }
    return String.join(" ", elements);
  }

  @Override
  public String toString() {
    String result = getClass().getSimpleName() + "[#" + this.id;
    if (this.nickname != null && !this.nickname.trim().isEmpty()) {
      result += ", nickname: " + this.nickname;
    }
    if (this.firstName != null && !this.firstName.trim().isEmpty()) {
      result += ", firstName: " + this.firstName;
    }
    if (this.lastName != null && !this.lastName.trim().isEmpty()) {
      result += ", lastName: " + this.lastName;
    }
    result += "]";
    return result;
  }
}