package org.trd.app.teknichrono.model.jpa;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Version;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Location extends PanacheEntity {

  @Version
  @Column(name = "version")
  private int version;

  @Column(nullable = false)
  private String name;

  @Column
  private Duration minimum;

  @Column
  private Duration maximum;

  /**
   * True for a racetrack, false for a rally stage
   */
  @Column
  private boolean loopTrack;

  @OneToMany(orphanRemoval = true)
  @JoinColumn(name = "locationId")
  private Set<Session> sessions = new HashSet<>();

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
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isLoopTrack() {
    return this.loopTrack;
  }

  public void setLoopTrack(boolean loop) {
    this.loopTrack = loop;
  }

  public Set<Session> getSessions() {
    return this.sessions;
  }

  public void setSessions(final Set<Session> sessions) {
    this.sessions = sessions;
  }

  public Duration getMaximum() {
    return this.maximum;
  }

  public void setMaximum(Duration maximum) {
    this.maximum = maximum;
  }

  public Duration getMinimum() {
    return this.minimum;
  }

  public void setMinimum(Duration minimum) {
    this.minimum = minimum;
  }

  @Override
  public String toString() {
    String result = getClass().getSimpleName() + " ";
    if (this.name != null && !this.name.trim().isEmpty()) {
      result += "name: " + this.name;
    }
    result += ", loop: " + this.loopTrack;
    return result;
  }
}
