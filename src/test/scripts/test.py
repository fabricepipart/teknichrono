#!/usr/bin/python

import requests
import json
from base import *
from pilots import *
from beacons import *

# ----------------------------------------------------------------------

# Cleanup
print getBeacons()
deleteBeacons()
print getBeacons()

print getPilots()
deletePilots()
print getPilots()



# Add Beacons
for i in range(1, 20):
    addBeacon(i);

addPilot('Jerome', 'Rousseau')
addPilot('Fabrice', 'Pipart')
addPilot('Jeremy', 'Ponchel')
addPilot('Valentino', 'Rossi')
addPilot('Marc', 'Marquez')
addPilot('Dani', 'Pedrosa')
addPilot('Jorge', 'Lorenzo')

print getPilots()
print getBeacons()

associatePilotBeacon(getPilot('Jerome', 'Rousseau')['id'],getBeacon(12)['id'])

associatePilotBeacon(getPilot('Fabrice', 'Pipart')['id'],getBeacon(2)['id'])
associatePilotBeacon(getPilot('Jeremy', 'Ponchel')['id'],getBeacon(12)['id'])

associatePilotBeacon(getPilot('Valentino', 'Rossi')['id'],getBeacon(4)['id'])
associatePilotBeacon(getPilot('Jorge', 'Lorenzo')['id'],getBeacon(8)['id'])

deleteBeacon(getBeacon(2)['id'])

print getPilots()
print getBeacons()
