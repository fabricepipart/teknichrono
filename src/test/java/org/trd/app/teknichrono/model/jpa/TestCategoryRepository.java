package org.trd.app.teknichrono.model.jpa;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.trd.app.teknichrono.model.repository.CategoryRepository;
import org.trd.app.teknichrono.model.repository.PilotRepository;
import org.trd.app.teknichrono.util.exception.NotFoundException;

import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
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

}
