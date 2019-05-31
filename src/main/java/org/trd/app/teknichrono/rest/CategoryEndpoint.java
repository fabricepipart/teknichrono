package org.trd.app.teknichrono.rest;

import org.trd.app.teknichrono.model.dto.CategoryDTO;
import org.trd.app.teknichrono.model.dto.NestedPilotDTO;
import org.trd.app.teknichrono.model.jpa.Category;
import org.trd.app.teknichrono.model.jpa.CategoryRepository;
import org.trd.app.teknichrono.model.jpa.Pilot;
import org.trd.app.teknichrono.model.jpa.PilotRepository;

import javax.inject.Inject;
import javax.persistence.EntityManager;
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
import java.util.Set;
import java.util.stream.Collectors;

@Path("/categories")
public class CategoryEndpoint {

  // TODO, get rid of the em (still needed for the fromDTO)
  private final EntityManager em;

  private final CategoryRepository categoryRespository;

  private final PilotRepository pilotRespository;

  @Inject
  public CategoryEndpoint(EntityManager em, CategoryRepository categoryRespository, PilotRepository pilotRespository) {
    this.em = em;
    this.categoryRespository = categoryRespository;
    this.pilotRespository = pilotRespository;
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Transactional
  public Response create(Category entity) {
    categoryRespository.persist(entity);
    return Response
        .created(UriBuilder.fromResource(CategoryEndpoint.class).path(String.valueOf(entity.id)).build()).build();
  }

  @DELETE
  @Path("/{id:[0-9][0-9]*}")
  @Transactional
  public Response deleteById(@PathParam("id") long id) {
    Category entity = categoryRespository.findById(id);
    if (entity == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    Set<Pilot> pilots = entity.getPilots();
    if (pilots != null) {
      for (Pilot pilot : pilots) {
        pilot.setCategory(null);
        pilotRespository.persist(pilot);
      }
    }
    categoryRespository.delete(entity);
    return Response.noContent().build();
  }

  @GET
  @Path("/{id:[0-9][0-9]*}")
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public Response findById(@PathParam("id") long id) {
    Category entity = categoryRespository.findById(id);
    if (entity == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    CategoryDTO dto = CategoryDTO.fromCategory(entity);
    return Response.ok(dto).build();
  }

  @GET
  @Path("/name")
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public Response findCategoryByName(@QueryParam("name") String name) {
    Category entity = categoryRespository.findByName(name);
    if (entity == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    CategoryDTO dto = CategoryDTO.fromCategory(entity);
    return Response.ok(dto).build();
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public List<CategoryDTO> listAll(@QueryParam("start") Integer startPosition, @QueryParam("max") Integer maxResult) {
    return categoryRespository.findAll()
        .page(Paging.from(startPosition, maxResult))
        .stream()
        .map(CategoryDTO::fromCategory)
        .collect(Collectors.toList());
  }

  @POST
  @Path("{categoryId:[0-9][0-9]*}/addPilot")
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public Response addPilot(@PathParam("categoryId") long categoryId, @QueryParam("pilotId") Long pilotId) {
    Category category = categoryRespository.findById(categoryId);
    if (category == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    Pilot pilot = pilotRespository.findById(pilotId);
    if (pilot == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    pilot.setCategory(category);
    category.getPilots().add(pilot);
    categoryRespository.persist(category);
    for (Pilot c : category.getPilots()) {
      pilotRespository.persist(c);
    }
    CategoryDTO dto = CategoryDTO.fromCategory(category);
    return Response.ok(dto).build();
  }

  @PUT
  @Path("/{id:[0-9][0-9]*}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Transactional
  public Response update(@PathParam("id") long id, CategoryDTO dto) {
    if (dto == null) {
      return Response.status(Status.BAD_REQUEST).build();
    }
    if (id != dto.getId()) {
      return Response.status(Status.CONFLICT).entity(dto).build();
    }
    Category category = categoryRespository.findById(id);
    if (category == null) {
      return Response.status(Status.NOT_FOUND).build();
    }

    category.setName(dto.getName());
    if (dto.getPilots() != null && dto.getPilots().size() > 0) {
      for (NestedPilotDTO p : dto.getPilots()) {
        Pilot pilot = pilotRespository.findById(p.getId());
        category.getPilots().add(pilot);
      }
    }

    try {
      categoryRespository.persist(category);
    } catch (OptimisticLockException e) {
      return Response.status(Response.Status.CONFLICT).entity(e.getEntity()).build();
    }

    return Response.noContent().build();
  }
}
