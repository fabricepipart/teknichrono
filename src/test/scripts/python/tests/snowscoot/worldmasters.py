#!python3

from api.beacons import getBeacon, addBeacon
from api.category import addCategory, addPilotToCategory
from api.chronometer import addChronometer
from api.event import addEvent, addSessionToEvent
from api.location import addLocation, addSessionToLocation
from api.pilots import addPilot, associatePilotBeacon
from api.session import addSession, addChronometerToSession, addPilotToSession
from datetime import datetime

from tests.snowscoot.thursday_evening import ThursdayEveningTest
from tests.snowscoot.friday_morning import FridayMorningTest
from tests.snowscoot.friday_afternoon import FridayAfternoonTest
from tests.snowscoot.saturday_morning import SaturdayMorningTest
from tests.snowscoot.saturday_afternoon import SaturdayAfternoonTest
from tests.snowscoot.sunday_morning import SundayMorningTest


class WorldMasters:
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
    self.organizerPilots = []
    self.fake = None
    self.chrono0 = None
    self.chrono1 = None
    self.chrono2 = None
    self.boarderCross = None
    self.mercantour = None
    self.dual = None
    self.valette = None
    self.redRiver = None
    self.roubines = None

  def createSession(self, name, start, end, sessionType, location, chronometers, pilots=[]):
    session = addSession(name, start, end, sessionType)
    addSessionToLocation(location['id'], session['id'])
    addSessionToEvent(self.event['id'], session['id'])
    for c in chronometers:
      addChronometerToSession(session['id'], c['id'])
    for pilot in pilots:
      addPilotToSession(session['id'], pilot['id'])

  def prepareFridayMorning(self):
    print("---- Create session of Friday morning ----")
    chronos = [self.chrono1, self.chrono2]
    pilots = self.allPilots
    self.createSession('Friday am tests', datetime(2019, 1, 25, 10), datetime(2019, 1, 25, 11), 'tt', self.boarderCross, chronos, pilots)
    self.createSession('Friday am Chrono', datetime(2019, 1, 25, 11), datetime(2019, 1, 25, 12), 'tt', self.boarderCross, chronos)

  def prepareFridayAfternoon(self):
    print("---- Create sessions of Friday afternoon ----")
    chronos = [self.fake, self.chrono2]
    self.createSession('Fri pm Boarder X 1/16 #1', datetime(2019, 1, 25, 14, 0), datetime(2019, 1, 25, 14, 4), 'rc', self.boarderCross, chronos)
    self.createSession('Fri pm Boarder X 1/16 #2', datetime(2019, 1, 25, 14, 4), datetime(2019, 1, 25, 14, 8), 'rc', self.boarderCross, chronos)
    self.createSession('Fri pm Boarder X 1/16 #3', datetime(2019, 1, 25, 14, 8), datetime(2019, 1, 25, 14, 12), 'rc', self.boarderCross, chronos)
    self.createSession('Fri pm Boarder X 1/16 #4', datetime(2019, 1, 25, 14, 12), datetime(2019, 1, 25, 14, 16), 'rc', self.boarderCross, chronos)
    self.createSession('Fri pm Boarder X 1/16 #5', datetime(2019, 1, 25, 14, 16), datetime(2019, 1, 25, 14, 20), 'rc', self.boarderCross, chronos)
    self.createSession('Fri pm Boarder X 1/16 #6', datetime(2019, 1, 25, 14, 20), datetime(2019, 1, 25, 14, 24), 'rc', self.boarderCross, chronos)
    self.createSession('Fri pm Boarder X 1/16 #7', datetime(2019, 1, 25, 14, 24), datetime(2019, 1, 25, 14, 28), 'rc', self.boarderCross, chronos)
    self.createSession('Fri pm Boarder X 1/16 #8', datetime(2019, 1, 25, 14, 28), datetime(2019, 1, 25, 14, 32), 'rc', self.boarderCross, chronos)
    self.createSession('Fri pm Boarder X 1/16 #9', datetime(2019, 1, 25, 14, 32), datetime(2019, 1, 25, 14, 36), 'rc', self.boarderCross, chronos)
    self.createSession('Fri pm Boarder X 1/16 #10', datetime(2019, 1, 25, 14, 36), datetime(2019, 1, 25, 14, 40), 'rc', self.boarderCross, chronos)
    self.createSession('Fri pm Boarder X 1/16 #11', datetime(2019, 1, 25, 14, 40), datetime(2019, 1, 25, 14, 44), 'rc', self.boarderCross, chronos)
    self.createSession('Fri pm Boarder X 1/16 #12', datetime(2019, 1, 25, 14, 44), datetime(2019, 1, 25, 14, 48), 'rc', self.boarderCross, chronos)
    self.createSession('Fri pm Boarder X 1/16 #13', datetime(2019, 1, 25, 14, 48), datetime(2019, 1, 25, 14, 52), 'rc', self.boarderCross, chronos)
    self.createSession('Fri pm Boarder X 1/16 #14', datetime(2019, 1, 25, 14, 52), datetime(2019, 1, 25, 14, 56), 'rc', self.boarderCross, chronos)
    self.createSession('Fri pm Boarder X 1/16 #15', datetime(2019, 1, 25, 14, 56), datetime(2019, 1, 25, 15, 0), 'rc', self.boarderCross, chronos)
    self.createSession('Fri pm Boarder X 1/16 #16', datetime(2019, 1, 25, 15, 0), datetime(2019, 1, 25, 15, 4), 'rc', self.boarderCross, chronos)
    self.createSession('Fri pm Boarder X 1/8 #1', datetime(2019, 1, 25, 15, 16), datetime(2019, 1, 25, 15, 20), 'rc', self.boarderCross, chronos)
    self.createSession('Fri pm Boarder X 1/8 #2', datetime(2019, 1, 25, 15, 20), datetime(2019, 1, 25, 15, 24), 'rc', self.boarderCross, chronos)
    self.createSession('Fri pm Boarder X 1/8 #3', datetime(2019, 1, 25, 15, 24), datetime(2019, 1, 25, 15, 28), 'rc', self.boarderCross, chronos)
    self.createSession('Fri pm Boarder X 1/8 #4', datetime(2019, 1, 25, 15, 28), datetime(2019, 1, 25, 15, 32), 'rc', self.boarderCross, chronos)
    self.createSession('Fri pm Boarder X 1/8 #5', datetime(2019, 1, 25, 15, 32), datetime(2019, 1, 25, 15, 36), 'rc', self.boarderCross, chronos)
    self.createSession('Fri pm Boarder X 1/8 #6', datetime(2019, 1, 25, 15, 36), datetime(2019, 1, 25, 15, 40), 'rc', self.boarderCross, chronos)
    self.createSession('Fri pm Boarder X 1/8 #7', datetime(2019, 1, 25, 15, 40), datetime(2019, 1, 25, 15, 44), 'rc', self.boarderCross, chronos)
    self.createSession('Fri pm Boarder X 1/8 #8', datetime(2019, 1, 25, 15, 44), datetime(2019, 1, 25, 15, 48), 'rc', self.boarderCross, chronos)
    self.createSession('Fri pm Boarder X 1/4 #1', datetime(2019, 1, 25, 16, 0), datetime(2019, 1, 25, 16, 4), 'rc', self.boarderCross, chronos)
    self.createSession('Fri pm Boarder X 1/4 #2', datetime(2019, 1, 25, 16, 4), datetime(2019, 1, 25, 16, 8), 'rc', self.boarderCross, chronos)
    self.createSession('Fri pm Boarder X 1/4 #3', datetime(2019, 1, 25, 16, 8), datetime(2019, 1, 25, 16, 12), 'rc', self.boarderCross, chronos)
    self.createSession('Fri pm Boarder X 1/4 #4', datetime(2019, 1, 25, 16, 12), datetime(2019, 1, 25, 16, 16), 'rc', self.boarderCross, chronos)
    self.createSession('Fri pm Boarder X 1/2 #1', datetime(2019, 1, 25, 16, 30), datetime(2019, 1, 25, 16, 34), 'rc', self.boarderCross, chronos)
    self.createSession('Fri pm Boarder X 1/2 #2', datetime(2019, 1, 25, 16, 34), datetime(2019, 1, 25, 16, 38), 'rc', self.boarderCross, chronos)
    self.createSession('Fri pm Boarder X Finale', datetime(2019, 1, 25, 16, 45), datetime(2019, 1, 25, 17, 0), 'rc', self.boarderCross, chronos)

  def prepareSaturdayMorning(self):
    print("---- Create session of Saturday morning ----")
    chronos = [self.fake, self.chrono2]
    self.createSession('Sat Derby 1 - Elite', datetime(2019, 1, 26, 10, 5), datetime(2019, 1, 26, 10, 10), 'rc', self.mercantour, chronos, self.elitePilots)
    self.createSession('Sat Derby 1 - Open', datetime(2019, 1, 26, 10, 15), datetime(2019, 1, 26, 10, 20), 'rc', self.mercantour, chronos, self.openPilots)
    self.createSession('Sat Derby 1 - Woman', datetime(2019, 1, 26, 10, 25), datetime(2019, 1, 26, 10, 30), 'rc', self.mercantour, chronos, self.womanPilots)
    self.createSession('Sat Derby 1 - Junior', datetime(2019, 1, 26, 10, 35), datetime(2019, 1, 26, 10, 40), 'rc', self.mercantour, chronos, self.juniorPilots)

  def prepareSaturdayAfternoon(self):
    print("---- Create session of Saturday afternoon ----")
    s1 = datetime(2019, 1, 26, 14)
    e1 = datetime(2019, 1, 26, 15)
    event = self.event
    location = self.dual
    chronos = [self.fake, self.chrono2]
    pilots = self.allPilots
    self.createSession('Saturday pm qualification', s1, e1, 'tt', location, chronos, pilots)
    for i in range(1, 33):
      name = "Sat pm dual 32th #" + str(i)
      h, m = divmod(i * 2, 60)
      eh, em = divmod(i * 2 + 2, 60)
      self.createSession(name, datetime(2019, 1, 26, 16 + h, m), datetime(2000, 1, 1, 16 + eh, em), 'rc', location, chronos)
    for i in range(1, 17):
      name = 'Sat pm dual 16th #' + str(i)
      self.createSession(name, datetime(2019, 1, 26, 17, 20 + i * 2), datetime(2000, 1, 1, 17, 22 + i * 2), 'rc', location, chronos)
    for i in range(1, 9):
      name = 'Sat pm dual 8th #' + str(i)
      self.createSession(name, datetime(2019, 1, 26, 18, i * 2), datetime(2000, 1, 1, 18, i * 2 + 2), 'rc', location, chronos)
    for i in range(1, 5):
      name = 'Sat pm dual 4th #' + str(i)
      self.createSession(name, datetime(2019, 1, 26, 18, 30 + i * 2), datetime(2000, 1, 1, 15, 32 + i * 2 + 2), 'rc', location, chronos)
    for i in range(1, 3):
      name = 'Sat pm dual 2th #' + str(i)
      self.createSession(name, datetime(2019, 1, 26, 19, i * 2), datetime(2000, 1, 1, 19, i * 2 + 2), 'rc', location, chronos)
    self.createSession('Sat pm dual finale', datetime(2019, 1, 26, 19, 30), datetime(2000, 1, 1, 19, 32), 'rc', location, chronos)

  def prepareSundayMorning(self):
    print("---- Create session of Sunday morning ----")
    valette = self.valette
    event = self.event
    chronos = [self.fake, self.chrono2]
    self.createSession('Sun Derby 2 - Elite', datetime(2019, 1, 27, 10, 5), datetime(2019, 1, 27, 10, 10), 'rc', valette, chronos, self.elitePilots)
    self.createSession('Sun Derby 2 - Open', datetime(2019, 1, 27, 10, 15), datetime(2019, 1, 27, 10, 20), 'rc', valette, chronos, self.openPilots)
    self.createSession('Sun Derby 2 - Woman', datetime(2019, 1, 27, 10, 25), datetime(2019, 1, 27, 10, 30), 'rc', valette, chronos, self.womanPilots)
    self.createSession('Sun Derby 2 - Junior', datetime(2019, 1, 27, 10, 35), datetime(2019, 1, 27, 10, 40), 'rc', valette, chronos, self.juniorPilots)
    chronos1 = [self.chrono1, self.chrono2]
    chronos0 = [self.chrono0, self.chrono2]
    redRiver = self.redRiver
    roubines = self.roubines
    allPilots = self.allPilots
    self.createSession('Sun Double - Red River', datetime(2019, 1, 27, 11, 0), datetime(2019, 1, 27, 13, 00), 'tt', redRiver, chronos1, allPilots)
    self.createSession('Sun Double - Roubines', datetime(2019, 1, 27, 11, 0), datetime(2019, 1, 27, 13, 00), 'tt', roubines, chronos0, allPilots)

  def prepareTestSessions(self):
    chronos = [self.chrono0, self.chrono1, self.chrono2]
    self.createSession('Verify', datetime(2019, 1, 25, 8, 0), datetime(2019, 1, 27, 20, 00), 'tt', self.redRiver, chronos, self.organizerPilots)

  def prepareEvent(self):
    # Add Events
    self.event = addEvent(self.name)

  def prepareCategories(self):
    # Add Categories
    # ELITE (30) OPEN HOMME (20) F2MININES (20) JUNIOR (10)
    self.eliteCategory = addCategory('Elite')
    self.openCategory = addCategory('Open')
    self.womanCategory = addCategory('Woman')
    self.juniorCategory = addCategory('Junior')
    self.organizerCategory = addCategory('Organizer')

  def addOrganizerPilot(self, firstname, lastname, beaconNumber):
    pilot = addPilot(firstname, lastname)
    self.organizerPilots.append(pilot)
    self.allPilots.append(pilot)
    addPilotToCategory(self.organizerCategory['id'], pilot['id'])
    associatePilotBeacon(pilot['id'], self.beacons[beaconNumber]['id'])

  def addElitePilot(self, firstname, lastname, beaconNumber):
    pilot = addPilot(firstname, lastname)
    self.elitePilots.append(pilot)
    self.allPilots.append(pilot)
    addPilotToCategory(self.eliteCategory['id'], pilot['id'])
    associatePilotBeacon(pilot['id'], self.beacons[beaconNumber]['id'])

  def addOpenPilot(self, firstname, lastname, beaconNumber):
    pilot = addPilot(firstname, lastname)
    self.openPilots.append(pilot)
    self.allPilots.append(pilot)
    addPilotToCategory(self.openCategory['id'], pilot['id'])
    associatePilotBeacon(pilot['id'], self.beacons[beaconNumber]['id'])

  def addWomanPilot(self, firstname, lastname, beaconNumber):
    pilot = addPilot(firstname, lastname)
    self.womanPilots.append(pilot)
    self.allPilots.append(pilot)
    addPilotToCategory(self.womanCategory['id'], pilot['id'])
    associatePilotBeacon(pilot['id'], self.beacons[beaconNumber]['id'])

  def addJuniorPilot(self, firstname, lastname, beaconNumber):
    pilot = addPilot(firstname, lastname)
    self.juniorPilots.append(pilot)
    self.allPilots.append(pilot)
    addPilotToCategory(self.juniorCategory['id'], pilot['id'])
    associatePilotBeacon(pilot['id'], self.beacons[beaconNumber]['id'])

  def prepareChronos(self):
    self.fake = addChronometer('Fake for Races')
    # Old Pi - Last
    self.chrono0 = addChronometer('Raspberry-0')
    # Black Pi - High (or first)
    self.chrono1 = addChronometer('Raspberry-1')
    # White Pi - Last
    self.chrono2 = addChronometer('Raspberry-2')

  def prepareLocations(self):
    # Add Locations
    self.boarderCross = addLocation('Isola - Boarder cross', False)
    self.mercantour = addLocation('Isola - Mercantour', False)
    self.dual = addLocation('Isola - Dual', False)
    self.valette = addLocation('Isola - Valette', False)
    self.redRiver = addLocation('Isola - Red river', False)
    self.roubines = addLocation('Isola - Roubines', False)

  def prepareBeacons(self):
    for i in range(100, 200):
      self.beacons[i] = addBeacon(i)
    for i in range(200, 300):
      self.beacons[i] = addBeacon(i)
    for i in range(300, 400):
      self.beacons[i] = addBeacon(i)

  def prepare(self):
    self.prepareEvent()
    self.prepareCategories()
    self.prepareBeacons()
    self.prepareChronos()
    self.prepareLocations()
    self.prepareTestSessions()
