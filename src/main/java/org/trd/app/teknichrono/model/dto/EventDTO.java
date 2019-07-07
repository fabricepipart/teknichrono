package org.trd.app.teknichrono.model.dto;

import lombok.Data;
import org.trd.app.teknichrono.model.jpa.Event;

import java.util.ArrayList;
import java.util.List;

@Data
public class EventDTO implements EntityDTO {

  private Long id;
  private int version;
  private String name;
  private List<NestedSessionDTO> sessions = new ArrayList<>();

  public static EventDTO fromEvent(Event event) {
    return DtoMapper.INSTANCE.asEventDto(event);
  }
}
