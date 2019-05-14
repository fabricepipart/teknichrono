package org.trd.app.teknichrono.business.client;

import org.jboss.logging.Logger;
import org.trd.app.teknichrono.model.jpa.Chronometer;
import org.trd.app.teknichrono.model.jpa.LapTime;
import org.trd.app.teknichrono.model.jpa.Pilot;
import org.trd.app.teknichrono.model.jpa.Ping;
import org.trd.app.teknichrono.model.jpa.Session;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class SessionSelector {

  private final Duration ONE_DAY = Duration.ofDays(1);

  private Logger logger = Logger.getLogger(SessionSelector.class);

  public Session pickMostRelevantCurrent(List<Session> allSessions) {
    return pickMostRelevantByDistance(allSessions, Instant.now());
  }

  /**
   * @param ping
   * @return
   */
  public Session pickMostRelevant(Ping ping) {

    Instant time = ping.getInstant();

    // Only consider sessions that contain chronometer
    List<Session> candidateSessions = new ArrayList<>(ping.getChrono().getSessions());
    // Same day
    candidateSessions.removeIf(s -> duration(s, time).compareTo(ONE_DAY) > 0);
    if (candidateSessions.isEmpty()) {
      return null;
    }
    // Admin has the option to declare pilots of sessions. If he does, that's considered in priority
    List<Session> pilotSessions = new ArrayList<>(candidateSessions);
    pilotSessions.removeIf(s -> !containsPilot(s, ping.getBeacon().getPilot()));
    if (!pilotSessions.isEmpty()) {
      logger.debug("Pilot was part of sessions " + pilotSessions);
      candidateSessions = pilotSessions;
    }

    // Check if one or more are started
    List<Session> startedSessions = new ArrayList<>(candidateSessions);
    startedSessions.removeIf(s -> !s.isCurrent());
    if (!startedSessions.isEmpty()) {
      logger.debug("Current sessions " + startedSessions);
      candidateSessions = startedSessions;
    }

    // Check if we have unfinished business for ex aequo (use case : 2 starts 1 end)
    List<Session> sessionsWithShortestDistance = sessionsWithShortestDistance(candidateSessions, time);
    if (!sessionsWithShortestDistance.isEmpty()) {
      logger.debug("Closest sessions " + sessionsWithShortestDistance);
      if (sessionsWithShortestDistance.size() > 1) {
        // Pilot may have a session with an ongoing lap that miss the ping from that chrono. Pick that.
        Session unfinishedSession = getUnfinishedSession(ping.getBeacon().getPilot(), sessionsWithShortestDistance, ping.getChrono());
        if (unfinishedSession != null) {
          logger.debug("Pilot had started unfinished laps in sessions " + unfinishedSession);
          return unfinishedSession;
        }
      }
      // Either there is just one or No idea! Random!
      return sessionsWithShortestDistance.get(0);
    }

    return null;
  }

  private Session getUnfinishedSession(Pilot pilot, List<Session> chronoSessions, Chronometer chrono) {
    for (LapTime pilotLap : pilot.getLaps()) {
      Session pilotLapSession = pilotLap.getSession();
      // Intersect with chronoSessions
      for (Session chronoSession : chronoSessions) {
        if (chronoSession.id == pilotLapSession.id) {
          // This is a lap of a session that contains this chronometer
          boolean containsAnIntermediateForChrono = false;
          for (Ping intermediate : pilotLap.getIntermediates()) {
            if (intermediate.getChrono().id == chrono.id) {
              containsAnIntermediateForChrono = true;
            }
          }
          if (!containsAnIntermediateForChrono) {
            return pilotLapSession;
          }
        }
      }
    }
    return null;
  }

  private boolean containsPilot(Session s, Pilot pilot) {
    if (s.getPilots() != null) {
      for (Pilot p : s.getPilots()) {
        if (p.id == pilot.id) {
          return true;
        }
      }
    }
    return false;
  }

  private List<Session> sessionsWithShortestDistance(List<Session> sessions, Instant pingTime) {
    List<Session> sessionsWithShortestDistance = new ArrayList<>();
    Duration mostRelevantDistance = Duration.ofMillis(Long.MAX_VALUE);
    for (Session session : sessions) {
      Duration distance = duration(session, pingTime);
      if (distance.equals(mostRelevantDistance)) {
        sessionsWithShortestDistance.add(session);
      } else if (distance.compareTo(mostRelevantDistance) < 0) {
        sessionsWithShortestDistance.clear();
        sessionsWithShortestDistance.add(session);
        mostRelevantDistance = distance;
      }
    }
    return sessionsWithShortestDistance;
  }

  private Session pickMostRelevantByDistance(List<Session> sessions, Instant pingTime) {
    List<Session> sessionsWithShortestDistance = sessionsWithShortestDistance(sessions, pingTime);
    if (!sessionsWithShortestDistance.isEmpty()) {
      return sessionsWithShortestDistance.get(0);
    }
    return null;
  }

  private Duration duration(Session session, Instant pingTime) {
    Instant start = session.getStart();
    Instant end = session.getEnd();
    if (pingTime.isAfter(end)) {
      return Duration.between(end, pingTime);
    } else if (pingTime.isBefore(start)) {
      return Duration.between(pingTime, start);
    }
    return Duration.ZERO;
  }

}
