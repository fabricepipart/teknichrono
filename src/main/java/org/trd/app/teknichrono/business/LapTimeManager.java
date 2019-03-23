package org.trd.app.teknichrono.business;

import org.trd.app.teknichrono.model.dto.LapTimeDTO;
import org.trd.app.teknichrono.model.dto.NestedPilotDTO;
import org.trd.app.teknichrono.model.manage.LapTimeFiller;
import org.trd.app.teknichrono.model.manage.LapTimeFilter;
import org.trd.app.teknichrono.model.manage.LapTimeOrder;

import java.util.List;
import java.util.Set;

public class LapTimeManager {

  LapTimeFilter filter = new LapTimeFilter();
  LapTimeFiller filler = new LapTimeFiller();
  LapTimeOrder order = new LapTimeOrder();


  public void arrangeDisplay(List<LapTimeDTO> results, LapTimeDisplay... displays) {
    arrangeDisplay(results, null, displays);
  }

  public void arrangeDisplay(List<LapTimeDTO> results, Set<NestedPilotDTO> pilots, LapTimeDisplay... displays) {
    filter.filterExtreme(results);
    filler.fillLapsNumber(results);
    for (LapTimeDisplay display : displays) {
      switch (display) {
        case KEEP_COMPLETE:
          filter.filterNoDuration(results);
          // Adjust
          filler.fillLapsNumber(results);
          break;
        case KEEP_LAST:
          // TODO Should probably be merged with Best (best and last info present
          // in DTO) and then its just a matter of order
          filter.keepOnlyLast(results);
          break;
        case ORDER_FOR_RACE:
          order.orderForRace(results);
          filler.fillRaceGaps(results);
          break;
        case KEEP_BEST:
          filter.keepOnlyBest(results);
          break;
        case ORDER_BY_DURATION:
          order.orderByDuration(results);
          filler.fillGaps(results);
          break;
        case ORDER_BY_DATE:
          order.orderByDate(results);
        default:
          break;
      }
    }
    filler.ensureAllPilotsPresent(results, pilots);
  }

}
