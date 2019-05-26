#!python3

from api.base import *

LocationsUrl = '/rest/locations'

# ----------------------------------------------------------------------


def addLocation(name, loop=True):
  "This adds a Location"
  data = '{"name":"' + name + '", "loopTrack":"' + str(loop) + '"}'
  post(data, LocationsUrl)
  print("Location " + name + " added")
  locationResponse = getLocationByName(name)
  return locationResponse


def getLocationByName(name):
  "This gets a Location by name and returns a json"
  url = LocationsUrl + '/name?name=' + name
  locationResponse = get(url)
  return locationResponse


def deleteLocation(id):
  "This deletes a Location by id"
  url = LocationsUrl + '/' + str(id)
  delete(url)
  print("Deleted Location id " + str(id))
  return


def getLocations():
  "This gets all Locations"
  LocationResponse = get(LocationsUrl)
  return LocationResponse


def deleteLocations():
  "Deletes all Locations"
  Locations = getLocations()
  for Location in Locations:
    deleteLocation(Location['id'])
  return


def addSessionToLocation(locationId, sessionId):
  "Associate Location and Session"
  url = LocationsUrl + '/' + str(locationId) + '/addSession?sessionId=' + str(sessionId)
  post('', url)
  print("Associate Location id " + str(locationId) + " and session id " + str(sessionId))
  return


# ----------------------------------------------------------------------
