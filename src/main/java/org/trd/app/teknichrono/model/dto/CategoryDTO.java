package org.trd.app.teknichrono.model.dto;

import lombok.Data;
import org.trd.app.teknichrono.model.jpa.Category;
import org.trd.app.teknichrono.model.jpa.Pilot;

import javax.persistence.EntityManager;
import java.util.Set;

@Data
public class CategoryDTO {

  private long id;
  private int version;
  private String name;
  private Set<NestedPilotDTO> pilots;

  public static CategoryDTO fromCategory(Category category) {
    return DtoMapper.INSTANCE.asCategoryDto(category);
  }

  public Category fromDTO(Category entity, EntityManager em) {
    if (entity == null) {
      entity = new Category();
    }
    entity.setVersion(this.version);
    entity.setName(this.name);
    entity.getPilots().clear();
    if (this.pilots != null) {
      for (NestedPilotDTO nestedPilotDTO : pilots) {
        entity.getPilots().add(nestedPilotDTO.fromDTO(new Pilot(), em));
      }
    }
    entity = em.merge(entity);
    return entity;
  }

}
