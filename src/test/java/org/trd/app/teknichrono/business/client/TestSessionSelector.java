package org.trd.app.teknichrono.business.client;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.trd.app.teknichrono.model.jpa.Beacon;
import org.trd.app.teknichrono.model.jpa.Chronometer;
import org.trd.app.teknichrono.model.jpa.Pilot;
import org.trd.app.teknichrono.model.jpa.Ping;
import org.trd.app.teknichrono.model.jpa.Session;
import org.trd.app.teknichrono.model.jpa.TestLapTimeCreator;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class TestSessionSelector {

  private final long HOUR = 60 * 60 * 1000L;
  private long id = 1;
  private long now = System.currentTimeMillis();

  SessionSelector selector = new SessionSelector();

  private Ping ping = new Ping();
  private Chronometer chrono = new Chronometer();
  private Pilot pilot = new Pilot();
  private Beacon beacon = new Beacon();

  @Before
  public void prepare() {
    chrono.id++;
    ping.setChrono(chrono);
    ping.id++;
    ping.setDateTime(new Timestamp(now));
    pilot.id++;
    beacon.setPilot(pilot);
    beacon.id++;
    ping.setBeacon(beacon);
  }

  private Session addSession(long start, long end) {
    Session s = createSession(start, end);
    chrono.getSessions().add(s);
    return s;
  }

  @Test
  public void picksCurrentSession() {
    Session s0 = createSession(-1 * HOUR, HOUR);
    Session s1 = createSession(2 * HOUR, 4 * HOUR);
    Session s2 = createSession(-4 * HOUR, -2 * HOUR);
    List<Session> sessions = new ArrayList<>();
    sessions.add(s0);
    sessions.add(s1);
    sessions.add(s2);
    Session session = selector.pickMostRelevantCurrent(sessions);
    Assert.assertEquals(s0.id, session.id);
  }

  @Test
  public void picksNullIfNoSession() {
    List<Session> sessions = new ArrayList<>();
    Session session = selector.pickMostRelevantCurrent(sessions);
    Assert.assertEquals(null, session);
  }

  private Session createSession(long start, long end) {
    Session s = new Session();
    s.setId(id++);
    s.setStart(new Timestamp(now + start));
    s.setEnd(new Timestamp(now + end));
    return s;
  }

  @Test
  public void picksCurrentSessionComparedToPing() {
    Session s0 = addSession(-1 * HOUR, HOUR);
    Session s1 = addSession(2 * HOUR, 4 * HOUR);
    Session s2 = addSession(-4 * HOUR, -2 * HOUR);
    Session session = selector.pickMostRelevant(ping);
    Assert.assertEquals(s0.id, session.id);
  }

  @Test
  public void picksClosestSessionComparedToPing() {
    Session s0 = addSession(30 * HOUR, 31 * HOUR);
    Session s1 = addSession(2 * HOUR, 4 * HOUR);
    Session s2 = addSession(10 * HOUR, 12 * HOUR);
    Session s3 = addSession(22 * HOUR, 24 * HOUR);
    Session session = selector.pickMostRelevant(ping);
    Assert.assertEquals(s1.id, session.id);
  }

  @Test
  public void picksAnySessionIfDistancesEqual() {
    Session s0 = addSession(-10 * HOUR, -2 * HOUR);
    Session s1 = addSession(2 * HOUR, 4 * HOUR);
    Session s2 = addSession(10 * HOUR, 20 * HOUR);
    Session session = selector.pickMostRelevant(ping);
    Assert.assertTrue(s0.id == session.id || s1.id == session.id);
  }

  @Test
  public void picksNoneIfInMoreThanOneDay() {
    Session s0 = addSession(48 * HOUR, 50 * HOUR);
    Session s1 = addSession(72 * HOUR, 74 * HOUR);
    Session session = selector.pickMostRelevant(ping);
    Assert.assertNull(session);
  }

  @Test
  public void picksInPrioritySessionsWherePilotIsDeclared() {
    Session s0 = addSession(30 * HOUR, 31 * HOUR);
    Session s1 = addSession(2 * HOUR, 4 * HOUR);
    Session s2 = addSession(10 * HOUR, 12 * HOUR);
    Session s3 = addSession(22 * HOUR, 24 * HOUR);
    s0.getPilots().add(pilot);
    s3.getPilots().add(pilot);
    Session session = selector.pickMostRelevant(ping);
    Assert.assertEquals(s3.id, session.id);
  }

  @Test
  public void picksClosestIfPilotPartOfNoSession() {
    List otherPilots = new ArrayList();
    otherPilots.add(new Pilot());
    otherPilots.add(new Pilot());
    otherPilots.add(new Pilot());
    Session s0 = addSession(30 * HOUR, 31 * HOUR);
    Session s1 = addSession(2 * HOUR, 4 * HOUR);
    Session s2 = addSession(10 * HOUR, 12 * HOUR);
    Session s3 = addSession(22 * HOUR, 24 * HOUR);
    s0.getPilots().addAll(otherPilots);
    s1.getPilots().addAll(otherPilots);
    Session session = selector.pickMostRelevant(ping);
    Assert.assertEquals(s1.id, session.id);
  }

  @Test
  public void picksInPrioritySessionsWherePilotIsDeclaredAndSessionStarted() {
    Session s0 = addSession(3 * HOUR, 4 * HOUR);
    Session s1 = addSession(6 * HOUR, 7 * HOUR);
    Session s2 = addSession(2 * HOUR, 3 * HOUR);
    Session s3 = addSession(1 * HOUR, 2 * HOUR);
    Session s4 = addSession(4 * HOUR, 5 * HOUR);
    Session s5 = addSession(7 * HOUR, 8 * HOUR);
    Session s6 = addSession(5 * HOUR, 6 * HOUR);
    s4.getPilots().add(pilot);
    s6.getPilots().add(pilot);
    s1.getPilots().add(pilot);
    s5.getPilots().add(pilot);
    s5.setCurrent(true);
    s6.setCurrent(true);
    Session session = selector.pickMostRelevant(ping);
    Assert.assertEquals(s6.id, session.id);
  }

  @Test
  public void ifSessionsWithPilotAndStartedAndNoUnfinishedPicksFirst() {
    Session s0 = addSession(3 * HOUR, 4 * HOUR);
    Session s1 = addSession(6 * HOUR, 7 * HOUR);
    Session s2 = addSession(2 * HOUR, 3 * HOUR);
    Session s3 = addSession(1 * HOUR, 2 * HOUR);
    Session s4 = addSession(4 * HOUR, 5 * HOUR);
    Session s5 = addSession(7 * HOUR, 8 * HOUR);
    Session s6 = addSession(7 * HOUR, 10 * HOUR);
    Session s7 = addSession(7 * HOUR, 10 * HOUR);
    s4.getPilots().add(pilot);
    s6.getPilots().add(pilot);
    s1.getPilots().add(pilot);
    s5.getPilots().add(pilot);
    s7.getPilots().add(pilot);
    s5.setCurrent(true);
    s6.setCurrent(true);
    s7.setCurrent(true);

    Session session = selector.pickMostRelevant(ping);
    Assert.assertEquals(s5.id, session.id);
  }

  @Test
  public void picksInPrioritySessionsWithPilotAndStartedAndUnfinished() {
    Session s0 = addSession(3 * HOUR, 4 * HOUR);
    Session s1 = addSession(6 * HOUR, 7 * HOUR);
    Session s2 = addSession(2 * HOUR, 3 * HOUR);
    Session s3 = addSession(1 * HOUR, 2 * HOUR);
    Session s4 = addSession(4 * HOUR, 5 * HOUR);
    Session s5 = addSession(7 * HOUR, 8 * HOUR);
    Session s6 = addSession(7 * HOUR, 10 * HOUR);
    Session s7 = addSession(7 * HOUR, 10 * HOUR);
    s4.getPilots().add(pilot);
    s6.getPilots().add(pilot);
    s1.getPilots().add(pilot);
    s5.getPilots().add(pilot);
    s7.getPilots().add(pilot);
    s5.setCurrent(true);
    s6.setCurrent(true);
    s7.setCurrent(true);

    TestLapTimeCreator creator = new TestLapTimeCreator();
    creator.setPilot(pilot);
    creator.setSession(s5);
    pilot.getLaps().add(creator.createLapTimeWithIntermediates(10000, 20000, 30000, 40000));
    pilot.getLaps().add(creator.createLapTimeWithIntermediates(110000, 120000, 130000, 140000));
    pilot.getLaps().add(creator.createLapTimeWithIntermediates(210000, 220000, 230000, 240000));
    pilot.getLaps().add(creator.createLapTimeWithIntermediates(310000, 320000, 330000, 340000));

    creator.setSession(s6);
    pilot.getLaps().add(creator.createLapTimeWithIntermediates(510000, 520000, 530000, 540000));
    pilot.getLaps().add(creator.createLapTimeWithIntermediates(-1, -1, 630000, 640000));

    creator.setSession(s7);
    pilot.getLaps().add(creator.createLapTimeWithIntermediates(410000, 420000, 430000, 440000));

    Session session = selector.pickMostRelevant(ping);
    Assert.assertEquals(s6.id, session.id);
  }

}