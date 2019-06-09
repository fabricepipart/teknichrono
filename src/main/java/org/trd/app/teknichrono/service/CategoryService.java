package org.trd.app.teknichrono.service;

import org.trd.app.teknichrono.model.dto.CategoryDTO;
import org.trd.app.teknichrono.model.dto.NestedPilotDTO;
import org.trd.app.teknichrono.model.jpa.Category;
import org.trd.app.teknichrono.model.jpa.CategoryRepository;
import org.trd.app.teknichrono.model.jpa.Pilot;
import org.trd.app.teknichrono.model.jpa.PilotRepository;
import org.trd.app.teknichrono.rest.Paging;
import org.trd.app.teknichrono.util.exception.MissingIdException;
import org.trd.app.teknichrono.util.exception.NotFoundException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class CategoryService {

  @Inject
  private CategoryRepository categoryRespository;

  @Inject
  private PilotRepository pilotRepository;


  public void create(Category entity) {
    categoryRespository.persist(entity);
  }

  public void deleteById(long id) throws NotFoundException {
    Category entity = categoryRespository.findById(id);
    if (entity == null) {
      throw new NotFoundException();
    }
    Set<Pilot> associatedPilots = entity.getPilots();
    if (associatedPilots != null) {
      for (Pilot associatedPilot : associatedPilots) {
        associatedPilot.setCategory(null);
        pilotRepository.persist(associatedPilot);
      }
    }
    categoryRespository.delete(entity);
  }


  public Category findById(Long aLong) {
    return categoryRespository.findById(aLong);
  }


  public Category findByName(String name) {
    return categoryRespository.findByName(name);
  }

  public List<CategoryDTO> findAll(Integer startPosition, Integer maxResult) {
    return categoryRespository.findAll()
        .page(Paging.from(startPosition, maxResult))
        .stream()
        .map(CategoryDTO::fromCategory)
        .collect(Collectors.toList());
  }

  public CategoryDTO addPilot(long categoryId, Long pilotId) throws NotFoundException {
    Category category = findById(categoryId);
    if (category == null) {
      throw new NotFoundException("Category not found with ID=" + categoryId);
    }
    Pilot pilot = pilotRepository.findById(pilotId);
    if (pilot == null) {
      throw new NotFoundException("Pilot not found with ID=" + pilotId);
    }
    pilot.setCategory(category);
    category.getPilots().add(pilot);
    categoryRespository.persist(category);
    pilotRepository.persist(pilot);
    return CategoryDTO.fromCategory(category);
  }

  public void update(long id, CategoryDTO entity) throws MissingIdException, NotFoundException {
    if (id != entity.getId()) {
      throw new MissingIdException();
    }
    Category category = categoryRespository.findById(id);
    if (category == null) {
      throw new NotFoundException("Category not found with ID=" + id);
    }

    // Update of pilots
    category.getPilots().clear();
    if (entity.getPilots() != null) {
      for (NestedPilotDTO pilotDto : entity.getPilots()) {
        Pilot pilot = pilotRepository.findById(pilotDto.getId());
        if (pilot == null) {
          throw new NotFoundException("Pilot not found with ID=" + pilotDto.getId());
        }
        category.getPilots().add(pilot);
      }
    }
    category.setName(entity.getName());
    categoryRespository.persist(category);
  }
}
