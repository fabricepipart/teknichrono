package org.trd.app.teknichrono.model.dto;

import java.io.Serializable;

import org.trd.app.teknichrono.model.jpa.Ping;

public class SectorDTO implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 4761496132411889132L;

  private long start;
  private long duration;
  private long fromChronoId;
  private long toChronoId;

  public SectorDTO(final Ping from, final Ping to) {
    if (from != null && to != null) {
      this.start = from.getDateTime().getTime();
      this.duration = to.getDateTime().getTime() - this.start;
      this.fromChronoId = from.getChrono().id;
      this.toChronoId = to.getChrono().id;
    }
  }

  public SectorDTO(final long start, final long fromChronoId, final long duration) {
    this.start = start;
    this.duration = duration;
    this.fromChronoId = fromChronoId;
    this.toChronoId = 0;
  }

  @Override
  public String toString() {
    return fromChronoId + "->" + toChronoId + ":" + duration;
  }

  public long getDuration() {
    return this.duration;
  }

  public void setDuration(final long duration) {
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

  public long getStart() {
    return start;
  }

  public void setStart(long start) {
    this.start = start;
  }
}