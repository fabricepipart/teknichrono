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

jerome = addPilot('Jerome', 'Rousseau')
fabrice = addPilot('Fabrice', 'Pipart')
jeremy = addPilot('Jeremy', 'Ponchel')
valentino = addPilot('Valentino', 'TRD')
jorge = addPilot('Jorge', 'TRD')

# ----------------------------------------------------------------------
# Add Categories
cat1 = addCategory('Cat1')
cat2 = addCategory('Cat2')
cat3 = addCategory('Cat3')

addPilotToCategory(cat3['id'], fabrice['id'])
addPilotToCategory(cat2['id'], jeremy['id'])
addPilotToCategory(cat1['id'], valentino['id'])
addPilotToCategory(cat1['id'], jorge['id'])

# ----------------------------------------------------------------------
# Play with associations

associatePilotBeacon(jerome['id'], getBeacon(12)['id'])

associatePilotBeacon(fabrice['id'], getBeacon(2)['id'])
associatePilotBeacon(jeremy['id'], getBeacon(12)['id'])

associatePilotBeacon(valentino['id'], getBeacon(4)['id'])
associatePilotBeacon(jorge['id'], getBeacon(8)['id'])

deleteBeacon(getBeacon(2)['id'])
associatePilotBeacon(fabrice['id'], getBeacon(3)['id'])

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
session = addSession('Morning TRD Le Luc 2016-08-22', datetime(2016, 8, 22), datetime(2016, 8, 22), 'tt')
addSessionToLocation(leLuc['id'], session['id'])
addSessionToEvent(trd1['id'], session['id'])

addSession('Morning TRD Ledenon 2016-09-12+13', datetime(2016, 9, 12), datetime(2016, 9, 13), 'tt')
addSession('Morning TRD Aragon 2016-10-22+23', datetime(2016, 10, 22), datetime(2016, 10, 23), 'tt')
addSession('Morning TRD Le Luc 2016-11-01', datetime(2016, 11, 1), datetime(2016, 11, 1), 'tt')
addSession('Session of Rally 1', datetime(2017, 3, 1), datetime(2017, 3, 1), 'tt')

# ----------------------------------------------------------------------
# Associate chronometers to session in right order
addChronometerToSession(session['id'], getChronometerByName('Raspberry-0')['id'])
addChronometerToSession(session['id'], getChronometerByName('Raspberry-2')['id'])
addChronometerToSession(session['id'], getChronometerByName('Raspberry-1')['id'], 1)
addChronometerToSession(session['id'], getChronometerByName('Raspberry-3')['id'])

# ----------------------------------------------------------------------
# Send pings
fabriceBeaconId = getBeacon(3)['id']
jeremyBeaconId = getBeacon(12)['id']
valentinoBeaconId = getBeacon(4)['id']
jorgeBeaconId = getBeacon(8)['id']

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

lapsFabrice = getLapsOfPilot(fabrice['id'], sessionId=session['id'])
lapsJeremy = getLapsOfPilot(jeremy['id'], eventId=trd1['id'])
lapsValentino = getLapsOfPilot(valentino['id'], locationId=leLuc['id'])
lapsJorge = getLapsOfPilot(jorge['id'], sessionId=session['id'])

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
laps = getLapsForSession(session['id'])
printLaps(laps, True)

# Laps by category
printLaps(getLapsForSession(session['id'], cat1['id']), True)

# Laps of location and category
printLaps(getLaps(locationId=leLuc['id'], categoryId=cat1['id']), True)

# Laps of Event and category
printLaps(getLaps(eventId=trd1['id'], categoryId=cat1['id']), True)
