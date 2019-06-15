package org.trd.app.teknichrono.model.dto;

import lombok.Data;

@Data
public class NestedBeaconDTO implements EntityDTO {

  private Long id;
  private int version;
  private long number;
}
