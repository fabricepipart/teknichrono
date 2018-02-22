#!python3

import requests
import json
import sys

from base import *
from pilots import *
from beacons import *
from chronometer import *
from event import *
from ping import *
from laps import *

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
deleteEvents()

# ----------------------------------------------------------------------
# Add Beacons
for i in range(0, 20):
  addBeacon(i)

# ----------------------------------------------------------------------
# Add Pilots

addPilot('Fabrice', 'Pipart')
addPilot('Bruce', 'Rulfo')
addPilot('Rider', 'One')
addPilot('Rider', 'Two')
addPilot('Rider', 'Three')

# ----------------------------------------------------------------------
# Play with associations

associatePilotBeacon(getPilot('Bruce', 'Rulfo')['id'], getBeacon(5)['id'])

associatePilotBeacon(getPilot('Fabrice', 'Pipart')['id'], getBeacon(2)['id'])
associatePilotBeacon(getPilot('Rider', 'One')['id'], getBeacon(12)['id'])

associatePilotBeacon(getPilot('Rider', 'Two')['id'], getBeacon(4)['id'])
associatePilotBeacon(getPilot('Rider', 'Three')['id'], getBeacon(8)['id'])

deleteBeacon(getBeacon(2)['id'])
associatePilotBeacon(getPilot('Fabrice', 'Pipart')['id'], getBeacon(3)['id'])

# ----------------------------------------------------------------------
# Add Chronometers
for i in range(0, 3):
  addChronometer('Raspberry-' + str(i))

# ----------------------------------------------------------------------
# Add Events
addEvent('Snowscoot Isola', date(2016, 8, 26), date(2016, 8, 27), False)

# ----------------------------------------------------------------------
# Associate chronometers to event in right order
event = getEventByName('Snowscoot Isola')

addChronometerToEvent(event['id'], getChronometerByName('Raspberry-0')['id'])
addChronometerToEvent(event['id'], getChronometerByName('Raspberry-2')['id'])
addChronometerToEvent(event['id'], getChronometerByName('Raspberry-1')['id'], 1)

# ----------------------------------------------------------------------
# Send pings
fabriceBeaconId = getBeacon(3)['id']
bruceBeaconId = getBeacon(5)['id']
oneBeaconId = getBeacon(12)['id']
twoBeaconId = getBeacon(4)['id']
threeBeaconId = getBeacon(8)['id']

chrono0 = getChronometerByName('Raspberry-0')['id']
chrono1 = getChronometerByName('Raspberry-1')['id']
chrono2 = getChronometerByName('Raspberry-2')['id']

# import random
# random.shuffle(array)
baseDate = datetime.datetime(2017, 1, 27, 11, 0, 0, 1 * 1000)

# ----------------------------------------------------------
# Chronos passed in order  0 1 2
# ----------------------------------------------------------

ping(datetime.datetime(2017, 1, 27, 11, 0, 1, 1 * 1000), fabriceBeaconId, -83, chrono0)
ping(datetime.datetime(2017, 1, 27, 11, 2, 2, 1 * 1000), fabriceBeaconId, -83, chrono1)
ping(datetime.datetime(2017, 1, 27, 11, 4, 3, 1 * 1000), fabriceBeaconId, -83, chrono2)

ping(datetime.datetime(2017, 1, 27, 15, 0, 1, 1 * 1000), fabriceBeaconId, -83, chrono0)
ping(datetime.datetime(2017, 1, 27, 15, 2, 3, 1 * 1000), fabriceBeaconId, -83, chrono1)
ping(datetime.datetime(2017, 1, 27, 15, 4, 5, 1 * 1000), fabriceBeaconId, -83, chrono2)

# ----------------------------------------------------------
# Chronos passed in order  0 1 2
# Pings in wrong order
# ----------------------------------------------------------

ping(datetime.datetime(2017, 1, 27, 11, 1, 2, 2 * 1000), bruceBeaconId, -83, chrono1)
ping(datetime.datetime(2017, 1, 27, 11, 2, 3, 2 * 1000), bruceBeaconId, -83, chrono2)
ping(datetime.datetime(2017, 1, 27, 11, 0, 1, 2 * 1000), bruceBeaconId, -83, chrono0)

ping(datetime.datetime(2017, 1, 27, 15, 1, 3, 2 * 1000), bruceBeaconId, -83, chrono1)
ping(datetime.datetime(2017, 1, 27, 15, 2, 5, 2 * 1000), bruceBeaconId, -83, chrono2)
ping(datetime.datetime(2017, 1, 27, 15, 0, 1, 2 * 1000), bruceBeaconId, -83, chrono0)

ping(datetime.datetime(2017, 1, 27, 17, 1, 3, 2 * 1000), bruceBeaconId, -83, chrono1)
ping(datetime.datetime(2017, 1, 27, 17, 2, 5, 2 * 1000), bruceBeaconId, -83, chrono2)
ping(datetime.datetime(2017, 1, 27, 17, 0, 1, 2 * 1000), bruceBeaconId, -83, chrono0)

# ----------------------------------------------------------
# Chronos passed in order  1 2 3 0
# Pings in random order
# ----------------------------------------------------------
ping(datetime.datetime(2017, 1, 27, 11, 0, 1, 1 * 1000), oneBeaconId, -83, chrono0)
ping(datetime.datetime(2017, 1, 27, 11, 1, 12, 1 * 1000), oneBeaconId, -83, chrono1)
ping(datetime.datetime(2017, 1, 27, 11, 2, 23, 1 * 1000), oneBeaconId, -83, chrono2)

ping(datetime.datetime(2017, 1, 27, 15, 0, 1, 1 * 1000), oneBeaconId, -83, chrono0)
ping(datetime.datetime(2017, 1, 27, 15, 1, 13, 1 * 1000), oneBeaconId, -83, chrono1)
ping(datetime.datetime(2017, 1, 27, 15, 2, 25, 1 * 1000), oneBeaconId, -83, chrono2)

ping(datetime.datetime(2017, 1, 27, 11, 0, 1, 1 * 1000), twoBeaconId, -83, chrono0)
ping(datetime.datetime(2017, 1, 27, 11, 1, 22, 1 * 1000), twoBeaconId, -83, chrono1)
ping(datetime.datetime(2017, 1, 27, 11, 2, 33, 1 * 1000), twoBeaconId, -83, chrono2)

ping(datetime.datetime(2017, 1, 27, 15, 0, 1, 1 * 1000), twoBeaconId, -83, chrono0)
ping(datetime.datetime(2017, 1, 27, 15, 1, 23, 1 * 1000), twoBeaconId, -83, chrono1)
ping(datetime.datetime(2017, 1, 27, 15, 2, 35, 1 * 1000), twoBeaconId, -83, chrono2)

ping(datetime.datetime(2017, 1, 27, 11, 0, 1, 1 * 1000), threeBeaconId, -83, chrono0)
ping(datetime.datetime(2017, 1, 27, 11, 1, 32, 1 * 1000), threeBeaconId, -83, chrono1)
ping(datetime.datetime(2017, 1, 27, 11, 2, 53, 1 * 1000), threeBeaconId, -83, chrono2)

ping(datetime.datetime(2017, 1, 27, 15, 0, 1, 1 * 1000), threeBeaconId, -83, chrono0)
ping(datetime.datetime(2017, 1, 27, 15, 1, 33, 1 * 1000), threeBeaconId, -83, chrono1)
ping(datetime.datetime(2017, 1, 27, 15, 2, 55, 1 * 1000), threeBeaconId, -83, chrono2)

# ----------------------------------------------------------------------
# Get laptimes
#
# We should have per pilot all laps (12) in order with 4 intermediates in each (for 4 chronos)

runsFabrice = getLapsOfPilot(getPilot('Fabrice', 'Pipart')['id'])
runsBruce = getLapsOfPilot(getPilot('Bruce', 'Rulfo')['id'])
runsOne = getLapsOfPilot(getPilot('Rider', 'One')['id'])
runsTwo = getLapsOfPilot(getPilot('Rider', 'Two')['id'])
runsThree = getLapsOfPilot(getPilot('Rider', 'Three')['id'])

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

laps = getLaps()
printLaps(laps, True)

# --------- TODO -------------
# Time trial with Just one chrono

# --------- TODO -------------
# Time trial  with several chronos

# --------- TODO -------------
# Race (derby) with just one chrono

# --------- TODO -------------
# Race (derby) with several chronos

# --------- TODO -------------
# Boarder cross  with just one chrono
# --------- TODO -------------
# Boarder cross  with several chronos

# --------- TODO -------------
# deux site de descentes differentes qui ont la meme arrivée, mais deux points de départ differents