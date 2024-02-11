package org.trd.app.teknichrono.model.jpa;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Version;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Event extends PanacheEntity {

  @Version
  @Column(name = "version")
  private int version;

  @OneToMany(orphanRemoval = true)
  @JoinColumn(name = "eventId")
  private Set<Session> sessions = new HashSet<>();

  @Column(nullable = false)
  private String name;

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

  public Set<Session> getSessions() {
    return this.sessions;
  }

  public void setSessions(final Set<Session> sessions) {
    this.sessions = sessions;
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
