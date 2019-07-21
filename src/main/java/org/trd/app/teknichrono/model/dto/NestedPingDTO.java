package org.trd.app.teknichrono.model.dto;

import lombok.Data;
import org.trd.app.teknichrono.model.jpa.Ping;

import java.time.Instant;

@Data
public class NestedPingDTO implements EntityDTO {

  private Long id;
  private int version;
  private NestedBeaconDTO beacon;
  private Instant instant;
  private long power;

  public static NestedPingDTO fromPing(Ping ping) {
    return DtoMapper.INSTANCE.asNestedPingDto(ping);
  }
}