package org.trd.app.teknichrono.model.jpa;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Version;

@Entity
public class Category extends PanacheEntity {

  @Version
  @Column(name = "version")
  private int version;

  @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true)
  @JoinColumn(name = "categoryId")
  private Set<Pilot> pilots = new HashSet<>();

  @Column(nullable = false)
  private String name;

  public int getVersion() {
    return this.version;
  }

  public void setVersion(final int version) {
    this.version = version;
  }

  public Set<Pilot> getPilots() {
    return this.pilots;
  }

  public void setPilots(final Set<Pilot> pilots) {
    this.pilots = pilots;
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
