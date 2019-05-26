package org.trd.app.teknichrono.rest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.trd.app.teknichrono.model.jpa.Beacon;
import org.trd.app.teknichrono.model.jpa.BeaconRepository;
import org.trd.app.teknichrono.model.jpa.Pilot;
import org.trd.app.teknichrono.model.jpa.PilotRepository;
import org.trd.app.teknichrono.model.jpa.Ping;
import org.trd.app.teknichrono.model.jpa.PingRepository;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.ext.RuntimeDelegate;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TestBeaconEndpoint {

  private long id = 1L;

  @Mock
  private RuntimeDelegate runtimeDelegate;
  @Mock
  private Response.ResponseBuilder responseBuilder;
  @Mock
  private Response response;

  @Mock
  private UriBuilder uriBuilder;

  @Mock
  private BeaconRepository beaconRepository;

  @Mock
  private PilotRepository pilotRepository;

  @Mock
  private PingRepository pingRepository;

  @InjectMocks
  private BeaconEndpoint endpoint;

  @Before
  public void setUp() throws Exception {
    URI uri = new URI("");
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
    verify(beaconRepository).persist(entity);
    Assert.assertEquals(response, r);
  }

  @Test
  public void createsBeaconWithCompletePilotIfIdProvided() {
    Beacon entity = newBeacon(999, 9);
    endpoint.create(entity);
    verify(pilotRepository).findById(9L);
    verify(beaconRepository).persist(entity);
  }

  public Beacon newBeacon(long number, long pilotId) {
    Beacon beacon = new Beacon();
    beacon.id = id++;
    beacon.setNumber(number);
    if (pilotId >= 0) {
      Pilot p = new Pilot();
      p.id = pilotId;
      beacon.setPilot(p);
      p.setFirstName("First");
      p.setLastName("Last");
    }
    for (int i = 0; i < 10; i++) {
      Ping p = new Ping();
      p.id = id++;
      p.setBeacon(beacon);
      beacon.getPings().add(p);
    }
    return beacon;
  }

  @Test
  public void deleteById() {
    Beacon entity = newBeacon(999, 9);
    when(beaconRepository.findById(entity.id)).thenReturn(entity);
    endpoint.deleteById(entity.id);
    verify(beaconRepository).delete(entity);
  }

  @Test
  public void deleteByIdRemovesBeaconFromPilot() {
    Beacon entity = newBeacon(999, 9);
    when(beaconRepository.findById(entity.id)).thenReturn(entity);
    endpoint.deleteById(entity.id);
    verify(beaconRepository).delete(entity);
    ArgumentCaptor<Pilot> captor = ArgumentCaptor.forClass(Pilot.class);
    verify(pilotRepository, atLeastOnce()).persist(captor.capture());
    List<Pilot> pilots = captor.getAllValues();
    for (Pilot p : pilots) {
      Assert.assertNull(p.getCurrentBeacon());
    }
  }

  @Test
  public void deleteByIdRemovesBeaconFromPings() {
    Beacon entity = newBeacon(999, 9);
    when(beaconRepository.findById(entity.id)).thenReturn(entity);
    endpoint.deleteById(entity.id);
    verify(beaconRepository).delete(entity);
  }

  @Test
  public void deleteByIdReturnsErrorIfBeaconDoesNotExist() {
    Beacon entity = newBeacon(999, 9);
    endpoint.deleteById(entity.id);
    verify(beaconRepository, never()).delete(any());
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