package org.trd.app.teknichrono.model.dto;

import lombok.Data;
import org.trd.app.teknichrono.model.jpa.Ping;

import java.time.Instant;

@Data
public class PingDTO implements EntityDTO {

  private Long id;
  private int version;
  private NestedBeaconDTO beacon;
  private NestedChronometerDTO chronometer;
  private Instant instant;
  private long power;

  public static PingDTO fromPing(Ping ping) {
    return DtoMapper.INSTANCE.asPingDto(ping);
  }

  public static PingDTO create(Long beaconId, Long chronoId, Instant instant, long power) {
    PingDTO ping = new PingDTO();
    ping.setInstant(instant);
    ping.setPower(power);
    NestedBeaconDTO beacon = new NestedBeaconDTO();
    beacon.setId(beaconId);
    ping.setBeacon(beacon);
    NestedChronometerDTO c = new NestedChronometerDTO();
    c.setId(chronoId);
    ping.setChronometer(c);
    return ping;
  }
}
