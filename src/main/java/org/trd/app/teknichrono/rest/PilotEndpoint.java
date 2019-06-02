package org.trd.app.teknichrono.rest;

import org.trd.app.teknichrono.model.dto.PilotDTO;
import org.trd.app.teknichrono.model.jpa.Beacon;
import org.trd.app.teknichrono.model.jpa.BeaconRepository;
import org.trd.app.teknichrono.model.jpa.Category;
import org.trd.app.teknichrono.model.jpa.CategoryRepository;
import org.trd.app.teknichrono.model.jpa.Pilot;
import org.trd.app.teknichrono.model.jpa.PilotRepository;
import org.trd.app.teknichrono.model.jpa.Session;

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

  private final PilotRepository pilotRepository;

  private final BeaconRepository beaconRepository;

  private final CategoryRepository categoryRepository;

  @Inject
  public PilotEndpoint(PilotRepository pilotRepository, BeaconRepository beaconRepository,
                       CategoryRepository categoryRepository) {
    this.categoryRepository = categoryRepository;
    this.pilotRepository = pilotRepository;
    this.beaconRepository = beaconRepository;
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Transactional
  public Response create(Pilot entity) {
    if (entity.getCurrentBeacon() != null && entity.getCurrentBeacon().id > 0) {
      Beacon beacon = beaconRepository.findById(entity.getCurrentBeacon().id);
      entity.setCurrentBeacon(beacon);
    }
    pilotRepository.persist(entity);
    return Response.created(UriBuilder.fromResource(PilotEndpoint.class).path(String.valueOf(entity.id)).build())
        .build();
  }

  @DELETE
  @Path("/{id:[0-9][0-9]*}")
  @Transactional
  public Response deleteById(@PathParam("id") long id) {
    Pilot entity = pilotRepository.findById(id);
    if (entity == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    for (Session s : entity.getSessions()) {
      s.getPilots().remove(entity);
    }
    pilotRepository.delete(entity);
    return Response.noContent().build();
  }

  @GET
  @Path("/{id:[0-9][0-9]*}")
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public Response findById(@PathParam("id") long id) {
    Pilot entity = pilotRepository.findById(id);
    if (entity == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    PilotDTO dto = PilotDTO.fromPilot(entity);
    return Response.ok(dto).build();
  }

  @GET
  @Path("/name")
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public Response findByName(@QueryParam("firstname") String firstname, @QueryParam("lastname") String lastname) {
    Pilot entity = pilotRepository.findByName(firstname, lastname);
    if (entity == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    PilotDTO dto = PilotDTO.fromPilot(entity);
    return Response.ok(dto).build();
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public List<PilotDTO> listAll(@QueryParam("start") Integer startPosition, @QueryParam("max") Integer maxResult) {
    return pilotRepository.findAll().page(Paging.from(startPosition, maxResult)).stream().map(PilotDTO::fromPilot)
        .collect(Collectors.toList());
  }

  @POST
  @Path("{pilotId:[0-9][0-9]*}/setBeacon")
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public Response associateBeacon(@PathParam("pilotId") long pilotId, @QueryParam("beaconId") long beaconId) {
    Pilot pilot = pilotRepository.findById(pilotId);
    if (pilot == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    Beacon beacon = beaconRepository.findById(beaconId);
    if (beacon == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    pilot.setCurrentBeacon(beacon);
    pilotRepository.persist(pilot);
    beaconRepository.persist(beacon);
    PilotDTO dto = PilotDTO.fromPilot(pilot);
    return Response.ok(dto).build();
  }

  @PUT
  @Path("/{id:[0-9][0-9]*}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Transactional
  public Response update(@PathParam("id") long id, Pilot dto) {
    if (dto == null) {
      return Response.status(Status.BAD_REQUEST).build();
    }
    if (id != dto.id) {
      return Response.status(Status.CONFLICT).entity(dto).build();
    }
    Pilot pilot = pilotRepository.findById(id);
    if (pilot == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    // Update of category
    pilot.setCategory(null);
    if (dto.getCategory() != null && dto.getCategory().getId() != null && dto.getCategory().getId() > 0) {
      Category category = categoryRepository.findById(dto.getCategory().id);
      pilot.setCategory(category);
    }
    // Update of beacon
    pilot.setCurrentBeacon(null);
    if (dto.getCurrentBeacon() != null && dto.getCurrentBeacon().getId() != null
        && dto.getCurrentBeacon().getId() > 0) {
      Beacon beacon = beaconRepository.findById(dto.getCurrentBeacon().id);
      pilot.setCurrentBeacon(beacon);
    }
    pilot.setFirstName(dto.getFirstName());
    pilot.setLastName(dto.getLastName());
    try {
      pilotRepository.persist(pilot);
    } catch (OptimisticLockException e) {
      return Response.status(Response.Status.CONFLICT).entity(e.getEntity()).build();
    }

    return Response.noContent().build();
  }
}
