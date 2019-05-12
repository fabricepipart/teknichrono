package org.trd.app.teknichrono.model.jpa;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Version;

@Entity
public class Chronometer extends PanacheEntity  {

  @Version
  @Column(name = "version")
  private int version;

  /* =============================== Fields =============================== */

  @Column
  private String name;

  // Can be null if after event, items are reassociated
  @OneToMany(mappedBy = "chrono")
  private List<Ping> pings = new ArrayList<Ping>();

  @ManyToMany(mappedBy = "chronometers")
  private List<Session> sessions = new ArrayList<Session>();

  /* ===================== Getters and setters ======================== */
  public int getVersion() {
    return this.version;
  }

  public void setVersion(final int version) {
    this.version = version;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<Ping> getPings() {
    return pings;
  }

  public void setPings(List<Ping> pings) {
    this.pings = pings;
  }

  public List<Session> getSessions() {
    return sessions;
  }

  public void setSessions(List<Session> sessions) {
    this.sessions = sessions;
  }

  /* ===================== Other ======================== */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof Chronometer)) {
      return false;
    }
    Chronometer other = (Chronometer) obj;
    if (id != other.id) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    String result = getClass().getSimpleName() + " ";
    if (name != null && !name.trim().isEmpty())
      result += name;
    return result;
  }
}