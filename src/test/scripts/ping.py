#!/usr/bin/python

from base import *
import random

headers = {'Content-type': 'application/json'}
pingsUrl = '/teknichrono/rest/pings'

# ----------------------------------------------------------------------
def ping( dateTime, pilotBeaconId, power, chronoId  ):
  "This adds a Ping"
  data = '{"dateTime":"' + formatDatetime(dateTime) + '", "power":"' + str(power) + '"}'
  url = pingsUrl + '/create?chronoId=' + str(chronoId) + '&beaconId=' + str(pilotBeaconId)
  pingResponse = post(data, url);
  #print "Ping " + formatDatetime(dateTime) + " added"
  return;

def pingsForLap( baseDateTime, pilotTimePerSector, pilotBeaconId, chrono0, chrono1, chrono2, chrono3 ):
    print "Create pings for lap @ " + formatDatetime(baseDateTime) + " for pilot " + str(pilotBeaconId)
    pilotTimePerSector = (pilotTimePerSector * 1000) + (10 * random.random())
    ping(baseDateTime + datetime.timedelta(milliseconds=pilotTimePerSector), pilotBeaconId, -83, chrono0)
    ping(baseDateTime + datetime.timedelta(milliseconds=(pilotTimePerSector*2)), pilotBeaconId, -83, chrono1)
    ping(baseDateTime + datetime.timedelta(milliseconds=(pilotTimePerSector*3)), pilotBeaconId, -83, chrono2)
    ping(baseDateTime + datetime.timedelta(milliseconds=(pilotTimePerSector*4)), pilotBeaconId, -83, chrono3)
    return baseDateTime + datetime.timedelta(milliseconds=(pilotTimePerSector*4));
