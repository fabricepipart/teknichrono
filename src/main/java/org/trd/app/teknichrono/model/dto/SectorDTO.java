package org.trd.app.teknichrono.model.dto;

import lombok.Data;
import org.trd.app.teknichrono.model.jpa.Ping;

import java.time.Duration;
import java.time.Instant;

@Data
public class SectorDTO {

  private Instant start;
  private Duration duration;
  private Long fromChronoId;
  private Long toChronoId;

  SectorDTO(final Ping from, final Ping to) {
    if (from != null && to != null) {
      this.start = from.getInstant();
      this.duration = Duration.between(start, to.getInstant());
      this.fromChronoId = from.getChrono().id;
      this.toChronoId = to.getChrono().id;
    }
  }

  SectorDTO(final Instant start, final long fromChronoId, final Duration duration) {
    this.start = start;
    this.duration = duration;
    this.fromChronoId = fromChronoId;
    this.toChronoId = 0L;
  }
}