package org.trd.app.teknichrono.model.jpa;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Version;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Chronometer extends PanacheEntity {

  public enum ChronometerOrder {
    UPDATE, RESTART
  }

  public enum PingSelectionStrategy {
    FIRST, MID, LAST, HIGH, PROXIMITY
  }

  public enum PingSendStrategy {
    NONE, ASYNC, RESEND, SYNC, STORE
  }

  @Version
  @Column(name = "version")
  private int version;

  /* =============================== Fields =============================== */

  @Column(nullable = false, unique = true)
  private String name;

  // Can be null if after event, items are reassociated
  @OneToMany(mappedBy = "chrono", cascade = CascadeType.REMOVE)
  private Set<Ping> pings = new HashSet<>();

  @ManyToMany(mappedBy = "chronometers")
  private Set<Session> sessions = new HashSet<>();

  @OneToMany(cascade = CascadeType.REMOVE)
  @OrderBy(value = "date")
  @JoinColumn(name = "chronoId")
  private List<Log> logs = new ArrayList<>();

  @Column
  private boolean bluetoothDebug;

  @Column
  private boolean debug;

  @Column
  private boolean sendLogs;

  @Column
  private long txThreshold = -100;

  @Column
  @Enumerated(EnumType.STRING)
  private ChronometerOrder orderToExecute;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private PingSelectionStrategy selectionStrategy;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private PingSendStrategy sendStrategy;

  @Column(nullable = false)
  private Duration inactivityWindow;

  /* ===================== Getters and setters ======================== */
  public Long getId() {
    return this.id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public int getVersion() {
    return this.version;
  }

  public void setVersion(final int version) {
    this.version = version;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Set<Ping> getPings() {
    return this.pings;
  }

  public void setPings(Set<Ping> pings) {
    this.pings = pings;
  }

  public Set<Session> getSessions() {
    return this.sessions;
  }

  public void setSessions(Set<Session> sessions) {
    this.sessions = sessions;
  }

  public boolean isDebug() {
    return this.debug;
  }

  public void setDebug(boolean debug) {
    this.debug = debug;
  }

  public boolean isBluetoothDebug() {
    return this.bluetoothDebug;
  }

  public void setBluetoothDebug(boolean bluetoothDebug) {
    this.bluetoothDebug = bluetoothDebug;
  }

  public ChronometerOrder getOrderToExecute() {
    return this.orderToExecute;
  }

  public void setOrderToExecute(ChronometerOrder orderToExecute) {
    this.orderToExecute = orderToExecute;
  }

  public PingSelectionStrategy getSelectionStrategy() {
    return this.selectionStrategy;
  }

  public void setSelectionStrategy(PingSelectionStrategy selectionStrategy) {
    this.selectionStrategy = selectionStrategy;
  }

  public Duration getInactivityWindow() {
    return this.inactivityWindow;
  }

  public void setInactivityWindow(Duration inactivityWindow) {
    this.inactivityWindow = inactivityWindow;
  }

  public PingSendStrategy getSendStrategy() {
    return this.sendStrategy;
  }

  public void setSendStrategy(PingSendStrategy sendStrategy) {
    this.sendStrategy = sendStrategy;
  }

  public long getTxThreshold() {
    return this.txThreshold;
  }

  public void setTxThreshold(long txThreshold) {
    this.txThreshold = txThreshold;
  }

  public boolean isSendLogs() {
    return this.sendLogs;
  }

  public void setSendLogs(boolean sendLogs) {
    this.sendLogs = sendLogs;
  }

  public List<Log> getLogs() {
    return this.logs;
  }

  public void setLogs(List<Log> logs) {
    this.logs = logs;
  }

  /* ===================== Other ======================== */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof Chronometer)) {
      return false;
    }
    Chronometer other = (Chronometer) obj;
    if (this.id != other.id) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    String result = getClass().getSimpleName() + " ";
    if (this.name != null && !this.name.trim().isEmpty()) {
      result += this.name;
    }
    return result;
  }
}