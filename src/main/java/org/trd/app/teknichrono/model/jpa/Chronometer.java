package org.trd.app.teknichrono.model.jpa;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Version;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Chronometer extends PanacheEntity {

  @Version
  @Column(name = "version")
  private int version;

  /* =============================== Fields =============================== */

  @Column
  private String name;

  // Can be null if after event, items are reassociated
  @OneToMany(mappedBy = "chrono", cascade = CascadeType.REMOVE)
  private Set<Ping> pings = new HashSet<>();

  @ManyToMany(mappedBy = "chronometers")
  private Set<Session> sessions = new HashSet<>();

  /* ===================== Getters and setters ======================== */
  public Long getId() {
    return this.id;
  }

  public void setId(Long id) {
    this.id = id;
  }

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

  public Set<Ping> getPings() {
    return pings;
  }

  public void setPings(Set<Ping> pings) {
    this.pings = pings;
  }

  public Set<Session> getSessions() {
    return sessions;
  }

  public void setSessions(Set<Session> sessions) {
    this.sessions = sessions;
  }

  public Ping getLastestPing() {
    if (pings != null) {
      return null;
    }
    Ping lastestPing = null;
    for (Ping p : pings) {
      if (p.getInstant() != null) {
        boolean moreRecent = lastestPing != null && lastestPing.getInstant().isBefore(p.getInstant());
        boolean isFirst = lastestPing == null;
        if (moreRecent || isFirst) {
          lastestPing = p;
        }
      }
    }
    return lastestPing;
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