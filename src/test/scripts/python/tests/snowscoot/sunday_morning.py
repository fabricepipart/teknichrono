#!python3

from datetime import datetime
from random import randint

from api.check import (checkLaps, checkBestLaps, checkResults)
from api.laps import (getBestLapsForSession, getLapsForSession, getResultsForSession, printLaps)
from api.ping import ping
from api.session_simulator import SessionSimulator


class SundayMorningTest:
  def __init__(self, championship):
    self.championship = championship
    self.fake1 = self.championship.fake1
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

  def createSessions(self):
    # Add sessions
    print("---- Create session of Sunday morning ----")
    chronos = [self.fake1, self.chrono]
    self.sunDerby2Elite.create('Sun Derby 2 - Elite', datetime(2000, 1, 3, 10, 5), datetime(2000, 1, 3, 10, 10), 'rc', self.valette, self.championship.event, chronos, self.beacons,
                               self.elitePilots)
    self.sunDerby2Open.create('Sun Derby 2 - Open', datetime(2000, 1, 3, 10, 15), datetime(2000, 1, 3, 10, 20), 'rc', self.valette, self.championship.event, chronos, self.beacons,
                              self.openPilots)
    self.sunDerby2Woman.create('Sun Derby 2 - Woman', datetime(2000, 1, 3, 10, 25), datetime(2000, 1, 3, 10, 30), 'rc', self.valette, self.championship.event, chronos,
                               self.beacons, self.womanPilots)
    self.sunDerby2Junior.create('Sun Derby 2 - Junior', datetime(2000, 1, 3, 10, 35), datetime(2000, 1, 3, 10, 40), 'rc', self.valette, self.championship.event, chronos,
                                self.beacons, self.juniorPilots)

  def test(self):
    self.testDerby()
    self.testDouble()

  def testDouble(self):
    print("---- Double of Sunday morning ----")

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
