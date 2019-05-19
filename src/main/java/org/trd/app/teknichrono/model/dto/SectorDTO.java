package org.trd.app.teknichrono.model.dto;

import org.trd.app.teknichrono.model.jpa.Ping;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;

public class SectorDTO implements Serializable {

  /**
   *
   */
  private static final long serialVersionUID = 4761496132411889132L;

  private Instant start;
  private Duration duration;
  private Long fromChronoId;
  private Long toChronoId;

  public SectorDTO(final Ping from, final Ping to) {
    if (from != null && to != null) {
      this.start = from.getInstant();
      this.duration = Duration.between(start, to.getInstant());
      this.fromChronoId = from.getChrono().id;
      this.toChronoId = to.getChrono().id;
    }
  }

  public SectorDTO(final Instant start, final long fromChronoId, final Duration duration) {
    this.start = start;
    this.duration = duration;
    this.fromChronoId = fromChronoId;
    this.toChronoId = 0L;
  }

  @Override
  public String toString() {
    return fromChronoId + "->" + toChronoId + ":" + duration;
  }

  public Duration getDuration() {
    return this.duration;
  }

  public void setDuration(final Duration duration) {
    this.duration = duration;
  }

  public long getFromChronoId() {
    return fromChronoId;
  }

  public void setFromChronoId(long fromChronoId) {
    this.fromChronoId = fromChronoId;
  }

  public long getToChronoId() {
    return toChronoId;
  }

  public void setToChronoId(long toChronoId) {
    this.toChronoId = toChronoId;
  }

  public Instant getStart() {
    return start;
  }

  public void setStart(Instant start) {
    this.start = start;
  }
}