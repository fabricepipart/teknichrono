package org.trd.app.teknichrono.model.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.trd.app.teknichrono.model.dto.CategoryDTO;
import org.trd.app.teknichrono.model.jpa.Category;
import org.trd.app.teknichrono.model.jpa.Pilot;
import org.trd.app.teknichrono.util.exception.ConflictingIdException;
import org.trd.app.teknichrono.util.exception.NotFoundException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

@Dependent
public class CategoryRepository extends PanacheRepositoryWrapper<Category, CategoryDTO> {


  @ApplicationScoped
  public static class Panache implements PanacheRepository<Category> {
  }

  private final Panache panacheRepository;

  private final PilotRepository.Panache pilotRepository;

  @Inject
  public CategoryRepository(Panache panacheRepository, PilotRepository.Panache pilotRepository) {
    super(panacheRepository);
    this.panacheRepository = panacheRepository;
    this.pilotRepository = pilotRepository;
  }

  @Override
  public Category create(CategoryDTO entity) throws ConflictingIdException, NotFoundException {
    Category category = fromDTO(entity);
    panacheRepository.persist(category);
    return category;
  }

  @Override
  public String getEntityName() {
    return Category.class.getSimpleName();
  }

  @Override
  public CategoryDTO toDTO(Category dto) {
    return CategoryDTO.fromCategory(dto);
  }

  @Override
  public Category fromDTO(CategoryDTO entity) throws ConflictingIdException, NotFoundException {
    checkNoId(entity);
    Category category = new Category();
    category.setName(entity.getName());
    setOneToManyRelationship(category, entity.getPilots(), Category::getPilots, Pilot::setCategory, pilotRepository);
    return category;
  }


  @Override
  public void deleteById(long id) throws NotFoundException {
    Category entity = ensureFindById(id);
    nullifyOneToManyRelationship(entity.getPilots(), Pilot::setCategory, pilotRepository);
    panacheRepository.delete(entity);
  }

  @Override
  public void update(long id, CategoryDTO entity) throws ConflictingIdException, NotFoundException {
    checkIdsMatch(id, entity);
    Category category = ensureFindById(id);
    category.setName(entity.getName());

    // Update of pilots
    setOneToManyRelationship(category, entity.getPilots(), Category::getPilots, Pilot::setCategory, pilotRepository);
    panacheRepository.persist(category);
  }

  public PanacheRepository getPilotRepository() {
    return pilotRepository;
  }
}
