package org.trd.app.teknichrono.model.manage;

import org.trd.app.teknichrono.model.dto.LapTimeDTO;
import org.trd.app.teknichrono.model.dto.NestedPilotDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LapTimeFiller {


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
