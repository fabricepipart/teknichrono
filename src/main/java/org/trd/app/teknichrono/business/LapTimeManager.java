package org.trd.app.teknichrono.business;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.trd.app.teknichrono.model.LapTime;
import org.trd.app.teknichrono.rest.dto.LapTimeDTO;
import org.trd.app.teknichrono.rest.dto.NestedPilotDTO;
import org.trd.app.teknichrono.rest.dto.NestedSessionDTO;

public class LapTimeManager {

  public List<LapTimeDTO> convert(List<LapTime> searchResults) {
    // First make sure they are in the start date order
    // System.out.println("Before : " + searchResults);
    List<LapTime> lapTimes = new ArrayList<LapTime>(searchResults);
    lapTimes.sort(new LapTimeStartComparator());
    // System.out.println("After : " + lapTimes);

    // Check if we are in a loop session
    // Keep a map of last pilot laps to set new laptime when next lap is reached
    Map<Integer, LapTimeDTO> lastLapPerPilot = new HashMap<Integer, LapTimeDTO>();
    final List<LapTimeDTO> results = new ArrayList<LapTimeDTO>();
    for (LapTime searchResult : lapTimes) {
      LapTimeDTO dto = new LapTimeDTO(searchResult);
      NestedSessionDTO session = dto.getSession();
      NestedPilotDTO pilot = dto.getPilot();
      LapTimeDTO lastPilotLap = lastLapPerPilot.get(pilot.getId());
      if (session.isLoopTrack() && lastPilotLap != null && lastPilotLap.getSession().getId() == session.getId()) {
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
    Map<Integer, LapTimeDTO> bests = new HashMap<>();
    List<LapTimeDTO> toRemove = new ArrayList<>();
    for (LapTimeDTO lapTimeDTO : results) {
      if (lapTimeDTO.getDuration() <= 0) {
        toRemove.add(lapTimeDTO);
        continue;
      }
      int pilotId = lapTimeDTO.getPilot().getId();
      LapTimeDTO pilotBest = bests.get(pilotId);
      if (pilotBest == null) {
        bests.put(pilotId, lapTimeDTO);
      } else {
        if (lapTimeDTO.getDuration() < pilotBest.getDuration()) {
          bests.put(pilotId, lapTimeDTO);
          toRemove.add(pilotBest);
        } else {
          toRemove.add(lapTimeDTO);
        }
      }
    }
    results.removeAll(toRemove);
  }

}
