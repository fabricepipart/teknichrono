package org.trd.app.teknichrono.business;

import java.util.Comparator;

import org.trd.app.teknichrono.rest.dto.LapTimeDTO;

public class LapTimeDTOComparator implements Comparator<LapTimeDTO> {

  @Override
  public int compare(LapTimeDTO l1, LapTimeDTO l2) {
    if (l1.getDuration() <= 0) {
      if (l2.getDuration() <= 0) {
        return 0;
      }
      return 1;
    } else if (l2.getDuration() <= 0) {
      return -1;
    } else {
      return Long.valueOf(l1.getDuration()).compareTo(l2.getDuration());
    }
  }

}
