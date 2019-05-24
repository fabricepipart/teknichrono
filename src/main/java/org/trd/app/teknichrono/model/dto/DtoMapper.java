package org.trd.app.teknichrono.model.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.trd.app.teknichrono.model.jpa.Beacon;
import org.trd.app.teknichrono.model.jpa.Category;
import org.trd.app.teknichrono.model.jpa.Chronometer;
import org.trd.app.teknichrono.model.jpa.Event;
import org.trd.app.teknichrono.model.jpa.LapTime;
import org.trd.app.teknichrono.model.jpa.Location;
import org.trd.app.teknichrono.model.jpa.Pilot;
import org.trd.app.teknichrono.model.jpa.Ping;
import org.trd.app.teknichrono.model.jpa.Session;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Mapper
interface DtoMapper {

    DtoMapper INSTANCE = Mappers.getMapper(DtoMapper.class);

    BeaconDTO asBeaconDto(Beacon beacon);

    CategoryDTO asCategoryDto(Category category);

    @Mapping(source = "lastestPing", target = "lastSeen")
    ChronometerDTO asChronometerDto(Chronometer chronometer);

    EventDTO asEventDto(Event event);

    default LapTimeDTO asLapTimeDTO(LapTime lapTime) {
        if (lapTime == null) {
            return null;
        }
        LapTimeDTO dto = new LapTimeDTO();
        dto.setId(lapTime.id);
        dto.setVersion(lapTime.getVersion());
        dto.setPilot(asNestedPilotDto(lapTime.getPilot()));
        NestedSessionDTO session = asNestedSessionDto(lapTime.getSession());
        dto.setSession(session);
        List<SectorDTO> sectors = new ArrayList<>();
        Ping previous = null;
        Instant lastSeenDate = null;
        for (Ping ping : lapTime.getIntermediates()) {
            if (previous != null) {
                sectors.add(new SectorDTO(previous, ping));
            } else if (lapTime.getSession().getChronoIndex(ping.getChrono()) == 0) {
                dto.setStartDate(ping);
            }
            if (lastSeenDate == null) {
                lastSeenDate = ping.getInstant();
            } else  if (ping.getInstant() != null && ping.getInstant().isAfter(lastSeenDate)) {
                lastSeenDate = ping.getInstant();
            }
            previous = ping;
        }
        dto.setSectors(sectors);
        dto.setLastSeenDate(lastSeenDate);
        boolean loop = dto.getSession().isLoopTrack();
        if (!loop && previous != null && lapTime.getSession().getChronoIndex(previous.getChrono()) == (session.getChronometersCount() - 1)) {
            dto.setEndDate(previous);
        }
        return dto;
    }

    NestedLocationDTO asNestedLocationDto(Location location);

    @Mapping(source = "currentBeacon.number", target = "beaconNumber")
    NestedPilotDTO asNestedPilotDto(Pilot pilot);

    NestedPingDTO asNestedPingDto(Ping ping);

    @Mapping(source = "location.loopTrack", target = "loopTrack")
    @Mapping(expression = "java(session.getChronometers().size())", target = "chronometersCount")
    NestedSessionDTO asNestedSessionDto(Session session);

    SessionDTO asSessionDto(Session session);
}
