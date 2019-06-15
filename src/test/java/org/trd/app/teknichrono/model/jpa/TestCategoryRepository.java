package org.trd.app.teknichrono.model.jpa;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.trd.app.teknichrono.model.dto.CategoryDTO;
import org.trd.app.teknichrono.model.dto.NestedPilotDTO;
import org.trd.app.teknichrono.model.repository.CategoryRepository;
import org.trd.app.teknichrono.model.repository.PilotRepository;
import org.trd.app.teknichrono.util.exception.NotFoundException;

import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TestCategoryRepository {

  private long id = 1L;

  @Mock
  private PilotRepository.Panache pilotRepository;

  @Mock
  private CategoryRepository.Panache categoryPanacheRepository;

  @InjectMocks
  private CategoryRepository categoryRespository;

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
    Set<Pilot> pilots = entity.getPilots();

    when(categoryPanacheRepository.findById(entity.getId())).thenReturn(entity);

    categoryRespository.deleteById(entity.getId());

    verify(categoryPanacheRepository).delete(entity);
    for (Pilot pilot : pilots) {
      verify(pilotRepository).persist(pilot);
      assertThat(pilot.getCurrentBeacon()).isNull();
    }
  }

  @Test
  public void deleteByIdReturnsErrorIfCategoryDoesNotExist() {
    Assertions.assertThrows(NotFoundException.class, () -> categoryRespository.deleteById(42L));
    verify(categoryPanacheRepository, never()).delete(any());
  }


  @Test
  public void addPilot() throws NotFoundException {
    Pilot pilot = new Pilot();
    pilot.setId(102L);
    Category entity = newCategory(9, 10, 11);

    when(categoryPanacheRepository.findById(entity.getId())).thenReturn(entity);
    when(pilotRepository.findById(102L)).thenReturn(pilot);

    CategoryDTO modifiedCategory = categoryRespository.addPilot(entity.getId(), 102L);

    assertThat(modifiedCategory).isNotNull();
    assertThat(modifiedCategory.getId()).isEqualTo(entity.getId());
    assertThat(modifiedCategory.getPilots().stream().mapToLong(NestedPilotDTO::getId)).contains(102L);

    verify(pilotRepository, times(1)).persist(pilot);
    assertThat(pilot.getId()).isEqualTo(102L);
  }

  @Test
  public void addPilotReturnsNotFoundIfCategoryMissing() {
    Pilot pilot = new Pilot();
    pilot.setId(102L);
    Category entity = newCategory(9, 10, 11);

    when(categoryPanacheRepository.findById(entity.getId())).thenReturn(null);

    Assertions.assertThrows(NotFoundException.class, () -> categoryRespository.addPilot(entity.getId(), 102L));

    verify(categoryPanacheRepository, never()).persist(any(Category.class));
    verify(pilotRepository, never()).persist(any(Pilot.class));
  }

  @Test
  public void addPilotReturnsNotFoundIfPilotMissing() {
    Pilot pilot = new Pilot();
    pilot.setId(102L);
    Category entity = newCategory(9, 10, 11);

    when(categoryPanacheRepository.findById(entity.getId())).thenReturn(entity);

    Assertions.assertThrows(NotFoundException.class, () -> categoryRespository.addPilot(entity.getId(), 102L));

    verify(categoryPanacheRepository, never()).persist(any(Category.class));
    verify(pilotRepository, never()).persist(any(Pilot.class));

  }
}
