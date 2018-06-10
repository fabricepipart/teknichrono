#!python3

from datetime import datetime
from random import randint

from api.check import (checkCountWithLapIndex, checkCountWithLapNumber, checkDeltaBestInIncreasingOrder,
                       checkDeltaPreviousFilled, checkNumberLaps, checkPilotFilled, checkStartsOrdered,
                       checkEndsOrdered, checkLaptimeBetween)
from api.event import addSessionToEvent
from api.laps import (getBestLapsForSession, getLapsForSession, getResultsForSession, printLaps)
from api.location import addSessionToLocation
from api.ping import ping
from api.session import (addChronometerToSession, addPilotToSession, addSession, startSession, endSession)


class SaturdayMorningTest:
  def __init__(self, championship):
    self.championship = championship
    self.fake1 = self.championship.fake1
    self.chrono = self.championship.chrono
    self.allPilots = self.championship.allPilots
    self.elitePilots = self.championship.elitePilots
    self.openPilots = self.championship.openPilots
    self.womanPilots = self.championship.womanPilots
    self.juniorPilots = self.championship.juniorPilots
    self.mercantour = self.championship.mercantour
    self.satDerby1Elite = None
    self.satDerby1Open = None
    self.satDerby1Woman = None
    self.satDerby1Junior = None

  def createSessions(self):
    # Add sessions
    print("---- Create session of Saturday morning ----")

    self.satDerby1Elite = addSession('Sat Derby 1 - Elite', datetime(2000, 1, 2, 10, 5), datetime(2000, 1, 2, 10, 10),
                                     'rc')
    addSessionToLocation(self.mercantour['id'], self.satDerby1Elite['id'])
    addSessionToEvent(self.championship.event['id'], self.satDerby1Elite['id'])
    addChronometerToSession(self.satDerby1Elite['id'], self.fake1['id'])
    addChronometerToSession(self.satDerby1Elite['id'], self.chrono['id'])
    for pilot in self.elitePilots:
      addPilotToSession(self.satDerby1Elite['id'], pilot['id'])

    self.satDerby1Open = addSession('Sat Derby 1 - Open', datetime(2000, 1, 2, 10, 15), datetime(2000, 1, 2, 10, 20),
                                    'rc')
    addSessionToLocation(self.mercantour['id'], self.satDerby1Open['id'])
    addSessionToEvent(self.championship.event['id'], self.satDerby1Open['id'])
    addChronometerToSession(self.satDerby1Open['id'], self.fake1['id'])
    addChronometerToSession(self.satDerby1Open['id'], self.chrono['id'])
    for pilot in self.openPilots:
      addPilotToSession(self.satDerby1Open['id'], pilot['id'])

    self.satDerby1Woman = addSession('Sat Derby 1 - Woman', datetime(2000, 1, 2, 10, 25), datetime(2000, 1, 2, 10, 30),
                                     'rc')
    addSessionToLocation(self.mercantour['id'], self.satDerby1Woman['id'])
    addSessionToEvent(self.championship.event['id'], self.satDerby1Woman['id'])
    addChronometerToSession(self.satDerby1Woman['id'], self.fake1['id'])
    addChronometerToSession(self.satDerby1Woman['id'], self.chrono['id'])
    for pilot in self.womanPilots:
      addPilotToSession(self.satDerby1Woman['id'], pilot['id'])

    self.satDerby1Junior = addSession('Sat Derby 1 - Junior', datetime(2000, 1, 2, 10, 35), datetime(
        2000, 1, 2, 10, 40), 'rc')
    addSessionToLocation(self.mercantour['id'], self.satDerby1Junior['id'])
    addSessionToEvent(self.championship.event['id'], self.satDerby1Junior['id'])
    addChronometerToSession(self.satDerby1Junior['id'], self.fake1['id'])
    addChronometerToSession(self.satDerby1Junior['id'], self.chrono['id'])
    for pilot in self.juniorPilots:
      addPilotToSession(self.satDerby1Junior['id'], pilot['id'])

  def test(self):
    h = 10
    m = 7
    startSession(self.satDerby1Elite['id'], datetime(2000, 1, 2, h, m, 5))
    eh, em = divmod((h * 60) + m + 2, 60)
    for pilot in self.elitePilots:
      es = randint(5, 15)
      ping(datetime(2000, 1, 2, eh, em, es, randint(0, 500000)), pilot['currentBeacon']['id'], -99, self.chrono['id'])
    endSession(self.satDerby1Elite['id'], datetime(2000, 1, 2, eh, em, 59))

    print("---- Tests Results of " + self.satDerby1Elite['name'] + "----")
    sessionLaps = getLapsForSession(self.satDerby1Elite['id'])
    printLaps(sessionLaps, True)
    checkNumberLaps(sessionLaps, len(self.elitePilots))
    checkCountWithLapIndex(sessionLaps, 1, len(self.elitePilots))
    checkCountWithLapNumber(sessionLaps, 1, len(self.elitePilots))
    checkPilotFilled(sessionLaps)
    checkLaptimeBetween(sessionLaps, 120000, 131000)
    checkStartsOrdered(sessionLaps)
    checkEndsOrdered(sessionLaps)

    sessionBests = getBestLapsForSession(self.satDerby1Elite['id'])
    printLaps(sessionBests, True)
    checkNumberLaps(sessionBests, len(self.elitePilots))
    checkCountWithLapIndex(sessionBests, 1, len(self.elitePilots))
    checkCountWithLapNumber(sessionBests, 1, len(self.elitePilots))
    checkPilotFilled(sessionBests)
    checkLaptimeBetween(sessionBests, 120000, 131000)
    checkDeltaBestInIncreasingOrder(sessionBests)
    checkDeltaPreviousFilled(sessionBests)

    sessionResults = getResultsForSession(self.satDerby1Elite['id'])
    printLaps(sessionResults, True)
    checkCountWithLapIndex(sessionResults, 1, len(self.elitePilots))
    checkCountWithLapNumber(sessionResults, 1, len(self.elitePilots))
    checkNumberLaps(sessionResults, len(self.elitePilots))
    checkPilotFilled(sessionResults)
    checkLaptimeBetween(sessionResults, 120000, 131000)
    checkDeltaBestInIncreasingOrder(sessionResults, True)
    checkDeltaPreviousFilled(sessionResults, True)

    h = 10
    m = 17
    startSession(self.satDerby1Open['id'], datetime(2000, 1, 2, h, m, 5))
    eh, em = divmod((h * 60) + m + 2, 60)
    for pilot in self.openPilots:
      es = randint(15, 25)
      ping(datetime(2000, 1, 2, eh, em, es, randint(0, 500000)), pilot['currentBeacon']['id'], -99, self.chrono['id'])
    endSession(self.satDerby1Open['id'], datetime(2000, 1, 2, eh, em, 59))

    print("---- Tests Results of " + self.satDerby1Open['name'] + "----")
    sessionLaps = getLapsForSession(self.satDerby1Open['id'])
    printLaps(sessionLaps, True)
    checkNumberLaps(sessionLaps, len(self.openPilots))
    checkCountWithLapIndex(sessionLaps, 1, len(self.openPilots))
    checkCountWithLapNumber(sessionLaps, 1, len(self.openPilots))
    checkPilotFilled(sessionLaps)
    checkLaptimeBetween(sessionLaps, 130000, 141000)
    checkStartsOrdered(sessionLaps)
    checkEndsOrdered(sessionLaps)

    sessionBests = getBestLapsForSession(self.satDerby1Open['id'])
    printLaps(sessionBests, True)
    checkNumberLaps(sessionBests, len(self.openPilots))
    checkCountWithLapIndex(sessionBests, 1, len(self.openPilots))
    checkCountWithLapNumber(sessionBests, 1, len(self.openPilots))
    checkPilotFilled(sessionBests)
    checkLaptimeBetween(sessionBests, 130000, 141000)
    checkDeltaBestInIncreasingOrder(sessionBests)
    checkDeltaPreviousFilled(sessionBests)

    sessionResults = getResultsForSession(self.satDerby1Open['id'])
    printLaps(sessionResults, True)
    checkCountWithLapIndex(sessionResults, 1, len(self.openPilots))
    checkCountWithLapNumber(sessionResults, 1, len(self.openPilots))
    checkNumberLaps(sessionResults, len(self.openPilots))
    checkPilotFilled(sessionResults)
    checkLaptimeBetween(sessionResults, 130000, 141000)
    checkDeltaBestInIncreasingOrder(sessionResults, True)
    checkDeltaPreviousFilled(sessionResults, True)

    h = 10
    m = 27
    startSession(self.satDerby1Woman['id'], datetime(2000, 1, 2, h, m, 5))
    eh, em = divmod((h * 60) + m + 2, 60)
    for pilot in self.womanPilots:
      es = randint(25, 35)
      ping(datetime(2000, 1, 2, eh, em, es, randint(0, 500000)), pilot['currentBeacon']['id'], -99, self.chrono['id'])
    endSession(self.satDerby1Woman['id'], datetime(2000, 1, 2, eh, em, 59))

    print("---- Tests Results of " + self.satDerby1Woman['name'] + "----")
    sessionLaps = getLapsForSession(self.satDerby1Woman['id'])
    printLaps(sessionLaps, True)
    checkNumberLaps(sessionLaps, len(self.womanPilots))
    checkCountWithLapIndex(sessionLaps, 1, len(self.womanPilots))
    checkCountWithLapNumber(sessionLaps, 1, len(self.womanPilots))
    checkPilotFilled(sessionLaps)
    checkLaptimeBetween(sessionLaps, 140000, 151000)
    checkStartsOrdered(sessionLaps)
    checkEndsOrdered(sessionLaps)

    sessionBests = getBestLapsForSession(self.satDerby1Woman['id'])
    printLaps(sessionBests, True)
    checkNumberLaps(sessionBests, len(self.womanPilots))
    checkCountWithLapIndex(sessionBests, 1, len(self.womanPilots))
    checkCountWithLapNumber(sessionBests, 1, len(self.womanPilots))
    checkPilotFilled(sessionBests)
    checkLaptimeBetween(sessionBests, 140000, 151000)
    checkDeltaBestInIncreasingOrder(sessionBests)
    checkDeltaPreviousFilled(sessionBests)

    sessionResults = getResultsForSession(self.satDerby1Woman['id'])
    printLaps(sessionResults, True)
    checkCountWithLapIndex(sessionResults, 1, len(self.womanPilots))
    checkCountWithLapNumber(sessionResults, 1, len(self.womanPilots))
    checkNumberLaps(sessionResults, len(self.womanPilots))
    checkPilotFilled(sessionResults)
    checkLaptimeBetween(sessionResults, 140000, 151000)
    checkDeltaBestInIncreasingOrder(sessionResults, True)
    checkDeltaPreviousFilled(sessionResults, True)

    h = 10
    m = 37
    startSession(self.satDerby1Junior['id'], datetime(2000, 1, 2, h, m, 5))
    eh, em = divmod((h * 60) + m + 2, 60)
    for pilot in self.juniorPilots:
      es = randint(35, 45)
      ping(datetime(2000, 1, 2, eh, em, es, randint(0, 500000)), pilot['currentBeacon']['id'], -99, self.chrono['id'])
    endSession(self.satDerby1Junior['id'], datetime(2000, 1, 2, eh, em, 59))

    print("---- Tests Results of " + self.satDerby1Junior['name'] + "----")
    sessionLaps = getLapsForSession(self.satDerby1Junior['id'])
    printLaps(sessionLaps, True)
    checkNumberLaps(sessionLaps, len(self.juniorPilots))
    checkCountWithLapIndex(sessionLaps, 1, len(self.juniorPilots))
    checkCountWithLapNumber(sessionLaps, 1, len(self.juniorPilots))
    checkPilotFilled(sessionLaps)
    checkLaptimeBetween(sessionLaps, 150000, 161000)
    checkStartsOrdered(sessionLaps)
    checkEndsOrdered(sessionLaps)

    sessionBests = getBestLapsForSession(self.satDerby1Junior['id'])
    printLaps(sessionBests, True)
    checkNumberLaps(sessionBests, len(self.juniorPilots))
    checkCountWithLapIndex(sessionBests, 1, len(self.juniorPilots))
    checkCountWithLapNumber(sessionBests, 1, len(self.juniorPilots))
    checkPilotFilled(sessionBests)
    checkLaptimeBetween(sessionBests, 150000, 161000)
    checkDeltaBestInIncreasingOrder(sessionBests)
    checkDeltaPreviousFilled(sessionBests)

    sessionResults = getResultsForSession(self.satDerby1Junior['id'])
    printLaps(sessionResults, True)
    checkCountWithLapIndex(sessionResults, 1, len(self.juniorPilots))
    checkCountWithLapNumber(sessionResults, 1, len(self.juniorPilots))
    checkNumberLaps(sessionResults, len(self.juniorPilots))
    checkPilotFilled(sessionResults)
    checkLaptimeBetween(sessionResults, 150000, 161000)
    checkDeltaBestInIncreasingOrder(sessionResults, True)
    checkDeltaPreviousFilled(sessionResults, True)
