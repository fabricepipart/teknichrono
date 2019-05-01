package org.trd.app.teknichrono.model.dto;

import org.trd.app.teknichrono.business.view.LapTimeConverter;
import org.trd.app.teknichrono.business.view.LapTimeFiller;
import org.trd.app.teknichrono.model.jpa.LapTime;
import org.trd.app.teknichrono.model.jpa.TestLapTimeCreator;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class TestLapTimeDTOCreator {

  private int lapId = 0;
  private int pilotId = 0;
  private long now = System.currentTimeMillis();

  public void nextPilot() {
    pilotId++;
  }


  public LapTimeDTO createDTOLapTimeWithSession(long startDate, long endDate, int lapNb, NestedSessionDTO session) {
    LapTimeDTO lap = createDTOLapTime(startDate, endDate, lapNb);
    lap.setSession(session);
    return lap;
  }

  public LapTimeDTO createDTOLapTime(long startDate, long endDate, int lapNb) {
    LapTimeDTO laptime = new LapTimeDTO();
    laptime.setId(++lapId);
    NestedPilotDTO p = new NestedPilotDTO();
    p.setId(pilotId);
    laptime.setPilot(p);
    laptime.setStartDate(new Timestamp(startDate));
    if (endDate > 0) {
      laptime.setEndDate(new Timestamp(endDate));
    }
    laptime.setLastSeenDate(new Timestamp(Math.max(startDate, endDate)));
    if (lapNb > 0) {
      laptime.setLapNumber(lapNb);
    }
    return laptime;
  }

  public List<LapTimeDTO> convertList(List<LapTime> laps) {
    List<LapTimeDTO> toReturn = new ArrayList<>();
    for (LapTime l : laps) {
      toReturn.add(new LapTimeDTO(l));
    }
    return toReturn;
  }


  public List<LapTimeDTO> createLaps() {
    List<LapTimeDTO> laps = new ArrayList();
    // Pilots with 1 lap or several
    // Pilots with laps in order and not in order
    // Pilots with laps contiguous and not contiguous
    // Pilots with laps finished and not finished

    // Several laps , in order , contiguous , finished
    nextPilot();
    laps.add(createDTOLapTime(now, now + 1000, 0));
    laps.add(createDTOLapTime(now + 1000, now + 2101, 0));
    laps.add(createDTOLapTime(now + 2100, now + 3301, 0));

    // Several laps , in order , contiguous , not finished
    nextPilot();
    laps.add(createDTOLapTime(now, now + 1000, 0));
    laps.add(createDTOLapTime(now + 1000, now + 2102, 0));
    laps.add(createDTOLapTime(now + 2100, now + 3302, 0));
    laps.add(createDTOLapTime(now + 3300, 0, 0));
    laps.add(createDTOLapTime(now + 4400, now + 5502, 0));
    laps.add(createDTOLapTime(now + 5800, 0, 0));

    // Several laps , in order , not contiguous , finished
    nextPilot();
    laps.add(createDTOLapTime(now, now + 1000, 0));
    laps.add(createDTOLapTime(now + 10000, now + 11203, 0));
    laps.add(createDTOLapTime(now + 20000, now + 21503, 0));

    // Several laps , in order , not contiguous , not finished
    nextPilot();
    laps.add(createDTOLapTime(now, now + 1000, 0));
    laps.add(createDTOLapTime(now + 10000, 0, 0));

    // Several laps , not in order , contiguous , finished
    nextPilot();
    laps.add(createDTOLapTime(now, now + 1000, 0));
    laps.add(createDTOLapTime(now + 2100, now + 3305, 0));
    laps.add(createDTOLapTime(now + 1000, now + 2105, 0));

    // Several laps , not in order , contiguous , not finished
    nextPilot();
    laps.add(createDTOLapTime(now + 3300, 0, 0));
    laps.add(createDTOLapTime(now, now + 1006, 0));
    laps.add(createDTOLapTime(now + 4400, now + 5506, 0));
    laps.add(createDTOLapTime(now + 1000, now + 2106, 0));
    laps.add(createDTOLapTime(now + 5800, 0, 0));
    laps.add(createDTOLapTime(now + 2100, now + 3306, 0));

    // Several laps , not in order , not contiguous , finished
    nextPilot();
    laps.add(createDTOLapTime(now + 20000, now + 21507, 0));
    laps.add(createDTOLapTime(now, now + 1007, 0));
    laps.add(createDTOLapTime(now + 10000, now + 11207, 0));

    // Several laps , not in order , not contiguous , not finished
    nextPilot();
    laps.add(createDTOLapTime(now + 10000, 0, 0));
    laps.add(createDTOLapTime(now, 0, 0));

    // One lap , finished
    nextPilot();
    laps.add(createDTOLapTime(now + 22222, now + 23333, 0));

    // One lap , not finished
    nextPilot();
    laps.add(createDTOLapTime(now, 0, 0));

    return laps;
  }

  public List<LapTimeDTO> createLapsWithIntermediates() {
    List laps = (new TestLapTimeCreator()).createLapsWithIntermediates();
    return convertList(laps);
  }


  public List<LapTimeDTO> createRaceLapsWithIntermediates() {
    TestLapTimeCreator testLapTimeCreator = new TestLapTimeCreator();
    testLapTimeCreator.nextLocation(true);
    List laps = testLapTimeCreator.createLapsWithIntermediates();
    LapTimeConverter converter = new LapTimeConverter();
    List toReturn = converter.convert(laps);
    (new LapTimeFiller()).fillLapsNumber(toReturn);
    return toReturn;
  }


}
