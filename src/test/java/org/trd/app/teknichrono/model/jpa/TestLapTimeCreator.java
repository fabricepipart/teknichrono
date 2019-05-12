package org.trd.app.teknichrono.model.jpa;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class TestLapTimeCreator {

  private long lapId = 0L;
  private long pilotId = 0L;
  private long now = System.currentTimeMillis();
  private long sessionId = 0L;
  private long locationId = 0L;
  private boolean locationLoops = false;

  public void nextSession() {
    sessionId++;
    now = now + (24 * 60 * 60 * 1000L);
  }

  public void nextLocation(boolean loop) {
    locationLoops = loop;
    locationId++;
  }

  public void nextPilot() {
    pilotId++;
  }

  public void setPilot(Pilot p) {
    pilotId = p.id;
  }

  public void setSession(Session s) {
    sessionId = s.id;
  }


  public LapTime createLapTimeWithIntermediates(long i1, long i2, long i3, long i4) {
    LapTime entity = new LapTime();
    entity.id = ++lapId;
    Pilot pilot = new Pilot();
    pilot.id = pilotId;
    Beacon b = new Beacon();
    b.id = pilotId;
    pilot.setCurrentBeacon(b);
    entity.setPilot(pilot);
    Session s = new Session();
    Location l = new Location();
    l.id = locationId;
    l.setLoopTrack(locationLoops);
    s.setLocation(l);
    s.setId(sessionId);
    Chronometer c0 = new Chronometer();
    c0.id = 0L;
    s.addChronometer(c0);
    Chronometer c1 = new Chronometer();
    c1.id = 1L;
    s.addChronometer(c1);
    Chronometer c2 = new Chronometer();
    c2.id = 2L;
    s.addChronometer(c2);
    Chronometer c3 = new Chronometer();
    c3.id = 3L;
    s.addChronometer(c3);
    entity.setSession(s);
    if (i1 >= 0) {
      Ping p = new Ping();
      p.setDateTime(new Timestamp(now + i1));
      p.setChrono(c0);
      p.setBeacon(b);
      entity.addIntermediates(p);
    }
    if (i2 >= 0) {
      Ping p = new Ping();
      p.setDateTime(new Timestamp(now + i2));
      p.setChrono(c1);
      p.setBeacon(b);
      entity.addIntermediates(p);
    }
    if (i3 >= 0) {
      Ping p = new Ping();
      p.setDateTime(new Timestamp(now + i3));
      p.setChrono(c2);
      p.setBeacon(b);
      entity.addIntermediates(p);
    }
    if (i4 >= 0) {
      Ping p = new Ping();
      p.setDateTime(new Timestamp(now + i4));
      p.setChrono(c3);
      p.setBeacon(b);
      entity.addIntermediates(p);
    }
    return entity;
  }

  public List<LapTime> createLapsWithIntermediates() {
    List laps = new ArrayList();
    // Pilots with 1 lap
    // Pilots with lap before does not end
    // Pilots with lap after does not start
    // Pilots with no start
    // Pilots with no inter1
    // Pilots with no inter2
    // Pilots with no end

    // One Lap , no start
    nextPilot();
    laps.add(createLapTimeWithIntermediates(-1, 30001, 50001, 80001));
    // One Lap , no inter1
    nextPilot();
    laps.add(createLapTimeWithIntermediates(0, -1, 50022, 80022));
    // One Lap , no inter2
    nextPilot();
    laps.add(createLapTimeWithIntermediates(0, 30500, -1, 80303));
    // One Lap , no end
    nextPilot();
    laps.add(createLapTimeWithIntermediates(0, 30800, 50300, -1));
    // One Lap , no start, no inter1
    nextPilot();
    laps.add(createLapTimeWithIntermediates(-1, -1, 50030, 80404));
    // One Lap , no start, no inter2
    nextPilot();
    laps.add(createLapTimeWithIntermediates(-1, 30400, -1, 80505));
    // One Lap , no start, no end
    nextPilot();
    laps.add(createLapTimeWithIntermediates(-1, 30300, 50030, -1));
    // One Lap , no inter1, no inter2
    nextPilot();
    laps.add(createLapTimeWithIntermediates(0, -1, -1, 80606));
    // One Lap , no inter1, no end
    nextPilot();
    laps.add(createLapTimeWithIntermediates(0, -1, 50400, -1));
    // One Lap , no inter2, no end
    nextPilot();
    laps.add(createLapTimeWithIntermediates(0, 30200, -1, -1));
    // One Lap , no start, no inter1, no inter2
    nextPilot();
    laps.add(createLapTimeWithIntermediates(-1, -1, -1, 80707));
    // One Lap , no start, no inter1, no end
    nextPilot();
    laps.add(createLapTimeWithIntermediates(-1, -1, 51020, -1));
    // One Lap , no start, no inter2, no end
    nextPilot();
    laps.add(createLapTimeWithIntermediates(-1, 31000, -1, -1));
    // One Lap , no inter1, no inter2, no end
    nextPilot();
    laps.add(createLapTimeWithIntermediates(0, -1, -1, -1));

    // Lap before does not end, no start, Lap after complete
    nextPilot();
    laps.add(createLapTimeWithIntermediates(0, 30800, 50300, -1));
    laps.add(createLapTimeWithIntermediates(-1, 130001, 150001, 180001));
    laps.add(createLapTimeWithIntermediates(200123, 230350, 250190, 280012));
    // Lap before does not end, no inter1, Lap after complete
    nextPilot();
    laps.add(createLapTimeWithIntermediates(-1, 30300, 50030, -1));
    laps.add(createLapTimeWithIntermediates(100124, -1, 150022, 180022));
    laps.add(createLapTimeWithIntermediates(200125, 230340, 250180, 280013));
    // Lap before does not end, no inter2, Lap after complete
    nextPilot();
    laps.add(createLapTimeWithIntermediates(0, -1, 53400, -1));
    laps.add(createLapTimeWithIntermediates(100134, 130500, -1, 180303));
    laps.add(createLapTimeWithIntermediates(200135, 230330, 250170, 280014));
    // Lap before does not end, no end, Lap after complete
    nextPilot();
    laps.add(createLapTimeWithIntermediates(0, 30200, -1, -1));
    laps.add(createLapTimeWithIntermediates(100138, 130800, 150300, -1));
    laps.add(createLapTimeWithIntermediates(200139, 230320, 250170, 280015));
    // Lap before does not end, no start, no inter1, Lap after complete
    nextPilot();
    laps.add(createLapTimeWithIntermediates(-1, -1, 51020, -1));
    laps.add(createLapTimeWithIntermediates(-1, -1, 150030, 180404));
    laps.add(createLapTimeWithIntermediates(200147, 230310, 250160, 280016));
    // Lap before does not end, no start, no inter2, Lap after complete
    nextPilot();
    laps.add(createLapTimeWithIntermediates(-1, 31000, -1, -1));
    laps.add(createLapTimeWithIntermediates(-1, 130400, -1, 180505));
    laps.add(createLapTimeWithIntermediates(200159, 230300, 250150, 280017));
    // Lap before does not end, no start, no end, Lap after complete
    nextPilot();
    laps.add(createLapTimeWithIntermediates(0, -1, -1, -1));
    laps.add(createLapTimeWithIntermediates(-1, 130300, 150030, -1));
    laps.add(createLapTimeWithIntermediates(200161, 230290, 250490, 280018));
    // Lap before does not end, no inter1, no inter2, Lap after complete
    nextPilot();
    laps.add(createLapTimeWithIntermediates(0, 30800, 50300, -1));
    laps.add(createLapTimeWithIntermediates(100162, -1, -1, 180606));
    laps.add(createLapTimeWithIntermediates(200163, 230280, 250480, 280019));
    // Lap before does not end, no inter1, no end, Lap after complete
    nextPilot();
    laps.add(createLapTimeWithIntermediates(-1, 30300, 50030, -1));
    laps.add(createLapTimeWithIntermediates(100164, -1, 150400, -1));
    laps.add(createLapTimeWithIntermediates(200165, 230270, 250470, 280020));
    // Lap before does not end, no inter2, no end, Lap after complete
    nextPilot();
    laps.add(createLapTimeWithIntermediates(0, -1, 50400, -1));
    laps.add(createLapTimeWithIntermediates(100166, 130200, -1, -1));
    laps.add(createLapTimeWithIntermediates(200167, 230260, 250450, 280021));
    // Lap before does not end, no start, no inter1, no inter2, Lap after complete
    nextPilot();
    laps.add(createLapTimeWithIntermediates(0, 30200, -1, -1));
    laps.add(createLapTimeWithIntermediates(-1, -1, -1, 180707));
    laps.add(createLapTimeWithIntermediates(200168, 230250, 250890, 280022));
    // Lap before does not end, no start, no inter1, no end, Lap after complete
    nextPilot();
    laps.add(createLapTimeWithIntermediates(-1, -1, 51020, -1));
    laps.add(createLapTimeWithIntermediates(-1, -1, 151020, -1));
    laps.add(createLapTimeWithIntermediates(200171, 230150, 250450, 280023));
    // Lap before does not end, no start, no inter2, no end, Lap after complete
    nextPilot();
    laps.add(createLapTimeWithIntermediates(-1, 31000, -1, -1));
    laps.add(createLapTimeWithIntermediates(-1, 131000, -1, -1));
    laps.add(createLapTimeWithIntermediates(200172, 230240, 250160, 280024));
    // Lap before does not end, no inter1, no inter2, no end, Lap after complete
    nextPilot();
    laps.add(createLapTimeWithIntermediates(0, -1, -1, -1));
    laps.add(createLapTimeWithIntermediates(100177, -1, -1, -1));
    laps.add(createLapTimeWithIntermediates(200178, 230230, 250120, 280025));

    // Lap before complete, no start, Lap after no start
    nextPilot();
    laps.add(createLapTimeWithIntermediates(0, 30350, 50191, 80026));
    laps.add(createLapTimeWithIntermediates(-1, 130001, 150011, 180026));
    laps.add(createLapTimeWithIntermediates(200179, 230800, 250310, -1));
    // Lap before complete, no inter1, Lap after no start
    nextPilot();
    laps.add(createLapTimeWithIntermediates(0, 32340, 52180, 80027));
    laps.add(createLapTimeWithIntermediates(100181, -1, 150222, 180027));
    laps.add(createLapTimeWithIntermediates(-1, 232300, 250230, -1));
    // Lap before complete, no inter2, Lap after no start
    nextPilot();
    laps.add(createLapTimeWithIntermediates(0, 30333, 53170, 80028));
    laps.add(createLapTimeWithIntermediates(100182, 133500, -1, 180328));
    laps.add(createLapTimeWithIntermediates(200183, -1, 250430, -1));
    // Lap before complete, no end, Lap after no start
    nextPilot();
    laps.add(createLapTimeWithIntermediates(0, 34320, 54170, 80029));
    laps.add(createLapTimeWithIntermediates(100184, 134800, 150340, -1));
    laps.add(createLapTimeWithIntermediates(200185, 230240, -1, -1));
    // Lap before complete, no start, no inter1, Lap after no start
    nextPilot();
    laps.add(createLapTimeWithIntermediates(0, 30315, 55160, 85030));
    laps.add(createLapTimeWithIntermediates(-1, -1, 150530, 180430));
    laps.add(createLapTimeWithIntermediates(-1, -1, 251520, -1));
    // Lap before complete, no start, no inter2, Lap after no start
    nextPilot();
    laps.add(createLapTimeWithIntermediates(0, 30360, 50156, 80031));
    laps.add(createLapTimeWithIntermediates(-1, 136400, -1, 180531));
    laps.add(createLapTimeWithIntermediates(-1, 231600, -1, -1));
    // Lap before complete, no start, no end, Lap after no start
    nextPilot();
    laps.add(createLapTimeWithIntermediates(0, 30790, 50470, 80032));
    laps.add(createLapTimeWithIntermediates(-1, 137300, 150730, -1));
    laps.add(createLapTimeWithIntermediates(200191, -1, -1, -1));
    // Lap before complete, no inter1, no inter2, Lap after no start
    nextPilot();
    laps.add(createLapTimeWithIntermediates(0, 38280, 50880, 80033));
    laps.add(createLapTimeWithIntermediates(100192, -1, -1, 180633));
    laps.add(createLapTimeWithIntermediates(200193, 238800, 258300, -1));
    // Lap before complete, no inter1, no end, Lap after no start
    nextPilot();
    laps.add(createLapTimeWithIntermediates(0, 30970, 50479, 80034));
    laps.add(createLapTimeWithIntermediates(100194, -1, 150490, -1));
    laps.add(createLapTimeWithIntermediates(-1, 239300, 250930, -1));
    // Lap before complete, no inter2, no end, Lap after no start
    nextPilot();
    laps.add(createLapTimeWithIntermediates(0, 30265, 58450, 80035));
    laps.add(createLapTimeWithIntermediates(100195, 135200, -1, -1));
    laps.add(createLapTimeWithIntermediates(200196, -1, 258400, -1));
    // Lap before complete, no start, no inter1, no inter2, Lap after no start
    nextPilot();
    laps.add(createLapTimeWithIntermediates(0, 36250, 56890, 80036));
    laps.add(createLapTimeWithIntermediates(-1, -1, -1, 136736));
    laps.add(createLapTimeWithIntermediates(200197, 236200, -1, -1));
    // Lap before complete, no start, no inter1, no end, Lap after no start
    nextPilot();
    laps.add(createLapTimeWithIntermediates(0, 30154, 54450, 80037));
    laps.add(createLapTimeWithIntermediates(-1, -1, 154020, -1));
    laps.add(createLapTimeWithIntermediates(-1, -1, 25420, -1));
    // Lap before complete, no start, no inter2, no end, Lap after no start
    nextPilot();
    laps.add(createLapTimeWithIntermediates(0, 32240, 55160, 80038)); // 93
    laps.add(createLapTimeWithIntermediates(-1, 141000, -1, -1));
    laps.add(createLapTimeWithIntermediates(-1, 231200, -1, -1));
    // Lap before complete, no inter1, no inter2, no end, Lap after no start
    nextPilot();
    laps.add(createLapTimeWithIntermediates(0, 30230, 50120, 80039)); // 96
    laps.add(createLapTimeWithIntermediates(100198, -1, -1, -1));
    laps.add(createLapTimeWithIntermediates(200199, -1, -1, -1));

    // Laps with nothing!
    nextPilot();
    laps.add(createLapTimeWithIntermediates(-1, -1, -1, -1));
    laps.add(createLapTimeWithIntermediates(-1, -1, -1, -1)); // 100
    nextPilot();
    laps.add(createLapTimeWithIntermediates(0, 30234, 50124, 80040));
    laps.add(createLapTimeWithIntermediates(-1, -1, -1, -1));
    laps.add(createLapTimeWithIntermediates(200200, 230235, 250125, 280040)); // 103

    return laps;

  }

  public LapTime createLapTimeWithNoIntermediate(long i, boolean loop, long startDate) {
    LapTime laptime = new LapTime();
    laptime.id = i;
    Timestamp time = new Timestamp(startDate);
    laptime.setStartDate(time);
    Ping ping = new Ping();
    Chronometer chrono = new Chronometer();
    ping.setChrono(chrono);
    ping.setDateTime(time);
    laptime.addIntermediates(ping);
    Pilot p = new Pilot();
    p.id = 1L;
    laptime.setPilot(p);

    Session session = new Session();
    session.addChronometer(chrono);
    Location location = new Location();
    session.setLocation(location);
    location.setLoopTrack(loop);
    laptime.setSession(session);
    return laptime;
  }

  public LapTime createLapTimeWithIntermediates(long i, boolean loop) {
    LapTime laptime = new LapTime();
    laptime.id = i;
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

  private LapTime createLapTimeWithPilot(long i, boolean loop, long startDate) {
    return createLapTimeWithPilot(i, i, loop, startDate);
  }

  public LapTime createLapTimeWithPilot(long i, long pilotId, boolean loop, long startDate) {
    LapTime laptime = new LapTime();
    laptime.id = i;
    Timestamp time = new Timestamp(startDate);
    laptime.setStartDate(time);
    Ping ping = new Ping();
    Chronometer chrono = new Chronometer();
    ping.setChrono(chrono);
    ping.setDateTime(time);
    laptime.addIntermediates(ping);
    Pilot p = new Pilot();
    p.id = pilotId;
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
