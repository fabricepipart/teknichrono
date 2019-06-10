package org.trd.app.teknichrono.rest;

import org.jboss.logging.Logger;
import org.trd.app.teknichrono.model.dto.LocationDTO;
import org.trd.app.teknichrono.model.jpa.Location;
import org.trd.app.teknichrono.model.jpa.LocationRepository;
import org.trd.app.teknichrono.model.jpa.Session;
import org.trd.app.teknichrono.model.jpa.SessionRepository;
import org.trd.app.teknichrono.util.DurationLogger;
import org.trd.app.teknichrono.util.exception.NotFoundException;

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
import java.util.List;
import java.util.stream.Collectors;

@Path("/locations")
public class LocationEndpoint {

  private static final Logger LOGGER = Logger.getLogger(LocationEndpoint.class);

  private final LocationRepository locationRepository;
  private final SessionRepository sessionRepository;

  @Inject
  public LocationEndpoint(LocationRepository locationRepository, SessionRepository sessionRepository) {
    this.locationRepository = locationRepository;
    this.sessionRepository = sessionRepository;
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Transactional
  public Response create(Location entity) {
    locationRepository.persist(entity);
    return Response
        .created(UriBuilder.fromResource(LocationEndpoint.class).path(String.valueOf(entity.id)).build()).build();
  }

  @DELETE
  @Path("/{id:[0-9][0-9]*}")
  @Transactional
  public Response deleteById(@PathParam("id") long id) {
    try (DurationLogger perf = DurationLogger.get(LOGGER).start("Delete pilot id=" + id)) {
      try {
        locationRepository.deleteById(id);
      } catch (NotFoundException e) {
        return Response.status(Status.NOT_FOUND).build();
      }
      return Response.noContent().build();
    }
  }

  @GET
  @Path("/{id:[0-9][0-9]*}")
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public Response findById(@PathParam("id") long id) {
    LocationDTO entity = LocationDTO.fromLocation(locationRepository.findById(id));
    if (entity == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    return Response.ok(entity).build();
  }

  @GET
  @Path("/name")
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public Response findLocationByName(@QueryParam("name") String name) {
    LocationDTO entity = LocationDTO.fromLocation(locationRepository.findByName(name));
    if (entity == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    return Response.ok(entity).build();
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public List<LocationDTO> listAll(@QueryParam("page") Integer pageIndex, @QueryParam("pageSize") Integer pageSize) {
    try (DurationLogger perf = DurationLogger.get(LOGGER).start("Find all locations")) {
      return locationRepository.findAll()
            .page(Paging.from(pageIndex, pageSize))
            .stream()
            .map(LocationDTO::fromLocation)
            .collect(Collectors.toList());
    }
  }

  @POST
  @Path("{locationId:[0-9][0-9]*}/addSession")
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public Response addSession(@PathParam("locationId") long locationId, @QueryParam("sessionId") Long sessionId) {
    Location location = locationRepository.findById(locationId);
    if (location == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    Session session = sessionRepository.findById(sessionId);
    if (session == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    session.setLocation(location);
    location.getSessions().add(session);
    locationRepository.persist(location);
    sessionRepository.persist(session);
    LocationDTO dto = LocationDTO.fromLocation(location);
    return Response.ok(dto).build();
  }

  @PUT
  @Path("/{id:[0-9][0-9]*}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Transactional
  public Response update(@PathParam("id") long id, Location entity) {
    if (entity == null) {
      return Response.status(Status.BAD_REQUEST).build();
    }
    if (id != entity.id) {
      return Response.status(Status.CONFLICT).entity(entity).build();
    }
    if (locationRepository.findById(id) == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    try {
      locationRepository.persist(entity);
    } catch (OptimisticLockException e) {
      return Response.status(Response.Status.CONFLICT).entity(e.getEntity()).build();
    }
    return Response.noContent().build();
  }
}
