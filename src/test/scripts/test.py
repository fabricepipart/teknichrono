#!/usr/bin/python

import requests
import json
from datetime import date
from base import *
from pilots import *
from beacons import *
from chronometer import *
from event import *

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
print event
addChronometerToEvent(event['id'], getChronometerByName('Raspberry-0')['id'])
event = getEventByName('TRD Le Luc 2016-08-22')
print event

addChronometerToEvent(event['id'], getChronometerByName('Raspberry-2')['id'])
event = getEventByName('TRD Le Luc 2016-08-22')
print event

addChronometerToEvent(event['id'], getChronometerByName('Raspberry-1')['id'], 1)

event = getEventByName('TRD Le Luc 2016-08-22')
print event
