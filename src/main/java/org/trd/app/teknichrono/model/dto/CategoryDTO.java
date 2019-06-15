package org.trd.app.teknichrono.model.dto;

import lombok.Data;
import org.trd.app.teknichrono.model.jpa.Category;

import java.util.HashSet;
import java.util.Set;

@Data
public class CategoryDTO implements EntityDTO {

  private Long id;
  private int version;
  private String name;
  private Set<NestedPilotDTO> pilots = new HashSet<>();

  public static CategoryDTO fromCategory(Category category) {
    return DtoMapper.INSTANCE.asCategoryDto(category);
  }

}
