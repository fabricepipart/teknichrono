package org.trd.app.teknichrono.model.compare;

import org.trd.app.teknichrono.model.jpa.LapTime;

import java.util.Comparator;

public class LapTimeStartComparator implements Comparator<LapTime> {

  @Override
  public int compare(LapTime l1, LapTime l2) {
    if (l1.getStartDate() == null) {
      if (l2.getStartDate() == null) {
        return 0;
      }
      return -1;
    } else if (l2.getStartDate() == null) {
      return 1;
    } else {
      return l1.getStartDate().compareTo(l2.getStartDate());
    }
  }
}
