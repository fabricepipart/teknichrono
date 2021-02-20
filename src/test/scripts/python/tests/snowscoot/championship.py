#!python3

from api.beacons import getBeacon
from api.category import addCategory, addPilotToCategory
from api.chronometer import addChronometer
from api.event import addEvent
from api.location import addLocation
from api.pilots import addPilot, associatePilotBeacon
from tests.snowscoot.thursday_evening import ThursdayEveningTest
from tests.snowscoot.friday_morning import FridayMorningTest
from tests.snowscoot.friday_afternoon import FridayAfternoonTest
from tests.snowscoot.saturday_morning import SaturdayMorningTest
from tests.snowscoot.saturday_afternoon import SaturdayAfternoonTest
from tests.snowscoot.sunday_morning import SundayMorningTest


class ChampionshipTest:
  def __init__(self, name):
    self.name = name
    self.event = None
    self.beacons = {}
    self.eliteCategory = None
    self.openCategory = None
    self.womanCategory = None
    self.juniorCategory = None
    self.allPilots = []
    self.elitePilots = []
    self.openPilots = []
    self.womanPilots = []
    self.juniorPilots = []
    self.chrono = None
    self.fake1 = None
    self.fake2 = None
    self.boarderCross = None
    self.mercantour = None
    self.dual = None
    self.valette = None
    self.redRiver = None
    self.roubines = None
    self.startBeaconNumber = 10

  def testThursdayEvening(self):
    thursdayEveningTest = ThursdayEveningTest(self)
    thursdayEveningTest.test()

  def testFridayMorning(self):
    fridayMorningTest = FridayMorningTest(self)
    fridayMorningTest.createSessions()
    fridayMorningTest.test()

  def testFridayAfternoon(self):
    fridayAfternoonTest = FridayAfternoonTest(self)
    fridayAfternoonTest.createSessions()
    fridayAfternoonTest.test()

  def testSaturdayMorning(self):
    saturdayMorningTest = SaturdayMorningTest(self)
    saturdayMorningTest.createSessions()
    saturdayMorningTest.test()

  def testSaturdayAfternoon(self):
    saturdayAfternoonTest = SaturdayAfternoonTest(self)
    saturdayAfternoonTest.createSessions()
    saturdayAfternoonTest.test()

  def testSundayMorning(self):
    sundayMorningTest = SundayMorningTest(self)
    sundayMorningTest.createSessions()
    sundayMorningTest.test()

  def createEvent(self):
    # Add Events
    self.event = addEvent(self.name)

  def createCategories(self):
    # Add Categories
    # ELITE (30) OPEN HOMME (20) F2MININES (20) JUNIOR (10)
    self.eliteCategory = addCategory('Elite ' + self.name)
    self.openCategory = addCategory('Open ' + self.name)
    self.womanCategory = addCategory('Woman ' + self.name)
    self.juniorCategory = addCategory('Junior ' + self.name)

  def createPilots(self):
    # Add Pilots
    for i in range(10, 40):
      pilot = addPilot('Rider ' + str(i), 'Elite ' + self.name)
      self.elitePilots.append(pilot)
      addPilotToCategory(self.eliteCategory['id'], pilot['id'])
    for i in range(40, 60):
      pilot = addPilot('Rider ' + str(i), 'Open ' + self.name)
      self.openPilots.append(pilot)
      addPilotToCategory(self.openCategory['id'], pilot['id'])
    for i in range(60, 80):
      pilot = addPilot('Rider ' + str(i), 'Woman ' + self.name)
      self.womanPilots.append(pilot)
      addPilotToCategory(self.womanCategory['id'], pilot['id'])
    for i in range(80, 90):
      pilot = addPilot('Rider ' + str(i), 'Junior ' + self.name)
      self.juniorPilots.append(pilot)
      addPilotToCategory(self.juniorCategory['id'], pilot['id'])
    self.allPilots = self.elitePilots + self.openPilots + self.womanPilots + self.juniorPilots

  def createChronos(self):
    # Add Chronometers
    self.chrono = addChronometer('Raspberry ' + self.name)
    self.fake1 = addChronometer('Fake1 ' + self.name)
    self.fake2 = addChronometer('Fake2 ' + self.name)

  def createLocations(self):
    # Add Locations
    self.boarderCross = addLocation('Isola - Boarder cross ' + self.name, False)
    self.mercantour = addLocation('Isola - Mercantour ' + self.name, False)
    self.dual = addLocation('Isola - Dual ' + self.name, False)
    self.valette = addLocation('Isola - Valette ' + self.name, False)
    self.redRiver = addLocation('Isola - Red river ' + self.name, False)
    self.roubines = addLocation('Isola - Roubines ' + self.name, False)

  def assignBeasons(self):
    # jeudi soir	accueil concurrents et distribution transpondeurs
    beaconNumber = self.startBeaconNumber
    for pilot in self.allPilots:
      self.beacons[beaconNumber] = getBeacon(beaconNumber)
      associatePilotBeacon(pilot['id'], self.beacons[beaconNumber]['id'])
      pilot['currentBeacon'] = self.beacons[beaconNumber]
      beaconNumber += 1

  def prepare(self):
    self.createEvent()
    self.createCategories()
    self.createPilots()
    self.createChronos()
    self.createLocations()
