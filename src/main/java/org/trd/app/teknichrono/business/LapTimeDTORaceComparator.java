package org.trd.app.teknichrono.business;

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
    int lapNumberComparison = Integer.valueOf(l1.getLapNumber()).compareTo(l2.getLapNumber());
    if (lapNumberComparison == 0) {
      return Long.valueOf(l1.getStartDate().getTime()).compareTo(l2.getStartDate().getTime());
    }
    return lapNumberComparison;
  }
}
