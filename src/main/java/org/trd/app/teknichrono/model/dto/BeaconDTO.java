package org.trd.app.teknichrono.model.dto;

import org.trd.app.teknichrono.model.jpa.Beacon;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

public class BeaconDTO {

  private long id;

  private int version;
  private long number;
  private NestedPilotDTO pilot;

  public BeaconDTO() {
  }

  public BeaconDTO(final Beacon entity) {
    if (entity != null) {
      this.id = entity.id;
      this.version = entity.getVersion();
      this.number = entity.getNumber();
      if (entity.getPilot() != null) {
        this.pilot = new NestedPilotDTO(entity.getPilot());
      }
    }
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("[id=" + getId());
    sb.append(",pilot=" + pilot.getId());
    sb.append(",number=" + number + "]");
    return sb.toString();
  }

  //
  public Beacon fromDTO(Beacon entity, EntityManager em) {
    if (entity == null) {
      entity = new Beacon();
    }
    entity.setVersion(this.version);
    entity.setNumber(this.number);
    if (this.pilot != null) {
      entity.setPilot(this.pilot.fromDTO(entity.getPilot(), em));
    }
    entity = em.merge(entity);
    return entity;
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

  public long getNumber() {
    return number;
  }

  public void setNumber(long number) {
    this.number = number;
  }

  public NestedPilotDTO getPilot() {
    return pilot;
  }

  public void setPilot(NestedPilotDTO pilot) {
    this.pilot = pilot;
  }

  public static List<BeaconDTO> convert(List<Beacon> results) {
    List<BeaconDTO> converted = new ArrayList<>();
    if (results != null) {
      for (Beacon beacon : results) {
        converted.add(new BeaconDTO(beacon));
      }
    }
    return converted;
  }

}
