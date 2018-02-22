package org.trd.app.teknichrono.business;

import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;

import org.trd.app.teknichrono.model.Beacon;
import org.trd.app.teknichrono.model.Chronometer;
import org.trd.app.teknichrono.model.Event;
import org.trd.app.teknichrono.model.LapTime;
import org.trd.app.teknichrono.model.Pilot;
import org.trd.app.teknichrono.model.Ping;

public class ChronoManager {

  private EntityManager em;

  public ChronoManager(EntityManager em) {
    this.em = em;
  }

  /**
   * @param ping
   */
  public void addPing(Ping ping) {
    Beacon beacon = ping.getBeacon();
    if (beacon == null) {
      System.err.println("Beacon is not in the DB, cannot updates laptimes for Ping @ " + ping.getDateTime());
      return;
    }

    Pilot pilot = ping.getBeacon().getPilot();
    if (pilot == null) {
      System.err.println("Pilot is not associated to Beacon " + beacon.getId() + ", cannot updates laptimes");
      return;
    }

    Chronometer chronometer = ping.getChrono();
    if (chronometer == null) {
      System.err.println("Chrono is not in the DB, cannot updates laptimes for Ping @ " + ping.getDateTime());
      return;
    }
    int chronoIndex = chronometer.getChronoIndex().intValue();

    Event event = ping.getChrono().getEvent();
    if (event == null) {
      System.err.println("No Event associated to Chrono " + chronometer.getId() + ", cannot updates laptimes");
      return;
    }

    List<LapTime> previousLaptimes = pilot.getLaps();

    // System.out.println("\n\n");
    // System.out.println("----------------------------");
    // System.out.println("Trying to insert : " + ping);
    // System.out.println("----------------------------");
    if (previousLaptimes != null && !previousLaptimes.isEmpty()) {
      LapTime lapTimeOfPingBefore = null;
      Ping pingBefore = null;
      LapTime lapTimeOfPingAfter = null;
      Ping pingAfter = null;
      for (LapTime lapTime : previousLaptimes) {
        // System.out.println("Lap #" + lapTime.getId());
        for (Ping lapTimePing : lapTime.getIntermediates()) {
          // System.out.println("\t\tIntermediate #" + lapTimePing.getId() + " @
          // " + lapTimePing.getDateTime() + " ("
          // + lapTimePing.getChrono().getChronoIndex() + ") ");
          if (lapTimePing.getDateTime().getTime() < ping.getDateTime().getTime()) {
            lapTimeOfPingBefore = lapTime;
            pingBefore = lapTimePing;
          } else if (lapTimePing.getDateTime().getTime() == ping.getDateTime().getTime()) {
            System.err.println("Trying to store a ping that was already in DB " + ping.getDateTime());
          } else if (lapTimePing.getDateTime().getTime() > ping.getDateTime().getTime()
              && (pingAfter == null || lapTimePing.getDateTime().getTime() < pingAfter.getDateTime().getTime())) {
            lapTimeOfPingAfter = lapTime;
            pingAfter = lapTimePing;
          }
        }
      }
      // System.out.println("----------------------------");
      // System.out.println("lapTimeOfPingBefore " + lapTimeOfPingBefore);
      // System.out.println("pingBefore " + pingBefore);
      // System.out.println("lapTimeOfPingAfter " + lapTimeOfPingAfter);
      // System.out.println("pingAfter " + pingAfter);

      if (pingBefore == null) {
        if (pingAfter == null) {
          System.err.println("We had previous laptimes but none before and none after ???");
          return;
        } else {
          // Is it part of the lap of pingAfter ?
          if (chronoIndex < pingAfter.getChrono().getChronoIndex().intValue()) {
            int insertAtIdex = lapTimeOfPingAfter.getIntermediates().indexOf(pingAfter);
            lapTimeOfPingAfter.addIntermediates(insertAtIdex, ping);
            em.persist(lapTimeOfPingAfter);
          } else {
            LapTime lap = createLaptime(event, pilot, ping, chronometer);
            em.persist(lap);
          }
        }
      } else if (pingAfter == null) {
        // Is it part of the lap of pingBefore ?
        if (chronoIndex > pingBefore.getChrono().getChronoIndex().intValue()) {
          int insertAtIndex = lapTimeOfPingBefore.getIntermediates().indexOf(pingBefore) + 1;
          lapTimeOfPingBefore.addIntermediates(insertAtIndex, ping);
          em.persist(lapTimeOfPingBefore);
        } else {
          LapTime lap = createLaptime(event, pilot, ping, chronometer);
          em.persist(lap);
        }
      } else {
        // One before, one after
        // Is it part of the lap of pingBefore ?
        if (chronoIndex > pingBefore.getChrono().getChronoIndex().intValue()) {
          // We ll insert it in the lap of the ping before
          int insertAtIndex = lapTimeOfPingBefore.getIntermediates().indexOf(pingBefore) + 1;
          // Is this index taken by pingAfter ?
          if (lapTimeOfPingBefore.getId() == lapTimeOfPingAfter.getId()
              && chronoIndex >= pingAfter.getChrono().getChronoIndex().intValue()) {
            // Split
            List<Ping> toInsertInNewLap = lapTimeOfPingBefore.getIntermediates().subList(insertAtIndex,
                lapTimeOfPingBefore.getIntermediates().size());
            LapTime lap = createLaptime(event, pilot, toInsertInNewLap, chronometer);
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
          if (chronoIndex < pingAfter.getChrono().getChronoIndex().intValue()) {
            int insertAtIdex = lapTimeOfPingAfter.getIntermediates().indexOf(pingAfter);
            // Is this index taken by pingBefore ?
            if (lapTimeOfPingBefore.getId() == lapTimeOfPingAfter.getId()
                && chronoIndex <= pingBefore.getChrono().getChronoIndex().intValue()) {
              // Split
              List<Ping> toInsertInNewLap = lapTimeOfPingAfter.getIntermediates().subList(0, insertAtIdex);
              LapTime lap = createLaptime(event, pilot, toInsertInNewLap, chronometer);
              em.persist(lap);
              toInsertInNewLap.clear();
              lapTimeOfPingAfter.setStartDate();
              // since toInsertInNewLap is backed by original list, this removes
              // all sub-list items from the original list
              lapTimeOfPingAfter.addIntermediates(0, ping);
              em.persist(lapTimeOfPingAfter);
            } else {
              lapTimeOfPingAfter.addIntermediates(insertAtIdex, ping);
              em.persist(lapTimeOfPingAfter);
            }
          } else {
            LapTime lap = createLaptime(event, pilot, ping, chronometer);
            em.persist(lap);
          }
        }
      }
    } else {
      LapTime lap = createLaptime(event, pilot, ping, chronometer);
      em.persist(lap);
    }

    // Missing intermediate of previous existing Laptime

    // New intermediate of last Laptime

    // New Laptime / Finish laptime
    // The chronopoint is the last one of the event

    // New Laptime but not first intermediate
  }

  private LapTime createLaptime(Event event, Pilot pilot, List<Ping> toInsertInNewLap, Chronometer chronometer) {
    LapTime lap = new LapTime();
    lap.setEvent(event);
    lap.setPilot(pilot);
    for (Ping p : toInsertInNewLap) {
      lap.addIntermediates(p);
    }
    return lap;
  }

  private LapTime createLaptime(Event event, Pilot pilot, Ping ping, Chronometer chronometer) {
    return createLaptime(event, pilot, Arrays.asList(ping), chronometer);
  }

}
