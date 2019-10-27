import os
import logging
from threading import Thread, Event
from datamodel.chrono import Chronometer
from common.rest import get, post, iso_to_seconds
from logs.init import setupLogging


class ChronoSynchronizer(Thread):

  WAIT_BETWEEN_UPDATES = int(os.getenv('WAIT_BETWEEN_UPDATES', '60'))

  def __init__(self, name, host):
    super(ChronoSynchronizer, self).__init__()
    self.exit = Event()
    self.chronometer = Chronometer()
    self.chronometer.name = name
    self.server = host
    self.logger = logging.getLogger('ChronoSync')
    self.restApiUrl = host + '/rest/chronometers'
    self.restartNeeded = False
    self.resetNeeded = False
    self.updateChronometer()

  def getChronometerByName(self, name):
    "This gets a Chronometer by name and returns a json"
    url = self.restApiUrl + '/name?name=' + name
    response = get(url)
    return response

  def ackOrder(self):
    "This sends ack after executing an order"
    url = self.restApiUrl + '/' + str(self.chronometer.id) + '/ack'
    post('{}', url)

  def updateChronometer(self):
    chronometerMap = self.getChronometerByName(self.chronometer.name)
    self.logger.debug("Chrono " + str(chronometerMap))
    chronoChanged = False
    # id
    if self.chronometer.id != chronometerMap['id']:
      chronoChanged = True
      self.chronometer.id = chronometerMap['id']
    # selectionStrategy
    if self.chronometer.selectionStrategy != chronometerMap['selectionStrategy']:
      chronoChanged = True
      self.resetNeeded = True
      self.chronometer.selectionStrategy = chronometerMap.get('selectionStrategy', 'HIGH')
    # sendStrategy
    if self.chronometer.sendStrategy != chronometerMap['sendStrategy']:
      chronoChanged = True
      self.resetNeeded = True
      self.chronometer.sendStrategy = chronometerMap.get('sendStrategy', 'ASYNC')
    # inactivityWindow
    inactivityWindow = int(iso_to_seconds(chronometerMap.get('inactivityWindow', 'PT5S')))
    if self.chronometer.inactivityWindow != inactivityWindow:
      chronoChanged = True
      self.chronometer.inactivityWindow = inactivityWindow
    # bluetoothDebug
    bluetoothDebug = chronometerMap.get('bluetoothDebug', False)
    if self.chronometer.bluetoothDebug != bluetoothDebug:
      chronoChanged = True
      self.chronometer.bluetoothDebug = bluetoothDebug
    # debug
    debug = chronometerMap.get('debug', False)
    if self.chronometer.debug != debug:
      chronoChanged = True
      self.chronometer.debug = debug
    # selectionStrategy
    if self.chronometer.orderToExecute != chronometerMap.get('orderToExecute', None):
      chronoChanged = True
      self.chronometer.orderToExecute = chronometerMap.get('orderToExecute', None)
      self.executeOrder()
    if chronoChanged:
      self.printChronoInfo()
      setupLogging(self.chronometer.debug, self.chronometer.bluetoothDebug)

  def executeOrder(self):
    self.logger.info("New order : %s", self.chronometer.orderToExecute)
    self.ackOrder()
    if self.chronometer.orderToExecute == 'RESTART':
      self.restartNeeded = True
    if self.chronometer.orderToExecute == 'UPDATE':
      self.restartNeeded = True

  def printChronoInfo(self):
    "This prints the chrono configuration"
    self.logger.info('--------------------------------------')
    self.logger.info('Chronometer info')
    self.logger.info('--------------------------------------')
    self.logger.info('DEBUG = %s', str(self.chronometer.debug))
    self.logger.info('BT_DEBUG = %s', str(self.chronometer.bluetoothDebug))
    self.logger.info('PING_SELECTION_STRATEGY = %s', self.chronometer.selectionStrategy)
    self.logger.info('PING_SEND_STRATEGY = %s', self.chronometer.sendStrategy)
    self.logger.info('INACTIVITY_WINDOW = %s', str(self.chronometer.inactivityWindow))
    self.logger.info('TEKNICHRONO_SERVER = %s', self.server)
    self.logger.info('CHRONO_NAME = %s', self.chronometer.name)
    self.logger.info('--------------------------------------')

  def stop(self):
    self.logger.info('Stopping chrono thread')
    self.exit.set()

  def run(self):
    while not self.exit.is_set():
      try:
        self.exit.wait(self.WAIT_BETWEEN_UPDATES)
        self.updateChronometer()
      except Exception as ex:
        self.logger.error("Chronometer update failed : " + type(ex).__name__)