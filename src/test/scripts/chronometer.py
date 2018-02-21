#!/usr/bin/python

from base import *

ChronometersUrl = '/teknichrono/rest/chronometers'

# ----------------------------------------------------------------------

def addChronometer( name ):
  "This adds a Chronometer"
  data = '{"name":"' + name + '" }'
  ChronometerResponse = post(data, ChronometersUrl);
  print("Chronometer " + name + " added")
  return;

def getChronometerByName( name ):
  "This gets a Chronometer by name and returns a json"
  url = ChronometersUrl + '/name?name=' + name
  ChronometerResponse = get(url);
  return ChronometerResponse;

def deleteChronometer( id ):
  "This deletes a Chronometer by id"
  url = ChronometersUrl + '/' + str(id)
  delete(url);
  print("Deleted Chronometer id " + str(id))
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
