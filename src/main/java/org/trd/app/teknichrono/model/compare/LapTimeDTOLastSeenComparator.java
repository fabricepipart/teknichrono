package org.trd.app.teknichrono.model.compare;

import org.trd.app.teknichrono.model.dto.LapTimeDTO;

import java.time.Duration;
import java.util.Comparator;

/**
 * Orders the laps by last seen date (date of the last ping that has been assigned to this lap).
 * If there is no last seen date (it should not) then the lap is considered the oldest.
 */
public class LapTimeDTOLastSeenComparator implements Comparator<LapTimeDTO> {

  @Override
  public int compare(LapTimeDTO l1, LapTimeDTO l2) {
    if (l1.getLastSeenDate() == null) {
      if (l2.getLastSeenDate() == null) {
        return 0;
      }
      return -1;
    } else if (l2.getLastSeenDate() == null) {
      return 1;
    } else {
      return l1.getLastSeenDate().compareTo(l2.getLastSeenDate());
    }
  }

}
