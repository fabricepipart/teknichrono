package org.trd.app.teknichrono.model.manage;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.trd.app.teknichrono.model.dto.LapTimeDTO;
import org.trd.app.teknichrono.model.dto.NestedPilotDTO;
import org.trd.app.teknichrono.model.dto.TestLapTimeDTOCreator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LapTimeFillerTest {
  private TestLapTimeDTOCreator creator;
  private LapTimeFiller filler;

  @Before
  public void setUp() throws Exception {
    creator = new TestLapTimeDTOCreator();
    filler = new LapTimeFiller();
  }

  @Test
  public void addsLapNumbers() {
    List<LapTimeDTO> laps = creator.createLaps();
    filler.fillLapsNumber(laps);
    Assert.assertEquals(30, laps.size());

    Assert.assertEquals(3, laps.get(0).getLapNumber());
    Assert.assertEquals(3, laps.get(1).getLapNumber());
    Assert.assertEquals(3, laps.get(2).getLapNumber());

    Assert.assertEquals(6, laps.get(3).getLapNumber());
    Assert.assertEquals(6, laps.get(4).getLapNumber());
    Assert.assertEquals(6, laps.get(5).getLapNumber());
    Assert.assertEquals(6, laps.get(6).getLapNumber());
    Assert.assertEquals(6, laps.get(7).getLapNumber());
    Assert.assertEquals(6, laps.get(8).getLapNumber());


    Assert.assertEquals(3, laps.get(9).getLapNumber());
    Assert.assertEquals(3, laps.get(10).getLapNumber());
    Assert.assertEquals(3, laps.get(1).getLapNumber());

    Assert.assertEquals(2, laps.get(12).getLapNumber());
    Assert.assertEquals(2, laps.get(13).getLapNumber());

    Assert.assertEquals(3, laps.get(14).getLapNumber());
    Assert.assertEquals(3, laps.get(15).getLapNumber());
    Assert.assertEquals(3, laps.get(16).getLapNumber());


    Assert.assertEquals(6, laps.get(17).getLapNumber());
    Assert.assertEquals(6, laps.get(18).getLapNumber());
    Assert.assertEquals(6, laps.get(19).getLapNumber());
    Assert.assertEquals(6, laps.get(20).getLapNumber());
    Assert.assertEquals(6, laps.get(21).getLapNumber());
    Assert.assertEquals(6, laps.get(22).getLapNumber());


    Assert.assertEquals(3, laps.get(23).getLapNumber());
    Assert.assertEquals(3, laps.get(24).getLapNumber());
    Assert.assertEquals(3, laps.get(25).getLapNumber());

    Assert.assertEquals(2, laps.get(26).getLapNumber());
    Assert.assertEquals(2, laps.get(27).getLapNumber());

    Assert.assertEquals(1, laps.get(28).getLapNumber());

    Assert.assertEquals(1, laps.get(29).getLapNumber());
  }

  @Test
  public void addsLapIndexesInOrderProvided() {
    List<LapTimeDTO> laps = creator.createLaps();
    filler.fillLapsNumber(laps);
    Assert.assertEquals(30, laps.size());

    Assert.assertEquals(1, laps.get(0).getLapIndex());
    Assert.assertEquals(2, laps.get(1).getLapIndex());
    Assert.assertEquals(3, laps.get(2).getLapIndex());

    Assert.assertEquals(1, laps.get(3).getLapIndex());
    Assert.assertEquals(2, laps.get(4).getLapIndex());
    Assert.assertEquals(3, laps.get(5).getLapIndex());
    Assert.assertEquals(4, laps.get(6).getLapIndex());
    Assert.assertEquals(5, laps.get(7).getLapIndex());
    Assert.assertEquals(6, laps.get(8).getLapIndex());


    Assert.assertEquals(1, laps.get(9).getLapIndex());
    Assert.assertEquals(2, laps.get(10).getLapIndex());
    Assert.assertEquals(3, laps.get(11).getLapIndex());

    Assert.assertEquals(1, laps.get(12).getLapIndex());
    Assert.assertEquals(2, laps.get(13).getLapIndex());

    Assert.assertEquals(1, laps.get(14).getLapIndex());
    Assert.assertEquals(2, laps.get(15).getLapIndex());
    Assert.assertEquals(3, laps.get(16).getLapIndex());


    Assert.assertEquals(1, laps.get(17).getLapIndex());
    Assert.assertEquals(2, laps.get(18).getLapIndex());
    Assert.assertEquals(3, laps.get(19).getLapIndex());
    Assert.assertEquals(4, laps.get(20).getLapIndex());
    Assert.assertEquals(5, laps.get(21).getLapIndex());
    Assert.assertEquals(6, laps.get(22).getLapIndex());


    Assert.assertEquals(1, laps.get(23).getLapIndex());
    Assert.assertEquals(2, laps.get(24).getLapIndex());
    Assert.assertEquals(3, laps.get(25).getLapIndex());

    Assert.assertEquals(1, laps.get(26).getLapIndex());
    Assert.assertEquals(2, laps.get(27).getLapIndex());

    Assert.assertEquals(1, laps.get(28).getLapIndex());

    Assert.assertEquals(1, laps.get(29).getLapIndex());
  }

  @Test
  public void addsEmptyLapsForPilotsNotPresent() {
    List<LapTimeDTO> laps = creator.createLaps();
    Set<NestedPilotDTO> pilots = new HashSet<>();
    NestedPilotDTO pilot = new NestedPilotDTO();
    pilot.setId(999);
    pilots.add(pilot);
    filler.ensureAllPilotsPresent(laps, pilots);
    Assert.assertEquals(31, laps.size());
    laps.removeIf(l -> l.getPilot().getId() != 999);
    Assert.assertEquals(1, laps.size());
  }

  @Test
  public void addsNoLapsIfNoPilotPassed() {
    List<LapTimeDTO> laps = creator.createLaps();
    Set<NestedPilotDTO> pilots = new HashSet<>();
    filler.ensureAllPilotsPresent(laps, pilots);
    Assert.assertEquals(30, laps.size());
  }

}