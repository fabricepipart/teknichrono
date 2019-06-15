package org.trd.app.teknichrono.model.dto;

import lombok.Data;
import org.trd.app.teknichrono.model.jpa.Pilot;

@Data
public class PilotDTO implements EntityDTO {

  private Long id;
  private int version;
  private String firstName;
  private String lastName;
  private NestedBeaconDTO currentBeacon;
  private NestedCategoryDTO category;

  public static PilotDTO fromPilot(Pilot pilot) {
    return DtoMapper.INSTANCE.asPilotDto(pilot);
  }
}
