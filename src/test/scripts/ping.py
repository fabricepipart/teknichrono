#!/usr/bin/python

from base import *
import variables

headers = {'Content-type': 'application/json'}
pingsUrl = '/teknichrono/rest/pings'

# ----------------------------------------------------------------------
def ping( dateTime, pilotBeaconId, power, chronoId  ):
  "This adds a Ping"
  data = '{"dateTime":"' + formatDatetime(dateTime) + '", "power":"' + str(power) + '"}'
  url = pingsUrl + '/' + str(chronoId) + '/' + str(pilotBeaconId)
  pingResponse = post(data, url);
  #print "Ping " + formatDatetime(dateTime) + " added"
  return;

def pingsForLap( baseDateTime, pilotTimePerSector, pilotBeaconId, chrono0, chrono1, chrono2, chrono3 ):
    print "Create pings for lap @ " + formatDatetime(baseDateTime) + " for pilot " + str(pilotBeaconId)
    ping(baseDateTime + datetime.timedelta(seconds=pilotTimePerSector), pilotBeaconId, -83, chrono1)
    ping(baseDateTime + datetime.timedelta(seconds=(pilotTimePerSector*2)), pilotBeaconId, -83, chrono2)
    ping(baseDateTime + datetime.timedelta(seconds=(pilotTimePerSector*3)), pilotBeaconId, -83, chrono3)
    ping(baseDateTime + datetime.timedelta(seconds=(pilotTimePerSector*4)), pilotBeaconId, -83, chrono0)
    return baseDateTime + datetime.timedelta(seconds=(pilotTimePerSector*4));
