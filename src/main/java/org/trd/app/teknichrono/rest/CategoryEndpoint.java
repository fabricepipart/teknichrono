package org.trd.app.teknichrono.rest;

import org.trd.app.teknichrono.model.dto.CategoryDTO;
import org.trd.app.teknichrono.model.jpa.Category;
import org.trd.app.teknichrono.model.jpa.Pilot;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.OptimisticLockException;
import javax.persistence.TypedQuery;
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

/**
 * 
 */
@Path("/categories")
public class CategoryEndpoint {

  private final EntityManager em;

  @Inject
  public CategoryEndpoint(EntityManager em) {
    this.em = em;
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Transactional
  public Response create(Category entity) {
    em.persist(entity);
    return Response
        .created(UriBuilder.fromResource(CategoryEndpoint.class).path(String.valueOf(entity.id)).build()).build();
  }

  @DELETE
  @Path("/{id:[0-9][0-9]*}")
  @Transactional
  public Response deleteById(@PathParam("id") long id) {
    Category entity = em.find(Category.class, id);
    if (entity == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    em.remove(entity);
    return Response.noContent().build();
  }

  @GET
  @Path("/{id:[0-9][0-9]*}")
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public Response findById(@PathParam("id") long id) {
    TypedQuery<Category> findByIdQuery = em.createQuery(
        "SELECT DISTINCT e FROM Category e LEFT JOIN FETCH e.pilots WHERE e.id = :entityId ORDER BY e.id",
        Category.class);
    findByIdQuery.setParameter("entityId", id);
    Category entity;
    try {
      entity = findByIdQuery.getSingleResult();
    } catch (NoResultException nre) {
      entity = null;
    }
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
  public CategoryDTO findCategoryByName(@QueryParam("name") String name) {
    TypedQuery<Category> findByNameQuery = em.createQuery(
        "SELECT DISTINCT e FROM Category e LEFT JOIN FETCH e.pilots WHERE e.name = :name ORDER BY e.id",
        Category.class);
    findByNameQuery.setParameter("name", name);
    Category entity;
    try {
      entity = findByNameQuery.getSingleResult();
    } catch (NoResultException nre) {
      entity = null;
    }
    return CategoryDTO.fromCategory(entity);
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public List<CategoryDTO> listAll(@QueryParam("start") Integer startPosition, @QueryParam("max") Integer maxResult) {
    TypedQuery<Category> findAllQuery = em
        .createQuery("SELECT DISTINCT e FROM Category e LEFT JOIN FETCH e.pilots ORDER BY e.id", Category.class);
    if (startPosition != null) {
      findAllQuery.setFirstResult(startPosition);
    }
    if (maxResult != null) {
      findAllQuery.setMaxResults(maxResult);
    }
    return findAllQuery.getResultStream()
            .map(CategoryDTO::fromCategory)
            .collect(Collectors.toList());
  }

  @POST
  @Path("{categoryId:[0-9][0-9]*}/addPilot")
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public Response addPilot(@PathParam("categoryId") long categoryId, @QueryParam("pilotId") Long pilotId) {
    Category category = em.find(Category.class, categoryId);
    if (category == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    Pilot pilot = em.find(Pilot.class, pilotId);
    if (pilot == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    pilot.setCategory(category);
    category.getPilots().add(pilot);
    em.persist(category);
    for (Pilot c : category.getPilots()) {
      em.persist(c);
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
    Category category = em.find(Category.class, id);
    if (category == null) {
      return Response.status(Status.NOT_FOUND).build();
    }

    category = dto.fromDTO(category, em);
    try {
      em.merge(category);
    } catch (OptimisticLockException e) {
      return Response.status(Response.Status.CONFLICT).entity(e.getEntity()).build();
    }

    return Response.noContent().build();
  }
}
