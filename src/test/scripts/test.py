#!/usr/bin/python

import requests
import json

headers = {'Content-type': 'application/json'}
beaconsUrl = 'http://localhost:8080/teknichrono/rest/beacons'
pilotsUrl = 'http://localhost:8080/teknichrono/rest/pilots'


#response = requests.get(url, data=data)


def addPilot( firstName, lastName ):
  "This adds a Pilot"
  data = '{"firstName":"'+ firstName+'", "lastName":"'+lastName+'"}'
  pilotResponse = post(data, pilotsUrl);
  print "Pilot " + firstName + ' / '+lastName+" added"
  return;


def getPilot( firstName, lastName ):
  "This adds a Pilot"
  url = pilotsUrl + '/name?firstname=' + firstName + '&lastname=' + lastName
  pilotResponse = get(url);
  return pilotResponse;



def getPilots( ):
    "This gets all Pilots"
    pilotResponse = get(pilotsUrl);
    return pilotResponse;

def deletePilot( id ):
  "This deletes a Pilot by id"
  url = pilotsUrl + '/' + str(id)
  delete(url);
  print "Deleted pilot id " + str(id)
  return;

def deletePilots():
  "Deletes all Beacons"
  pilots = getPilots();
  for pilot in pilots:
    deletePilot(pilot['id'])
  return;

def associatePilotBeacon(pilot, beacon):
    "Associate Pilot and Beacon"
    url = pilotsUrl + '/' + str(pilot['id'])
    pilot['currentBeacon'] = beacon['id']
    data = json.dumps(pilot)
    put(data, url);
    print "Associate pilot id " + str(pilot['id']) + " and beacon number " + str(beacon['number'])
    return;


# ----------------------------------------------------------------------

def addBeacon( number ):
  "This adds a Beacon"
  data = '{"number":' + str(number) + '}'
  beaconResponse = post(data, beaconsUrl);
  print "Beacon " + str(number) + " added"
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
  print "Deleted beacon id " + str(id)
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

def post( dataString, url ):
  "This posts a json to a URL and returns a json"
  response = requests.post(url, data=dataString, headers=headers)
  if(not response.ok):
    response.raise_for_status();
  return;

def put( dataString, url ):
    "This send in a PUT a json to a URL"
    response = requests.put(url, data=dataString, headers=headers)
    if(not response.ok):
      response.raise_for_status();
    return;

def delete(url ):
  "This sends a DELETE to a URL"
  response = requests.delete(url, headers=headers)
  if(not response.ok):
    response.raise_for_status();
  return;

def get(url ):
  "This sends a get to a URL and returns a json"
  response = requests.get(url, headers=headers)
  if(not response.ok):
    response.raise_for_status();
  jData = json.loads(response.content)
  return jData;

# ----------------------------------------------------------------------

# Cleanup
print getBeacons()
deleteBeacons()
print getBeacons()

print getPilots()
deletePilots()
print getPilots()



# Add Beacons
for i in range(1, 20):
    addBeacon(i);

addPilot('Jerome', 'Rousseau')
addPilot('Fabrice', 'Pipart')
addPilot('Jeremy', 'Ponchel')
addPilot('Valentino', 'Rossi')
addPilot('Marc', 'Marquez')
addPilot('Dani', 'Pedrosa')
addPilot('Jorge', 'Lorenzo')

print getPilots()

associatePilotBeacon(getPilot('Jerome', 'Rousseau'),getBeacon(12))
associatePilotBeacon(getPilot('Fabrice', 'Pipart'),getBeacon(2))
associatePilotBeacon(getPilot('Jeremy', 'Ponchel'),getBeacon(12))
associatePilotBeacon(getPilot('Valentino', 'Rossi'),getBeacon(4))
associatePilotBeacon(getPilot('Jorge', 'Lorenzo'),getBeacon(8))
