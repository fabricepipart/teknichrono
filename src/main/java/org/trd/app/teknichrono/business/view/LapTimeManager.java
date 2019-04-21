package org.trd.app.teknichrono.business.view;

import org.trd.app.teknichrono.model.dto.LapTimeDTO;
import org.trd.app.teknichrono.model.dto.NestedPilotDTO;

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
          break;
        case KEEP_BEST:
          filter.keepOnlyBest(results);
          break;
        case ORDER_BY_DURATION:
          order.orderByDuration(results);
          break;
        case ORDER_BY_LAST_SEEN:
          order.orderbyLastSeen(results);
        default:
          break;
      }
    }
    filler.ensureAllPilotsPresent(results, pilots);
  }

}
