package org.trd.app.teknichrono.model.jpa;
// Generated 5 mai 2016 11:08:49 by Hibernate Tools 4.3.1.Final

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
public class LapTime extends PanacheEntity {

  @Version
  @Column(name = "version")
  private int version;

  /* =============================== Fields =============================== */

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "pilotId")
  private Pilot pilot;

  // Used to order the laps for the pilot relationship
  @Column
  private Instant startDate;

  @Transient
  private Instant startChronoInstant;

  @Transient
  private Instant endChronoInstant;

  @Transient
  private Instant lastChronoInstant;

  @ManyToOne(fetch = FetchType.LAZY)
  private Session session;

  @OneToMany(orphanRemoval = true)
  @OrderColumn(name = "dateTime")
  private List<Ping> intermediates = new ArrayList<>();

  /* ===================== Getters and setters ======================== */

  public int getVersion() {
    return version;
  }

  public void setVersion(int version) {
    this.version = version;
  }

  public Pilot getPilot() {
    return pilot;
  }

  public void setPilot(Pilot pilot) {
    this.pilot = pilot;
  }

  public Instant getStartDate() {
    if (startDate == null && intermediates != null) {
      recomputeDates();
    }
    return startDate;
  }

  public void setStartDate(Instant captureDate) {
    startDate = captureDate;
  }

  public Instant getStartChronoInstant() {
    if (startChronoInstant == null && intermediates != null) {
      recomputeDates();
    }
    return startChronoInstant;
  }

  public Instant getEndChronoInstant() {
    if (endChronoInstant == null && intermediates != null) {
      recomputeDates();
    }
    return endChronoInstant;
  }

  public Instant getLastChronoInstant() {
    if (lastChronoInstant == null && intermediates != null) {
      recomputeDates();
    }
    return lastChronoInstant;
  }

  public void recomputeDates() {
    startDate = null;
    intermediates.forEach(this::updateDates);
  }

  private void updateDates(Ping p) {
    if (startDate == null || p.getInstant().isBefore(startDate)) {
      startDate = p.getInstant();
    }
    if (session != null) {
      long currentChronoIndex = session.getChronoIndex(p.getChrono());
      if (startChronoInstant == null && currentChronoIndex == 0) {
        startChronoInstant = p.getInstant();
      }
      if (endChronoInstant == null && !isLoopTrack() && currentChronoIndex == (session.getChronometers().size() - 1)) {
        endChronoInstant = p.getInstant();
      }
    }
    if (lastChronoInstant == null || p.getInstant().isAfter(lastChronoInstant)) {
      lastChronoInstant = p.getInstant();
    }
  }

  private boolean isLoopTrack() {
    return session.getLocation() != null && session.getLocation().isLoopTrack();
  }

  public Session getSession() {
    return session;
  }

  public void setSession(Session session) {
    this.session = session;
  }

  public List<Ping> getIntermediates() {
    return intermediates;
  }

  public void setIntermediates(List<Ping> intermediates) {
    this.intermediates = intermediates;
  }

  public void addIntermediates(Ping p) {
    addIntermediates(intermediates.size(), p);
  }

  public void addIntermediates(int index, Ping p) {
    updateDates(p);
    intermediates.add(index, p);
  }

  @Override
  public String toString() {
    String result = getClass().getSimpleName() + " ";
    result += "id: " + id;
    result += "start: " + startDate;
    return result;
  }
}
