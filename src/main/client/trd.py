#!/usr/bin/python
# Beacon Laptiming Adaptation
# PONCHEL JEREMY - 08/02/2016

import os
import socket

from ping_selectors.factory import getSelectionStrategy
from ping_senders.factory import getSendStrategy
from scan.factory import getScanner
from datamodel.chrono_synchronizer import ChronoSynchronizer
from logs.init import setupBackupFile, setupLogging

TEKNICHRONO_SERVER = os.getenv('TEKNICHRONO_SERVER', 'http://localhost:8080')
CHRONO_NAME = os.getenv('CHRONO_NAME', 'Raspberry')

setupLogging(False, False)
BACKUP_FILE = setupBackupFile()

socket.setdefaulttimeout(2.0)

chronoSynchronizer = ChronoSynchronizer(CHRONO_NAME, TEKNICHRONO_SERVER)
chronoSynchronizer.start()

scanner = getScanner()
sendStrategy = None
selectionStrategy = None

try:
  while not chronoSynchronizer.restartNeeded:
    if chronoSynchronizer.resetNeeded:
      if sendStrategy:
        sendStrategy.stop()
        sendStrategy.join()
      selectionStrategy = getSelectionStrategy(chronoSynchronizer.chronometer.selectionStrategy, chronoSynchronizer.chronometer)
      sendStrategy = getSendStrategy(chronoSynchronizer.chronometer.sendStrategy, TEKNICHRONO_SERVER, chronoSynchronizer.chronometer.id)
      sendStrategy.start()
      chronoSynchronizer.resetNeeded = False
    current = scanner.scan()
    toSend = selectionStrategy.select(current)
    if toSend:
      print(toSend.toJson(), file=BACKUP_FILE)
      sendStrategy.append(toSend)
finally:
  print("Ending application")
  if sendStrategy:
    sendStrategy.stop()
    sendStrategy.join()
  chronoSynchronizer.stop()
  chronoSynchronizer.join()
  BACKUP_FILE.close()
