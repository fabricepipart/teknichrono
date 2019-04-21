package org.trd.app.teknichrono.business.client;

import org.jboss.logging.Logger;
import org.trd.app.teknichrono.model.jpa.Chronometer;
import org.trd.app.teknichrono.model.jpa.LapTime;
import org.trd.app.teknichrono.model.jpa.Pilot;
import org.trd.app.teknichrono.model.jpa.Ping;
import org.trd.app.teknichrono.model.jpa.Session;

import java.util.ArrayList;
import java.util.List;

public class SessionSelector {

  private final long ONE_DAY = 1 * 24 * 60 * 60 * 1000L;

  private Logger logger = Logger.getLogger(SessionSelector.class);

  public Session pickMostRelevantCurrent(List<Session> allSessions) {
    return pickMostRelevantByDistance(allSessions, System.currentTimeMillis());
  }

  /**
   * @param ping
   * @return
   */
  public Session pickMostRelevant(Ping ping) {

    long time = ping.getDateTime().getTime();

    // Only consider sessions that contain chronometer
    List<Session> candidateSessions = new ArrayList<>(ping.getChrono().getSessions());
    // Same day
    candidateSessions.removeIf(s -> distanceBetween(s, time) > ONE_DAY);
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
        if (chronoSession.getId() == pilotLapSession.getId()) {
          // This is a lap of a session that contains this chronometer
          boolean containsAnIntermediateForChrono = false;
          for (Ping intermediate : pilotLap.getIntermediates()) {
            if (intermediate.getChrono().getId() == chrono.getId()) {
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
        if (p.getId() == pilot.getId()) {
          return true;
        }
      }
    }
    return false;
  }

  private List<Session> sessionsWithShortestDistance(List<Session> sessions, long pingTime) {
    List<Session> sessionsWithShortestDistance = new ArrayList<>();
    long mostRelevantDistance = Long.MAX_VALUE;
    for (Session session : sessions) {
      long distance = distanceBetween(session, pingTime);
      if (distance == mostRelevantDistance) {
        sessionsWithShortestDistance.add(session);
      } else if (distance < mostRelevantDistance) {
        sessionsWithShortestDistance.clear();
        sessionsWithShortestDistance.add(session);
        mostRelevantDistance = distance;
      }
    }
    return sessionsWithShortestDistance;
  }

  private Session pickMostRelevantByDistance(List<Session> sessions, long pingTime) {
    List<Session> sessionsWithShortestDistance = sessionsWithShortestDistance(sessions, pingTime);
    if (!sessionsWithShortestDistance.isEmpty()) {
      return sessionsWithShortestDistance.get(0);
    }
    return null;
  }

  private long distanceBetween(Session session, long pingTime) {
    long start = session.getStart().getTime();
    long end = session.getEnd().getTime();
    if (pingTime > end) {
      return (pingTime - end);
    } else if (pingTime < start) {
      return (start - pingTime);
    }
    return 0;
  }

}
