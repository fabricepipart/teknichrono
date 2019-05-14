package org.trd.app.teknichrono.model.compare;

import org.trd.app.teknichrono.model.dto.LapTimeDTO;
import org.trd.app.teknichrono.model.dto.SectorDTO;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;

/**
 * In case of race, order by nb laps and then last lap start
 *
 * @author fabricepipart
 */
public class LapTimeDTORaceComparator implements Comparator<LapTimeDTO> {

  @Override
  public int compare(LapTimeDTO l1, LapTimeDTO l2) {
    int lapNumberComparison = Long.valueOf(l2.getLapIndex()).compareTo(l1.getLapIndex());
    if (lapNumberComparison == 0) {
      Instant l1EndDate = l1.getEndDate();
      Instant l2EndDate = l2.getEndDate();
      int endDateComparison = compareDates(l1EndDate, l2EndDate);
      if (endDateComparison == 0) {
        // Compare sectors
        int sectorsComparison = compareSectors(l1.getIntermediates(), l2.getIntermediates());
        if (sectorsComparison == 0) {
          return compareDates(l1.getStartDate(), l2.getStartDate());
        }
        return sectorsComparison;
      }
      return endDateComparison;
    }
    return lapNumberComparison;
  }

  private int compareDates(Instant date1, Instant date2) {
    int comparison = 0;
    if (date1 == null && date2 != null) {
      comparison = 1;
    } else if (date2 == null && date1 != null) {
      comparison = -1;
    } else if (date1 != null && date2 != null) {
      comparison = date1.compareTo(date2);
    }
    return comparison;
  }

  private int compareSectors(List<SectorDTO> sectors1, List<SectorDTO> sectors2) {
    int comparison = 0;
    if (sectors1 == null && sectors2 != null && !sectors2.isEmpty()) {
      comparison = 1;
    } else if (sectors2 == null && sectors1 != null && !sectors1.isEmpty()) {
      comparison = -1;
    } else if (sectors2 != null && !sectors2.isEmpty() && sectors1 != null && !sectors1.isEmpty()) {
      // Compare last
      SectorDTO lastSector = sectors2.get(sectors2.size() - 1);
      SectorDTO correspondingSector = getCorrespondingSector(lastSector, sectors1);
      if (correspondingSector != null) {
        comparison = compareDates(correspondingSector.getStart(), lastSector.getStart());
      }
    }
    return comparison;

  }

  private SectorDTO getCorrespondingSector(SectorDTO sector, List<SectorDTO> sectors) {
    for (SectorDTO s : sectors) {
      if (s.getFromChronoId() == sector.getFromChronoId() && s.getToChronoId() == sector.getToChronoId()) {
        return s;
      }
    }
    return null;
  }

  /**
   * @param l1
   * @param l2
   * @return the distance between the two laps (>0 is l2 is after l1)
   */
  public Duration distance(LapTimeDTO l1, LapTimeDTO l2) {
    if (l2.getLapNumber() == l1.getLapNumber()) {
      Instant l1EndDate = l1.getEndDate();
      Instant l2EndDate = l2.getEndDate();
      if (l1EndDate == null && l2EndDate == null) {
        // Compare sectors
        List<SectorDTO> sectors1 = l1.getIntermediates();
        List<SectorDTO> sectors2 = l2.getIntermediates();
        if ((sectors2 == null || sectors2.isEmpty()) && (sectors1 == null || sectors1.isEmpty())) {
          // Compare start dates
          return distance(l1.getStartDate(), l2.getStartDate());
        }
        if (sectors2 != null && !sectors2.isEmpty() && sectors1 != null && !sectors1.isEmpty()) {
          // Compare sectors dates
          SectorDTO lastSector = sectors2.get(sectors2.size() - 1);
          SectorDTO correspondingSector = getCorrespondingSector(lastSector, sectors1);
          if (correspondingSector != null) {
            return Duration.between(lastSector.getStart(), correspondingSector.getStart()).abs();
          }
        }
      }
      if (l1EndDate != null && l2EndDate != null) {
        // Compare end dates
        return distance(l1EndDate, l2EndDate);
      }
    }
    return Duration.ZERO;
  }

  /**
   * @param d1
   * @param d2
   * @return the distance between the two laps (>0 is l2 is after l1)
   */
  private Duration distance(Instant d1, Instant d2) {
    if (d1 != null) {
      if (d2 != null) {
        return Duration.between(d2, d1).abs();
      }
      return Duration.ZERO;
    }
    if (d2 != null) {
      return Duration.ZERO;
    }
    return Duration.ZERO;
  }

}
