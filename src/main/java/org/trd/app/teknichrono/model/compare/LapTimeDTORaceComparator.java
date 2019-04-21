package org.trd.app.teknichrono.model.compare;

import org.trd.app.teknichrono.model.dto.LapTimeDTO;
import org.trd.app.teknichrono.model.dto.SectorDTO;

import java.sql.Timestamp;
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
    int lapNumberComparison = Integer.valueOf(l2.getLapIndex()).compareTo(l1.getLapIndex());
    if (lapNumberComparison == 0) {
      Timestamp l1EndDate = l1.getEndDate();
      Timestamp l2EndDate = l2.getEndDate();
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

  private int compareDates(Timestamp date1, Timestamp date2) {
    int comparison = 0;
    if (date1 == null && date2 != null) {
      comparison = 1;
    } else if (date2 == null && date1 != null) {
      comparison = -1;
    } else if (date1 != null && date2 != null) {
      comparison = Long.valueOf(date1.getTime()).compareTo(date2.getTime());
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
        comparison = compareDates(new Timestamp(correspondingSector.getStart()), new Timestamp(lastSector.getStart()));
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
  public long distance(LapTimeDTO l1, LapTimeDTO l2) {
    if (l2.getLapNumber() == l1.getLapNumber()) {
      Timestamp l1EndDate = l1.getEndDate();
      Timestamp l2EndDate = l2.getEndDate();
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
            return lastSector.getStart() - correspondingSector.getStart();
          }
        }
      }
      if (l1EndDate != null && l2EndDate != null) {
        // Compare end dates
        return distance(l1EndDate, l2EndDate);
      }
    }
    return 0;
  }

  /**
   * @param d1
   * @param d2
   * @return the distance between the two laps (>0 is l2 is after l1)
   */
  private long distance(Timestamp d1, Timestamp d2) {
    if (d1 != null) {
      if (d2 != null) {
        return d2.getTime() - d1.getTime();
      }
      return -d1.getTime();
    }
    if (d2 != null) {
      return d2.getTime();
    }
    return 0;
  }

}
