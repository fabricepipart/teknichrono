from datetime import datetime
from random import randint

from api.location import addSessionToLocation
from api.event import addSessionToEvent
from api.session import addSession, addChronometerToSession, addPilotToSession, startSession, endSession
from api.ping import ping


class SessionSimulator:
  def __init__(self):
    self.name = None
    self.start = None
    self.end = None
    self.session = None
    self.event = None
    self.location = None
    self.chronometers = []
    self.pilots = []

  def create(self, name, start, end, sessionType, location, event, chronometers, pilots=[]):
    self.name = name
    self.start = start
    self.end = end
    self.session = addSession(name, start, end, sessionType)
    self.event = event
    self.location = location
    self.chronometers = chronometers
    addSessionToLocation(location['id'], self.session['id'])
    addSessionToEvent(event['id'], self.session['id'])
    for c in chronometers:
      addChronometerToSession(self.session['id'], c['id'])
    for pilot in pilots:
      self.addPilot(pilot)
      pilots

  def addPilot(self, pilot):
    addPilotToSession(self.session['id'], pilot['id'])
    self.pilots.append(pilot)

  def getBeaconsIdsOfSession(self, beacons):
    beaconsIdsOfSession = []
    for pilot in self.pilots:
      beaconsIdsOfSession.append(beacons[pilot['beaconNumber']]['id'])
    return beaconsIdsOfSession

  def simRace(self, beaconsIdsOfSession, avgDurationMin, delta, chronoId, doNotFinish=[]):
    startHour = self.start.hour
    startMinute = self.start.minute
    # Starts all together
    h, m = divmod((startHour * 60) + startMinute, 60)
    startSession(self.session['id'], datetime(self.start.year, self.start.month, self.start.day, h, m, 5))
    # Ends
    eh, em = divmod((h * 60) + m + avgDurationMin, 60)
    for beaconId in beaconsIdsOfSession:
      es = randint(0, delta)
      if beaconId not in doNotFinish:
        ping(datetime(self.start.year, self.start.month, self.start.day, eh, em, es, randint(0, 500000)), beaconId, -99, chronoId)
    endSession(self.session['id'], datetime(self.start.year, self.start.month, self.start.day, eh, em, 59))