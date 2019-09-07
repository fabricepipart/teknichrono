#!python3

from datetime import datetime

from api.check import (checkLaps, checkBestLaps, checkResults)
from api.laps import (getBestLapsForSession, getLapsForSession, getResultsForSession)
from api.session import getSessionByName
from api.session_simulator import SessionSimulator


class FridayAfternoonTest:
  def __init__(self, championship):
    self.championship = championship
    self.event = self.championship.event
    self.boarderCross = self.championship.boarderCross
    self.chronos = [self.championship.fake1, self.championship.chrono]
    self.allPilots = self.championship.allPilots
    self.elitePilots = self.championship.elitePilots
    self.openPilots = self.championship.openPilots
    self.womanPilots = self.championship.womanPilots
    self.juniorPilots = self.championship.juniorPilots
    self.mercantour = self.championship.mercantour
    self.sessionSim = SessionSimulator()
    self.friPm16Sessions = []
    self.friPm8Sessions = []
    self.friPm4Sessions = []
    self.friPmSemiSessions = []
    self.friPmFinale = None
    self.beacons = self.championship.beacons

  def createSessions(self):
    # Add sessions
    print("---- Create sessions of Friday afternoon ----")
    self.createPm16Sessions()
    self.createPm8Sessions()
    self.createPm4Sessions()
    self.createPmSemiSessions()
    self.createPmFinale()

  def createPm16Sessions(self):
    friPm16Run1 = SessionSimulator()
    friPm16Run1.create('Fri pm Boarder X 1/16 #1', datetime(2000, 1, 1, 14, 0), datetime(2000, 1, 1, 14, 4), 'rc', self.boarderCross, self.event, self.chronos, self.beacons)
    friPm16Run2 = SessionSimulator()
    friPm16Run2.create('Fri pm Boarder X 1/16 #2', datetime(2000, 1, 1, 14, 4), datetime(2000, 1, 1, 14, 8), 'rc', self.boarderCross, self.event, self.chronos, self.beacons)
    friPm16Run3 = SessionSimulator()
    friPm16Run3.create('Fri pm Boarder X 1/16 #3', datetime(2000, 1, 1, 14, 8), datetime(2000, 1, 1, 14, 12), 'rc', self.boarderCross, self.event, self.chronos, self.beacons)
    friPm16Run4 = SessionSimulator()
    friPm16Run4.create('Fri pm Boarder X 1/16 #4', datetime(2000, 1, 1, 14, 12), datetime(2000, 1, 1, 14, 16), 'rc', self.boarderCross, self.event, self.chronos, self.beacons)
    friPm16Run5 = SessionSimulator()
    friPm16Run5.create('Fri pm Boarder X 1/16 #5', datetime(2000, 1, 1, 14, 16), datetime(2000, 1, 1, 14, 20), 'rc', self.boarderCross, self.event, self.chronos, self.beacons)
    friPm16Run6 = SessionSimulator()
    friPm16Run6.create('Fri pm Boarder X 1/16 #6', datetime(2000, 1, 1, 14, 20), datetime(2000, 1, 1, 14, 24), 'rc', self.boarderCross, self.event, self.chronos, self.beacons)
    friPm16Run7 = SessionSimulator()
    friPm16Run7.create('Fri pm Boarder X 1/16 #7', datetime(2000, 1, 1, 14, 24), datetime(2000, 1, 1, 14, 28), 'rc', self.boarderCross, self.event, self.chronos, self.beacons)
    friPm16Run8 = SessionSimulator()
    friPm16Run8.create('Fri pm Boarder X 1/16 #8', datetime(2000, 1, 1, 14, 28), datetime(2000, 1, 1, 14, 32), 'rc', self.boarderCross, self.event, self.chronos, self.beacons)
    friPm16Run9 = SessionSimulator()
    friPm16Run9.create('Fri pm Boarder X 1/16 #9', datetime(2000, 1, 1, 14, 32), datetime(2000, 1, 1, 14, 36), 'rc', self.boarderCross, self.event, self.chronos, self.beacons)
    friPm16Run10 = SessionSimulator()
    friPm16Run10.create('Fri pm Boarder X 1/16 #10', datetime(2000, 1, 1, 14, 36), datetime(2000, 1, 1, 14, 40), 'rc', self.boarderCross, self.event, self.chronos, self.beacons)
    friPm16Run11 = SessionSimulator()
    friPm16Run11.create('Fri pm Boarder X 1/16 #11', datetime(2000, 1, 1, 14, 40), datetime(2000, 1, 1, 14, 44), 'rc', self.boarderCross, self.event, self.chronos, self.beacons)
    friPm16Run12 = SessionSimulator()
    friPm16Run12.create('Fri pm Boarder X 1/16 #12', datetime(2000, 1, 1, 14, 44), datetime(2000, 1, 1, 14, 48), 'rc', self.boarderCross, self.event, self.chronos, self.beacons)
    friPm16Run13 = SessionSimulator()
    friPm16Run13.create('Fri pm Boarder X 1/16 #13', datetime(2000, 1, 1, 14, 48), datetime(2000, 1, 1, 14, 52), 'rc', self.boarderCross, self.event, self.chronos, self.beacons)
    friPm16Run14 = SessionSimulator()
    friPm16Run14.create('Fri pm Boarder X 1/16 #14', datetime(2000, 1, 1, 14, 52), datetime(2000, 1, 1, 14, 56), 'rc', self.boarderCross, self.event, self.chronos, self.beacons)
    friPm16Run15 = SessionSimulator()
    friPm16Run15.create('Fri pm Boarder X 1/16 #15', datetime(2000, 1, 1, 14, 56), datetime(2000, 1, 1, 15, 0), 'rc', self.boarderCross, self.event, self.chronos, self.beacons)
    friPm16Run16 = SessionSimulator()
    friPm16Run16.create('Fri pm Boarder X 1/16 #16', datetime(2000, 1, 1, 15, 0), datetime(2000, 1, 1, 15, 4), 'rc', self.boarderCross, self.event, self.chronos, self.beacons)
    self.friPm16Sessions = [
        friPm16Run1, friPm16Run2, friPm16Run3, friPm16Run4, friPm16Run5, friPm16Run6, friPm16Run7, friPm16Run8, friPm16Run9, friPm16Run10, friPm16Run11, friPm16Run12, friPm16Run13,
        friPm16Run14, friPm16Run15, friPm16Run16
    ]

  def createPm8Sessions(self):
    friPm8Run1 = SessionSimulator()
    friPm8Run1.create('Fri pm Boarder X 1/8 #1', datetime(2000, 1, 1, 15, 16), datetime(2000, 1, 1, 15, 20), 'rc', self.boarderCross, self.event, self.chronos, self.beacons)
    friPm8Run2 = SessionSimulator()
    friPm8Run2.create('Fri pm Boarder X 1/8 #2', datetime(2000, 1, 1, 15, 20), datetime(2000, 1, 1, 15, 24), 'rc', self.boarderCross, self.event, self.chronos, self.beacons)
    friPm8Run3 = SessionSimulator()
    friPm8Run3.create('Fri pm Boarder X 1/8 #3', datetime(2000, 1, 1, 15, 24), datetime(2000, 1, 1, 15, 28), 'rc', self.boarderCross, self.event, self.chronos, self.beacons)
    friPm8Run4 = SessionSimulator()
    friPm8Run4.create('Fri pm Boarder X 1/8 #4', datetime(2000, 1, 1, 15, 28), datetime(2000, 1, 1, 15, 32), 'rc', self.boarderCross, self.event, self.chronos, self.beacons)
    friPm8Run5 = SessionSimulator()
    friPm8Run5.create('Fri pm Boarder X 1/8 #5', datetime(2000, 1, 1, 15, 32), datetime(2000, 1, 1, 15, 36), 'rc', self.boarderCross, self.event, self.chronos, self.beacons)
    friPm8Run6 = SessionSimulator()
    friPm8Run6.create('Fri pm Boarder X 1/8 #6', datetime(2000, 1, 1, 15, 36), datetime(2000, 1, 1, 15, 40), 'rc', self.boarderCross, self.event, self.chronos, self.beacons)
    friPm8Run7 = SessionSimulator()
    friPm8Run7.create('Fri pm Boarder X 1/8 #7', datetime(2000, 1, 1, 15, 40), datetime(2000, 1, 1, 15, 44), 'rc', self.boarderCross, self.event, self.chronos, self.beacons)
    friPm8Run8 = SessionSimulator()
    friPm8Run8.create('Fri pm Boarder X 1/8 #8', datetime(2000, 1, 1, 15, 44), datetime(2000, 1, 1, 15, 48), 'rc', self.boarderCross, self.event, self.chronos, self.beacons)
    self.friPm8Sessions = [friPm8Run1, friPm8Run2, friPm8Run3, friPm8Run4, friPm8Run5, friPm8Run6, friPm8Run7, friPm8Run8]

  def createPm4Sessions(self):
    friPm4Run1 = SessionSimulator()
    friPm4Run1.create('Fri pm Boarder X 1/4 #1', datetime(2000, 1, 1, 16, 0), datetime(2000, 1, 1, 16, 4), 'rc', self.boarderCross, self.event, self.chronos, self.beacons)
    friPm4Run2 = SessionSimulator()
    friPm4Run2.create('Fri pm Boarder X 1/4 #2', datetime(2000, 1, 1, 16, 4), datetime(2000, 1, 1, 16, 8), 'rc', self.boarderCross, self.event, self.chronos, self.beacons)
    friPm4Run3 = SessionSimulator()
    friPm4Run3.create('Fri pm Boarder X 1/4 #3', datetime(2000, 1, 1, 16, 8), datetime(2000, 1, 1, 16, 12), 'rc', self.boarderCross, self.event, self.chronos, self.beacons)
    friPm4Run4 = SessionSimulator()
    friPm4Run4.create('Fri pm Boarder X 1/4 #4', datetime(2000, 1, 1, 16, 12), datetime(2000, 1, 1, 16, 16), 'rc', self.boarderCross, self.event, self.chronos, self.beacons)
    self.friPm4Sessions = [friPm4Run1, friPm4Run2, friPm4Run3, friPm4Run4]

  def createPmSemiSessions(self):
    friPmSemiRun1 = SessionSimulator()
    friPmSemiRun1.create('Fri pm Boarder X 1/2 #1', datetime(2000, 1, 1, 16, 30), datetime(2000, 1, 1, 16, 34), 'rc', self.boarderCross, self.event, self.chronos, self.beacons)
    friPmSemiRun2 = SessionSimulator()
    friPmSemiRun2.create('Fri pm Boarder X 1/2 #2', datetime(2000, 1, 1, 16, 34), datetime(2000, 1, 1, 16, 38), 'rc', self.boarderCross, self.event, self.chronos, self.beacons)
    self.friPmSemiSessions = [friPmSemiRun1, friPmSemiRun2]

  def createPmFinale(self):
    self.friPmFinale = SessionSimulator()
    self.friPmFinale.create('Fri pm Boarder X Finale', datetime(2000, 1, 1, 16, 45), datetime(2000, 1, 1, 17, 0), 'rc', self.boarderCross, self.event, self.chronos, self.beacons)

  def test(self):
    # -------------------------------------
    #vendredi apres midi	boarder cross	boarder cross	départ à 4 ou 6	?	?
    # Quel ordre de départ? Comment sont déterminés les groupes?
    # -------------------------------------

    # -- 1/16 th - 16 x 5
    print("---- 1 / 16 th ----")
    # Order from results of morning
    morningChrono = getSessionByName('Friday am Chrono')
    friMorningChronoResults = getResultsForSession(morningChrono['id'])
    for i in range(0, 80):
      session = self.friPm16Sessions[(i % 16)]
      session.addPilot(friMorningChronoResults[i]['pilot'])

    sessionIndex = 0
    for s in self.friPm16Sessions:
      session = s.session
      # We keep best
      s.startSession()
      s.simRace(2, 30, self.chronos[-1]['id'], 0)
      s.endSession()
      sessionIndex += 1
    friPm16SessionsResults = []
    for s in self.friPm16Sessions:
      sessionResults = getResultsForSession(s.session['id'])
      friPm16SessionsResults.append(sessionResults)
      print("---- Tests Results of " + s.session['name'] + "----")
      checkLaps(getLapsForSession(s.session['id']), 5, {1: 5}, {1: 5})
      checkBestLaps(getBestLapsForSession(s.session['id']), 5, {1: 5}, {1: 5})
      checkResults(sessionResults, 5, {1: 5}, {1: 5})

    # -- 1/8 th - 8 x 6
    print("---- 1 / 8 th ----")
    sessionIndex = 0
    for s in self.friPm8Sessions:
      session = s.session
      # We keep best
      addBestsOfSessions(friPm16SessionsResults[2 * sessionIndex], friPm16SessionsResults[(2 * sessionIndex) + 1], 3, s)
      s.startSession()
      s.simRace(2, 30, self.chronos[-1]['id'], 1)
      # We forget to end that one
      # s.endSession()
      sessionIndex += 1
    friPm8SessionsResults = []
    for s in self.friPm8Sessions:
      sessionResults = getResultsForSession(s.session['id'])
      friPm8SessionsResults.append(sessionResults)
      print("---- Tests Results of " + s.session['name'] + "----")
      checkLaps(getLapsForSession(s.session['id']), 5, {1: 5}, {1: 5})
      checkBestLaps(getBestLapsForSession(s.session['id']), 5, {1: 5}, {1: 5})
      # Because we skipped the endSession, the unfinished laps still looks ongoing
      checkResults(sessionResults, 6, {1: 6}, {1: 6})

    # -- 1/4 th - 4 x 6
    print("---- 1 / 4 th ----")
    sessionIndex = 0
    for s in self.friPm4Sessions:
      session = s.session
      # We keep best
      addBestsOfSessions(friPm8SessionsResults[2 * sessionIndex], friPm8SessionsResults[(2 * sessionIndex) + 1], 3, s)
      s.startSession()
      s.simRace(2, 30, self.chronos[-1]['id'], 1)
      s.endSession()
      sessionIndex += 1
    friPm4SessionsResults = []
    for s in self.friPm4Sessions:
      sessionResults = getResultsForSession(s.session['id'])
      friPm4SessionsResults.append(sessionResults)
      print("---- Tests Results of " + s.session['name'] + "----")
      checkLaps(getLapsForSession(s.session['id']), 5, {1: 5}, {1: 5})
      checkBestLaps(getBestLapsForSession(s.session['id']), 5, {1: 5}, {1: 5})
      checkResults(sessionResults, 6, {1: 6}, {1: 6})

    # -- 1/2 th - 2 x 6
    print("---- 1 / 2 th ----")
    sessionIndex = 0
    for s in self.friPmSemiSessions:
      session = s.session
      # We keep best
      addBestsOfSessions(friPm4SessionsResults[2 * sessionIndex], friPm4SessionsResults[(2 * sessionIndex) + 1], 3, s)
      s.startSession()
      s.simRace(2, 30, self.chronos[-1]['id'], 1)
      s.endSession()
      sessionIndex += 1
    friPmSemiSessionsResults = []
    for s in self.friPmSemiSessions:
      sessionResults = getResultsForSession(s.session['id'])
      friPmSemiSessionsResults.append(sessionResults)
      print("---- Tests Results of " + s.session['name'] + "----")
      checkLaps(getLapsForSession(s.session['id']), 5, {1: 5}, {1: 5})
      checkBestLaps(getBestLapsForSession(s.session['id']), 5, {1: 5}, {1: 5})
      checkResults(sessionResults, 6, {1: 6}, {1: 6})

    # -- Finale - 1 x 6
    print("---- Finale ----")
    # We keep best
    addBestsOfSessions(friPmSemiSessionsResults[0], friPmSemiSessionsResults[1], 3, self.friPmFinale)
    self.friPmFinale.startSession()
    self.friPmFinale.simRace(2, 30, self.chronos[-1]['id'])
    self.friPmFinale.endSession()
    print("---- Tests Results of " + self.friPmFinale.session['name'] + "----")
    checkLaps(getLapsForSession(self.friPmFinale.session['id']), 6, {1: 6}, {1: 6})
    checkBestLaps(getBestLapsForSession(self.friPmFinale.session['id']), 6, {1: 6}, {1: 6})
    checkResults(getResultsForSession(self.friPmFinale.session['id']), 6, {1: 6}, {1: 6})


def addBestsOfSessions(sessionResults1, sessionResults2, numberBests, session):
  for i in range(numberBests):
    session.addPilot(sessionResults1[i]['pilot'])
    session.addPilot(sessionResults2[i]['pilot'])