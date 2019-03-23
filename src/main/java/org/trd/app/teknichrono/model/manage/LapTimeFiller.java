package org.trd.app.teknichrono.model.manage;

import org.trd.app.teknichrono.model.compare.LapTimeDTORaceComparator;
import org.trd.app.teknichrono.model.dto.LapTimeDTO;
import org.trd.app.teknichrono.model.dto.NestedPilotDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LapTimeFiller {

  public void fillGaps(List<LapTimeDTO> results) {
    long best = -1;
    long previous = -1;
    for (LapTimeDTO lapTimeDTO : results) {
      if (best == -1) {
        best = lapTimeDTO.getDuration();
        previous = lapTimeDTO.getDuration();
        lapTimeDTO.setGapWithBest(0);
        lapTimeDTO.setGapWithPrevious(0);
      } else {
        long lapDuration = lapTimeDTO.getDuration();
        lapTimeDTO.setGapWithBest(lapDuration - best);
        lapTimeDTO.setGapWithPrevious(lapDuration - previous);
        previous = lapDuration;
      }
    }
  }

  public void fillRaceGaps(List<LapTimeDTO> results) {
    LapTimeDTORaceComparator comparator = new LapTimeDTORaceComparator();
    LapTimeDTO best = null;
    LapTimeDTO previous = null;
    for (LapTimeDTO lapTimeDTO : results) {
      if (best == null) {
        best = lapTimeDTO;
        previous = lapTimeDTO;
        lapTimeDTO.setGapWithBest(0);
        lapTimeDTO.setGapWithPrevious(0);
      } else {
        lapTimeDTO.setGapWithBest(comparator.distance(best, lapTimeDTO));
        lapTimeDTO.setGapWithPrevious(comparator.distance(previous, lapTimeDTO));
        previous = lapTimeDTO;
      }
    }
  }

  public void fillLapsNumber(List<LapTimeDTO> results) {
    Map<Integer, List<LapTimeDTO>> lapsPerPilot = new HashMap<Integer, List<LapTimeDTO>>();
    for (LapTimeDTO dto : results) {
      NestedPilotDTO pilot = dto.getPilot();
      List<LapTimeDTO> lapsOfPilot = lapsPerPilot.get(pilot.getId());
      if (lapsOfPilot == null || lapsOfPilot.isEmpty()) {
        lapsOfPilot = new ArrayList<>();
        lapsPerPilot.put(pilot.getId(), lapsOfPilot);
      }
      lapsOfPilot.add(dto);
    }
    for (Map.Entry<Integer, List<LapTimeDTO>> entry : lapsPerPilot.entrySet()) {
      int lapIndex = 1;
      for (LapTimeDTO lap : entry.getValue()) {
        lap.setLapIndex(lapIndex);
        lap.setLapNumber(entry.getValue().size());
        lapIndex++;
      }
    }
  }

  public void ensureAllPilotsPresent(List<LapTimeDTO> results, Set<NestedPilotDTO> mandatoryPilots) {
    if (mandatoryPilots != null && !mandatoryPilots.isEmpty()) {
      List<Integer> pilotsPresent = new ArrayList<>();
      for (LapTimeDTO lapTimeDTO : results) {
        pilotsPresent.add(lapTimeDTO.getPilot().getId());
      }
      for (NestedPilotDTO pilot : mandatoryPilots) {
        int id = pilot.getId();
        if (!pilotsPresent.contains(id)) {
          addEmptyLap(results, pilot);
        }
      }
    }
  }

  private void addEmptyLap(List<LapTimeDTO> results, NestedPilotDTO pilot) {
    LapTimeDTO dummyLap = new LapTimeDTO();
    dummyLap.setPilot(pilot);
    results.add(dummyLap);
  }
}
