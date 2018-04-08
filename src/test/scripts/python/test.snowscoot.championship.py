#!python3

import requests
import json
import sys
from datetime import date, datetime, timedelta

#from base import *
from pilots import addPilot, getPilot, associatePilotBeacon
from beacons import getBeacon
from chronometer import addChronometer
from event import addEvent, addSessionToEvent
#from ping import *
#from laps import *
from session import addSession, addChronometerToSession
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
for i in range(0, 30):
  pilot = addPilot('Rider ' + str(i), 'Elite')
  addPilotToCategory(eliteCategory['id'], pilot['id'])
for i in range(0, 20):
  pilot = addPilot('Rider ' + str(i), 'Open')
  addPilotToCategory(openCategory['id'], pilot['id'])
for i in range(0, 20):
  pilot = addPilot('Rider ' + str(i), 'Woman')
  addPilotToCategory(womanCategory['id'], pilot['id'])
for i in range(0, 10):
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

# -------------------------------------
# Thursday evening
# -------------------------------------
# jeudi soir	accueil concurrents et distribution transpondeurs

for i in range(0, 30):
  pilot = getPilot('Rider ' + str(i), 'Elite')
  associatePilotBeacon(pilot['id'], getBeacon(90 - i)['id'])
for i in range(0, 20):
  pilot = getPilot('Rider ' + str(i), 'Open')
  associatePilotBeacon(pilot['id'], getBeacon(60 - i)['id'])
for i in range(0, 20):
  pilot = getPilot('Rider ' + str(i), 'Woman')
  associatePilotBeacon(pilot['id'], getBeacon(40 - i)['id'])
for i in range(0, 10):
  pilot = getPilot('Rider ' + str(i), 'Junior')
  associatePilotBeacon(pilot['id'], getBeacon(20 - i)['id'])

# -------------------------------------
#vendredi matin	descente dans le boarder cross	Border cross	deux runs d essais (controle transpondeurs) deux runs chronos, le meilleur retenu	?	?	Donc à la fin il faut juste que tu aies un classement des meilleurs tours par pilote sur les deux runs, exact?
# -------------------------------------

friMorningTestSession = addSession('Friday morning tests', datetime(2000, 1, 1, 10), datetime(2000, 1, 1, 11), 'tt')
addSessionToLocation(boarderCross['id'], friMorningTestSession['id'])
addSessionToEvent(event['id'], friMorningTestSession['id'])
addChronometerToSession(friMorningTestSession['id'], chrono['id'])

#TODO Some do 1 test
#TODO Some dont test
#TODO Some finish after expected time

friMorningChronoSession = addSession('Friday morning Chrono', datetime(2000, 1, 1, 11), datetime(2000, 1, 1, 12), 'tt')
addSessionToLocation(boarderCross['id'], friMorningChronoSession['id'])
addSessionToEvent(event['id'], friMorningChronoSession['id'])
addChronometerToSession(friMorningChronoSession['id'], chrono['id'])

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