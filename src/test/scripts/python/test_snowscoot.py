#!python3

import requests
import json
import sys

from api.base import *
from api.pilots import *
from api.beacons import *
from api.chronometer import *
from api.event import *
from api.ping import *
from api.laps import *
from api.session import *
from api.location import *

# ----------------------------------------------------------------------
# Add Beacons
addBeacon(4012)
addBeacon(4002)
addBeacon(4003)
addBeacon(4004)
addBeacon(4005)
addBeacon(4008)


# ----------------------------------------------------------------------
# Add Pilots

addPilot('Fabrice', 'Snowscoot')
addPilot('Bruce', 'Snowscoot')
addPilot('Rider', 'One')
addPilot('Rider', 'Two')
addPilot('Rider', 'Three')

# ----------------------------------------------------------------------
# Play with associations

associatePilotBeacon(getPilot('Bruce', 'Snowscoot')['id'], getBeacon(4005)['id'])

associatePilotBeacon(getPilot('Fabrice', 'Snowscoot')['id'], getBeacon(4002)['id'])
associatePilotBeacon(getPilot('Rider', 'One')['id'], getBeacon(4012)['id'])

associatePilotBeacon(getPilot('Rider', 'Two')['id'], getBeacon(4004)['id'])
associatePilotBeacon(getPilot('Rider', 'Three')['id'], getBeacon(4008)['id'])

deleteBeacon(getBeacon(2)['id'])
associatePilotBeacon(getPilot('Fabrice', 'Snowscoot')['id'], getBeacon(4003)['id'])

# ----------------------------------------------------------------------
# Add Chronometers
for i in range(0, 3):
  addChronometer('Snowscoot-Raspberry-' + str(i))

# ----------------------------------------------------------------------
# Add Events
event = addEvent('Snowscoot Isola')

# ----------------------------------------------------------------------
# Add Locations
isola = addLocation('Isola', False)

# ----------------------------------------------------------------------
# Add Sessions
session = addSession('Snowscoot Isola', datetime(2017, 1, 27, 8), datetime(2017, 1, 27, 18), 'tt')

addSessionToLocation(isola['id'], session['id'])
addSessionToEvent(event['id'], session['id'])

# ----------------------------------------------------------------------
# Associate chronometers to event in right order

addChronometerToSession(session['id'], getChronometerByName('Snowscoot-Raspberry-0')['id'])
addChronometerToSession(session['id'], getChronometerByName('Snowscoot-Raspberry-2')['id'])
addChronometerToSession(session['id'], getChronometerByName('Snowscoot-Raspberry-1')['id'], 1)

# ----------------------------------------------------------------------
# Send pings
fabriceBeaconId = getBeacon(4003)['id']
bruceBeaconId = getBeacon(4005)['id']
oneBeaconId = getBeacon(4012)['id']
twoBeaconId = getBeacon(4004)['id']
threeBeaconId = getBeacon(4008)['id']

chrono0 = getChronometerByName('Snowscoot-Raspberry-0')['id']
chrono1 = getChronometerByName('Snowscoot-Raspberry-1')['id']
chrono2 = getChronometerByName('Snowscoot-Raspberry-2')['id']

# import random
# random.shuffle(array)
baseDate = datetime(2017, 1, 27, 11, 0, 0, 1 * 1000)

# ----------------------------------------------------------
# Chronos passed in order  0 1 2
# ----------------------------------------------------------

ping(datetime(2017, 1, 27, 11, 0, 1, 1 * 1000), fabriceBeaconId, -83, chrono0)
ping(datetime(2017, 1, 27, 11, 2, 2, 1 * 1000), fabriceBeaconId, -83, chrono1)
ping(datetime(2017, 1, 27, 11, 4, 3, 1 * 1000), fabriceBeaconId, -83, chrono2)

ping(datetime(2017, 1, 27, 15, 0, 1, 1 * 1000), fabriceBeaconId, -83, chrono0)
ping(datetime(2017, 1, 27, 15, 2, 3, 1 * 1000), fabriceBeaconId, -83, chrono1)
ping(datetime(2017, 1, 27, 15, 4, 5, 1 * 1000), fabriceBeaconId, -83, chrono2)

# ----------------------------------------------------------
# Chronos passed in order  0 1 2
# Pings in wrong order
# ----------------------------------------------------------

ping(datetime(2017, 1, 27, 11, 1, 2, 2 * 1000), bruceBeaconId, -83, chrono1)
ping(datetime(2017, 1, 27, 11, 2, 3, 2 * 1000), bruceBeaconId, -83, chrono2)
ping(datetime(2017, 1, 27, 11, 0, 1, 2 * 1000), bruceBeaconId, -83, chrono0)

ping(datetime(2017, 1, 27, 15, 1, 3, 2 * 1000), bruceBeaconId, -83, chrono1)
ping(datetime(2017, 1, 27, 15, 2, 5, 2 * 1000), bruceBeaconId, -83, chrono2)
ping(datetime(2017, 1, 27, 15, 0, 1, 2 * 1000), bruceBeaconId, -83, chrono0)

ping(datetime(2017, 1, 27, 17, 1, 3, 2 * 1000), bruceBeaconId, -83, chrono1)
ping(datetime(2017, 1, 27, 17, 2, 5, 2 * 1000), bruceBeaconId, -83, chrono2)
ping(datetime(2017, 1, 27, 17, 0, 1, 2 * 1000), bruceBeaconId, -83, chrono0)

# ----------------------------------------------------------
# Chronos passed in order  1 2 3 0
# Pings in random order
# ----------------------------------------------------------
ping(datetime(2017, 1, 27, 11, 0, 1, 1 * 1000), oneBeaconId, -83, chrono0)
ping(datetime(2017, 1, 27, 11, 1, 12, 1 * 1000), oneBeaconId, -83, chrono1)
ping(datetime(2017, 1, 27, 11, 2, 23, 1 * 1000), oneBeaconId, -83, chrono2)

ping(datetime(2017, 1, 27, 15, 0, 1, 1 * 1000), oneBeaconId, -83, chrono0)
ping(datetime(2017, 1, 27, 15, 1, 13, 1 * 1000), oneBeaconId, -83, chrono1)
ping(datetime(2017, 1, 27, 15, 2, 25, 1 * 1000), oneBeaconId, -83, chrono2)

ping(datetime(2017, 1, 27, 11, 0, 1, 1 * 1000), twoBeaconId, -83, chrono0)
ping(datetime(2017, 1, 27, 11, 1, 22, 1 * 1000), twoBeaconId, -83, chrono1)
ping(datetime(2017, 1, 27, 11, 2, 33, 1 * 1000), twoBeaconId, -83, chrono2)

ping(datetime(2017, 1, 27, 15, 0, 1, 1 * 1000), twoBeaconId, -83, chrono0)
ping(datetime(2017, 1, 27, 15, 1, 23, 1 * 1000), twoBeaconId, -83, chrono1)
ping(datetime(2017, 1, 27, 15, 2, 35, 1 * 1000), twoBeaconId, -83, chrono2)

ping(datetime(2017, 1, 27, 11, 0, 1, 1 * 1000), threeBeaconId, -83, chrono0)
ping(datetime(2017, 1, 27, 11, 1, 32, 1 * 1000), threeBeaconId, -83, chrono1)
ping(datetime(2017, 1, 27, 11, 2, 53, 1 * 1000), threeBeaconId, -83, chrono2)

ping(datetime(2017, 1, 27, 15, 0, 1, 1 * 1000), threeBeaconId, -83, chrono0)
ping(datetime(2017, 1, 27, 15, 1, 33, 1 * 1000), threeBeaconId, -83, chrono1)
ping(datetime(2017, 1, 27, 15, 2, 55, 1 * 1000), threeBeaconId, -83, chrono2)

# ----------------------------------------------------------------------
# Get laptimes
#
# We should have per pilot all laps (12) in order with 4 intermediates in each (for 4 chronos)

runsFabrice = getLapsOfPilot(getPilot('Fabrice', 'Snowscoot')['id'], sessionId=session['id'])
runsBruce = getLapsOfPilot(getPilot('Bruce', 'Snowscoot')['id'], sessionId=session['id'])
runsOne = getLapsOfPilot(getPilot('Rider', 'One')['id'], sessionId=session['id'])
runsTwo = getLapsOfPilot(getPilot('Rider', 'Two')['id'], sessionId=session['id'])
runsThree = getLapsOfPilot(getPilot('Rider', 'Three')['id'], sessionId=session['id'])

print("---- Laps Fabrice ----")
printLaps(runsFabrice)
assert len(runsFabrice) == 2

print("---- Laps Bruce ----")
printLaps(runsBruce)
assert len(runsBruce) == 3

print("---- Laps One ----")
printLaps(runsOne)
assert len(runsOne) == 2

print("---- Laps Two ----")
printLaps(runsTwo)
assert len(runsTwo) == 2

print("---- Laps Three ----")
printLaps(runsThree)
assert len(runsThree) == 2

# --------- TODO -------------
# Shuffle the pings in a crazy order and check they are still reordered correctly
# --------- TODO -------------

# Session summary
laps = getLaps(eventId=event['id'])
printLaps(laps, True)

# --------- TODO -------------
# -------------------------------
# Time trial with Just one chrono
# -------------------------------

# Laps per pilot
# All Laps
# Session summary
# --------- TODO -------------
# -------------------------------
# Time trial  with several chronos
# -------------------------------

# Laps per pilot
# All Laps
# Session summary
# --------- TODO -------------
# -------------------------------
# Race (derby) with just one chrono
# -------------------------------

# Laps per pilot
# All Laps
# Session summary
# --------- TODO -------------
# -------------------------------
# Race (derby) with several chronos
# -------------------------------

# Laps per pilot
# All Laps
# Session summary
# --------- TODO -------------
# -------------------------------
# Boarder cross  with just one chrono
# -------------------------------

# Laps per pilot
# All Laps
# Session summary

# --------- TODO -------------
# -------------------------------
# Boarder cross  with several chronos
# -------------------------------

# Laps per pilot
# All Laps
# Session summary
# --------- TODO -------------
# -------------------------------
# deux site de descentes differentes qui ont la meme arrivée, mais deux points de départ differents
# -------------------------------

# Laps per pilot
# All Laps
# Session summary