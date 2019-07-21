#!python3

from api.base import *
from prettytable import PrettyTable

lapsUrl = '/rest/laptimes'

# ----------------------------------------------------------------------


def getBestLapsOfPilot(pilotId, sessionId=None, locationId=None, eventId=None):
  return getLapsOfPilot(pilotId, sessionId, locationId, eventId, '/best')


def getRaceLapsOfPilot(pilotId, sessionId=None, locationId=None, eventId=None):
  return getLapsOfPilot(pilotId, sessionId, locationId, eventId, '/results')


def getLapsOfPilot(pilotId, sessionId=None, locationId=None, eventId=None, option=''):
  "This gets the laps of a given pilot and returns a json"
  sessionIdOption = ''
  if sessionId:
    sessionIdOption = '&sessionId=' + str(sessionId)
  locationIdOption = ''
  if locationId:
    locationIdOption = '&locationId=' + str(locationId)
  eventIdOption = ''
  if eventId:
    eventIdOption = '&eventId=' + str(eventId)
  url = lapsUrl + option + '?pilotId=' + str(pilotId) + str(sessionIdOption) + str(locationIdOption) + str(eventIdOption)
  lapsResponse = get(url)
  return lapsResponse


def getBestLaps(locationId=None, eventId=None, categoryId=None, option=''):
  return getLaps(locationId, eventId, categoryId, '/best')


def getRaceLaps(locationId=None, eventId=None, categoryId=None, option=''):
  return getLaps(locationId, eventId, categoryId, '/results')


def getLaps(locationId=None, eventId=None, categoryId=None, option=''):
  "This gets all Laps"
  locationIdOption = ''
  if locationId:
    locationIdOption = '&locationId=' + str(locationId)
  eventIdOption = ''
  if eventId:
    eventIdOption = '&eventId=' + str(eventId)
  categoryIdOption = ''
  if categoryId:
    categoryIdOption = '&categoryId=' + str(categoryId)
  url = lapsUrl + option
  if locationId or eventIdOption:
    url = url + '?' + str(locationIdOption) + str(eventIdOption) + str(categoryIdOption)
  lapsResponse = get(url)
  printLaps(lapsResponse, True)
  return lapsResponse


def getBestLapsForSession(sessionId, categoryId=None, option=''):
  laps = getLapsForSession(sessionId, categoryId, '/best')
  return laps


def getResultsForSession(sessionId, categoryId=None, option=''):
  laps = getLapsForSession(sessionId, categoryId, '/results')
  return laps


def getLapsForSession(sessionId, categoryId=None, option=''):
  "This gets the laps of a given session and returns a json"
  categoryIdOption = ''
  if categoryId:
    categoryIdOption = '&categoryId=' + str(categoryId)
  url = lapsUrl + option + '?sessionId=' + str(sessionId) + str(categoryIdOption)
  lapsResponse = get(url)
  printLaps(lapsResponse, True)
  return lapsResponse


def printLaps(laps, withDates=False):
  maxSectors = 0
  for lap in laps:
    maxSectors = max(maxSectors, len(lap['intermediates']))
  #print("Max Sectors = "+ str(maxSectors))
  headers = ['']
  headers.append('#')
  if withDates:
    headers.append('Start')
    headers.append('End')
  headers.append('Pilot')
  headers.append('Lap')
  headers.append('Lap time')
  if maxSectors > 1:
    for i in range(1, maxSectors + 1):
      headers.append("Sector " + str(i))
  headers.append('∆ Best')
  headers.append('∆ Prev')
  #print(str(headers))
  #print("#Laps : " + str(len(laps)))
  rowIndex = 1
  table = PrettyTable(headers)
  for lap in laps:
    #print("Raw Lap : " + str(lap))
    #lapId = str(lap['id'])
    startDate = ''
    if 'startDate' in lap:
      startDateValue = lap['startDate']
      if startDateValue:
        startDate = startDateValue
    
    endDate = ''
    if 'endDate' in lap:
      endDateValue = lap['endDate']
      if endDateValue:
        endDate = endDateValue

    pilot = str(lap['pilot']['firstName']) + ' ' + str(lap['pilot']['lastName'])
    lapIndex = str(lap['lapIndex']) + '/' + str(lap['lapNumber'])
    
    lapTime = '-'
    if 'duration' in lap:
      lapTime = pretty_time_delta_iso(lap['duration'])
    lapRow = [str(rowIndex)]
    lapRow.append(str(lap['pilot']['beaconNumber']))
    if withDates:
      lapRow.append(startDate)
      lapRow.append(endDate)
    lapRow.append(pilot)
    lapRow.append(lapIndex)
    lapRow.append(lapTime)
    intermediateIndex = 0
    intermediates = lap['intermediates']
    if maxSectors > 1:
      for i in range(0, maxSectors):
        if intermediateIndex >= len(intermediates):
          lapRow.append('')
        else:
          intermediate = intermediates[intermediateIndex]
          if intermediate['fromChronoId'] != i:
            lapRow.append('')
          else:
            lapRow.append(pretty_time_delta_iso(intermediate['duration']))
            intermediateIndex += 1
    if 'gapWithBest' in lap:
      lapRow.append(pretty_time_delta_iso(lap['gapWithBest']))
    else:
      lapRow.append('-')
    if 'gapWithPrevious' in lap:
      lapRow.append(pretty_time_delta_iso(lap['gapWithPrevious']))
    else:
      lapRow.append('-')
    table.add_row(lapRow)
    rowIndex += +1
  print(table)


# ----------------------------------------------------------------------
