package org.trd.app.teknichrono.model.dto;

import lombok.Data;
import org.trd.app.teknichrono.model.jpa.Location;

import java.util.HashSet;
import java.util.Set;

@Data
public class LocationDTO implements EntityDTO {

  private Long id;
  private int version;
  private String name;
  private boolean loopTrack;
  private Set<NestedSessionDTO> sessions = new HashSet<>();

  public static LocationDTO fromLocation(Location location) {
    return DtoMapper.INSTANCE.asLocationDto(location);
  }
}
