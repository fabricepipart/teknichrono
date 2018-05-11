#!python3

import requests
import json
import sys
from datetime import date, datetime, timedelta

#from base import *
from random import randint
from pilots import addPilot, getPilot, associatePilotBeacon
from beacons import getBeacon
from chronometer import addChronometer
from event import addEvent, addSessionToEvent
from ping import ping
from laps import printLaps, getLapsForSession, getBestLapsForSession, getResultsForSession
from session import addSession, addChronometerToSession, startSession, addPilotToSession, endSession
from location import addLocation, addSessionToLocation
from category import addCategory, addPilotToCategory
from check import checkNumberLaps, checkPilotFilled, checkCountWithLapIndex, checkCountWithLapNumber, checkLaptimeFilled, checkDeltaBestInIncreasingOrder, checkDeltaPreviousFilled, checkStartsOrdered, checkEndsOrdered, checkLaptimeBetween

print("-------------------------------------")
print("Pre-event")
print("-------------------------------------")

# Add Events
event = addEvent('Snowscoot championship')

# Add Categories
# ELITE (30) OPEN HOMME (20) F2MININES (20) JUNIOR (10)

eliteCategory = addCategory('Elite')
openCategory = addCategory('Open')
womanCategory = addCategory('Woman')
juniorCategory = addCategory('Junior')

allPilots = []
elitePilots = []
openPilots = []
womanPilots = []
juniorPilots = []

# Add Pilots
for i in range(10, 40):
  pilot = addPilot('Rider ' + str(i), 'Elite')
  elitePilots.append(pilot)
  addPilotToCategory(eliteCategory['id'], pilot['id'])
for i in range(40, 60):
  pilot = addPilot('Rider ' + str(i), 'Open')
  openPilots.append(pilot)
  addPilotToCategory(openCategory['id'], pilot['id'])
for i in range(60, 80):
  pilot = addPilot('Rider ' + str(i), 'Woman')
  womanPilots.append(pilot)
  addPilotToCategory(womanCategory['id'], pilot['id'])
for i in range(80, 90):
  pilot = addPilot('Rider ' + str(i), 'Junior')
  juniorPilots.append(pilot)
  addPilotToCategory(juniorCategory['id'], pilot['id'])

allPilots = elitePilots + openPilots + womanPilots + juniorPilots

# Add Chronometers

chrono = addChronometer('Raspberry')
fake1 = addChronometer('Fake1')
fake2 = addChronometer('Fake2')

# Add Locations
boarderCross = addLocation('Isola - Boarder cross', False)
mercantour = addLocation('Isola - Mercantour', False)
dual = addLocation('Isola - Dual', False)
valette = addLocation('Isola - Valette', False)
redRiver = addLocation('Isola - Red river', False)
roubines = addLocation('Isola - Roubines', False)

# Add sessions

print("---- Create session of Friday morning ----")

friMorningTestSession = addSession('Friday morning tests', datetime(2000, 1, 1, 10), datetime(2000, 1, 1, 11), 'tt')
addSessionToLocation(boarderCross['id'], friMorningTestSession['id'])
addSessionToEvent(event['id'], friMorningTestSession['id'])
addChronometerToSession(friMorningTestSession['id'], fake1['id'])
addChronometerToSession(friMorningTestSession['id'], chrono['id'])
for pilot in allPilots:
  addPilotToSession(friMorningTestSession['id'], pilot['id'])

friMorningChronoSession = addSession('Friday morning Chrono', datetime(2000, 1, 1, 11), datetime(2000, 1, 1, 12), 'tt')
addSessionToLocation(boarderCross['id'], friMorningChronoSession['id'])
addSessionToEvent(event['id'], friMorningChronoSession['id'])
addChronometerToSession(friMorningChronoSession['id'], fake1['id'])
addChronometerToSession(friMorningChronoSession['id'], chrono['id'])
for pilot in allPilots:
  addPilotToSession(friMorningChronoSession['id'], pilot['id'])

print("---- Create session of Friday afternoon ----")

friPm16Run1 = addSession('Fri pm Boarder X 1/16 #1', datetime(2000, 1, 1, 14, 0), datetime(2000, 1, 1, 14, 4), 'rc')
friPm16Run2 = addSession('Fri pm Boarder X 1/16 #2', datetime(2000, 1, 1, 14, 4), datetime(2000, 1, 1, 14, 8), 'rc')
friPm16Run3 = addSession('Fri pm Boarder X 1/16 #3', datetime(2000, 1, 1, 14, 8), datetime(2000, 1, 1, 14, 12), 'rc')
friPm16Run4 = addSession('Fri pm Boarder X 1/16 #4', datetime(2000, 1, 1, 14, 12), datetime(2000, 1, 1, 14, 16), 'rc')
friPm16Run5 = addSession('Fri pm Boarder X 1/16 #5', datetime(2000, 1, 1, 14, 16), datetime(2000, 1, 1, 14, 20), 'rc')
friPm16Run6 = addSession('Fri pm Boarder X 1/16 #6', datetime(2000, 1, 1, 14, 20), datetime(2000, 1, 1, 14, 24), 'rc')
friPm16Run7 = addSession('Fri pm Boarder X 1/16 #7', datetime(2000, 1, 1, 14, 24), datetime(2000, 1, 1, 14, 28), 'rc')
friPm16Run8 = addSession('Fri pm Boarder X 1/16 #8', datetime(2000, 1, 1, 14, 28), datetime(2000, 1, 1, 14, 32), 'rc')
friPm16Run9 = addSession('Fri pm Boarder X 1/16 #9', datetime(2000, 1, 1, 14, 32), datetime(2000, 1, 1, 14, 36), 'rc')
friPm16Run10 = addSession('Fri pm Boarder X 1/16 #10', datetime(2000, 1, 1, 14, 36), datetime(2000, 1, 1, 14, 40), 'rc')
friPm16Run11 = addSession('Fri pm Boarder X 1/16 #11', datetime(2000, 1, 1, 14, 40), datetime(2000, 1, 1, 14, 44), 'rc')
friPm16Run12 = addSession('Fri pm Boarder X 1/16 #12', datetime(2000, 1, 1, 14, 44), datetime(2000, 1, 1, 14, 48), 'rc')
friPm16Run13 = addSession('Fri pm Boarder X 1/16 #13', datetime(2000, 1, 1, 14, 48), datetime(2000, 1, 1, 14, 52), 'rc')
friPm16Run14 = addSession('Fri pm Boarder X 1/16 #14', datetime(2000, 1, 1, 14, 52), datetime(2000, 1, 1, 14, 56), 'rc')
friPm16Run15 = addSession('Fri pm Boarder X 1/16 #15', datetime(2000, 1, 1, 14, 56), datetime(2000, 1, 1, 15, 0), 'rc')
friPm16Run16 = addSession('Fri pm Boarder X 1/16 #16', datetime(2000, 1, 1, 15, 0), datetime(2000, 1, 1, 15, 4), 'rc')
friPm16Sessions = [
    friPm16Run1, friPm16Run2, friPm16Run3, friPm16Run4, friPm16Run5, friPm16Run6, friPm16Run7, friPm16Run8, friPm16Run9,
    friPm16Run10, friPm16Run11, friPm16Run12, friPm16Run13, friPm16Run14, friPm16Run15, friPm16Run16
]
for s in friPm16Sessions:
  addSessionToLocation(boarderCross['id'], s['id'])
  addSessionToEvent(event['id'], s['id'])
  addChronometerToSession(s['id'], fake1['id'])
  addChronometerToSession(s['id'], chrono['id'])

friPm8Run1 = addSession('Fri pm Boarder X 1/8 #1', datetime(2000, 1, 1, 15, 16), datetime(2000, 1, 1, 15, 20), 'rc')
friPm8Run2 = addSession('Fri pm Boarder X 1/8 #2', datetime(2000, 1, 1, 15, 20), datetime(2000, 1, 1, 15, 24), 'rc')
friPm8Run3 = addSession('Fri pm Boarder X 1/8 #3', datetime(2000, 1, 1, 15, 24), datetime(2000, 1, 1, 15, 28), 'rc')
friPm8Run4 = addSession('Fri pm Boarder X 1/8 #4', datetime(2000, 1, 1, 15, 28), datetime(2000, 1, 1, 15, 32), 'rc')
friPm8Run5 = addSession('Fri pm Boarder X 1/8 #5', datetime(2000, 1, 1, 15, 32), datetime(2000, 1, 1, 15, 36), 'rc')
friPm8Run6 = addSession('Fri pm Boarder X 1/8 #6', datetime(2000, 1, 1, 15, 36), datetime(2000, 1, 1, 15, 40), 'rc')
friPm8Run7 = addSession('Fri pm Boarder X 1/8 #7', datetime(2000, 1, 1, 15, 40), datetime(2000, 1, 1, 15, 44), 'rc')
friPm8Run8 = addSession('Fri pm Boarder X 1/8 #8', datetime(2000, 1, 1, 15, 44), datetime(2000, 1, 1, 15, 48), 'rc')
friPm8Sessions = [friPm8Run1, friPm8Run2, friPm8Run3, friPm8Run4, friPm8Run5, friPm8Run6, friPm8Run7, friPm8Run8]
for s in friPm8Sessions:
  addSessionToLocation(boarderCross['id'], s['id'])
  addSessionToEvent(event['id'], s['id'])
  addChronometerToSession(s['id'], fake1['id'])
  addChronometerToSession(s['id'], chrono['id'])

friPm4Run1 = addSession('Fri pm Boarder X 1/4 #1', datetime(2000, 1, 1, 16, 0), datetime(2000, 1, 1, 16, 4), 'rc')
friPm4Run2 = addSession('Fri pm Boarder X 1/4 #2', datetime(2000, 1, 1, 16, 4), datetime(2000, 1, 1, 16, 8), 'rc')
friPm4Run3 = addSession('Fri pm Boarder X 1/4 #3', datetime(2000, 1, 1, 16, 8), datetime(2000, 1, 1, 16, 12), 'rc')
friPm4Run4 = addSession('Fri pm Boarder X 1/4 #4', datetime(2000, 1, 1, 16, 12), datetime(2000, 1, 1, 16, 16), 'rc')
friPm4Sessions = [friPm4Run1, friPm4Run2, friPm4Run3, friPm4Run4]
for s in friPm4Sessions:
  addSessionToLocation(boarderCross['id'], s['id'])
  addSessionToEvent(event['id'], s['id'])
  addChronometerToSession(s['id'], fake1['id'])
  addChronometerToSession(s['id'], chrono['id'])

friPmSemiRun1 = addSession('Fri pm Boarder X 1/2 #1', datetime(2000, 1, 1, 16, 30), datetime(2000, 1, 1, 16, 34), 'rc')
friPmSemiRun2 = addSession('Fri pm Boarder X 1/2 #2', datetime(2000, 1, 1, 16, 34), datetime(2000, 1, 1, 16, 38), 'rc')
friPmSemiSessions = [friPmSemiRun1, friPmSemiRun2]
for s in friPmSemiSessions:
  addSessionToLocation(boarderCross['id'], s['id'])
  addSessionToEvent(event['id'], s['id'])
  addChronometerToSession(s['id'], fake1['id'])
  addChronometerToSession(s['id'], chrono['id'])

friPmFinale = addSession('Fri pm Boarder X Finale', datetime(2000, 1, 1, 16, 45), datetime(2000, 1, 1, 17, 0), 'rc')
addSessionToLocation(boarderCross['id'], friPmFinale['id'])
addSessionToEvent(event['id'], friPmFinale['id'])
addChronometerToSession(friPmFinale['id'], fake1['id'])
addChronometerToSession(friPmFinale['id'], chrono['id'])

print("---- Create session of Saturday morning ----")

satDerby1Elite = addSession('Sat Derby 1 - Elite', datetime(2000, 1, 2, 10, 5), datetime(2000, 1, 2, 10, 10), 'rc')
addSessionToLocation(mercantour['id'], satDerby1Elite['id'])
addSessionToEvent(event['id'], satDerby1Elite['id'])
addChronometerToSession(satDerby1Elite['id'], fake1['id'])
addChronometerToSession(satDerby1Elite['id'], chrono['id'])
for pilot in elitePilots:
  addPilotToSession(satDerby1Elite['id'], pilot['id'])

satDerby1Open = addSession('Sat Derby 1 - Open', datetime(2000, 1, 2, 10, 15), datetime(2000, 1, 2, 10, 20), 'rc')
addSessionToLocation(mercantour['id'], satDerby1Open['id'])
addSessionToEvent(event['id'], satDerby1Open['id'])
addChronometerToSession(satDerby1Open['id'], fake1['id'])
addChronometerToSession(satDerby1Open['id'], chrono['id'])
for pilot in openPilots:
  addPilotToSession(satDerby1Open['id'], pilot['id'])

satDerby1Woman = addSession('Sat Derby 1 - Woman', datetime(2000, 1, 2, 10, 25), datetime(2000, 1, 2, 10, 30), 'rc')
addSessionToLocation(mercantour['id'], satDerby1Woman['id'])
addSessionToEvent(event['id'], satDerby1Woman['id'])
addChronometerToSession(satDerby1Woman['id'], fake1['id'])
addChronometerToSession(satDerby1Woman['id'], chrono['id'])
for pilot in womanPilots:
  addPilotToSession(satDerby1Woman['id'], pilot['id'])

satDerby1Junior = addSession('Sat Derby 1 - Junior', datetime(2000, 1, 2, 10, 35), datetime(2000, 1, 2, 10, 40), 'rc')
addSessionToLocation(mercantour['id'], satDerby1Junior['id'])
addSessionToEvent(event['id'], satDerby1Junior['id'])
addChronometerToSession(satDerby1Junior['id'], fake1['id'])
addChronometerToSession(satDerby1Junior['id'], chrono['id'])
for pilot in juniorPilots:
  addPilotToSession(satDerby1Junior['id'], pilot['id'])

print("-------------------------------------")
print("Thursday evening")
print("-------------------------------------")
# jeudi soir	accueil concurrents et distribution transpondeurs

beacons = {}

beaconNumber = 10
for pilot in allPilots:
  beacons[beaconNumber] = getBeacon(beaconNumber)
  associatePilotBeacon(pilot['id'], beacons[beaconNumber]['id'])
  pilot['currentBeacon'] = beacons[beaconNumber]
  beaconNumber += 1

print("-------------------------------------")
print("Friday morning")
print("-------------------------------------")
# descente dans le boarder cross
# Border cross
# deux runs d essais (controle transpondeurs)
# deux runs chronos
# le meilleur retenu

# Created sessions earlier and start it here
startSession(friMorningTestSession['id'], datetime(2000, 1, 1, 10, 0, 30))

print("---- Test #1 ----")
# Starts every 20s
startDelta = 20
# TODO Check if there should be a specific start order
# TODO Check if it is acceptable to start manually each rider. If not afke1 should be a real chrono
# -- Start
startMinute = 1
for i in range(11, 90):
  m, s = divmod(i * startDelta, 60)
  h, m = divmod(startMinute + m, 60)
  ping(datetime(2000, 1, 1, 10 + h, m, s, randint(0, 500000)), beacons[i]['id'], -99, fake1['id'])
# -- End
endMinute = startMinute + 2
for i in range(11, 89):
  delta = int(i / 3) + randint(0, int(i / 3))
  m, s = divmod(i * startDelta + delta, 60)
  h, m = divmod(endMinute + m, 60)
  ping(datetime(2000, 1, 1, 10 + h, m, s, randint(0, 500000)), beacons[i]['id'], -99, chrono['id'])

print("---- Test #2 ----")
# Starts every 20s
# -- Start
startMinute = 31
for i in range(12, 90):
  m, s = divmod(i * startDelta, 60)
  h, m = divmod(startMinute + m, 60)
  ping(datetime(2000, 1, 1, 10 + h, m, s, randint(0, 500000)), beacons[i]['id'], -99, fake1['id'])
# -- End
endMinute = startMinute + 2
for i in range(12, 88):
  delta = int(i / 3) + randint(0, int(i / 3))
  m, s = divmod(i * startDelta + delta, 60)
  h, m = divmod(endMinute + m, 60)
  ping(datetime(2000, 1, 1, 10 + h, m, s, randint(0, 500000)), beacons[i]['id'], -99, chrono['id'])

print("---- Tests Results ----")

#  ---- Results for display ----

# 10 does not do #1 and #2
# 11 does not do #2
# 89 does not finish #1 and #2
# 88 does not finish #2

friMorningTestsLaps = getLapsForSession(friMorningTestSession['id'])
printLaps(friMorningTestsLaps, True)
checkNumberLaps(friMorningTestsLaps, 160 - 6)
checkPilotFilled(friMorningTestsLaps)
checkCountWithLapIndex(friMorningTestsLaps, 1, 78)
checkCountWithLapIndex(friMorningTestsLaps, 2, 76)
checkCountWithLapNumber(friMorningTestsLaps, 1, 2)
checkLaptimeFilled(friMorningTestsLaps)

friMorningTestsBests = getBestLapsForSession(friMorningTestSession['id'])
printLaps(friMorningTestsBests, True)
checkNumberLaps(friMorningTestsBests, 78)
checkPilotFilled(friMorningTestsBests)
checkCountWithLapNumber(friMorningTestsBests, 1, 2)
checkLaptimeFilled(friMorningTestsBests)
checkDeltaBestInIncreasingOrder(friMorningTestsBests)
checkDeltaPreviousFilled(friMorningTestsBests)

# Some do 1 test
# Some dont test
# Some start but dont finish
# Some finish after expected time

#  ---- Determine startup ----

friMorningTestsResults = getResultsForSession(friMorningTestSession['id'])
printLaps(friMorningTestsResults, True)
# TODO Have chart with startup list
# TODO Check if it should count points

checkNumberLaps(friMorningTestsResults, 80)
checkPilotFilled(friMorningTestsResults)
checkCountWithLapIndex(friMorningTestsResults, 0, 2)
checkCountWithLapNumber(friMorningTestsResults, 0, 2)
checkLaptimeFilled(friMorningTestsResults, True)
checkDeltaBestInIncreasingOrder(friMorningTestsResults, True)
checkDeltaPreviousFilled(friMorningTestsResults, True)

beaconsStartOrder = []
for i in reversed(range(30)):
  beaconsStartOrder.append(friMorningTestsResults[i]['pilot']['beaconNumber'])
for i in range(30, 80):
  beaconsStartOrder.append(friMorningTestsResults[i]['pilot']['beaconNumber'])

startSession(friMorningChronoSession['id'], datetime(2000, 1, 1, 11, 10, 00))
print("---- Chrono #1 ----")
# TODO Make start order the one of the previous results
# Starts every 20s
startDelta = 20
# -- Start
startMinute = 11
for i in range(1, 80):
  m, s = divmod(i * startDelta, 60)
  h, m = divmod(startMinute + m, 60)
  beaconId = beacons[beaconsStartOrder[i]]['id']
  ping(datetime(2000, 1, 1, 11 + h, m, s, randint(0, 500000)), beaconId, -99, fake1['id'])
# -- End
endMinute = startMinute + 2
for i in range(1, 79):
  delta = int(i / 3) + randint(0, int(i / 3))
  m, s = divmod(i * startDelta + delta, 60)
  h, m = divmod(endMinute + m, 60)
  beaconId = beacons[beaconsStartOrder[i]]['id']
  ping(datetime(2000, 1, 1, 11 + h, m, s, randint(0, 500000)), beaconId, -99, chrono['id'])

print("---- Chrono #2 ----")
# Starts every 20s
# -- Start
startMinute = 45
for i in range(2, 80):
  m, s = divmod(i * startDelta, 60)
  h, m = divmod(startMinute + m, 60)
  beaconId = beacons[beaconsStartOrder[i]]['id']
  ping(datetime(2000, 1, 1, 11 + h, m, s, randint(0, 500000)), beaconId, -99, fake1['id'])
# -- End
endMinute = startMinute + 2
for i in range(2, 78):
  delta = int(i / 3) + randint(0, int(i / 3))
  m, s = divmod(i * startDelta + delta, 60)
  h, m = divmod(endMinute + m, 60)
  beaconId = beacons[beaconsStartOrder[i]]['id']
  ping(datetime(2000, 1, 1, 11 + h, m, s, randint(0, 500000)), beaconId, -99, chrono['id'])

print("---- Chrono Results ----")

# ---- Results ----
# ---- Checks - Asserts ----
friMorningChronoLaps = getLapsForSession(friMorningChronoSession['id'])
printLaps(friMorningChronoLaps, True)
checkNumberLaps(friMorningChronoLaps, 160 - 6)
checkPilotFilled(friMorningChronoLaps)
checkCountWithLapIndex(friMorningChronoLaps, 1, 78)
checkCountWithLapIndex(friMorningChronoLaps, 2, 76)
checkCountWithLapNumber(friMorningChronoLaps, 1, 2)
checkLaptimeFilled(friMorningChronoLaps)

friMorningChronoBests = getBestLapsForSession(friMorningChronoSession['id'])
printLaps(friMorningChronoBests, True)
checkNumberLaps(friMorningChronoBests, 78)
checkPilotFilled(friMorningChronoBests)
checkCountWithLapNumber(friMorningChronoBests, 1, 2)
checkLaptimeFilled(friMorningChronoBests)
checkDeltaBestInIncreasingOrder(friMorningChronoBests)
checkDeltaPreviousFilled(friMorningChronoBests)

friMorningChronoResults = getResultsForSession(friMorningChronoSession['id'])
printLaps(friMorningChronoResults, True)
checkNumberLaps(friMorningChronoResults, 80)
checkPilotFilled(friMorningChronoResults)
checkCountWithLapIndex(friMorningChronoResults, 0, 2)
checkCountWithLapNumber(friMorningChronoResults, 0, 2)
checkLaptimeFilled(friMorningChronoResults, True)
checkDeltaBestInIncreasingOrder(friMorningChronoResults, True)
checkDeltaPreviousFilled(friMorningChronoResults, True)

# Some do 1 chrono
# Some dont chrono
# Some finish after expected time

# Some do 1 test
# Some dont test
# Some start but dont finish
# Some finish after expected time

# -------------------------------------
#vendredi apres midi	boarder cross	boarder cross	départ à 4 ou 6	?	?	Quel ordre de départ? Comment sont déterminés les groupes?
# -------------------------------------

# -- 1/16 th - 16 x 5
print("---- 1 / 16 th ----")
# Order from results of morning
beaconsPerSession = {}
for i in range(0, 80):
  s = friPm16Sessions[(i % 16)]
  addPilotToSession(s['id'], friMorningChronoResults[i]['pilot']['id'])
  beaconsForSession = beaconsPerSession.get(s['id'])
  if not beaconsForSession:
    beaconsPerSession[s['id']] = [friMorningChronoResults[i]['pilot']['beaconNumber']]
  else:
    beaconsForSession.append(friMorningChronoResults[i]['pilot']['beaconNumber'])

startHour = 14
startMinute = 1
startDelta = 4
sessionIndex = 0
for session in friPm16Sessions:
  # Starts all together
  h, m = divmod((startHour * 60) + startMinute + (sessionIndex * startDelta), 60)
  startSession(session['id'], datetime(2000, 1, 1, h, m, 0))
  # Ends
  beaconsOfSession = beaconsPerSession[session['id']]
  eh, em = divmod((h * 60) + m + 2, 60)
  for beaconNumber in beaconsOfSession:
    es = randint(0, 30)
    # This one falls :)
    if beaconNumber == 32:
      continue
    ping(datetime(2000, 1, 1, eh, em, es, randint(0, 500000)), beacons[beaconNumber]['id'], -99, chrono['id'])
  endSession(session['id'], datetime(2000, 1, 1, eh, em, 59))
  sessionIndex += 1

friPm16SessionsResults = []
for session in friPm16Sessions:
  print("---- Tests Results of " + session['name'] + "----")
  sessionLaps = getLapsForSession(session['id'])
  printLaps(sessionLaps, True)
  # Did not finish
  if 32 in beaconsPerSession[session['id']]:
    checkNumberLaps(sessionLaps, 4)
    checkCountWithLapIndex(sessionLaps, 1, 4)
    checkCountWithLapNumber(sessionLaps, 1, 4)
  else:
    checkNumberLaps(sessionLaps, 5)
    checkCountWithLapIndex(sessionLaps, 1, 5)
    checkCountWithLapNumber(sessionLaps, 1, 5)
  checkPilotFilled(sessionLaps)
  checkLaptimeFilled(sessionLaps)
  checkStartsOrdered(sessionLaps)
  checkEndsOrdered(sessionLaps)

  sessionBests = getBestLapsForSession(session['id'])
  printLaps(sessionBests, True)
  if 32 in beaconsPerSession[session['id']]:
    checkNumberLaps(sessionBests, 4)
    checkCountWithLapIndex(sessionBests, 1, 4)
    checkCountWithLapNumber(sessionBests, 1, 4)
  else:
    checkNumberLaps(sessionBests, 5)
    checkCountWithLapIndex(sessionBests, 1, 5)
    checkCountWithLapNumber(sessionBests, 1, 5)
  checkPilotFilled(sessionBests)
  checkLaptimeFilled(sessionBests)
  checkDeltaBestInIncreasingOrder(sessionBests)
  checkDeltaPreviousFilled(sessionBests)

  sessionResults = getResultsForSession(session['id'])
  friPm16SessionsResults.append(sessionResults)
  printLaps(sessionResults, True)
  if 32 in beaconsPerSession[session['id']]:
    checkCountWithLapIndex(sessionResults, 1, 4)
    checkCountWithLapNumber(sessionResults, 1, 4)
    checkCountWithLapIndex(sessionResults, 0, 1)
    checkCountWithLapNumber(sessionResults, 0, 1)
  else:
    checkCountWithLapIndex(sessionResults, 1, 5)
    checkCountWithLapNumber(sessionResults, 1, 5)
  checkNumberLaps(sessionResults, 5)
  checkPilotFilled(sessionResults)
  checkLaptimeFilled(sessionResults, True)
  checkDeltaBestInIncreasingOrder(sessionResults, True)
  checkDeltaPreviousFilled(sessionResults, True)

# -- 1/8 th - 8 x 6
print("---- 1 / 8 th ----")
# We keep 3 best
# Order from results of 1/16 th
beaconsPerSession = {}
for i in range(0, 8):
  s = friPm8Sessions[i]
  results1 = friPm16SessionsResults[2 * i]
  results2 = friPm16SessionsResults[(2 * i) + 1]
  addPilotToSession(s['id'], results1[0]['pilot']['id'])
  addPilotToSession(s['id'], results1[1]['pilot']['id'])
  addPilotToSession(s['id'], results1[2]['pilot']['id'])
  addPilotToSession(s['id'], results2[0]['pilot']['id'])
  addPilotToSession(s['id'], results2[1]['pilot']['id'])
  addPilotToSession(s['id'], results2[2]['pilot']['id'])
  beaconsPerSession[s['id']] = [
      results1[0]['pilot']['beaconNumber'],
      results1[1]['pilot']['beaconNumber'],
      results1[2]['pilot']['beaconNumber'],
      results2[0]['pilot']['beaconNumber'],
      results2[1]['pilot']['beaconNumber'],
      results2[2]['pilot']['beaconNumber'],
  ]

startHour = 15
startMinute = 16
startDelta = 4
sessionIndex = 0
for session in friPm8Sessions:
  # Starts all together
  h, m = divmod((startHour * 60) + startMinute + (sessionIndex * startDelta), 60)
  startSession(session['id'], datetime(2000, 1, 1, h, m, 5))
  # Ends
  beaconsOfSession = beaconsPerSession[session['id']]
  # This one falls :)
  doesNotFinish = beaconsOfSession[2]
  eh, em = divmod((h * 60) + m + 2, 60)
  for beaconNumber in beaconsOfSession:
    es = randint(0, 30)
    if beaconNumber != doesNotFinish:
      ping(datetime(2000, 1, 1, eh, em, es, randint(0, 500000)), beacons[beaconNumber]['id'], -99, chrono['id'])
  endSession(session['id'], datetime(2000, 1, 1, eh, em, 59))
  sessionIndex += 1

friPm8SessionsResults = []
for session in friPm8Sessions:
  print("---- Tests Results of " + session['name'] + "----")
  sessionLaps = getLapsForSession(session['id'])
  printLaps(sessionLaps, True)
  checkNumberLaps(sessionLaps, 5)
  checkCountWithLapIndex(sessionLaps, 1, 5)
  checkCountWithLapNumber(sessionLaps, 1, 5)
  checkPilotFilled(sessionLaps)
  checkLaptimeFilled(sessionLaps)
  checkStartsOrdered(sessionLaps)
  checkEndsOrdered(sessionLaps)

  sessionBests = getBestLapsForSession(session['id'])
  printLaps(sessionBests, True)
  checkNumberLaps(sessionBests, 5)
  checkCountWithLapIndex(sessionBests, 1, 5)
  checkCountWithLapNumber(sessionBests, 1, 5)
  checkPilotFilled(sessionBests)
  checkLaptimeFilled(sessionBests)
  checkDeltaBestInIncreasingOrder(sessionBests)
  checkDeltaPreviousFilled(sessionBests)

  sessionResults = getResultsForSession(session['id'])
  friPm8SessionsResults.append(sessionResults)
  printLaps(sessionResults, True)
  checkCountWithLapIndex(sessionResults, 1, 5)
  checkCountWithLapNumber(sessionResults, 1, 5)
  checkNumberLaps(sessionResults, 6)
  checkPilotFilled(sessionResults)
  checkLaptimeFilled(sessionResults, True)
  checkDeltaBestInIncreasingOrder(sessionResults, True)
  checkDeltaPreviousFilled(sessionResults, True)

# -- 1/4 th - 4 x 6
print("---- 1 / 4 th ----")
# We keep 3 best
# Order from results of 1/8 th
beaconsPerSession = {}
for i in range(0, 4):
  s = friPm4Sessions[i]
  results1 = friPm8SessionsResults[2 * i]
  results2 = friPm8SessionsResults[(2 * i) + 1]
  addPilotToSession(s['id'], results1[0]['pilot']['id'])
  addPilotToSession(s['id'], results1[1]['pilot']['id'])
  addPilotToSession(s['id'], results1[2]['pilot']['id'])
  addPilotToSession(s['id'], results2[0]['pilot']['id'])
  addPilotToSession(s['id'], results2[1]['pilot']['id'])
  addPilotToSession(s['id'], results2[2]['pilot']['id'])
  beaconsPerSession[s['id']] = [
      results1[0]['pilot']['beaconNumber'],
      results1[1]['pilot']['beaconNumber'],
      results1[2]['pilot']['beaconNumber'],
      results2[0]['pilot']['beaconNumber'],
      results2[1]['pilot']['beaconNumber'],
      results2[2]['pilot']['beaconNumber'],
  ]

startHour = 16
startMinute = 0
startDelta = 4
sessionIndex = 0
for session in friPm4Sessions:
  # Starts all together
  h, m = divmod((startHour * 60) + startMinute + (sessionIndex * startDelta), 60)
  startSession(session['id'], datetime(2000, 1, 1, h, m, 5))
  # Ends
  beaconsOfSession = beaconsPerSession[session['id']]
  # This one falls :)
  doesNotFinish = beaconsOfSession[2]
  eh, em = divmod((h * 60) + m + 2, 60)
  for beaconNumber in beaconsOfSession:
    es = randint(0, 30)
    if beaconNumber != doesNotFinish:
      ping(datetime(2000, 1, 1, eh, em, es, randint(0, 500000)), beacons[beaconNumber]['id'], -99, chrono['id'])
  endSession(session['id'], datetime(2000, 1, 1, eh, em, 59))
  sessionIndex += 1

friPm4SessionsResults = []
for session in friPm4Sessions:
  print("---- Tests Results of " + session['name'] + "----")
  sessionLaps = getLapsForSession(session['id'])
  printLaps(sessionLaps, True)
  checkNumberLaps(sessionLaps, 5)
  checkCountWithLapIndex(sessionLaps, 1, 5)
  checkCountWithLapNumber(sessionLaps, 1, 5)
  checkPilotFilled(sessionLaps)
  checkLaptimeFilled(sessionLaps)
  checkStartsOrdered(sessionLaps)
  checkEndsOrdered(sessionLaps)

  sessionBests = getBestLapsForSession(session['id'])
  printLaps(sessionBests, True)
  checkNumberLaps(sessionBests, 5)
  checkCountWithLapIndex(sessionBests, 1, 5)
  checkCountWithLapNumber(sessionBests, 1, 5)
  checkPilotFilled(sessionBests)
  checkLaptimeFilled(sessionBests)
  checkDeltaBestInIncreasingOrder(sessionBests)
  checkDeltaPreviousFilled(sessionBests)

  sessionResults = getResultsForSession(session['id'])
  friPm4SessionsResults.append(sessionResults)
  printLaps(sessionResults, True)
  checkCountWithLapIndex(sessionResults, 1, 5)
  checkCountWithLapNumber(sessionResults, 1, 5)
  checkNumberLaps(sessionResults, 6)
  checkPilotFilled(sessionResults)
  checkLaptimeFilled(sessionResults, True)
  checkDeltaBestInIncreasingOrder(sessionResults, True)
  checkDeltaPreviousFilled(sessionResults, True)

# -- 1/2 th - 2 x 6
print("---- 1 / 2 th ----")
# We keep 3 best
beaconsPerSession = {}
for i in range(0, 2):
  s = friPmSemiSessions[i]
  results1 = friPm4SessionsResults[2 * i]
  results2 = friPm4SessionsResults[(2 * i) + 1]
  addPilotToSession(s['id'], results1[0]['pilot']['id'])
  addPilotToSession(s['id'], results1[1]['pilot']['id'])
  addPilotToSession(s['id'], results1[2]['pilot']['id'])
  addPilotToSession(s['id'], results2[0]['pilot']['id'])
  addPilotToSession(s['id'], results2[1]['pilot']['id'])
  addPilotToSession(s['id'], results2[2]['pilot']['id'])
  beaconsPerSession[s['id']] = [
      results1[0]['pilot']['beaconNumber'],
      results1[1]['pilot']['beaconNumber'],
      results1[2]['pilot']['beaconNumber'],
      results2[0]['pilot']['beaconNumber'],
      results2[1]['pilot']['beaconNumber'],
      results2[2]['pilot']['beaconNumber'],
  ]

startHour = 16
startMinute = 30
startDelta = 4
sessionIndex = 0
for session in friPmSemiSessions:
  # Starts all together
  h, m = divmod((startHour * 60) + startMinute + (sessionIndex * startDelta), 60)
  startSession(session['id'], datetime(2000, 1, 1, h, m, 5))
  # Ends
  beaconsOfSession = beaconsPerSession[session['id']]
  # This one falls :)
  doesNotFinish = beaconsOfSession[2]
  eh, em = divmod((h * 60) + m + 2, 60)
  for beaconNumber in beaconsOfSession:
    es = randint(0, 30)
    if beaconNumber != doesNotFinish:
      ping(datetime(2000, 1, 1, eh, em, es, randint(0, 500000)), beacons[beaconNumber]['id'], -99, chrono['id'])
  endSession(session['id'], datetime(2000, 1, 1, eh, em, 59))
  sessionIndex += 1

friPmSemiSessionsResults = []
for session in friPmSemiSessions:
  print("---- Tests Results of " + session['name'] + "----")
  sessionLaps = getLapsForSession(session['id'])
  printLaps(sessionLaps, True)
  checkNumberLaps(sessionLaps, 5)
  checkCountWithLapIndex(sessionLaps, 1, 5)
  checkCountWithLapNumber(sessionLaps, 1, 5)
  checkPilotFilled(sessionLaps)
  checkLaptimeFilled(sessionLaps)
  checkStartsOrdered(sessionLaps)
  checkEndsOrdered(sessionLaps)

  sessionBests = getBestLapsForSession(session['id'])
  printLaps(sessionBests, True)
  checkNumberLaps(sessionBests, 5)
  checkCountWithLapIndex(sessionBests, 1, 5)
  checkCountWithLapNumber(sessionBests, 1, 5)
  checkPilotFilled(sessionBests)
  checkLaptimeFilled(sessionBests)
  checkDeltaBestInIncreasingOrder(sessionBests)
  checkDeltaPreviousFilled(sessionBests)

  sessionResults = getResultsForSession(session['id'])
  friPmSemiSessionsResults.append(sessionResults)
  printLaps(sessionResults, True)
  checkCountWithLapIndex(sessionResults, 1, 5)
  checkCountWithLapNumber(sessionResults, 1, 5)
  checkNumberLaps(sessionResults, 6)
  checkPilotFilled(sessionResults)
  checkLaptimeFilled(sessionResults, True)
  checkDeltaBestInIncreasingOrder(sessionResults, True)
  checkDeltaPreviousFilled(sessionResults, True)
# -- Finale - 1 x 6
print("---- Finale ----")
# We keep 3 best
beaconsPerSession = {}
s = friPmFinale
session = friPmFinale
results1 = friPmSemiSessionsResults[0]
results2 = friPmSemiSessionsResults[1]
addPilotToSession(s['id'], results1[0]['pilot']['id'])
addPilotToSession(s['id'], results1[1]['pilot']['id'])
addPilotToSession(s['id'], results1[2]['pilot']['id'])
addPilotToSession(s['id'], results2[0]['pilot']['id'])
addPilotToSession(s['id'], results2[1]['pilot']['id'])
addPilotToSession(s['id'], results2[2]['pilot']['id'])
beaconsPerSession[s['id']] = [
    results1[0]['pilot']['beaconNumber'],
    results1[1]['pilot']['beaconNumber'],
    results1[2]['pilot']['beaconNumber'],
    results2[0]['pilot']['beaconNumber'],
    results2[1]['pilot']['beaconNumber'],
    results2[2]['pilot']['beaconNumber'],
]

startHour = 16
startMinute = 45
# Starts all together
h, m = divmod((startHour * 60) + startMinute, 60)
startSession(session['id'], datetime(2000, 1, 1, h, m, 5))
# Ends
beaconsOfSession = beaconsPerSession[session['id']]
eh, em = divmod((h * 60) + m + 2, 60)
for beaconNumber in beaconsOfSession:
  es = randint(0, 30)
  ping(datetime(2000, 1, 1, eh, em, es, randint(0, 500000)), beacons[beaconNumber]['id'], -99, chrono['id'])
endSession(session['id'], datetime(2000, 1, 1, eh, em, 59))

print("---- Tests Results of " + session['name'] + "----")
sessionLaps = getLapsForSession(session['id'])
printLaps(sessionLaps, True)
checkNumberLaps(sessionLaps, 6)
checkCountWithLapIndex(sessionLaps, 1, 6)
checkCountWithLapNumber(sessionLaps, 1, 6)
checkPilotFilled(sessionLaps)
checkLaptimeFilled(sessionLaps)
checkStartsOrdered(sessionLaps)
checkEndsOrdered(sessionLaps)

sessionBests = getBestLapsForSession(session['id'])
printLaps(sessionBests, True)
checkNumberLaps(sessionBests, 6)
checkCountWithLapIndex(sessionBests, 1, 6)
checkCountWithLapNumber(sessionBests, 1, 6)
checkPilotFilled(sessionBests)
checkLaptimeFilled(sessionBests)
checkDeltaBestInIncreasingOrder(sessionBests)
checkDeltaPreviousFilled(sessionBests)

friPmFinaleSessionResults = getResultsForSession(session['id'])
printLaps(friPmFinaleSessionResults, True)
checkCountWithLapIndex(friPmFinaleSessionResults, 1, 6)
checkCountWithLapNumber(friPmFinaleSessionResults, 1, 6)
checkNumberLaps(friPmFinaleSessionResults, 6)
checkPilotFilled(friPmFinaleSessionResults)
checkLaptimeFilled(friPmFinaleSessionResults, True)
checkDeltaBestInIncreasingOrder(friPmFinaleSessionResults, True)
checkDeltaPreviousFilled(friPmFinaleSessionResults, True)

# -------------------------------------
# Samedi matin	derby 1 mercantour	Mercantour
# -------------------------------------

h = 10
m = 7
startSession(satDerby1Elite['id'], datetime(2000, 1, 2, h, m, 5))
eh, em = divmod((h * 60) + m + 2, 60)
for pilot in elitePilots:
  es = randint(5, 15)
  ping(datetime(2000, 1, 2, eh, em, es, randint(0, 500000)), pilot['currentBeacon']['id'], -99, chrono['id'])
endSession(satDerby1Elite['id'], datetime(2000, 1, 2, eh, em, 59))

print("---- Tests Results of " + satDerby1Elite['name'] + "----")
sessionLaps = getLapsForSession(satDerby1Elite['id'])
printLaps(sessionLaps, True)
checkNumberLaps(sessionLaps, len(elitePilots))
checkCountWithLapIndex(sessionLaps, 1, len(elitePilots))
checkCountWithLapNumber(sessionLaps, 1, len(elitePilots))
checkPilotFilled(sessionLaps)
checkLaptimeBetween(sessionLaps, 120000, 131000)
checkStartsOrdered(sessionLaps)
checkEndsOrdered(sessionLaps)

sessionBests = getBestLapsForSession(satDerby1Elite['id'])
printLaps(sessionBests, True)
checkNumberLaps(sessionBests, len(elitePilots))
checkCountWithLapIndex(sessionBests, 1, len(elitePilots))
checkCountWithLapNumber(sessionBests, 1, len(elitePilots))
checkPilotFilled(sessionBests)
checkLaptimeBetween(sessionBests, 120000, 131000)
checkDeltaBestInIncreasingOrder(sessionBests)
checkDeltaPreviousFilled(sessionBests)

sessionResults = getResultsForSession(satDerby1Elite['id'])
printLaps(sessionResults, True)
checkCountWithLapIndex(sessionResults, 1, len(elitePilots))
checkCountWithLapNumber(sessionResults, 1, len(elitePilots))
checkNumberLaps(sessionResults, len(elitePilots))
checkPilotFilled(sessionResults)
checkLaptimeBetween(sessionResults, 120000, 131000)
checkDeltaBestInIncreasingOrder(sessionResults, True)
checkDeltaPreviousFilled(sessionResults, True)

h = 10
m = 17
startSession(satDerby1Open['id'], datetime(2000, 1, 2, h, m, 5))
eh, em = divmod((h * 60) + m + 2, 60)
for pilot in openPilots:
  es = randint(15, 25)
  ping(datetime(2000, 1, 2, eh, em, es, randint(0, 500000)), pilot['currentBeacon']['id'], -99, chrono['id'])
endSession(satDerby1Open['id'], datetime(2000, 1, 2, eh, em, 59))

print("---- Tests Results of " + satDerby1Open['name'] + "----")
sessionLaps = getLapsForSession(satDerby1Open['id'])
printLaps(sessionLaps, True)
checkNumberLaps(sessionLaps, len(openPilots))
checkCountWithLapIndex(sessionLaps, 1, len(openPilots))
checkCountWithLapNumber(sessionLaps, 1, len(openPilots))
checkPilotFilled(sessionLaps)
checkLaptimeBetween(sessionLaps, 130000, 141000)
checkStartsOrdered(sessionLaps)
checkEndsOrdered(sessionLaps)

sessionBests = getBestLapsForSession(satDerby1Open['id'])
printLaps(sessionBests, True)
checkNumberLaps(sessionBests, len(openPilots))
checkCountWithLapIndex(sessionBests, 1, len(openPilots))
checkCountWithLapNumber(sessionBests, 1, len(openPilots))
checkPilotFilled(sessionBests)
checkLaptimeBetween(sessionBests, 130000, 141000)
checkDeltaBestInIncreasingOrder(sessionBests)
checkDeltaPreviousFilled(sessionBests)

sessionResults = getResultsForSession(satDerby1Open['id'])
printLaps(sessionResults, True)
checkCountWithLapIndex(sessionResults, 1, len(openPilots))
checkCountWithLapNumber(sessionResults, 1, len(openPilots))
checkNumberLaps(sessionResults, len(openPilots))
checkPilotFilled(sessionResults)
checkLaptimeBetween(sessionResults, 130000, 141000)
checkDeltaBestInIncreasingOrder(sessionResults, True)
checkDeltaPreviousFilled(sessionResults, True)

h = 10
m = 27
startSession(satDerby1Woman['id'], datetime(2000, 1, 2, h, m, 5))
eh, em = divmod((h * 60) + m + 2, 60)
for pilot in womanPilots:
  es = randint(25, 35)
  ping(datetime(2000, 1, 2, eh, em, es, randint(0, 500000)), pilot['currentBeacon']['id'], -99, chrono['id'])
endSession(satDerby1Woman['id'], datetime(2000, 1, 2, eh, em, 59))

print("---- Tests Results of " + satDerby1Woman['name'] + "----")
sessionLaps = getLapsForSession(satDerby1Woman['id'])
printLaps(sessionLaps, True)
checkNumberLaps(sessionLaps, len(womanPilots))
checkCountWithLapIndex(sessionLaps, 1, len(womanPilots))
checkCountWithLapNumber(sessionLaps, 1, len(womanPilots))
checkPilotFilled(sessionLaps)
checkLaptimeBetween(sessionLaps, 140000, 151000)
checkStartsOrdered(sessionLaps)
checkEndsOrdered(sessionLaps)

sessionBests = getBestLapsForSession(satDerby1Woman['id'])
printLaps(sessionBests, True)
checkNumberLaps(sessionBests, len(womanPilots))
checkCountWithLapIndex(sessionBests, 1, len(womanPilots))
checkCountWithLapNumber(sessionBests, 1, len(womanPilots))
checkPilotFilled(sessionBests)
checkLaptimeBetween(sessionBests, 140000, 151000)
checkDeltaBestInIncreasingOrder(sessionBests)
checkDeltaPreviousFilled(sessionBests)

sessionResults = getResultsForSession(satDerby1Woman['id'])
printLaps(sessionResults, True)
checkCountWithLapIndex(sessionResults, 1, len(womanPilots))
checkCountWithLapNumber(sessionResults, 1, len(womanPilots))
checkNumberLaps(sessionResults, len(womanPilots))
checkPilotFilled(sessionResults)
checkLaptimeBetween(sessionResults, 140000, 151000)
checkDeltaBestInIncreasingOrder(sessionResults, True)
checkDeltaPreviousFilled(sessionResults, True)

h = 10
m = 37
startSession(satDerby1Junior['id'], datetime(2000, 1, 2, h, m, 5))
eh, em = divmod((h * 60) + m + 2, 60)
for pilot in juniorPilots:
  es = randint(35, 45)
  ping(datetime(2000, 1, 2, eh, em, es, randint(0, 500000)), pilot['currentBeacon']['id'], -99, chrono['id'])
endSession(satDerby1Junior['id'], datetime(2000, 1, 2, eh, em, 59))

print("---- Tests Results of " + satDerby1Junior['name'] + "----")
sessionLaps = getLapsForSession(satDerby1Junior['id'])
printLaps(sessionLaps, True)
checkNumberLaps(sessionLaps, len(juniorPilots))
checkCountWithLapIndex(sessionLaps, 1, len(juniorPilots))
checkCountWithLapNumber(sessionLaps, 1, len(juniorPilots))
checkPilotFilled(sessionLaps)
checkLaptimeBetween(sessionLaps, 150000, 161000)
checkStartsOrdered(sessionLaps)
checkEndsOrdered(sessionLaps)

sessionBests = getBestLapsForSession(satDerby1Junior['id'])
printLaps(sessionBests, True)
checkNumberLaps(sessionBests, len(juniorPilots))
checkCountWithLapIndex(sessionBests, 1, len(juniorPilots))
checkCountWithLapNumber(sessionBests, 1, len(juniorPilots))
checkPilotFilled(sessionBests)
checkLaptimeBetween(sessionBests, 150000, 161000)
checkDeltaBestInIncreasingOrder(sessionBests)
checkDeltaPreviousFilled(sessionBests)

sessionResults = getResultsForSession(satDerby1Junior['id'])
printLaps(sessionResults, True)
checkCountWithLapIndex(sessionResults, 1, len(juniorPilots))
checkCountWithLapNumber(sessionResults, 1, len(juniorPilots))
checkNumberLaps(sessionResults, len(juniorPilots))
checkPilotFilled(sessionResults)
checkLaptimeBetween(sessionResults, 150000, 161000)
checkDeltaBestInIncreasingOrder(sessionResults, True)
checkDeltaPreviousFilled(sessionResults, True)

# -------------------------------------
# Samedi matin	freestyle
# -------------------------------------
# pas de chronos

# -------------------------------------
#samedi aprem 	qualification dual	dual	1 contre 1, deux runs, le cumul des deux pour gagner. soit on mesure  l ecart à l arrivée, soit on chronomètre le run complet
# -------------------------------------

# -------------------------------------
#samedi soir	dual de nuit phase finale	dual	1/4 PUIS 1/2 PUIS PETITE FINALE POUIS FINALE
# -------------------------------------

# -------------------------------------
#dimanche matin 	derby 2 valette	valette		?	?
# -------------------------------------
#	descente 2 et 3 red river + roubines	Red River et Roubines	DEUX DEPARTS DIFFERENTS UNE SEULE ET MEME ARRIVEE 3 RUNS. UN DE CHAQUE ET UN OPTIONNEL POUR SE RATTRAPER AU CHOIX SUR LES DEUX PARCOURS	?	?	Idem dual mais avec les départs plus éloignés l'un de l'autre?

# -------------------------------------
#15h remise des prix
# -------------------------------------

#			CLASSEMENT G2N2RAL PAR POINTS