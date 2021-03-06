#!python3

import requests
import json
import sys

from api.base import *
from api.pilots import deletePilots
from api.beacons import deleteBeacons, addBeacon
from api.chronometer import deleteChronometers
from api.event import deleteEvents
from api.category import deleteCategories
from api.session import deleteSessions
from api.location import deleteLocations

# Cleanup
deleteBeacons()
deletePilots()
deleteChronometers()
deleteSessions()
deleteEvents()
deleteCategories()
deleteLocations()
