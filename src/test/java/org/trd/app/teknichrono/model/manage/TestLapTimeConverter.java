package org.trd.app.teknichrono.model.manage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.trd.app.teknichrono.business.view.LapTimeConverter;
import org.trd.app.teknichrono.model.dto.LapTimeDTO;
import org.trd.app.teknichrono.model.dto.LapTimeDTOCreatorForTests;
import org.trd.app.teknichrono.model.dto.SectorDTO;
import org.trd.app.teknichrono.model.jpa.LapTime;
import org.trd.app.teknichrono.model.jpa.LapTimeCreatorForTests;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class TestLapTimeConverter {

  private LapTimeCreatorForTests creator;

  @BeforeEach
  public void setUp() {
    creator = new LapTimeCreatorForTests();
  }

  @Test
  public void convertsEmptyListToEmptyList() {
    LapTimeConverter testMe = new LapTimeConverter();
    List<LapTimeDTO> result = testMe.convert(new ArrayList<LapTime>());
    assertThat(result.isEmpty()).isTrue();
  }

  @Test
  public void noneCreatedWithNegativeDuration() {
    LapTimeDTOCreatorForTests creator = new LapTimeDTOCreatorForTests();
    List<LapTimeDTO> lapsWithIntermediates = creator.createLapsWithIntermediates();
    for (LapTimeDTO lap : lapsWithIntermediates) {
      checkNoNegativeDuration(lap);
    }
  }

  private void checkNoNegativeDuration(LapTimeDTO lap) {
    if (lap.getDuration() != null) {
      assertThat(lap.getDuration()).isGreaterThan(Duration.ZERO);
    }
    if (lap.getGapWithBest() != null) {
      assertThat(lap.getGapWithBest()).isGreaterThanOrEqualTo(Duration.ZERO);
    }
    for (SectorDTO s : lap.getIntermediates()) {
      if (s.getDuration() != null) {
//      Assert.assertTrue(s.getStart() >= 0); // FIXME
        assertThat(s.getDuration()).isGreaterThanOrEqualTo(Duration.ZERO);
      }
    }
  }

  @Test
  public void convertsAndOrderByStartTime() {
    LapTimeConverter testMe = new LapTimeConverter();
    ArrayList<LapTime> searchResults = new ArrayList<LapTime>();
    LapTime l1 = creator.createLapTimeWithIntermediates(1, false);
    LapTime l2 = creator.createLapTimeWithIntermediates(2, false);
    LapTime l3 = creator.createLapTimeWithIntermediates(3, false);
    LapTime l4 = creator.createLapTimeWithIntermediates(4, false);
    LapTime l5 = creator.createLapTimeWithIntermediates(5, false);
    LapTime l6 = creator.createLapTimeWithIntermediates(6, false);
    searchResults.add(l4);
    searchResults.add(l1);
    searchResults.add(l6);
    searchResults.add(l5);
    searchResults.add(l2);
    searchResults.add(l3);
    List<LapTimeDTO> result = testMe.convert(searchResults);
    assertThat(result.get(0).getId()).isEqualTo(1);
    assertThat(result.get(1).getId()).isEqualTo(2);
    assertThat(result.get(2).getId()).isEqualTo(3);
    assertThat(result.get(3).getId()).isEqualTo(4);
    assertThat(result.get(4).getId()).isEqualTo(5);
    assertThat(result.get(5).getId()).isEqualTo(6);
  }

  @Test
  public void convertsAndOrderByStartTimeForLoopTrack() {
    LapTimeConverter testMe = new LapTimeConverter();
    ArrayList<LapTime> searchResults = new ArrayList<LapTime>();
    LapTime l1 = creator.createLapTimeWithIntermediates(1, true);
    LapTime l2 = creator.createLapTimeWithIntermediates(2, true);
    LapTime l3 = creator.createLapTimeWithIntermediates(3, true);
    LapTime l4 = creator.createLapTimeWithIntermediates(4, true);
    LapTime l5 = creator.createLapTimeWithIntermediates(5, true);
    LapTime l6 = creator.createLapTimeWithIntermediates(6, true);
    searchResults.add(l4);
    searchResults.add(l1);
    searchResults.add(l6);
    searchResults.add(l5);
    searchResults.add(l2);
    searchResults.add(l3);
    List<LapTimeDTO> result = testMe.convert(searchResults);
    assertThat(result.get(0).getId()).isEqualTo(1);
    assertThat(result.get(1).getId()).isEqualTo(2);
    assertThat(result.get(2).getId()).isEqualTo(3);
    assertThat(result.get(3).getId()).isEqualTo(4);
    assertThat(result.get(4).getId()).isEqualTo(5);
    assertThat(result.get(5).getId()).isEqualTo(6);
  }

}