#!/usr/bin/python

from base import *

beaconsUrl = '/teknichrono/rest/beacons'

# ----------------------------------------------------------------------

def addBeacon( number ):
  "This adds a Beacon"
  data = '{"number":' + str(number) + '}'
  beaconResponse = post(data, beaconsUrl);
  print("Beacon " + str(number) + " added")
  return;

def getBeacon( number ):
  "This gets a Beacon by Number and returns a json"
  url = beaconsUrl + '/number/' + str(number)
  beaconResponse = get(url);
  return beaconResponse;

def deleteBeacon( id ):
  "This deletes a Beacon by id"
  url = beaconsUrl + '/' + str(id)
  delete(url);
  print("Deleted beacon id " + str(id))
  return;

def getBeacons():
  "This gets all Beacons"
  beaconResponse = get(beaconsUrl);
  return beaconResponse;

def deleteBeacons():
  "Deletes all Beacons"
  beacons = getBeacons();
  for beacon in beacons:
    deleteBeacon(beacon['id'])
  return;

# ----------------------------------------------------------------------
