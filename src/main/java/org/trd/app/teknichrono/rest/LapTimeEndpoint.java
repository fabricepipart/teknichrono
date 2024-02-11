package org.trd.app.teknichrono.rest;

import org.trd.app.teknichrono.business.view.LapTimeManager;
import org.trd.app.teknichrono.model.dto.LapTimeDTO;
import org.trd.app.teknichrono.model.jpa.LapTime;
import org.trd.app.teknichrono.model.repository.CategoryRepository;
import org.trd.app.teknichrono.model.repository.EventRepository;
import org.trd.app.teknichrono.model.repository.LapTimeRepository;
import org.trd.app.teknichrono.model.repository.LocationRepository;
import org.trd.app.teknichrono.model.repository.SessionRepository;
import org.trd.app.teknichrono.util.csv.CSVConverter;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import java.io.IOException;
import java.util.List;

@Path("/laptimes")
public class LapTimeEndpoint {

  private final LapTimeManager lapTimeManager;
  private final EntityEndpoint<LapTime, LapTimeDTO> entityEndpoint;

  @Inject
  public LapTimeEndpoint(LapTimeRepository lapTimeRepository, SessionRepository sessionRepository,
                         CategoryRepository categoryRepository, EventRepository eventRepository,
                         LocationRepository locationRepository) {
    this.entityEndpoint = new EntityEndpoint<>(lapTimeRepository);
    this.lapTimeManager = new LapTimeManager(lapTimeRepository, sessionRepository, categoryRepository, eventRepository, locationRepository);
  }

  @DELETE
  @Path("/{id:[0-9][0-9]*}")
  @Transactional
  public Response deleteById(@PathParam("id") long id) {
    return this.entityEndpoint.deleteById(id);
  }

  @GET
  @Path("/{id:[0-9][0-9]*}")
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public Response findById(@PathParam("id") long id) {
    return this.entityEndpoint.findById(id);
  }

  @GET
  @Path("/csv/best")
  @Produces("text/csv")
  @Transactional
  public Response bestToCsv(@QueryParam("pilotId") Long pilotId, @QueryParam("sessionId") Long sessionId,
                            @QueryParam("locationId") Long locationId, @QueryParam("eventId") Long eventId,
                            @QueryParam("categoryId") Long categoryId) {
    try {
      List<LapTimeDTO> results = this.lapTimeManager.bestLapTimes(pilotId, sessionId, locationId, eventId, categoryId, null, null);
      CSVConverter csvConverter = new CSVConverter();
      String csvResults = csvConverter.convertToCsv(results);
      return Response.ok().entity(csvResults).build();
    } catch (IOException e) {
      return Response.serverError().build();
    }
  }

  @GET
  @Path("/best")
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public Response best(@QueryParam("pilotId") Long pilotId, @QueryParam("sessionId") Long sessionId,
                       @QueryParam("locationId") Long locationId, @QueryParam("eventId") Long eventId,
                       @QueryParam("categoryId") Long categoryId, @QueryParam("page") Integer pageIndex,
                       @QueryParam("pageSize") Integer pageSize) {
    try {
      List<LapTimeDTO> lapTimes = this.lapTimeManager.bestLapTimes(pilotId, sessionId, locationId, eventId, categoryId, pageIndex, pageSize);
      return Response.ok().entity(lapTimes).build();
    } catch (IllegalArgumentException e) {
      return Response.status(Status.BAD_REQUEST).entity(e.toString()).build();
    }
  }

  @GET
  @Path("/csv/results")
  @Produces("text/csv")
  @Transactional
  public Response resultsToCsv(@QueryParam("pilotId") Long pilotId, @QueryParam("sessionId") Long sessionId,
                               @QueryParam("locationId") Long locationId, @QueryParam("eventId") Long eventId,
                               @QueryParam("categoryId") Long categoryId) {
    try {
      List<LapTimeDTO> results = this.lapTimeManager.resultsList(pilotId, sessionId, locationId, eventId, categoryId, null, null);
      CSVConverter csvConverter = new CSVConverter();
      String csvResults = csvConverter.convertToCsv(results);
      return Response.ok().entity(csvResults).build();
    } catch (IOException e) {
      return Response.serverError().build();
    }
  }

  @GET
  @Path("/results")
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public Response results(@QueryParam("pilotId") Long pilotId, @QueryParam("sessionId") Long sessionId,
                          @QueryParam("locationId") Long locationId, @QueryParam("eventId") Long eventId,
                          @QueryParam("categoryId") Long categoryId, @QueryParam("page") Integer pageIndex,
                          @QueryParam("pageSize") Integer pageSize) {
    try {
      List<LapTimeDTO> lapTimes = this.lapTimeManager.resultsList(pilotId, sessionId, locationId, eventId, categoryId, pageIndex, pageSize);
      return Response.ok().entity(lapTimes).build();
    } catch (IllegalArgumentException e) {
      return Response.status(Status.BAD_REQUEST).entity(e.toString()).build();
    }
  }

  @GET
  @Path("/csv")
  @Produces("text/csv")
  @Transactional
  public Response listAllToCsv(@QueryParam("pilotId") Long pilotId, @QueryParam("sessionId") Long sessionId,
                               @QueryParam("locationId") Long locationId, @QueryParam("eventId") Long eventId,
                               @QueryParam("categoryId") Long categoryId) {
    try {
      List<LapTimeDTO> results = this.lapTimeManager.all(pilotId, sessionId, locationId, eventId, categoryId, null, null);
      CSVConverter csvConverter = new CSVConverter();
      String csvResults = csvConverter.convertToCsv(results);
      return Response.ok().entity(csvResults).build();
    } catch (IOException e) {
      return Response.serverError().build();
    }
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public Response listAll(@QueryParam("pilotId") Long pilotId, @QueryParam("sessionId") Long sessionId,
                          @QueryParam("locationId") Long locationId, @QueryParam("eventId") Long eventId,
                          @QueryParam("categoryId") Long categoryId, @QueryParam("page") Integer pageIndex,
                          @QueryParam("pageSize") Integer pageSize) {
    try {
      List<LapTimeDTO> lapTimes = this.lapTimeManager.all(pilotId, sessionId, locationId, eventId, categoryId, pageIndex, pageSize);
      return Response.ok().entity(lapTimes).build();
    } catch (IllegalArgumentException e) {
      return Response.status(Status.BAD_REQUEST).entity(e.toString()).build();
    }
  }
}
