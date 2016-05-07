package org.trd.app.teknichrono.pi.rest;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.trd.app.teknichrono.pi.model.PiLap;

/**
 * 
 */
@Stateless
@Path("/laptimes")
public class LapEndpoint {
	@PersistenceContext(unitName = "teknichrono-persistence-unit")
	private EntityManager em;

	@GET
	@Path("/{beaconId:[0-9][0-9]*}")
	@Produces("application/json")
	public Response findById(@PathParam("beaconId") int beaconId) {
		TypedQuery<PiLap> findByIdQuery = em
				.createQuery("SELECT DISTINCT l FROM Lap l WHERE l.id = :entityId ORDER BY l.id", PiLap.class);
		findByIdQuery.setParameter("entityId", beaconId);
		PiLap entity;
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
	@Produces("application/json")
	public List<PiLap> listAll(@QueryParam("start") Integer startPosition, @QueryParam("max") Integer maxResult) {
		TypedQuery<PiLap> findAllQuery = em.createQuery("SELECT DISTINCT l FROM Lap l ORDER BY l.id", PiLap.class);
		if (startPosition != null) {
			findAllQuery.setFirstResult(startPosition);
		}
		if (maxResult != null) {
			findAllQuery.setMaxResults(maxResult);
		}
		final List<PiLap> results = findAllQuery.getResultList();
		return results;
	}

	@PUT
	@Path("/{id:[0-9][0-9]*}")
	@Consumes("application/json")
	public Response update(@PathParam("id") int id, PiLap entity) {
		if (entity == null) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		if (id != entity.getId()) {
			return Response.status(Status.CONFLICT).entity(entity).build();
		}
		if (em.find(PiLap.class, id) == null) {
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
