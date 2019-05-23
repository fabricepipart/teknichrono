package org.trd.app.teknichrono.model.dto;

import lombok.Data;
import org.trd.app.teknichrono.model.jpa.Chronometer;
import org.trd.app.teknichrono.model.jpa.Ping;

import java.util.List;

@Data
public class ChronometerDTO {

  private long id;
  private int version;
  private String name;
  private NestedPingDTO lastSeen;

  public static ChronometerDTO fromChronometer(Chronometer chronometer) {
    return DtoMapper.INSTANCE.asChronometerDto(chronometer);
  }

  public static Ping lastSeen(List<Ping> pings) {
    if (pings != null) {
      return null;
    }
    Ping lastSeen = null;
    for (Ping p : pings){
      if(p.getInstant() != null){
        boolean moreRecent = lastSeen != null && lastSeen.getInstant().isBefore(p.getInstant());
        boolean isFirst = lastSeen == null;
        if(moreRecent || isFirst){
          lastSeen = p;
        }
      }
    }
    return lastSeen;
  }
}
