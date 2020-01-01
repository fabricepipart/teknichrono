package org.trd.app.teknichrono.business.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.trd.app.teknichrono.model.dto.LapTimeDTO;
import org.trd.app.teknichrono.model.dto.LapTimeDTOCreatorForTests;
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
    this.creator = new LapTimeDTOCreatorForTests();
    this.laptimeCreator = new LapTimeCreatorForTests();
  }

  @Test
  public void filtersLapsThatAreTooLongComparedToMedian() {
    long now = System.currentTimeMillis();
    LapTimeDTOCreatorForTests dtoCreator = new LapTimeDTOCreatorForTests();
    List<LapTimeDTO> laps = new ArrayList<>();
    dtoCreator.nextPilot();
    laps.add(dtoCreator.createDTOLapTimeWithSession(now, now + 1000, 0));
    laps.add(dtoCreator.createDTOLapTimeWithSession(now + 1000, now + 2101, 0));
    laps.add(dtoCreator.createDTOLapTimeWithSession(now + 2100, now + 3301, 0));
    laps.add(dtoCreator.createDTOLapTimeWithSession(now + 3301, now + 5100, 0));
    laps.add(dtoCreator.createDTOLapTimeWithSession(now + 5100, now + 8301, 0));
    laps.add(dtoCreator.createDTOLapTimeWithSession(now + 9100, now + 23301, 0));
    laps.add(dtoCreator.createDTOLapTimeWithSession(now + 23301, now + 25301, 0));
    assertThat(laps).hasSize(7);

    LapTimeFilter testMe = new LapTimeFilter();
    testMe.filterExtreme(laps);
    assertThat(laps).hasSize(6);
  }

  @Test
  public void filteringExtremesSupportsLapsWithoutDuration() {
    long now = System.currentTimeMillis();
    LapTimeDTOCreatorForTests dtoCreator = new LapTimeDTOCreatorForTests();
    List<LapTimeDTO> laps = new ArrayList<>();
    dtoCreator.nextPilot();
    laps.add(dtoCreator.createDTOLapTimeWithSession(now, now + 1000, 0));
    laps.add(dtoCreator.createDTOLapTimeWithSession(now + 1000, now + 2101, 0));
    laps.add(dtoCreator.createDTOLapTimeWithSession(now + 2100, now + 3301, 0));
    laps.add(dtoCreator.createDTOLapTimeWithSession(now + 3301, now + 5100, 0));
    laps.add(dtoCreator.createDTOLapTimeWithSession(now + 5100, now + 8301, 0));
    laps.add(dtoCreator.createDTOLapTimeWithSession(now + 9100, now + 10000, 0));
    laps.add(dtoCreator.createDTOLapTimeWithSession(now + 10000, now + 10000, 0));
    assertThat(laps).hasSize(7);
    laps.get(0).setDuration(null);

    LapTimeFilter testMe = new LapTimeFilter();
    testMe.filterExtreme(laps);
    assertThat(laps).hasSize(7);
  }

  @Test
  public void filtersLapsThatAreTooLongComparedToSpecifiedRange() {
    long now = System.currentTimeMillis();
    LapTimeDTOCreatorForTests dtoCreator = new LapTimeDTOCreatorForTests();
    dtoCreator.getSession().getLocation().setMinimum(Duration.ofMillis(500));
    dtoCreator.getSession().getLocation().setMaximum(Duration.ofMillis(1500));
    List<LapTimeDTO> laps = new ArrayList<>();
    dtoCreator.nextPilot();
    laps.add(dtoCreator.createDTOLapTimeWithSession(now, now + 1000, 0));
    laps.add(dtoCreator.createDTOLapTimeWithSession(now + 1000, now + 2000, 0));
    laps.add(dtoCreator.createDTOLapTimeWithSession(now + 2000, now + 3000, 0));
    laps.add(dtoCreator.createDTOLapTimeWithSession(now + 3000, now + 5000, 0));
    laps.add(dtoCreator.createDTOLapTimeWithSession(now + 5000, now + 6500, 0));
    laps.add(dtoCreator.createDTOLapTimeWithSession(now + 6500, now + 8500, 0));
    laps.add(dtoCreator.createDTOLapTimeWithSession(now + 8500, now + 9999, 0));
    assertThat(laps).hasSize(7);

    LapTimeFilter testMe = new LapTimeFilter();
    testMe.filterExtreme(laps);
    assertThat(laps).hasSize(5);
  }

  @Test
  public void filterDoesNotTakeINtoAccountMinIfNoMax() {
    long now = System.currentTimeMillis();
    LapTimeDTOCreatorForTests dtoCreator = new LapTimeDTOCreatorForTests();
    dtoCreator.getSession().getLocation().setMinimum(Duration.ofMillis(800));
    List<LapTimeDTO> laps = new ArrayList<>();
    dtoCreator.nextPilot();
    laps.add(dtoCreator.createDTOLapTimeWithSession(now, now + 1000, 0));
    laps.add(dtoCreator.createDTOLapTimeWithSession(now + 1000, now + 2000, 0));
    laps.add(dtoCreator.createDTOLapTimeWithSession(now + 2000, now + 2500, 0));
    laps.add(dtoCreator.createDTOLapTimeWithSession(now + 2500, now + 3000, 0));
    assertThat(laps).hasSize(4);

    LapTimeFilter testMe = new LapTimeFilter();
    testMe.filterExtreme(laps);
    assertThat(laps).hasSize(4);
  }

  @Test
  public void filterDoesNotTakeIntoAccountMaxIfNoMin() {
    long now = System.currentTimeMillis();
    LapTimeDTOCreatorForTests dtoCreator = new LapTimeDTOCreatorForTests();
    dtoCreator.getSession().getLocation().setMaximum(Duration.ofMillis(1800));
    List<LapTimeDTO> laps = new ArrayList<>();
    dtoCreator.nextPilot();
    laps.add(dtoCreator.createDTOLapTimeWithSession(now, now + 1000, 0));
    laps.add(dtoCreator.createDTOLapTimeWithSession(now + 1000, now + 3000, 0));
    laps.add(dtoCreator.createDTOLapTimeWithSession(now + 2000, now + 2500, 0));
    laps.add(dtoCreator.createDTOLapTimeWithSession(now + 2500, now + 3000, 0));
    assertThat(laps).hasSize(4);

    LapTimeFilter testMe = new LapTimeFilter();
    testMe.filterExtreme(laps);
    assertThat(laps).hasSize(4);
  }

  @Test
  public void filterLapsWithoutDuration() {
    LapTimeFilter testMe = new LapTimeFilter();
    long time = System.currentTimeMillis();
    List<LapTimeDTO> result = new ArrayList<>();
    this.creator.nextPilot();
    result.add(this.creator.createDTOLapTime(time, time + 100000, 1));
    result.add(this.creator.createDTOLapTime(time, time + 100, 2));
    result.add(this.creator.createDTOLapTime(time, time + 1, 3));
    this.creator.nextPilot();
    result.add(this.creator.createDTOLapTime(time, time + 200, 1));
    this.creator.nextPilot();
    result.add(this.creator.createDTOLapTime(time, time + 0, 1));
    this.creator.nextPilot();
    result.add(this.creator.createDTOLapTime(time, time + 400, 1));
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
    this.creator.nextPilot();
    result.add(this.creator.createDTOLapTimeWithSession(time, time + 100, 1));
    result.add(this.creator.createDTOLapTimeWithSession(time, time + 100, 2));
    result.add(this.creator.createDTOLapTimeWithSession(time, time + 100, 3));
    this.creator.nextPilot();
    result.add(this.creator.createDTOLapTimeWithSession(time, time + 200, 1));
    this.creator.nextPilot();
    result.add(this.creator.createDTOLapTimeWithSession(time, time + 300, 1));
    this.creator.nextPilot();
    result.add(this.creator.createDTOLapTimeWithSession(time, time + 400, 1));
    // Average should be 200
    testMe.filterExtreme(result);
    assertThat(result).hasSize(6);
    for (int i = 7; i < 100; i++) {
      this.creator.nextPilot();
      result.add(this.creator.createDTOLapTimeWithSession(time, 0, 1));
    }
    testMe.filterExtreme(result);
    assertThat(result).hasSize(99);
  }

  @Test
  public void convertsAndFilters() {
    LapTimeFilter testMe = new LapTimeFilter();
    ArrayList<LapTime> searchResults = new ArrayList<>();
    long time = System.currentTimeMillis();
    LapTime l1 = this.laptimeCreator.createLapTimeWithNoIntermediate(1, true, time += 101);
    LapTime l2 = this.laptimeCreator.createLapTimeWithNoIntermediate(2, true, time += 102);
    LapTime l3 = this.laptimeCreator.createLapTimeWithNoIntermediate(3, true, time += 103); // long
    LapTime l4 = this.laptimeCreator.createLapTimeWithNoIntermediate(4, true, time += 1502);
    LapTime l5 = this.laptimeCreator.createLapTimeWithNoIntermediate(5, true, time += 98);
    LapTime l6 = this.laptimeCreator.createLapTimeWithNoIntermediate(6, true, time += 99);
    LapTime l7 = this.laptimeCreator.createLapTimeWithNoIntermediate(1, true, time += 104);
    LapTime l8 = this.laptimeCreator.createLapTimeWithNoIntermediate(2, true, time += 101); // short
    LapTime l9 = this.laptimeCreator.createLapTimeWithNoIntermediate(3, true, time += 10); // not finished
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
    ArrayList<LapTime> searchResults = new ArrayList<>();
    Instant time = Instant.now();

    LapTime l1 = this.laptimeCreator.createLapTimeWithPilot(1, 1, true, time);
    time = time.plus(Duration.ofMillis(111));
    LapTime l3 = this.laptimeCreator.createLapTimeWithPilot(3, 1, true, time);
    time = time.plus(Duration.ofMillis(133));
    LapTime l5 = this.laptimeCreator.createLapTimeWithPilot(5, 1, true, time);
    time = time.plus(Duration.ofMillis(155));
    LapTime l2 = this.laptimeCreator.createLapTimeWithPilot(2, 2, true, time);
    time = time.plus(Duration.ofMillis(162));
    LapTime l4 = this.laptimeCreator.createLapTimeWithPilot(4, 2, true, time);
    time = time.plus(Duration.ofMillis(124));
    LapTime l6 = this.laptimeCreator.createLapTimeWithPilot(6, 2, true, time);
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
    this.creator.nextPilot();
    LapTimeDTO l11 = this.creator.createDTOLapTime(start, end, 3);
    start = end;
    end = start + 111;
    LapTimeDTO l12 = this.creator.createDTOLapTime(start, end, 3);
    start = end;
    end = start + 111;
    LapTimeDTO l13 = this.creator.createDTOLapTime(start, end, 3);

    // Laps of pilot 2 - has first lap end
    start = startForAll;
    end = start + 112;
    this.creator.nextPilot();
    LapTimeDTO l21 = this.creator.createDTOLapTime(start, end, 2);
    start = end;
    end = start + 112;
    LapTimeDTO l22 = this.creator.createDTOLapTime(start, end, 2);

    // Laps of pilot 3 -
    start = startForAll;
    end = start + 113;
    this.creator.nextPilot();
    LapTimeDTO l31 = this.creator.createDTOLapTime(start, end, 2);
    start = end;
    end = start + 113;
    LapTimeDTO l32 = this.creator.createDTOLapTime(start, end, 2);

    // Laps of pilot 4 - has first lap start
    start = startForAll;
    end = start + 114;
    this.creator.nextPilot();
    LapTimeDTO l41 = this.creator.createDTOLapTime(start, -1, 2);
    start = end;
    end = start + 114;
    LapTimeDTO l42 = this.creator.createDTOLapTime(start, -1, 2);

    // Laps of pilot 5 -
    start = startForAll;
    end = start + 115;
    this.creator.nextPilot();
    LapTimeDTO l51 = this.creator.createDTOLapTime(start, -1, 2);
    start = end;
    end = start + 115;
    LapTimeDTO l52 = this.creator.createDTOLapTime(start, -1, 2);

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