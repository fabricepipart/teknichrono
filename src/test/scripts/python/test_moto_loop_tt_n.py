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
addBeacon(3012)
addBeacon(3002)
addBeacon(3003)
addBeacon(3004)
addBeacon(3008)


# ----------------------------------------------------------------------
# Add Pilots

jeromeTtId = addPilot('Jerome', 'Rousseau')['id']
fabriceTtId = addPilot('Fabrice', 'Pipart')['id']
jeremyTtId = addPilot('Jeremy', 'Ponchel')['id']
valentinoTtId = addPilot('Valentino', 'TRD')['id']
jorgeTtId = addPilot('Jorge', 'TRD')['id']

# ----------------------------------------------------------------------
# Add Categories
cat1 = addCategory('Cat1')
cat2 = addCategory('Cat2')
cat3 = addCategory('Cat3')

addPilotToCategory(cat3['id'], fabriceTtId)
addPilotToCategory(cat2['id'], jeremyTtId)
addPilotToCategory(cat1['id'], valentinoTtId)
addPilotToCategory(cat1['id'], jorgeTtId)

# ----------------------------------------------------------------------
# Play with associations

associatePilotBeacon(jeromeTtId, getBeacon(3012)['id'])

associatePilotBeacon(fabriceTtId, getBeacon(3002)['id'])
associatePilotBeacon(jeremyTtId, getBeacon(3012)['id'])

associatePilotBeacon(valentinoTtId, getBeacon(3004)['id'])
associatePilotBeacon(jorgeTtId, getBeacon(3008)['id'])

deleteBeacon(getBeacon(3002)['id'])
associatePilotBeacon(fabriceTtId, getBeacon(3003)['id'])

# ----------------------------------------------------------------------
# Add Chronometers
for i in range(0, 5):
  addChronometer('Raspberry-' + str(i))

# ----------------------------------------------------------------------
# Add Locations
leLuc = addLocation('Le Luc')

# ----------------------------------------------------------------------
# Add Events
trd1 = addEvent('TRD Le Luc 2016-08-22')

# ----------------------------------------------------------------------
# Add Sessions
sessionTtId = addSession('Morning TRD Le Luc 2016-08-22', datetime(2016, 8, 22), datetime(2016, 8, 22), 'tt')['id']
addSessionToLocation(leLuc['id'], sessionTtId)
addSessionToEvent(trd1['id'], sessionTtId)

addSession('Morning TRD Ledenon 2016-09-12+13', datetime(2016, 9, 12), datetime(2016, 9, 13), 'tt')
addSession('Morning TRD Aragon 2016-10-22+23', datetime(2016, 10, 22), datetime(2016, 10, 23), 'tt')
addSession('Morning TRD Le Luc 2016-11-01', datetime(2016, 11, 1), datetime(2016, 11, 1), 'tt')
addSession('Session of Rally 1', datetime(2017, 3, 1), datetime(2017, 3, 1), 'tt')

# ----------------------------------------------------------------------
# Associate chronometers to session in right order
addChronometerToSession(sessionTtId, getChronometerByName('Raspberry-0')['id'])
addChronometerToSession(sessionTtId, getChronometerByName('Raspberry-2')['id'])
addChronometerToSession(sessionTtId, getChronometerByName('Raspberry-1')['id'], 1)
addChronometerToSession(sessionTtId, getChronometerByName('Raspberry-3')['id'])

# ----------------------------------------------------------------------
# Send pings
fabriceBeaconId = getBeacon(3003)['id']
jeremyBeaconId = getBeacon(3012)['id']
valentinoBeaconId = getBeacon(3004)['id']
jorgeBeaconId = getBeacon(3008)['id']

chrono0 = getChronometerByName('Raspberry-0')['id']
chrono1 = getChronometerByName('Raspberry-1')['id']
chrono2 = getChronometerByName('Raspberry-2')['id']
chrono3 = getChronometerByName('Raspberry-3')['id']

# import random
# random.shuffle(array)
baseDate = datetime(2016, 8, 22, 11, 0, 0, 1 * 1000)

# ----------------------------------------------------------
# Chronos passed in order  0 1 2 3
# ----------------------------------------------------------
baseDate = pingsForLap(baseDate, 21, fabriceBeaconId, chrono0, chrono1, chrono2, chrono3)
baseDate = pingsForLap(baseDate, 20, fabriceBeaconId, chrono0, chrono1, chrono2, chrono3)
baseDate = pingsForLap(baseDate, 19, fabriceBeaconId, chrono0, chrono1, chrono2, chrono3)
baseDate = pingsForLap(baseDate, 21, fabriceBeaconId, chrono0, chrono1, chrono2, chrono3)
baseDate = pingsForLap(baseDate, 20, fabriceBeaconId, chrono0, chrono1, chrono2, chrono3)
baseDate = pingsForLap(baseDate, 19, fabriceBeaconId, chrono0, chrono1, chrono2, chrono3)
baseDate = pingsForLap(baseDate, 21, fabriceBeaconId, chrono0, chrono1, chrono2, chrono3)
baseDate = pingsForLap(baseDate, 20, fabriceBeaconId, chrono0, chrono1, chrono2, chrono3)
baseDate = pingsForLap(baseDate, 19, fabriceBeaconId, chrono0, chrono1, chrono2, chrono3)
baseDate = pingsForLap(baseDate, 21, fabriceBeaconId, chrono0, chrono1, chrono2, chrono3)
baseDate = pingsForLap(baseDate, 20, fabriceBeaconId, chrono0, chrono1, chrono2, chrono3)
baseDate = pingsForLap(baseDate, 19, fabriceBeaconId, chrono0, chrono1, chrono2, chrono3)

# ----------------------------------------------------------
# Chronos passed in order  1 2 3 0
# ----------------------------------------------------------
baseDate = datetime(2016, 8, 22, 11, 0, 1, 2 * 1000)
baseDate = pingsForLap(baseDate, 19, jeremyBeaconId, chrono1, chrono2, chrono3, chrono0)
baseDate = pingsForLap(baseDate, 18, jeremyBeaconId, chrono1, chrono2, chrono3, chrono0)
baseDate = pingsForLap(baseDate, 17, jeremyBeaconId, chrono1, chrono2, chrono3, chrono0)
baseDate = pingsForLap(baseDate, 19, jeremyBeaconId, chrono1, chrono2, chrono3, chrono0)
baseDate = pingsForLap(baseDate, 18, jeremyBeaconId, chrono1, chrono2, chrono3, chrono0)
baseDate = pingsForLap(baseDate, 17, jeremyBeaconId, chrono1, chrono2, chrono3, chrono0)
baseDate = pingsForLap(baseDate, 19, jeremyBeaconId, chrono1, chrono2, chrono3, chrono0)
baseDate = pingsForLap(baseDate, 18, jeremyBeaconId, chrono1, chrono2, chrono3, chrono0)
baseDate = pingsForLap(baseDate, 17, jeremyBeaconId, chrono1, chrono2, chrono3, chrono0)
baseDate = pingsForLap(baseDate, 19, jeremyBeaconId, chrono1, chrono2, chrono3, chrono0)
baseDate = pingsForLap(baseDate, 18, jeremyBeaconId, chrono1, chrono2, chrono3, chrono0)
baseDate = pingsForLap(baseDate, 17, jeremyBeaconId, chrono1, chrono2, chrono3, chrono0)

# ----------------------------------------------------------
# Chronos passed in order  2 3 0 1
# ----------------------------------------------------------

baseDate = datetime(2016, 8, 22, 11, 0, 2, 3 * 1000)
baseDate = pingsForLap(baseDate, 16, valentinoBeaconId, chrono2, chrono3, chrono0, chrono1)
baseDate = pingsForLap(baseDate, 15, valentinoBeaconId, chrono2, chrono3, chrono0, chrono1)
baseDate = pingsForLap(baseDate, 14, valentinoBeaconId, chrono2, chrono3, chrono0, chrono1)
baseDate = pingsForLap(baseDate, 16, valentinoBeaconId, chrono2, chrono3, chrono0, chrono1)
baseDate = pingsForLap(baseDate, 15, valentinoBeaconId, chrono2, chrono3, chrono0, chrono1)
baseDate = pingsForLap(baseDate, 14, valentinoBeaconId, chrono2, chrono3, chrono0, chrono1)
baseDate = pingsForLap(baseDate, 16, valentinoBeaconId, chrono2, chrono3, chrono0, chrono1)
baseDate = pingsForLap(baseDate, 15, valentinoBeaconId, chrono2, chrono3, chrono0, chrono1)
baseDate = pingsForLap(baseDate, 14, valentinoBeaconId, chrono2, chrono3, chrono0, chrono1)
baseDate = pingsForLap(baseDate, 16, valentinoBeaconId, chrono2, chrono3, chrono0, chrono1)
baseDate = pingsForLap(baseDate, 15, valentinoBeaconId, chrono2, chrono3, chrono0, chrono1)
baseDate = pingsForLap(baseDate, 14, valentinoBeaconId, chrono2, chrono3, chrono0, chrono1)
# ----------------------------------------------------------
# Chronos passed in order  1 2 3 0
# Pings in random order
# ----------------------------------------------------------
ping(datetime(2016, 8, 22, 11, 0, 45, random.randint(0, 100000)), jorgeBeaconId, -83, chrono2)
ping(datetime(2016, 8, 22, 11, 1, 44, random.randint(0, 100000)), jorgeBeaconId, -83, chrono2)
ping(datetime(2016, 8, 22, 11, 2, 47, random.randint(0, 100000)), jorgeBeaconId, -83, chrono2)

ping(datetime(2016, 8, 22, 11, 0, 17, random.randint(0, 100000)), jorgeBeaconId, -83, chrono0)
ping(datetime(2016, 8, 22, 11, 0, 31, random.randint(0, 100000)), jorgeBeaconId, -83, chrono1)
ping(datetime(2016, 8, 22, 11, 0, 59, random.randint(0, 100000)), jorgeBeaconId, -83, chrono3)

ping(datetime(2016, 8, 22, 11, 2, 17, random.randint(0, 100000)), jorgeBeaconId, -83, chrono0)
ping(datetime(2016, 8, 22, 11, 3, 3, random.randint(0, 100000)), jorgeBeaconId, -83, chrono3)
ping(datetime(2016, 8, 22, 11, 2, 31, random.randint(0, 100000)), jorgeBeaconId, -83, chrono1)

ping(datetime(2016, 8, 22, 11, 1, 59, random.randint(0, 100000)), jorgeBeaconId, -83, chrono3)
ping(datetime(2016, 8, 22, 11, 1, 29, random.randint(0, 100000)), jorgeBeaconId, -83, chrono1)
ping(datetime(2016, 8, 22, 11, 1, 14, random.randint(0, 100000)), jorgeBeaconId, -83, chrono0)

ping(datetime(2016, 8, 22, 11, 3, 17, random.randint(0, 100000)), jorgeBeaconId, -83, chrono0)

ping(datetime(2016, 8, 22, 11, 4, 59, random.randint(0, 100000)), jorgeBeaconId, -83, chrono3)
ping(datetime(2016, 8, 22, 11, 4, 44, random.randint(0, 100000)), jorgeBeaconId, -83, chrono2)
ping(datetime(2016, 8, 22, 11, 4, 29, random.randint(0, 100000)), jorgeBeaconId, -83, chrono1)
ping(datetime(2016, 8, 22, 11, 4, 14, random.randint(0, 100000)), jorgeBeaconId, -83, chrono0)

ping(datetime(2016, 8, 22, 11, 3, 31, random.randint(0, 100000)), jorgeBeaconId, -83, chrono1)

ping(datetime(2016, 8, 22, 11, 5, 15, random.randint(0, 100000)), jorgeBeaconId, -83, chrono0)
ping(datetime(2016, 8, 22, 11, 6, 3, random.randint(0, 100000)), jorgeBeaconId, -83, chrono3)
ping(datetime(2016, 8, 22, 11, 5, 31, random.randint(0, 100000)), jorgeBeaconId, -83, chrono1)
ping(datetime(2016, 8, 22, 11, 5, 47, random.randint(0, 100000)), jorgeBeaconId, -83, chrono2)

ping(datetime(2016, 8, 22, 11, 6, 17, random.randint(0, 100000)), jorgeBeaconId, -83, chrono0)
ping(datetime(2016, 8, 22, 11, 6, 31, random.randint(0, 100000)), jorgeBeaconId, -83, chrono1)
ping(datetime(2016, 8, 22, 11, 6, 45, random.randint(0, 100000)), jorgeBeaconId, -83, chrono2)
ping(datetime(2016, 8, 22, 11, 6, 59, random.randint(0, 100000)), jorgeBeaconId, -83, chrono3)

ping(datetime(2016, 8, 22, 11, 3, 59, random.randint(0, 100000)), jorgeBeaconId, -83, chrono3)
ping(datetime(2016, 8, 22, 11, 10, 29, random.randint(0, 100000)), jorgeBeaconId, -83, chrono1)

ping(datetime(2016, 8, 22, 11, 7, 14, random.randint(0, 100000)), jorgeBeaconId, -83, chrono0)
ping(datetime(2016, 8, 22, 11, 7, 29, random.randint(0, 100000)), jorgeBeaconId, -83, chrono1)
ping(datetime(2016, 8, 22, 11, 7, 44, random.randint(0, 100000)), jorgeBeaconId, -83, chrono2)
ping(datetime(2016, 8, 22, 11, 7, 59, random.randint(0, 100000)), jorgeBeaconId, -83, chrono3)

# Lets simulate a track shortcut and a missing ping
#ping(datetime(2016, 8, 22, 11, 8, 15, int( 10000* random.random())), jorgeBeaconId, -83, chrono0)
ping(datetime(2016, 8, 22, 11, 8, 31, random.randint(0, 100000)), jorgeBeaconId, -83, chrono1)
ping(datetime(2016, 8, 22, 11, 8, 47, random.randint(0, 100000)), jorgeBeaconId, -83, chrono2)
ping(datetime(2016, 8, 22, 11, 9, 3, random.randint(0, 100000)), jorgeBeaconId, -83, chrono3)

ping(datetime(2016, 8, 22, 11, 3, 45, random.randint(0, 100000)), jorgeBeaconId, -83, chrono2)
ping(datetime(2016, 8, 22, 11, 10, 44, random.randint(0, 100000)), jorgeBeaconId, -83, chrono2)

ping(datetime(2016, 8, 22, 11, 9, 17, random.randint(0, 100000)), jorgeBeaconId, -83, chrono0)
ping(datetime(2016, 8, 22, 11, 9, 31, random.randint(0, 100000)), jorgeBeaconId, -83, chrono1)
# Lets simulate a track shortcut and a missing ping
#ping(datetime(2016, 8, 22, 11, 9, 45, int( 10000* random.random())), jorgeBeaconId, -83, chrono2)
ping(datetime(2016, 8, 22, 11, 9, 59, random.randint(0, 100000)), jorgeBeaconId, -83, chrono3)

ping(datetime(2016, 8, 22, 11, 10, 14, random.randint(0, 100000)), jorgeBeaconId, -83, chrono0)

ping(datetime(2016, 8, 22, 11, 11, 15, random.randint(0, 100000)), jorgeBeaconId, -83, chrono0)
ping(datetime(2016, 8, 22, 11, 11, 31, random.randint(0, 100000)), jorgeBeaconId, -83, chrono1)
ping(datetime(2016, 8, 22, 11, 11, 47, random.randint(0, 100000)), jorgeBeaconId, -83, chrono2)
ping(datetime(2016, 8, 22, 11, 12, 3, random.randint(0, 100000)), jorgeBeaconId, -83, chrono3)
ping(datetime(2016, 8, 22, 11, 10, 59, random.randint(0, 100000)), jorgeBeaconId, -83, chrono3)

# ----------------------------------------------------------------------
# Get laptimes
#
# We should have per pilot all laps (12) in order with 4 intermediates in each (for 4 chronos)

lapsFabrice = getLapsOfPilot(fabriceTtId, sessionId=sessionTtId)
lapsJeremy = getLapsOfPilot(jeremyTtId, eventId=trd1['id'])
lapsValentino = getLapsOfPilot(valentinoTtId, locationId=leLuc['id'])
lapsJorge = getLapsOfPilot(jorgeTtId, sessionId=sessionTtId)

print("---- Laps Fabrice ----")
printLaps(lapsFabrice)
assert len(lapsFabrice) == 11

print("---- Laps Jeremy ----")
printLaps(lapsJeremy)
assert len(lapsJeremy) == 11

print("---- Laps Valentino ----")
printLaps(lapsValentino)
assert len(lapsValentino) == 11

print("---- Laps Jorge ----")
printLaps(lapsJorge)
assert len(lapsJorge) == 9

# TODO Add more assert

# --------- TODO -------------
# Shuffle the pings in a crazy order and check they are still reordered correctly

# Session summary
laps = getLapsForSession(sessionTtId)
printLaps(laps, True)

# Laps by category
printLaps(getLapsForSession(sessionTtId, cat1['id']), True)

# Laps of location and category
printLaps(getLaps(locationId=leLuc['id'], categoryId=cat1['id']), True)

# Laps of Event and category
printLaps(getLaps(eventId=trd1['id'], categoryId=cat1['id']), True)

print("--------------------------------------------------------------")
print("|     End of Test for Motorbikes on racetrack (n chronos)    |")
print("--------------------------------------------------------------")
