package org.trd.app.teknichrono.model.manage;

import org.trd.app.teknichrono.model.compare.LapTimeDTOComparator;
import org.trd.app.teknichrono.model.compare.LapTimeDTODateComparator;
import org.trd.app.teknichrono.model.compare.LapTimeDTORaceComparator;
import org.trd.app.teknichrono.model.dto.LapTimeDTO;

import java.util.List;

public class LapTimeOrder {


  public void orderByDuration(List<LapTimeDTO> results) {
    results.sort(new LapTimeDTOComparator());
  }

  public void orderForRace(List<LapTimeDTO> results) {
    results.sort(new LapTimeDTORaceComparator());
  }

  public void orderByDate(List<LapTimeDTO> results) {
    results.sort(new LapTimeDTODateComparator());
  }

}
