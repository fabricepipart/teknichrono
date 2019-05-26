package org.trd.app.teknichrono.rest;

import org.jboss.logging.Logger;
import org.trd.app.teknichrono.business.client.PingManager;
import org.trd.app.teknichrono.model.dto.NestedPingDTO;
import org.trd.app.teknichrono.model.jpa.Beacon;
import org.trd.app.teknichrono.model.jpa.BeaconRepository;
import org.trd.app.teknichrono.model.jpa.Chronometer;
import org.trd.app.teknichrono.model.jpa.ChronometerRepository;
import org.trd.app.teknichrono.model.jpa.Ping;
import org.trd.app.teknichrono.model.jpa.PingRepository;
import org.trd.app.teknichrono.util.DurationLogger;

import javax.inject.Inject;
import javax.persistence.EntityManager;
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
import java.util.List;
import java.util.stream.Collectors;

@Path("/pings")
public class PingEndpoint {

  private Logger logger = Logger.getLogger(LapTimeEndpoint.class);

  private final PingRepository pingRepository;

  private final ChronometerRepository chronometerRepository;

  private final BeaconRepository beaconRepository;

  // TODO get rid of this
  private final EntityManager em;

  @Inject
  public PingEndpoint(PingRepository pingRepository, ChronometerRepository chronometerRepository,
                      BeaconRepository beaconRepository, EntityManager em) {
    this.pingRepository = pingRepository;
    this.chronometerRepository = chronometerRepository;
    this.beaconRepository = beaconRepository;
    this.em = em;
  }

  @POST
  @Path("/create")
  @Consumes(MediaType.APPLICATION_JSON)
  @Transactional
  public Response create(Ping entity, @QueryParam("chronoId") long chronoId, @QueryParam("beaconId") long beaconId) {
    try (DurationLogger dl = new DurationLogger(logger, "Ping for chronometer ID=" + chronoId + " and beacon ID=" + beaconId)) {
      //Ping entity = new Ping();
      entity.setInstant(entity.getInstant());
      Chronometer chrono = chronometerRepository.findById(chronoId);
      if (chrono == null) {
        return Response.status(Status.NOT_FOUND).build();
      }
      entity.setChrono(chrono);
      Beacon beacon = beaconRepository.findById(beaconId);
      if (beacon == null) {
        return Response.status(Status.NOT_FOUND).build();
      }
      entity.setBeacon(beacon);
      pingRepository.persist(entity);
      // TODO Check if relevant to create it each time...
      PingManager manager = new PingManager(em);
      manager.addPing(entity);
      return Response.created(UriBuilder.fromResource(PingEndpoint.class).path(String.valueOf(entity.id)).build())
          .build();
    }
  }

  @DELETE
  @Path("/{id:[0-9][0-9]*}")
  @Transactional
  public Response deleteById(@PathParam("id") long id) {
    Ping entity = pingRepository.findById(id);
    if (entity == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    pingRepository.delete(entity);
    return Response.noContent().build();
  }

  @GET
  @Path("/{id:[0-9][0-9]*}")
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public Response findById(@PathParam("id") long id) {
    Ping entity = pingRepository.findById(id);
    if (entity == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    return Response.ok(entity).build();
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public List<NestedPingDTO> listAll(@QueryParam("start") Integer startPosition, @QueryParam("max") Integer maxResult) {
    return pingRepository.findAll()
            .page(Paging.from(startPosition, maxResult))
            .stream()
            .map(NestedPingDTO::fromPing)
            .collect(Collectors.toList());
  }

  @PUT
  @Path("/{id:[0-9][0-9]*}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Transactional
  public Response update(@PathParam("id") long id, Ping entity) {
    if (entity == null) {
      return Response.status(Status.BAD_REQUEST).build();
    }
    if (id != entity.id) {
      return Response.status(Status.CONFLICT).entity(entity).build();
    }
    if (pingRepository.findById(id) == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    try {
      pingRepository.persist(entity);
    } catch (OptimisticLockException e) {
      return Response.status(Response.Status.CONFLICT).entity(e.getEntity()).build();
    }
    return Response.noContent().build();
  }
}
