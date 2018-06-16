#!python3

from datetime import datetime
from random import randint

from api.check import (checkCountWithLapIndex, checkCountWithLapNumber, checkDeltaBestInIncreasingOrder,
                       checkDeltaPreviousFilled, checkLaptimeFilled, checkNumberLaps, checkPilotFilled,
                       checkStartsOrdered, checkEndsOrdered, checkLaps, checkBestLaps, checkResults)
from api.event import addSessionToEvent
from api.laps import (getBestLapsForSession, getLapsForSession, getResultsForSession, printLaps)
from api.location import addSessionToLocation
from api.ping import ping
from api.session import (addChronometerToSession, addPilotToSession, addSession, startSession, endSession,
                         getSessionByName)
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
    friPm16Run1.create('Fri pm Boarder X 1/16 #1', datetime(2000, 1, 1, 14, 0), datetime(2000, 1, 1, 14, 4), 'rc',
                       self.boarderCross, self.event, self.chronos)
    friPm16Run2 = SessionSimulator()
    friPm16Run2.create('Fri pm Boarder X 1/16 #2', datetime(2000, 1, 1, 14, 4), datetime(2000, 1, 1, 14, 8), 'rc',
                       self.boarderCross, self.event, self.chronos)
    friPm16Run3 = SessionSimulator()
    friPm16Run3.create('Fri pm Boarder X 1/16 #3', datetime(2000, 1, 1, 14, 8), datetime(2000, 1, 1, 14, 12), 'rc',
                       self.boarderCross, self.event, self.chronos)
    friPm16Run4 = SessionSimulator()
    friPm16Run4.create('Fri pm Boarder X 1/16 #4', datetime(2000, 1, 1, 14, 12), datetime(2000, 1, 1, 14, 16), 'rc',
                       self.boarderCross, self.event, self.chronos)
    friPm16Run5 = SessionSimulator()
    friPm16Run5.create('Fri pm Boarder X 1/16 #5', datetime(2000, 1, 1, 14, 16), datetime(2000, 1, 1, 14, 20), 'rc',
                       self.boarderCross, self.event, self.chronos)
    friPm16Run6 = SessionSimulator()
    friPm16Run6.create('Fri pm Boarder X 1/16 #6', datetime(2000, 1, 1, 14, 20), datetime(2000, 1, 1, 14, 24), 'rc',
                       self.boarderCross, self.event, self.chronos)
    friPm16Run7 = SessionSimulator()
    friPm16Run7.create('Fri pm Boarder X 1/16 #7', datetime(2000, 1, 1, 14, 24), datetime(2000, 1, 1, 14, 28), 'rc',
                       self.boarderCross, self.event, self.chronos)
    friPm16Run8 = SessionSimulator()
    friPm16Run8.create('Fri pm Boarder X 1/16 #8', datetime(2000, 1, 1, 14, 28), datetime(2000, 1, 1, 14, 32), 'rc',
                       self.boarderCross, self.event, self.chronos)
    friPm16Run9 = SessionSimulator()
    friPm16Run9.create('Fri pm Boarder X 1/16 #9', datetime(2000, 1, 1, 14, 32), datetime(2000, 1, 1, 14, 36), 'rc',
                       self.boarderCross, self.event, self.chronos)
    friPm16Run10 = SessionSimulator()
    friPm16Run10.create('Fri pm Boarder X 1/16 #10', datetime(2000, 1, 1, 14, 36), datetime(2000, 1, 1, 14, 40), 'rc',
                        self.boarderCross, self.event, self.chronos)
    friPm16Run11 = SessionSimulator()
    friPm16Run11.create('Fri pm Boarder X 1/16 #11', datetime(2000, 1, 1, 14, 40), datetime(2000, 1, 1, 14, 44), 'rc',
                        self.boarderCross, self.event, self.chronos)
    friPm16Run12 = SessionSimulator()
    friPm16Run12.create('Fri pm Boarder X 1/16 #12', datetime(2000, 1, 1, 14, 44), datetime(2000, 1, 1, 14, 48), 'rc',
                        self.boarderCross, self.event, self.chronos)
    friPm16Run13 = SessionSimulator()
    friPm16Run13.create('Fri pm Boarder X 1/16 #13', datetime(2000, 1, 1, 14, 48), datetime(2000, 1, 1, 14, 52), 'rc',
                        self.boarderCross, self.event, self.chronos)
    friPm16Run14 = SessionSimulator()
    friPm16Run14.create('Fri pm Boarder X 1/16 #14', datetime(2000, 1, 1, 14, 52), datetime(2000, 1, 1, 14, 56), 'rc',
                        self.boarderCross, self.event, self.chronos)
    friPm16Run15 = SessionSimulator()
    friPm16Run15.create('Fri pm Boarder X 1/16 #15', datetime(2000, 1, 1, 14, 56), datetime(2000, 1, 1, 15, 0), 'rc',
                        self.boarderCross, self.event, self.chronos)
    friPm16Run16 = SessionSimulator()
    friPm16Run16.create('Fri pm Boarder X 1/16 #16', datetime(2000, 1, 1, 15, 0), datetime(2000, 1, 1, 15, 4), 'rc',
                        self.boarderCross, self.event, self.chronos)
    self.friPm16Sessions = [
        friPm16Run1, friPm16Run2, friPm16Run3, friPm16Run4, friPm16Run5, friPm16Run6, friPm16Run7, friPm16Run8,
        friPm16Run9, friPm16Run10, friPm16Run11, friPm16Run12, friPm16Run13, friPm16Run14, friPm16Run15, friPm16Run16
    ]

  def createPm8Sessions(self):
    friPm8Run1 = SessionSimulator()
    friPm8Run1.create('Fri pm Boarder X 1/8 #1', datetime(2000, 1, 1, 15, 16), datetime(2000, 1, 1, 15, 20), 'rc',
                      self.boarderCross, self.event, self.chronos)
    friPm8Run2 = SessionSimulator()
    friPm8Run2.create('Fri pm Boarder X 1/8 #2', datetime(2000, 1, 1, 15, 20), datetime(2000, 1, 1, 15, 24), 'rc',
                      self.boarderCross, self.event, self.chronos)
    friPm8Run3 = SessionSimulator()
    friPm8Run3.create('Fri pm Boarder X 1/8 #3', datetime(2000, 1, 1, 15, 24), datetime(2000, 1, 1, 15, 28), 'rc',
                      self.boarderCross, self.event, self.chronos)
    friPm8Run4 = SessionSimulator()
    friPm8Run4.create('Fri pm Boarder X 1/8 #4', datetime(2000, 1, 1, 15, 28), datetime(2000, 1, 1, 15, 32), 'rc',
                      self.boarderCross, self.event, self.chronos)
    friPm8Run5 = SessionSimulator()
    friPm8Run5.create('Fri pm Boarder X 1/8 #5', datetime(2000, 1, 1, 15, 32), datetime(2000, 1, 1, 15, 36), 'rc',
                      self.boarderCross, self.event, self.chronos)
    friPm8Run6 = SessionSimulator()
    friPm8Run6.create('Fri pm Boarder X 1/8 #6', datetime(2000, 1, 1, 15, 36), datetime(2000, 1, 1, 15, 40), 'rc',
                      self.boarderCross, self.event, self.chronos)
    friPm8Run7 = SessionSimulator()
    friPm8Run7.create('Fri pm Boarder X 1/8 #7', datetime(2000, 1, 1, 15, 40), datetime(2000, 1, 1, 15, 44), 'rc',
                      self.boarderCross, self.event, self.chronos)
    friPm8Run8 = SessionSimulator()
    friPm8Run8.create('Fri pm Boarder X 1/8 #8', datetime(2000, 1, 1, 15, 44), datetime(2000, 1, 1, 15, 48), 'rc',
                      self.boarderCross, self.event, self.chronos)
    self.friPm8Sessions = [
        friPm8Run1, friPm8Run2, friPm8Run3, friPm8Run4, friPm8Run5, friPm8Run6, friPm8Run7, friPm8Run8
    ]

  def createPm4Sessions(self):
    friPm4Run1 = SessionSimulator()
    friPm4Run1.create('Fri pm Boarder X 1/4 #1', datetime(2000, 1, 1, 16, 0), datetime(2000, 1, 1, 16, 4), 'rc',
                      self.boarderCross, self.event, self.chronos)
    friPm4Run2 = SessionSimulator()
    friPm4Run2.create('Fri pm Boarder X 1/4 #2', datetime(2000, 1, 1, 16, 4), datetime(2000, 1, 1, 16, 8), 'rc',
                      self.boarderCross, self.event, self.chronos)
    friPm4Run3 = SessionSimulator()
    friPm4Run3.create('Fri pm Boarder X 1/4 #3', datetime(2000, 1, 1, 16, 8), datetime(2000, 1, 1, 16, 12), 'rc',
                      self.boarderCross, self.event, self.chronos)
    friPm4Run4 = SessionSimulator()
    friPm4Run4.create('Fri pm Boarder X 1/4 #4', datetime(2000, 1, 1, 16, 12), datetime(2000, 1, 1, 16, 16), 'rc',
                      self.boarderCross, self.event, self.chronos)
    self.friPm4Sessions = [friPm4Run1, friPm4Run2, friPm4Run3, friPm4Run4]

  def createPmSemiSessions(self):
    friPmSemiRun1 = SessionSimulator()
    friPmSemiRun1.create('Fri pm Boarder X 1/2 #1', datetime(2000, 1, 1, 16, 30), datetime(2000, 1, 1, 16, 34), 'rc',
                         self.boarderCross, self.event, self.chronos)
    friPmSemiRun2 = SessionSimulator()
    friPmSemiRun2.create('Fri pm Boarder X 1/2 #2', datetime(2000, 1, 1, 16, 34), datetime(2000, 1, 1, 16, 38), 'rc',
                         self.boarderCross, self.event, self.chronos)
    self.friPmSemiSessions = [friPmSemiRun1, friPmSemiRun2]

  def createPmFinale(self):
    self.friPmFinale = SessionSimulator()
    self.friPmFinale.create('Fri pm Boarder X Finale', datetime(2000, 1, 1, 16, 45), datetime(2000, 1, 1, 17, 0), 'rc',
                            self.boarderCross, self.event, self.chronos)

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
    beaconsPerSession = {}
    for i in range(0, 80):
      s = self.friPm16Sessions[(i % 16)].session
      addPilotToSession(s['id'], friMorningChronoResults[i]['pilot']['id'])
      beaconsForSession = beaconsPerSession.get(s['id'])
      if not beaconsForSession:
        beaconsPerSession[s['id']] = [friMorningChronoResults[i]['pilot']['beaconNumber']]
      else:
        beaconsForSession.append(friMorningChronoResults[i]['pilot']['beaconNumber'])

    startHour = 14
    startMinute = 1
    startDelta = 4
    sessionIndex = 0
    for s in self.friPm16Sessions:
      session = s.session
      # Starts all together
      h, m = divmod((startHour * 60) + startMinute + (sessionIndex * startDelta), 60)
      startSession(session['id'], datetime(2000, 1, 1, h, m, 0))
      # Ends
      beaconsOfSession = beaconsPerSession[session['id']]
      eh, em = divmod((h * 60) + m + 2, 60)
      for beaconNumber in beaconsOfSession:
        es = randint(0, 30)
        # This one falls :)
        if beaconNumber == 32:
          continue
        ping(
            datetime(2000, 1, 1, eh, em, es, randint(0, 500000)), self.beacons[beaconNumber]['id'], -99,
            self.chronos[-1]['id'])
      endSession(session['id'], datetime(2000, 1, 1, eh, em, 59))
      sessionIndex += 1

    friPm16SessionsResults = []
    for s in self.friPm16Sessions:
      session = s.session
      print("---- Tests Results of " + session['name'] + "----")
      sessionLaps = getLapsForSession(session['id'])
      printLaps(sessionLaps, True)
      # Did not finish
      if 32 in beaconsPerSession[session['id']]:
        checkNumberLaps(sessionLaps, 4)
        checkCountWithLapIndex(sessionLaps, 1, 4)
        checkCountWithLapNumber(sessionLaps, 1, 4)
      else:
        checkNumberLaps(sessionLaps, 5)
        checkCountWithLapIndex(sessionLaps, 1, 5)
        checkCountWithLapNumber(sessionLaps, 1, 5)
      checkPilotFilled(sessionLaps)
      checkLaptimeFilled(sessionLaps)
      checkStartsOrdered(sessionLaps)
      checkEndsOrdered(sessionLaps)

      sessionBests = getBestLapsForSession(session['id'])
      printLaps(sessionBests, True)
      if 32 in beaconsPerSession[session['id']]:
        checkNumberLaps(sessionBests, 4)
        checkCountWithLapIndex(sessionBests, 1, 4)
        checkCountWithLapNumber(sessionBests, 1, 4)
      else:
        checkNumberLaps(sessionBests, 5)
        checkCountWithLapIndex(sessionBests, 1, 5)
        checkCountWithLapNumber(sessionBests, 1, 5)
      checkPilotFilled(sessionBests)
      checkLaptimeFilled(sessionBests)
      checkDeltaBestInIncreasingOrder(sessionBests)
      checkDeltaPreviousFilled(sessionBests)

      sessionResults = getResultsForSession(session['id'])
      friPm16SessionsResults.append(sessionResults)
      printLaps(sessionResults, True)
      if 32 in beaconsPerSession[session['id']]:
        checkCountWithLapIndex(sessionResults, 1, 4)
        checkCountWithLapNumber(sessionResults, 1, 4)
        checkCountWithLapIndex(sessionResults, 0, 1)
        checkCountWithLapNumber(sessionResults, 0, 1)
      else:
        checkCountWithLapIndex(sessionResults, 1, 5)
        checkCountWithLapNumber(sessionResults, 1, 5)
      checkNumberLaps(sessionResults, 5)
      checkPilotFilled(sessionResults)
      checkLaptimeFilled(sessionResults, True)
      checkDeltaBestInIncreasingOrder(sessionResults, True)
      checkDeltaPreviousFilled(sessionResults, True)

    # -- 1/8 th - 8 x 6
    print("---- 1 / 8 th ----")
    # We keep 3 best
    # Order from results of 1/16 th
    beaconsPerSession = {}
    for i in range(0, 8):
      s = self.friPm8Sessions[i].session
      results1 = friPm16SessionsResults[2 * i]
      results2 = friPm16SessionsResults[(2 * i) + 1]
      addPilotToSession(s['id'], results1[0]['pilot']['id'])
      addPilotToSession(s['id'], results1[1]['pilot']['id'])
      addPilotToSession(s['id'], results1[2]['pilot']['id'])
      addPilotToSession(s['id'], results2[0]['pilot']['id'])
      addPilotToSession(s['id'], results2[1]['pilot']['id'])
      addPilotToSession(s['id'], results2[2]['pilot']['id'])
      beaconsPerSession[s['id']] = [
          results1[0]['pilot']['beaconNumber'],
          results1[1]['pilot']['beaconNumber'],
          results1[2]['pilot']['beaconNumber'],
          results2[0]['pilot']['beaconNumber'],
          results2[1]['pilot']['beaconNumber'],
          results2[2]['pilot']['beaconNumber'],
      ]

    startHour = 15
    startMinute = 16
    startDelta = 4
    sessionIndex = 0
    for s in self.friPm8Sessions:
      session = s.session
      # Starts all together
      h, m = divmod((startHour * 60) + startMinute + (sessionIndex * startDelta), 60)
      startSession(session['id'], datetime(2000, 1, 1, h, m, 5))
      # Ends
      beaconsOfSession = beaconsPerSession[session['id']]
      # This one falls :)
      doesNotFinish = beaconsOfSession[2]
      eh, em = divmod((h * 60) + m + 2, 60)
      for beaconNumber in beaconsOfSession:
        es = randint(0, 30)
        if beaconNumber != doesNotFinish:
          ping(
              datetime(2000, 1, 1, eh, em, es, randint(0, 500000)), self.beacons[beaconNumber]['id'], -99,
              self.chronos[-1]['id'])
      endSession(session['id'], datetime(2000, 1, 1, eh, em, 59))
      sessionIndex += 1

    friPm8SessionsResults = []
    for s in self.friPm8Sessions:
      session = s.session
      print("---- Tests Results of " + session['name'] + "----")
      sessionLaps = getLapsForSession(session['id'])
      printLaps(sessionLaps, True)
      checkNumberLaps(sessionLaps, 5)
      checkCountWithLapIndex(sessionLaps, 1, 5)
      checkCountWithLapNumber(sessionLaps, 1, 5)
      checkPilotFilled(sessionLaps)
      checkLaptimeFilled(sessionLaps)
      checkStartsOrdered(sessionLaps)
      checkEndsOrdered(sessionLaps)

      sessionBests = getBestLapsForSession(session['id'])
      printLaps(sessionBests, True)
      checkNumberLaps(sessionBests, 5)
      checkCountWithLapIndex(sessionBests, 1, 5)
      checkCountWithLapNumber(sessionBests, 1, 5)
      checkPilotFilled(sessionBests)
      checkLaptimeFilled(sessionBests)
      checkDeltaBestInIncreasingOrder(sessionBests)
      checkDeltaPreviousFilled(sessionBests)

      sessionResults = getResultsForSession(session['id'])
      friPm8SessionsResults.append(sessionResults)
      printLaps(sessionResults, True)
      checkCountWithLapIndex(sessionResults, 1, 5)
      checkCountWithLapNumber(sessionResults, 1, 5)
      checkNumberLaps(sessionResults, 6)
      checkPilotFilled(sessionResults)
      checkLaptimeFilled(sessionResults, True)
      checkDeltaBestInIncreasingOrder(sessionResults, True)
      checkDeltaPreviousFilled(sessionResults, True)

    # -- 1/4 th - 4 x 6
    print("---- 1 / 4 th ----")
    # We keep 3 best
    # Order from results of 1/8 th
    beaconsPerSession = {}
    for i in range(0, 4):
      s = self.friPm4Sessions[i].session
      results1 = friPm8SessionsResults[2 * i]
      results2 = friPm8SessionsResults[(2 * i) + 1]
      addPilotToSession(s['id'], results1[0]['pilot']['id'])
      addPilotToSession(s['id'], results1[1]['pilot']['id'])
      addPilotToSession(s['id'], results1[2]['pilot']['id'])
      addPilotToSession(s['id'], results2[0]['pilot']['id'])
      addPilotToSession(s['id'], results2[1]['pilot']['id'])
      addPilotToSession(s['id'], results2[2]['pilot']['id'])
      beaconsPerSession[s['id']] = [
          results1[0]['pilot']['beaconNumber'],
          results1[1]['pilot']['beaconNumber'],
          results1[2]['pilot']['beaconNumber'],
          results2[0]['pilot']['beaconNumber'],
          results2[1]['pilot']['beaconNumber'],
          results2[2]['pilot']['beaconNumber'],
      ]

    startHour = 16
    startMinute = 0
    startDelta = 4
    sessionIndex = 0
    for s in self.friPm4Sessions:
      session = s.session
      # Starts all together
      h, m = divmod((startHour * 60) + startMinute + (sessionIndex * startDelta), 60)
      startSession(session['id'], datetime(2000, 1, 1, h, m, 5))
      # Ends
      beaconsOfSession = beaconsPerSession[session['id']]
      # This one falls :)
      doesNotFinish = beaconsOfSession[2]
      eh, em = divmod((h * 60) + m + 2, 60)
      for beaconNumber in beaconsOfSession:
        es = randint(0, 30)
        if beaconNumber != doesNotFinish:
          ping(
              datetime(2000, 1, 1, eh, em, es, randint(0, 500000)), self.beacons[beaconNumber]['id'], -99,
              self.chronos[-1]['id'])
      endSession(session['id'], datetime(2000, 1, 1, eh, em, 59))
      sessionIndex += 1

    friPm4SessionsResults = []
    for s in self.friPm4Sessions:
      session = s.session
      print("---- Tests Results of " + session['name'] + "----")
      sessionLaps = getLapsForSession(session['id'])
      printLaps(sessionLaps, True)
      checkNumberLaps(sessionLaps, 5)
      checkCountWithLapIndex(sessionLaps, 1, 5)
      checkCountWithLapNumber(sessionLaps, 1, 5)
      checkPilotFilled(sessionLaps)
      checkLaptimeFilled(sessionLaps)
      checkStartsOrdered(sessionLaps)
      checkEndsOrdered(sessionLaps)

      sessionBests = getBestLapsForSession(session['id'])
      printLaps(sessionBests, True)
      checkNumberLaps(sessionBests, 5)
      checkCountWithLapIndex(sessionBests, 1, 5)
      checkCountWithLapNumber(sessionBests, 1, 5)
      checkPilotFilled(sessionBests)
      checkLaptimeFilled(sessionBests)
      checkDeltaBestInIncreasingOrder(sessionBests)
      checkDeltaPreviousFilled(sessionBests)

      sessionResults = getResultsForSession(session['id'])
      friPm4SessionsResults.append(sessionResults)
      printLaps(sessionResults, True)
      checkCountWithLapIndex(sessionResults, 1, 5)
      checkCountWithLapNumber(sessionResults, 1, 5)
      checkNumberLaps(sessionResults, 6)
      checkPilotFilled(sessionResults)
      checkLaptimeFilled(sessionResults, True)
      checkDeltaBestInIncreasingOrder(sessionResults, True)
      checkDeltaPreviousFilled(sessionResults, True)

    # -- 1/2 th - 2 x 6
    print("---- 1 / 2 th ----")
    # We keep 3 best
    beaconsPerSession = {}
    for i in range(0, 2):
      s = self.friPmSemiSessions[i].session
      results1 = friPm4SessionsResults[2 * i]
      results2 = friPm4SessionsResults[(2 * i) + 1]
      addPilotToSession(s['id'], results1[0]['pilot']['id'])
      addPilotToSession(s['id'], results1[1]['pilot']['id'])
      addPilotToSession(s['id'], results1[2]['pilot']['id'])
      addPilotToSession(s['id'], results2[0]['pilot']['id'])
      addPilotToSession(s['id'], results2[1]['pilot']['id'])
      addPilotToSession(s['id'], results2[2]['pilot']['id'])
      beaconsPerSession[s['id']] = [
          results1[0]['pilot']['beaconNumber'],
          results1[1]['pilot']['beaconNumber'],
          results1[2]['pilot']['beaconNumber'],
          results2[0]['pilot']['beaconNumber'],
          results2[1]['pilot']['beaconNumber'],
          results2[2]['pilot']['beaconNumber'],
      ]

    startHour = 16
    startMinute = 30
    startDelta = 4
    sessionIndex = 0
    for s in self.friPmSemiSessions:
      session = s.session
      # Starts all together
      h, m = divmod((startHour * 60) + startMinute + (sessionIndex * startDelta), 60)
      startSession(session['id'], datetime(2000, 1, 1, h, m, 5))
      # Ends
      beaconsOfSession = beaconsPerSession[session['id']]
      # This one falls :)
      doesNotFinish = beaconsOfSession[2]
      eh, em = divmod((h * 60) + m + 2, 60)
      for beaconNumber in beaconsOfSession:
        es = randint(0, 30)
        if beaconNumber != doesNotFinish:
          ping(
              datetime(2000, 1, 1, eh, em, es, randint(0, 500000)), self.beacons[beaconNumber]['id'], -99,
              self.chronos[-1]['id'])
      endSession(session['id'], datetime(2000, 1, 1, eh, em, 59))
      sessionIndex += 1

    friPmSemiSessionsResults = []
    for s in self.friPmSemiSessions:
      session = s.session
      print("---- Tests Results of " + session['name'] + "----")
      sessionLaps = getLapsForSession(session['id'])
      printLaps(sessionLaps, True)
      checkNumberLaps(sessionLaps, 5)
      checkCountWithLapIndex(sessionLaps, 1, 5)
      checkCountWithLapNumber(sessionLaps, 1, 5)
      checkPilotFilled(sessionLaps)
      checkLaptimeFilled(sessionLaps)
      checkStartsOrdered(sessionLaps)
      checkEndsOrdered(sessionLaps)

      sessionBests = getBestLapsForSession(session['id'])
      printLaps(sessionBests, True)
      checkNumberLaps(sessionBests, 5)
      checkCountWithLapIndex(sessionBests, 1, 5)
      checkCountWithLapNumber(sessionBests, 1, 5)
      checkPilotFilled(sessionBests)
      checkLaptimeFilled(sessionBests)
      checkDeltaBestInIncreasingOrder(sessionBests)
      checkDeltaPreviousFilled(sessionBests)

      sessionResults = getResultsForSession(session['id'])
      friPmSemiSessionsResults.append(sessionResults)
      printLaps(sessionResults, True)
      checkCountWithLapIndex(sessionResults, 1, 5)
      checkCountWithLapNumber(sessionResults, 1, 5)
      checkNumberLaps(sessionResults, 6)
      checkPilotFilled(sessionResults)
      checkLaptimeFilled(sessionResults, True)
      checkDeltaBestInIncreasingOrder(sessionResults, True)
      checkDeltaPreviousFilled(sessionResults, True)
    # -- Finale - 1 x 6
    print("---- Finale ----")
    # We keep 3 best
    addBestsOfSessions(friPmSemiSessionsResults[0], friPmSemiSessionsResults[1], 3, self.friPmFinale)
    self.friPmFinale.simRace(self.friPmFinale.getBeaconsIdsOfSession(self.beacons), 2, 30, self.chronos[-1]['id'])

    print("---- Tests Results of " + self.friPmFinale.session['name'] + "----")
    checkLaps(getLapsForSession(self.friPmFinale.session['id']), 6, {1: 6}, {1: 6})
    checkBestLaps(getBestLapsForSession(self.friPmFinale.session['id']), 6, {1: 6}, {1: 6})
    checkResults(getResultsForSession(self.friPmFinale.session['id']), 6, {1: 6}, {1: 6})


def addBestsOfSessions(sessionResults1, sessionResults2, numberBests, session):
  pilots = []
  for i in range(numberBests):
    session.addPilot(sessionResults1[i]['pilot'])
    session.addPilot(sessionResults2[i]['pilot'])