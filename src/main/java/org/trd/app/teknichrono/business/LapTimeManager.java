package org.trd.app.teknichrono.business;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.trd.app.teknichrono.model.LapTime;
import org.trd.app.teknichrono.rest.dto.LapTimeDTO;
import org.trd.app.teknichrono.rest.dto.NestedEventDTO;
import org.trd.app.teknichrono.rest.dto.NestedPilotDTO;

public class LapTimeManager {

  public List<LapTimeDTO> convert(List<LapTime> searchResults) {
    // First make sure they are in the start date order
    // System.out.println("Before : " + searchResults);
    List<LapTime> lapTimes = new ArrayList<LapTime>(searchResults);
    lapTimes.sort(new LapTimeStartComparator());
    // System.out.println("After : " + lapTimes);

    // Check if we are in a loop event
    // Keep a map of last pilot laps to set new laptime when next lap is reached
    Map<Integer, LapTimeDTO> lastLapPerPilot = new HashMap<Integer, LapTimeDTO>();
    final List<LapTimeDTO> results = new ArrayList<LapTimeDTO>();
    for (LapTime searchResult : lapTimes) {
      LapTimeDTO dto = new LapTimeDTO(searchResult);
      NestedEventDTO event = dto.getEvent();
      NestedPilotDTO pilot = dto.getPilot();
      LapTimeDTO lastPilotLap = lastLapPerPilot.get(pilot.getId());
      if (event.isLoopTrack() && lastPilotLap != null && lastPilotLap.getEvent().getId() == event.getId()) {
        Timestamp startDate = dto.getStartDate();
        if (startDate != null && startDate.getTime() > 0) {
          // System.out.println("Add last sector to lap " + lastPilotLap + "
          // because we now found " + dto);
          lastPilotLap.addLastSector(startDate);
        }
      }
      lastLapPerPilot.put(dto.getPilot().getId(), dto);
      results.add(dto);
    }
    return results;
  }

  public void orderByDuration(List<LapTimeDTO> results) {
    results.sort(new LapTimeDTOComparator());
  }

  public void keepOnlyBest(List<LapTimeDTO> results) {
    // results.removeIf(filter)

  }

}
