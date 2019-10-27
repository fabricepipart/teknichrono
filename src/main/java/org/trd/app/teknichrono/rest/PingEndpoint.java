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
      entityEndpoint.create(dto, "Chrono #" + dto.getChronometer().getId() +
          " Beacon #" + dto.getBeacon().getId() + " @ " + dto.getInstant());
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
