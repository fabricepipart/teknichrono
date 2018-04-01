package org.trd.app.teknichrono.business;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.trd.app.teknichrono.model.Chronometer;
import org.trd.app.teknichrono.model.LapTime;
import org.trd.app.teknichrono.model.Location;
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
    LapTime l1 = createLapTime(1, false);
    LapTime l2 = createLapTime(2, false);
    LapTime l3 = createLapTime(3, false);
    LapTime l4 = createLapTime(4, false);
    LapTime l5 = createLapTime(5, false);
    LapTime l6 = createLapTime(6, false);
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
    LapTime l1 = createLapTime(1, true, time += 101);
    LapTime l2 = createLapTime(2, true, time += 102);
    LapTime l3 = createLapTime(3, true, time += 103);
    LapTime l4 = createLapTime(4, true, time += 502);
    LapTime l5 = createLapTime(5, true, time += 98);
    LapTime l6 = createLapTime(6, true, time += 99);
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
  public void convertsAndOrderByStartTimeForLoopTrack() {
    LapTimeManager testMe = new LapTimeManager();
    ArrayList<LapTime> searchResults = new ArrayList<LapTime>();
    LapTime l1 = createLapTime(1, true);
    LapTime l2 = createLapTime(2, true);
    LapTime l3 = createLapTime(3, true);
    LapTime l4 = createLapTime(4, true);
    LapTime l5 = createLapTime(5, true);
    LapTime l6 = createLapTime(6, true);
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

  private LapTime createLapTime(int i, boolean loop, long startDate) {
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

  private LapTime createLapTime(int i, boolean loop) {
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
