package org.trd.app.teknichrono.model.jpa;

import java.sql.Timestamp;

public class TestLapTimeCreator {


  public LapTime createLapTimeWithNoIntermediate(int i, boolean loop, long startDate) {
    LapTime laptime = new LapTime();
    laptime.setId(i);
    Timestamp time = new Timestamp(startDate);
    laptime.setStartDate(time);
    Ping ping = new Ping();
    Chronometer chrono = new Chronometer();
    ping.setChrono(chrono);
    ping.setDateTime(time);
    laptime.addIntermediates(ping);
    Pilot p = new Pilot();
    p.setId(1);
    laptime.setPilot(p);

    Session session = new Session();
    session.addChronometer(chrono);
    Location location = new Location();
    session.setLocation(location);
    location.setLoopTrack(loop);
    laptime.setSession(session);
    return laptime;
  }

  public LapTime createLapTimeWithIntermediates(int i, boolean loop) {
    LapTime laptime = new LapTime();
    laptime.setId(i);
    Timestamp time = new Timestamp(System.currentTimeMillis() + (i * 60 * 60 * 1000));
    laptime.setStartDate(time);
    Session session = new Session();
    Location location = new Location();
    session.setLocation(location);
    location.setLoopTrack(loop);
    laptime.setSession(session);
    for (int j = 0; j < 6; j++) {
      Ping ping = new Ping();
      Chronometer chrono = new Chronometer();
      ping.setChrono(chrono);
      ping.setDateTime(new Timestamp(time.getTime() + (j * 10 * 60 * 1000)));
      laptime.addIntermediates(ping);
    }

    return laptime;
  }

  private LapTime createLapTimeWithPilot(int i, boolean loop, long startDate) {
    return createLapTimeWithPilot(i, i, loop, startDate);
  }

  public LapTime createLapTimeWithPilot(int i, int pilotId, boolean loop, long startDate) {
    LapTime laptime = new LapTime();
    laptime.setId(i);
    Timestamp time = new Timestamp(startDate);
    laptime.setStartDate(time);
    Ping ping = new Ping();
    Chronometer chrono = new Chronometer();
    ping.setChrono(chrono);
    ping.setDateTime(time);
    laptime.addIntermediates(ping);
    Pilot p = new Pilot();
    p.setId(pilotId);
    laptime.setPilot(p);
    Session session = new Session();
    session.addChronometer(chrono);
    Location location = new Location();
    session.setLocation(location);
    location.setLoopTrack(loop);
    laptime.setSession(session);
    return laptime;
  }
}
