package org.trd.app.teknichrono.model.jpa;
// Generated 5 mai 2016 11:08:49 by Hibernate Tools 4.3.1.Final

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Version;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Beacon extends PanacheEntity {

  @Version
  @Column(name = "version")
  private int version;

  /* =============================== Fields =============================== */

  @Column(nullable = false, unique = true)
  private long number;

  // Mapped by denotes that Pilot is the owner of the relationship
  // http://meri-stuff.blogspot.fr/2012/03/jpa-tutorial.html#RelationshipsBidirectionalOneToManyManyToOneConsistency
  @OneToOne(optional = true, mappedBy = "currentBeacon", cascade = CascadeType.MERGE)
  private Pilot pilot;

  // Can be null if after event, items are reassociated
  @OneToMany(mappedBy = "beacon", cascade = CascadeType.ALL, orphanRemoval=true)
  private List<Ping> pings = new ArrayList<>();

  /* ===================== Getters and setters ======================== */

  public Pilot getPilot() {
    return pilot;
  }

  public void setPilot(Pilot pilot) {
    // prevent endless loop
    if (sameAsFormer(pilot)) {
      return;
    }
    Pilot oldPilot = this.pilot;
    // Set new pilot
    this.pilot = pilot;
    // This beacon is not associated to the previous Pilot
    if (oldPilot != null) {
      oldPilot.setCurrentBeacon(null);
    }
    // Set reverse relationship
    if (pilot != null) {
      pilot.setCurrentBeacon(this);
    }
  }

  private boolean sameAsFormer(Pilot newPilot) {
    return pilot == null ? newPilot == null : pilot.equals(newPilot);
  }

  public List<Ping> getPings() {
    return pings;
  }

  public void setPings(List<Ping> pings) {
    this.pings = pings;
  }

  public int getVersion() {
    return this.version;
  }

  public void setVersion(final int version) {
    this.version = version;
  }

  public long getNumber() {
    return this.number;
  }

  public void setNumber(long number) {
    this.number = number;
  }

  @Override
  public String toString() {
    String result = getClass().getSimpleName() + " ";
    result += "number: " + number;
    return result;
  }
}
