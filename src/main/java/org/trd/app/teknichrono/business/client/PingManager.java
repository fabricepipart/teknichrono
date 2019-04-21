package org.trd.app.teknichrono.business.client;

import org.jboss.logging.Logger;
import org.trd.app.teknichrono.model.jpa.Beacon;
import org.trd.app.teknichrono.model.jpa.Chronometer;
import org.trd.app.teknichrono.model.jpa.LapTime;
import org.trd.app.teknichrono.model.jpa.Pilot;
import org.trd.app.teknichrono.model.jpa.Ping;
import org.trd.app.teknichrono.model.jpa.Session;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.List;

public class PingManager {

  private Logger logger = Logger.getLogger(PingManager.class);

  private EntityManager em;
  private SessionSelector selector = new SessionSelector();

  public PingManager(EntityManager em) {
    this.em = em;
  }

  /**
   * @param ping
   */
  public void addPing(Ping ping) {
    Beacon beacon = ping.getBeacon();
    if (beacon == null) {
      logger.error("Beacon is not in the DB, cannot updates laptimes for Ping @ " + ping.getDateTime());
      return;
    }

    Pilot pilot = ping.getBeacon().getPilot();
    if (pilot == null) {
      logger.error("Pilot is not associated to Beacon " + beacon.getId() + ", cannot updates laptimes");
      return;
    }

    Chronometer chronometer = ping.getChrono();
    if (chronometer == null) {
      logger.error("Chrono is not in the DB, cannot updates laptimes for Ping @ " + ping.getDateTime());
      return;
    }

    Session session = selector.pickMostRelevant(ping);
    if (session == null) {
      logger.error("No Session associated to Chrono " + chronometer.getId() + ", cannot updates laptimes");
      return;
    }
    addPing(ping, pilot, chronometer, session);
  }

  public void addPing(Ping ping, Pilot pilot, Chronometer chronometer, Session session) {
    int chronoIndex = session.getChronoIndex(chronometer);
    if (chronoIndex < 0) {
      logger.error("Ping error since from a chronometer '" + chronometer.getName() +
          "' that is not part of the Session " + session.getId());
      return;
    }

    long inactivity = session.getInactivity();
    if (inactivity > 0) {
      long pingTime = ping.getDateTime().getTime();
      long start = session.getStart().getTime();
      long inactivityEnd = start + inactivity;
      if (pingTime > start && pingTime < inactivityEnd) {
        logger.info("Ping ignored since Session " + session.getId() + " is during its inactivity period.");
        return;
      }
    }

    List<LapTime> previousLaptimes = pilot.getLaps();

    logger.debug("Trying to insert : " + ping);
    if (previousLaptimes != null && !previousLaptimes.isEmpty()) {
      LapTime lapTimeOfPingBefore = null;
      Ping pingBefore = null;
      LapTime lapTimeOfPingAfter = null;
      Ping pingAfter = null;
      for (LapTime lapTime : previousLaptimes) {
        for (Ping lapTimePing : lapTime.getIntermediates()) {
          if (lapTimePing.getDateTime().getTime() < ping.getDateTime().getTime()) {
            lapTimeOfPingBefore = lapTime;
            pingBefore = lapTimePing;
          } else if (lapTimePing.getDateTime().getTime() == ping.getDateTime().getTime()) {
            logger.error("Trying to store a ping that was already in DB " + ping.getDateTime());
          } else if (lapTimePing.getDateTime().getTime() > ping.getDateTime().getTime()
              && (pingAfter == null || lapTimePing.getDateTime().getTime() < pingAfter.getDateTime().getTime())) {
            lapTimeOfPingAfter = lapTime;
            pingAfter = lapTimePing;
          }
        }
      }
      logger.debug("lapTimeOfPingBefore " + lapTimeOfPingBefore);
      logger.debug("pingBefore " + pingBefore);
      logger.debug("lapTimeOfPingAfter " + lapTimeOfPingAfter);
      logger.debug("pingAfter " + pingAfter);

      if (pingBefore == null) {
        if (pingAfter == null) {
          logger.error("We had previous laptimes but none before and none after ???");
          return;
        } else {
          // Is it part of the lap of pingAfter ?
          if (chronoIndex < session.getChronoIndex(pingAfter.getChrono())) {
            int insertAtIdex = lapTimeOfPingAfter.getIntermediates().indexOf(pingAfter);
            lapTimeOfPingAfter.addIntermediates(insertAtIdex, ping);
            em.persist(lapTimeOfPingAfter);
          } else {
            LapTime lap = createLaptime(session, pilot, ping, chronometer);
            em.persist(lap);
          }
        }
      } else if (pingAfter == null) {
        // Is it part of the lap of pingBefore ?
        if (chronoIndex > session.getChronoIndex(pingBefore.getChrono())) {
          int insertAtIndex = lapTimeOfPingBefore.getIntermediates().indexOf(pingBefore) + 1;
          lapTimeOfPingBefore.addIntermediates(insertAtIndex, ping);
          em.persist(lapTimeOfPingBefore);
        } else {
          LapTime lap = createLaptime(session, pilot, ping, chronometer);
          em.persist(lap);
        }
      } else {
        // One before, one after
        // Is it part of the lap of pingBefore ?
        if (chronoIndex > session.getChronoIndex(pingBefore.getChrono())) {
          // We ll insert it in the lap of the ping before
          int insertAtIndex = lapTimeOfPingBefore.getIntermediates().indexOf(pingBefore) + 1;
          // Is this index taken by pingAfter ?
          if (lapTimeOfPingBefore.getId() == lapTimeOfPingAfter.getId()
              && chronoIndex >= session.getChronoIndex(pingAfter.getChrono())) {
            // Split
            List<Ping> toInsertInNewLap = lapTimeOfPingBefore.getIntermediates().subList(insertAtIndex,
                lapTimeOfPingBefore.getIntermediates().size());
            LapTime lap = createLaptime(session, pilot, toInsertInNewLap, chronometer);
            em.persist(lap);
            toInsertInNewLap.clear();
            lapTimeOfPingBefore.setStartDate();
            // since toInsertInNewLap is backed by original list,
            // this removes all sub-list items from the original list
            lapTimeOfPingBefore.addIntermediates(insertAtIndex, ping);
            em.persist(lapTimeOfPingBefore);
          } else {
            // Insert
            lapTimeOfPingBefore.addIntermediates(insertAtIndex, ping);
            em.persist(lapTimeOfPingBefore);
          }
        } else {
          // We ll insert it in the lap of the ping after
          if (chronoIndex < session.getChronoIndex(pingAfter.getChrono())) {
            int insertAtIdex = lapTimeOfPingAfter.getIntermediates().indexOf(pingAfter);
            lapTimeOfPingAfter.addIntermediates(insertAtIdex, ping);
            em.persist(lapTimeOfPingAfter);
          } else {
            LapTime lap = createLaptime(session, pilot, ping, chronometer);
            em.persist(lap);
          }
        }
      }
    } else {
      LapTime lap = createLaptime(session, pilot, ping, chronometer);
      em.persist(lap);
    }

    // Missing intermediate of previous existing Laptime

    // New intermediate of last Laptime

    // New Laptime / Finish laptime
    // The chronopoint is the last one of the session

    // New Laptime but not first intermediate
  }

  private LapTime createLaptime(Session session, Pilot pilot, List<Ping> toInsertInNewLap, Chronometer chronometer) {
    LapTime lap = new LapTime();
    lap.setSession(session);
    lap.setPilot(pilot);
    for (Ping p : toInsertInNewLap) {
      lap.addIntermediates(p);
    }
    return lap;
  }

  private LapTime createLaptime(Session session, Pilot pilot, Ping ping, Chronometer chronometer) {
    return createLaptime(session, pilot, Arrays.asList(ping), chronometer);
  }

}
