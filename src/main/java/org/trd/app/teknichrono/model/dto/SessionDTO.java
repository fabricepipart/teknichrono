package org.trd.app.teknichrono.model.dto;

import lombok.Data;
import org.trd.app.teknichrono.model.jpa.Session;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class SessionDTO implements EntityDTO {

  private Long id;
  private Instant start;
  private long inactivity = 60000L;
  private Instant end;
  private String type;
  private boolean current = false;
  private List<NestedChronometerDTO> chronometers = new ArrayList<>();
  private String name;
  private NestedLocationDTO location;
  private NestedEventDTO event = null;
  private Set<NestedPilotDTO> pilots = new HashSet<>();

  public static SessionDTO fromSession(Session session) {
    return DtoMapper.INSTANCE.asSessionDto(session);
  }
}
