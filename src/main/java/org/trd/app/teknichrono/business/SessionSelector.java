package org.trd.app.teknichrono.business;

import java.util.ArrayList;
import java.util.List;

import org.trd.app.teknichrono.model.Chronometer;
import org.trd.app.teknichrono.model.Ping;
import org.trd.app.teknichrono.model.Session;

public class SessionSelector {

  public Session pickMostRelevantCurrent(List<Session> allSessions) {
    return pickMostRelevant(allSessions, null);
  }

  public Session pickMostRelevant(List<Session> sessions, Ping ping) {
    List<Session> currentSessions = new ArrayList<>(sessions);
    final Chronometer chrono = ping != null ? ping.getChrono() : null;
    currentSessions.removeIf(s -> sessionCanBeCurrent(s, chrono));
    long time = ping != null ? ping.getDateTime().getTime() : System.currentTimeMillis();
    if (!currentSessions.isEmpty()) {
      return pickMostRelevantByDistance(currentSessions, chrono, time);
    }
    return pickMostRelevantByDistance(sessions, chrono, time);
  }

  private boolean sessionCanBeCurrent(Session s, Chronometer c) {
    boolean candidate = true;
    candidate &= s.isCurrent();
    if (c != null) {
      candidate &= sessionUsesChrono(s, c);
    }
    return candidate;
  }

  private boolean sessionUsesChrono(Session s, Chronometer c) {
    boolean usesChronometer = false;
    for (Chronometer chrono : s.getChronometers()) {
      usesChronometer |= (chrono.getId() == c.getId());
    }
    return usesChronometer;
  }

  private Session pickMostRelevantByDistance(List<Session> sessions, Chronometer chrono, long pingTime) {
    Session mostRelevant = null;
    long mostRelevantDistance = Long.MAX_VALUE;
    for (Session session : sessions) {
      if (chrono != null && !sessionUsesChrono(session, chrono)) {
        continue;
      }
      long distance = distanceBetween(session, pingTime);
      if (distance == 0) {
        return session;
      }
      if (distance < mostRelevantDistance) {
        mostRelevant = session;
        mostRelevantDistance = distance;
      }
    }
    return mostRelevant;
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
