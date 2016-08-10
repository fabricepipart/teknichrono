#!/usr/bin/python

from base import *

lapsUrl = '/teknichrono/rest/laptimes'

# ----------------------------------------------------------------------

def getLapsOfPilot( pilotId ):
  "This gets the laps of a given pilot and returns a json"
  url = lapsUrl + '?pilotId=' + str(pilotId)
  lapsResponse = get(url);
  return lapsResponse;

def getLaps():
  "This gets all Laps"
  lapsResponse = get(lapsUrl);
  return lapsResponse;

def printLaps(laps):
  print "#Laps : " + str(len(laps))
  for lap in laps:
    print "Laps #" + str(lap['id']) + " with intermediates #" + str(len(lap['intermediates']))
    for intermediate in lap['intermediates']:
      print "    Intermediate #" + str(intermediate['id']) + " @ " + formatDatetime(timestampToDate(intermediate['dateTime']))

# ----------------------------------------------------------------------
