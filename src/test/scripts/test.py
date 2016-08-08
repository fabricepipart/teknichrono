#!/usr/bin/python

import requests
import json
import sys
import variables

from base import *
from pilots import *
from beacons import *
from chronometer import *
from event import *
from ping import *

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

baseDate = datetime.datetime(2016,8,22,11,0,1,2*1000)
baseDate = pingsForLap( baseDate, 19, jeremyBeaconId, chrono0, chrono1, chrono2, chrono3 );
baseDate = pingsForLap( baseDate, 18, jeremyBeaconId, chrono0, chrono1, chrono2, chrono3 );
baseDate = pingsForLap( baseDate, 17, jeremyBeaconId, chrono0, chrono1, chrono2, chrono3 );
baseDate = pingsForLap( baseDate, 19, jeremyBeaconId, chrono0, chrono1, chrono2, chrono3 );
baseDate = pingsForLap( baseDate, 18, jeremyBeaconId, chrono0, chrono1, chrono2, chrono3 );
baseDate = pingsForLap( baseDate, 17, jeremyBeaconId, chrono0, chrono1, chrono2, chrono3 );
baseDate = pingsForLap( baseDate, 19, jeremyBeaconId, chrono0, chrono1, chrono2, chrono3 );
baseDate = pingsForLap( baseDate, 18, jeremyBeaconId, chrono0, chrono1, chrono2, chrono3 );
baseDate = pingsForLap( baseDate, 17, jeremyBeaconId, chrono0, chrono1, chrono2, chrono3 );
baseDate = pingsForLap( baseDate, 19, jeremyBeaconId, chrono0, chrono1, chrono2, chrono3 );
baseDate = pingsForLap( baseDate, 18, jeremyBeaconId, chrono0, chrono1, chrono2, chrono3 );
baseDate = pingsForLap( baseDate, 17, jeremyBeaconId, chrono0, chrono1, chrono2, chrono3 );

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

baseDate = datetime.datetime(2016,8,22,11,0,3,4*1000)
baseDate = pingsForLap( baseDate, 14, jorgeBeaconId, chrono0, chrono1, chrono2, chrono3 );
baseDate = pingsForLap( baseDate, 15, jorgeBeaconId, chrono0, chrono1, chrono2, chrono3 );
baseDate = pingsForLap( baseDate, 16, jorgeBeaconId, chrono0, chrono1, chrono2, chrono3 );
baseDate = pingsForLap( baseDate, 14, jorgeBeaconId, chrono0, chrono1, chrono2, chrono3 );
baseDate = pingsForLap( baseDate, 15, jorgeBeaconId, chrono0, chrono1, chrono2, chrono3 );
baseDate = pingsForLap( baseDate, 16, jorgeBeaconId, chrono0, chrono1, chrono2, chrono3 );
baseDate = pingsForLap( baseDate, 14, jorgeBeaconId, chrono0, chrono1, chrono2, chrono3 );
baseDate = pingsForLap( baseDate, 15, jorgeBeaconId, chrono0, chrono1, chrono2, chrono3 );
baseDate = pingsForLap( baseDate, 16, jorgeBeaconId, chrono0, chrono1, chrono2, chrono3 );
baseDate = pingsForLap( baseDate, 14, jorgeBeaconId, chrono0, chrono1, chrono2, chrono3 );
baseDate = pingsForLap( baseDate, 15, jorgeBeaconId, chrono0, chrono1, chrono2, chrono3 );
baseDate = pingsForLap( baseDate, 16, jorgeBeaconId, chrono0, chrono1, chrono2, chrono3 );
