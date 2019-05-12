package org.trd.app.teknichrono.business.view;

import org.jboss.logging.Logger;
import org.trd.app.teknichrono.model.dto.LapTimeDTO;
import org.trd.app.teknichrono.model.dto.NestedLocationDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LapTimeFilter {

  private static final long ACCEPTANCE_FACTOR = 5;

  private Logger logger = Logger.getLogger(LapTimeFilter.class);

  public void filterNoDuration(List<LapTimeDTO> results) {
    results.removeIf(r -> r.getDuration() <= 0);
  }

  public void filterExtreme(List<LapTimeDTO> results) {
    // Needs to be done here since we did not have all info before
    Map<Long, List<LapTimeDTO>> lapsPerLocation = new HashMap<>();
    for (LapTimeDTO dto : results) {
      if (dto.getDuration() > 0) {
        NestedLocationDTO location = dto.getSession().getLocation();
        if (lapsPerLocation.containsKey(location.getId())) {
          lapsPerLocation.get(location.getId()).add(dto);
        } else {
          List<LapTimeDTO> lapsOfLocation = new ArrayList<>();
          lapsOfLocation.add(dto);
          lapsPerLocation.put(location.getId(), lapsOfLocation);
        }
      }
    }
    Map<Long, Long> averagePerLocation = new HashMap<>();
    for (Map.Entry<Long, List<LapTimeDTO>> entry : lapsPerLocation.entrySet()) {
      Long locationId = entry.getKey();
      List<LapTimeDTO> laps = entry.getValue();
      long sum = laps.stream().mapToLong(LapTimeDTO::getDuration).sum();
      averagePerLocation.put(locationId, sum / laps.size());
    }

    List<LapTimeDTO> toRemove = new ArrayList<>();
    for (LapTimeDTO lapTimeDTO : results) {
      NestedLocationDTO location = lapTimeDTO.getSession().getLocation();
      Long averageOfLocation = averagePerLocation.get(location.getId());
      if (averageOfLocation != null && lapTimeDTO.getDuration() > 0) {
        if (lapTimeDTO.getDuration() > (averageOfLocation.longValue() * ACCEPTANCE_FACTOR)) {
          logger.info("Discarding lap ID " + lapTimeDTO.getId()
              + " since it is too long (" + lapTimeDTO.getDuration() +
              ") compared to the average for this location : " + averageOfLocation.longValue());
          toRemove.add(lapTimeDTO);
        } else if (lapTimeDTO.getDuration() < (averageOfLocation.longValue() / ACCEPTANCE_FACTOR)) {
          logger.info("Discarding lap ID " + lapTimeDTO.getId()
              + " since it is too short (" + lapTimeDTO.getDuration() +
              ") compared to the average for this location : " + averageOfLocation.longValue());
          toRemove.add(lapTimeDTO);
        }
      }
    }
    results.removeAll(toRemove);

  }

  public void keepOnlyBest(List<LapTimeDTO> results) {
    Map<Long, LapTimeDTO> bests = new HashMap<>();
    List<LapTimeDTO> toRemove = new ArrayList<>();
    for (LapTimeDTO lapTimeDTO : results) {
      if (lapTimeDTO.getDuration() <= 0) {
        toRemove.add(lapTimeDTO);
        continue;
      }
      long pilotId = lapTimeDTO.getPilot().getId();
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
    Map<Long, LapTimeDTO> lasts = new HashMap<>();
    List<LapTimeDTO> toRemove = new ArrayList<>();
    for (LapTimeDTO lapTimeDTO : results) {
      long pilotId = lapTimeDTO.getPilot().getId();
      LapTimeDTO pilotLast = lasts.get(pilotId);
      if (pilotLast != null) {
        toRemove.add(pilotLast);
      }
      lasts.put(pilotId, lapTimeDTO);
    }
    results.removeAll(toRemove);
  }

}
