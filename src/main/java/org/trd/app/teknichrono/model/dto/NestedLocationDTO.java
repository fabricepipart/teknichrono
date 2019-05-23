package org.trd.app.teknichrono.model.dto;

import lombok.Data;
import org.trd.app.teknichrono.model.jpa.Location;

@Data
public class NestedLocationDTO {

  private Long id;
  private int version;
  private String name;

  public static NestedLocationDTO fromLocation(Location location) {
    return DtoMapper.INSTANCE.asNestedLocationDto(location);
  }
}