package org.trd.app.teknichrono.model.manage;

import org.junit.Before;
import org.junit.Test;
import org.trd.app.teknichrono.model.dto.LapTimeDTO;
import org.trd.app.teknichrono.model.dto.NestedLocationDTO;
import org.trd.app.teknichrono.model.dto.NestedSessionDTO;
import org.trd.app.teknichrono.model.dto.TestLapTimeDTOCreator;
import org.trd.app.teknichrono.model.jpa.LapTime;
import org.trd.app.teknichrono.model.jpa.TestLapTimeCreator;

import java.util.ArrayList;
import java.util.List;

public class LapTimeFilterTest {


  private TestLapTimeDTOCreator creator;
  private TestLapTimeCreator laptimeCreator;

  @Before
  public void setUp() throws Exception {
    creator = new TestLapTimeDTOCreator();
    laptimeCreator = new TestLapTimeCreator();
  }

  @Test
  public void filterLapsWithoutDuration() {
    LapTimeFilter testMe = new LapTimeFilter();
    long time = System.currentTimeMillis();
    List<LapTimeDTO> result = new ArrayList<>();
    result.add(creator.createDTOLapTime(1, 1, time, time + 100000, 1));
    result.add(creator.createDTOLapTime(2, 1, time, time + 100, 2));
    result.add(creator.createDTOLapTime(3, 1, time, time + 1, 3));
    result.add(creator.createDTOLapTime(4, 2, time, time + 200, 1));
    result.add(creator.createDTOLapTime(5, 3, time, time + 0, 1));
    result.add(creator.createDTOLapTime(6, 4, time, time + 400, 1));
    testMe.filterNoDuration(result);
    org.junit.Assert.assertEquals(5, result.size());
    for (LapTimeDTO r : result) {
      org.junit.Assert.assertTrue(r.getDuration() > 0);

    }

  }

  @Test
  public void incompleteLapsDontInfluenceFiltering() {
    LapTimeFilter testMe = new LapTimeFilter();
    long time = System.currentTimeMillis();
    List<LapTimeDTO> result = new ArrayList<>();
    NestedSessionDTO session = new NestedSessionDTO();
    NestedLocationDTO location = new NestedLocationDTO();
    session.setLocation(location);
    result.add(creator.createDTOLapTimeWithSession(1, 1, time, time + 100, 1, session));
    result.add(creator.createDTOLapTimeWithSession(2, 1, time, time + 100, 2, session));
    result.add(creator.createDTOLapTimeWithSession(3, 1, time, time + 100, 3, session));
    result.add(creator.createDTOLapTimeWithSession(4, 2, time, time + 200, 1, session));
    result.add(creator.createDTOLapTimeWithSession(5, 3, time, time + 300, 1, session));
    result.add(creator.createDTOLapTimeWithSession(6, 4, time, time + 400, 1, session));
    // Average should be 200
    testMe.filterExtreme(result);
    org.junit.Assert.assertEquals(6, result.size());
    for (int i = 7; i < 100; i++) {
      result.add(creator.createDTOLapTimeWithSession(i, i, time, 0, 1, session));
    }
    testMe.filterExtreme(result);
    org.junit.Assert.assertEquals(99, result.size());
  }

  @Test
  public void convertsAndFilters() {
    LapTimeFilter testMe = new LapTimeFilter();
    ArrayList<LapTime> searchResults = new ArrayList<LapTime>();
    long time = System.currentTimeMillis();
    LapTime l1 = laptimeCreator.createLapTimeWithNoIntermediate(1, true, time += 101);
    LapTime l2 = laptimeCreator.createLapTimeWithNoIntermediate(2, true, time += 102);
    LapTime l3 = laptimeCreator.createLapTimeWithNoIntermediate(3, true, time += 103); // long
    LapTime l4 = laptimeCreator.createLapTimeWithNoIntermediate(4, true, time += 1502);
    LapTime l5 = laptimeCreator.createLapTimeWithNoIntermediate(5, true, time += 98);
    LapTime l6 = laptimeCreator.createLapTimeWithNoIntermediate(6, true, time += 99);
    LapTime l7 = laptimeCreator.createLapTimeWithNoIntermediate(1, true, time += 104);
    LapTime l8 = laptimeCreator.createLapTimeWithNoIntermediate(2, true, time += 101); // short
    LapTime l9 = laptimeCreator.createLapTimeWithNoIntermediate(3, true, time += 10); // not finished
    searchResults.add(l4);
    searchResults.add(l1);
    searchResults.add(l6);
    searchResults.add(l5);
    searchResults.add(l2);
    searchResults.add(l3);
    searchResults.add(l7);
    searchResults.add(l8);
    searchResults.add(l9);
    List<LapTimeDTO> result = (new LapTimeConverter()).convert(searchResults);
    testMe.filterExtreme(result);
    // Only the long laptime and short has been filtered
    org.junit.Assert.assertEquals(7, result.size());
    org.junit.Assert.assertTrue(!result.contains(l3));
    org.junit.Assert.assertTrue(!result.contains(l8));
  }

  @Test
  public void keepsOnlyBestForEachPilot() {
    LapTimeFilter testMe = new LapTimeFilter();
    ArrayList<LapTime> searchResults = new ArrayList<LapTime>();
    long time = System.currentTimeMillis();

    LapTime l1 = laptimeCreator.createLapTimeWithPilot(1, 1, true, time);
    time = time + 111;
    LapTime l3 = laptimeCreator.createLapTimeWithPilot(3, 1, true, time);
    time = time + 133;
    LapTime l5 = laptimeCreator.createLapTimeWithPilot(5, 1, true, time);
    time = time + 155;
    LapTime l2 = laptimeCreator.createLapTimeWithPilot(2, 2, true, time);
    time = time + 162;
    LapTime l4 = laptimeCreator.createLapTimeWithPilot(4, 2, true, time);
    time = time + 124;
    LapTime l6 = laptimeCreator.createLapTimeWithPilot(6, 2, true, time);
    // l6 does not end

    searchResults.add(l4);
    searchResults.add(l1);
    searchResults.add(l6);
    searchResults.add(l5);
    searchResults.add(l2);
    searchResults.add(l3);
    List<LapTimeDTO> result = (new LapTimeConverter()).convert(searchResults);
    testMe.keepOnlyBest(result);

    org.junit.Assert.assertEquals(2, result.size());
    org.junit.Assert.assertEquals(l1.getId(), result.get(0).getId());
    org.junit.Assert.assertEquals(l4.getId(), result.get(1).getId());
  }


  @Test
  public void ordersByNbLapsAndThenLapEndThenLapStart() {
    LapTimeFilter testMe = new LapTimeFilter();
    long startForAll = System.currentTimeMillis();

    // Laps of pilot 1 - has more laps
    long start = startForAll;
    long end = start + 111;
    LapTimeDTO l11 = creator.createDTOLapTime(11, 1, start, end, 3);
    start = end;
    end = start + 111;
    LapTimeDTO l12 = creator.createDTOLapTime(12, 1, start, end, 3);
    start = end;
    end = start + 111;
    LapTimeDTO l13 = creator.createDTOLapTime(13, 1, start, end, 3);

    // Laps of pilot 2 - has first lap end
    start = startForAll;
    end = start + 112;
    LapTimeDTO l21 = creator.createDTOLapTime(21, 2, start, end, 2);
    start = end;
    end = start + 112;
    LapTimeDTO l22 = creator.createDTOLapTime(22, 2, start, end, 2);

    // Laps of pilot 3 -
    start = startForAll;
    end = start + 113;
    LapTimeDTO l31 = creator.createDTOLapTime(31, 3, start, end, 2);
    start = end;
    end = start + 113;
    LapTimeDTO l32 = creator.createDTOLapTime(32, 3, start, end, 2);

    // Laps of pilot 4 - has first lap start
    start = startForAll;
    end = start + 114;
    LapTimeDTO l41 = creator.createDTOLapTime(41, 4, start, -1, 2);
    start = end;
    end = start + 114;
    LapTimeDTO l42 = creator.createDTOLapTime(42, 4, start, -1, 2);

    // Laps of pilot 5 -
    start = startForAll;
    end = start + 115;
    LapTimeDTO l51 = creator.createDTOLapTime(51, 5, start, -1, 2);
    start = end;
    end = start + 115;
    LapTimeDTO l52 = creator.createDTOLapTime(52, 5, start, -1, 2);

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

  }
}