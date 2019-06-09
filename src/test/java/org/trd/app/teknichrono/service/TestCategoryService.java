package org.trd.app.teknichrono.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.trd.app.teknichrono.model.dto.CategoryDTO;
import org.trd.app.teknichrono.model.jpa.Category;
import org.trd.app.teknichrono.model.jpa.CategoryRepository;
import org.trd.app.teknichrono.model.jpa.Pilot;
import org.trd.app.teknichrono.model.jpa.PilotRepository;
import org.trd.app.teknichrono.util.exception.NotFoundException;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TestCategoryService {

  private long id = 1L;

  @Mock
  private CategoryRepository categoryRespository;

  @Mock
  private PilotRepository pilotRepository;

  @InjectMocks
  private CategoryService categoryService;


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
  public void deleteByIdRemovesCategoryFromPilots() throws NotFoundException {
    Category entity = newCategory(9, 10, 11);
    when(categoryRespository.findById(entity.getId())).thenReturn(entity);

    categoryService.deleteById(entity.getId());

    verify(categoryRespository).delete(entity);
    ArgumentCaptor<Pilot> captor = ArgumentCaptor.forClass(Pilot.class);
    verify(pilotRepository, atLeastOnce()).persist(captor.capture());
    List<Pilot> pilots = captor.getAllValues();
    assertThat(pilots.size()).isEqualTo(entity.getPilots().size());
    for (Pilot p : pilots) {
      assertThat(p.getCategory()).isNull();
    }
  }

  @Test
  public void deleteByIdReturnsErrorIfCategoryDoesNotExist() throws NotFoundException {
    Category entity = newCategory(9, 10, 11);
    Assertions.assertThrows(NotFoundException.class, () -> categoryService.deleteById(entity.getId()));
  }


  @Test
  public void addPilot() throws NotFoundException {
    Pilot pilot = new Pilot();
    pilot.setId(102L);
    Category entity = newCategory(9, 10, 11);
    when(categoryRespository.findById(entity.getId())).thenReturn(entity);
    when(pilotRepository.findById(102L)).thenReturn(pilot);

    CategoryDTO modifiedCategory = categoryService.addPilot(entity.getId(), 102L);
    assertThat(modifiedCategory).isNotNull();

    assertThat(modifiedCategory.getId()).isEqualTo(entity.getId());
    assertThat(modifiedCategory.getPilots().stream().anyMatch(p -> p.getId() == 102)).isTrue();

    ArgumentCaptor<Pilot> pilotCaptor = ArgumentCaptor.forClass(Pilot.class);
    verify(pilotRepository, atLeastOnce()).persist(pilotCaptor.capture());
    assertThat(pilotCaptor.getAllValues().size()).isEqualTo(1);
    assertThat(pilotCaptor.getAllValues().get(0).getId()).isEqualTo(102L);
  }

  @Test
  public void addPilotReturnsNotFoundIfCategoryMissing() throws NotFoundException {
    Pilot pilot = new Pilot();
    pilot.setId(102L);
    Category entity = newCategory(9, 10, 11);

    Assertions.assertThrows(NotFoundException.class, () -> categoryService.addPilot(entity.getId(), 102L));

    verify(categoryRespository, never()).persist(any(Category.class));
    verify(pilotRepository, never()).persist(any(Pilot.class));
  }

  @Test
  public void addPilotReturnsNotFoundIfPilotMissing() throws NotFoundException {
    Pilot pilot = new Pilot();
    pilot.setId(102L);
    Category entity = newCategory(9, 10, 11);
    when(categoryRespository.findById(entity.getId())).thenReturn(entity);

    Assertions.assertThrows(NotFoundException.class, () -> categoryService.addPilot(entity.getId(), 102L));

    verify(categoryRespository, never()).persist(any(Category.class));
    verify(pilotRepository, never()).persist(any(Pilot.class));

  }
}
