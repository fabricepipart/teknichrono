#!python3

from datetime import datetime
from random import randint

from api.check import (checkLaps, checkBestLaps, checkResults)
from api.laps import (getBestLapsForSession, getLapsForSession, getResultsForSession, printLaps)
from api.ping import ping
from api.session_simulator import SessionSimulator


class SaturdayMorningTest:
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
    self.mercantour = self.championship.mercantour
    self.satDerby1Elite = SessionSimulator()
    self.satDerby1Open = SessionSimulator()
    self.satDerby1Woman = SessionSimulator()
    self.satDerby1Junior = SessionSimulator()

  def createSessions(self):
    # Add sessions
    print("---- Create session of Saturday morning ----")
    chronos = [self.fake1, self.chrono]
    self.satDerby1Elite.create('Sat Derby 1 - Elite', datetime(2000, 1, 2, 10, 5), datetime(2000, 1, 2, 10, 10), 'rc', self.mercantour, self.championship.event, chronos,
                               self.beacons, self.elitePilots)
    self.satDerby1Open.create('Sat Derby 1 - Open', datetime(2000, 1, 2, 10, 15), datetime(2000, 1, 2, 10, 20), 'rc', self.mercantour, self.championship.event, chronos,
                              self.beacons, self.openPilots)
    self.satDerby1Woman.create('Sat Derby 1 - Woman', datetime(2000, 1, 2, 10, 25), datetime(2000, 1, 2, 10, 30), 'rc', self.mercantour, self.championship.event, chronos,
                               self.beacons, self.womanPilots)
    self.satDerby1Junior.create('Sat Derby 1 - Junior', datetime(2000, 1, 2, 10, 35), datetime(2000, 1, 2, 10, 40), 'rc', self.mercantour, self.championship.event, chronos,
                                self.beacons, self.juniorPilots)

  def test(self):
    self.satDerby1Elite.simRace(2, 10, self.chrono['id'])
    print("---- Tests Results of " + self.satDerby1Elite.session['name'] + "----")
    checkLaps(getLapsForSession(self.satDerby1Elite.session['id']), len(self.elitePilots), {1: len(self.elitePilots)}, {1: len(self.elitePilots)}, 'Elite', 120000, 130000)
    checkBestLaps(getBestLapsForSession(self.satDerby1Elite.session['id']), len(self.elitePilots), {1: len(self.elitePilots)}, {1: len(self.elitePilots)}, 'Elite', 120000, 130000)
    checkResults(getResultsForSession(self.satDerby1Elite.session['id']), len(self.elitePilots), {1: len(self.elitePilots)}, {1: len(self.elitePilots)}, 'Elite', 120000, 130000)

    self.satDerby1Open.simRace(2, 20, self.chrono['id'])
    print("---- Tests Results of " + self.satDerby1Open.session['name'] + "----")
    checkLaps(getLapsForSession(self.satDerby1Open.session['id']), len(self.openPilots), {1: len(self.openPilots)}, {1: len(self.openPilots)}, 'Open', 120000, 140000)
    checkBestLaps(getBestLapsForSession(self.satDerby1Open.session['id']), len(self.openPilots), {1: len(self.openPilots)}, {1: len(self.openPilots)}, 'Open', 120000, 140000)
    checkResults(getResultsForSession(self.satDerby1Open.session['id']), len(self.openPilots), {1: len(self.openPilots)}, {1: len(self.openPilots)}, 'Open', 120000, 140000)

    self.satDerby1Woman.simRace(2, 30, self.chrono['id'])
    checkLaps(getLapsForSession(self.satDerby1Woman.session['id']), len(self.womanPilots), {1: len(self.womanPilots)}, {1: len(self.womanPilots)}, 'Woman', 120000, 150000)
    checkBestLaps(getBestLapsForSession(self.satDerby1Woman.session['id']), len(self.womanPilots), {1: len(self.womanPilots)}, {1: len(self.womanPilots)}, 'Woman', 120000, 150000)
    checkResults(getResultsForSession(self.satDerby1Woman.session['id']), len(self.womanPilots), {1: len(self.womanPilots)}, {1: len(self.womanPilots)}, 'Woman', 120000, 150000)

    self.satDerby1Junior.simRace(2, 40, self.chrono['id'])
    checkLaps(getLapsForSession(self.satDerby1Junior.session['id']), len(self.juniorPilots), {1: len(self.juniorPilots)}, {1: len(self.juniorPilots)}, 'Junior', 120000, 160000)
    checkBestLaps(
        getBestLapsForSession(self.satDerby1Junior.session['id']), len(self.juniorPilots), {1: len(self.juniorPilots)}, {1: len(self.juniorPilots)}, 'Junior', 120000, 160000)
    checkResults(
        getResultsForSession(self.satDerby1Junior.session['id']), len(self.juniorPilots), {1: len(self.juniorPilots)}, {1: len(self.juniorPilots)}, 'Junior', 120000, 160000)
