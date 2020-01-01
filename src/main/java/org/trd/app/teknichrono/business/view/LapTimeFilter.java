package org.trd.app.teknichrono.business.view;

import org.jboss.logging.Logger;
import org.trd.app.teknichrono.model.dto.LapTimeDTO;
import org.trd.app.teknichrono.model.dto.NestedLocationDTO;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LapTimeFilter {

  private static final long ACCEPTANCE_FACTOR = 5;

  private Logger logger = Logger.getLogger(LapTimeFilter.class);

  public void filterNoDuration(List<LapTimeDTO> results) {
    results.removeIf(r -> r.getDuration() == null || r.getDuration().compareTo(Duration.ZERO) <= 0);
  }

  public void filterExtreme(List<LapTimeDTO> results) {
    // Needs to be done here since we did not have all info before
    Map<Long, List<LapTimeDTO>> lapsPerLocation = new HashMap<>();
    Map<Long, Duration> minPerLocation = new HashMap<>();
    Map<Long, Duration> maxPerLocation = new HashMap<>();
    fillLapsInformation(results, lapsPerLocation, minPerLocation, maxPerLocation);
    fillMinAndMaxInformation(lapsPerLocation, minPerLocation, maxPerLocation);

    List<LapTimeDTO> toRemove = new ArrayList<>();
    for (LapTimeDTO lapTimeDTO : results) {
      NestedLocationDTO location = lapTimeDTO.getSession().getLocation();
      Duration locationMinDuration = minPerLocation.get(location.getId());
      Duration locationMaxDuration = maxPerLocation.get(location.getId());
      if (lapTimeDTO.getDuration() != null && lapTimeDTO.getDuration().compareTo(Duration.ZERO) > 0) {
        if (lapTimeDTO.getDuration().compareTo(locationMaxDuration) > 0) {
          this.logger.info("Discarding lap ID " + lapTimeDTO.getId()
              + " since it is too long (" + lapTimeDTO.getDuration() +
              ") compared to the max authorized for this location : " + locationMaxDuration);
          toRemove.add(lapTimeDTO);
        } else if (lapTimeDTO.getDuration().compareTo(locationMinDuration) < 0) {
          this.logger.info("Discarding lap ID " + lapTimeDTO.getId()
              + " since it is too short (" + lapTimeDTO.getDuration() +
              ") compared to the min authorized for this location : " + locationMinDuration);
          toRemove.add(lapTimeDTO);
        }
      }
    }
    results.removeAll(toRemove);

  }

  private void fillMinAndMaxInformation(Map<Long, List<LapTimeDTO>> lapsPerLocation, Map<Long, Duration> minPerLocation, Map<Long, Duration> maxPerLocation) {
    for (Map.Entry<Long, List<LapTimeDTO>> entry : lapsPerLocation.entrySet()) {
      Long locationId = entry.getKey();
      if (!minPerLocation.containsKey(locationId) || !maxPerLocation.containsKey(locationId)) {
        List<LapTimeDTO> laps = entry.getValue();
        new LapTimeOrder().orderByDuration(laps);
        Duration medianDuration = laps.get(laps.size() / 2).getDuration();
        minPerLocation.put(locationId, medianDuration.dividedBy(ACCEPTANCE_FACTOR));
        maxPerLocation.put(locationId, medianDuration.multipliedBy(ACCEPTANCE_FACTOR));
      }
    }
  }

  private void fillLapsInformation(List<LapTimeDTO> results, Map<Long, List<LapTimeDTO>> lapsPerLocation, Map<Long, Duration> minPerLocation, Map<Long, Duration> maxPerLocation) {
    for (LapTimeDTO dto : results) {
      if (dto.getDuration() != null && dto.getDuration().compareTo(Duration.ZERO) > 0) {
        NestedLocationDTO location = dto.getSession().getLocation();
        if (location.getMinimum() != null && !location.getMinimum().isZero()) {
          minPerLocation.put(location.getId(), location.getMinimum());
        }
        if (location.getMaximum() != null && !location.getMaximum().isZero()) {
          maxPerLocation.put(location.getId(), location.getMaximum());
        }
        if (lapsPerLocation.containsKey(location.getId())) {
          lapsPerLocation.get(location.getId()).add(dto);
        } else {
          List<LapTimeDTO> lapsOfLocation = new ArrayList<>();
          lapsOfLocation.add(dto);
          lapsPerLocation.put(location.getId(), lapsOfLocation);
        }
      }
    }
  }

  public void keepOnlyBest(List<LapTimeDTO> results) {
    Map<Long, LapTimeDTO> bests = new HashMap<>();
    List<LapTimeDTO> toRemove = new ArrayList<>();
    for (LapTimeDTO lapTimeDTO : results) {
      if (lapTimeDTO.getDuration() == null || lapTimeDTO.getDuration().compareTo(Duration.ZERO) <= 0) {
        toRemove.add(lapTimeDTO);
        continue;
      }
      long pilotId = lapTimeDTO.getPilot().getId();
      LapTimeDTO pilotBest = bests.get(pilotId);
      if (pilotBest == null) {
        bests.put(pilotId, lapTimeDTO);
      } else {
        if (lapTimeDTO.getDuration().compareTo(pilotBest.getDuration()) < 0) {
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
