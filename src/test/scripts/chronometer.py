#!/usr/bin/python

from base import *

ChronometersUrl = 'http://localhost:8080/teknichrono/rest/chronometers'

# ----------------------------------------------------------------------

def addChronometer( name, index ):
  "This adds a Chronometer"
  data = '{"name":"' + name + '", "index":' + str(index) + '}'
  ChronometerResponse = post(data, ChronometersUrl);
  print "Chronometer " + str(index) + " added"
  return;

def deleteChronometer( id ):
  "This deletes a Chronometer by id"
  url = ChronometersUrl + '/' + str(id)
  delete(url);
  print "Deleted Chronometer id " + str(id)
  return;

def getChronometers():
  "This gets all Chronometers"
  ChronometerResponse = get(ChronometersUrl);
  return ChronometerResponse;

def deleteChronometers():
  "Deletes all Chronometers"
  Chronometers = getChronometers();
  for Chronometer in Chronometers:
    deleteChronometer(Chronometer['id'])
  return;

# ----------------------------------------------------------------------
