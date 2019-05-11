package org.trd.app.teknichrono.model.jpa;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@XmlRootElement
public class Location implements java.io.Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 7127979330876487660L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", unique = true, nullable = false)
  private int id;

  @Version
  @Column(name = "version")
  private int version;

  @Column(nullable = false)
  private String name;

  /**
   * True for a racetrack, false for a rally stage
   */
  @Column
  private boolean loopTrack;

  @OneToMany(orphanRemoval = true)
  @JoinColumn(name = "locationId")
  private List<Session> sessions = new ArrayList<Session>();

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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isLoopTrack() {
    return loopTrack;
  }

  public void setLoopTrack(boolean loop) {
    this.loopTrack = loop;
  }

  public List<Session> getSessions() {
    return this.sessions;
  }

  public void setSessions(final List<Session> sessions) {
    this.sessions = sessions;
  }

  @Override
  public String toString() {
    String result = getClass().getSimpleName() + " ";
    if (name != null && !name.trim().isEmpty())
      result += "name: " + name;
    result += ", loop: " + loopTrack;
    return result;
  }

}
