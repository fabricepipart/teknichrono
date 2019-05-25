package org.trd.app.teknichrono.model.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class NestedPingDTO {

  private long id;
  private int version;
  private NestedBeaconDTO beacon;
  private Instant instant;
  private long power;
}