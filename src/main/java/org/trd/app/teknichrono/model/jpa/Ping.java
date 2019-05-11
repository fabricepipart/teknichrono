package org.trd.app.teknichrono.model.jpa;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlRootElement;


@Entity
@XmlRootElement
public class Ping implements Serializable {

  /* =========================== Entity stuff =========================== */
  /**
   * 
   */
  private static final long serialVersionUID = -7022575222961829989L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", updatable = false, nullable = false)
  private int id;

  @Version
  @Column(name = "version")
  private int version;

  /* =============================== Fields =============================== */

  @Column(columnDefinition="TIMESTAMP(3)", nullable = false)
  private Timestamp dateTime;

  // Can be null if after event, items are reassociated
  @ManyToOne(optional = true)
  @JoinColumn(name = "beaconId")
  private Beacon beacon;

  @Column
  private int power;

  // Can be null if after event, items are reassociated
  @ManyToOne(optional = true)
  @JoinColumn(name = "chronoId")
  private Chronometer chrono;

  /* ===================== Getters and setters ======================== */

  public int getId() {
    return this.id;
  }

  public void setId(final int id) {
    this.id = id;
  }

  public int getVersion() {
    return this.version;
  }

  public void setVersion(final int version) {
    this.version = version;
  }

  public Timestamp getDateTime() {
    return dateTime;
  }

  public void setDateTime(Timestamp dateTime) {
    this.dateTime = dateTime;
  }

  public int getPower() {
    return power;
  }

  public void setPower(int power) {
    this.power = power;
  }

  public Beacon getBeacon() {
    return beacon;
  }

  public void setBeacon(Beacon beacon) {
    this.beacon = beacon;
  }

  public Chronometer getChrono() {
    return chrono;
  }

  public void setChrono(Chronometer chrono) {
    this.chrono = chrono;
  }

  @Override
  public String toString() {
    String result = getClass().getSimpleName() + " ";
    result += "time: " + dateTime;
    result += ", beaconId: " + beacon;
    result += ", power: " + power;
    result += ", chronoId: " + chrono;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof Ping)) {
      return false;
    }
    Ping other = (Ping) obj;

    if (id != other.id) {
      return false;
    }
    return true;
  }
}