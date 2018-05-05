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
from session import addSession, addChronometerToSession, startSession, addPilotToSession
from location import addLocation, addSessionToLocation
from category import addCategory, addPilotToCategory
from check import checkNumberLaps, checkPilotFilled, checkCountWithLapIndex, checkCountWithLapNumber, checkLaptimeFilled, checkDeltaBestInIncreasingOrder, checkDeltaPreviousFilled, checkStartsOrdered, checkEndsOrdered

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

print("-------------------------------------")
print("Thursday evening")
print("-------------------------------------")
# jeudi soir	accueil concurrents et distribution transpondeurs

beacons = {}

beaconNumber = 10
for pilot in allPilots:
  beacons[beaconNumber] = getBeacon(beaconNumber)
  associatePilotBeacon(pilot['id'], beacons[beaconNumber]['id'])
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
  sessionIndex += 1

#TODO Add verifications
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
# -- 1/4 th - 4 x 6
print("---- 1 / 4 th ----")
# -- 1/2 th - 2 x 6
print("---- 1 / 2 th ----")
# -- Finale - 1 x 6
print("---- Finale ----")

# -------------------------------------
#Samedi matin	derby 1 mercantour	Mercantour
# -------------------------------------

#	freestyle	-	pas de chronos

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