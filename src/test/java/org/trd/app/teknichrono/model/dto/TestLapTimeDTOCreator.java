package org.trd.app.teknichrono.model.dto;

import java.sql.Timestamp;

public class TestLapTimeDTOCreator {


  public LapTimeDTO createDTOLapTimeWithSession(int i, int pilotId, long startDate, long endDate, int lapNb, NestedSessionDTO session) {
    LapTimeDTO lap = createDTOLapTime(i, pilotId, startDate, endDate, lapNb);
    lap.setSession(session);
    return lap;
  }

  public LapTimeDTO createDTOLapTime(int i, int pilotId, long startDate, long endDate, int lapNb) {
    LapTimeDTO laptime = new LapTimeDTO();
    laptime.setId(i);
    NestedPilotDTO p = new NestedPilotDTO();
    p.setId(pilotId);
    laptime.setPilot(p);
    laptime.setStartDate(new Timestamp(startDate));
    if (endDate > 0) {
      laptime.setEndDate(new Timestamp(endDate));
    }
    laptime.setLapNumber(lapNb);
    return laptime;
  }
}
