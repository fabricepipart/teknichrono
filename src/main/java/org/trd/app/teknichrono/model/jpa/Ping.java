package org.trd.app.teknichrono.model.jpa;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Version;
import java.time.Instant;

@Entity
public class Ping extends PanacheEntity {

  @Version
  @Column(name = "version")
  private int version;

  /* =============================== Fields =============================== */
  @Column(columnDefinition = "TIMESTAMP(3)", nullable = false)
  private Instant instant;

  // Can be null if after event, items are reassociated
  @ManyToOne(optional = true)
  @JoinColumn(name = "beaconId")
  private Beacon beacon;

  @Column
  private long power;

  // Can be null if after event, items are reassociated
  @ManyToOne(optional = true)
  @JoinColumn(name = "chronoId")
  private Chronometer chrono;

  /* ===================== Getters and setters ======================== */

  public int getVersion() {
    return this.version;
  }

  public void setVersion(final int version) {
    this.version = version;
  }

  public Instant getInstant() {
    return instant;
  }

  public void setInstant(Instant instant) {
    this.instant = instant;
  }

  public long getPower() {
    return power;
  }

  public void setPower(long power) {
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
    result += "time: " + instant;
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