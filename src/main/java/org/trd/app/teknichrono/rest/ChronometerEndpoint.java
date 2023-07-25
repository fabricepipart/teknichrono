package org.trd.app.teknichrono.rest;

import org.jboss.logging.Logger;
import org.trd.app.teknichrono.model.dto.ChronometerDTO;
import org.trd.app.teknichrono.model.jpa.Chronometer;
import org.trd.app.teknichrono.model.repository.ChronometerRepository;
import org.trd.app.teknichrono.util.DurationLogger;
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

@Path("/chronometers")
public class ChronometerEndpoint {

  private Logger LOGGER = Logger.getLogger(SessionEndpoint.class);

  private ChronometerRepository chronometerRepository;
  private final EntityEndpoint<Chronometer, ChronometerDTO> entityEndpoint;

  @Inject
  public ChronometerEndpoint(ChronometerRepository chronometerRepository) {
    this.chronometerRepository = chronometerRepository;
    this.entityEndpoint = new EntityEndpoint<>(chronometerRepository);
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Transactional
  public Response create(ChronometerDTO entity) {
    return entityEndpoint.create(entity, entity.getName());
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
  public Response findById(@PathParam("id") long id) {
    return entityEndpoint.findById(id);
  }

  @GET
  @Path("/name")
  @Produces(MediaType.APPLICATION_JSON)
  public Response findChronometerByName(@QueryParam("name") String name) {
    return entityEndpoint.findByField("name", name);
  }

  @POST
  @Path("/{id:[0-9][0-9]*}/ack")
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public Response ack(@PathParam("id") long id) {
    try (DurationLogger perf = DurationLogger.get(LOGGER).start("Ack orders of chronometer " + id)) {
      try {
        ChronometerDTO dto = chronometerRepository.ack(id);
        return Response.ok(dto).build();
      } catch (NotFoundException e) {
        return Response.status(Response.Status.NOT_FOUND).build();
      }
    }
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public List<ChronometerDTO> listAll(@QueryParam("page") Integer pageIndex, @QueryParam("pageSize") Integer pageSize) {
    return entityEndpoint.listAll(pageIndex, pageSize);
  }

  @PUT
  @Path("/{id:[0-9][0-9]*}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Transactional
  public Response update(@PathParam("id") long id, ChronometerDTO dto) {
    return entityEndpoint.update(id, dto);
  }
}
