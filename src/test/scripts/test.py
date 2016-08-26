#!/usr/bin/python

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
print "Date = " + formatDatetime(timestampToDate(1471863621321))
# 2016-08-22T11:00:21.321Z

# ----------------------------------------------------------------------
# Command line parameters
print len(sys.argv)
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
    addBeacon(i);


# ----------------------------------------------------------------------
# Add Pilots

addPilot('Jerome', 'Rousseau')
addPilot('Fabrice', 'Pipart')
addPilot('Jeremy', 'Ponchel')
addPilot('Valentino', 'Rossi')
addPilot('Marc', 'Marquez')
addPilot('Dani', 'Pedrosa')
addPilot('Jorge', 'Lorenzo')


# ----------------------------------------------------------------------
# Play with associations

associatePilotBeacon(getPilot('Jerome', 'Rousseau')['id'],getBeacon(12)['id'])

associatePilotBeacon(getPilot('Fabrice', 'Pipart')['id'],getBeacon(2)['id'])
associatePilotBeacon(getPilot('Jeremy', 'Ponchel')['id'],getBeacon(12)['id'])

associatePilotBeacon(getPilot('Valentino', 'Rossi')['id'],getBeacon(4)['id'])
associatePilotBeacon(getPilot('Jorge', 'Lorenzo')['id'],getBeacon(8)['id'])

deleteBeacon(getBeacon(2)['id'])
associatePilotBeacon(getPilot('Fabrice', 'Pipart')['id'],getBeacon(3)['id'])


# ----------------------------------------------------------------------
# Add Chronometers
for i in range(0, 5):
    addChronometer('Raspberry-' + str(i));

# ----------------------------------------------------------------------
# Add Events
addEvent('TRD Le Luc 2016-08-22', date(2016,8,22), date(2016,8,22));
addEvent('TRD Ledenon 2016-09-12+13', date(2016,9,12), date(2016,9,13));
addEvent('TRD Aragon 2016-10-22+23', date(2016,10,22), date(2016,10,23));
addEvent('TRD Le Luc 2016-11-01', date(2016,11,01), date(2016,11,01));
addEvent('Rally #1', date(2017,3,1), date(2017,3,1), False);

# ----------------------------------------------------------------------
# Associate chronometers to event in right order
event = getEventByName('TRD Le Luc 2016-08-22')

addChronometerToEvent(event['id'], getChronometerByName('Raspberry-0')['id'])
addChronometerToEvent(event['id'], getChronometerByName('Raspberry-2')['id'])
addChronometerToEvent(event['id'], getChronometerByName('Raspberry-1')['id'], 1)
addChronometerToEvent(event['id'], getChronometerByName('Raspberry-3')['id'])

# ----------------------------------------------------------------------
# Send pings
fabriceBeaconId = getBeacon(3)['id'];
jeremyBeaconId = getBeacon(12)['id'];
valentinoBeaconId = getBeacon(4)['id'];
jorgeBeaconId = getBeacon(8)['id'];

chrono0 = getChronometerByName('Raspberry-0')['id'];
chrono1 = getChronometerByName('Raspberry-1')['id'];
chrono2 = getChronometerByName('Raspberry-2')['id'];
chrono3 = getChronometerByName('Raspberry-3')['id'];


# import random
# random.shuffle(array)
baseDate = datetime.datetime(2016,8,22,11,0,0,1*1000);

# ----------------------------------------------------------
# Chronos passed in order  0 1 2 3
# ----------------------------------------------------------
baseDate = pingsForLap( baseDate, 21, fabriceBeaconId, chrono0, chrono1, chrono2, chrono3 );
baseDate = pingsForLap( baseDate, 20, fabriceBeaconId, chrono0, chrono1, chrono2, chrono3 );
baseDate = pingsForLap( baseDate, 19, fabriceBeaconId, chrono0, chrono1, chrono2, chrono3 );
baseDate = pingsForLap( baseDate, 21, fabriceBeaconId, chrono0, chrono1, chrono2, chrono3 );
baseDate = pingsForLap( baseDate, 20, fabriceBeaconId, chrono0, chrono1, chrono2, chrono3 );
baseDate = pingsForLap( baseDate, 19, fabriceBeaconId, chrono0, chrono1, chrono2, chrono3 );
baseDate = pingsForLap( baseDate, 21, fabriceBeaconId, chrono0, chrono1, chrono2, chrono3 );
baseDate = pingsForLap( baseDate, 20, fabriceBeaconId, chrono0, chrono1, chrono2, chrono3 );
baseDate = pingsForLap( baseDate, 19, fabriceBeaconId, chrono0, chrono1, chrono2, chrono3 );
baseDate = pingsForLap( baseDate, 21, fabriceBeaconId, chrono0, chrono1, chrono2, chrono3 );
baseDate = pingsForLap( baseDate, 20, fabriceBeaconId, chrono0, chrono1, chrono2, chrono3 );
baseDate = pingsForLap( baseDate, 19, fabriceBeaconId, chrono0, chrono1, chrono2, chrono3 );

# ----------------------------------------------------------
# Chronos passed in order  1 2 3 0
# ----------------------------------------------------------
baseDate = datetime.datetime(2016,8,22,11,0,1,2*1000)
baseDate = pingsForLap( baseDate, 19, jeremyBeaconId, chrono1, chrono2, chrono3, chrono0 );
baseDate = pingsForLap( baseDate, 18, jeremyBeaconId, chrono1, chrono2, chrono3, chrono0 );
baseDate = pingsForLap( baseDate, 17, jeremyBeaconId, chrono1, chrono2, chrono3, chrono0 );
baseDate = pingsForLap( baseDate, 19, jeremyBeaconId, chrono1, chrono2, chrono3, chrono0 );
baseDate = pingsForLap( baseDate, 18, jeremyBeaconId, chrono1, chrono2, chrono3, chrono0 );
baseDate = pingsForLap( baseDate, 17, jeremyBeaconId, chrono1, chrono2, chrono3, chrono0 );
baseDate = pingsForLap( baseDate, 19, jeremyBeaconId, chrono1, chrono2, chrono3, chrono0 );
baseDate = pingsForLap( baseDate, 18, jeremyBeaconId, chrono1, chrono2, chrono3, chrono0 );
baseDate = pingsForLap( baseDate, 17, jeremyBeaconId, chrono1, chrono2, chrono3, chrono0 );
baseDate = pingsForLap( baseDate, 19, jeremyBeaconId, chrono1, chrono2, chrono3, chrono0 );
baseDate = pingsForLap( baseDate, 18, jeremyBeaconId, chrono1, chrono2, chrono3, chrono0 );
baseDate = pingsForLap( baseDate, 17, jeremyBeaconId, chrono1, chrono2, chrono3, chrono0 );

baseDate = datetime.datetime(2016,8,22,11,0,2,3*1000)
baseDate = pingsForLap( baseDate, 16, valentinoBeaconId, chrono0, chrono1, chrono2, chrono3 );
baseDate = pingsForLap( baseDate, 15, valentinoBeaconId, chrono0, chrono1, chrono2, chrono3 );
baseDate = pingsForLap( baseDate, 14, valentinoBeaconId, chrono0, chrono1, chrono2, chrono3 );
baseDate = pingsForLap( baseDate, 16, valentinoBeaconId, chrono0, chrono1, chrono2, chrono3 );
baseDate = pingsForLap( baseDate, 15, valentinoBeaconId, chrono0, chrono1, chrono2, chrono3 );
baseDate = pingsForLap( baseDate, 14, valentinoBeaconId, chrono0, chrono1, chrono2, chrono3 );
baseDate = pingsForLap( baseDate, 16, valentinoBeaconId, chrono0, chrono1, chrono2, chrono3 );
baseDate = pingsForLap( baseDate, 15, valentinoBeaconId, chrono0, chrono1, chrono2, chrono3 );
baseDate = pingsForLap( baseDate, 14, valentinoBeaconId, chrono0, chrono1, chrono2, chrono3 );
baseDate = pingsForLap( baseDate, 16, valentinoBeaconId, chrono0, chrono1, chrono2, chrono3 );
baseDate = pingsForLap( baseDate, 15, valentinoBeaconId, chrono0, chrono1, chrono2, chrono3 );
baseDate = pingsForLap( baseDate, 14, valentinoBeaconId, chrono0, chrono1, chrono2, chrono3 );

# ----------------------------------------------------------
# Chronos passed in order  1 2 3 0
# Pings in random order
# ----------------------------------------------------------
ping(datetime.datetime(2016,8,22,11,0,45,4*1000), jorgeBeaconId, -83, chrono2)
ping(datetime.datetime(2016,8,22,11,1,44,4*1000), jorgeBeaconId, -83, chrono2)
ping(datetime.datetime(2016,8,22,11,2,47,4*1000), jorgeBeaconId, -83, chrono2)

ping(datetime.datetime(2016,8,22,11,0,17,4*1000), jorgeBeaconId, -83, chrono0)
ping(datetime.datetime(2016,8,22,11,0,31,4*1000), jorgeBeaconId, -83, chrono1)
ping(datetime.datetime(2016,8,22,11,0,59,4*1000), jorgeBeaconId, -83, chrono3)

ping(datetime.datetime(2016,8,22,11,2,17,4*1000), jorgeBeaconId, -83, chrono0)
ping(datetime.datetime(2016,8,22,11,3,3,4*1000), jorgeBeaconId, -83, chrono3)
ping(datetime.datetime(2016,8,22,11,2,31,4*1000), jorgeBeaconId, -83, chrono1)

ping(datetime.datetime(2016,8,22,11,1,59,4*1000), jorgeBeaconId, -83, chrono3)
ping(datetime.datetime(2016,8,22,11,1,29,4*1000), jorgeBeaconId, -83, chrono1)
ping(datetime.datetime(2016,8,22,11,1,14,4*1000), jorgeBeaconId, -83, chrono0)

ping(datetime.datetime(2016,8,22,11,3,17,4*1000), jorgeBeaconId, -83, chrono0)

ping(datetime.datetime(2016,8,22,11,4,59,4*1000), jorgeBeaconId, -83, chrono3)
ping(datetime.datetime(2016,8,22,11,4,44,4*1000), jorgeBeaconId, -83, chrono2)
ping(datetime.datetime(2016,8,22,11,4,29,4*1000), jorgeBeaconId, -83, chrono1)
ping(datetime.datetime(2016,8,22,11,4,14,4*1000), jorgeBeaconId, -83, chrono0)

ping(datetime.datetime(2016,8,22,11,3,31,4*1000), jorgeBeaconId, -83, chrono1)

ping(datetime.datetime(2016,8,22,11,5,15,4*1000), jorgeBeaconId, -83, chrono0)
ping(datetime.datetime(2016,8,22,11,6,3,4*1000), jorgeBeaconId, -83, chrono3)
ping(datetime.datetime(2016,8,22,11,5,31,4*1000), jorgeBeaconId, -83, chrono1)
ping(datetime.datetime(2016,8,22,11,5,47,4*1000), jorgeBeaconId, -83, chrono2)

ping(datetime.datetime(2016,8,22,11,6,17,4*1000), jorgeBeaconId, -83, chrono0)
ping(datetime.datetime(2016,8,22,11,6,31,4*1000), jorgeBeaconId, -83, chrono1)
ping(datetime.datetime(2016,8,22,11,6,45,4*1000), jorgeBeaconId, -83, chrono2)
ping(datetime.datetime(2016,8,22,11,6,59,4*1000), jorgeBeaconId, -83, chrono3)

ping(datetime.datetime(2016,8,22,11,3,59,4*1000), jorgeBeaconId, -83, chrono3)
ping(datetime.datetime(2016,8,22,11,10,29,4*1000), jorgeBeaconId, -83, chrono1)

ping(datetime.datetime(2016,8,22,11,7,14,4*1000), jorgeBeaconId, -83, chrono0)
ping(datetime.datetime(2016,8,22,11,7,29,4*1000), jorgeBeaconId, -83, chrono1)
ping(datetime.datetime(2016,8,22,11,7,44,4*1000), jorgeBeaconId, -83, chrono2)
ping(datetime.datetime(2016,8,22,11,7,59,4*1000), jorgeBeaconId, -83, chrono3)

ping(datetime.datetime(2016,8,22,11,8,15,4*1000), jorgeBeaconId, -83, chrono0)
ping(datetime.datetime(2016,8,22,11,8,31,4*1000), jorgeBeaconId, -83, chrono1)
ping(datetime.datetime(2016,8,22,11,8,47,4*1000), jorgeBeaconId, -83, chrono2)
ping(datetime.datetime(2016,8,22,11,9,3,4*1000), jorgeBeaconId, -83, chrono3)

ping(datetime.datetime(2016,8,22,11,3,45,4*1000), jorgeBeaconId, -83, chrono2)
ping(datetime.datetime(2016,8,22,11,10,44,4*1000), jorgeBeaconId, -83, chrono2)

ping(datetime.datetime(2016,8,22,11,9,17,4*1000), jorgeBeaconId, -83, chrono0)
ping(datetime.datetime(2016,8,22,11,9,31,4*1000), jorgeBeaconId, -83, chrono1)
ping(datetime.datetime(2016,8,22,11,9,45,4*1000), jorgeBeaconId, -83, chrono2)
ping(datetime.datetime(2016,8,22,11,9,59,4*1000), jorgeBeaconId, -83, chrono3)

ping(datetime.datetime(2016,8,22,11,10,14,4*1000), jorgeBeaconId, -83, chrono0)

ping(datetime.datetime(2016,8,22,11,11,15,4*1000), jorgeBeaconId, -83, chrono0)
ping(datetime.datetime(2016,8,22,11,11,31,4*1000), jorgeBeaconId, -83, chrono1)
ping(datetime.datetime(2016,8,22,11,11,47,4*1000), jorgeBeaconId, -83, chrono2)
ping(datetime.datetime(2016,8,22,11,12,3,4*1000), jorgeBeaconId, -83, chrono3)

ping(datetime.datetime(2016,8,22,11,10,59,4*1000), jorgeBeaconId, -83, chrono3)

# ----------------------------------------------------------------------
# Get laptimes
#
# We should have per pilot all laps (12) in order with 4 intermediates in each (for 4 chronos)

lapsFabrice = getLapsOfPilot(getPilot('Fabrice', 'Pipart')['id'])
lapsJeremy = getLapsOfPilot(getPilot('Jeremy', 'Ponchel')['id'])
lapsValentino = getLapsOfPilot(getPilot('Valentino', 'Rossi')['id'])
lapsJorge = getLapsOfPilot(getPilot('Jorge', 'Lorenzo')['id'])

print "---- Laps Fabrice ----"
printLaps(lapsFabrice)
assert len(lapsFabrice) == 12

print "---- Laps Jeremy ----"
printLaps(lapsJeremy)
assert len(lapsJeremy) == 13

print "---- Laps Valentino ----"
printLaps(lapsValentino)
assert len(lapsValentino) == 12

print "---- Laps Jorge ----"
printLaps(lapsJorge)
assert len(lapsJorge) == 12


# --------- TODO -------------
# Shuffle the pings in a crazy order and check they are still reordered correctly
# --------- TODO -------------
