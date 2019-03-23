package org.trd.app.teknichrono.rest;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;

import org.jboss.logging.Logger;
import org.trd.app.teknichrono.model.jpa.Beacon;
import org.trd.app.teknichrono.model.jpa.Pilot;
import org.trd.app.teknichrono.model.jpa.Ping;
import org.trd.app.teknichrono.model.dto.BeaconDTO;
import org.trd.app.teknichrono.util.DurationLogger;

/**
 * 
 */
@Stateless(name = "beacons")
@Path("/beacons")
public class BeaconEndpoint {

  private Logger logger = Logger.getLogger(BeaconEndpoint.class);

  @PersistenceContext(unitName = "teknichrono-persistence-unit")
  private EntityManager em;

  @POST
  @Consumes("application/json")
  public Response create(Beacon entity) {
    DurationLogger perf = DurationLogger.get(logger).start("Create beacon " + entity.getNumber());
    if (entity.getPilot() != null && entity.getPilot().getId() > 0) {
      Pilot pilot = em.find(Pilot.class, entity.getPilot().getId());
      entity.setPilot(pilot);
    }
    em.persist(entity);
    Response toReturn = Response
        .created(UriBuilder.fromResource(BeaconEndpoint.class).path(String.valueOf(entity.getId())).build()).build();
    perf.end();
    return toReturn;
  }

  @DELETE
  @Path("/{id:[0-9][0-9]*}")
  public Response deleteById(@PathParam("id") int id) {
    DurationLogger perf = DurationLogger.get(logger).start("Delete beacon id=" + id);
    Beacon entity = em.find(Beacon.class, id);
    if (entity == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    Pilot associatedPilot = entity.getPilot();
    if (associatedPilot != null) {
      associatedPilot.setCurrentBeacon(null);
      em.persist(associatedPilot);
    }
    List<Ping> pings = entity.getPings();
    if (pings != null) {
      for (Ping ping : pings) {
        ping.setBeacon(null);
        em.persist(ping);
      }
    }
    em.remove(entity);
    perf.end();
    return Response.noContent().build();
  }

  @GET
  @Path("/{id:[0-9][0-9]*}")
  @Produces("application/json")
  public Response findById(@PathParam("id") int id) {
    DurationLogger perf = DurationLogger.get(logger).start("Find beacon id=" + id);
    BeaconDTO entity = findBeacon(id);
    if (entity == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    perf.end();
    return Response.ok(entity).build();
  }

  public BeaconDTO findBeacon(int id) {
    TypedQuery<Beacon> findByIdQuery = em
        .createQuery("SELECT DISTINCT b FROM Beacon b WHERE b.id = :entityId ORDER BY b.id", Beacon.class);
    findByIdQuery.setParameter("entityId", id);
    BeaconDTO dto = null;
    try {
      Beacon entity = findByIdQuery.getSingleResult();
      dto = new BeaconDTO(entity);
    } catch (NoResultException nre) {
      logger.warn("Beacon ID=" + id + " not found");
    }
    return dto;
  }

  @GET
  @Path("/number/{number:[0-9][0-9]*}")
  @Produces("application/json")
  public Response findBeaconNumber(@PathParam("number") int number) {
    DurationLogger perf = DurationLogger.get(logger).start("Find beacon number " + number);
    TypedQuery<Beacon> findByIdQuery = em
        .createQuery("SELECT DISTINCT b FROM Beacon b WHERE b.number = :entityId ORDER BY b.number", Beacon.class);
    findByIdQuery.setParameter("entityId", number);
    BeaconDTO dto = null;
    try {
      Beacon entity = findByIdQuery.getSingleResult();
      dto = new BeaconDTO(entity);
    } catch (NoResultException nre) {
      logger.warn("Beacon Number=" + number + " not found");
      return Response.status(Status.NOT_FOUND).build();
    }
    perf.end();
    return Response.ok(dto).build();
  }

  @GET
  @Produces("application/json")
  public List<BeaconDTO> listAll(@QueryParam("start") Integer startPosition, @QueryParam("max") Integer maxResult) {
    DurationLogger perf = DurationLogger.get(logger).start("Find all beacons");
    TypedQuery<Beacon> findAllQuery = em.createQuery("SELECT DISTINCT b FROM Beacon b ORDER BY b.id", Beacon.class);
    if (startPosition != null) {
      findAllQuery.setFirstResult(startPosition);
    }
    if (maxResult != null) {
      findAllQuery.setMaxResults(maxResult);
    }
    final List<Beacon> results = findAllQuery.getResultList();
    final List<BeaconDTO> converted = BeaconDTO.convert(results);
    perf.end();
    return converted;
  }

  @PUT
  @Path("/{id:[0-9][0-9]*}")
  @Consumes("application/json")
  public Response update(@PathParam("id") int id, Beacon entity) {
    if (entity == null) {
      return Response.status(Status.BAD_REQUEST).build();
    }
    DurationLogger perf = DurationLogger.get(logger).start("Update beacon number " + entity.getNumber());
    if (id != entity.getId()) {
      perf.end();
      return Response.status(Status.CONFLICT).entity(entity).build();
    }
    Beacon beacon = em.find(Beacon.class, id);
    if (beacon == null) {
      perf.end();
      return Response.status(Status.NOT_FOUND).build();
    }

    // Update of pilot
    if (entity.getPilot() != null && entity.getPilot().getId() > 0) {
      Pilot pilot = em.find(Pilot.class, entity.getPilot().getId());
      beacon.setPilot(pilot);
    }
    beacon.setNumber(entity.getNumber());
    try {
      em.persist(beacon);
    } catch (OptimisticLockException e) {
      return Response.status(Response.Status.CONFLICT).entity(e.getEntity()).build();
    }

    perf.end();
    return Response.noContent().build();
  }
}
