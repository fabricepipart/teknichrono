package org.trd.app.teknichrono.rest;

import org.jboss.logging.Logger;
import org.trd.app.teknichrono.model.dto.LogDTO;
import org.trd.app.teknichrono.model.dto.NestedChronometerDTO;
import org.trd.app.teknichrono.model.jpa.Chronometer;
import org.trd.app.teknichrono.model.jpa.Log;
import org.trd.app.teknichrono.model.repository.ChronometerRepository;
import org.trd.app.teknichrono.model.repository.LogRepository;
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
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Path("/logs")
public class LogEndpoint {

  private static final Logger LOGGER = Logger.getLogger(LogEndpoint.class);

  private final EntityEndpoint entityEndpoint;
  private final ChronometerRepository chronometerRepository;
  private final LogRepository logRepository;

  @Inject
  public LogEndpoint(LogRepository logRepository, ChronometerRepository chronometerRepository) {
    this.entityEndpoint = new EntityEndpoint(logRepository);
    this.logRepository = logRepository;
    this.chronometerRepository = chronometerRepository;
  }


  @POST
  @Path("/create")
  @Consumes(MediaType.APPLICATION_JSON)
  @Transactional
  public Response create(LogDTO dto, @QueryParam("chronoId") long chronoId) {
    try (DurationLogger perf = DurationLogger.get(LOGGER).start("Creating log of chronometer " + chronoId)) {
      try {
        if (chronoId <= 0) {
          return Response.status(Response.Status.BAD_REQUEST).build();
        }
        NestedChronometerDTO chronometer = new NestedChronometerDTO();
        chronometer.setId(chronoId);
        dto.setChronometer(chronometer);
        this.logRepository.create(dto);
        return Response.noContent().build();
      } catch (NotFoundException e) {
        return Response.status(Response.Status.NOT_FOUND).build();
      } catch (ConflictingIdException e) {
        return Response.status(Response.Status.BAD_REQUEST).build();
      }
    }
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
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public Response listAll(@QueryParam("chronoId") Long chronoId,
                          @QueryParam("from") String from, @QueryParam("to") String to,
                          @QueryParam("page") Integer pageIndex, @QueryParam("pageSize") Integer pageSize) {
    try (DurationLogger dl = DurationLogger.get(LOGGER).start("Find all logs of chronometer " + chronoId)) {
      if (chronoId == null || chronoId.longValue() <= 0) {
        LOGGER.error("Chronometer ID required to get logs");
        return Response.status(Response.Status.BAD_REQUEST).build();
      }
      Chronometer chronometer = this.chronometerRepository.findById(chronoId);
      Instant fromInstant = from != null ? Instant.parse(from) : Instant.now().minus(1, ChronoUnit.DAYS);
      Instant toInstant = to != null ? Instant.parse(to) : Instant.now();
      try (Stream<Log> s = this.logRepository.findLogs(chronometer, fromInstant, toInstant, pageIndex, pageSize)) {
        return Response.ok(s.map(this.logRepository::toDTO).collect(Collectors.toList())).build();
      }
    }
  }

  @PUT
  @Path("/{id:[0-9][0-9]*}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Transactional
  public Response update(@PathParam("id") long id, LogDTO dto) {
    return this.entityEndpoint.update(id, dto);
  }

}
