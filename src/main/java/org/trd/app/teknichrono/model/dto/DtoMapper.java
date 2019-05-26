package org.trd.app.teknichrono.model.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
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

import java.util.ArrayList;
import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
interface DtoMapper {

  DtoMapper INSTANCE = Mappers.getMapper(DtoMapper.class);

  BeaconDTO asBeaconDto(Beacon beacon);

  CategoryDTO asCategoryDto(Category category);

  @Mapping(target = "lastSeen", source = "lastestPing")
  ChronometerDTO asChronometerDto(Chronometer chronometer);

  EventDTO asEventDto(Event event);

  @Mapping(target = "duration", ignore = true)
  @Mapping(target = "gapWithPrevious", ignore = true)
  @Mapping(target = "gapWithBest", ignore = true)
  @Mapping(target = "lapIndex", ignore = true)
  @Mapping(target = "lapNumber", ignore = true)
  @Mapping(target = "startDate", source = "startChronoInstant")
  @Mapping(target = "endDate", source = "endChronoInstant")
  @Mapping(target = "lastSeenDate", source = "lastChronoInstant")
  @Mapping(target = "intermediates", source = "intermediates")
  LapTimeDTO asLapTimeDTO(LapTime lapTime);

  default List<SectorDTO> asSectorsDTO(List<Ping> pings) {
    List<SectorDTO> sectors = new ArrayList<>();
    Ping previous = null;
    for (Ping ping : pings) {
      if (previous != null) {
        sectors.add(new SectorDTO(previous, ping));
      }
      previous = ping;
    }
    return sectors;
  }

  LocationDTO asLocationDto(Location location);

  NestedBeaconDTO asNestedBeaconDto(Beacon beacon);

  NestedCategoryDTO asNestedCategoryDto(Category category);

  NestedChronometerDTO asNestedChronometerDto(Chronometer chronometer);

  NestedEventDTO asNestedEventDto(Event event);

  NestedLocationDTO asNestedLocationDto(Location location);

  @Mapping(target = "beaconNumber", source = "currentBeacon.number")
  NestedPilotDTO asNestedPilotDto(Pilot pilot);

  NestedPingDTO asNestedPingDto(Ping ping);

  @Mapping(target = "loopTrack", source = "location.loopTrack")
  @Mapping(target = "chronometersCount", expression = "java(session.getChronometers().size())")
  NestedSessionDTO asNestedSessionDto(Session session);

  PilotDTO asPilotDto(Pilot pilot);

  SessionDTO asSessionDto(Session session);
}