package org.trd.app.teknichrono.business;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.trd.app.teknichrono.model.Chronometer;
import org.trd.app.teknichrono.model.LapTime;
import org.trd.app.teknichrono.model.Location;
import org.trd.app.teknichrono.model.Pilot;
import org.trd.app.teknichrono.model.Ping;
import org.trd.app.teknichrono.model.Session;
import org.trd.app.teknichrono.rest.dto.LapTimeDTO;

public class LapTimeManagerTest {

  @Test
  public void convertsEmptyListToEmptyList() {
    LapTimeManager testMe = new LapTimeManager();
    List<LapTimeDTO> result = testMe.convert(new ArrayList<LapTime>());
    org.junit.Assert.assertTrue(result.isEmpty());
  }

  @Test
  public void convertsAndOrderByStartTime() {
    LapTimeManager testMe = new LapTimeManager();
    ArrayList<LapTime> searchResults = new ArrayList<LapTime>();
    LapTime l1 = createLapTimeWithIntermediates(1, false);
    LapTime l2 = createLapTimeWithIntermediates(2, false);
    LapTime l3 = createLapTimeWithIntermediates(3, false);
    LapTime l4 = createLapTimeWithIntermediates(4, false);
    LapTime l5 = createLapTimeWithIntermediates(5, false);
    LapTime l6 = createLapTimeWithIntermediates(6, false);
    searchResults.add(l4);
    searchResults.add(l1);
    searchResults.add(l6);
    searchResults.add(l5);
    searchResults.add(l2);
    searchResults.add(l3);
    List<LapTimeDTO> result = testMe.convert(searchResults);
    org.junit.Assert.assertEquals(1, result.get(0).getId());
    org.junit.Assert.assertEquals(2, result.get(1).getId());
    org.junit.Assert.assertEquals(3, result.get(2).getId());
    org.junit.Assert.assertEquals(4, result.get(3).getId());
    org.junit.Assert.assertEquals(5, result.get(4).getId());
    org.junit.Assert.assertEquals(6, result.get(5).getId());
  }

  @Test
  public void convertsAndFilters() {
    LapTimeManager testMe = new LapTimeManager();
    ArrayList<LapTime> searchResults = new ArrayList<LapTime>();
    long time = System.currentTimeMillis();
    ;
    LapTime l1 = createLapTimeWithNoIntermediate(1, true, time += 101);
    LapTime l2 = createLapTimeWithNoIntermediate(2, true, time += 102);
    LapTime l3 = createLapTimeWithNoIntermediate(3, true, time += 103);
    LapTime l4 = createLapTimeWithNoIntermediate(4, true, time += 502);
    LapTime l5 = createLapTimeWithNoIntermediate(5, true, time += 98);
    LapTime l6 = createLapTimeWithNoIntermediate(6, true, time += 99);
    searchResults.add(l4);
    searchResults.add(l1);
    searchResults.add(l6);
    searchResults.add(l5);
    searchResults.add(l2);
    searchResults.add(l3);
    List<LapTimeDTO> result = testMe.convert(searchResults);
    testMe.filterExtreme(result);
    // Only the long laptime has been filtered
    org.junit.Assert.assertEquals(5, result.size());
    org.junit.Assert.assertTrue(!result.contains(l3));
  }

  @Test
  public void ordersByDuration() {
    LapTimeManager testMe = new LapTimeManager();
    ArrayList<LapTime> searchResults = new ArrayList<LapTime>();
    long time = System.currentTimeMillis();
    LapTime l1 = createLapTimeWithNoIntermediate(1, true, time);
    time = time + 111;

    LapTime l2 = createLapTimeWithNoIntermediate(2, true, time);
    time = time + 162;

    LapTime l3 = createLapTimeWithNoIntermediate(3, true, time);
    time = time + 133;

    LapTime l4 = createLapTimeWithNoIntermediate(4, true, time);
    time = time + 124;

    LapTime l5 = createLapTimeWithNoIntermediate(5, true, time);
    time = time + 155;

    LapTime l6 = createLapTimeWithNoIntermediate(6, true, time);
    // l6 does not end

    searchResults.add(l4);
    searchResults.add(l1);
    searchResults.add(l6);
    searchResults.add(l5);
    searchResults.add(l2);
    searchResults.add(l3);
    List<LapTimeDTO> result = testMe.convert(searchResults);
    testMe.orderByDuration(result);

    org.junit.Assert.assertEquals(6, result.size());
    org.junit.Assert.assertEquals(l1.getId(), result.get(0).getId());
    org.junit.Assert.assertEquals(l4.getId(), result.get(1).getId());
    org.junit.Assert.assertEquals(l3.getId(), result.get(2).getId());
    org.junit.Assert.assertEquals(l5.getId(), result.get(3).getId());
    org.junit.Assert.assertEquals(l2.getId(), result.get(4).getId());
    org.junit.Assert.assertEquals(l6.getId(), result.get(5).getId());
  }

  @Test
  public void ordersByNbLapsAndThenLastLapStart() {
    LapTimeManager testMe = new LapTimeManager();
    ArrayList<LapTime> searchResults = new ArrayList<LapTime>();
    long time = System.currentTimeMillis();
    LapTime l1 = createLapTimeWithPilot(1, true, time);
    time = time + 111;

    LapTime l2 = createLapTimeWithPilot(2, true, time);
    time = time + 162;

    LapTime l3 = createLapTimeWithPilot(3, true, time);
    time = time + 133;

    LapTime l4 = createLapTimeWithPilot(4, true, time);
    time = time + 124;

    LapTime l5 = createLapTimeWithPilot(5, true, time);
    time = time + 155;

    LapTime l6 = createLapTimeWithPilot(6, true, time);
    // l6 does not end

    searchResults.add(l4);
    searchResults.add(l1);
    searchResults.add(l6);
    searchResults.add(l5);
    searchResults.add(l2);
    searchResults.add(l3);
    List<LapTimeDTO> result = testMe.convert(searchResults);
    // Ordered by start time 1 2 3 4 5 6
    org.junit.Assert.assertEquals(l1.getId(), result.get(0).getId());
    org.junit.Assert.assertEquals(l2.getId(), result.get(1).getId());
    org.junit.Assert.assertEquals(l3.getId(), result.get(2).getId());
    org.junit.Assert.assertEquals(l4.getId(), result.get(3).getId());
    org.junit.Assert.assertEquals(l5.getId(), result.get(4).getId());
    org.junit.Assert.assertEquals(l6.getId(), result.get(5).getId());

    // Now we set one with more laps
    result.get(0).setLapNumber(2);
    result.get(1).setLapNumber(2);
    result.get(2).setLapNumber(3);
    result.get(3).setLapNumber(2);
    result.get(5).setLapNumber(2);
    result.get(4).setLapNumber(3);

    testMe.orderForRace(result);

    org.junit.Assert.assertEquals(6, result.size());
    org.junit.Assert.assertEquals(l3.getId(), result.get(0).getId());
    org.junit.Assert.assertEquals(l5.getId(), result.get(1).getId());
    org.junit.Assert.assertEquals(l1.getId(), result.get(2).getId());
    org.junit.Assert.assertEquals(l2.getId(), result.get(3).getId());
    org.junit.Assert.assertEquals(l4.getId(), result.get(4).getId());
    org.junit.Assert.assertEquals(l6.getId(), result.get(5).getId());
  }

  @Test
  public void keepsOnlyBestForEachPilot() {
    LapTimeManager testMe = new LapTimeManager();
    ArrayList<LapTime> searchResults = new ArrayList<LapTime>();
    long time = System.currentTimeMillis();

    LapTime l1 = createLapTimeWithPilot(1, 1, true, time);
    time = time + 111;
    LapTime l3 = createLapTimeWithPilot(3, 1, true, time);
    time = time + 133;
    LapTime l5 = createLapTimeWithPilot(5, 1, true, time);
    time = time + 155;
    LapTime l2 = createLapTimeWithPilot(2, 2, true, time);
    time = time + 162;
    LapTime l4 = createLapTimeWithPilot(4, 2, true, time);
    time = time + 124;
    LapTime l6 = createLapTimeWithPilot(6, 2, true, time);
    // l6 does not end

    searchResults.add(l4);
    searchResults.add(l1);
    searchResults.add(l6);
    searchResults.add(l5);
    searchResults.add(l2);
    searchResults.add(l3);
    List<LapTimeDTO> result = testMe.convert(searchResults);
    testMe.keepOnlyBest(result);

    org.junit.Assert.assertEquals(2, result.size());
    org.junit.Assert.assertEquals(l1.getId(), result.get(0).getId());
    org.junit.Assert.assertEquals(l4.getId(), result.get(1).getId());
  }

  @Test
  public void convertsAndOrderByStartTimeForLoopTrack() {
    LapTimeManager testMe = new LapTimeManager();
    ArrayList<LapTime> searchResults = new ArrayList<LapTime>();
    LapTime l1 = createLapTimeWithIntermediates(1, true);
    LapTime l2 = createLapTimeWithIntermediates(2, true);
    LapTime l3 = createLapTimeWithIntermediates(3, true);
    LapTime l4 = createLapTimeWithIntermediates(4, true);
    LapTime l5 = createLapTimeWithIntermediates(5, true);
    LapTime l6 = createLapTimeWithIntermediates(6, true);
    searchResults.add(l4);
    searchResults.add(l1);
    searchResults.add(l6);
    searchResults.add(l5);
    searchResults.add(l2);
    searchResults.add(l3);
    List<LapTimeDTO> result = testMe.convert(searchResults);
    org.junit.Assert.assertEquals(1, result.get(0).getId());
    org.junit.Assert.assertEquals(2, result.get(1).getId());
    org.junit.Assert.assertEquals(3, result.get(2).getId());
    org.junit.Assert.assertEquals(4, result.get(3).getId());
    org.junit.Assert.assertEquals(5, result.get(4).getId());
    org.junit.Assert.assertEquals(6, result.get(5).getId());
  }

  private LapTime createLapTimeWithPilot(int i, boolean loop, long startDate) {
    return createLapTimeWithPilot(i, i, loop, startDate);
  }

  private LapTime createLapTimeWithPilot(int i, int pilotId, boolean loop, long startDate) {
    LapTime laptime = new LapTime();
    laptime.setId(i);
    Timestamp time = new Timestamp(startDate);
    laptime.setStartDate(time);
    Ping ping = new Ping();
    Chronometer chrono = new Chronometer();
    chrono.setChronoIndex(0);
    ping.setChrono(chrono);
    ping.setDateTime(time);
    laptime.addIntermediates(ping);

    Pilot p = new Pilot();
    p.setId(pilotId);
    laptime.setPilot(p);
    Session session = new Session();
    Location location = new Location();
    session.setLocation(location);
    location.setLoopTrack(loop);
    laptime.setSession(session);
    return laptime;
  }

  private LapTime createLapTimeWithNoIntermediate(int i, boolean loop, long startDate) {
    LapTime laptime = new LapTime();
    laptime.setId(i);
    Timestamp time = new Timestamp(startDate);
    laptime.setStartDate(time);
    Ping ping = new Ping();
    Chronometer chrono = new Chronometer();
    chrono.setChronoIndex(0);
    ping.setChrono(chrono);
    ping.setDateTime(time);
    laptime.addIntermediates(ping);

    Session session = new Session();
    Location location = new Location();
    session.setLocation(location);
    location.setLoopTrack(loop);
    laptime.setSession(session);
    return laptime;
  }

  private LapTime createLapTimeWithIntermediates(int i, boolean loop) {
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
      chrono.setChronoIndex(j);
      ping.setChrono(chrono);
      ping.setDateTime(new Timestamp(time.getTime() + (j * 10 * 60 * 1000)));
      laptime.addIntermediates(ping);
    }

    return laptime;
  }

}
