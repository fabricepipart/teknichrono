package org.trd.app.teknichrono.model.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class NestedSessionDTO implements EntityDTO {

  private Long id;
  private int version;
  private Instant start;
  private Instant end;
  private String name;
  private boolean loopTrack;
  private long chronometersCount;
  private NestedLocationDTO location;

}