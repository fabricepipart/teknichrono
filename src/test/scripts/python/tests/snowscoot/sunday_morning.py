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
    self.sunDoubleRedRiver1 = SessionSimulator()
    self.sunDoubleRoubines1 = SessionSimulator()
    self.sunDoubleRedRiver2 = SessionSimulator()
    self.sunDoubleRoubines2 = SessionSimulator()

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
    self.sunDoubleRedRiver1.create('Sun Double - Red River run #1', datetime(2000, 1, 3, 11, 0), datetime(2000, 1, 3, 11, 30), 'tt', redRiver, event, chronos, beacons, allPilots)
    self.sunDoubleRoubines1.create('Sun Double - Roubines run #1', datetime(2000, 1, 3, 11, 30), datetime(2000, 1, 3, 12, 00), 'tt', roubines, event, chronos2, beacons, allPilots)
    pilotGroup1 = self.elitePilots + self.openPilots
    pilotGroup2 = self.womanPilots + self.juniorPilots
    self.sunDoubleRedRiver2.create('Sun Double - Red River run #2', datetime(2000, 1, 3, 12, 0), datetime(2000, 1, 3, 12, 30), 'tt', redRiver, event, chronos, beacons, pilotGroup1)
    self.sunDoubleRoubines2.create('Sun Double - Roubines run #2', datetime(2000, 1, 3, 12, 30), datetime(2000, 1, 3, 13, 00), 'tt', roubines, event, chronos2, beacons,
                                   pilotGroup2)

  def test(self):
    self.testDerby()
    self.testDouble()

  def testDouble(self):
    print("---- Double of Sunday morning ----")
    print("-- First runs")
    self.sunDoubleRedRiver1.simTimeTrial(2, 19, 30, self.fake1['id'], self.chrono['id'])
    self.sunDoubleRoubines1.simTimeTrial(2, 19, 30, self.fake2['id'], self.chrono['id'])
    print("-- Second runs")
    self.sunDoubleRedRiver2.simTimeTrial(2, 19, 30, self.fake1['id'], self.chrono['id'], 2, 2)
    self.sunDoubleRoubines2.simTimeTrial(2, 19, 30, self.fake2['id'], self.chrono['id'], 3, 3)
    print("---- Tests Results (general) ----")
    bestLapsRedRiver = getBestLaps(self.redRiver['id'])
    bestLapsRoubines = getBestLaps(self.roubines['id'])
    # 2 laps for 30+20-2-2
    checkBestLaps(bestLapsRedRiver, 80, {}, {1: 34, 2: 46})
    # 2 laps for 20+10-3-3
    checkBestLaps(bestLapsRoubines, 80, {}, {1: 56, 2: 24})

  def testDerby(self):
    print("---- Derby of Sunday morning ----")
    self.sunDerby2Elite.simRace(3, 10, self.chrono['id'])
    print("---- Tests Results of " + self.sunDerby2Elite.session['name'] + "----")
    checkLaps(getLapsForSession(self.sunDerby2Elite.session['id']), len(self.elitePilots), {1: len(self.elitePilots)}, {1: len(self.elitePilots)}, 'Elite', 180000, 190000)
    checkBestLaps(getBestLapsForSession(self.sunDerby2Elite.session['id']), len(self.elitePilots), {1: len(self.elitePilots)}, {1: len(self.elitePilots)}, 'Elite', 180000, 190000)
    checkResults(getResultsForSession(self.sunDerby2Elite.session['id']), len(self.elitePilots), {1: len(self.elitePilots)}, {1: len(self.elitePilots)}, 'Elite', 180000, 190000)

    self.sunDerby2Open.simRace(3, 20, self.chrono['id'])
    print("---- Tests Results of " + self.sunDerby2Open.session['name'] + "----")
    checkLaps(getLapsForSession(self.sunDerby2Open.session['id']), len(self.openPilots), {1: len(self.openPilots)}, {1: len(self.openPilots)}, 'Open', 180000, 200000)
    checkBestLaps(getBestLapsForSession(self.sunDerby2Open.session['id']), len(self.openPilots), {1: len(self.openPilots)}, {1: len(self.openPilots)}, 'Open', 180000, 200000)
    checkResults(getResultsForSession(self.sunDerby2Open.session['id']), len(self.openPilots), {1: len(self.openPilots)}, {1: len(self.openPilots)}, 'Open', 180000, 200000)

    self.sunDerby2Woman.simRace(3, 30, self.chrono['id'])
    checkLaps(getLapsForSession(self.sunDerby2Woman.session['id']), len(self.womanPilots), {1: len(self.womanPilots)}, {1: len(self.womanPilots)}, 'Woman', 180000, 210000)
    checkBestLaps(getBestLapsForSession(self.sunDerby2Woman.session['id']), len(self.womanPilots), {1: len(self.womanPilots)}, {1: len(self.womanPilots)}, 'Woman', 180000, 210000)
    checkResults(getResultsForSession(self.sunDerby2Woman.session['id']), len(self.womanPilots), {1: len(self.womanPilots)}, {1: len(self.womanPilots)}, 'Woman', 180000, 210000)

    self.sunDerby2Junior.simRace(3, 40, self.chrono['id'])
    checkLaps(getLapsForSession(self.sunDerby2Junior.session['id']), len(self.juniorPilots), {1: len(self.juniorPilots)}, {1: len(self.juniorPilots)}, 'Junior', 180000, 220000)
    checkBestLaps(
        getBestLapsForSession(self.sunDerby2Junior.session['id']), len(self.juniorPilots), {1: len(self.juniorPilots)}, {1: len(self.juniorPilots)}, 'Junior', 180000, 220000)
    checkResults(
        getResultsForSession(self.sunDerby2Junior.session['id']), len(self.juniorPilots), {1: len(self.juniorPilots)}, {1: len(self.juniorPilots)}, 'Junior', 180000, 220000)
