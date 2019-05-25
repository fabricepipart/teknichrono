package org.trd.app.teknichrono.model.dto;

import lombok.Data;
import org.trd.app.teknichrono.model.jpa.Location;

import java.util.ArrayList;
import java.util.List;

@Data
public class LocationDTO {

  private long id;
  private int version;
  private String name;
  private boolean loopTrack;
  private List<NestedSessionDTO> sessions = new ArrayList<>();

  public static LocationDTO fromLocation(Location location) {
    return DtoMapper.INSTANCE.asLocationDto(location);
  }
}
