#!python3

from datetime import datetime
from random import randint

from api.check import (checkLaps, checkBestLaps, checkResults)
from api.laps import (getBestLapsForSession, getLapsForSession, getResultsForSession, printLaps, getBestLaps)
from api.ping import ping
from api.session_simulator import SessionSimulator


class SundayMorningTest:
  def __init__(self, championship):
    self.championship = championship
    self.fake1 = self.championship.fake1
    self.fake2 = self.championship.fake2
    self.chrono = self.championship.chrono
    self.beacons = self.championship.beacons
    self.allPilots = self.championship.allPilots
    self.elitePilots = self.championship.elitePilots
    self.openPilots = self.championship.openPilots
    self.womanPilots = self.championship.womanPilots
    self.juniorPilots = self.championship.juniorPilots
    self.valette = self.championship.valette
    self.sunDerby2Elite = SessionSimulator()
    self.sunDerby2Open = SessionSimulator()
    self.sunDerby2Woman = SessionSimulator()
    self.sunDerby2Junior = SessionSimulator()
    self.redRiver = self.championship.redRiver
    self.roubines = self.championship.roubines
    self.sunDoubleRedRiver = SessionSimulator()
    self.sunDoubleRoubines = SessionSimulator()

  def createSessions(self):
    # Add sessions
    print("---- Create session of Sunday morning ----")
    valette = self.valette
    event = self.championship.event
    beacons = self.beacons
    chronos = [self.fake1, self.chrono]
    self.sunDerby2Elite.create('Sun Derby 2 - Elite', datetime(2000, 1, 3, 10, 5), datetime(2000, 1, 3, 10, 10), 'rc', valette, event, chronos, beacons, self.elitePilots)
    self.sunDerby2Open.create('Sun Derby 2 - Open', datetime(2000, 1, 3, 10, 15), datetime(2000, 1, 3, 10, 20), 'rc', valette, event, chronos, beacons, self.openPilots)
    self.sunDerby2Woman.create('Sun Derby 2 - Woman', datetime(2000, 1, 3, 10, 25), datetime(2000, 1, 3, 10, 30), 'rc', valette, event, chronos, beacons, self.womanPilots)
    self.sunDerby2Junior.create('Sun Derby 2 - Junior', datetime(2000, 1, 3, 10, 35), datetime(2000, 1, 3, 10, 40), 'rc', valette, event, chronos, beacons, self.juniorPilots)
    chronos2 = [self.fake2, self.chrono]
    redRiver = self.redRiver
    roubines = self.roubines
    allPilots = self.allPilots
    self.sunDoubleRedRiver.create('Sun Double - Red River', datetime(2000, 1, 3, 11, 0), datetime(2000, 1, 3, 13, 00), 'tt', redRiver, event, chronos, beacons, allPilots)
    self.sunDoubleRoubines.create('Sun Double - Roubines', datetime(2000, 1, 3, 11, 0), datetime(2000, 1, 3, 13, 00), 'tt', roubines, event, chronos2, beacons, allPilots)

  def test(self):
    self.testDerby()
    self.testDouble()

  def testDouble(self):
    print("---- Double of Sunday morning ----")
    print("-- First runs")
    self.sunDoubleRedRiver.startSession()
    self.sunDoubleRoubines.startSession()
    print(" Elite and Junior run in Red River")
    self.sunDoubleRedRiver.pilots = self.elitePilots + self.juniorPilots
    self.sunDoubleRedRiver.simTimeTrial(3, 59, 10, self.fake1['id'], self.chrono['id'])
    print(" Open and Women run in Roubines")
    self.sunDoubleRoubines.pilots = self.openPilots + self.womanPilots
    self.sunDoubleRoubines.simTimeTrial(3, 59, 10, self.fake2['id'], self.chrono['id'])
    print(" Open and Women run in Red River")
    self.sunDoubleRedRiver.pilots = self.openPilots + self.womanPilots
    self.sunDoubleRedRiver.simTimeTrial(3, 59, 10, self.fake1['id'], self.chrono['id'], startShift=20)
    print(" Elite and Junior run in Roubines")
    self.sunDoubleRoubines.pilots = self.elitePilots + self.juniorPilots
    self.sunDoubleRoubines.simTimeTrial(3, 59, 10, self.fake2['id'], self.chrono['id'], startShift=20)
    print("-- Second runs")
    print(" Elite improve in Red River")
    self.sunDoubleRedRiver.pilots = self.elitePilots
    self.sunDoubleRedRiver.simTimeTrial(2, 59, 10, self.fake1['id'], self.chrono['id'], startShift=45)
    print(" Open dont improve in Red River")
    self.sunDoubleRedRiver.pilots = self.openPilots
    self.sunDoubleRedRiver.simTimeTrial(4, 59, 10, self.fake1['id'], self.chrono['id'], startShift=60)
    print(" Women improve in Roubines")
    self.sunDoubleRoubines.pilots = self.womanPilots
    self.sunDoubleRoubines.simTimeTrial(2, 59, 10, self.fake2['id'], self.chrono['id'], startShift=45)
    print(" Junior dont improve in Roubines")
    self.sunDoubleRoubines.pilots = self.juniorPilots
    self.sunDoubleRoubines.simTimeTrial(4, 59, 10, self.fake2['id'], self.chrono['id'], startShift=60)
    self.sunDoubleRedRiver.endSession()
    self.sunDoubleRoubines.endSession()
    print("---- Tests Results (general) ----")
    bestLapsRedRiver = getBestLaps(self.redRiver['id'])
    bestLapsRoubines = getBestLaps(self.roubines['id'])
    checkBestLaps(bestLapsRedRiver, 80, {2: 30, 1: 50}, {1: 30, 2: 50}, None, 120000, 240000)
    checkBestLaps(bestLapsRoubines, 80, {2: 20, 1: 60}, {1: 50, 2: 30}, None, 120000, 240000)

  def testDerby(self):
    print("---- Derby of Sunday morning ----")
    self.sunDerby2Elite.startSession()
    self.sunDerby2Elite.simRace(3, 10, self.chrono['id'])
    self.sunDerby2Elite.endSession()
    print("---- Tests Results of " + self.sunDerby2Elite.session['name'] + "----")
    checkLaps(getLapsForSession(self.sunDerby2Elite.session['id']), len(self.elitePilots), {1: len(self.elitePilots)}, {1: len(self.elitePilots)},
              self.championship.eliteCategory['name'], 180000, 190000)
    checkBestLaps(getBestLapsForSession(self.sunDerby2Elite.session['id']), len(self.elitePilots), {1: len(self.elitePilots)}, {1: len(self.elitePilots)},
                  self.championship.eliteCategory['name'], 180000, 190000)
    checkResults(getResultsForSession(self.sunDerby2Elite.session['id']), len(self.elitePilots), {1: len(self.elitePilots)}, {1: len(self.elitePilots)},
                 self.championship.eliteCategory['name'], 180000, 190000)

    self.sunDerby2Open.startSession()
    self.sunDerby2Open.simRace(3, 20, self.chrono['id'])
    # We forget to end that one
    #self.sunDerby2Open.endSession()
    print("---- Tests Results of " + self.sunDerby2Open.session['name'] + "----")
    checkLaps(getLapsForSession(self.sunDerby2Open.session['id']), len(self.openPilots), {1: len(self.openPilots)}, {1: len(self.openPilots)},
              self.championship.openCategory['name'], 180000, 200000)
    checkBestLaps(getBestLapsForSession(self.sunDerby2Open.session['id']), len(self.openPilots), {1: len(self.openPilots)}, {1: len(self.openPilots)},
                  self.championship.openCategory['name'], 180000, 200000)
    checkResults(getResultsForSession(self.sunDerby2Open.session['id']), len(self.openPilots), {1: len(self.openPilots)}, {1: len(self.openPilots)},
                 self.championship.openCategory['name'], 180000, 200000)

    self.sunDerby2Woman.startSession()
    self.sunDerby2Woman.simRace(3, 30, self.chrono['id'])
    self.sunDerby2Woman.endSession()
    checkLaps(getLapsForSession(self.sunDerby2Woman.session['id']), len(self.womanPilots), {1: len(self.womanPilots)}, {1: len(self.womanPilots)},
              self.championship.womanCategory['name'], 180000, 210000)
    checkBestLaps(getBestLapsForSession(self.sunDerby2Woman.session['id']), len(self.womanPilots), {1: len(self.womanPilots)}, {1: len(self.womanPilots)},
                  self.championship.womanCategory['name'], 180000, 210000)
    checkResults(getResultsForSession(self.sunDerby2Woman.session['id']), len(self.womanPilots), {1: len(self.womanPilots)}, {1: len(self.womanPilots)},
                 self.championship.womanCategory['name'], 180000, 210000)

    self.sunDerby2Junior.startSession()
    self.sunDerby2Junior.simRace(3, 40, self.chrono['id'])
    self.sunDerby2Junior.endSession()
    checkLaps(getLapsForSession(self.sunDerby2Junior.session['id']), len(self.juniorPilots), {1: len(self.juniorPilots)}, {1: len(self.juniorPilots)},
              self.championship.juniorCategory['name'], 180000, 220000)
    checkBestLaps(getBestLapsForSession(self.sunDerby2Junior.session['id']), len(self.juniorPilots), {1: len(self.juniorPilots)}, {1: len(self.juniorPilots)},
                  self.championship.juniorCategory['name'], 180000, 220000)
    checkResults(getResultsForSession(self.sunDerby2Junior.session['id']), len(self.juniorPilots), {1: len(self.juniorPilots)}, {1: len(self.juniorPilots)},
                 self.championship.juniorCategory['name'], 180000, 220000)
