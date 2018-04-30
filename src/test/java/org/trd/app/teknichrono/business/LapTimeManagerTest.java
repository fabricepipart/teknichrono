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
import org.trd.app.teknichrono.rest.dto.NestedPilotDTO;

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
  public void ordersByNbLapsAndThenLapEndThenLapStart() {
    LapTimeManager testMe = new LapTimeManager();
    long startForAll = System.currentTimeMillis();

    // Laps of pilot 1 - has more laps
    long start = startForAll;
    long end = start + 111;
    LapTimeDTO l11 = createDTOLapTime(11, 1, start, end, 3);
    start = end;
    end = start + 111;
    LapTimeDTO l12 = createDTOLapTime(12, 1, start, end, 3);
    start = end;
    end = start + 111;
    LapTimeDTO l13 = createDTOLapTime(13, 1, start, end, 3);

    // Laps of pilot 2 - has first lap end
    start = startForAll;
    end = start + 112;
    LapTimeDTO l21 = createDTOLapTime(21, 2, start, end, 2);
    start = end;
    end = start + 112;
    LapTimeDTO l22 = createDTOLapTime(22, 2, start, end, 2);

    // Laps of pilot 3 -
    start = startForAll;
    end = start + 113;
    LapTimeDTO l31 = createDTOLapTime(31, 3, start, end, 2);
    start = end;
    end = start + 113;
    LapTimeDTO l32 = createDTOLapTime(32, 3, start, end, 2);

    // Laps of pilot 4 - has first lap start
    start = startForAll;
    end = start + 114;
    LapTimeDTO l41 = createDTOLapTime(41, 4, start, -1, 2);
    start = end;
    end = start + 114;
    LapTimeDTO l42 = createDTOLapTime(42, 4, start, -1, 2);

    // Laps of pilot 5 -
    start = startForAll;
    end = start + 115;
    LapTimeDTO l51 = createDTOLapTime(51, 5, start, -1, 2);
    start = end;
    end = start + 115;
    LapTimeDTO l52 = createDTOLapTime(52, 5, start, -1, 2);

    List<LapTimeDTO> result = new ArrayList<>();

    result.add(l11);
    result.add(l12);
    result.add(l13);
    result.add(l21);
    result.add(l22);
    result.add(l31);
    result.add(l32);
    result.add(l41);
    result.add(l42);
    result.add(l51);
    result.add(l52);

    testMe.keepOnlyLast(result);
    org.junit.Assert.assertEquals(5, result.size());
    org.junit.Assert.assertTrue(result.contains(l13));
    org.junit.Assert.assertTrue(result.contains(l22));
    org.junit.Assert.assertTrue(result.contains(l32));
    org.junit.Assert.assertTrue(result.contains(l42));
    org.junit.Assert.assertTrue(result.contains(l52));

    testMe.orderForRace(result);

    org.junit.Assert.assertEquals(5, result.size());
    org.junit.Assert.assertEquals(13, result.get(0).getId());
    org.junit.Assert.assertEquals(22, result.get(1).getId());
    org.junit.Assert.assertEquals(32, result.get(2).getId());
    org.junit.Assert.assertEquals(42, result.get(3).getId());
    org.junit.Assert.assertEquals(52, result.get(4).getId());
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

  private LapTimeDTO createDTOLapTime(int i, int pilotId, long startDate, long endDate, int lapNb) {
    LapTimeDTO laptime = new LapTimeDTO();
    laptime.setId(i);
    NestedPilotDTO p = new NestedPilotDTO();
    p.setId(pilotId);
    laptime.setPilot(p);
    laptime.setStartDate(new Timestamp(startDate));
    if (endDate > 0) {
      laptime.setEndDate(new Timestamp(endDate));
    }
    laptime.setLapNumber(lapNb);

    return laptime;
  }

  private LapTime createLapTimeWithPilot(int i, int pilotId, boolean loop, long startDate) {
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

  private LapTime createLapTimeWithNoIntermediate(int i, boolean loop, long startDate) {
    LapTime laptime = new LapTime();
    laptime.setId(i);
    Timestamp time = new Timestamp(startDate);
    laptime.setStartDate(time);
    Ping ping = new Ping();
    Chronometer chrono = new Chronometer();
    ping.setChrono(chrono);
    ping.setDateTime(time);
    laptime.addIntermediates(ping);

    Session session = new Session();
    session.addChronometer(chrono);
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
      ping.setChrono(chrono);
      ping.setDateTime(new Timestamp(time.getTime() + (j * 10 * 60 * 1000)));
      laptime.addIntermediates(ping);
    }

    return laptime;
  }

}
