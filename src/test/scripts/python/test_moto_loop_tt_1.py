#!python3

import requests
import json
import sys

from api.base import *
from api.pilots import *
from api.beacons import *
from api.chronometer import *
from api.event import *
from api.category import *
from api.session import *
from api.location import *
from api.ping import *
from api.laps import *

# ----------------------------------------------------------------------
# Add Beacons
addBeacon(2046)
addBeacon(2093)
addBeacon(2026)
addBeacon(2099)
addBeacon(2005)
addBeacon(2035)

# ----------------------------------------------------------------------
# Add Pilots
valentino = addPilot('Valentino', 'Rossi')
marc = addPilot('Marc', 'Marquez')
dani = addPilot('Dani', 'Pedrosa')
jorge = addPilot('Jorge', 'Lorenzo')
johann = addPilot('Johann', 'Zarco')
cal = addPilot('Cal', 'Crutchlow')

# ----------------------------------------------------------------------
# Add Categories
topPilot = addCategory('Top Pilot')

addPilotToCategory(topPilot['id'], valentino['id'])
addPilotToCategory(topPilot['id'], marc['id'])
addPilotToCategory(topPilot['id'], dani['id'])
addPilotToCategory(topPilot['id'], jorge['id'])
addPilotToCategory(topPilot['id'], johann['id'])
addPilotToCategory(topPilot['id'], cal['id'])

# ----------------------------------------------------------------------
# Add Chronometers
addChronometer('Raspberry-loop-tt-0')
addChronometer('Raspberry-loop-tt-1')

# ----------------------------------------------------------------------
# Add Locations
ledenon = addLocation('Ledenon')

# --------- TODO -------------
print("-------------------------------")
print("Time trial with Just one chrono")
print("-------------------------------")

# TODO Session of same event

eventName = 'Time trial with Just one chrono'
timeTrial = addEvent(eventName)

morningSession = addSession(eventName + ' morning session', datetime(2018, 1, 1, 8, 0, 0, 0),
                            datetime(2018, 1, 1, 12, 0, 0, 0), 'tt')
addSessionToLocation(ledenon['id'], morningSession['id'])
addSessionToEvent(timeTrial['id'], morningSession['id'])
chrono1 = getChronometerByName('Raspberry-loop-tt-1')['id']
addChronometerToSession(morningSession['id'], chrono1)

session = addSession(eventName + ' afternoon session', datetime(2018, 1, 1, 14, 0, 0, 0),
                     datetime(2018, 1, 1, 18, 0, 0, 0), 'tt')
addSessionToLocation(ledenon['id'], session['id'])
addSessionToEvent(timeTrial['id'], session['id'])
chrono0 = getChronometerByName('Raspberry-loop-tt-0')['id']
addChronometerToSession(session['id'], chrono0)

associatePilotBeacon(valentino['id'], getBeacon(2046)['id'])
associatePilotBeacon(marc['id'], getBeacon(2093)['id'])
associatePilotBeacon(dani['id'], getBeacon(2026)['id'])
associatePilotBeacon(jorge['id'], getBeacon(2099)['id'])
associatePilotBeacon(johann['id'], getBeacon(2005)['id'])
associatePilotBeacon(cal['id'], getBeacon(2035)['id'])

valentinoBeaconId = getBeacon(2046)['id']
jorgeBeaconId = getBeacon(2099)['id']
marcBeaconId = getBeacon(2093)['id']
daniBeaconId = getBeacon(2026)['id']
johannBeaconId = getBeacon(2005)['id']
calBeaconId = getBeacon(2035)['id']

for i in range(0, 6):
  ping(datetime(2018, 1, 1, 10, i, random.randint(10, 30), random.randint(0, 100000)), jorgeBeaconId, -99, chrono1)
  ping(datetime(2018, 1, 1, 10, 40 + i, random.randint(5, 30), random.randint(0, 100000)), marcBeaconId, -93, chrono1)
  ping(datetime(2018, 1, 1, 10, 20 + i, random.randint(10, 30), random.randint(0, 100000)), calBeaconId, -35, chrono1)
  ping(datetime(2018, 1, 1, 10, 40 + i, random.randint(12, 32), random.randint(0, 100000)), johannBeaconId, -5, chrono1)

for i in range(0, 5):
  ping(datetime(2018, 1, 1, 14, i, random.randint(10, 30), random.randint(0, 100000)), jorgeBeaconId, -99, chrono0)
  ping(datetime(2018, 1, 1, 15, 10 + i, random.randint(10, 30), random.randint(0, 100000)), jorgeBeaconId, -99, chrono0)
  ping(datetime(2018, 1, 1, 16, 20 + i, random.randint(10, 30), random.randint(0, 100000)), jorgeBeaconId, -99, chrono0)
  ping(datetime(2018, 1, 1, 17, 30 + i, random.randint(10, 30), random.randint(0, 100000)), jorgeBeaconId, -99, chrono0)
  ping(
      datetime(2018, 1, 1, 14, 10 + i, random.randint(10, 30), random.randint(0, 100000)), valentinoBeaconId, -46,
      chrono0)
  ping(
      datetime(2018, 1, 1, 15, 20 + i, random.randint(10, 30), random.randint(0, 100000)), valentinoBeaconId, -46,
      chrono0)
  ping(
      datetime(2018, 1, 1, 16, 30 + i, random.randint(10, 30), random.randint(0, 100000)), valentinoBeaconId, -46,
      chrono0)
  ping(datetime(2018, 1, 1, 14, 20 + i, random.randint(5, 30), random.randint(0, 100000)), marcBeaconId, -93, chrono0)
  ping(datetime(2018, 1, 1, 15, 30 + i, random.randint(5, 30), random.randint(0, 100000)), marcBeaconId, -93, chrono0)
  ping(datetime(2018, 1, 1, 17, 40 + i, random.randint(5, 30), random.randint(0, 100000)), marcBeaconId, -93, chrono0)
  ping(datetime(2018, 1, 1, 15, 10 + i, random.randint(5, 30), random.randint(0, 100000)), daniBeaconId, -26, chrono0)
  ping(datetime(2018, 1, 1, 16, 20 + i, random.randint(5, 30), random.randint(0, 100000)), daniBeaconId, -26, chrono0)
  ping(datetime(2018, 1, 1, 17, 30 + i, random.randint(5, 30), random.randint(0, 100000)), daniBeaconId, -26, chrono0)
  ping(datetime(2018, 1, 1, 14, i, random.randint(10, 30), random.randint(0, 100000)), johannBeaconId, -5, chrono0)
  ping(datetime(2018, 1, 1, 15, 10 + i, random.randint(10, 30), random.randint(0, 100000)), johannBeaconId, -5, chrono0)
  ping(datetime(2018, 1, 1, 16, 40 + i, random.randint(10, 30), random.randint(0, 100000)), johannBeaconId, -5, chrono0)
  ping(datetime(2018, 1, 1, 17, 30 + i, random.randint(10, 30), random.randint(0, 100000)), johannBeaconId, -5, chrono0)
  ping(datetime(2018, 1, 1, 14, i, random.randint(10, 30), random.randint(0, 100000)), calBeaconId, -35, chrono0)
  ping(datetime(2018, 1, 1, 15, 10 + i, random.randint(10, 30), random.randint(0, 100000)), calBeaconId, -35, chrono0)
  ping(datetime(2018, 1, 1, 16, 20 + i, random.randint(10, 30), random.randint(0, 100000)), calBeaconId, -35, chrono0)
  ping(datetime(2018, 1, 1, 17, 30 + i, random.randint(10, 30), random.randint(0, 100000)), calBeaconId, -35, chrono0)

for i in range(0, 10):
  ping(datetime(2018, 1, 1, 15, 40 + i, random.randint(12, 32), random.randint(0, 100000)), johannBeaconId, -5, chrono0)

# Laps per pilot
print("---- Laps Valentino ----")

printLaps(getLapsOfPilot(valentino['id'], session['id']), True)
printLaps(getBestLapsOfPilot(valentino['id'], session['id']), True)

#print(str(len(laps)))
#assert len(laps) == 12

print("---- Laps Marc ----")
printLaps(getLapsOfPilot(marc['id'], morningSession['id']), True)
printLaps(getLapsOfPilot(marc['id'], session['id']), True)
printLaps(getBestLapsOfPilot(marc['id'], morningSession['id']), True)
printLaps(getBestLapsOfPilot(marc['id'], session['id']), True)

print("---- Laps Dani ----")
laps = getLapsOfPilot(dani['id'], session['id'])
printLaps(laps, True)

print("---- Laps Jorge ----")
morningLaps = getLapsOfPilot(jorge['id'], morningSession['id'])
printLaps(morningLaps, True)
laps = getLapsOfPilot(jorge['id'], session['id'])
printLaps(laps, True)

print("---- Laps Johann ----")
morningLaps = getLapsOfPilot(johann['id'], morningSession['id'])
printLaps(morningLaps, True)
laps = getLapsOfPilot(johann['id'], session['id'])
printLaps(laps, True)

# Laps of location
printLaps(getLapsOfPilot(pilotId=johann['id'], locationId=ledenon['id']), True)
printLaps(getBestLapsOfPilot(pilotId=johann['id'], locationId=ledenon['id']), True)

# Laps of Event
printLaps(getLapsOfPilot(pilotId=johann['id'], eventId=timeTrial['id']), True)
printLaps(getBestLapsOfPilot(pilotId=johann['id'], eventId=timeTrial['id']), True)

print("---- Laps Cal ----")
morningLaps = getLapsOfPilot(cal['id'], morningSession['id'])
printLaps(morningLaps, True)
laps = getLapsOfPilot(cal['id'], session['id'])
printLaps(laps, True)

print("---- Laps of location ----")
# Laps of location
printLaps(getLaps(locationId=ledenon['id']), True)
printLaps(getBestLaps(locationId=ledenon['id']), True)

print("---- Laps of Event ----")
# Laps of Event
printLaps(getLaps(eventId=timeTrial['id']), True)
printLaps(getBestLaps(eventId=timeTrial['id']), True)

print("---- Laps summary ----")
# Session summary
printLaps(getLapsForSession(session['id']), True)
printLaps(getBestLapsForSession(session['id']), True)

print("--------------------------------------------------------------")
print("|     End of Test for Motorbikes on racetrack (1 chrono)     |")
print("--------------------------------------------------------------")
