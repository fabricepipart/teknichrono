#!/usr/bin/python
# Beacon Laptiming Adaptation
# PONCHEL JEREMY - 08/02/2016

import logging
import os
import socket
from multiprocessing import Queue
if os.getenv('DEMO_MODE', 'false') == 'false':
  from bluetooth_scanner import BluetoothScanner
else:
  from demo_scanner import FakeBluetoothScanner
from select_first import SelectFirstStrategy
from select_last import SelectLastStrategy
from select_high import SelectHighStrategy
from send_none import SendNoneStrategy
from send_async import SendAsyncStrategy
from chrono import Chronometer

LOGS_PATH = os.getenv('LOGS_PATH', '/home/pi/scripts/logs')
DEBUG = (os.getenv('TEKNICHRONO_DEBUG', 'false') == 'true')
BT_DEBUG = (os.getenv('TEKNICHRONO_BT_DEBUG', 'false') == 'true')
PING_SELECTION_STRATEGY = os.getenv('PING_SELECTION_STRATEGY', 'FIRST')
PING_SEND_STRATEGY = os.getenv('PING_SEND_STRATEGY', 'NONE')
INACTIVITY_WINDOW = int(os.getenv('INACTIVITY_WINDOW', '30'))
TEKNICHRONO_SERVER = os.getenv('TEKNICHRONO_SERVER', 'http://localhost:8080')
CHRONO_NAME = os.getenv('CHRONO_NAME', 'Raspberry')


def setupBackupFile():
  return open(LOGS_PATH + '/all_pings.log', 'a', buffering=1)


def setupLogging():
  # set up logging to file
  fh = logging.FileHandler(LOGS_PATH + '/teknichrono.log')
  # create console handler with a higher log level
  ch = logging.StreamHandler()
  # Set log levels
  if DEBUG:
    fh.setLevel(logging.DEBUG)
    ch.setLevel(logging.DEBUG)
    logging.getLogger('').setLevel(logging.DEBUG)
  else:
    fh.setLevel(logging.INFO)
    ch.setLevel(logging.INFO)
    logging.getLogger('').setLevel(logging.INFO)
  if BT_DEBUG:
    logging.getLogger('BT').setLevel(logging.DEBUG)
  else:
    logging.getLogger('BT').setLevel(logging.INFO)
  # create formatter and add it to the handlers
  formatter = logging.Formatter('%(asctime)s %(name)-20s %(levelname)-8s %(message)s')
  formatter.default_msec_format = '%s.%03d'
  ch.setFormatter(formatter)
  fh.setFormatter(formatter)
  # add the handlers to logger
  logging.getLogger('').addHandler(ch)
  logging.getLogger('').addHandler(fh)


def getSelectionStrategy(key):
  switcher = {
      'FIRST': SelectFirstStrategy(INACTIVITY_WINDOW),
      'LAST': SelectLastStrategy(INACTIVITY_WINDOW),
      'HIGH': SelectHighStrategy(INACTIVITY_WINDOW),
  }
  # Get the function from switcher dictionary
  return switcher.get(key)


def getSendStrategy(key, chronoId, workQueue):
  switcher = {'NONE': SendNoneStrategy(), 'ASYNC': SendAsyncStrategy(TEKNICHRONO_SERVER, chronoId, workQueue)}
  # Get the function from switcher dictionary
  return switcher.get(key)


setupLogging()
backupFile = setupBackupFile()
logger = logging.getLogger('startup')
socket.setdefaulttimeout(2.0)

logger.info('--------------------------------------')
logger.info('Teknichrono client Startup')
logger.info('--------------------------------------')
logger.info('DEBUG = ' + str(DEBUG))
logger.info('BT_DEBUG = ' + str(BT_DEBUG))
logger.info('PING_SELECTION_STRATEGY = ' + PING_SELECTION_STRATEGY)
logger.info('PING_SEND_STRATEGY = ' + PING_SEND_STRATEGY)
logger.info('INACTIVITY_WINDOW = ' + str(INACTIVITY_WINDOW))
logger.info('TEKNICHRONO_SERVER = ' + TEKNICHRONO_SERVER)
logger.info('CHRONO_NAME = ' + CHRONO_NAME)
logger.info('--------------------------------------')

if os.getenv('DEMO_MODE', 'false') == 'false':
  scanner = BluetoothScanner()
else:
  scanner = FakeBluetoothScanner()
scanner.init()

chrono = Chronometer(CHRONO_NAME, TEKNICHRONO_SERVER)
logger.info('Using Chronometer ID=' + str(chrono.id))
selectionStrategy = getSelectionStrategy(PING_SELECTION_STRATEGY)
work_q = Queue()
sendStrategy = getSendStrategy(PING_SEND_STRATEGY, chrono.id, work_q)
sendStrategy.start()

try:
  while True:
    current = scanner.scan()
    toSend = selectionStrategy.select(current)
    if toSend:
      print(toSend.toJson(), file=backupFile)
      work_q.put(toSend)
finally:
  sendStrategy.terminate()
  backupFile.close()
