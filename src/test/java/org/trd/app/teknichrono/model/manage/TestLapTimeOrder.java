package org.trd.app.teknichrono.model.manage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.trd.app.teknichrono.business.view.LapTimeConverter;
import org.trd.app.teknichrono.business.view.LapTimeFiller;
import org.trd.app.teknichrono.business.view.LapTimeFilter;
import org.trd.app.teknichrono.business.view.LapTimeOrder;
import org.trd.app.teknichrono.model.compare.LapTimeStartComparator;
import org.trd.app.teknichrono.model.dto.LapTimeDTO;
import org.trd.app.teknichrono.model.dto.LapTimeDTOCreatorForTests;
import org.trd.app.teknichrono.model.jpa.LapTime;
import org.trd.app.teknichrono.model.jpa.LapTimeCreatorForTests;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class TestLapTimeOrder {


  private LapTimeDTOCreatorForTests dtoCreator;
  private LapTimeCreatorForTests creator;

  @BeforeEach
  public void setUp() {
    dtoCreator = new LapTimeDTOCreatorForTests();
    creator = new LapTimeCreatorForTests();
  }

  @Test
  public void ordersByStartDate() {
    List<LapTime> laps = creator.createLapsWithIntermediates();
    laps.sort(new LapTimeStartComparator());
    Instant previousStart = Instant.MIN;
    boolean lastStartAvailable = false;
    for (LapTime l : laps) {
      if (l.getStartDate() != null) {
        assertThat(l.getStartDate().compareTo(previousStart)).
            isGreaterThanOrEqualTo(0).describedAs("Last seen date in incorrect order");
        previousStart = l.getStartDate();
        lastStartAvailable = true;
      } else {
        assertThat(lastStartAvailable).isFalse().describedAs("There has been a laptime with time before and " +
            "this one has no time. Laps was no time are considered the oldest.");
      }
    }
  }


  @Test
  public void ordersDtoByStartDateOrEndDate() {
    LapTimeOrder testMe = new LapTimeOrder();
    long time = System.currentTimeMillis();
    List<LapTimeDTO> result = new ArrayList<>();
    dtoCreator.nextPilot();
    result.add(dtoCreator.createDTOLapTime(time, time + 1000, 1));
    result.add(dtoCreator.createDTOLapTime(time, time + 100, 2));
    result.add(dtoCreator.createDTOLapTime(time, time + 10, 3));
    dtoCreator.nextPilot();
    result.add(dtoCreator.createDTOLapTime(time, time + 1, 1));
    dtoCreator.nextPilot();
    result.add(dtoCreator.createDTOLapTime(time, time + 100000, 1));
    dtoCreator.nextPilot();
    result.add(dtoCreator.createDTOLapTime(time, -1, 1));
    dtoCreator.nextPilot();
    result.add(dtoCreator.createDTOLapTime(time + 10, time + 400, 1));
    dtoCreator.nextPilot();
    result.add(dtoCreator.createDTOLapTime(time + 20, time + 410, 1));
    testMe.orderbyLastSeen(result);
    Assertions.assertEquals(8, result.size());

    Assertions.assertEquals(6, result.get(0).getId());
    Assertions.assertEquals(4, result.get(1).getId());
    Assertions.assertEquals(3, result.get(2).getId());
    Assertions.assertEquals(2, result.get(3).getId());
    Assertions.assertEquals(7, result.get(4).getId());
    Assertions.assertEquals(8, result.get(5).getId());
    Assertions.assertEquals(1, result.get(6).getId());
    Assertions.assertEquals(5, result.get(7).getId());
  }

  @Test
  public void ordersByStartDateOrEndDateWithIncompleteLaps() {
    LapTimeOrder testMe = new LapTimeOrder();
    List<LapTimeDTO> lapsWithIntermediates = dtoCreator.createLapsWithIntermediates();
    long nbLapsBefore = lapsWithIntermediates.size();
    testMe.orderbyLastSeen(lapsWithIntermediates);

    Assertions.assertEquals(nbLapsBefore, lapsWithIntermediates.size());
    checkOrderByDateWithIntermediates(lapsWithIntermediates);
  }


  private void checkOrderByDateWithIntermediates(List<LapTimeDTO> laps) {
    // All start dates are in order
    Instant previousLastSeen = Instant.MIN;
    boolean lastSeenAvailable = false;
    for (LapTimeDTO l : laps) {
      if (l.getLastSeenDate() != null) {

        assertThat(l.getLastSeenDate().compareTo(previousLastSeen)).
            isGreaterThanOrEqualTo(0).describedAs("Last seen date in incorrect order");
        previousLastSeen = l.getLastSeenDate();
        lastSeenAvailable = true;
      } else {
        assertThat(lastSeenAvailable).isFalse().describedAs("There has been a laptime with time before and " +
            "this one has no time. Laps was no time are considered the oldest.");
      }
    }
  }

  @Test
  public void ordersByDuration() {
    LapTimeOrder testMe = new LapTimeOrder();
    ArrayList<LapTime> searchResults = new ArrayList<LapTime>();
    Instant time = Instant.now();
    LapTime l1 = creator.createLapTimeWithNoIntermediate(1, true, time);
    time = time.plus(Duration.ofMillis(111));

    LapTime l2 = creator.createLapTimeWithNoIntermediate(2, true, time);
    time = time.plus(Duration.ofMillis(162));

    LapTime l3 = creator.createLapTimeWithNoIntermediate(3, true, time);
    time = time.plus(Duration.ofMillis(133));

    LapTime l4 = creator.createLapTimeWithNoIntermediate(4, true, time);
    time = time.plus(Duration.ofMillis(124));

    LapTime l5 = creator.createLapTimeWithNoIntermediate(5, true, time);
    time = time.plus(Duration.ofMillis(155));

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

    Assertions.assertEquals(6, result.size());
    Assertions.assertEquals(l1.id.longValue(), result.get(0).getId());
    Assertions.assertEquals(l4.id.longValue(), result.get(1).getId());
    Assertions.assertEquals(l3.id.longValue(), result.get(2).getId());
    Assertions.assertEquals(l5.id.longValue(), result.get(3).getId());
    Assertions.assertEquals(l2.id.longValue(), result.get(4).getId());
    Assertions.assertEquals(l6.id.longValue(), result.get(5).getId());
  }

  @Test
  public void ordersByNbLapsAndThenLapEndThenLapStart() {
    LapTimeOrder testMe = new LapTimeOrder();
    long startForAll = System.currentTimeMillis();

    // Laps of pilot 1 - has more laps
    long start = startForAll;
    long end = start + 111;
    dtoCreator.nextPilot();
    LapTimeDTO l1 = dtoCreator.createDTOLapTime(start, end, 3);
    start = end;
    end = start + 111;
    LapTimeDTO l2 = dtoCreator.createDTOLapTime(start, end, 3);
    start = end;
    end = start + 111;
    LapTimeDTO l3 = dtoCreator.createDTOLapTime(start, end, 3);

    // Laps of pilot 2 - has first lap end
    start = startForAll;
    end = start + 112;
    dtoCreator.nextPilot();
    LapTimeDTO l4 = dtoCreator.createDTOLapTime(start, end, 2);
    start = end;
    end = start + 112;
    LapTimeDTO l5 = dtoCreator.createDTOLapTime(start, end, 2);

    // Laps of pilot 3 -
    start = startForAll;
    end = start + 113;
    dtoCreator.nextPilot();
    LapTimeDTO l6 = dtoCreator.createDTOLapTime(start, end, 2);
    start = end;
    end = start + 113;
    LapTimeDTO l7 = dtoCreator.createDTOLapTime(start, end, 2);

    // Laps of pilot 4 - has first lap start
    start = startForAll;
    end = start + 114;
    dtoCreator.nextPilot();
    LapTimeDTO l8 = dtoCreator.createDTOLapTime(start, -1, 2);
    start = end;
    end = start + 114;
    LapTimeDTO l9 = dtoCreator.createDTOLapTime(start, -1, 2);

    // Laps of pilot 5 -
    start = startForAll;
    dtoCreator.nextPilot();
    end = start + 115;
    LapTimeDTO l10 = dtoCreator.createDTOLapTime(start, -1, 2);
    start = end;
    end = start + 115;
    LapTimeDTO l11 = dtoCreator.createDTOLapTime(start, -1, 2);

    List<LapTimeDTO> result = new ArrayList<>();

    result.add(l1);
    result.add(l2);
    result.add(l3);
    result.add(l4);
    result.add(l5);
    result.add(l6);
    result.add(l7);
    result.add(l8);
    result.add(l9);
    result.add(l10);
    result.add(l11);

    (new LapTimeFiller()).fillLapsNumber(result);
    (new LapTimeFilter()).keepOnlyLast(result);
    testMe.orderForRace(result);
    Assertions.assertEquals(5, result.size());
    Assertions.assertEquals(3, result.get(0).getId());
    Assertions.assertEquals(5, result.get(1).getId());
    Assertions.assertEquals(7, result.get(2).getId());
    Assertions.assertEquals(9, result.get(3).getId());
    Assertions.assertEquals(11, result.get(4).getId());
  }

  @Test
  public void ordersRaceLapsByNbLapsAndThenLapEndThenLapStart() {
    LapTimeOrder testMe = new LapTimeOrder();
    List<LapTimeDTO> raceLapsWithIntermediates = dtoCreator.createRaceLapsWithIntermediates();
    testMe.orderForRace(raceLapsWithIntermediates);
    checkRaceLapsOrder(raceLapsWithIntermediates);
  }

  public void checkRaceLapsOrder(List<LapTimeDTO> raceLapsWithIntermediates) {
    long lastLapIndex = Long.MAX_VALUE;
    for (LapTimeDTO l : raceLapsWithIntermediates) {
      assertThat(l.getLapIndex()).isLessThanOrEqualTo(lastLapIndex)
          .describedAs("Lap " + l + " has a lap index inferior to previous lap : " + lastLapIndex);
      lastLapIndex = l.getLapIndex();
    }
  }

  @Test
  public void ordersByDurationOrStartDate() {
    List<LapTimeDTO> laps = dtoCreator.createLaps();
    checkOrderByDurationOrStartDate(laps);
  }

  public void checkOrderByDurationOrStartDate(List<LapTimeDTO> laps) {
    LapTimeOrder testMe = new LapTimeOrder();
    testMe.orderByDuration(laps);
    Duration previousDuration = Duration.ZERO;
    Instant previousStart = Instant.MIN;
    for (LapTimeDTO l : laps) {
      if (l.getDuration() != null && l.getDuration().compareTo(Duration.ZERO) > 0) {
        Assertions.assertTrue(l.getDuration().compareTo(previousDuration) >= 0);
        previousDuration = l.getDuration();
      } else {
        // None order by duration anymore
        previousDuration = Duration.ofMillis(Long.MAX_VALUE);
        if (l.getStartDate() != null) {
          // Is it the first that did not end
          if (previousStart.isAfter(Instant.MIN)) {
            Assertions.assertTrue(l.getStartDate().compareTo(previousStart) >= 0);
          }
          previousStart = l.getStartDate();
        } else {
          // None order by start date anymore
          previousStart = Instant.MAX;
        }
      }
    }
  }

  @Test
  public void ordersLapsWithIntermediatesByDurationOrStartDate() {
    List<LapTimeDTO> laps = dtoCreator.createLapsWithIntermediates();
    checkOrderByDurationOrStartDate(laps);
  }

  @Test
  public void fillGapsInfoWhenOrderedByDuration() {
    LapTimeOrder order = new LapTimeOrder();
    List<LapTimeDTO> laps = dtoCreator.createLaps();
    order.orderByDuration(laps);
    laps.removeIf(l -> l.getDuration() == null);
    Duration previousGap = null;

    for (LapTimeDTO l : laps) {
      if (previousGap == null) {
        Assertions.assertEquals(Duration.ZERO, l.getGapWithBest());
      } else if (l.getDuration() != null && l.getDuration().compareTo(Duration.ZERO) > 0) {
        Assertions.assertTrue(l.getGapWithBest().compareTo(previousGap) >= 0);
      }
      previousGap = l.getGapWithBest();
    }
  }

  @Test
  public void lapsWithNoDurationAreLargest() {
    LapTimeOrder order = new LapTimeOrder();
    List<LapTimeDTO> laps = dtoCreator.createLaps();
    order.orderByDuration(laps);
    Collections.reverse(laps);
    boolean firstWithNonNullDuration = false;

    for (LapTimeDTO l : laps) {
      if (firstWithNonNullDuration) {
        Assertions.assertNotNull(l.getDuration());
      } else {
        if (l.getDuration() != null) {
          firstWithNonNullDuration = true;
        }
      }
    }
  }
}
