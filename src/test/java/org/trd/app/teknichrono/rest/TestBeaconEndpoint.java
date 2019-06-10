package org.trd.app.teknichrono.rest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.trd.app.teknichrono.model.dto.BeaconDTO;
import org.trd.app.teknichrono.model.jpa.Beacon;
import org.trd.app.teknichrono.model.jpa.BeaconRepository;
import org.trd.app.teknichrono.model.jpa.Pilot;
import org.trd.app.teknichrono.model.jpa.Ping;
import org.trd.app.teknichrono.util.exception.ConflictingIdException;
import org.trd.app.teknichrono.util.exception.MissingIdException;
import org.trd.app.teknichrono.util.exception.NotFoundException;

import javax.persistence.OptimisticLockException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.ext.RuntimeDelegate;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class TestBeaconEndpoint {

  private long id = 1L;

  private RuntimeDelegate previousRuntimeDelegate;

  @Mock
  private RuntimeDelegate runtimeDelegate;
  @Mock
  private Response.ResponseBuilder responseBuilder;
  @Mock
  private Response response;

  @Mock
  private UriBuilder uriBuilder;

  @Mock
  private BeaconRepository beaconService;

  @InjectMocks
  private BeaconEndpoint endpoint;

  @BeforeEach
  public void setUp() throws URISyntaxException {
    URI uri = new URI("");
    previousRuntimeDelegate = RuntimeDelegate.getInstance();
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
    //when(em.createQuery(anyString(), eq(Beacon.class))).thenReturn(query);
  }

  @AfterEach
  public void cleanup() {
    RuntimeDelegate.setInstance(previousRuntimeDelegate);
  }

  @Test
  public void createsBeacon() throws NotFoundException, ConflictingIdException {
    Beacon entity = newBeacon(999, -1);
    Response r = endpoint.create(BeaconDTO.fromBeacon(entity));
    verify(beaconService).create(BeaconDTO.fromBeacon(entity));
    assertThat(r).isEqualTo(response);
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
  public void deleteById() throws NotFoundException {
    Beacon entity = newBeacon(999, 9);
    when(beaconService.findById(entity.id)).thenReturn(entity);
    endpoint.deleteById(entity.id);
    verify(beaconService).deleteById(entity.getId());
  }

  @Test
  public void findById() {
    Beacon entity = newBeacon(999, 9);
    when(beaconService.findById(entity.getId())).thenReturn(entity);
    Response r = endpoint.findById(entity.getId());
    assertThat(r).isNotNull();
    ArgumentCaptor<BeaconDTO> captor = ArgumentCaptor.forClass(BeaconDTO.class);
    verify(responseBuilder).entity(captor.capture());
    BeaconDTO dto = captor.getValue();
    assertThat(dto.getNumber()).isEqualTo(999);
    assertThat(dto.getPilot().getId()).isEqualTo(9L);
  }

  @Test
  public void findByIdReturnsNullIfNotFound() {
    Beacon entity = newBeacon(999, 9);
    when(beaconService.findById(entity.getId())).thenReturn(null);
    Response r = endpoint.findById(entity.getId());
    assertThat(r).isNotNull();
    verify(responseBuilder, never()).entity(any());
    verify(responseBuilder).status((Response.StatusType) javax.ws.rs.core.Response.Status.NOT_FOUND);
  }

  @Test
  public void findBeaconNumber() {
    Beacon entity = newBeacon(999, 9);
    when(beaconService.findByNumber(entity.getNumber())).thenReturn(entity);
    Response r = endpoint.findBeaconNumber(999);
    assertThat(r).isNotNull();
    ArgumentCaptor<BeaconDTO> captor = ArgumentCaptor.forClass(BeaconDTO.class);
    verify(responseBuilder).entity(captor.capture());
    BeaconDTO dto = captor.getValue();
    assertThat(dto.getNumber()).isEqualTo(999);
    assertThat(dto.getPilot().getId()).isEqualTo(9L);
  }

  @Test
  public void findByNumberReturnsNullIfNotFound() {
    Response r = endpoint.findBeaconNumber(999);
    assertThat(r).isNotNull();
    verify(responseBuilder, never()).entity(any());
    verify(responseBuilder).status((Response.StatusType) javax.ws.rs.core.Response.Status.NOT_FOUND);
  }

  @Test
  public void listAll() {
    List<BeaconDTO> entities = new ArrayList<>();
    Beacon entity1 = newBeacon(999, 9);
    Beacon entity2 = newBeacon(-1, 10);
    Beacon entity3 = newBeacon(46, -1);
    entities.add(BeaconDTO.fromBeacon(entity1));
    entities.add(BeaconDTO.fromBeacon(entity2));
    entities.add(BeaconDTO.fromBeacon(entity3));

    when(beaconService.findAll(null, null)).thenReturn(Stream.of(entity1, entity2, entity3));

    List<BeaconDTO> beacons = endpoint.listAll(null, null);
    assertThat(beacons).isNotNull();
    assertThat(beacons).hasSize(3);

    assertThat(beacons.stream().filter(b -> (b.getNumber() == 999 && b.getPilot() != null && b.getPilot().getId() == 9)).count()).isEqualTo(1);
    assertThat(beacons.stream().filter(b -> (b.getPilot() != null && b.getPilot().getId() == 10)).count()).isEqualTo(1);
    assertThat(beacons.stream().filter(b -> (b.getNumber() == 46 && b.getPilot() == null)).count()).isEqualTo(1);

  }

  @Test
  public void listAllCanUseWindows() {
    List<BeaconDTO> entities = new ArrayList<>();
    Beacon entity1 = newBeacon(999, 9);
    entities.add(BeaconDTO.fromBeacon(entity1));

    when(beaconService.findAll(1, 1)).thenReturn(Stream.of(entity1));

    List<BeaconDTO> beacons = endpoint.listAll(1, 1);
    assertThat(beacons).isNotNull();
    assertThat(beacons).hasSize(1);
  }

  @Test
  public void update() throws MissingIdException, NotFoundException {
    Beacon before = newBeacon(999, 11);
    Beacon after = newBeacon(99, 12);
    after.setId(before.getId());

    when(beaconService.findById(before.getId())).thenReturn(before);

    Response r = endpoint.update(before.getId(), BeaconDTO.fromBeacon(after));

    assertThat(r).isNotNull();
    ArgumentCaptor<BeaconDTO> captor = ArgumentCaptor.forClass(BeaconDTO.class);
    verify(beaconService).updateBeacon(eq(after.getId()), captor.capture());
    BeaconDTO beacon = captor.getValue();
    assertThat(beacon.getNumber()).isEqualTo(99);
    assertThat(beacon.getPilot().getId()).isEqualTo(12L);
  }

  @Test
  public void updateIsBadRequestIfNoBeaconPassed() {
    Beacon before = newBeacon(999, 11);
    endpoint.update(before.getId(), null);

    verify(responseBuilder).status((Response.StatusType) Response.Status.BAD_REQUEST);
  }

  @Test
  public void updateIsConflictIfIdsDontMatch() throws MissingIdException, NotFoundException {
    Beacon before = newBeacon(999, 11);
    Beacon after = newBeacon(99, 12);
    doThrow(new MissingIdException()).when(beaconService).updateBeacon(anyLong(), any(BeaconDTO.class));
    endpoint.update(before.getId(), BeaconDTO.fromBeacon(after));
    verify(responseBuilder).status((Response.StatusType) Response.Status.CONFLICT);
  }

  @Test
  public void updateReturnsNullIfNotFound() throws MissingIdException, NotFoundException {
    Beacon before = newBeacon(999, 11);
    Beacon after = newBeacon(99, 12);
    after.setId(before.getId());
    doThrow(new NotFoundException()).when(beaconService).updateBeacon(anyLong(), any(BeaconDTO.class));
    Response r = endpoint.update(before.getId(), BeaconDTO.fromBeacon(after));
    assertThat(r).isNotNull();
    verify(responseBuilder).status((Response.StatusType) javax.ws.rs.core.Response.Status.NOT_FOUND);
  }

  @Test
  public void updateIsConflictIfOptimisticLockException() throws MissingIdException, NotFoundException {
    doThrow(new OptimisticLockException()).when(beaconService).updateBeacon(anyLong(), any(BeaconDTO.class));
    Beacon before = newBeacon(999, 11);
    Beacon after = newBeacon(99, 12);
    after.setId(before.getId());
    endpoint.update(before.getId(), BeaconDTO.fromBeacon(after));
    verify(responseBuilder).status((Response.StatusType) Response.Status.CONFLICT);
  }


}