#!python3

import requests
import json
import sys

from base import *
from pilots import *
from beacons import *
from chronometer import *
from event import *
from category import *
from session import *
from location import *
from ping import *
from laps import *

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

associatePilotBeacon(valentino['id'], getBeacon(46)['id'])
associatePilotBeacon(marc['id'], getBeacon(93)['id'])
associatePilotBeacon(dani['id'], getBeacon(26)['id'])
associatePilotBeacon(jorge['id'], getBeacon(99)['id'])
associatePilotBeacon(johann['id'], getBeacon(5)['id'])
associatePilotBeacon(cal['id'], getBeacon(35)['id'])

valentinoBeaconId = getBeacon(46)['id']
jorgeBeaconId = getBeacon(99)['id']
marcBeaconId = getBeacon(93)['id']
daniBeaconId = getBeacon(26)['id']
johannBeaconId = getBeacon(5)['id']
calBeaconId = getBeacon(35)['id']

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
laps = getLapsOfPilot(valentino['id'], session['id'])
printLaps(laps, True)
#print(str(len(laps)))
#assert len(laps) == 12

print("---- Laps Marc ----")
morningLaps = getLapsOfPilot(marc['id'], morningSession['id'])
printLaps(morningLaps, True)
laps = getLapsOfPilot(marc['id'], session['id'])
printLaps(laps, True)

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
laps = getLapsOfPilot(pilotId=johann['id'], locationId=ledenon['id'])
printLaps(laps, True)

# Laps of Event
laps = getLapsOfPilot(pilotId=johann['id'], eventId=timeTrial['id'])
printLaps(laps, True)

print("---- Laps Cal ----")
morningLaps = getLapsOfPilot(cal['id'], morningSession['id'])
printLaps(morningLaps, True)
laps = getLapsOfPilot(cal['id'], session['id'])
printLaps(laps, True)

# Laps of location
laps = getLaps(locationId=ledenon['id'])
printLaps(laps, True)

# Laps of Event
laps = getLaps(eventId=timeTrial['id'])
printLaps(laps, True)

# Session summary
laps = getLapsForSession(session['id'])
printLaps(laps, True)

# -------------------------------
# Race with just one chrono
# -------------------------------

# --------- TODO -------------
# -------------------------------
# Race with just one chrono
# -------------------------------

# Laps per pilot
# All Laps
# Session summary

# --------- TODO -------------
# -------------------------------
# Race with several chronos
# -------------------------------

# Laps per pilot
# All Laps
# Session summary