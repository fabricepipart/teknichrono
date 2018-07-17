package org.trd.app.teknichrono.rest.dto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.xml.bind.annotation.XmlRootElement;

import org.trd.app.teknichrono.model.Category;
import org.trd.app.teknichrono.model.Pilot;

@XmlRootElement
public class CategoryDTO {

  private int id;

  private int version;
  private String name;
  private Set<NestedPilotDTO> pilots;

  public CategoryDTO() {
  }

  public CategoryDTO(final Category entity) {
    if (entity != null) {
      this.id = entity.getId();
      this.version = entity.getVersion();
      this.name = entity.getName();
      this.pilots = new HashSet<>();
      if (entity.getPilots() != null) {
        for (Pilot p : entity.getPilots()) {
          pilots.add(new NestedPilotDTO(p));
        }
      }
    }
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("[id=" + getId());
    sb.append(",name=" + name + "]");
    return sb.toString();
  }

  //
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

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getVersion() {
    return version;
  }

  public void setVersion(int version) {
    this.version = version;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Set<NestedPilotDTO> getPilots() {
    return pilots;
  }

  public void setPilots(Set<NestedPilotDTO> pilots) {
    this.pilots = pilots;
  }

  public static List<CategoryDTO> convert(List<Category> results) {
    List<CategoryDTO> converted = new ArrayList<>();
    if (results != null) {
      for (Category category : results) {
        converted.add(new CategoryDTO(category));
      }
    }
    return converted;
  }

}
