package org.trd.app.teknichrono.model.dto;

import lombok.Data;

@Data
public class NestedChronometerDTO implements EntityDTO {

  private Long id;
  private int version;
  private String name;
}
