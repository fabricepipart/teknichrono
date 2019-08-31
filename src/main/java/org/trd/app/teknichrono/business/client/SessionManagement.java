package org.trd.app.teknichrono.business.client;

import org.jboss.logging.Logger;
import org.trd.app.teknichrono.model.dto.PingDTO;
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
      startOngoingSession(session, timestamp);
      if (session.getSessionType() == SessionType.RACE) {
        startOngoingRace(session, timestamp);
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
    sessionRepository.startSession(session, timestamp);
  }

  private void startOngoingRace(Session session, Instant timestamp) throws ConflictingIdException, NotFoundException {
    if (session.getChronometers().isEmpty()) {
      throw new NotFoundException("No Chronometer associated to this sessions #" + session.getId());
    }
    Chronometer chronometer = session.getChronometers().get(0);
    Set<Pilot> pilots = session.getPilots();
    PingManager cm = new PingManager(lapTimeRepository);
    for (Pilot pilot : pilots) {
      PingDTO dto = PingDTO.create(pilot.getCurrentBeacon().getId(), chronometer.getId(), timestamp, 0);
      Ping p = pingRepository.create(dto);
      cm.addPing(p, pilot, chronometer, session);
    }
  }

  public void endSession(Session session, Instant end) {
    if (session.getEnd().isBefore(end)) {
      LOGGER.warn("Session ID=" + session.id + " has been stopped after expected end");
    }
    sessionRepository.endSession(session, end);
  }
}
