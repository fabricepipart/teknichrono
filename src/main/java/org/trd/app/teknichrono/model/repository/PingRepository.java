package org.trd.app.teknichrono.model.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import org.trd.app.teknichrono.model.dto.PingDTO;
import org.trd.app.teknichrono.model.jpa.Beacon;
import org.trd.app.teknichrono.model.jpa.Chronometer;
import org.trd.app.teknichrono.model.jpa.Ping;
import org.trd.app.teknichrono.util.exception.ConflictingIdException;
import org.trd.app.teknichrono.util.exception.NotFoundException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Dependent
public class PingRepository extends PanacheRepositoryWrapper<Ping, PingDTO> {

  private static final Logger LOGGER = Logger.getLogger(PingRepository.class);

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
    return Ping.class.getSimpleName();
  }

  @Override
  public Ping create(PingDTO dto) throws ConflictingIdException, NotFoundException {
    Optional<Ping> alreadyExistingPing = find(dto.getInstant(), dto.getChronometer().getId(), dto.getBeacon().getId());
    if (alreadyExistingPing.isPresent()) {
      LOGGER.warn("Tried to create an existing ping. Returning the existing one and ignoring request.");
      return alreadyExistingPing.get();
    }
    Ping ping = fromDTO(dto);
    panacheRepository.persist(ping);
    return ping;
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

  public PingDTO latestOfChronometer(long chronometerId) throws NotFoundException {
    Instant yesterday = Instant.now().minus(1, ChronoUnit.DAYS);
    List<Ping> pings = panacheRepository.find("chrono.id = ?1", chronometerId)
        .stream().filter(p -> p.getInstant().toEpochMilli() > yesterday.toEpochMilli()).toList();
    if (pings.size() > 0) {
      return PingDTO.fromPing(pings.stream().max(Comparator.comparing(Ping::getInstant)).get());
    }
    throw new NotFoundException("No Ping found for Chronometer " + chronometerId);
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

  public Optional<Ping> find(Instant instant, long chronoId, long beaconId) {
    // You cannot use instant in find it returns random things
    return panacheRepository.find("beacon.id = ?1 and chrono.id = ?2", beaconId, chronoId).stream()
        .filter(p -> p.getInstant().toEpochMilli() == instant.toEpochMilli()).findFirst();
  }

}
