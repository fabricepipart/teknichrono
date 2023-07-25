package org.trd.app.teknichrono.rest;

import org.jboss.logging.Logger;
import org.trd.app.teknichrono.business.client.PingManager;
import org.trd.app.teknichrono.model.dto.NestedBeaconDTO;
import org.trd.app.teknichrono.model.dto.NestedChronometerDTO;
import org.trd.app.teknichrono.model.dto.NestedPingDTO;
import org.trd.app.teknichrono.model.dto.PingDTO;
import org.trd.app.teknichrono.model.jpa.Ping;
import org.trd.app.teknichrono.model.repository.LapTimeRepository;
import org.trd.app.teknichrono.model.repository.PingRepository;
import org.trd.app.teknichrono.util.DurationLogger;
import org.trd.app.teknichrono.util.exception.ConflictingIdException;
import org.trd.app.teknichrono.util.exception.NotFoundException;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/pings")
public class PingEndpoint {

  private static final Logger LOGGER = Logger.getLogger(PingEndpoint.class);

  private final EntityEndpoint entityEndpoint;
  private final LapTimeRepository lapTimeRepository;
  private final PingRepository pingRepository;

  @Inject
  public PingEndpoint(PingRepository pingRepository, LapTimeRepository lapTimeRepository) {
    this.entityEndpoint = new EntityEndpoint(pingRepository);
    this.lapTimeRepository = lapTimeRepository;
    this.pingRepository = pingRepository;
  }

  @POST
  @Path("/create")
  @Consumes(MediaType.APPLICATION_JSON)
  @Transactional
  public Response create(PingDTO entity, @QueryParam("chronoId") long chronoId, @QueryParam("beaconId") long beaconId) {
    try (DurationLogger dl = DurationLogger.get(LOGGER).start("Create Ping - Chrono id=" + chronoId + " beacon id=" + beaconId + " @ " + entity.getInstant())) {
      try {
        entity.setChronometer(new NestedChronometerDTO());
        entity.getChronometer().setId(chronoId);
        entity.setBeacon(new NestedBeaconDTO());
        entity.getBeacon().setId(beaconId);
        Ping ping = pingRepository.create(entity);
        PingManager manager = new PingManager(lapTimeRepository);
        manager.addPing(ping);
        return Response.noContent().build();
      } catch (NotFoundException e) {
        return Response.status(Response.Status.NOT_FOUND).build();
      } catch (ConflictingIdException e) {
        return Response.status(Response.Status.CONFLICT).build();
      }
    }
  }

  @POST
  @Path("/create-multi")
  @Consumes(MediaType.APPLICATION_JSON)
  @Transactional
  public Response create(List<PingDTO> entities) {
    if (entities == null) {
      return Response.status(Response.Status.BAD_REQUEST).build();
    }
    for (PingDTO dto : entities) {
      if (dto.getChronometer() == null || dto.getBeacon() == null) {
        return Response.status(Response.Status.BAD_REQUEST).build();
      }
      create(dto, dto.getChronometer().getId(), dto.getBeacon().getId());
    }
    return Response.noContent().build();
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
  @Path("/latest")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getLatestPing(@QueryParam("chronoId") long chronoId) {
    try (DurationLogger perf = DurationLogger.get(LOGGER).start("Latest ping of chronometer " + chronoId)) {
      try {
        PingDTO dto = pingRepository.latestOfChronometer(chronoId);
        return Response.ok(dto).build();
      } catch (NotFoundException e) {
        return Response.status(Response.Status.NOT_FOUND).build();
      }
    }
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public List<NestedPingDTO> listAll(@QueryParam("page") Integer pageIndex, @QueryParam("pageSize") Integer pageSize) {
    return entityEndpoint.listAll(pageIndex, pageSize);
  }

  @PUT
  @Path("/{id:[0-9][0-9]*}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Transactional
  public Response update(@PathParam("id") long id, PingDTO dto) {
    return entityEndpoint.update(id, dto);
  }
}
