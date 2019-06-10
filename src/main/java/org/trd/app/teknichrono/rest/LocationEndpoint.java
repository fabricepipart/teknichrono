package org.trd.app.teknichrono.rest;

import org.jboss.logging.Logger;
import org.trd.app.teknichrono.model.dto.LocationDTO;
import org.trd.app.teknichrono.model.jpa.Location;
import org.trd.app.teknichrono.model.jpa.LocationRepository;
import org.trd.app.teknichrono.util.DurationLogger;
import org.trd.app.teknichrono.util.exception.ConflictingIdException;
import org.trd.app.teknichrono.util.exception.MissingIdException;
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

  @Inject
  public LocationEndpoint(LocationRepository locationRepository) {
    this.locationRepository = locationRepository;
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Transactional
  public Response create(LocationDTO entity) {
    try (DurationLogger perf = DurationLogger.get(LOGGER).start("Create location " + entity.getName())) {
      try {
        locationRepository.create(entity);
      } catch (NotFoundException e) {
        return Response.status(Status.NOT_FOUND).build();
      } catch (ConflictingIdException e) {
        return Response.status(Status.CONFLICT).build();
      }
      UriBuilder path = UriBuilder.fromResource(CategoryEndpoint.class).path(String.valueOf(entity.getId()));
      Response response = Response.created(path.build()).build();
      return response;
    }
  }

  @DELETE
  @Path("/{id:[0-9][0-9]*}")
  @Transactional
  public Response deleteById(@PathParam("id") long id) {
    try (DurationLogger perf = DurationLogger.get(LOGGER).start("Delete location id=" + id)) {
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
    try (DurationLogger perf = DurationLogger.get(LOGGER).start("Find location id=" + id)) {
      Location entity = locationRepository.findById(id);
      if (entity == null) {
        return Response.status(Status.NOT_FOUND).build();
      }
      LocationDTO dto = LocationDTO.fromLocation(entity);
      return Response.ok(dto).build();
    }
  }

  @GET
  @Path("/name")
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public Response findLocationByName(@QueryParam("name") String name) {
    try (DurationLogger perf = DurationLogger.get(LOGGER).start("Find location name=" + name)) {
      Location entity = locationRepository.findByName(name);
      if (entity == null) {
        return Response.status(Status.NOT_FOUND).build();
      }
      LocationDTO dto = LocationDTO.fromLocation(entity);
      return Response.ok(dto).build();
    }
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public List<LocationDTO> listAll(@QueryParam("page") Integer pageIndex, @QueryParam("pageSize") Integer pageSize) {
    try (DurationLogger perf = DurationLogger.get(LOGGER).start("Find all locations")) {
      return locationRepository.findAll(pageIndex, pageSize)
          .map(LocationDTO::fromLocation)
          .collect(Collectors.toList());
    }
  }

  @POST
  @Path("{locationId:[0-9][0-9]*}/addSession")
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public Response addSession(@PathParam("locationId") long locationId, @QueryParam("sessionId") Long sessionId) {
    try (DurationLogger perf = DurationLogger.get(LOGGER).start("Add session id=" + sessionId + " to location id=" + locationId)) {
      try {
        LocationDTO dto = locationRepository.addSession(locationId, sessionId);
        return Response.ok(dto).build();
      } catch (NotFoundException e) {
        return Response.status(Status.NOT_FOUND).build();
      }
    }
  }

  @PUT
  @Path("/{id:[0-9][0-9]*}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Transactional
  public Response update(@PathParam("id") long id, LocationDTO dto) {
    try (DurationLogger perf = DurationLogger.get(LOGGER).start("Update category id=" + id)) {
      if (dto == null) {
        return Response.status(Status.BAD_REQUEST).build();
      }
      try {
        locationRepository.update(id, dto);
      } catch (OptimisticLockException e) {
        return Response.status(Response.Status.CONFLICT).entity(e.getEntity()).build();
      } catch (NotFoundException e) {
        return Response.status(Status.NOT_FOUND).build();
      } catch (MissingIdException e) {
        return Response.status(Status.CONFLICT).entity(dto).build();
      }
      return Response.noContent().build();
    }
  }
}
