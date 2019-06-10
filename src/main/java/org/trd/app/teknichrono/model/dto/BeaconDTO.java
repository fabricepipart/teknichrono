package org.trd.app.teknichrono.model.dto;

import lombok.Data;
import org.trd.app.teknichrono.model.jpa.Beacon;

@Data
public class BeaconDTO {

  private long id;
  private int version;
  private long number;
  private NestedPilotDTO pilot;

  public static BeaconDTO fromBeacon(Beacon beacon) {
    return DtoMapper.INSTANCE.asBeaconDto(beacon);
  }

}
