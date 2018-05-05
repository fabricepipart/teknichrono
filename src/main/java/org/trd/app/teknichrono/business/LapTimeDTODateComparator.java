package org.trd.app.teknichrono.business;

import java.util.Comparator;

import org.trd.app.teknichrono.rest.dto.LapTimeDTO;

public class LapTimeDTODateComparator implements Comparator<LapTimeDTO> {

  @Override
  public int compare(LapTimeDTO l1, LapTimeDTO l2) {
    if (l1.getStartDate() == null) {
      if (l2.getStartDate() == null) {
        return 0;
      }
      return 1;
    } else if (l2.getStartDate() == null) {
      return -1;
    } else {
      int startComparison = Long.valueOf(l1.getStartDate().getTime()).compareTo(l2.getStartDate().getTime());
      if (startComparison != 0) {
        return startComparison;
      } else {
        if (l1.getEndDate() == null) {
          if (l2.getEndDate() == null) {
            return 0;
          }
          return 1;
        } else if (l2.getEndDate() == null) {
          return -1;
        } else {
          return Long.valueOf(l1.getEndDate().getTime()).compareTo(l2.getEndDate().getTime());
        }
      }
    }
  }

}
