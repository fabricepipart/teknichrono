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
from check import checkNumberLaps, checkPilotFilled, checkCountWithLapIndex, checkCountWithLapNumber, checkLaptimeFilled, checkDeltaBestInIncreasingOrder, checkDeltaPreviousFilled

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

print("-------------------------------------")
print("Thursday evening")
print("-------------------------------------")
# jeudi soir	accueil concurrents et distribution transpondeurs

beaconNumber = 10
for pilot in allPilots:
  associatePilotBeacon(pilot['id'], getBeacon(beaconNumber)['id'])
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
  ping(datetime(2000, 1, 1, 10 + h, m, s, randint(0, 500000)), getBeacon(i)['id'], -99, fake1['id'])
# -- End
endMinute = startMinute + 2
for i in range(11, 89):
  delta = int(i / 3) + randint(0, int(i / 3))
  m, s = divmod(i * startDelta + delta, 60)
  h, m = divmod(endMinute + m, 60)
  ping(datetime(2000, 1, 1, 10 + h, m, s, randint(0, 500000)), getBeacon(i)['id'], -99, chrono['id'])

print("---- Test #2 ----")
# Starts every 20s
# -- Start
startMinute = 31
for i in range(12, 90):
  m, s = divmod(i * startDelta, 60)
  h, m = divmod(startMinute + m, 60)
  ping(datetime(2000, 1, 1, 10 + h, m, s, randint(0, 500000)), getBeacon(i)['id'], -99, fake1['id'])
# -- End
endMinute = startMinute + 2
for i in range(12, 88):
  delta = int(i / 3) + randint(0, int(i / 3))
  m, s = divmod(i * startDelta + delta, 60)
  h, m = divmod(endMinute + m, 60)
  ping(datetime(2000, 1, 1, 10 + h, m, s, randint(0, 500000)), getBeacon(i)['id'], -99, chrono['id'])

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

# TODO Checks - Asserts
checkNumberLaps(friMorningTestsResults, 80)
checkPilotFilled(friMorningTestsResults)
checkCountWithLapIndex(friMorningTestsResults, 0, 2)
checkCountWithLapNumber(friMorningTestsResults, 0, 2)
checkLaptimeFilled(friMorningTestsResults, True)
checkDeltaBestInIncreasingOrder(friMorningTestsResults, True)
checkDeltaPreviousFilled(friMorningTestsResults, True)

startSession(friMorningChronoSession['id'], datetime(2000, 1, 1, 11, 10, 00))
print("---- Chrono #1 ----")
# TODO Make start order the one of the previous results
# Starts every 20s
startDelta = 20
# -- Start
startMinute = 11
for i in range(11, 90):
  m, s = divmod(i * startDelta, 60)
  h, m = divmod(startMinute + m, 60)
  ping(datetime(2000, 1, 1, 11 + h, m, s, randint(0, 500000)), getBeacon(i)['id'], -99, fake1['id'])
# -- End
endMinute = startMinute + 2
for i in range(11, 89):
  delta = int(i / 3) + randint(0, int(i / 3))
  m, s = divmod(i * startDelta + delta, 60)
  h, m = divmod(endMinute + m, 60)
  ping(datetime(2000, 1, 1, 11 + h, m, s, randint(0, 500000)), getBeacon(i)['id'], -99, chrono['id'])

print("---- Chrono #2 ----")
# Starts every 20s
# -- Start
startMinute = 45
for i in range(12, 90):
  m, s = divmod(i * startDelta, 60)
  h, m = divmod(startMinute + m, 60)
  ping(datetime(2000, 1, 1, 11 + h, m, s, randint(0, 500000)), getBeacon(i)['id'], -99, fake1['id'])
# -- End
endMinute = startMinute + 2
for i in range(12, 88):
  delta = int(i / 3) + randint(0, int(i / 3))
  m, s = divmod(i * startDelta + delta, 60)
  h, m = divmod(endMinute + m, 60)
  ping(datetime(2000, 1, 1, 11 + h, m, s, randint(0, 500000)), getBeacon(i)['id'], -99, chrono['id'])

print("---- Chrono Results ----")

# ---- Results ----
printLaps(getLapsForSession(friMorningChronoSession['id']), True)
printLaps(getBestLapsForSession(friMorningChronoSession['id']), True)
friMorningChronoResults = getResultsForSession(friMorningChronoSession['id'])
printLaps(friMorningChronoResults, True)
checkNumberLaps(friMorningChronoResults, 80)

# ---- Checks - Asserts ----
#TODO Some do 1 chrono
#TODO Some dont chrono
#TODO Some finish after expected time

# -------------------------------------
#vendredi apres midi	boarder cross	boarder cross	départ à 4 ou 6	?	?	Quel ordre de départ? Comment sont déterminés les groupes?
# -------------------------------------

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