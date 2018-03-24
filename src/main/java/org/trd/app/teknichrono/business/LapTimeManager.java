package org.trd.app.teknichrono.business;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jboss.logging.Logger;
import org.trd.app.teknichrono.model.LapTime;
import org.trd.app.teknichrono.rest.dto.LapTimeDTO;
import org.trd.app.teknichrono.rest.dto.NestedPilotDTO;
import org.trd.app.teknichrono.rest.dto.NestedSessionDTO;

public class LapTimeManager {

  private Logger logger = Logger.getLogger(LapTimeManager.class);

  public List<LapTimeDTO> convert(List<LapTime> searchResults) {
    // First make sure they are in the start date order
    logger.debug("Before : " + searchResults);
    List<LapTime> lapTimes = new ArrayList<LapTime>(searchResults);
    lapTimes.sort(new LapTimeStartComparator());
    logger.debug("After : " + lapTimes);

    // Check if we are in a loop session
    // Keep a map of last pilot laps to set new laptime when next lap is reached
    Map<Integer, List<LapTimeDTO>> lapsPerPilot = new HashMap<Integer, List<LapTimeDTO>>();
    final List<LapTimeDTO> results = new ArrayList<LapTimeDTO>();
    for (LapTime searchResult : lapTimes) {
      LapTimeDTO dto = new LapTimeDTO(searchResult);
      NestedSessionDTO session = dto.getSession();
      NestedPilotDTO pilot = dto.getPilot();
      List<LapTimeDTO> lapsOfPilot = lapsPerPilot.get(pilot.getId());
      LapTimeDTO lastPilotLap = null;
      if (lapsOfPilot != null && lapsOfPilot.size() > 0) {
        lastPilotLap = lapsOfPilot.get(lapsOfPilot.size() - 1);
      } else {
        lapsOfPilot = new ArrayList<>();
        lapsPerPilot.put(pilot.getId(), lapsOfPilot);
      }
      if (session.isLoopTrack() && lastPilotLap != null && lastPilotLap.getSession().getId() == session.getId()) {
        Timestamp startDate = dto.getStartDate();
        if (startDate != null && startDate.getTime() > 0) {
          logger.debug("Add last sector to lap " + lastPilotLap + " because we now found " + dto);
          lastPilotLap.addLastSector(startDate);
        }
      }
      lapsOfPilot.add(dto);
      dto.setLapIndex(lapsOfPilot.size());
      results.add(dto);
    }
    fillLapsNumber(lapsPerPilot);
    return results;
  }

  private void fillLapsNumber(Map<Integer, List<LapTimeDTO>> lapsPerPilot) {
    for (Entry<Integer, List<LapTimeDTO>> entry : lapsPerPilot.entrySet()) {
      for (LapTimeDTO lap : entry.getValue()) {
        lap.setLapNumber(entry.getValue().size());
      }
    }
  }

  public void orderByDuration(List<LapTimeDTO> results) {
    results.sort(new LapTimeDTOComparator());
  }

  public void orderForRace(List<LapTimeDTO> results) {
    results.sort(new LapTimeDTORaceComparator());
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

  public void keepOnlyLast(List<LapTimeDTO> results) {
    Map<Integer, LapTimeDTO> lasts = new HashMap<>();
    List<LapTimeDTO> toRemove = new ArrayList<>();
    for (LapTimeDTO lapTimeDTO : results) {
      int pilotId = lapTimeDTO.getPilot().getId();
      LapTimeDTO pilotLast = lasts.get(pilotId);
      if (pilotLast != null) {
        toRemove.add(pilotLast);
      }
      lasts.put(pilotId, lapTimeDTO);
    }
    results.removeAll(toRemove);
  }

  public void arrangeDisplay(LapTimeDisplay display, List<LapTimeDTO> results) {
    switch (display) {
    case RACE:
      // In case of race, order by nb laps and then last lap start
      keepOnlyLast(results);
      orderForRace(results);
      break;
    case BEST:
      keepOnlyBest(results);
      orderByDuration(results);
      break;
    case TIMESHEET:
    default:
      // Order by startDate
      break;
    }
  }

}
