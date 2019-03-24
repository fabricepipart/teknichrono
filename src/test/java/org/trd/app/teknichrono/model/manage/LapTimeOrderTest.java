package org.trd.app.teknichrono.model.manage;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.trd.app.teknichrono.model.dto.LapTimeDTO;
import org.trd.app.teknichrono.model.dto.TestLapTimeDTOCreator;
import org.trd.app.teknichrono.model.jpa.LapTime;
import org.trd.app.teknichrono.model.jpa.TestLapTimeCreator;

import java.util.ArrayList;
import java.util.List;

public class LapTimeOrderTest {


  private TestLapTimeDTOCreator dtoCreator;
  private TestLapTimeCreator creator;

  @Before
  public void setUp() throws Exception {
    dtoCreator = new TestLapTimeDTOCreator();
    creator = new TestLapTimeCreator();
  }

  @Test
  public void ordersByStartDateOrEndDate() {
    LapTimeOrder testMe = new LapTimeOrder();
    long time = System.currentTimeMillis();
    List<LapTimeDTO> result = new ArrayList<>();
    result.add(dtoCreator.createDTOLapTime(1, 1, time, time + 1000, 1));
    result.add(dtoCreator.createDTOLapTime(2, 1, time, time + 100, 2));
    result.add(dtoCreator.createDTOLapTime(3, 1, time, time + 10, 3));
    result.add(dtoCreator.createDTOLapTime(4, 2, time, time + 1, 1));
    result.add(dtoCreator.createDTOLapTime(5, 3, time, time + 100000, 1));
    result.add(dtoCreator.createDTOLapTime(6, 4, time, -1, 1));
    result.add(dtoCreator.createDTOLapTime(7, 5, time + 10, time + 400, 1));
    result.add(dtoCreator.createDTOLapTime(8, 6, time + 20, time + 400, 1));
    testMe.orderByDate(result);
    org.junit.Assert.assertEquals(8, result.size());
    // By start
    org.junit.Assert.assertEquals(7, result.get(6).getId());
    org.junit.Assert.assertEquals(8, result.get(7).getId());
    // By end
    org.junit.Assert.assertEquals(4, result.get(0).getId());
    org.junit.Assert.assertEquals(3, result.get(1).getId());
    org.junit.Assert.assertEquals(2, result.get(2).getId());
    org.junit.Assert.assertEquals(1, result.get(3).getId());
    org.junit.Assert.assertEquals(5, result.get(4).getId());
    org.junit.Assert.assertEquals(6, result.get(5).getId());

  }

  @Test
  public void ordersByDuration() {
    LapTimeOrder testMe = new LapTimeOrder();
    ArrayList<LapTime> searchResults = new ArrayList<LapTime>();
    long time = System.currentTimeMillis();
    LapTime l1 = creator.createLapTimeWithNoIntermediate(1, true, time);
    time = time + 111;

    LapTime l2 = creator.createLapTimeWithNoIntermediate(2, true, time);
    time = time + 162;

    LapTime l3 = creator.createLapTimeWithNoIntermediate(3, true, time);
    time = time + 133;

    LapTime l4 = creator.createLapTimeWithNoIntermediate(4, true, time);
    time = time + 124;

    LapTime l5 = creator.createLapTimeWithNoIntermediate(5, true, time);
    time = time + 155;

    LapTime l6 = creator.createLapTimeWithNoIntermediate(6, true, time);
    // l6 does not end

    searchResults.add(l4);
    searchResults.add(l1);
    searchResults.add(l6);
    searchResults.add(l5);
    searchResults.add(l2);
    searchResults.add(l3);
    List<LapTimeDTO> result = (new LapTimeConverter()).convert(searchResults);
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
    LapTimeOrder testMe = new LapTimeOrder();
    long startForAll = System.currentTimeMillis();

    // Laps of pilot 1 - has more laps
    long start = startForAll;
    long end = start + 111;
    LapTimeDTO l11 = dtoCreator.createDTOLapTime(11, 1, start, end, 3);
    start = end;
    end = start + 111;
    LapTimeDTO l12 = dtoCreator.createDTOLapTime(12, 1, start, end, 3);
    start = end;
    end = start + 111;
    LapTimeDTO l13 = dtoCreator.createDTOLapTime(13, 1, start, end, 3);

    // Laps of pilot 2 - has first lap end
    start = startForAll;
    end = start + 112;
    LapTimeDTO l21 = dtoCreator.createDTOLapTime(21, 2, start, end, 2);
    start = end;
    end = start + 112;
    LapTimeDTO l22 = dtoCreator.createDTOLapTime(22, 2, start, end, 2);

    // Laps of pilot 3 -
    start = startForAll;
    end = start + 113;
    LapTimeDTO l31 = dtoCreator.createDTOLapTime(31, 3, start, end, 2);
    start = end;
    end = start + 113;
    LapTimeDTO l32 = dtoCreator.createDTOLapTime(32, 3, start, end, 2);

    // Laps of pilot 4 - has first lap start
    start = startForAll;
    end = start + 114;
    LapTimeDTO l41 = dtoCreator.createDTOLapTime(41, 4, start, -1, 2);
    start = end;
    end = start + 114;
    LapTimeDTO l42 = dtoCreator.createDTOLapTime(42, 4, start, -1, 2);

    // Laps of pilot 5 -
    start = startForAll;
    end = start + 115;
    LapTimeDTO l51 = dtoCreator.createDTOLapTime(51, 5, start, -1, 2);
    start = end;
    end = start + 115;
    LapTimeDTO l52 = dtoCreator.createDTOLapTime(52, 5, start, -1, 2);

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

    (new LapTimeFilter()).keepOnlyLast(result);
    testMe.orderForRace(result);
    org.junit.Assert.assertEquals(5, result.size());
    org.junit.Assert.assertEquals(13, result.get(0).getId());
    org.junit.Assert.assertEquals(22, result.get(1).getId());
    org.junit.Assert.assertEquals(32, result.get(2).getId());
    org.junit.Assert.assertEquals(42, result.get(3).getId());
    org.junit.Assert.assertEquals(52, result.get(4).getId());
  }

  @Test
  public void ordersByDurationOrStartDate() {
    List<LapTimeDTO> laps = dtoCreator.createLaps();
    LapTimeOrder testMe = new LapTimeOrder();
    testMe.orderByDuration(laps);
    long previousDuration = -1;
    long previousStart = -1;
    for (LapTimeDTO l : laps) {

      if (l.getDuration() > 0) {
        Assert.assertTrue(l.getDuration() >= previousDuration);
        previousDuration = l.getDuration();
      } else {
        // None order by duration anymore
        previousDuration = Long.MAX_VALUE;
        if (l.getStartDate() != null) {
          // Is it the first that did not end
          if (previousStart >= 0) {
            Assert.assertTrue(l.getStartDate().getTime() >= previousStart);
          }
          previousStart = l.getStartDate().getTime();
        } else {
          // None order by start date anymore
          previousStart = Long.MAX_VALUE;
        }
      }
    }
  }

  @Test
  public void fillGapsInfoWhenOrderedByDuration() {
    LapTimeOrder order = new LapTimeOrder();
    List<LapTimeDTO> laps = dtoCreator.createLaps();
    order.orderByDuration(laps);
    long previousGap = -1;

    for (LapTimeDTO l : laps) {
      if (previousGap == -1) {
        Assert.assertEquals(0, l.getGapWithBest());
      }
      if (l.getDuration() > 0) {
        Assert.assertTrue(l.getGapWithBest() >= previousGap);
      } else {
        // No more gaps
        previousGap = Long.MAX_VALUE;
      }
      previousGap = l.getGapWithBest();
    }
  }
}