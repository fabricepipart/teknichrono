package org.trd.app.teknichrono.rest.dto;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.xml.bind.annotation.XmlRootElement;

import org.jboss.logging.Logger;
import org.trd.app.teknichrono.model.LapTime;
import org.trd.app.teknichrono.model.Ping;
import org.trd.app.teknichrono.util.InvalidArgumentException;

@XmlRootElement
public class LapTimeDTO implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = -8453562523141344579L;

  private Logger logger = Logger.getLogger(LapTimeDTO.class);

  private int id;
  private int version;
  private NestedPilotDTO pilot;
  private NestedSessionDTO session;
  private Timestamp startDate;
  // Either the last intermediate ping or the first ping of the next lap
  private Timestamp endDate;
  // In milliseconds
  private long duration;
  private long gapWithPrevious;
  private long gapWithBest;
  private List<SectorDTO> sectors = new ArrayList<SectorDTO>();
  private int lapIndex;
  private int lapNumber;

  public LapTimeDTO() {
  }

  public LapTimeDTO(final LapTime entity) {
    if (entity != null) {
      this.id = entity.getId();
      this.version = entity.getVersion();
      this.pilot = new NestedPilotDTO(entity.getPilot());
      this.session = new NestedSessionDTO(entity.getSession());
      Iterator<Ping> iterIntermediates = entity.getIntermediates().iterator();
      Ping previous = null;
      while (iterIntermediates.hasNext()) {
        Ping element = iterIntermediates.next();
        if (previous != null) {
          this.sectors.add(new SectorDTO(previous, element));
        } else {
          if (entity.getSession().getChronoIndex(element.getChrono()) == 0) {
            this.setStartDate(element);
          }
        }
        previous = element;
      }
      boolean loop = session.isLoopTrack();
      if (!loop && entity.getSession().getChronoIndex(previous.getChrono()) == (session.getChronometersCount() - 1)) {
        this.setEndDate(previous);
      }
    }
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("[id=" + getId());
    sb.append(",pilot=" + pilot.getId());
    sb.append(",session=" + session.getId());
    sb.append(",startDate=" + startDate);
    sb.append(",duration=" + duration + "]");
    return sb.toString();
  }

  //
  public LapTime fromDTO(LapTime entity, EntityManager em) {
    if (entity == null) {
      entity = new LapTime();
    }
    entity.setVersion(this.version);
    if (this.pilot != null) {
      entity.setPilot(this.pilot.fromDTO(entity.getPilot(), em));
    }
    if (this.session != null) {
      entity.setSession(this.session.fromDTO(entity.getSession(), em));
    }
    if (!this.getIntermediates().isEmpty()) {
      logger.error("Sorry I cannot rebuild a LapTime from a LapTimeDTO. Leaving list empty.");
    }
    entity = em.merge(entity);
    return entity;
  }

  public int getId() {
    return this.id;
  }

  public void setId(final int id) {
    this.id = id;
  }

  public int getVersion() {
    return this.version;
  }

  public void setVersion(final int version) {
    this.version = version;
  }

  public NestedPilotDTO getPilot() {
    return this.pilot;
  }

  public void setPilot(final NestedPilotDTO pilot) {
    this.pilot = pilot;
  }

  public NestedSessionDTO getSession() {
    return this.session;
  }

  public void setSession(final NestedSessionDTO session) {
    this.session = session;
  }

  public List<SectorDTO> getIntermediates() {
    return this.sectors;
  }

  public void setIntermediates(final List<SectorDTO> intermediates) {
    this.sectors = intermediates;
  }

  public Timestamp getStartDate() {
    return startDate;
  }

  public void setStartDate(Timestamp startDate) {
    this.startDate = startDate;
    if (endDate != null) {
      setDuration(endDate.getTime() - startDate.getTime());
    }
  }

  public void setStartDate(Ping start) {
    setStartDate(start.getDateTime());
  }

  public Timestamp getEndDate() {
    return endDate;
  }

  public void setEndDate(Timestamp endDate) {
    this.endDate = endDate;
    if (startDate != null) {
      setDuration(endDate.getTime() - startDate.getTime());
    }
  }

  public void setEndDate(Ping end) {
    setEndDate(end.getDateTime());
  }

  public long getDuration() {
    return duration;
  }

  public void setDuration(long duration) {
    if (duration < 0) {
      throw new InvalidArgumentException();
    }
    // System.out.println("Setting duration " + duration);
    this.duration = duration;
  }

  /**
   * If we are on a loop and we identified the lap after this one. Then its
   * start is the end of this one.
   * 
   * @param endDate
   */
  public void addLastSector(Timestamp endDate) {
    // If we have a proper sector, we use it
    if (sectors.size() > 0) {
      SectorDTO previousLast = this.sectors.get(sectors.size() - 1);
      long previousLastStart = previousLast.getStart();
      long previousLastEnd = previousLastStart + previousLast.getDuration();
      // System.out.println("Last sector : " + previousLastEnd + " during " +
      // (endDate.getTime() - previousLastEnd));
      this.sectors
          .add(new SectorDTO(previousLastEnd, previousLast.getToChronoId(), endDate.getTime() - previousLastEnd));
    } else {
      this.sectors.add(new SectorDTO(startDate.getTime(), 0, endDate.getTime() - startDate.getTime()));
    }
    setEndDate(endDate);
  }

  public int getLapIndex() {
    return lapIndex;
  }

  public void setLapIndex(int lapIndex) {
    this.lapIndex = lapIndex;
  }

  public int getLapNumber() {
    return lapNumber;
  }

  public void setLapNumber(int lapNumber) {
    this.lapNumber = lapNumber;
  }

  public long getGapWithPrevious() {
    return gapWithPrevious;
  }

  public void setGapWithPrevious(long gapWithPrevious) {
    this.gapWithPrevious = gapWithPrevious;
  }

  public long getGapWithBest() {
    return gapWithBest;
  }

  public void setGapWithBest(long gapWithBest) {
    this.gapWithBest = gapWithBest;
  }
}