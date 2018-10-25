#!/usr/bin/python
# Beacon Laptiming Adaptation
# PONCHEL JEREMY - 08/02/2016

import os
import datetime
from bluetooth_scanner import BluetoothScanner
from select_first import SelectFirstStrategy
from send_none import SendNoneStrategy
from chrono import Chronometer

DEBUG = (os.getenv('TEKNICHRONO_DEBUG', 'false') == 'true')
BT_DEBUG = (os.getenv('TEKNICHRONO_BT_DEBUG', 'false') == 'true')
PING_SELECTION_STRATEGY = os.getenv('PING_SELECTION_STRATEGY', 'FIRST')
PING_SEND_STRATEGY = os.getenv('PING_SEND_STRATEGY', 'NONE')
INACTIVITY_WINDOW = int(os.getenv('INACTIVITY_WINDOW', '30'))
WINDOW = int(os.getenv('WINDOW', '5'))
TEKNICHRONO_SERVER = os.getenv('TEKNICHRONO_SERVER', 'http://localhost:8080')
CHRONO_NAME = os.getenv('CHRONO_NAME', 'Raspberry')

print('--------------------------------------')
print('Teknichrono client Startup')
print('--------------------------------------')
print('DEBUG = ' + str(DEBUG))
print('BT_DEBUG = ' + str(DEBUG))
print('PING_SELECTION_STRATEGY = ' + PING_SELECTION_STRATEGY)
print('PING_SEND_STRATEGY = ' + PING_SEND_STRATEGY)
print('INACTIVITY_WINDOW = ' + str(INACTIVITY_WINDOW))
print('WINDOW = ' + str(WINDOW))
print('TEKNICHRONO_SERVER = ' + TEKNICHRONO_SERVER)
print('CHRONO_NAME = ' + CHRONO_NAME)
print('--------------------------------------')

scanner = BluetoothScanner()
scanner.init()


def getSelectionStrategy(key):
  switcher = {'FIRST': SelectFirstStrategy(DEBUG, INACTIVITY_WINDOW)}
  # Get the function from switcher dictionary
  return switcher.get(key)


def getSendStrategy(key):
  switcher = {'NONE': SendNoneStrategy()}
  # Get the function from switcher dictionary
  return switcher.get(key)


chrono = Chronometer(CHRONO_NAME, TEKNICHRONO_SERVER)
print('Using Chronometer ID=' + str(chrono.id))
selectionStrategy = getSelectionStrategy(PING_SELECTION_STRATEGY)
sendStrategy = getSendStrategy(PING_SEND_STRATEGY)

while True:
  current = scanner.scan()
  toSend = selectionStrategy.select(current)
  if toSend is not None:
    sendStrategy.send(toSend)
