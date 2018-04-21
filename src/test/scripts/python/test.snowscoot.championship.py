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
from laps import printLaps, getLapsForSession, getBestLapsForSession
from session import addSession, addChronometerToSession, startSession
from location import addLocation, addSessionToLocation
from category import addCategory, addPilotToCategory

# -------------------------------------
# Pre-event
# -------------------------------------

# Add Events
event = addEvent('Snowscoot championship')

# Add Categories
# ELITE (30) OPEN HOMME (20) F2MININES (20) JUNIOR (10)

eliteCategory = addCategory('Elite')
openCategory = addCategory('Open')
womanCategory = addCategory('Woman')
juniorCategory = addCategory('Junior')

# Add Pilots
for i in range(10, 40):
  pilot = addPilot('Rider ' + str(i), 'Elite')
  addPilotToCategory(eliteCategory['id'], pilot['id'])
for i in range(40, 60):
  pilot = addPilot('Rider ' + str(i), 'Open')
  addPilotToCategory(openCategory['id'], pilot['id'])
for i in range(60, 80):
  pilot = addPilot('Rider ' + str(i), 'Woman')
  addPilotToCategory(womanCategory['id'], pilot['id'])
for i in range(80, 90):
  pilot = addPilot('Rider ' + str(i), 'Junior')
  addPilotToCategory(juniorCategory['id'], pilot['id'])

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

friMorningChronoSession = addSession('Friday morning Chrono', datetime(2000, 1, 1, 11), datetime(2000, 1, 1, 12), 'tt')
addSessionToLocation(boarderCross['id'], friMorningChronoSession['id'])
addSessionToEvent(event['id'], friMorningChronoSession['id'])
addChronometerToSession(friMorningChronoSession['id'], chrono['id'])

# -------------------------------------
# Thursday evening
# -------------------------------------
# jeudi soir	accueil concurrents et distribution transpondeurs

for i in range(10, 40):
  pilot = getPilot('Rider ' + str(i), 'Elite')
  associatePilotBeacon(pilot['id'], getBeacon(i)['id'])
for i in range(40, 60):
  pilot = getPilot('Rider ' + str(i), 'Open')
  associatePilotBeacon(pilot['id'], getBeacon(i)['id'])
for i in range(60, 80):
  pilot = getPilot('Rider ' + str(i), 'Woman')
  associatePilotBeacon(pilot['id'], getBeacon(i)['id'])
for i in range(80, 90):
  pilot = getPilot('Rider ' + str(i), 'Junior')
  associatePilotBeacon(pilot['id'], getBeacon(i)['id'])

# -------------------------------------
# vendredi matin
# descente dans le boarder cross
# Border cross
# deux runs d essais (controle transpondeurs)
# deux runs chronos
# le meilleur retenu
# -------------------------------------

# Created sessions earlier and start it here
startSession(friMorningTestSession['id'], datetime(2000, 1, 1, 10, 0, 30))

# ---- Test #1 ----
# Starts every 20s
startDelta = 20
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

# ---- Test #2 ----
# Starts every 20s
# -- Start
startMinute = 31
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

# ---- Results ----
printLaps(getLapsForSession(friMorningTestSession['id']), True)
printLaps(getBestLapsForSession(friMorningTestSession['id']), True)

#TODO Checks - Asserts

#TODO Some do 1 test (2nd)
#TODO Some dont test (1st)
#TODO Some start but dont finish (last ie 89)

#TODO Some finish after expected time

# ----

#TODO Have chart with startup list
#TODO Have people that did not test

startSession(friMorningChronoSession['id'], datetime(2000, 1, 1, 11, 5, 00))

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