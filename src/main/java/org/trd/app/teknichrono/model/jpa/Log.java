package org.trd.app.teknichrono.model.jpa;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Version;
import java.time.Instant;

@Entity
public class Log extends PanacheEntity {

  @Version
  @Column(name = "version")
  private int version;

  /* =============================== Fields =============================== */
  @Column(columnDefinition = "TIMESTAMP(3)", nullable = false)
  private Instant date;

  @Column
  private String level;

  @Column
  private String loggerName;

  @Column(nullable = false)
  private String message;

  @ManyToOne
  @JoinColumn(name = "chronoId")
  private Chronometer chronometer;

  /* ===================== Getters and setters ======================== */

  public int getVersion() {
    return this.version;
  }

  public void setVersion(final int version) {
    this.version = version;
  }

  public Instant getDate() {
    return this.date;
  }

  public void setDate(Instant date) {
    this.date = date;
  }

  public String getLevel() {
    return this.level;
  }

  public void setLevel(String level) {
    this.level = level;
  }

  public Chronometer getChronometer() {
    return this.chronometer;
  }

  public void setChronometer(Chronometer chronometer) {
    this.chronometer = chronometer;
  }

  public String getLoggerName() {
    return this.loggerName;
  }

  public void setLoggerName(String loggerName) {
    this.loggerName = loggerName;
  }

  public String getMessage() {
    return this.message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  @Override
  public String toString() {
    String result = getClass().getSimpleName() + " ";
    result += "time: " + this.date;
    result += ", level: " + this.level;
    result += ", chronoId: " + this.chronometer;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof Log)) {
      return false;
    }
    Log other = (Log) obj;

    if (this.id != other.id) {
      return false;
    }
    return true;
  }
}
