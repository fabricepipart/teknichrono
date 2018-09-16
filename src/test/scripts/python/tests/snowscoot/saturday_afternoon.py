from datetime import datetime
from api.session_simulator import SessionSimulator
from api.check import checkBestLaps
from api.laps import getBestLapsForSession


class SaturdayAfternoonTest:
  def __init__(self, championship):
    self.championship = championship
    self.qualification = SessionSimulator()
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

  def test(self):
    print("---- Test #1 ----")
    self.qualification.simTimeTrial(2, 19, 20, self.fake1['id'], self.chrono['id'])
    print("---- Test #2 ----")
    self.qualification.simTimeTrial(2, 19, 20, self.fake1['id'], self.chrono['id'], startShift=30)

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
