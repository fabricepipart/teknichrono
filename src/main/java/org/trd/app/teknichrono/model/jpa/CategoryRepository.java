package org.trd.app.teknichrono.model.jpa;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.trd.app.teknichrono.model.dto.CategoryDTO;
import org.trd.app.teknichrono.model.dto.NestedPilotDTO;
import org.trd.app.teknichrono.util.exception.ConflictingIdException;
import org.trd.app.teknichrono.util.exception.NotFoundException;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.Set;

@Dependent
public class CategoryRepository extends PanacheRepositoryWrapper<Category> implements EntityRepository<Category, CategoryDTO> {

  @ApplicationScoped
  public static class Panache implements PanacheRepository<Category> {
  }

  private final Panache panacheRepository;

  private final PilotRepository pilotRepository;

  @Inject
  public CategoryRepository(Panache panacheRepository, PilotRepository pilotRepository) {
    super(panacheRepository);
    this.panacheRepository = panacheRepository;
    this.pilotRepository = pilotRepository;
  }

  public Category findByName(String name) {
    return panacheRepository.find("name", name).firstResult();
  }

  public void create(CategoryDTO entity) throws ConflictingIdException, NotFoundException {
    Category category = fromDTO(entity);
    panacheRepository.persist(category);
  }

  @Override
  public String getEntityName() {
    return Category.class.getName();
  }

  @Override
  public CategoryDTO toDTO(Category dto) {
    return CategoryDTO.fromCategory(dto);
  }

  @Override
  public Category fromDTO(CategoryDTO entity) throws ConflictingIdException, NotFoundException {
    Category category = new Category();
    if (entity.getId() > 0) {
      throw new ConflictingIdException("Can't create Category with already an ID");
    }
    category.setName(entity.getName());
    if (entity.getPilots() != null) {
      for (NestedPilotDTO nestedPilot : entity.getPilots()) {
        Pilot pilot = pilotRepository.findById(nestedPilot.getId());
        if (pilot == null) {
          throw new NotFoundException("Pilot not found with ID=" + nestedPilot.getId());
        }
        pilot.setCategory(category);
        category.getPilots().add(pilot);
      }
    }
    return category;
  }


  public void deleteById(long id) throws NotFoundException {
    Category entity = findById(id);
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
    panacheRepository.delete(entity);
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
    persist(category);
    pilotRepository.persist(pilot);
    return CategoryDTO.fromCategory(category);
  }

  public void update(long id, CategoryDTO entity) throws ConflictingIdException, NotFoundException {
    if (id != entity.getId()) {
      throw new ConflictingIdException();
    }
    Category category = findById(id);
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
        pilot.setCategory(category);
        category.getPilots().add(pilot);
        pilotRepository.persist(pilot);
      }
    }
    category.setName(entity.getName());
    panacheRepository.persist(category);
  }
}
