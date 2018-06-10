#!python3

from api.base import *

pilotsUrl = '/rest/pilots'


def addPilot(firstName, lastName):
  "This adds a Pilot"
  data = '{"firstName":"' + firstName + '", "lastName":"' + lastName + '"}'
  post(data, pilotsUrl)
  print("Pilot " + firstName + ' / ' + lastName + " added")
  pilotResponse = getPilot(firstName, lastName)
  return pilotResponse


def getPilot(firstName, lastName):
  "This adds a Pilot"
  url = pilotsUrl + '/name?firstname=' + firstName + '&lastname=' + lastName
  pilotResponse = get(url)
  return pilotResponse


def getPilots():
  "This gets all Pilots"
  pilotResponse = get(pilotsUrl)
  return pilotResponse


def deletePilot(id):
  "This deletes a Pilot by id"
  url = pilotsUrl + '/' + str(id)
  delete(url)
  print("Deleted pilot id " + str(id))
  return


def deletePilots():
  "Deletes all Beacons"
  pilots = getPilots()
  for pilot in pilots:
    deletePilot(pilot['id'])
  return


def associatePilotBeacon(pilotId, beaconId):
  "Associate Pilot and Beacon"
  url = pilotsUrl + '/' + str(pilotId) + '/setBeacon?beaconId=' + str(beaconId)
  post('', url)
  print("Associate pilot id " + str(pilotId) + " and beacon id " + str(beaconId))
  return
