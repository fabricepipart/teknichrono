from datetime import datetime
from api.session_simulator import SessionSimulator
from api.check import (checkLaps, checkBestLaps, checkResults)
from api.laps import (getBestLapsForSession, getLapsForSession, getResultsForSession)


class SaturdayAfternoonTest:
  def __init__(self, championship):
    self.championship = championship
    self.qualification = SessionSimulator()
    self.sessions32 = []
    self.sessions16 = []
    self.sessions8 = []
    self.sessions4 = []
    self.sessions2 = []
    self.finale = None
    self.dual = self.championship.dual
    self.fake1 = self.championship.fake1
    self.chrono = self.championship.chrono
    self.beacons = self.championship.beacons
    self.elitePilots = self.championship.elitePilots
    self.openPilots = self.championship.openPilots
    self.womanPilots = self.championship.womanPilots
    self.juniorPilots = self.championship.juniorPilots
    self.eliteCategory = self.championship.eliteCategory
    self.openCategory = self.championship.openCategory
    self.womanCategory = self.championship.womanCategory
    self.juniorCategory = self.championship.juniorCategory

  def createSessions(self):
    # Add sessions
    print("---- Create session of Saturday afternoon ----")
    s1 = datetime(2000, 1, 2, 14)
    e1 = datetime(2000, 1, 2, 15)
    event = self.championship.event
    location = self.dual
    chronos = [self.fake1, self.chrono]
    pilots = self.championship.allPilots
    self.qualification.create('Saturday pm qualification', s1, e1, 'tt', location, event, chronos, self.beacons, pilots)
    for i in range(1, 33):
      sessions32_i = SessionSimulator()
      name = "Sat pm dual 32th #" + str(i)
      h, m = divmod(i * 2, 60)
      eh, em = divmod(i * 2 + 2, 60)
      sessions32_i.create(name, datetime(2000, 1, 2, 16 + h, m), datetime(2000, 1, 1, 16 + eh, em), 'rc', location, event, chronos, self.beacons)
      self.sessions32.append(sessions32_i)
    for i in range(1, 17):
      name = 'Sat pm dual 16th #' + str(i)
      sessions16_i = SessionSimulator()
      sessions16_i.create(name, datetime(2000, 1, 2, 17, 20 + i * 2), datetime(2000, 1, 1, 17, 22 + i * 2), 'rc', location, event, chronos, self.beacons)
      self.sessions16.append(sessions16_i)
    for i in range(1, 9):
      name = 'Sat pm dual 8th #' + str(i)
      sessions8_i = SessionSimulator()
      sessions8_i.create(name, datetime(2000, 1, 2, 18, i * 2), datetime(2000, 1, 1, 18, i * 2 + 2), 'rc', location, event, chronos, self.beacons)
      self.sessions8.append(sessions8_i)
    for i in range(1, 5):
      name = 'Sat pm dual 4th #' + str(i)
      sessions4_i = SessionSimulator()
      sessions4_i.create(name, datetime(2000, 1, 2, 18, 30 + i * 2), datetime(2000, 1, 1, 15, 32 + i * 2 + 2), 'rc', location, event, chronos, self.beacons)
      self.sessions4.append(sessions4_i)
    for i in range(1, 3):
      name = 'Sat pm dual 2th #' + str(i)
      sessions2_i = SessionSimulator()
      sessions2_i.create(name, datetime(2000, 1, 2, 19, i * 2), datetime(2000, 1, 1, 19, i * 2 + 2), 'rc', location, event, chronos, self.beacons)
      self.sessions2.append(sessions2_i)
    self.finale = SessionSimulator()
    self.finale.create('Sat pm dual finale', datetime(2000, 1, 2, 19, 30), datetime(2000, 1, 1, 19, 32), 'rc', location, event, chronos, self.beacons)

  def test(self):
    bestLaps = self.qualify()
    qualifiedNext = self.runSessions(bestLaps, self.sessions32)
    qualifiedNext = self.runSessions(qualifiedNext, self.sessions16)
    qualifiedNext = self.runSessions(qualifiedNext, self.sessions8)
    qualifiedNext = self.runSessions(qualifiedNext, self.sessions4)
    qualifiedNext = self.runSessions(qualifiedNext, self.sessions2)
    self.runSessions(qualifiedNext, [self.finale])

  def runFinale(self, qualified):
    print("---- Dual Finale ----")

  def runSessions(self, qualified, sessions):
    results = []
    print("---- Dual " + str(len(sessions)) + "th finale ----")
    for i in range(0, len(sessions)):
      session = sessions[i]
      session.addPilot(qualified[2 * i]['pilot'])
      session.addPilot(qualified[2 * i + 1]['pilot'])
      session.startSession()
      session.simRace(1, 10, self.chrono['id'], 0)
      session.endSession()
      # We keep winner
      sessionResults = getResultsForSession(session.session['id'])
      results.append(sessionResults[0])
      print("---- Tests Results of " + session.session['name'] + "----")
      checkLaps(getLapsForSession(session.session['id']), 2, {1: 2}, {1: 2})
      checkBestLaps(getBestLapsForSession(session.session['id']), 2, {1: 2}, {1: 2})
      checkResults(sessionResults, 2, {1: 2}, {1: 2})
    return results

  def qualify(self):

    self.qualification.startSession()
    print("---- Test #1 ----")
    self.qualification.simTimeTrial(2, 19, 20, self.fake1['id'], self.chrono['id'])
    print("---- Test #2 ----")
    self.qualification.simTimeTrial(2, 19, 20, self.fake1['id'], self.chrono['id'], startShift=30)
    self.qualification.endSession()

    print("---- Tests Results (general) ----")
    bestLaps = getBestLapsForSession(self.qualification.session['id'])
    checkBestLaps(bestLaps, 80, {}, {})

    print("---- Tests Results (Elite) ----")
    bestLapsElite = getBestLapsForSession(self.qualification.session['id'], self.eliteCategory['id'])
    checkBestLaps(bestLapsElite, len(self.elitePilots), {}, {}, 'Elite', 120000, 140000)

    print("---- Tests Results (Open) ----")
    bestLapsOpen = getBestLapsForSession(self.qualification.session['id'], self.openCategory['id'])
    checkBestLaps(bestLapsOpen, len(self.openPilots), {}, {}, 'Open', 120000, 140000)

    print("---- Tests Results (Women) ----")
    bestLapsWomen = getBestLapsForSession(self.qualification.session['id'], self.womanCategory['id'])
    checkBestLaps(bestLapsWomen, len(self.womanPilots), {}, {}, 'Woman', 120000, 140000)

    print("---- Tests Results (Junior) ----")
    bestLapsJunior = getBestLapsForSession(self.qualification.session['id'], self.juniorCategory['id'])
    checkBestLaps(bestLapsJunior, len(self.juniorPilots), {}, {}, 'Junior', 120000, 140000)

    # Find the 64 participants
    qualified = bestLapsElite[0:24] + bestLapsOpen[0:16] + bestLapsWomen[0:16] + bestLapsJunior[0:8]
    return qualified