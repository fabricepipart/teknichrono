#!python3

import requests
import json
import sys

from base import *
from pilots import deletePilots
from beacons import deleteBeacons, addBeacon
from chronometer import deleteChronometers
from event import deleteEvents
from category import deleteCategories
from session import deleteSessions
from location import deleteLocations

# Cleanup
deleteBeacons()
deletePilots()
deleteChronometers()
deleteSessions()
deleteEvents()
deleteCategories()
deleteLocations()

# ----------------------------------------------------------------------
# Add Beacons
for i in range(0, 100):
  addBeacon(i)