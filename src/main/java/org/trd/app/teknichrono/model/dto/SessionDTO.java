package org.trd.app.teknichrono.model.dto;

import lombok.Data;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.trd.app.teknichrono.model.jpa.Session;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class SessionDTO {

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

    @Mapper
    interface ModelMapper {
        SessionDTO asSessionDto(Session session);
    }

    private final static ModelMapper  MAPPER = Mappers.getMapper(ModelMapper.class);

    public static SessionDTO fromSession(Session session) {
        return MAPPER.asSessionDto(session);
    }
}
