package org.trd.app.teknichrono.model.dto;

import lombok.Data;
import org.trd.app.teknichrono.model.jpa.Pilot;

@Data
public class NestedPilotDTO implements EntityDTO {

  private Long id;
  private String firstName;
  private String lastName;
  private String nickname;
  private String fullname;
  private long beaconNumber;
  private String categoryName;

  public static NestedPilotDTO fromPilot(Pilot pilot) {
    return DtoMapper.INSTANCE.asNestedPilotDto(pilot);
  }

}