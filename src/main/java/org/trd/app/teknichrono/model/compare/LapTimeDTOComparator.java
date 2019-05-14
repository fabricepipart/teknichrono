package org.trd.app.teknichrono.model.compare;

import org.trd.app.teknichrono.model.dto.LapTimeDTO;

import java.time.Duration;
import java.util.Comparator;

/**
 * Order by Duration then lap start
 */
public class LapTimeDTOComparator implements Comparator<LapTimeDTO> {

  @Override
  public int compare(LapTimeDTO l1, LapTimeDTO l2) {
    if (l1.getDuration().compareTo(Duration.ZERO) <= 0) {
      if (l2.getDuration().compareTo(Duration.ZERO) <= 0) {
        return compareStartDate(l1, l2);
      }
      return 1;
    } else if (l2.getDuration().compareTo(Duration.ZERO) <= 0) {
      return -1;
    } else {
      return l1.getDuration().compareTo(l2.getDuration());
    }
  }

  public int compareStartDate(LapTimeDTO l1, LapTimeDTO l2) {
    if (l1.getStartDate() == null) {
      if (l2.getStartDate() == null) {
        return 0;
      }
      return 1;
    } else if (l2.getStartDate() == null) {
      return -1;
    } else {
      return l1.getStartDate().compareTo(l2.getStartDate());
    }
  }

}
