package org.trd.app.teknichrono.model.dto;

import lombok.Data;

import java.time.Duration;

@Data
public class NestedLocationDTO implements EntityDTO {

  private Long id;
  private int version;
  private String name;
  private Duration minimum;
  private Duration maximum;
}