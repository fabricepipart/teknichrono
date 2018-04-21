package org.trd.app.teknichrono.business;

import java.sql.Timestamp;
import java.util.Comparator;

import org.trd.app.teknichrono.rest.dto.LapTimeDTO;

/**
 * In case of race, order by nb laps and then last lap start
 * 
 * @author fabricepipart
 *
 */
public class LapTimeDTORaceComparator implements Comparator<LapTimeDTO> {

  @Override
  public int compare(LapTimeDTO l1, LapTimeDTO l2) {
    int lapNumberComparison = Integer.valueOf(l2.getLapNumber()).compareTo(l1.getLapNumber());
    if (lapNumberComparison == 0) {
      Timestamp l1EndDate = l1.getEndDate();
      Timestamp l2EndDate = l2.getEndDate();
      int endDateComparison = 0;
      if (l1EndDate == null && l2EndDate != null) {
        endDateComparison = 1;
      } else if (l2EndDate == null && l1EndDate != null) {
        endDateComparison = -1;
      } else if (l1EndDate != null && l2EndDate != null) {
        endDateComparison = Long.valueOf(l1EndDate.getTime()).compareTo(l2EndDate.getTime());
      }
      if (endDateComparison == 0) {
        return Long.valueOf(l1.getStartDate().getTime()).compareTo(l2.getStartDate().getTime());
      }
      return endDateComparison;
    }
    return lapNumberComparison;
  }
}
