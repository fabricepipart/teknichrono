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
    self.beacons = []

  def create(self, name, start, end, sessionType, location, event, chronometers, beacons, pilots=[]):
    self.name = name
    self.start = start
    self.end = end
    self.session = addSession(name, start, end, sessionType)
    self.event = event
    self.location = location
    self.chronometers = chronometers
    self.beacons = beacons
    addSessionToLocation(location['id'], self.session['id'])
    addSessionToEvent(event['id'], self.session['id'])
    for c in chronometers:
      addChronometerToSession(self.session['id'], c['id'])
    for pilot in pilots:
      self.addPilot(pilot)

  def addPilot(self, pilot):
    addPilotToSession(self.session['id'], pilot['id'])
    self.pilots.append(pilot)

  def getBeaconsIdsOfSession(self):
    beaconsIdsOfSession = []
    for pilot in self.pilots:
      beaconNumber = None
      # Because the structure might come from different sources. Still not very smart
      #TODO Improve and insure always same structure is passed
      if 'currentBeacon' in pilot:
        beaconNumber = pilot['currentBeacon']['number']
      else:
        beaconNumber = pilot['beaconNumber']
      beaconsIdsOfSession.append(self.beacons[beaconNumber]['id'])
    return beaconsIdsOfSession

  def simRace(self, avgDurationMin, delta, chronoId, doNotFinish=0):
    numberThatDidNotFinish = 0
    beaconsIdsOfSession = self.getBeaconsIdsOfSession()
    startHour = self.start.hour
    startMinute = self.start.minute
    # Starts all together
    startSession(self.session['id'], datetime(self.start.year, self.start.month, self.start.day, startHour, startMinute, 5))
    # Ends
    eh, em = divmod((startHour * 60) + startMinute + avgDurationMin, 60)
    for beaconId in beaconsIdsOfSession:
      es = randint(0, delta)
      if numberThatDidNotFinish < doNotFinish:
        numberThatDidNotFinish += 1
      else:
        ping(datetime(self.start.year, self.start.month, self.start.day, eh, em, es, randint(0, 500000)), beaconId, -99, chronoId)
    endSession(self.session['id'], datetime(self.start.year, self.start.month, self.start.day, eh, em, 59))

  def simTimeTrial(self, avgDurationMin, delta, startPeriod, chronoStartId, chronoEndId, doNotStart=0, doNotFinish=0, startShift=0):
    numberThatDidNotStart = 0
    numberThatDidNotFinish = 0
    beaconsIdsOfSession = self.getBeaconsIdsOfSession()
    startHour = self.start.hour
    startMinute = self.start.minute + startShift
    # Starts
    startSession(self.session['id'], datetime(self.start.year, self.start.month, self.start.day, startHour, startMinute, 5))
    i = 0
    for beaconId in beaconsIdsOfSession:
      sm, ss = divmod(i * startPeriod, 60)
      sh, sm = divmod((startHour * 60) + startMinute + sm, 60)
      if numberThatDidNotStart < doNotStart:
        numberThatDidNotStart += 1
      else:
        ping(datetime(self.start.year, self.start.month, self.start.day, sh, sm, ss, randint(0, 500000)), beaconId, -99, chronoStartId)
      i += 1
    # Ends
    numberThatDidNotStart = 0
    i = 0
    for beaconId in beaconsIdsOfSession:
      em, es = divmod(i * startPeriod + randint(0, delta), 60)
      eh, em = divmod((startHour * 60) + startMinute + em + avgDurationMin, 60)
      if numberThatDidNotStart < doNotStart:
        numberThatDidNotStart += 1
      elif numberThatDidNotFinish < doNotFinish:
        numberThatDidNotFinish += 1
      else:
        ping(datetime(self.start.year, self.start.month, self.start.day, eh, em, es, randint(0, 500000)), beaconId, -99, chronoEndId)
      i += 1
    endSession(self.session['id'], datetime(self.start.year, self.start.month, self.start.day, eh, em, 59))