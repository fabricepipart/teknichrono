package org.trd.app.teknichrono.rest;

import org.jboss.logging.Logger;
import org.trd.app.teknichrono.model.dto.PilotDTO;
import org.trd.app.teknichrono.model.jpa.Pilot;
import org.trd.app.teknichrono.model.jpa.PilotRepository;
import org.trd.app.teknichrono.util.DurationLogger;
import org.trd.app.teknichrono.util.exception.ConflictingIdException;
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

@Path("/pilots")
public class PilotEndpoint {

  private static final Logger LOGGER = Logger.getLogger(PilotEndpoint.class);

  private final PilotRepository pilotRepository;

  @Inject
  public PilotEndpoint(PilotRepository pilotRepository) {
    this.pilotRepository = pilotRepository;
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Transactional
  public Response create(PilotDTO entity) {
    try (DurationLogger perf = DurationLogger.get(LOGGER).start("Create pilot " + entity.getFirstName() + " " + entity.getLastName())) {
      try {
        pilotRepository.create(entity);
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
    try (DurationLogger perf = DurationLogger.get(LOGGER).start("Delete pilot id=" + id)) {
      try {
        pilotRepository.deleteById(id);
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
    try (DurationLogger perf = DurationLogger.get(LOGGER).start("Find pilot id=" + id)) {
      Pilot entity = pilotRepository.findById(id);
      if (entity == null) {
        return Response.status(Status.NOT_FOUND).build();
      }
      PilotDTO dto = PilotDTO.fromPilot(entity);
      return Response.ok(dto).build();
    }
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
    try (DurationLogger perf = DurationLogger.get(LOGGER).start("Find all pilots")) {
      return pilotRepository.findAll(pageIndex, pageSize).map(PilotDTO::fromPilot).collect(Collectors.toList());
    }
  }

  @POST
  @Path("{pilotId:[0-9][0-9]*}/setBeacon")
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public Response associateBeacon(@PathParam("pilotId") long pilotId, @QueryParam("beaconId") long beaconId) {
    try (DurationLogger perf = DurationLogger.get(LOGGER).start("Associate pilot id=" + pilotId + " to beacon id=" + beaconId)) {
      try {
        PilotDTO dto = pilotRepository.associateBeacon(pilotId, beaconId);
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
  public Response update(@PathParam("id") long id, PilotDTO dto) {
    try (DurationLogger perf = DurationLogger.get(LOGGER).start("Update category id=" + id)) {
      if (dto == null) {
        return Response.status(Status.BAD_REQUEST).build();
      }
      try {
        pilotRepository.update(id, dto);
      } catch (OptimisticLockException e) {
        return Response.status(Response.Status.CONFLICT).entity(e.getEntity()).build();
      } catch (NotFoundException e) {
        return Response.status(Status.NOT_FOUND).build();
      } catch (ConflictingIdException e) {
        return Response.status(Status.CONFLICT).entity(dto).build();
      }
      return Response.noContent().build();
    }
  }
}
