package org.trd.app.teknichrono.rest;

import org.trd.app.teknichrono.model.dto.CategoryDTO;
import org.trd.app.teknichrono.model.jpa.Category;
import org.trd.app.teknichrono.model.jpa.Pilot;
import org.trd.app.teknichrono.model.repository.CategoryRepository;

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

@Path("/categories")
public class CategoryEndpoint {

  private final EntityEndpoint<Category, CategoryDTO> entityEndpoint;
  private final CategoryRepository categoryRepository;

  @Inject
  public CategoryEndpoint(CategoryRepository categoryRepository) {
    this.categoryRepository = categoryRepository;
    this.entityEndpoint = new EntityEndpoint<>(categoryRepository);
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Transactional
  public Response create(CategoryDTO entity) {
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
  @Transactional
  public Response findById(@PathParam("id") long id) {
    return entityEndpoint.findById(id);
  }

  @GET
  @Path("/name")
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public Response findCategoryByName(@QueryParam("name") String name) {
    return entityEndpoint.findByField("name", name);
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public List<CategoryDTO> listAll(@QueryParam("page") Integer pageIndex, @QueryParam("pageSize") Integer pageSize) {
    return entityEndpoint.listAll(pageIndex, pageSize);
  }

  @POST
  @Path("{categoryId:[0-9][0-9]*}/addPilot")
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public Response addPilot(@PathParam("categoryId") long categoryId, @QueryParam("pilotId") Long pilotId) {
    return entityEndpoint.addToOneToManyField(categoryId, pilotId, categoryRepository.getPilotRepository(),
        Pilot::setCategory, Category::getPilots);
  }

  @PUT
  @Path("/{id:[0-9][0-9]*}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Transactional
  public Response update(@PathParam("id") long id, CategoryDTO dto) {
    return entityEndpoint.update(id, dto);
  }
}
