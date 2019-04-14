package org.trd.app.teknichrono.model.dto;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class TestLapTimeDTOCreator {

  private int lapId = 1;


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
    if (lapNb > 0) {
      laptime.setLapNumber(lapNb);
    }
    return laptime;
  }

  public List<LapTimeDTO> createLaps() {
    long now = System.currentTimeMillis();
    List laps = new ArrayList();
    int pilotId = 0;
    // Pilots with 1 lap or several
    // Pilots with laps in order and not in order
    // Pilots with laps contiguous and not contiguous
    // Pilots with laps finished and not finished

    // Several laps , in order , contiguous , finished
    laps.add(createDTOLapTime(lapId(), 1, now, now + 1000, 0));
    laps.add(createDTOLapTime(lapId(), 1, now + 1000, now + 2101, 0));
    laps.add(createDTOLapTime(lapId(), 1, now + 2100, now + 3301, 0));

    // Several laps , in order , contiguous , not finished
    laps.add(createDTOLapTime(lapId(), 2, now, now + 1000, 0));
    laps.add(createDTOLapTime(lapId(), 2, now + 1000, now + 2102, 0));
    laps.add(createDTOLapTime(lapId(), 2, now + 2100, now + 3302, 0));
    laps.add(createDTOLapTime(lapId(), 2, now + 3300, 0, 0));
    laps.add(createDTOLapTime(lapId(), 2, now + 4400, now + 5502, 0));
    laps.add(createDTOLapTime(lapId(), 2, now + 5800, 0, 0));

    // Several laps , in order , not contiguous , finished
    laps.add(createDTOLapTime(lapId(), 3, now, now + 1000, 0));
    laps.add(createDTOLapTime(lapId(), 3, now + 10000, now + 11203, 0));
    laps.add(createDTOLapTime(lapId(), 3, now + 20000, now + 21503, 0));

    // Several laps , in order , not contiguous , not finished
    laps.add(createDTOLapTime(lapId(), 4, now, now + 1000, 0));
    laps.add(createDTOLapTime(lapId(), 4, now + 10000, 0, 0));

    // Several laps , not in order , contiguous , finished
    laps.add(createDTOLapTime(lapId(), 5, now, now + 1000, 0));
    laps.add(createDTOLapTime(lapId(), 5, now + 2100, now + 3305, 0));
    laps.add(createDTOLapTime(lapId(), 5, now + 1000, now + 2105, 0));

    // Several laps , not in order , contiguous , not finished
    laps.add(createDTOLapTime(lapId(), 6, now + 3300, 0, 0));
    laps.add(createDTOLapTime(lapId(), 6, now, now + 1006, 0));
    laps.add(createDTOLapTime(lapId(), 6, now + 4400, now + 5506, 0));
    laps.add(createDTOLapTime(lapId(), 6, now + 1000, now + 2106, 0));
    laps.add(createDTOLapTime(lapId(), 6, now + 5800, 0, 0));
    laps.add(createDTOLapTime(lapId(), 6, now + 2100, now + 3306, 0));

    // Several laps , not in order , not contiguous , finished
    laps.add(createDTOLapTime(lapId(), 7, now + 20000, now + 21507, 0));
    laps.add(createDTOLapTime(lapId(), 7, now, now + 1007, 0));
    laps.add(createDTOLapTime(lapId(), 7, now + 10000, now + 11207, 0));

    // Several laps , not in order , not contiguous , not finished
    laps.add(createDTOLapTime(lapId(), 8, now + 10000, 0, 0));
    laps.add(createDTOLapTime(lapId(), 8, now, 0, 0));

    // One lap , finished
    laps.add(createDTOLapTime(lapId(), 9, now + 22222, now + 23333, 0));

    // One lap , not finished
    laps.add(createDTOLapTime(lapId(), 10, now, 0, 0));

    return laps;
  }

  public int lapId() {
    return ++lapId;
  }

}
