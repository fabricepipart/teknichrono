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


  public static SectorDTO from(final Ping from, final Ping to) {
    SectorDTO dto = new SectorDTO();
    if (from != null && to != null) {
      dto.start = from.getInstant();
      dto.duration = Duration.between(dto.start, to.getInstant());
      dto.fromChronoId = from.getChrono().id;
      dto.toChronoId = to.getChrono().id;
    }
    return dto;
  }

  public static SectorDTO from(Instant previousLastEnd, Instant endDate, long previousLastChronoId) {
    SectorDTO dto = new SectorDTO();
    dto.start = previousLastEnd;
    dto.duration = Duration.between(previousLastEnd, endDate);
    dto.fromChronoId = previousLastChronoId;
    dto.toChronoId = 0L;
    return dto;
  }
}