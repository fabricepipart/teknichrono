package org.trd.app.teknichrono.business.view;

import org.trd.app.teknichrono.model.dto.LapTimeDTO;
import org.trd.app.teknichrono.model.dto.NestedPilotDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LapTimeFiller {


  public void fillLapsNumber(List<LapTimeDTO> results) {
    Map<Long, List<LapTimeDTO>> lapsPerPilot = new HashMap<>();
    for (LapTimeDTO dto : results) {
      NestedPilotDTO pilot = dto.getPilot();
      List<LapTimeDTO> lapsOfPilot = lapsPerPilot.get(pilot.getId());
      if (lapsOfPilot == null || lapsOfPilot.isEmpty()) {
        lapsOfPilot = new ArrayList<>();
        lapsPerPilot.put(pilot.getId(), lapsOfPilot);
      }
      lapsOfPilot.add(dto);
    }
    for (Map.Entry<Long, List<LapTimeDTO>> entry : lapsPerPilot.entrySet()) {
      long lapIndex = 1;
      for (LapTimeDTO lap : entry.getValue()) {
        lap.setLapIndex(lapIndex);
        lap.setLapNumber(entry.getValue().size());
        lapIndex++;
      }
    }
  }

  public void ensureAllPilotsPresent(List<LapTimeDTO> results, Set<NestedPilotDTO> mandatoryPilots) {
    if (mandatoryPilots != null && !mandatoryPilots.isEmpty()) {
      List<Long> pilotsPresent = new ArrayList<>();
      for (LapTimeDTO lapTimeDTO : results) {
        pilotsPresent.add(lapTimeDTO.getPilot().getId());
      }
      for (NestedPilotDTO pilot : mandatoryPilots) {
        long id = pilot.getId();
        if (!pilotsPresent.contains(id)) {
          addEmptyLap(results, pilot);
        }
      }
    }
  }

  private void addEmptyLap(List<LapTimeDTO> results, NestedPilotDTO pilot) {
    LapTimeDTO dummyLap = new LapTimeDTO();
    dummyLap.setPilot(pilot);
    dummyLap.setLapIndex(1);
    dummyLap.setLapNumber(1);
    results.add(dummyLap);
  }
}
