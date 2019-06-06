package org.trd.app.teknichrono.rest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.trd.app.teknichrono.model.dto.CategoryDTO;
import org.trd.app.teknichrono.model.jpa.Category;
import org.trd.app.teknichrono.model.jpa.CategoryRepository;
import org.trd.app.teknichrono.model.jpa.Pilot;
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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
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

  @Mock
  private CategoryRepository categoryRepository;

  @InjectMocks
  private CategoryEndpoint endpoint;

  @BeforeEach
  public void setUp() throws URISyntaxException {
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
    when(responseBuilder.entity(any())).thenReturn(responseBuilder);
    //when(em.createQuery(anyString(), eq(Category.class))).thenReturn(query);
  }

  @AfterEach
  public void tearDown() {
    RuntimeDelegate.setInstance(null);
  }

  @Test
  public void createsCategory() {
    Category entity = newCategory(9);
    Response r = endpoint.create(entity);
    verify(categoryRepository).create(entity);
    assertThat(r).isEqualTo(response);
  }

  @Test
  public void createsCategoryWithNoPilot() {
    Category entity = newCategory();
    Response r = endpoint.create(entity);
    verify(categoryRepository).create(entity);
    assertThat(r).isEqualTo(response);
  }

  @Test
  public void createsCategoryWithSeveralPilots() {
    Category entity = newCategory(9, 10, 11);
    Response r = endpoint.create(entity);
    verify(categoryRepository).create(entity);
    assertThat(r).isEqualTo(response);
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
  public void deleteById() throws NotFoundException {
    Category entity = newCategory();
    when(categoryRepository.findById(entity.getId())).thenReturn(entity);
    Response r = endpoint.deleteById(entity.getId());
    verify(categoryRepository).deleteById(entity.getId());
  }

  @Test
  public void findById() {
    Category entity = newCategory(9, 10, 11);
    when(categoryRepository.findById(entity.getId())).thenReturn(entity);
    Response r = endpoint.findById(entity.getId());
    assertThat(r).isNotNull();
    ArgumentCaptor<CategoryDTO> captor = ArgumentCaptor.forClass(CategoryDTO.class);
    verify(responseBuilder).entity(captor.capture());
    CategoryDTO dto = captor.getValue();
    assertThat(dto.getName()).isEqualTo(entity.getName());
    assertThat(dto.getPilots().stream().filter(p -> p.getId() == 9).count()).isEqualTo(1);
    assertThat(dto.getPilots().stream().filter(p -> p.getId() == 10).count()).isEqualTo(1);
    assertThat(dto.getPilots().stream().filter(p -> p.getId() == 11).count()).isEqualTo(1);
  }

  @Test
  public void findByIdReturnsNullIfNotFound() {
    when(categoryRepository.findById(any())).thenReturn(null);
    Response r = endpoint.findById(999);
    assertThat(r).isNotNull();
    verify(responseBuilder, never()).entity(any());
    verify(responseBuilder).status((Response.StatusType) javax.ws.rs.core.Response.Status.NOT_FOUND);
  }

  @Test
  public void findCategoryByName() {
    Category entity = newCategory(9, 10, 11);
    when(categoryRepository.findByName(entity.getName())).thenReturn(entity);
    Response r = endpoint.findCategoryByName(entity.getName());
    assertThat(r).isNotNull();

    ArgumentCaptor<CategoryDTO> captor = ArgumentCaptor.forClass(CategoryDTO.class);
    verify(responseBuilder).entity(captor.capture());
    CategoryDTO dto = captor.getValue();

    assertThat(dto.getName()).isEqualTo(entity.getName());
    assertThat(dto.getPilots().stream().filter(p -> p.getId() == 9).count()).isEqualTo(1);
    assertThat(dto.getPilots().stream().filter(p -> p.getId() == 10).count()).isEqualTo(1);
    assertThat(dto.getPilots().stream().filter(p -> p.getId() == 11).count()).isEqualTo(1);
  }

  @Test
  public void findCategoryByNameReturnsAnEmptyIfNotFound() {
    Category entity = newCategory(9, 10, 11);
    when(categoryRepository.findByName(anyString())).thenReturn(null);
    Response r = endpoint.findCategoryByName(entity.getName());
    assertThat(r).isNotNull();
    verify(responseBuilder, never()).entity(any());
    verify(responseBuilder).status((Response.StatusType) javax.ws.rs.core.Response.Status.NOT_FOUND);
  }

  @Test
  public void listAll() {
    List<CategoryDTO> entities = new ArrayList<>();
    Category entity1 = newCategory(9, 10, 11);
    Category entity2 = newCategory(12);
    Category entity3 = newCategory();
    entities.add(CategoryDTO.fromCategory(entity1));
    entities.add(CategoryDTO.fromCategory(entity2));
    entities.add(CategoryDTO.fromCategory(entity3));

    when(categoryRepository.findAll(null, null)).thenReturn(entities);

    List<CategoryDTO> categoryDTOS = endpoint.listAll(null, null);
    assertThat(categoryDTOS).isNotNull();
    assertThat(categoryDTOS).hasSize(3);

    assertThat(categoryDTOS.stream().filter(b -> (b.getName() == entity1.getName() && b.getPilots() != null && b.getPilots().size() == 3)).count()).isEqualTo(1);
    assertThat(categoryDTOS.stream().filter(b -> (b.getPilots() != null && b.getPilots().size() == 1)).count()).isEqualTo(1);
    assertThat(categoryDTOS.stream().filter(b -> (b.getName() == entity3.getName() && b.getPilots().size() == 0)).count()).isEqualTo(1);

  }

  @Test
  public void listAllCanUseWindows() {
    List<CategoryDTO> entities = new ArrayList<>();
    Category entity1 = newCategory(9, 10, 11);
    entities.add(CategoryDTO.fromCategory(entity1));

    when(categoryRepository.findAll(1, 1)).thenReturn(entities);

    List<CategoryDTO> beacons = endpoint.listAll(1, 1);
    assertThat(beacons).isNotNull();
    assertThat(beacons).hasSize(1);
  }

  @Test
  public void addPilot() throws NotFoundException {
    Pilot pilot = new Pilot();
    pilot.setId(102L);
    Category entity = newCategory(9, 10, 11);
    when(categoryRepository.findById(entity.getId())).thenReturn(entity);
    //when(pilotRepository.findById(102L)).thenReturn(pilot);

    Response r = endpoint.addPilot(entity.getId(), 102L);
    assertThat(r).isNotNull();

    verify(categoryRepository, atLeastOnce()).addPilot(entity.getId(), 102L);
  }

  @Test
  public void update() throws MissingIdException, NotFoundException {
    Category before = newCategory(9, 10, 11);
    Category entity = newCategory(9, 11, 12);
    entity.setId(before.getId());
    entity.setName("new");
    CategoryDTO dto = CategoryDTO.fromCategory(entity);
    //when(em.createQuery(anyString(), eq(Pilot.class))).thenReturn(queryPilot);


    when(categoryRepository.findById(before.getId())).thenReturn(before);
    Response r = endpoint.update(before.getId(), dto);

    assertThat(r).isNotNull();
    ArgumentCaptor<CategoryDTO> captor = ArgumentCaptor.forClass(CategoryDTO.class);
    verify(categoryRepository, atLeastOnce()).update(anyLong(), captor.capture());
    CategoryDTO c = captor.getAllValues().get(0);
    assertThat(c.getName()).isEqualTo(entity.getName());

  }


  @Test
  public void updateIsBadRequestIfNoEntityPassed() {
    Category before = newCategory(9, 10, 11);
    Response r = endpoint.update(before.getId(), null);

    verify(responseBuilder).status((Response.StatusType) Response.Status.BAD_REQUEST);
  }

  @Test
  public void updateIsConflictIfIdsDontMatch() throws MissingIdException, NotFoundException {
    doThrow(MissingIdException.class).when(categoryRepository).update(anyLong(), any(CategoryDTO.class));
    Category before = newCategory(9, 10, 11);
    Category after = newCategory(9, 11, 12);
    after.setName("new");
    CategoryDTO dto = CategoryDTO.fromCategory(after);
    Response r = endpoint.update(before.getId(), dto);
    verify(responseBuilder).status((Response.StatusType) Response.Status.CONFLICT);
  }

  @Test
  public void updateReturnsNullIfNotFound() throws MissingIdException, NotFoundException {
    doThrow(NotFoundException.class).when(categoryRepository).update(anyLong(), any(CategoryDTO.class));
    Category before = newCategory(9, 10, 11);
    Category after = newCategory(9, 11, 12);
    after.setId(before.getId());
    after.setName("new");
    CategoryDTO dto = CategoryDTO.fromCategory(after);
    when(categoryRepository.findById(before.getId())).thenReturn(null);
    Response r = endpoint.update(before.getId(), dto);
    assertThat(r).isNotNull();
    verify(responseBuilder).status((Response.StatusType) javax.ws.rs.core.Response.Status.NOT_FOUND);
  }

  @Test
  public void updateIsConflictIfOptimisticLockException() throws MissingIdException, NotFoundException {
    doThrow(new OptimisticLockException()).when(categoryRepository).update(anyLong(), any(CategoryDTO.class));
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