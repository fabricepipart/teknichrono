package org.trd.app.teknichrono.rest;

import org.jboss.logging.Logger;
import org.trd.app.teknichrono.model.dto.PilotDTO;
import org.trd.app.teknichrono.model.jpa.Pilot;
import org.trd.app.teknichrono.model.repository.BeaconRepository;
import org.trd.app.teknichrono.model.repository.PilotRepository;
import org.trd.app.teknichrono.util.DurationLogger;
import org.trd.app.teknichrono.util.exception.NotFoundException;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.List;

@Path("/pilots")
public class PilotEndpoint {

  private static final Logger LOGGER = Logger.getLogger(PilotEndpoint.class);

  private final BeaconRepository beaconRepository;
  private final PilotRepository pilotRepository;
  private final EntityEndpoint entityEndpoint;

  @Inject
  public PilotEndpoint(PilotRepository pilotRepository, BeaconRepository beaconRepository) {
    this.beaconRepository = beaconRepository;
    this.pilotRepository = pilotRepository;
    this.entityEndpoint = new EntityEndpoint(pilotRepository);
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Transactional
  public Response create(PilotDTO entity) {
    return entityEndpoint.create(entity, entity.getFirstName() + " " + entity.getLastName());
  }

  @DELETE
  @Path("/{id:[0-9][0-9]*}")
  @Transactional
  public Response deleteById(@PathParam("id") long id) {
    return entityEndpoint.deleteById(id);
  }

  @GET
  @Path("/{id:[0-9][0-9]*}")
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public Response findById(@PathParam("id") long id) {
    return entityEndpoint.findById(id);
  }

  @GET
  @Path("/name")
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public Response findByName(@QueryParam("firstname") String firstname, @QueryParam("lastname") String lastname) {
    try (DurationLogger perf = DurationLogger.get(LOGGER).start("Find pilot " + firstname + " " + lastname)) {
      Pilot entity = pilotRepository.findByName(firstname, lastname);
      if (entity == null) {
        return Response.status(Status.NOT_FOUND).build();
      }
      PilotDTO dto = PilotDTO.fromPilot(entity);
      return Response.ok(dto).build();
    }
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public List<PilotDTO> listAll(@QueryParam("page") Integer pageIndex, @QueryParam("pageSize") Integer pageSize) {
    return entityEndpoint.listAll(pageIndex, pageSize);
  }

  @POST
  @Path("{pilotId:[0-9][0-9]*}/setBeacon")
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public Response associateBeacon(@PathParam("pilotId") long pilotId, @QueryParam("beaconId") long beaconId) {
    try (DurationLogger dl = DurationLogger.get(LOGGER).start("Add beacon id=" + beaconId +
        " to pilot id=" + pilotId)) {
      try {
        PilotDTO dto = pilotRepository.associateBeacon(pilotId, beaconId);
        return Response.ok(dto).build();
      } catch (NotFoundException e) {
        return Response.status(Response.Status.NOT_FOUND).build();
      }
    }
    /*return entityEndpoint.setField(pilotId, beaconId, beaconRepository,
        Pilot::setCurrentBeacon, Beacon::setPilot);*/
  }

  @PUT
  @Path("/{id:[0-9][0-9]*}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Transactional
  public Response update(@PathParam("id") long id, PilotDTO dto) {
    return entityEndpoint.update(id, dto);
  }
}
