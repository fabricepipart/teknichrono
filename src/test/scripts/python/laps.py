#!python3

from base import *
from prettytable import PrettyTable

lapsUrl = '/rest/laptimes'

# ----------------------------------------------------------------------


def getBestLapsOfPilot(pilotId, sessionId=None, locationId=None, eventId=None):
  return getLapsOfPilot(pilotId, sessionId, locationId, eventId, '/best')


def getRaceLapsOfPilot(pilotId, sessionId=None, locationId=None, eventId=None):
  return getLapsOfPilot(pilotId, sessionId, locationId, eventId, '/race')


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
  return getLaps(locationId, eventId, categoryId, '/race')


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
  return lapsResponse


def getBestLapsForSession(sessionId, categoryId=None, option=''):
  return getLapsForSession(sessionId, categoryId, '/best')


def getRaceLapsForSession(sessionId, categoryId=None, option=''):
  return getLapsForSession(sessionId, categoryId, '/race')


def getLapsForSession(sessionId, categoryId=None, option=''):
  "This gets the laps of a given session and returns a json"
  categoryIdOption = ''
  if categoryId:
    categoryIdOption = '&categoryId=' + str(categoryId)
  url = lapsUrl + option + '?sessionId=' + str(sessionId) + str(categoryIdOption)
  lapsResponse = get(url)
  return lapsResponse


def printLaps(laps, withDates=False):
  maxSectors = 0
  for lap in laps:
    maxSectors = max(maxSectors, len(lap['intermediates']))
  #print("Max Sectors = "+ str(maxSectors))
  headers = ['Lap']
  if withDates:
    headers.append('Date')
  headers.append('Pilot')
  headers.append('Lap index')
  headers.append('Lap time')
  for i in range(1, maxSectors + 1):
    headers.append("Sector " + str(i))
  headers.append('Gap with Best')
  headers.append('Gap with Previous')
  #print(str(headers))
  #print("#Laps : " + str(len(laps)))
  table = PrettyTable(headers)
  for lap in laps:
    #print("Raw Lap : " + str(lap))
    lapId = str(lap['id'])
    startDateValue = lap['startDate']
    if startDateValue:
      startDate = timestampToDate(lap['startDate'])
    else:
      startDate = ''
    pilot = str(lap['pilot']['firstName']) + ' ' + str(lap['pilot']['lastName'])
    lapIndex = str(lap['lapIndex']) + ' / ' + str(lap['lapNumber'])
    lapTime = pretty_time_delta(lap['duration'])
    if withDates:
      lapRow = [lapId, startDate, pilot, lapIndex, lapTime]
    else:
      lapRow = [lapId, pilot, lapIndex, lapTime]
    intermediateIndex = 0
    intermediates = lap['intermediates']
    for i in range(0, maxSectors):
      if intermediateIndex >= len(intermediates):
        lapRow.append('')
      else:
        intermediate = intermediates[intermediateIndex]
        if intermediate['fromChronoId'] != i:
          lapRow.append('')
        else:
          lapRow.append(pretty_time_delta(intermediate['duration']))
          intermediateIndex += 1
    lapRow.append(pretty_time_delta(lap['gapWithBest']))
    lapRow.append(pretty_time_delta(lap['gapWithPrevious']))
    table.add_row(lapRow)
  print(table)


# ----------------------------------------------------------------------
