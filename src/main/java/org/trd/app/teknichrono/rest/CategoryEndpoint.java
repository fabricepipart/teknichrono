package org.trd.app.teknichrono.rest;

import org.jboss.logging.Logger;
import org.trd.app.teknichrono.model.dto.CategoryDTO;
import org.trd.app.teknichrono.model.jpa.Category;
import org.trd.app.teknichrono.service.CategoryService;
import org.trd.app.teknichrono.util.DurationLogger;
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

@Path("/categories")
public class CategoryEndpoint {

  private static final Logger LOGGER = Logger.getLogger(CategoryEndpoint.class);

  private final CategoryService categoryService;

  @Inject
  public CategoryEndpoint(CategoryService categoryService) {
    this.categoryService = categoryService;
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Transactional
  public Response create(Category entity) {
    try (DurationLogger perf = DurationLogger.get(LOGGER).start("Create category " + entity.getName())) {
      categoryService.create(entity);
      UriBuilder path = UriBuilder.fromResource(CategoryEndpoint.class).path(String.valueOf(entity.id));
      Response response = Response.created(path.build()).build();
      return response;
    }
  }

  @DELETE
  @Path("/{id:[0-9][0-9]*}")
  @Transactional
  public Response deleteById(@PathParam("id") long id) {
    try (DurationLogger perf = DurationLogger.get(LOGGER).start("Delete category id=" + id)) {
      try {
        categoryService.deleteById(id);
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
    try (DurationLogger perf = DurationLogger.get(LOGGER).start("Find category id=" + id)) {
      Category entity = categoryService.findById(id);
      if (entity == null) {
        return Response.status(Status.NOT_FOUND).build();
      }
      CategoryDTO dto = CategoryDTO.fromCategory(entity);
      return Response.ok(dto).build();
    }
  }

  @GET
  @Path("/name")
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public Response findCategoryByName(@QueryParam("name") String name) {
    try (DurationLogger perf = DurationLogger.get(LOGGER).start("Find category name=" + name)) {
      Category entity = categoryService.findByName(name);
      if (entity == null) {
        return Response.status(Status.NOT_FOUND).build();
      }
      CategoryDTO dto = CategoryDTO.fromCategory(entity);
      return Response.ok(dto).build();
    }
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public List<CategoryDTO> listAll(@QueryParam("start") Integer startPosition, @QueryParam("max") Integer maxResult) {
    try (DurationLogger perf = DurationLogger.get(LOGGER).start("Find all categories")) {
      return categoryService.findAll(startPosition, maxResult);
    }
  }

  @POST
  @Path("{categoryId:[0-9][0-9]*}/addPilot")
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public Response addPilot(@PathParam("categoryId") long categoryId, @QueryParam("pilotId") Long pilotId) {
    try (DurationLogger perf = DurationLogger.get(LOGGER).start("Add pilot id=" + pilotId + " to category id=" + categoryId)) {
      try {
        CategoryDTO dto = categoryService.addPilot(categoryId, pilotId);
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
  public Response update(@PathParam("id") long id, CategoryDTO dto) {
    try (DurationLogger perf = DurationLogger.get(LOGGER).start("Update category id=" + id)) {
      if (dto == null) {
        return Response.status(Status.BAD_REQUEST).build();
      }
      try {
        categoryService.update(id, dto);
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
