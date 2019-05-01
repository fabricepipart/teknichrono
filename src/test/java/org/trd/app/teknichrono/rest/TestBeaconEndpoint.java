package org.trd.app.teknichrono.rest;

import org.jboss.logging.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.trd.app.teknichrono.model.dto.BeaconDTO;
import org.trd.app.teknichrono.model.jpa.Beacon;
import org.trd.app.teknichrono.model.jpa.Pilot;
import org.trd.app.teknichrono.model.jpa.Ping;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.OptimisticLockException;
import javax.persistence.TypedQuery;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.ext.RuntimeDelegate;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TestBeaconEndpoint {

  private int id = 1;
  @Mock
  private TypedQuery<Beacon> query;
  @Mock
  private RuntimeDelegate runtimeDelegate;
  @Mock
  private Response.ResponseBuilder responseBuilder;
  @Mock
  private Response response;

  @Mock
  private UriBuilder uriBuilder;

  private URI uri;

  @Mock
  private Logger logger;

  @Mock
  private EntityManager em;

  @InjectMocks
  private BeaconEndpoint endpoint;

  @Before
  public void setUp() throws Exception {
    uri = new URI("");
    RuntimeDelegate.setInstance(runtimeDelegate);
    when(runtimeDelegate.createUriBuilder()).thenReturn(uriBuilder);
    when(uriBuilder.path(any(Class.class))).thenReturn(uriBuilder);
    when(uriBuilder.path(anyString())).thenReturn(uriBuilder);
    when(uriBuilder.build()).thenReturn(uri);
    when(runtimeDelegate.createResponseBuilder()).thenReturn(responseBuilder);
    when(responseBuilder.status(any(Response.StatusType.class))).thenReturn(responseBuilder);
    when(responseBuilder.location(uri)).thenReturn(responseBuilder);
    when(responseBuilder.build()).thenReturn(response);
    when(responseBuilder.entity(any())).thenReturn(responseBuilder);
    when(em.createQuery(anyString(), eq(Beacon.class))).thenReturn(query);
  }

  @Test
  public void createsBeacon() {
    Beacon entity = newBeacon(999, -1);
    Response r = endpoint.create(entity);
    verify(em).persist(entity);
    Assert.assertEquals(response, r);
  }

  @Test
  public void createsBeaconWithCompletePilotIfIdProvided() {
    Beacon entity = newBeacon(999, 9);
    Response r = endpoint.create(entity);
    verify(em).find(Pilot.class, 9);
    verify(em).persist(entity);
  }

  public Beacon newBeacon(int number, int pilotId) {
    Beacon beacon = new Beacon();
    beacon.setId(id++);
    beacon.setNumber(number);
    if (pilotId >= 0) {
      Pilot p = new Pilot();
      p.setId(pilotId);
      beacon.setPilot(p);
      p.setFirstName("First");
      p.setLastName("Last");
    }
    for (int i = 0; i < 10; i++) {
      Ping p = new Ping();
      p.setId(id++);
      p.setBeacon(beacon);
      beacon.getPings().add(p);
    }
    return beacon;
  }

  @Test
  public void deleteById() {
    Beacon entity = newBeacon(999, 9);
    when(em.find(Beacon.class, entity.getId())).thenReturn(entity);
    Response r = endpoint.deleteById(entity.getId());
    verify(em).remove(entity);
  }

  @Test
  public void deleteByIdRemovesBeaconFromPilot() {
    Beacon entity = newBeacon(999, 9);
    when(em.find(Beacon.class, entity.getId())).thenReturn(entity);
    Response r = endpoint.deleteById(entity.getId());
    verify(em).remove(entity);
    ArgumentCaptor<Pilot> captor = ArgumentCaptor.forClass(Pilot.class);
    verify(em, atLeastOnce()).persist(captor.capture());
    List values = captor.getAllValues();
    values.removeIf(e -> !(e instanceof Pilot));
    List<Pilot> pilotValues = (List<Pilot>) values;
    for (Pilot p : pilotValues) {
      Assert.assertNull(p.getCurrentBeacon());
    }
  }

  @Test
  public void deleteByIdRemovesBeaconFromPings() {
    Beacon entity = newBeacon(999, 9);
    when(em.find(Beacon.class, entity.getId())).thenReturn(entity);
    Response r = endpoint.deleteById(entity.getId());
    verify(em).remove(entity);
    ArgumentCaptor captor = ArgumentCaptor.forClass(Ping.class);
    verify(em, atLeastOnce()).persist(captor.capture());
    List values = captor.getAllValues();
    values.removeIf(e -> !(e instanceof Ping));
    List<Ping> pingValues = (List<Ping>) values;
    Assert.assertEquals(entity.getPings().size(), pingValues.size());
    for (Ping p : pingValues) {
      Assert.assertNull(p.getBeacon());
    }
  }

  @Test
  public void deleteByIdReturnsErrorIfBeaconDoesNotExist() {
    Beacon entity = newBeacon(999, 9);
    Response r = endpoint.deleteById(entity.getId());
    verify(em, never()).remove(any());
  }

  @Test
  public void findById() {
    Beacon entity = newBeacon(999, 9);
    when(query.getSingleResult()).thenReturn(entity);
    Response r = endpoint.findById(999);
    Assert.assertNotNull(r);
    ArgumentCaptor<BeaconDTO> captor = ArgumentCaptor.forClass(BeaconDTO.class);
    verify(responseBuilder).entity(captor.capture());
    BeaconDTO dto = captor.getValue();
    Assert.assertEquals(999, dto.getNumber());
    Assert.assertEquals(9, dto.getPilot().getId());
  }

  @Test
  public void findByIdReturnsNullIfNotFound() {
    Beacon entity = newBeacon(999, 9);
    when(query.getSingleResult()).thenThrow(new NoResultException());
    Response r = endpoint.findById(999);
    Assert.assertNotNull(r);
    verify(responseBuilder, never()).entity(any());
    verify(responseBuilder).status((Response.StatusType) javax.ws.rs.core.Response.Status.NOT_FOUND);
  }

  @Test
  public void findBeaconNumber() {
    Beacon entity = newBeacon(999, 9);
    when(query.getSingleResult()).thenReturn(entity);
    Response r = endpoint.findBeaconNumber(999);
    Assert.assertNotNull(r);
    ArgumentCaptor<BeaconDTO> captor = ArgumentCaptor.forClass(BeaconDTO.class);
    verify(responseBuilder).entity(captor.capture());
    BeaconDTO dto = captor.getValue();
    Assert.assertEquals(999, dto.getNumber());
    Assert.assertEquals(9, dto.getPilot().getId());
  }

  @Test
  public void findByNumberReturnsNullIfNotFound() {
    Beacon entity = newBeacon(999, 9);
    when(query.getSingleResult()).thenThrow(new NoResultException());
    Response r = endpoint.findBeaconNumber(999);
    Assert.assertNotNull(r);
    verify(responseBuilder, never()).entity(any());
    verify(responseBuilder).status((Response.StatusType) javax.ws.rs.core.Response.Status.NOT_FOUND);
  }

  @Test
  public void listAll() {
    List<Beacon> entities = new ArrayList<>();
    Beacon entity1 = newBeacon(999, 9);
    Beacon entity2 = newBeacon(-1, 10);
    Beacon entity3 = newBeacon(46, -1);
    entities.add(entity1);
    entities.add(entity2);
    entities.add(entity3);
    when(query.getResultList()).thenReturn(entities);
    List<BeaconDTO> beacons = endpoint.listAll(null, null);
    Assert.assertNotNull(beacons);
    Assert.assertEquals(3, beacons.size());

    Assert.assertEquals(1, beacons.stream().filter(b -> (b.getNumber() == 999 && b.getPilot() != null && b.getPilot().getId() == 9)).count());
    Assert.assertEquals(1, beacons.stream().filter(b -> (b.getPilot() != null && b.getPilot().getId() == 10)).count());
    Assert.assertEquals(1, beacons.stream().filter(b -> (b.getNumber() == 46 && b.getPilot() == null)).count());

  }

  @Test
  public void listAllCanUseWindows() {
    List<Beacon> entities = new ArrayList<>();
    Beacon entity1 = newBeacon(999, 9);
    entities.add(entity1);
    when(query.getResultList()).thenReturn(entities);
    List<BeaconDTO> beacons = endpoint.listAll(1, 1);
    Assert.assertNotNull(beacons);
    Assert.assertEquals(1, beacons.size());
  }

  @Test
  public void update() {
    Beacon before = newBeacon(999, 11);
    Beacon after = newBeacon(99, 12);
    after.setId(before.getId());

    when(em.find(Beacon.class, before.getId())).thenReturn(before);
    when(em.find(Pilot.class, after.getPilot().getId())).thenReturn(after.getPilot());
    Response r = endpoint.update(before.getId(), after);

    Assert.assertNotNull(r);
    ArgumentCaptor<Beacon> captor = ArgumentCaptor.forClass(Beacon.class);
    verify(em).persist(captor.capture());
    Beacon beacon = captor.getValue();
    Assert.assertEquals(99, beacon.getNumber());
    Assert.assertEquals(12, beacon.getPilot().getId());
  }

  @Test
  public void updateIsBadRequestIfNoBeaconPassed() {
    Beacon before = newBeacon(999, 11);
    Response r = endpoint.update(before.getId(), null);

    verify(responseBuilder).status((Response.StatusType) Response.Status.BAD_REQUEST);
  }

  @Test
  public void updateIsConflictIfIdsDontMatch() {
    Beacon before = newBeacon(999, 11);
    Beacon after = newBeacon(99, 12);
    Response r = endpoint.update(before.getId(), after);
    verify(responseBuilder).status((Response.StatusType) Response.Status.CONFLICT);
  }

  @Test
  public void updateReturnsNullIfNotFound() {
    Beacon before = newBeacon(999, 11);
    Beacon after = newBeacon(99, 12);
    after.setId(before.getId());
    when(em.find(Beacon.class, before.getId())).thenReturn(null);
    Response r = endpoint.update(before.getId(), after);
    Assert.assertNotNull(r);
    verify(responseBuilder).status((Response.StatusType) javax.ws.rs.core.Response.Status.NOT_FOUND);
  }

  @Test
  public void updateIsConflictIfOptimisticLockException() {
    doThrow(new OptimisticLockException()).when(em).persist(any());
    Beacon before = newBeacon(999, 11);
    Beacon after = newBeacon(99, 12);
    after.setId(before.getId());
    when(em.find(Beacon.class, before.getId())).thenReturn(before);
    Response r = endpoint.update(before.getId(), after);
    verify(responseBuilder).status((Response.StatusType) Response.Status.CONFLICT);
  }


}