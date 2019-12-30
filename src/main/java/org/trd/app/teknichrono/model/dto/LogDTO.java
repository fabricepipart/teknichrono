package org.trd.app.teknichrono.model.dto;

import lombok.Data;
import org.trd.app.teknichrono.model.jpa.Log;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Format imposed by restapi-logging-handler
 *
 * @see "https://github.com/narwhaljames/restapi-logging-handler"
 */
@Data
public class LogDTO implements EntityDTO {

  private Long id;
  private NestedLogMeta meta = new NestedLogMeta();
  private Map details = new HashMap();
  private String log;
  private String level;
  private String message;
  private String pid;
  private String tid;

  private NestedChronometerDTO chronometer;

  public Instant getDate() {
    long seconds = getMeta().getCreated().longValue();
    long micros = (long) ((getMeta().getCreated() - getMeta().getCreated().longValue()) * 1000.0 * 1000.0);
    return Instant.ofEpochSecond(seconds, micros * 1000);
  }

  public static LogDTO fromLog(Log log) {
    return DtoMapper.INSTANCE.asLogDto(log);
  }

}
