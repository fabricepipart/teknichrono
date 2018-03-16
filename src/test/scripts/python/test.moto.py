#!python3

import requests
import json
import sys

from base import *
from pilots import *
from beacons import *
from chronometer import *
from event import *
from session import *
from location import *
from ping import *
from laps import *
from tests import *

# ----------------------------------------------------------------------
# Test dqte primitives
print("Date = " + formatDatetime(timestampToDate(1471863621321)))
# 2016-08-22T11:00:21.321Z

# ----------------------------------------------------------------------
# Command line parameters
print(len(sys.argv))
if len(sys.argv) >= 2:
  setHost(sys.argv[1])

# ----------------------------------------------------------------------

# Cleanup
deleteBeacons()
deletePilots()
deleteChronometers()
deleteSessions()
deleteEvents()
deleteLocations()

# ----------------------------------------------------------------------
# Add Beacons
for i in range(0, 100):
  addBeacon(i)

# ----------------------------------------------------------------------
# Add Pilots

jerome = addPilot('Jerome', 'Rousseau')
fabrice = addPilot('Fabrice', 'Pipart')
jeremy = addPilot('Jeremy', 'Ponchel')
valentino = addPilot('Valentino', 'Rossi')
marc = addPilot('Marc', 'Marquez')
dani = addPilot('Dani', 'Pedrosa')
jorge = addPilot('Jorge', 'Lorenzo')
johann = addPilot('Johann', 'Zarco')
cal = addPilot('Cal', 'Crutchlow')

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
ledenon = addLocation('Ledenon')
aragon = addLocation('Aragon')
rally = addLocation('Rally #1', False)

# ----------------------------------------------------------------------
# Add Events
trd1 = addEvent('TRD Le Luc 2016-08-22')
trd2 = addEvent('TRD Ledenon 2016-09-12+13')
trd3 = addEvent('TRD Aragon 2016-10-22+23')
trd4 = addEvent('TRD Le Luc 2016-11-01')
rally = addEvent('Rally-1-event')

# ----------------------------------------------------------------------
# Add Sessions
session = addSession('Morning TRD Le Luc 2016-08-22', datetime(2016, 8, 22), datetime(2016, 8, 22))
addSessionToLocation(leLuc['id'], session['id'])
addSessionToEvent(trd1['id'], session['id'])

addSession('Morning TRD Ledenon 2016-09-12+13', datetime(2016, 9, 12), datetime(2016, 9, 13))
addSession('Morning TRD Aragon 2016-10-22+23', datetime(2016, 10, 22), datetime(2016, 10, 23))
addSession('Morning TRD Le Luc 2016-11-01', datetime(2016, 11, 1), datetime(2016, 11, 1))
addSession('Session of Rally 1', datetime(2017, 3, 1), datetime(2017, 3, 1))

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
assert len(lapsFabrice) == 12

print("---- Laps Jeremy ----")
printLaps(lapsJeremy)
assert len(lapsJeremy) == 13

print("---- Laps Valentino ----")
printLaps(lapsValentino)
assert len(lapsValentino) == 13

print("---- Laps Jorge ----")
printLaps(lapsJorge)
assert len(lapsJorge) == 12

# TODO Add more assert

# --------- TODO -------------
# Shuffle the pings in a crazy order and check they are still reordered correctly

# Session summary
laps = getLapsForSession(session['id'])
printLaps(laps, True)

# --------- TODO -------------
print("-------------------------------")
print("Time trial with Just one chrono")
print("-------------------------------")

# TODO Session of same event

eventName = 'Time trial with Just one chrono'
timeTrial = addEvent(eventName)

morningSession = addSession(eventName + ' morning session', datetime(2018, 1, 1, 8, 0, 0, 0),
                            datetime(2018, 1, 1, 12, 0, 0, 0))
addSessionToLocation(ledenon['id'], morningSession['id'])
addSessionToEvent(timeTrial['id'], morningSession['id'])
addChronometerToSession(morningSession['id'], getChronometerByName('Raspberry-1')['id'])
chrono1 = getChronometerByName('Raspberry-1')['id']

session = addSession(eventName + ' afternoon session', datetime(2018, 1, 1, 14, 0, 0, 0),
                     datetime(2018, 1, 1, 18, 0, 0, 0))
addSessionToLocation(ledenon['id'], session['id'])
addSessionToEvent(timeTrial['id'], session['id'])
addChronometerToSession(session['id'], getChronometerByName('Raspberry-0')['id'])
chrono0 = getChronometerByName('Raspberry-0')['id']

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