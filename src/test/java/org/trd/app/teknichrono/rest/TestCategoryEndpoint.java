package org.trd.app.teknichrono.rest;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
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
import org.trd.app.teknichrono.model.jpa.CategoryRepository;
import org.trd.app.teknichrono.model.jpa.Pilot;
import org.trd.app.teknichrono.model.jpa.PilotRepository;

import javax.persistence.OptimisticLockException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.ext.RuntimeDelegate;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TestCategoryEndpoint {

  private long id = 1;
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
  private CategoryRepository categoryRepository;

  @Mock
  private PilotRepository pilotRepository;

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
    //when(em.createQuery(anyString(), eq(Category.class))).thenReturn(query);
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void createsCategory() {
    Category entity = newCategory(9);
    Response r = endpoint.create(entity);
    verify(categoryRepository).persist(entity);
    Assert.assertEquals(response, r);
  }

  @Test
  public void createsCategoryWithNoPilot() {
    Category entity = newCategory();
    Response r = endpoint.create(entity);
    verify(categoryRepository).persist(entity);
    Assert.assertEquals(response, r);
  }

  @Test
  public void createsCategoryWithSeveralPilots() {
    Category entity = newCategory(9, 10, 11);
    Response r = endpoint.create(entity);
    verify(categoryRepository).persist(entity);
    Assert.assertEquals(response, r);
  }

  public Category newCategory(int... pilotIds) {
    Category category = new Category();
    category.setId(id++);
    category.setName("Category #id=" + category.getId());
    if (pilotIds != null) {
      for (long pilotId : pilotIds) {
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
    when(categoryRepository.findById(entity.getId())).thenReturn(entity);
    Response r = endpoint.deleteById(entity.getId());
    verify(categoryRepository).delete(entity);
  }

  @Test
  public void deleteByIdRemovesBeaconFromPilots() {
    Category entity = newCategory(9, 10, 11);
    when(categoryRepository.findById(entity.getId())).thenReturn(entity);
    Response r = endpoint.deleteById(entity.getId());
    verify(categoryRepository).delete(entity);
    ArgumentCaptor<Pilot> captor = ArgumentCaptor.forClass(Pilot.class);
    verify(pilotRepository, atLeastOnce()).persist(captor.capture());
    List<Pilot> pingValues = captor.getAllValues();
    Assert.assertEquals(entity.getPilots().size(), pingValues.size());
    for (Pilot p : pingValues) {
      Assert.assertNull(p.getCategory());
    }
  }

  @Test
  public void deleteByIdReturnsErrorIfBeaconDoesNotExist() {
    Category entity = newCategory(9, 10, 11);
    Response r = endpoint.deleteById(entity.getId());
    verify(categoryRepository, never()).delete(any());
    verify(responseBuilder).status((Response.StatusType) Response.Status.NOT_FOUND);
  }

  @Test
  public void findById() {
    Category entity = newCategory(9, 10, 11);
    when(categoryRepository.findById(entity.getId())).thenReturn(entity);
    Response r = endpoint.findById(entity.getId());
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
    when(categoryRepository.findById(any())).thenReturn(null);
    Response r = endpoint.findById(999);
    Assert.assertNotNull(r);
    verify(responseBuilder, never()).entity(any());
    verify(responseBuilder).status((Response.StatusType) javax.ws.rs.core.Response.Status.NOT_FOUND);
  }

  @Test
  public void findCategoryByName() {
    Category entity = newCategory(9, 10, 11);
    when(categoryRepository.findByName(entity.getName())).thenReturn(entity);
    Response r = endpoint.findCategoryByName(entity.getName());
    Assert.assertNotNull(r);

    ArgumentCaptor<CategoryDTO> captor = ArgumentCaptor.forClass(CategoryDTO.class);
    verify(responseBuilder).entity(captor.capture());
    CategoryDTO dto = captor.getValue();

    Assert.assertNotNull(dto);
    Assert.assertEquals(entity.getName(), dto.getName());
    Assert.assertEquals(1, dto.getPilots().stream().filter(p -> p.getId() == 9).count());
    Assert.assertEquals(1, dto.getPilots().stream().filter(p -> p.getId() == 10).count());
    Assert.assertEquals(1, dto.getPilots().stream().filter(p -> p.getId() == 11).count());
  }

  @Test
  public void findCategoryByNameReturnsAnEmptyIfNotFound() {
    Category entity = newCategory(9, 10, 11);
    when(categoryRepository.findByName(anyString())).thenReturn(null);
    Response r = endpoint.findCategoryByName(entity.getName());
    Assert.assertNotNull(r);
    verify(responseBuilder, never()).entity(any());
    verify(responseBuilder).status((Response.StatusType) javax.ws.rs.core.Response.Status.NOT_FOUND);
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

    PanacheQuery<Category> query = mock(PanacheQuery.class);
    when(categoryRepository.findAll()).thenReturn(query);
    when(query.page(any())).thenReturn(query);
    when(query.stream()).thenReturn(entities.stream());

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

    PanacheQuery<Category> query = mock(PanacheQuery.class);
    when(categoryRepository.findAll()).thenReturn(query);
    when(query.page(any())).thenReturn(query);
    when(query.stream()).thenReturn(entities.stream());

    List<CategoryDTO> beacons = endpoint.listAll(1, 1);
    Assert.assertNotNull(beacons);
    Assert.assertEquals(1, beacons.size());
  }

  @Test
  public void addPilot() {
    Pilot pilot = new Pilot();
    pilot.setId(102L);
    Category entity = newCategory(9, 10, 11);
    when(categoryRepository.findById(entity.getId())).thenReturn(entity);
    when(pilotRepository.findById(102L)).thenReturn(pilot);

    Response r = endpoint.addPilot(entity.getId(), 102L);
    Assert.assertNotNull(r);

    ArgumentCaptor<CategoryDTO> captor = ArgumentCaptor.forClass(CategoryDTO.class);
    verify(responseBuilder).entity(captor.capture());
    CategoryDTO modifiedCategory = captor.getValue();
    assertThat(modifiedCategory.getId()).isEqualTo(entity.getId());
    Assert.assertTrue(modifiedCategory.getPilots().stream().anyMatch(p -> p.getId() == 102));

    ArgumentCaptor<Pilot> pilotCaptor = ArgumentCaptor.forClass(Pilot.class);
    verify(pilotRepository, atLeastOnce()).persist(pilotCaptor.capture());
    List<Pilot> pilotValues = pilotCaptor.getAllValues();
    pilotValues.removeIf(p -> p.getId() != 102);
    Assert.assertEquals(1, pilotValues.size());
    assertThat(pilotValues.get(0).getId()).isEqualTo(102L);


    ArgumentCaptor<Category> categoryCaptor = ArgumentCaptor.forClass(Category.class);
    verify(categoryRepository, atLeastOnce()).persist(categoryCaptor.capture());
    List<Category> categoryValues = categoryCaptor.getAllValues();
    Assert.assertEquals(1, categoryValues.size());
    Assert.assertEquals(entity.getId(), categoryValues.get(0).getId());
  }

  @Test
  public void addPilotReturnsNotFoundIfCategoryMissing() {
    Pilot pilot = new Pilot();
    pilot.setId(102L);
    Category entity = newCategory(9, 10, 11);

    Response r = endpoint.addPilot(entity.getId(), 102L);
    Assert.assertNotNull(r);

    verify(categoryRepository, never()).persist(any(Category.class));
    verify(pilotRepository, never()).persist(any(Pilot.class));
    verify(responseBuilder).status((Response.StatusType) javax.ws.rs.core.Response.Status.NOT_FOUND);
  }

  @Test
  public void addPilotReturnsNotFoundIfPilotMissing() {
    Pilot pilot = new Pilot();
    pilot.setId(102L);
    Category entity = newCategory(9, 10, 11);
    when(categoryRepository.findById(entity.getId())).thenReturn(entity);

    Response r = endpoint.addPilot(entity.getId(), 102L);
    Assert.assertNotNull(r);

    verify(categoryRepository, never()).persist(any(Category.class));
    verify(pilotRepository, never()).persist(any(Pilot.class));
    verify(responseBuilder).status((Response.StatusType) javax.ws.rs.core.Response.Status.NOT_FOUND);

  }

  @Test
  public void update() {
    Category before = newCategory(9, 10, 11);
    Category entity = newCategory(9, 11, 12);
    entity.setId(before.getId());
    entity.setName("new");
    CategoryDTO dto = CategoryDTO.fromCategory(entity);
    //when(em.createQuery(anyString(), eq(Pilot.class))).thenReturn(queryPilot);


    when(categoryRepository.findById(before.getId())).thenReturn(before);
    Response r = endpoint.update(before.getId(), dto);

    Assert.assertNotNull(r);
    ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);
    verify(categoryRepository, atLeastOnce()).persist(captor.capture());
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
    CategoryDTO dto = CategoryDTO.fromCategory(after);
    Response r = endpoint.update(before.getId(), dto);
    verify(responseBuilder).status((Response.StatusType) Response.Status.CONFLICT);
  }

  @Test
  public void updateReturnsNullIfNotFound() {
    Category before = newCategory(9, 10, 11);
    Category after = newCategory(9, 11, 12);
    after.setId(before.getId());
    after.setName("new");
    CategoryDTO dto = CategoryDTO.fromCategory(after);
    when(categoryRepository.findById(before.getId())).thenReturn(null);
    Response r = endpoint.update(before.getId(), dto);
    Assert.assertNotNull(r);
    verify(responseBuilder).status((Response.StatusType) javax.ws.rs.core.Response.Status.NOT_FOUND);
  }

  @Test
  public void updateIsConflictIfOptimisticLockException() {
    doThrow(new OptimisticLockException()).when(categoryRepository).persist(any(Category.class));
    Category before = newCategory(9, 10, 11);
    Category after = newCategory(9, 11, 12);
    after.setId(before.getId());
    after.setName("new");
    CategoryDTO dto = CategoryDTO.fromCategory(after);
    //when(em.createQuery(anyString(), eq(Pilot.class))).thenReturn(queryPilot);
    when(categoryRepository.findById(before.getId())).thenReturn(before);
    Response r = endpoint.update(before.getId(), dto);
    verify(responseBuilder).status((Response.StatusType) Response.Status.CONFLICT);
  }

}