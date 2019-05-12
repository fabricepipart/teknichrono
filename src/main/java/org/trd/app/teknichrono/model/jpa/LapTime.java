package org.trd.app.teknichrono.model.jpa;
// Generated 5 mai 2016 11:08:49 by Hibernate Tools 4.3.1.Final

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Version;

@Entity
public class LapTime extends PanacheEntity  {

  @Version
  @Column(name = "version")
  private int version;

  /* =============================== Fields =============================== */

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "pilotId")
  private Pilot pilot;

  // Used to order the laps for the pilot relationship
  @Column(columnDefinition="TIMESTAMP(3)")
  private Timestamp startDate;

  @ManyToOne(fetch = FetchType.LAZY)
  private Session session;

  @OneToMany
  @OrderColumn(name = "dateTime")
  private List<Ping> intermediates = new ArrayList<Ping>();

  /* ===================== Getters and setters ======================== */

  public int getVersion() {
    return this.version;
  }

  public void setVersion(final int version) {
    this.version = version;
  }

  public Pilot getPilot() {
    return pilot;
  }

  public void setPilot(Pilot pilot) {
    this.pilot = pilot;
  }

  public Timestamp getStartDate() {
    return startDate;
  }

  public void setStartDate(Timestamp captureDate) {
    this.startDate = captureDate;
  }

  public Session getSession() {
    return this.session;
  }

  public void setSession(final Session session) {
    this.session = session;
  }

  @Override
  public String toString() {
    String result = getClass().getSimpleName() + " ";
    result += "id: " + id;
    result += "start: " + startDate;
    return result;
  }

  public List<Ping> getIntermediates() {
    return this.intermediates;
  }

  public void setIntermediates(final List<Ping> intermediates) {
    this.intermediates = intermediates;
  }

  public void addIntermediates(Ping p) {
    addIntermediates(this.intermediates.size(), p);
  }

  public void addIntermediates(int index, Ping p) {
    updateStartDate(p);
    this.intermediates.add(index, p);
  }

  private void updateStartDate(Ping p) {
    if (startDate == null || startDate.getTime() > p.getDateTime().getTime()) {
      startDate = p.getDateTime();
    }
  }

  public void setStartDate() {
    startDate = null;
    for (Ping ping : intermediates) {
      updateStartDate(ping);
    }
  }

}
