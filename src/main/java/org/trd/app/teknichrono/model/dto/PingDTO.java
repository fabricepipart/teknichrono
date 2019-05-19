package org.trd.app.teknichrono.model.dto;

import org.trd.app.teknichrono.model.jpa.Ping;

import java.time.Instant;

public class PingDTO {


  private long id;
  private int version;


  /* =============================== Fields =============================== */
  private Instant instant;
  private NestedBeaconDTO beacon;
  private long power;
  private NestedChronometerDTO chrono;


  public PingDTO(final Ping entity) {
    if (entity != null) {
      this.id = entity.id;
      this.version = entity.getVersion();
      if (entity.getBeacon() != null) {
        this.beacon = new NestedBeaconDTO(entity.getBeacon());
      }
      if (entity.getChrono() != null) {
        this.chrono = new NestedChronometerDTO(entity.getChrono());
      }
      this.power = entity.getPower();
    }
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


  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public int getVersion() {
    return version;
  }

  public void setVersion(int version) {
    this.version = version;
  }

  public Instant getInstant() {
    return instant;
  }

  public void setInstant(Instant instant) {
    this.instant = instant;
  }

  public NestedBeaconDTO getBeacon() {
    return beacon;
  }

  public void setBeacon(NestedBeaconDTO beacon) {
    this.beacon = beacon;
  }

  public long getPower() {
    return power;
  }

  public void setPower(long power) {
    this.power = power;
  }

  public NestedChronometerDTO getChrono() {
    return chrono;
  }

  public void setChrono(NestedChronometerDTO chrono) {
    this.chrono = chrono;
  }

}
