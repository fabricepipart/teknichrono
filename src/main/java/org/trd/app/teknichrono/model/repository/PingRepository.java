package org.trd.app.teknichrono.model.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.trd.app.teknichrono.model.dto.NestedBeaconDTO;
import org.trd.app.teknichrono.model.dto.NestedChronometerDTO;
import org.trd.app.teknichrono.model.dto.PingDTO;
import org.trd.app.teknichrono.model.jpa.Beacon;
import org.trd.app.teknichrono.model.jpa.Chronometer;
import org.trd.app.teknichrono.model.jpa.Ping;
import org.trd.app.teknichrono.util.exception.ConflictingIdException;
import org.trd.app.teknichrono.util.exception.NotFoundException;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class PingRepository extends PanacheRepositoryWrapper<Ping, PingDTO> {

  @ApplicationScoped
  public static class Panache implements PanacheRepository<Ping> {
  }

  private final Panache panacheRepository;

  private final BeaconRepository.Panache beaconRepository;

  private final ChronometerRepository.Panache chronometerRepository;

  @Inject
  public PingRepository(Panache panacheRepository, BeaconRepository.Panache beaconRepository, ChronometerRepository.Panache chronometerRepository) {
    super(panacheRepository);
    this.panacheRepository = panacheRepository;
    this.beaconRepository = beaconRepository;
    this.chronometerRepository = chronometerRepository;
  }

  @Override
  public String getEntityName() {
    return Ping.class.getName();
  }

  @Override
  public Ping create(PingDTO dto) throws ConflictingIdException, NotFoundException {
    Ping ping = fromDTO(dto);
    panacheRepository.persist(ping);
    return ping;
  }

  @Deprecated
  public Ping create(PingDTO dto, long chronoId, long beaconId) throws ConflictingIdException, NotFoundException {
    NestedChronometerDTO chrono = new NestedChronometerDTO();
    chrono.setId(chronoId);
    dto.setChronometer(chrono);
    NestedBeaconDTO beacon = new NestedBeaconDTO();
    beacon.setId(beaconId);
    dto.setBeacon(beacon);
    return create(dto);
  }


  @Override
  public Ping fromDTO(PingDTO dto) throws ConflictingIdException, NotFoundException {
    checkNoId(dto);
    Ping ping = new Ping();
    ping.setInstant(dto.getInstant());
    ping.setPower(dto.getPower());
    setManyToOneRelationship(ping, dto.getBeacon(), Ping::setBeacon, Beacon::getPings, beaconRepository);
    setManyToOneRelationship(ping, dto.getChronometer(), Ping::setChrono, Chronometer::getPings, chronometerRepository);
    return ping;
  }

  @Override
  public PingDTO toDTO(Ping dto) {
    return PingDTO.fromPing(dto);
  }

  @Override
  public void update(long id, PingDTO dto) throws ConflictingIdException, NotFoundException {
    checkIdsMatch(id, dto);
    Ping ping = ensureFindById(id);
    ping.setInstant(dto.getInstant());
    ping.setPower(dto.getPower());
    setManyToOneRelationship(ping, dto.getBeacon(), Ping::setBeacon, Beacon::getPings, beaconRepository);
    setManyToOneRelationship(ping, dto.getChronometer(), Ping::setChrono, Chronometer::getPings, chronometerRepository);
    panacheRepository.persist(ping);
  }

}
