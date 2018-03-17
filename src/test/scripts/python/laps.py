#!python3

import datetime
from base import *
from prettytable import PrettyTable

lapsUrl = '/teknichrono/rest/laptimes'

# ----------------------------------------------------------------------


def getLapsOfPilot(pilotId, sessionId=None, locationId=None, eventId=None):
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
  url = lapsUrl + '?pilotId=' + str(pilotId) + str(sessionIdOption) + str(locationIdOption) + str(eventIdOption)
  lapsResponse = get(url)
  return lapsResponse


def getLaps(locationId=None, eventId=None, categoryId=None):
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
  url = lapsUrl
  if locationId or eventIdOption:
    url = url + '?' + str(locationIdOption) + str(eventIdOption) + str(categoryIdOption)
  lapsResponse = get(url)
  return lapsResponse


def getLapsForSession(sessionId, categoryId=None):
  "This gets the laps of a given session and returns a json"
  categoryIdOption = ''
  if categoryId:
    categoryIdOption = '&categoryId=' + str(categoryId)
  url = lapsUrl + '?sessionId=' + str(sessionId) + str(categoryIdOption)
  lapsResponse = get(url)
  return lapsResponse


def printLaps(laps, withDates=False):
  maxSectors = 0
  for lap in laps:
    maxSectors = max(maxSectors, len(lap['intermediates']))
  #print("Max Sectors = "+ str(maxSectors))
  if withDates:
    headers = ['Lap', 'Date', 'Pilot', 'Lap time']
  else:
    headers = ['Lap', 'Pilot', 'Lap time']
  for i in range(1, maxSectors + 1):
    headers.append("Sector " + str(i))
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
    lapTime = pretty_time_delta(lap['duration'])
    if withDates:
      lapRow = [lapId, startDate, pilot, lapTime]
    else:
      lapRow = [lapId, pilot, lapTime]
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
    table.add_row(lapRow)
  print(table)


# ----------------------------------------------------------------------
