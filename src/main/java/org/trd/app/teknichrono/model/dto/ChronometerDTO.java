package org.trd.app.teknichrono.model.dto;

import lombok.Data;
import org.trd.app.teknichrono.model.jpa.Chronometer;

import java.time.Duration;

@Data
public class ChronometerDTO implements EntityDTO {

  private Long id;
  private int version;
  private String name;
  private String selectionStrategy = Chronometer.PingSelectionStrategy.HIGH.toString();
  private String sendStrategy = Chronometer.PingSendStrategy.ASYNC.toString();
  private Duration inactivityWindow = Duration.ofSeconds(5);
  private boolean bluetoothDebug = false;
  private boolean debug = false;
  private String orderToExecute;
  private long txThreshold = -100L;
  private boolean sendLogs = false;

  public static ChronometerDTO fromChronometer(Chronometer chronometer) {
    return DtoMapper.INSTANCE.asChronometerDto(chronometer);
  }
}
