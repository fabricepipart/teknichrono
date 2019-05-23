package org.trd.app.teknichrono.model.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.trd.app.teknichrono.model.jpa.Beacon;
import org.trd.app.teknichrono.model.jpa.Category;
import org.trd.app.teknichrono.model.jpa.Chronometer;
import org.trd.app.teknichrono.model.jpa.Location;
import org.trd.app.teknichrono.model.jpa.Pilot;
import org.trd.app.teknichrono.model.jpa.Ping;
import org.trd.app.teknichrono.model.jpa.Session;

@Mapper
interface DtoMapper {

    DtoMapper INSTANCE = Mappers.getMapper(DtoMapper.class);

    BeaconDTO asBeaconDto(Beacon beacon);

    CategoryDTO asCategoryDto(Category category);

    @Mapping(target = "lastSeen",
            expression = "java(asNestedPingDto(ChronometerDTO.lastSeen(chronometer.getPings())))")
    ChronometerDTO asChronometerDto(Chronometer chronometer);

    NestedLocationDTO asNestedLocationDto(Location location);

    @Mapping(source = "currentBeacon.number", target = "beaconNumber")
    NestedPilotDTO asNestedPilotDto(Pilot pilot);

    NestedPingDTO asNestedPingDto(Ping ping);

    SessionDTO asSessionDto(Session session);
}
