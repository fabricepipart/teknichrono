package org.trd.app.teknichrono.rest;

import org.jboss.logging.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.trd.app.teknichrono.model.dto.CategoryDTO;
import org.trd.app.teknichrono.model.jpa.Category;
import org.trd.app.teknichrono.model.jpa.Pilot;

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
public class TestCategoryEndpoint {

  private int id = 1;
  @Mock
  private TypedQuery<Category> query;
  @Mock
  private TypedQuery<Pilot> queryPilot;
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
  private CategoryEndpoint endpoint;

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
    when(em.createQuery(anyString(), eq(Category.class))).thenReturn(query);
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void createsCategory() {
    Category entity = newCategory(9);
    Response r = endpoint.create(entity);
    verify(em).persist(entity);
    Assert.assertEquals(response, r);
  }

  @Test
  public void createsCategoryWithNoPilot() {
    Category entity = newCategory();
    Response r = endpoint.create(entity);
    verify(em).persist(entity);
    Assert.assertEquals(response, r);
  }

  @Test
  public void createsCategoryWithSeveralPilots() {
    Category entity = newCategory(9, 10, 11);
    Response r = endpoint.create(entity);
    verify(em).persist(entity);
    Assert.assertEquals(response, r);
  }

  public Category newCategory(int... pilotIds) {
    Category category = new Category();
    category.setId(id++);
    category.setName("Category #id=" + category.getId());
    if (pilotIds != null) {
      for (int pilotId : pilotIds) {
        if (pilotId >= 0) {
          Pilot p = new Pilot();
          p.setId(pilotId);
          category.getPilots().add(p);
          p.setFirstName("First");
          p.setLastName("Last" + pilotId);
        }
      }
    }
    return category;
  }

  @Test
  public void deleteById() {
    Category entity = newCategory();
    when(em.find(Category.class, entity.getId())).thenReturn(entity);
    Response r = endpoint.deleteById(entity.getId());
    verify(em).remove(entity);
  }

  @Test
  public void deleteByIdRemovesBeaconFromPilots() {
    Category entity = newCategory(9, 10, 11);
    when(em.find(Category.class, entity.getId())).thenReturn(entity);
    Response r = endpoint.deleteById(entity.getId());
    verify(em).remove(entity);
    ArgumentCaptor captor = ArgumentCaptor.forClass(Pilot.class);
    verify(em, atLeastOnce()).persist(captor.capture());
    List values = captor.getAllValues();
    values.removeIf(e -> !(e instanceof Pilot));
    List<Pilot> pingValues = (List<Pilot>) values;
    Assert.assertEquals(entity.getPilots().size(), pingValues.size());
    for (Pilot p : pingValues) {
      Assert.assertNull(p.getCategory());
    }
  }

  @Test
  public void deleteByIdReturnsErrorIfBeaconDoesNotExist() {
    Category entity = newCategory(9, 10, 11);
    Response r = endpoint.deleteById(entity.getId());
    verify(em, never()).remove(any());
    verify(responseBuilder).status((Response.StatusType) Response.Status.NOT_FOUND);
  }

  @Test
  public void findById() {
    Category entity = newCategory(9, 10, 11);
    when(query.getSingleResult()).thenReturn(entity);
    Response r = endpoint.findById(999);
    Assert.assertNotNull(r);
    ArgumentCaptor<CategoryDTO> captor = ArgumentCaptor.forClass(CategoryDTO.class);
    verify(responseBuilder).entity(captor.capture());
    CategoryDTO dto = captor.getValue();
    Assert.assertEquals(entity.getName(), dto.getName());
    Assert.assertEquals(1, dto.getPilots().stream().filter(p -> p.getId() == 9).count());
    Assert.assertEquals(1, dto.getPilots().stream().filter(p -> p.getId() == 10).count());
    Assert.assertEquals(1, dto.getPilots().stream().filter(p -> p.getId() == 11).count());
  }

  @Test
  public void findByIdReturnsNullIfNotFound() {
    when(query.getSingleResult()).thenThrow(new NoResultException());
    Response r = endpoint.findById(999);
    Assert.assertNotNull(r);
    verify(responseBuilder, never()).entity(any());
    verify(responseBuilder).status((Response.StatusType) javax.ws.rs.core.Response.Status.NOT_FOUND);
  }

  @Test
  public void findCategoryByName() {
    Category entity = newCategory(9, 10, 11);
    when(query.getSingleResult()).thenReturn(entity);
    CategoryDTO dto = endpoint.findCategoryByName(entity.getName());
    Assert.assertNotNull(dto);
    Assert.assertEquals(entity.getName(), dto.getName());
    Assert.assertEquals(1, dto.getPilots().stream().filter(p -> p.getId() == 9).count());
    Assert.assertEquals(1, dto.getPilots().stream().filter(p -> p.getId() == 10).count());
    Assert.assertEquals(1, dto.getPilots().stream().filter(p -> p.getId() == 11).count());
  }

  @Test
  public void findCategoryByNameReturnsAnEmptyIfNotFound() {
    Category entity = newCategory(9, 10, 11);
    when(query.getSingleResult()).thenThrow(new NoResultException());
    CategoryDTO dto = endpoint.findCategoryByName(entity.getName());
    Assert.assertNotNull(dto);
    //TODO Check if it mqkes sense or should return null
    Assert.assertEquals(0, dto.getId());
    Assert.assertNull(dto.getName());
  }

  @Test
  public void listAll() {
    List<Category> entities = new ArrayList<>();
    Category entity1 = newCategory(9, 10, 11);
    Category entity2 = newCategory(12);
    Category entity3 = newCategory();
    entities.add(entity1);
    entities.add(entity2);
    entities.add(entity3);
    when(query.getResultList()).thenReturn(entities);
    List<CategoryDTO> categoryDTOS = endpoint.listAll(null, null);
    Assert.assertNotNull(categoryDTOS);
    Assert.assertEquals(3, categoryDTOS.size());

    Assert.assertEquals(1, categoryDTOS.stream().filter(b -> (b.getName() == entity1.getName() && b.getPilots() != null && b.getPilots().size() == 3)).count());
    Assert.assertEquals(1, categoryDTOS.stream().filter(b -> (b.getPilots() != null && b.getPilots().size() == 1)).count());
    Assert.assertEquals(1, categoryDTOS.stream().filter(b -> (b.getName() == entity3.getName() && b.getPilots().size() == 0)).count());

  }

  @Test
  public void listAllCanUseWindows() {
    List<Category> entities = new ArrayList<>();
    Category entity1 = newCategory(9, 10, 11);
    entities.add(entity1);
    when(query.getResultList()).thenReturn(entities);
    List<CategoryDTO> beacons = endpoint.listAll(1, 1);
    Assert.assertNotNull(beacons);
    Assert.assertEquals(1, beacons.size());
  }

  @Test
  public void addPilot() {
    Pilot pilot = new Pilot();
    pilot.setId(102);
    Category entity = newCategory(9, 10, 11);
    when(em.find(Category.class, entity.getId())).thenReturn(entity);
    when(em.find(Pilot.class, 102)).thenReturn(pilot);

    Response r =  endpoint.addPilot(entity.getId(), 102);
    Assert.assertNotNull(r);

    ArgumentCaptor<CategoryDTO> captor = ArgumentCaptor.forClass(CategoryDTO.class);
    verify(responseBuilder).entity(captor.capture());
    CategoryDTO modifiedCategory = captor.getValue();
    Assert.assertEquals(entity.getId(), modifiedCategory.getId());
    Assert.assertTrue(modifiedCategory.getPilots().stream().anyMatch(p-> p.getId() == 102));

    ArgumentCaptor pilotCaptor = ArgumentCaptor.forClass(Pilot.class);
    verify(em, atLeastOnce()).persist(pilotCaptor.capture());
    List values = pilotCaptor.getAllValues();
    values.removeIf(e -> !(e instanceof Pilot));
    List<Pilot> pilotValues = (List<Pilot>) values;
    pilotValues.removeIf(p-> p.getId() != 102);
    Assert.assertEquals(1, pilotValues.size());
    Assert.assertEquals(102, pilotValues.get(0).getId());


    ArgumentCaptor categoryCaptor = ArgumentCaptor.forClass(Category.class);
    verify(em, atLeastOnce()).persist(categoryCaptor.capture());
    values = categoryCaptor.getAllValues();
    values.removeIf(e -> !(e instanceof Category));
    List<Category> categoryValues = (List<Category>) values;
    Assert.assertEquals(1, categoryValues.size());
    Assert.assertEquals(entity.getId(), categoryValues.get(0).getId());
  }

  @Test
  public void addPilotReturnsNotFoundIfCategoryMissing() {
    Pilot pilot = new Pilot();
    pilot.setId(102);
    Category entity = newCategory(9, 10, 11);

    Response r =  endpoint.addPilot(entity.getId(), 102);
    Assert.assertNotNull(r);

    verify(em, never()).persist(any());
    verify(responseBuilder).status((Response.StatusType) javax.ws.rs.core.Response.Status.NOT_FOUND);
  }

  @Test
  public void addPilotReturnsNotFoundIfPilotMissing() {
    Pilot pilot = new Pilot();
    pilot.setId(102);
    Category entity = newCategory(9, 10, 11);
    when(em.find(Category.class, entity.getId())).thenReturn(entity);

    Response r =  endpoint.addPilot(entity.getId(), 102);
    Assert.assertNotNull(r);

    verify(em, never()).persist(any());
    verify(responseBuilder).status((Response.StatusType) javax.ws.rs.core.Response.Status.NOT_FOUND);

  }
  @Test
  public void update() {
    Category before = newCategory(9, 10, 11);
    Category entity = newCategory(9, 11, 12);
    entity.setId(before.getId());
    entity.setName("new");
    CategoryDTO dto = new CategoryDTO(entity);
    when(em.createQuery(anyString(), eq(Pilot.class))).thenReturn(queryPilot);


    when(em.find(Category.class, before.getId())).thenReturn(before);
    Response r = endpoint.update(before.getId(), dto);

    Assert.assertNotNull(r);
    ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);
    verify(em, atLeastOnce()).merge(captor.capture());
    Category c = captor.getAllValues().get(0);
    Assert.assertEquals(entity.getName(), c.getName());

  }


  @Test
  public void updateIsBadRequestIfNoEntityPassed() {
    Category before = newCategory(9, 10, 11);
    Response r = endpoint.update(before.getId(), null);

    verify(responseBuilder).status((Response.StatusType) Response.Status.BAD_REQUEST);
  }

  @Test
  public void updateIsConflictIfIdsDontMatch() {
    Category before = newCategory(9, 10, 11);
    Category after = newCategory(9, 11, 12);
    after.setName("new");
    CategoryDTO dto = new CategoryDTO(after);
    Response r = endpoint.update(before.getId(), dto);
    verify(responseBuilder).status((Response.StatusType) Response.Status.CONFLICT);
  }

  @Test
  public void updateReturnsNullIfNotFound() {
    Category before = newCategory(9, 10, 11);
    Category after = newCategory(9, 11, 12);
    after.setId(before.getId());
    after.setName("new");
    CategoryDTO dto = new CategoryDTO(after);
    when(em.find(Category.class, before.getId())).thenReturn(null);
    Response r = endpoint.update(before.getId(), dto);
    Assert.assertNotNull(r);
    verify(responseBuilder).status((Response.StatusType) javax.ws.rs.core.Response.Status.NOT_FOUND);
  }

  @Test
  public void updateIsConflictIfOptimisticLockException() {
    doThrow(new OptimisticLockException()).when(em).merge(any());
    Category before = newCategory(9, 10, 11);
    Category after = newCategory(9, 11, 12);
    after.setId(before.getId());
    after.setName("new");
    CategoryDTO dto = new CategoryDTO(after);
    when(em.createQuery(anyString(), eq(Pilot.class))).thenReturn(queryPilot);
    when(em.find(Category.class, before.getId())).thenReturn(before);
    Response r = endpoint.update(before.getId(), dto);
    verify(responseBuilder).status((Response.StatusType) Response.Status.CONFLICT);
  }

}