package org.trd.app.teknichrono.rest.dto;

import java.io.Serializable;
import org.trd.app.teknichrono.model.LapTime;
import javax.persistence.EntityManager;
import org.trd.app.teknichrono.rest.dto.NestedPilotDTO;
import org.trd.app.teknichrono.rest.dto.NestedEventDTO;
import java.util.List;
import java.util.ArrayList;
import org.trd.app.teknichrono.rest.dto.NestedPingDTO;
import org.trd.app.teknichrono.model.Ping;
import java.util.Iterator;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class LapTimeDTO implements Serializable {

  private int id;
  private int version;
  private NestedPilotDTO pilot;
  private NestedEventDTO event;
  private List<NestedPingDTO> intermediates = new ArrayList<NestedPingDTO>();

  public LapTimeDTO() {
  }

  public LapTimeDTO(final LapTime entity) {
    if (entity != null) {
      this.id = entity.getId();
      this.version = entity.getVersion();
      this.pilot = new NestedPilotDTO(entity.getPilot());
      this.event = new NestedEventDTO(entity.getEvent());
      Iterator<Ping> iterIntermediates = entity.getIntermediates().iterator();
      while (iterIntermediates.hasNext()) {
        Ping element = iterIntermediates.next();
        this.intermediates.add(new NestedPingDTO(element));
      }
    }
  }

  public LapTime fromDTO(LapTime entity, EntityManager em) {
    if (entity == null) {
      entity = new LapTime();
    }
    entity.setVersion(this.version);
    if (this.pilot != null) {
      entity.setPilot(this.pilot.fromDTO(entity.getPilot(), em));
    }
    if (this.event != null) {
      entity.setEvent(this.event.fromDTO(entity.getEvent(), em));
    }
    Iterator<Ping> iterIntermediates = entity.getIntermediates().iterator();
    while (iterIntermediates.hasNext()) {
      boolean found = false;
      Ping ping = iterIntermediates.next();
      Iterator<NestedPingDTO> iterDtoIntermediates = this.getIntermediates().iterator();
      while (iterDtoIntermediates.hasNext()) {
        NestedPingDTO dtoPing = iterDtoIntermediates.next();
        if (((Integer) dtoPing.getId()).equals((Integer) ping.getId())) {
          found = true;
          break;
        }
      }
      if (found == false) {
        iterIntermediates.remove();
      }
    }
    Iterator<NestedPingDTO> iterDtoIntermediates = this.getIntermediates().iterator();
    while (iterDtoIntermediates.hasNext()) {
      boolean found = false;
      NestedPingDTO dtoPing = iterDtoIntermediates.next();
      iterIntermediates = entity.getIntermediates().iterator();
      while (iterIntermediates.hasNext()) {
        Ping ping = iterIntermediates.next();
        if (((Integer) dtoPing.getId()).equals((Integer) ping.getId())) {
          found = true;
          break;
        }
      }
      if (found == false) {
        Iterator<Ping> resultIter = em.createQuery("SELECT DISTINCT p FROM Ping p", Ping.class).getResultList()
            .iterator();
        while (resultIter.hasNext()) {
          Ping result = resultIter.next();
          if (((Integer) result.getId()).equals((Integer) dtoPing.getId())) {
            entity.getIntermediates().add(result);
            break;
          }
        }
      }
    }
    entity = em.merge(entity);
    return entity;
  }

  public int getId() {
    return this.id;
  }

  public void setId(final int id) {
    this.id = id;
  }

  public int getVersion() {
    return this.version;
  }

  public void setVersion(final int version) {
    this.version = version;
  }

  public NestedPilotDTO getPilot() {
    return this.pilot;
  }

  public void setPilot(final NestedPilotDTO pilot) {
    this.pilot = pilot;
  }

  public NestedEventDTO getEvent() {
    return this.event;
  }

  public void setEvent(final NestedEventDTO event) {
    this.event = event;
  }

  public List<NestedPingDTO> getIntermediates() {
    return this.intermediates;
  }

  public void setIntermediates(final List<NestedPingDTO> intermediates) {
    this.intermediates = intermediates;
  }
}