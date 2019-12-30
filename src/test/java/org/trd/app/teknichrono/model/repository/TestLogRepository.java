package org.trd.app.teknichrono.model.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.trd.app.teknichrono.model.dto.LogDTO;
import org.trd.app.teknichrono.model.jpa.Chronometer;
import org.trd.app.teknichrono.model.jpa.Log;
import org.trd.app.teknichrono.util.exception.ConflictingIdException;
import org.trd.app.teknichrono.util.exception.NotFoundException;

import java.time.Instant;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TestLogRepository {

  @InjectMocks
  private LogRepository repository;

  @Test
  public void convertToDto() {
    Log log = new Log();
    log.setDate(Instant.now());
    log.setMessage("Log message");
    log.setLoggerName("Logger");
    log.setLevel("Info");
    Chronometer chronometer = new Chronometer();
    chronometer.setId(22L);
    log.setChronometer(chronometer);

    LogDTO logDTO = this.repository.toDTO(log);
    assertThat(logDTO.getMeta().getCreated()).isGreaterThan(1577312101L);
    assertThat(logDTO.getMeta().getCreated()).isLessThan(1677312101L);
    assertThat(logDTO.getLog()).isEqualTo("Logger");
    assertThat(logDTO.getLevel()).isEqualTo("Info");
    assertThat(logDTO.getMessage()).isEqualTo("Log message");
    assertThat(logDTO.getChronometer().getId()).isEqualTo(22);
  }

  @Test
  public void convertFromDto() throws ConflictingIdException, NotFoundException {
    LogDTO dto = new LogDTO();
    dto.getMeta().setCreated(1577312101.835677);
    dto.setMessage("Log message");
    dto.setLog("Logger");
    dto.setLevel("Info");

    //Useless but could be present
    dto.getMeta().setFuncName("sendAll");
    dto.getMeta().setLine(27);
    dto.setDetails(new HashMap());
    dto.setPid("p-41699");
    dto.setTid("t-123145344335872");

    Log log = this.repository.fromDTO(dto);
    assertThat(log.getDate()).isNotNull();
    assertThat(log.getDate().toEpochMilli()).isEqualTo(1577312101835L);
    assertThat(log.getLoggerName()).isEqualTo("Logger");
    assertThat(log.getLevel()).isEqualTo("Info");
    assertThat(log.getMessage()).isEqualTo("Log message");
    assertThat(log.toString()).isNotNull();
  }

  @Test
  public void backAndForth() throws ConflictingIdException, NotFoundException {
    Log log = new Log();
    log.setDate(Instant.now());
    log.setMessage("Log message");
    log.setLoggerName("Logger");
    log.setLevel("Info");

    LogDTO dto = this.repository.toDTO(log);
    Log log2 = this.repository.fromDTO(dto);
    assertThat(log).isEqualTo(log2);
    assertThat(log.toString()).isNotNull();
  }

}
