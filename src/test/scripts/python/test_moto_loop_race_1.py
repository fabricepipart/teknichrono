#!python3

import requests
import json
import sys
from random import randint

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
# Add Pilots
valentino = addPilot('Valentino', 'Race1')
marc = addPilot('Marc', 'Race1')
dani = addPilot('Dani', 'Race1')
jorge = addPilot('Jorge', 'Race1')
johann = addPilot('Johann', 'Race1')
cal = addPilot('Cal', 'Race1')

# ----------------------------------------------------------------------
# Add Categories
motogp = addCategory('MotoGP')

addPilotToCategory(motogp['id'], valentino['id'])
addPilotToCategory(motogp['id'], marc['id'])
addPilotToCategory(motogp['id'], dani['id'])
addPilotToCategory(motogp['id'], jorge['id'])
addPilotToCategory(motogp['id'], johann['id'])
addPilotToCategory(motogp['id'], cal['id'])

# ----------------------------------------------------------------------
# Add Chronometers
chrono = addChronometer('Raspberry-loop-rc-0')

# ----------------------------------------------------------------------
# Add Locations
aragon = addLocation('Aragon')

print("-------------------------------")
print("Race with Just one chrono")
print("-------------------------------")

eventName = 'Race in Aragon'
raceEvent = addEvent('Event in Aragon')

race = addSession('Race in Aragon session', datetime(2018, 1, 2, 14, 0, 0, 0), datetime(2018, 1, 2, 15, 0, 0, 0), 'rc')
addSessionToLocation(aragon['id'], race['id'])
addChronometerToSession(race['id'], chrono['id'])

associatePilotBeacon(valentino['id'], getBeacon(46)['id'])
associatePilotBeacon(marc['id'], getBeacon(93)['id'])
associatePilotBeacon(dani['id'], getBeacon(26)['id'])
associatePilotBeacon(jorge['id'], getBeacon(99)['id'])
associatePilotBeacon(johann['id'], getBeacon(5)['id'])
associatePilotBeacon(cal['id'], getBeacon(35)['id'])

valeBeaconId = getBeacon(46)['id']
jorgeBeaconId = getBeacon(99)['id']
marcBeaconId = getBeacon(93)['id']
daniBeaconId = getBeacon(26)['id']
johannBeaconId = getBeacon(5)['id']
calBeaconId = getBeacon(35)['id']

addPilotToSession(race['id'], valentino['id'])
addPilotToSession(race['id'], marc['id'])
addPilotToSession(race['id'], dani['id'])
addPilotToSession(race['id'], jorge['id'])
addPilotToSession(race['id'], johann['id'])
addPilotToSession(race['id'], cal['id'])

# Start
startSession(race['id'], datetime(2018, 1, 2, 14, 1, 0))

# TODO This should work
# A few pilots started behind the chrono
#ping(datetime(2018, 1, 2, 14, 1, 1, 0), jorgeBeaconId, -99, chrono['id'])
#ping(datetime(2018, 1, 2, 14, 1, 2, 0), daniBeaconId, -26, chrono['id'])
#ping(datetime(2018, 1, 2, 14, 1, 3, 0), calBeaconId, -5, chrono['id'])

for i in range(1, 21):
  if (i < 10):
    ping(datetime(2018, 1, 2, 14, 1 + 2 * i, randint(0, 10), randint(0, 100000)), jorgeBeaconId, -99, chrono['id'])
  ping(datetime(2018, 1, 2, 14, 1 + 2 * i, randint(0, 10), randint(0, 100000)), marcBeaconId, -93, chrono['id'])
  ping(datetime(2018, 1, 2, 14, 1 + 2 * i, randint(0, 10), randint(0, 100000)), calBeaconId, -35, chrono['id'])
  ping(datetime(2018, 1, 2, 14, 1 + 2 * i, randint(0, 10), randint(0, 100000)), johannBeaconId, -5, chrono['id'])
  ping(datetime(2018, 1, 2, 14, 1 + 2 * i, randint(0, 10), randint(0, 100000)), valeBeaconId, -46, chrono['id'])
  ping(datetime(2018, 1, 2, 14, 1 + 2 * i, randint(0, 10), randint(0, 100000)), daniBeaconId, -26, chrono['id'])
# TODO Display for each lap the intermediate display

endSession(race['id'], datetime(2018, 1, 2, 15, 2, 1))

# Laps per pilot
print("---- Laps Valentino ----")
laps = getLapsOfPilot(valentino['id'], race['id'])
printLaps(laps, True)
#print(str(len(laps)))
#assert len(laps) == 12

print("---- Laps Marc ----")
laps = getLapsOfPilot(marc['id'], race['id'])
printLaps(laps, True)

print("---- Laps Dani ----")
laps = getLapsOfPilot(dani['id'], race['id'])
printLaps(laps, True)

print("---- Laps Jorge ----")
laps = getLapsOfPilot(jorge['id'], race['id'])
printLaps(laps, True)

print("---- Laps Johann ----")
printLaps(getLapsOfPilot(johann['id'], race['id']), True)
printLaps(getBestLapsOfPilot(johann['id'], race['id']), True)

# Laps of location
laps = getLapsOfPilot(pilotId=johann['id'], locationId=aragon['id'])
printLaps(laps, True)

# Laps of Event
laps = getLapsOfPilot(pilotId=johann['id'], eventId=raceEvent['id'])
printLaps(laps, True)

print("---- Laps Cal ----")
laps = getLapsOfPilot(cal['id'], race['id'])
printLaps(laps, True)
printLaps(getRaceLapsOfPilot(pilotId=cal['id'], sessionId=race['id']), True)

# Laps of location
print("---- Laps location ----")
laps = getLaps(locationId=aragon['id'])
printLaps(laps, True)

# Laps of Event
print("---- Laps Event ----")
laps = getLaps(eventId=raceEvent['id'])
printLaps(laps, True)

# Session summary
print("---- Session summary ----")
printLaps(getLapsForSession(race['id']), True)
printLaps(getBestLapsForSession(race['id']), True)
printLaps(getResultsForSession(race['id']), True)

#TODO Best laps shown, thats not what we want : order by time of last lap end