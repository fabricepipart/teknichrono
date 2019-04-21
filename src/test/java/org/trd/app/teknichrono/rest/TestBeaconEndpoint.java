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
import org.trd.app.teknichrono.model.jpa.Beacon;
import org.trd.app.teknichrono.model.jpa.Pilot;
import org.trd.app.teknichrono.model.jpa.Ping;

import javax.persistence.EntityManager;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.ext.RuntimeDelegate;
import java.net.URI;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TestBeaconEndpoint {

  private int id = 1;

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
  }

  @Test
  public void findBeacon() {
  }

  @Test
  public void findBeaconNumber() {
  }

  @Test
  public void listAll() {
  }

  @Test
  public void update() {
  }
}