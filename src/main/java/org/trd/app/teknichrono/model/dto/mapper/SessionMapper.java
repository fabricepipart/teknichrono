package org.trd.app.teknichrono.model.dto.mapper;

import fr.xebia.extras.selma.Field;
import fr.xebia.extras.selma.IgnoreMissing;
import fr.xebia.extras.selma.Mapper;
import fr.xebia.extras.selma.Maps;
import org.trd.app.teknichrono.model.dto.NestedPilotDTO;
import org.trd.app.teknichrono.model.dto.SessionDTO;
import org.trd.app.teknichrono.model.jpa.Pilot;
import org.trd.app.teknichrono.model.jpa.Session;

@Mapper
public interface SessionMapper {

    @Maps(withIgnoreMissing = IgnoreMissing.DESTINATION)
    SessionDTO asSessionDto(Session session);

    @Maps(
            withIgnoreMissing = IgnoreMissing.DESTINATION,
            withCustomFields = {
                    @Field({"Pilot.currentBeacon.number", "beaconNumber"})
            })
    NestedPilotDTO asNestedPilotDto(Pilot pilot);
}
