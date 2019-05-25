package org.trd.app.teknichrono.model.dto;

import lombok.Data;
import org.trd.app.teknichrono.model.jpa.Chronometer;

@Data
public class ChronometerDTO {

  private long id;
  private int version;
  private String name;
  private NestedPingDTO lastSeen;

  public static ChronometerDTO fromChronometer(Chronometer chronometer) {
    return DtoMapper.INSTANCE.asChronometerDto(chronometer);
  }
}
