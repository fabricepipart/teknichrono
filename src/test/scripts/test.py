#!/usr/bin/python

import requests
import json
from base import *
from pilots import *
from beacons import *
from chronometer import *

# ----------------------------------------------------------------------

# Cleanup
deleteBeacons()
deletePilots()
deleteChronometers()



# ----------------------------------------------------------------------
# Add Beacons
for i in range(1, 20):
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
    addChronometer('Raspberry #' + str(i+1), i);

# ----------------------------------------------------------------------
# Add Events


# ----------------------------------------------------------------------
# Associate chronometers to event in right order
