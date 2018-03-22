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