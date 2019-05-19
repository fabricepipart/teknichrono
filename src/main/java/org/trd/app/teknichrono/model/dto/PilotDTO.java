package org.trd.app.teknichrono.model.dto;

import org.trd.app.teknichrono.model.jpa.Pilot;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

public class PilotDTO {

  private long id;
  private int version;
  private String firstName;
  private String lastName;
  private NestedBeaconDTO currentBeacon;
  private NestedCategoryDTO category;

  public PilotDTO() {
  }

  public PilotDTO(final Pilot entity) {
    if (entity != null) {
      this.id = entity.id;
      this.version = entity.getVersion();
      this.firstName = entity.getFirstName();
      this.lastName = entity.getLastName();
      if (entity.getCurrentBeacon() != null) {
        this.currentBeacon = new NestedBeaconDTO(entity.getCurrentBeacon());
      }
      if (entity.getCategory() != null) {
        this.category = new NestedCategoryDTO(entity.getCategory());
      }
    }
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("[id=" + getId());
    sb.append(",firstName=" + firstName);
    sb.append(",lastName=" + lastName + "]");
    return sb.toString();
  }

  //
  public Pilot fromDTO(Pilot entity, EntityManager em) {
    if (entity == null) {
      entity = new Pilot();
    }
    entity.setVersion(this.version);
    entity.setFirstName(this.firstName);
    entity.setLastName(this.lastName);
    if (this.currentBeacon != null) {
      entity.setCurrentBeacon(this.currentBeacon.fromDTO(entity.getCurrentBeacon(), em));
    }
    if (this.category != null) {
      entity.setCategory(this.category.fromDTO(entity.getCategory(), em));
    }
    entity = em.merge(entity);
    return entity;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public int getVersion() {
    return version;
  }

  public void setVersion(int version) {
    this.version = version;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public NestedBeaconDTO getCurrentBeacon() {
    return currentBeacon;
  }

  public void setCurrentBeacon(NestedBeaconDTO currentBeacon) {
    this.currentBeacon = currentBeacon;
  }

  public NestedCategoryDTO getCategory() {
    return category;
  }

  public void setCategory(NestedCategoryDTO category) {
    this.category = category;
  }

  public static List<PilotDTO> convert(List<Pilot> results) {
    List<PilotDTO> converted = new ArrayList<>();
    if (results != null) {
      for (Pilot pilot : results) {
        converted.add(new PilotDTO(pilot));
      }
    }
    return converted;
  }

}
