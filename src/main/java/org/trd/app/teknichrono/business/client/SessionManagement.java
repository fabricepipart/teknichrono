package org.trd.app.teknichrono.business.client;

import org.jboss.logging.Logger;
import org.trd.app.teknichrono.model.dto.PingDTO;
import org.trd.app.teknichrono.model.jpa.Beacon;
import org.trd.app.teknichrono.model.jpa.Chronometer;
import org.trd.app.teknichrono.model.jpa.Event;
import org.trd.app.teknichrono.model.jpa.Pilot;
import org.trd.app.teknichrono.model.jpa.Ping;
import org.trd.app.teknichrono.model.jpa.Session;
import org.trd.app.teknichrono.model.jpa.SessionType;
import org.trd.app.teknichrono.model.repository.LapTimeRepository;
import org.trd.app.teknichrono.model.repository.PingRepository;
import org.trd.app.teknichrono.model.repository.SessionRepository;
import org.trd.app.teknichrono.util.exception.ConflictingIdException;
import org.trd.app.teknichrono.util.exception.NotFoundException;

import java.time.Instant;
import java.util.Set;

public class SessionManagement {

  private Logger LOGGER = Logger.getLogger(SessionManagement.class);

  private final SessionRepository sessionRepository;
  private final LapTimeRepository lapTimeRepository;
  private final PingRepository pingRepository;

  public SessionManagement(LapTimeRepository lapTimeRepository, PingRepository pingRepository, SessionRepository sessionRepository) {
    this.lapTimeRepository = lapTimeRepository;
    this.pingRepository = pingRepository;
    this.sessionRepository = sessionRepository;
  }

  public void startSession(Session session, Instant timestamp) throws ConflictingIdException, NotFoundException {
    if (!session.isCurrent()) {
      Instant instant = timestamp != null ? timestamp : Instant.now();
      startOngoingSession(session, instant);
      if (session.getSessionType() == SessionType.RACE) {
        startOngoingRace(session, instant);
      }
    }
  }

  private void startOngoingSession(Session session, Instant timestamp) {
    // Stop all other sessions of the event that are not concurrent
    Event event = session.getEvent();
    if (event != null) {
      for (Session otherSession : event.getSessions()) {
        if (otherSession.id != session.id && otherSession.isCurrent() && !session.intersects(otherSession)) {
          endSession(otherSession, timestamp);
        }
      }
    }
    this.sessionRepository.startSession(session, timestamp);
  }

  private void startOngoingRace(Session session, Instant timestamp) throws ConflictingIdException, NotFoundException {
    if (session.getChronometers().isEmpty()) {
      throw new NotFoundException("No Chronometer associated to this sessions #" + session.getId());
    }
    Chronometer chronometer = session.getChronometers().get(0);
    Set<Pilot> pilots = session.getPilots();
    PingManager cm = new PingManager(this.lapTimeRepository);
    for (Pilot pilot : pilots) {
      Beacon currentBeacon = pilot.getCurrentBeacon();
      if (currentBeacon != null) {
        PingDTO dto = PingDTO.create(currentBeacon.getId(), chronometer.getId(), timestamp, 0);
        Ping p = this.pingRepository.create(dto);
        cm.addPing(p, pilot, chronometer, session);
      }
    }
  }

  public void endSession(Session session, Instant end) {
    if (session.getEnd().isBefore(end)) {
      this.LOGGER.warn("Session ID=" + session.id + " has been stopped after expected end");
    }
    this.sessionRepository.endSession(session, end);
  }
}
