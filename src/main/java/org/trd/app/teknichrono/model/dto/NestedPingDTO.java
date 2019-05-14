package org.trd.app.teknichrono.model.dto;

import org.trd.app.teknichrono.model.jpa.Ping;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.time.Instant;

@XmlRootElement
public class NestedPingDTO implements Serializable{

  private long id;
  private int version;
  private NestedBeaconDTO beacon;
  private Instant instant;
  private long power;


  public NestedPingDTO() {
  }

  public NestedPingDTO(final Ping entity) {
    if (entity != null) {
      this.id = entity.id;
      this.version = entity.getVersion();
      if (entity.getBeacon() != null) {
        this.beacon = new NestedBeaconDTO(entity.getBeacon());
      }
      this.instant = entity.getInstant();
      this.power = entity.getPower();
    }
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("[id=" + getId());
    sb.append(",beacon=" + beacon.getNumber());
    sb.append(",instant=" + instant);
    sb.append(",power=" + power + "]");
    return sb.toString();
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

  public NestedBeaconDTO getBeacon() {
    return beacon;
  }

  public void setBeacon(NestedBeaconDTO beacon) {
    this.beacon = beacon;
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

}