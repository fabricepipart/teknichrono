package org.trd.app.teknichrono.model.dto;

import org.trd.app.teknichrono.business.view.LapTimeConverter;
import org.trd.app.teknichrono.business.view.LapTimeFiller;
import org.trd.app.teknichrono.model.jpa.LapTime;
import org.trd.app.teknichrono.model.jpa.LapTimeCreatorForTests;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LapTimeDTOCreatorForTests {

  private long lapId = 0;
  private long pilotId = 0;
  private long now = System.currentTimeMillis();

  private final NestedSessionDTO session;

  public LapTimeDTOCreatorForTests() {
    this.session = new NestedSessionDTO();
    NestedLocationDTO location = new NestedLocationDTO();
    location.setId(1234595L);
    this.session.setLocation(location);
  }

  public NestedSessionDTO getSession() {
    return this.session;
  }

  public void nextPilot() {
    this.pilotId++;
  }


  public LapTimeDTO createDTOLapTimeWithSession(long startDate, long endDate, long lapNb) {
    LapTimeDTO lap = createDTOLapTime(startDate, endDate, lapNb);
    lap.setSession(this.session);
    return lap;
  }

  public LapTimeDTO createDTOLapTime(long startDate, long endDate, long lapNb) {
    LapTimeDTO laptime = new LapTimeDTO();
    laptime.setId(++this.lapId);
    NestedPilotDTO p = new NestedPilotDTO();
    p.setId(this.pilotId);
    laptime.setPilot(p);
    laptime.setStartDate(Instant.ofEpochMilli(startDate));
    if (endDate > 0) {
      laptime.setEndDate(Instant.ofEpochMilli(endDate));
    }
    laptime.setLastSeenDate(Instant.ofEpochMilli(Math.max(startDate, endDate)));
    if (lapNb > 0) {
      laptime.setLapNumber(lapNb);
    }
    return laptime;
  }


  public List<LapTimeDTO> createLaps() {
    List<LapTimeDTO> laps = new ArrayList<>();
    // Pilots with 1 lap or several
    // Pilots with laps in order and not in order
    // Pilots with laps contiguous and not contiguous
    // Pilots with laps finished and not finished

    // Several laps , in order , contiguous , finished
    nextPilot();
    laps.add(createDTOLapTime(this.now, this.now + 1000, 0));
    laps.add(createDTOLapTime(this.now + 1000, this.now + 2101, 0));
    laps.add(createDTOLapTime(this.now + 2100, this.now + 3301, 0));

    // Several laps , in order , contiguous , not finished
    nextPilot();
    laps.add(createDTOLapTime(this.now, this.now + 1000, 0));
    laps.add(createDTOLapTime(this.now + 1000, this.now + 2102, 0));
    laps.add(createDTOLapTime(this.now + 2100, this.now + 3302, 0));
    laps.add(createDTOLapTime(this.now + 3300, 0, 0));
    laps.add(createDTOLapTime(this.now + 4400, this.now + 5502, 0));
    laps.add(createDTOLapTime(this.now + 5800, 0, 0));

    // Several laps , in order , not contiguous , finished
    nextPilot();
    laps.add(createDTOLapTime(this.now, this.now + 1000, 0));
    laps.add(createDTOLapTime(this.now + 10000, this.now + 11203, 0));
    laps.add(createDTOLapTime(this.now + 20000, this.now + 21503, 0));

    // Several laps , in order , not contiguous , not finished
    nextPilot();
    laps.add(createDTOLapTime(this.now, this.now + 1000, 0));
    laps.add(createDTOLapTime(this.now + 10000, 0, 0));

    // Several laps , not in order , contiguous , finished
    nextPilot();
    laps.add(createDTOLapTime(this.now, this.now + 1000, 0));
    laps.add(createDTOLapTime(this.now + 2100, this.now + 3305, 0));
    laps.add(createDTOLapTime(this.now + 1000, this.now + 2105, 0));

    // Several laps , not in order , contiguous , not finished
    nextPilot();
    laps.add(createDTOLapTime(this.now + 3300, 0, 0));
    laps.add(createDTOLapTime(this.now, this.now + 1006, 0));
    laps.add(createDTOLapTime(this.now + 4400, this.now + 5506, 0));
    laps.add(createDTOLapTime(this.now + 1000, this.now + 2106, 0));
    laps.add(createDTOLapTime(this.now + 5800, 0, 0));
    laps.add(createDTOLapTime(this.now + 2100, this.now + 3306, 0));

    // Several laps , not in order , not contiguous , finished
    nextPilot();
    laps.add(createDTOLapTime(this.now + 20000, this.now + 21507, 0));
    laps.add(createDTOLapTime(this.now, this.now + 1007, 0));
    laps.add(createDTOLapTime(this.now + 10000, this.now + 11207, 0));

    // Several laps , not in order , not contiguous , not finished
    nextPilot();
    laps.add(createDTOLapTime(this.now + 10000, 0, 0));
    laps.add(createDTOLapTime(this.now, 0, 0));

    // One lap , finished
    nextPilot();
    laps.add(createDTOLapTime(this.now + 22222, this.now + 23333, 0));

    // One lap , not finished
    nextPilot();
    laps.add(createDTOLapTime(this.now, 0, 0));

    return laps;
  }

  public List<LapTimeDTO> createLapsWithIntermediates() {
    List<LapTime> laps = new LapTimeCreatorForTests().createLapsWithIntermediates();
    return laps.stream().map(LapTimeDTO::fromLapTime).collect(Collectors.toList());
  }

  public List<LapTimeDTO> createRaceLapsWithIntermediates() {
    LapTimeCreatorForTests testLapTimeCreator = new LapTimeCreatorForTests();
    testLapTimeCreator.nextLocation(true);
    List<LapTime> laps = testLapTimeCreator.createLapsWithIntermediates();
    LapTimeConverter converter = new LapTimeConverter();
    List<LapTimeDTO> toReturn = converter.convert(laps);
    new LapTimeFiller().fillLapsNumber(toReturn);
    return toReturn;
  }
}
