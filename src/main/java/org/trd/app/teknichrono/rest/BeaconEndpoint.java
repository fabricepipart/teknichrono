package org.trd.app.teknichrono.rest;

import org.jboss.logging.Logger;
import org.trd.app.teknichrono.model.dto.BeaconDTO;
import org.trd.app.teknichrono.model.jpa.Beacon;
import org.trd.app.teknichrono.model.jpa.BeaconRepository;
import org.trd.app.teknichrono.model.jpa.Pilot;
import org.trd.app.teknichrono.model.jpa.PilotRepository;
import org.trd.app.teknichrono.model.jpa.Ping;
import org.trd.app.teknichrono.model.jpa.PingRepository;
import org.trd.app.teknichrono.util.DurationLogger;

import javax.inject.Inject;
import javax.persistence.OptimisticLockException;
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
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Path("/beacons")
public class BeaconEndpoint {

  private static final Logger LOGGER = Logger.getLogger(BeaconEndpoint.class);

  private final BeaconRepository beaconRepository;

  private final PilotRepository pilotRepository;

  private final PingRepository pingRepository;

  @Inject
  public BeaconEndpoint(BeaconRepository beaconRepository, PilotRepository pilotRepository, PingRepository pingRepository) {
    this.beaconRepository = beaconRepository;
    this.pilotRepository = pilotRepository;
    this.pingRepository = pingRepository;
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Transactional
  public Response create(Beacon entity) {
    DurationLogger perf = DurationLogger.get(LOGGER).start("Create beacon " + entity.getNumber());
    if (entity.getPilot() != null && entity.getPilot().id > 0) {
      Pilot pilot = pilotRepository.findById(entity.getPilot().id);
      entity.setPilot(pilot);
      pilotRepository.persist(pilot);
    }
    beaconRepository.persist(entity);
    URI location = UriBuilder.fromResource(BeaconEndpoint.class).path(String.valueOf(entity.id)).build();
    Response toReturn = Response.created(location).build();
    perf.end();
    return toReturn;
  }

  @DELETE
  @Path("/{id:[0-9][0-9]*}")
  @Transactional
  public Response deleteById(@PathParam("id") long id) {
    DurationLogger perf = DurationLogger.get(LOGGER).start("Delete beacon id=" + id);
    Beacon entity = beaconRepository.findById(id);
    if (entity == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    Pilot associatedPilot = entity.getPilot();
    if (associatedPilot != null) {
      associatedPilot.setCurrentBeacon(null);
      pilotRepository.persist(associatedPilot);
    }
    List<Ping> pings = new ArrayList<>(entity.getPings());
    if (pings != null) {
      for (Ping ping : pings) {
        ping.setBeacon(null);
        pingRepository.persist(ping);
      }
    }
    beaconRepository.delete(entity);
    perf.end();
    return Response.noContent().build();
  }

  @GET
  @Path("/{id:[0-9][0-9]*}")
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public Response findById(@PathParam("id") long id) {
    DurationLogger perf = DurationLogger.get(LOGGER).start("Find beacon id=" + id);
    BeaconDTO entity = findBeaconById(id);
    if (entity == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    perf.end();
    return Response.ok(entity).build();
  }

  private BeaconDTO findBeaconById(long id) {
    Beacon entity = beaconRepository.findById(id);
    if (entity != null) {
      return BeaconDTO.fromBeacon(entity);
    }
    LOGGER.warn("Beacon ID=" + id + " not found");
    return null;
  }

  @GET
  @Path("/number/{number:[0-9][0-9]*}")
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public Response findBeaconNumber(@PathParam("number") long number) {
    DurationLogger perf = DurationLogger.get(LOGGER).start("Find beacon number " + number);
    Beacon entity = beaconRepository.findByNumber(number);
    if (entity == null) {
      LOGGER.warn("Beacon Number=" + number + " not found");
      return Response.status(Status.NOT_FOUND).build();
    }
    BeaconDTO dto = BeaconDTO.fromBeacon(entity);
    perf.end();
    return Response.ok(dto).build();
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public List<BeaconDTO> listAll(@QueryParam("start") Integer startPosition, @QueryParam("max") Integer maxResult) {
    DurationLogger perf = DurationLogger.get(LOGGER).start("Find all beacons");
    List<BeaconDTO> results = beaconRepository.findAll()
        .page(Paging.from(startPosition, maxResult))
        .stream()
        .map(BeaconDTO::fromBeacon)
        .collect(Collectors.toList());
    perf.end();
    return results;
  }

  @PUT
  @Path("/{id:[0-9][0-9]*}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Transactional
  public Response update(@PathParam("id") long id, Beacon entity) {
    if (entity == null) {
      return Response.status(Status.BAD_REQUEST).build();
    }
    DurationLogger perf = DurationLogger.get(LOGGER).start("Update beacon number " + entity.getNumber());
    if (id != entity.id) {
      perf.end();
      return Response.status(Status.CONFLICT).entity(entity).build();
    }
    Beacon beacon = beaconRepository.findById(id);
    if (beacon == null) {
      perf.end();
      return Response.status(Status.NOT_FOUND).build();
    }

    // Update of pilot
    if (entity.getPilot() != null && entity.getPilot().id > 0) {
      Pilot pilot = pilotRepository.findById(entity.getPilot().id);
      beacon.setPilot(pilot);
    }
    beacon.setNumber(entity.getNumber());
    try {
      beacon.persist();
    } catch (OptimisticLockException e) {
      return Response.status(Response.Status.CONFLICT).entity(e.getEntity()).build();
    }

    perf.end();
    return Response.noContent().build();
  }
}
