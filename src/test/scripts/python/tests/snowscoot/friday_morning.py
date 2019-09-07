#!python3

from datetime import datetime
from random import randint

from api.check import (checkCategory, checkCountWithLapIndex, checkCountWithLapNumber, checkDeltaBestInIncreasingOrder, checkDeltaPreviousFilled, checkLaptimeFilled,
                       checkNumberLaps, checkPilotFilled, checkResults, checkLaps, checkBestLaps)
from api.laps import (getBestLapsForSession, getLapsForSession, getResultsForSession, printLaps)
from api.ping import ping
from api.session import startSession
from api.session_simulator import SessionSimulator


class FridayMorningTest:
  def __init__(self, championship):
    self.championship = championship
    self.morningTest = SessionSimulator()
    self.morningChrono = SessionSimulator()
    self.boarderCross = self.championship.boarderCross
    self.fake1 = self.championship.fake1
    self.chrono = self.championship.chrono
    self.beacons = self.championship.beacons

  def createSessions(self):
    # Add sessions
    print("---- Create session of Friday morning ----")
    s1 = datetime(2000, 1, 1, 10)
    e1 = datetime(2000, 1, 1, 11)
    event = self.championship.event
    location = self.boarderCross
    chronos = [self.fake1, self.chrono]
    pilots = self.championship.allPilots
    self.morningTest.create('Friday am tests', s1, e1, 'tt', location, event, chronos, self.beacons, pilots)
    s2 = datetime(2000, 1, 1, 11)
    e2 = datetime(2000, 1, 1, 12)
    self.morningChrono.create('Friday am Chrono', s2, e2, 'tt', location, event, chronos, self.beacons)

  def test(self):
    self.borderCrossQP()
    self.borderCrossChronos()

  def borderCrossQP(self):
    # descente dans le boarder cross
    # Border cross
    # deux runs d essais (controle transpondeurs)
    # deux runs chronos
    # le meilleur retenu

    print("---- Test #1 ----")
    self.morningTest.startSession()
    self.morningTest.simTimeTrial(2, 19, 20, self.fake1['id'], self.chrono['id'], doNotStart=1, doNotFinish=1)
    print("---- Test #2 ----")
    self.morningTest.simTimeTrial(2, 19, 20, self.fake1['id'], self.chrono['id'], doNotStart=2, doNotFinish=2, startShift=30)
    # We forget to stop that one
    self.morningTest.endSession()

    print("---- Tests Results ----")
    friMorningTestsLaps = getLapsForSession(self.morningTest.session['id'])
    checkLaps(friMorningTestsLaps, 160 - 6, {1: 78, 2: 76}, {1: 2})

    friMorningTestsLapsElite = getLapsForSession(self.morningTest.session['id'], self.championship.eliteCategory['id'])
    checkLaps(friMorningTestsLapsElite, 60 - 6, {1: 28, 2: 26}, {1: 2}, "Elite")

    friMorningTestsBests = getBestLapsForSession(self.morningTest.session['id'])
    printLaps(friMorningTestsBests, True)
    checkBestLaps(friMorningTestsBests, 78, {}, {1: 2})
    #  ---- Determine startup ----

    friMorningTestsResults = getResultsForSession(self.morningTest.session['id'])
    # TODO Have chart with startup list
    # TODO Check if it should count points
    checkResults(friMorningTestsResults, 80, {}, {1: 4})

    beaconsStartOrder = []
    for i in reversed(range(30)):
      self.morningChrono.addPilot(friMorningTestsResults[i]['pilot'])
    for i in range(30, 80):
      self.morningChrono.addPilot(friMorningTestsResults[i]['pilot'])

  def borderCrossChronos(self):
    # Some do 1 test
    # Some dont test
    # Some start but dont finish
    # Some finish after expected time

    print("---- Chrono #1 ----")
    self.morningChrono.startSession()
    self.morningChrono.simTimeTrial(2, 19, 20, self.fake1['id'], self.chrono['id'], doNotStart=1, doNotFinish=1, startShift=10)

    print("---- Chrono #2 ----")
    self.morningChrono.simTimeTrial(2, 19, 20, self.fake1['id'], self.chrono['id'], doNotStart=2, doNotFinish=2, startShift=45)
    self.morningTest.endSession()

    print("---- Chrono Results ----")
    # ---- Results ----
    # ---- Checks - Asserts ----
    friMorningChronoLaps = getLapsForSession(self.morningChrono.session['id'])
    checkLaps(friMorningChronoLaps, 160 - 6, {1: 78, 2: 76}, {1: 2})

    friMorningChronoBests = getBestLapsForSession(self.morningChrono.session['id'])
    checkBestLaps(friMorningChronoBests, 78, {}, {1: 2})

    friMorningChronoResults = getResultsForSession(self.morningChrono.session['id'])
    checkResults(friMorningChronoResults, 80, {}, {1: 4})

    # Some do 1 chrono
    # Some dont chrono
    # Some finish after expected time

    # Some do 1 test
    # Some dont test
    # Some start but dont finish
    # Some finish after expected time
