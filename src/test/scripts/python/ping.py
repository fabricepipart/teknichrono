#!python3

from base import *
import random

headers = {'Content-type': 'application/json'}
pingsUrl = '/rest/pings'


# ----------------------------------------------------------------------
def ping(dateTime, pilotBeaconId, power, chronoId):
  "This adds a Ping"
  data = '{"dateTime":"' + formatDatetime(dateTime) + '", "power":"' + str(power) + '"}'
  url = pingsUrl + '/create?chronoId=' + str(chronoId) + '&beaconId=' + str(pilotBeaconId)
  #pingResponse = post(data, url)
  post(data, url)
  #print("Ping " + formatDatetime(dateTime) + " added " + data)
  return


def pingsForLap(baseDateTime, pilotTimePerSector, pilotBeaconId, chrono0, chrono1, chrono2, chrono3):
  print("Create pings for lap @ " + formatDatetime(baseDateTime) + " for pilot " + str(pilotBeaconId))
  pilotTimePerSector = (pilotTimePerSector * 1000) + (10 * random.random())
  ping(baseDateTime + timedelta(milliseconds=pilotTimePerSector * 0), pilotBeaconId, -83, chrono0)
  ping(baseDateTime + timedelta(milliseconds=(pilotTimePerSector * 1)), pilotBeaconId, -83, chrono1)
  ping(baseDateTime + timedelta(milliseconds=(pilotTimePerSector * 2)), pilotBeaconId, -83, chrono2)
  ping(baseDateTime + timedelta(milliseconds=(pilotTimePerSector * 3)), pilotBeaconId, -83, chrono3)
  return baseDateTime + timedelta(milliseconds=(pilotTimePerSector * 4))
