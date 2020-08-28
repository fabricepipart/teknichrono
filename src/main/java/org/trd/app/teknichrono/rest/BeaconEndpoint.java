package org.trd.app.teknichrono.rest;

import org.trd.app.teknichrono.model.dto.BeaconDTO;
import org.trd.app.teknichrono.model.jpa.Beacon;
import org.trd.app.teknichrono.model.repository.BeaconRepository;

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

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;


@Path("/beacons")
public class BeaconEndpoint {

  private final EntityEndpoint<Beacon, BeaconDTO> entityEndpoint;

  @Inject
  public BeaconEndpoint(BeaconRepository beaconRepository) {
    this.entityEndpoint = new EntityEndpoint(beaconRepository);
  }


  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Transactional
  public Response create(BeaconDTO entity) {
    return entityEndpoint.create(entity, String.valueOf(entity.getNumber()));
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
  @Path("/number/{number:[0-9][0-9]*}")
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  @APIResponses(
        value = {
            @APIResponse(
                responseCode = "404",
                description = "Unknown ID specified",
                content = @Content(mediaType = "text/plain")),
            @APIResponse(
                responseCode = "200",
                description = "Details of Beacon for given ID",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Beacon.class))) })
    @Operation(
        summary = "Get the description of a beacon",
        description = "Get the description of a beacon, explained longer")
  public Response findBeaconNumber(
    @Parameter(
            description = "The ID of the beacon you want to get",
            required = true,
            example = "1234")
    @PathParam("number") long number) {
    return entityEndpoint.findByField("number", number);
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public List<BeaconDTO> listAll(@QueryParam("page") Integer pageIndex, @QueryParam("pageSize") Integer pageSize) {
    return entityEndpoint.listAll(pageIndex, pageSize);
  }

  @PUT
  @Path("/{id:[0-9][0-9]*}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Transactional
  public Response update(@PathParam("id") long id, BeaconDTO dto) {
    return entityEndpoint.update(id, dto);
  }

}
