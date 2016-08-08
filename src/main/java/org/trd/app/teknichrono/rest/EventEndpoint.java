package org.trd.app.teknichrono.rest;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;

import org.trd.app.teknichrono.model.Chronometer;
import org.trd.app.teknichrono.model.Event;

/**
 * 
 */
@Stateless
@Path("/events")
public class EventEndpoint {
	@PersistenceContext(unitName = "teknichrono-persistence-unit")
	private EntityManager em;

	@POST
	@Consumes("application/json")
	public Response create(Event entity) {
		em.persist(entity);
		return Response
				.created(UriBuilder.fromResource(EventEndpoint.class).path(String.valueOf(entity.getId())).build())
				.build();
	}

	@DELETE
	@Path("/{id:[0-9][0-9]*}")
	public Response deleteById(@PathParam("id") int id) {
		Event entity = em.find(Event.class, id);
		if (entity == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		em.remove(entity);
		return Response.noContent().build();
	}

	@GET
	@Path("/{id:[0-9][0-9]*}")
	@Produces("application/json")
	public Response findById(@PathParam("id") int id) {
		TypedQuery<Event> findByIdQuery = em.createQuery(
				"SELECT DISTINCT e FROM Event e LEFT JOIN FETCH e.chronometers WHERE e.id = :entityId ORDER BY e.id",
				Event.class);
		findByIdQuery.setParameter("entityId", id);
		Event entity;
		try {
			entity = findByIdQuery.getSingleResult();
		} catch (NoResultException nre) {
			entity = null;
		}
		if (entity == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		return Response.ok(entity).build();
	}

	@GET
	@Path("/name")
	@Produces("application/json")
	public Event findEventByName(@QueryParam("name") String name) {
		TypedQuery<Event> findByNameQuery = em.createQuery(
				"SELECT DISTINCT e FROM Event e LEFT JOIN FETCH e.chronometers WHERE e.name = :name ORDER BY e.id",
				Event.class);
		findByNameQuery.setParameter("name", name);
		Event entity;
		try {
			entity = findByNameQuery.getSingleResult();
		} catch (NoResultException nre) {
			entity = null;
		}
		return entity;
	}

	@GET
	@Produces("application/json")
	public List<Event> listAll(@QueryParam("start") Integer startPosition, @QueryParam("max") Integer maxResult) {
		TypedQuery<Event> findAllQuery = em.createQuery(
				"SELECT DISTINCT e FROM Event e LEFT JOIN FETCH e.chronometers ORDER BY e.id", Event.class);
		if (startPosition != null) {
			findAllQuery.setFirstResult(startPosition);
		}
		if (maxResult != null) {
			findAllQuery.setMaxResults(maxResult);
		}
		final List<Event> results = findAllQuery.getResultList();
		return results;
	}

	@POST
	@Path("{eventId:[0-9][0-9]*}/addChronometer")
	@Produces("application/json")
	public Response addChronometer(@PathParam("eventId") int eventId, @QueryParam("chronoId") Integer chronoId,
			@QueryParam("index") Integer index) {
		Event event = em.find(Event.class, eventId);
		if (event == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		Chronometer chronometer = em.find(Chronometer.class, chronoId);
		if (chronometer == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		if (index != null) {
			chronometer.setChronoIndex(index);
		}
		event.addChronometer(chronometer);
		em.persist(event);
		for (Chronometer c : event.getChronometers()) {
			em.persist(c);
		}

		return Response.ok(event).build();
	}

	@PUT
	@Path("/{id:[0-9][0-9]*}")
	@Consumes("application/json")
	public Response update(@PathParam("id") int id, Event entity) {
		if (entity == null) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		if (id != entity.getId()) {
			return Response.status(Status.CONFLICT).entity(entity).build();
		}
		if (em.find(Event.class, id) == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		try {
			entity = em.merge(entity);
		} catch (OptimisticLockException e) {
			return Response.status(Response.Status.CONFLICT).entity(e.getEntity()).build();
		}

		return Response.noContent().build();
	}
}
