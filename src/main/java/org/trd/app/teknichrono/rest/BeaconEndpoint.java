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

import org.trd.app.teknichrono.model.Beacon;
import org.trd.app.teknichrono.model.Pilot;

/**
 * 
 */
@Stateless(name = "beacons")
@Path("/beacons")
public class BeaconEndpoint {
	@PersistenceContext(unitName = "teknichrono-persistence-unit")
	private EntityManager em;

	@POST
	@Consumes("application/json")
	public Response create(Beacon entity) {
		em.persist(entity);
		return Response
				.created(UriBuilder.fromResource(BeaconEndpoint.class).path(String.valueOf(entity.getId())).build())
				.build();
	}

	@DELETE
	@Path("/{id:[0-9][0-9]*}")
	public Response deleteById(@PathParam("id") int id) {
		Beacon entity = em.find(Beacon.class, id);
		if (entity == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		Pilot associatedPilot = entity.getPilot();
		if (associatedPilot != null) {
			associatedPilot.setCurrentBeacon(null);
			em.persist(associatedPilot);
		}

		em.remove(entity);
		return Response.noContent().build();
	}

	@GET
	@Path("/{id:[0-9][0-9]*}")
	@Produces("application/json")
	public Response findById(@PathParam("id") int id) {
		Beacon entity = findBeacon(id);
		if (entity == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		return Response.ok(entity).build();
	}

	public Beacon findBeacon(int id) {
		TypedQuery<Beacon> findByIdQuery = em
				.createQuery("SELECT DISTINCT b FROM Beacon b WHERE b.id = :entityId ORDER BY b.id", Beacon.class);
		findByIdQuery.setParameter("entityId", id);
		Beacon entity;
		try {
			entity = findByIdQuery.getSingleResult();
		} catch (NoResultException nre) {
			entity = null;
		}
		return entity;
	}

	@GET
	@Path("/number/{number:[0-9][0-9]*}")
	@Produces("application/json")
	public Beacon findBeaconNumber(@PathParam("number") int number) {
		TypedQuery<Beacon> findByIdQuery = em.createQuery(
				"SELECT DISTINCT b FROM Beacon b WHERE b.number = :entityId ORDER BY b.number", Beacon.class);
		findByIdQuery.setParameter("entityId", number);
		Beacon entity;
		try {
			entity = findByIdQuery.getSingleResult();
		} catch (NoResultException nre) {
			entity = null;
		}
		return entity;
	}

	@GET
	@Produces("application/json")
	public List<Beacon> listAll(@QueryParam("start") Integer startPosition, @QueryParam("max") Integer maxResult) {
		TypedQuery<Beacon> findAllQuery = em.createQuery("SELECT DISTINCT b FROM Beacon b ORDER BY b.id", Beacon.class);
		if (startPosition != null) {
			findAllQuery.setFirstResult(startPosition);
		}
		if (maxResult != null) {
			findAllQuery.setMaxResults(maxResult);
		}
		final List<Beacon> results = findAllQuery.getResultList();
		return results;
	}

	@PUT
	@Path("/{id:[0-9][0-9]*}")
	@Consumes("application/json")
	public Response update(@PathParam("id") int id, Beacon entity) {
		if (entity == null) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		if (id != entity.getId()) {
			return Response.status(Status.CONFLICT).entity(entity).build();
		}
		if (em.find(Beacon.class, id) == null) {
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
