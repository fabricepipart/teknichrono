package org.trd.app.teknichrono.model.manage;

import org.jboss.logging.Logger;
import org.trd.app.teknichrono.model.compare.LapTimeStartComparator;
import org.trd.app.teknichrono.model.dto.LapTimeDTO;
import org.trd.app.teknichrono.model.dto.NestedPilotDTO;
import org.trd.app.teknichrono.model.dto.NestedSessionDTO;
import org.trd.app.teknichrono.model.jpa.LapTime;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LapTimeConverter {


  private Logger logger = Logger.getLogger(LapTimeConverter.class);


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
}
