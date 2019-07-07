package org.trd.app.teknichrono.model.manage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.trd.app.teknichrono.business.view.LapTimeFiller;
import org.trd.app.teknichrono.model.dto.LapTimeDTO;
import org.trd.app.teknichrono.model.dto.LapTimeDTOCreatorForTests;
import org.trd.app.teknichrono.model.dto.NestedPilotDTO;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

public class TestLapTimeFiller {
  private LapTimeDTOCreatorForTests creator;
  private LapTimeFiller filler;

  @BeforeEach
  public void setUp() {
    creator = new LapTimeDTOCreatorForTests();
    filler = new LapTimeFiller();
  }

  @Test
  public void addsLapNumbers() {
    List<LapTimeDTO> laps = creator.createLaps();
    filler.fillLapsNumber(laps);
    assertThat(laps.size()).isEqualTo(30);
    assertThat(30).isEqualTo(laps.size());

    assertThat(3).isEqualTo(laps.get(0).getLapNumber());
    assertThat(3).isEqualTo(laps.get(1).getLapNumber());
    assertThat(3).isEqualTo(laps.get(2).getLapNumber());

    assertThat(6).isEqualTo(laps.get(3).getLapNumber());
    assertThat(6).isEqualTo(laps.get(4).getLapNumber());
    assertThat(6).isEqualTo(laps.get(5).getLapNumber());
    assertThat(6).isEqualTo(laps.get(6).getLapNumber());
    assertThat(6).isEqualTo(laps.get(7).getLapNumber());
    assertThat(6).isEqualTo(laps.get(8).getLapNumber());


    assertThat(3).isEqualTo(laps.get(9).getLapNumber());
    assertThat(3).isEqualTo(laps.get(10).getLapNumber());
    assertThat(3).isEqualTo(laps.get(1).getLapNumber());

    assertThat(2).isEqualTo(laps.get(12).getLapNumber());
    assertThat(2).isEqualTo(laps.get(13).getLapNumber());

    assertThat(3).isEqualTo(laps.get(14).getLapNumber());
    assertThat(3).isEqualTo(laps.get(15).getLapNumber());
    assertThat(3).isEqualTo(laps.get(16).getLapNumber());


    assertThat(6).isEqualTo(laps.get(17).getLapNumber());
    assertThat(6).isEqualTo(laps.get(18).getLapNumber());
    assertThat(6).isEqualTo(laps.get(19).getLapNumber());
    assertThat(6).isEqualTo(laps.get(20).getLapNumber());
    assertThat(6).isEqualTo(laps.get(21).getLapNumber());
    assertThat(6).isEqualTo(laps.get(22).getLapNumber());


    assertThat(3).isEqualTo(laps.get(23).getLapNumber());
    assertThat(3).isEqualTo(laps.get(24).getLapNumber());
    assertThat(3).isEqualTo(laps.get(25).getLapNumber());

    assertThat(2).isEqualTo(laps.get(26).getLapNumber());
    assertThat(2).isEqualTo(laps.get(27).getLapNumber());

    assertThat(1).isEqualTo(laps.get(28).getLapNumber());

    assertThat(1).isEqualTo(laps.get(29).getLapNumber());
  }

  @Test
  public void addsLapIndexesInOrderProvided() {
    List<LapTimeDTO> laps = creator.createLaps();
    filler.fillLapsNumber(laps);
    assertThat(30).isEqualTo(laps.size());

    assertThat(1).isEqualTo(laps.get(0).getLapIndex());
    assertThat(2).isEqualTo(laps.get(1).getLapIndex());
    assertThat(3).isEqualTo(laps.get(2).getLapIndex());

    assertThat(1).isEqualTo(laps.get(3).getLapIndex());
    assertThat(2).isEqualTo(laps.get(4).getLapIndex());
    assertThat(3).isEqualTo(laps.get(5).getLapIndex());
    assertThat(4).isEqualTo(laps.get(6).getLapIndex());
    assertThat(5).isEqualTo(laps.get(7).getLapIndex());
    assertThat(6).isEqualTo(laps.get(8).getLapIndex());


    assertThat(1).isEqualTo(laps.get(9).getLapIndex());
    assertThat(2).isEqualTo(laps.get(10).getLapIndex());
    assertThat(3).isEqualTo(laps.get(11).getLapIndex());

    assertThat(1).isEqualTo(laps.get(12).getLapIndex());
    assertThat(2).isEqualTo(laps.get(13).getLapIndex());

    assertThat(1).isEqualTo(laps.get(14).getLapIndex());
    assertThat(2).isEqualTo(laps.get(15).getLapIndex());
    assertThat(3).isEqualTo(laps.get(16).getLapIndex());


    assertThat(1).isEqualTo(laps.get(17).getLapIndex());
    assertThat(2).isEqualTo(laps.get(18).getLapIndex());
    assertThat(3).isEqualTo(laps.get(19).getLapIndex());
    assertThat(4).isEqualTo(laps.get(20).getLapIndex());
    assertThat(5).isEqualTo(laps.get(21).getLapIndex());
    assertThat(6).isEqualTo(laps.get(22).getLapIndex());


    assertThat(1).isEqualTo(laps.get(23).getLapIndex());
    assertThat(2).isEqualTo(laps.get(24).getLapIndex());
    assertThat(3).isEqualTo(laps.get(25).getLapIndex());

    assertThat(1).isEqualTo(laps.get(26).getLapIndex());
    assertThat(2).isEqualTo(laps.get(27).getLapIndex());

    assertThat(1).isEqualTo(laps.get(28).getLapIndex());

    assertThat(1).isEqualTo(laps.get(29).getLapIndex());
  }

  @Test
  public void addsEmptyLapsForPilotsNotPresent() {
    List<LapTimeDTO> laps = creator.createLaps();
    Set<NestedPilotDTO> pilots = new HashSet<>();
    NestedPilotDTO pilot = new NestedPilotDTO();
    pilot.setId(999L);
    pilots.add(pilot);
    filler.ensureAllPilotsPresent(laps, pilots);
    assertThat(31).isEqualTo(laps.size());
    laps.removeIf(l -> l.getPilot().getId() != 999L);
    assertThat(1).isEqualTo(laps.size());
  }

  @Test
  public void addsNoLapsIfNoPilotPassed() {
    List<LapTimeDTO> laps = creator.createLaps();
    Set<NestedPilotDTO> pilots = new HashSet<>();
    filler.ensureAllPilotsPresent(laps, pilots);
    assertThat(30).isEqualTo(laps.size());
  }

}