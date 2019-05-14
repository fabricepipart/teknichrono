package org.trd.app.teknichrono.model.manage;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.trd.app.teknichrono.business.view.LapTimeConverter;
import org.trd.app.teknichrono.model.dto.LapTimeDTO;
import org.trd.app.teknichrono.model.dto.SectorDTO;
import org.trd.app.teknichrono.model.dto.TestLapTimeDTOCreator;
import org.trd.app.teknichrono.model.jpa.LapTime;
import org.trd.app.teknichrono.model.jpa.TestLapTimeCreator;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class TestLapTimeConverter {

  private TestLapTimeCreator creator;

  @Before
  public void setUp() throws Exception {
    creator = new TestLapTimeCreator();
  }

  @Test
  public void convertsEmptyListToEmptyList() {
    LapTimeConverter testMe = new LapTimeConverter();
    List<LapTimeDTO> result = testMe.convert(new ArrayList<LapTime>());
    org.junit.Assert.assertTrue(result.isEmpty());
  }

  @Test
  public void noneCreatedWithNegativeDuration() {
    TestLapTimeDTOCreator creator = new TestLapTimeDTOCreator();
    List<LapTimeDTO> lapsWithIntermediates = creator.createLapsWithIntermediates();
    for (LapTimeDTO lap : lapsWithIntermediates) {
      checkNoNegativeDuration(lap);
    }
  }

  private void checkNoNegativeDuration(LapTimeDTO lap) {
    Assert.assertTrue(lap.getDuration().compareTo(Duration.ZERO) >= 0);
    Assert.assertTrue(lap.getGapWithBest().compareTo(Duration.ZERO) >= 0);
    for (SectorDTO s : lap.getIntermediates()) {
//      Assert.assertTrue(s.getStart() >= 0); // FIXME
      Assert.assertTrue("This intermediate duration is negative", s.getDuration().compareTo(Duration.ZERO) >= 0);
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
    org.junit.Assert.assertEquals(1, result.get(0).getId());
    org.junit.Assert.assertEquals(2, result.get(1).getId());
    org.junit.Assert.assertEquals(3, result.get(2).getId());
    org.junit.Assert.assertEquals(4, result.get(3).getId());
    org.junit.Assert.assertEquals(5, result.get(4).getId());
    org.junit.Assert.assertEquals(6, result.get(5).getId());
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
    org.junit.Assert.assertEquals(1, result.get(0).getId());
    org.junit.Assert.assertEquals(2, result.get(1).getId());
    org.junit.Assert.assertEquals(3, result.get(2).getId());
    org.junit.Assert.assertEquals(4, result.get(3).getId());
    org.junit.Assert.assertEquals(5, result.get(4).getId());
    org.junit.Assert.assertEquals(6, result.get(5).getId());
  }

}