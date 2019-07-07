package org.trd.app.teknichrono.model.manage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.trd.app.teknichrono.business.view.LapTimeConverter;
import org.trd.app.teknichrono.business.view.LapTimeFilter;
import org.trd.app.teknichrono.model.dto.LapTimeDTO;
import org.trd.app.teknichrono.model.dto.LapTimeDTOCreatorForTests;
import org.trd.app.teknichrono.model.dto.NestedLocationDTO;
import org.trd.app.teknichrono.model.dto.NestedSessionDTO;
import org.trd.app.teknichrono.model.jpa.LapTime;
import org.trd.app.teknichrono.model.jpa.LapTimeCreatorForTests;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class TestLapTimeFilter {


  private LapTimeDTOCreatorForTests creator;
  private LapTimeCreatorForTests laptimeCreator;

  @BeforeEach
  public void setUp() {
    creator = new LapTimeDTOCreatorForTests();
    laptimeCreator = new LapTimeCreatorForTests();
  }

  @Test
  public void filterLapsWithoutDuration() {
    LapTimeFilter testMe = new LapTimeFilter();
    long time = System.currentTimeMillis();
    List<LapTimeDTO> result = new ArrayList<>();
    creator.nextPilot();
    result.add(creator.createDTOLapTime(time, time + 100000, 1));
    result.add(creator.createDTOLapTime(time, time + 100, 2));
    result.add(creator.createDTOLapTime(time, time + 1, 3));
    creator.nextPilot();
    result.add(creator.createDTOLapTime(time, time + 200, 1));
    creator.nextPilot();
    result.add(creator.createDTOLapTime(time, time + 0, 1));
    creator.nextPilot();
    result.add(creator.createDTOLapTime(time, time + 400, 1));
    testMe.filterNoDuration(result);
    assertThat(result).hasSize(5);
    for (LapTimeDTO r : result) {
      assertThat(r.getDuration().compareTo(Duration.ZERO)).isGreaterThan(0);

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
    creator.nextPilot();
    result.add(creator.createDTOLapTimeWithSession(time, time + 100, 1, session));
    result.add(creator.createDTOLapTimeWithSession(time, time + 100, 2, session));
    result.add(creator.createDTOLapTimeWithSession(time, time + 100, 3, session));
    creator.nextPilot();
    result.add(creator.createDTOLapTimeWithSession(time, time + 200, 1, session));
    creator.nextPilot();
    result.add(creator.createDTOLapTimeWithSession(time, time + 300, 1, session));
    creator.nextPilot();
    result.add(creator.createDTOLapTimeWithSession(time, time + 400, 1, session));
    // Average should be 200
    testMe.filterExtreme(result);
    assertThat(result).hasSize(6);
    for (int i = 7; i < 100; i++) {
      creator.nextPilot();
      result.add(creator.createDTOLapTimeWithSession(time, 0, 1, session));
    }
    testMe.filterExtreme(result);
    assertThat(result).hasSize(99);
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
    assertThat(result).hasSize(7);
    //assertThat(result).doesNotContain(l3);
    //assertThat(result).doesNotContain(l8);
  }

  @Test
  public void keepsOnlyBestForEachPilot() {
    LapTimeFilter testMe = new LapTimeFilter();
    ArrayList<LapTime> searchResults = new ArrayList<LapTime>();
    Instant time = Instant.now();

    LapTime l1 = laptimeCreator.createLapTimeWithPilot(1, 1, true, time);
    time = time.plus(Duration.ofMillis(111));
    LapTime l3 = laptimeCreator.createLapTimeWithPilot(3, 1, true, time);
    time = time.plus(Duration.ofMillis(133));
    LapTime l5 = laptimeCreator.createLapTimeWithPilot(5, 1, true, time);
    time = time.plus(Duration.ofMillis(155));
    LapTime l2 = laptimeCreator.createLapTimeWithPilot(2, 2, true, time);
    time = time.plus(Duration.ofMillis(162));
    LapTime l4 = laptimeCreator.createLapTimeWithPilot(4, 2, true, time);
    time = time.plus(Duration.ofMillis(124));
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

    assertThat(result).hasSize(2);
    assertThat(result.get(0).getId()).isEqualTo(l1.id.longValue());
    assertThat(result.get(1).getId()).isEqualTo(l4.id.longValue());
  }


  @Test
  public void ordersByNbLapsAndThenLapEndThenLapStart() {
    LapTimeFilter testMe = new LapTimeFilter();
    long startForAll = System.currentTimeMillis();

    // Laps of pilot 1 - has more laps
    long start = startForAll;
    long end = start + 111;
    creator.nextPilot();
    LapTimeDTO l11 = creator.createDTOLapTime(start, end, 3);
    start = end;
    end = start + 111;
    LapTimeDTO l12 = creator.createDTOLapTime(start, end, 3);
    start = end;
    end = start + 111;
    LapTimeDTO l13 = creator.createDTOLapTime(start, end, 3);

    // Laps of pilot 2 - has first lap end
    start = startForAll;
    end = start + 112;
    creator.nextPilot();
    LapTimeDTO l21 = creator.createDTOLapTime(start, end, 2);
    start = end;
    end = start + 112;
    LapTimeDTO l22 = creator.createDTOLapTime(start, end, 2);

    // Laps of pilot 3 -
    start = startForAll;
    end = start + 113;
    creator.nextPilot();
    LapTimeDTO l31 = creator.createDTOLapTime(start, end, 2);
    start = end;
    end = start + 113;
    LapTimeDTO l32 = creator.createDTOLapTime(start, end, 2);

    // Laps of pilot 4 - has first lap start
    start = startForAll;
    end = start + 114;
    creator.nextPilot();
    LapTimeDTO l41 = creator.createDTOLapTime(start, -1, 2);
    start = end;
    end = start + 114;
    LapTimeDTO l42 = creator.createDTOLapTime(start, -1, 2);

    // Laps of pilot 5 -
    start = startForAll;
    end = start + 115;
    creator.nextPilot();
    LapTimeDTO l51 = creator.createDTOLapTime(start, -1, 2);
    start = end;
    end = start + 115;
    LapTimeDTO l52 = creator.createDTOLapTime(start, -1, 2);

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
    assertThat(result).hasSize(5);
    assertThat(result).contains(l13);
    assertThat(result).contains(l22);
    assertThat(result).contains(l32);
    assertThat(result).contains(l42);
    assertThat(result).contains(l52);

  }
}