package org.trd.app.teknichrono.business;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jboss.logging.Logger;
import org.trd.app.teknichrono.model.LapTime;
import org.trd.app.teknichrono.rest.dto.LapTimeDTO;
import org.trd.app.teknichrono.rest.dto.NestedLocationDTO;
import org.trd.app.teknichrono.rest.dto.NestedPilotDTO;
import org.trd.app.teknichrono.rest.dto.NestedSessionDTO;

public class LapTimeManager {

  private static final int ACCEPTANCE_PERCENTAGE = 300;

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
      if (pilot != null) {
        LapTimeDTO lastPilotLap = null;
        List<LapTimeDTO> lapsOfPilot = lapsPerPilot.get(pilot.getId());
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
      }
      results.add(dto);
    }
    return results;
  }

  private void fillGaps(List<LapTimeDTO> results) {
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

  private void fillRaceGaps(List<LapTimeDTO> results) {
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

  void filterNoDuration(List<LapTimeDTO> results) {
    results.removeIf(r -> r.getDuration() <= 0);
  }

  void filterExtreme(List<LapTimeDTO> results) {
    // Needs to be done here since we did not have all info before
    Map<Integer, LapTimeDTO> bestPerLocation = new HashMap<Integer, LapTimeDTO>();
    for (LapTimeDTO dto : results) {
      NestedLocationDTO location = dto.getSession().getLocation();
      LapTimeDTO bestOfLocation = bestPerLocation.get(location.getId());
      if (dto.getDuration() > 0) {
        if (bestOfLocation == null || bestOfLocation.getDuration() > dto.getDuration()) {
          bestPerLocation.put(location.getId(), dto);
        }
      }
    }

    List<LapTimeDTO> toRemove = new ArrayList<>();
    for (LapTimeDTO lapTimeDTO : results) {
      NestedLocationDTO location = lapTimeDTO.getSession().getLocation();
      LapTimeDTO bestOfLocation = bestPerLocation.get(location.getId());
      if (bestOfLocation != null) {
        if (lapTimeDTO.getDuration() > (bestOfLocation.getDuration() * ACCEPTANCE_PERCENTAGE / 100)) {
          logger.info("Discarding lap ID " + lapTimeDTO.getId()
              + " since it is too long compared to the min for this location : " + lapTimeDTO.getDuration());
          toRemove.add(lapTimeDTO);
        }
      }
    }
    results.removeAll(toRemove);
  }

  private void fillLapsNumber(List<LapTimeDTO> results) {
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
    for (Entry<Integer, List<LapTimeDTO>> entry : lapsPerPilot.entrySet()) {
      int lapIndex = 1;
      for (LapTimeDTO lap : entry.getValue()) {
        lap.setLapIndex(lapIndex);
        lap.setLapNumber(entry.getValue().size());
        lapIndex++;
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

  public void arrangeDisplay(List<LapTimeDTO> results, LapTimeDisplay... displays) {
    arrangeDisplay(results, null, displays);
  }

  public void arrangeDisplay(List<LapTimeDTO> results, Set<NestedPilotDTO> pilots, LapTimeDisplay... displays) {
    filterExtreme(results);
    fillLapsNumber(results);
    for (LapTimeDisplay display : displays) {
      switch (display) {
      case KEEP_COMPLETE:
        filterNoDuration(results);
        // Adjust
        fillLapsNumber(results);
        break;
      case KEEP_LAST:
        // TODO Should probably be merged with Best (best and last info present
        // in DTO) and then its just a matter of order
        keepOnlyLast(results);
        break;
      case ORDER_FOR_RACE:
        orderForRace(results);
        fillRaceGaps(results);
        break;
      case KEEP_BEST:
        keepOnlyBest(results);
        break;
      case ORDER_BY_DURATION:
        orderByDuration(results);
        fillGaps(results);
        break;
      case ORDER_BY_DATE:
        orderByDate(results);
      default:
        break;
      }
    }
    ensureAllPilotsPresent(results, pilots);
  }

  private void orderByDate(List<LapTimeDTO> results) {
    results.sort(new LapTimeDTODateComparator());
  }

  private void ensureAllPilotsPresent(List<LapTimeDTO> results, Set<NestedPilotDTO> mandatoryPilots) {
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
