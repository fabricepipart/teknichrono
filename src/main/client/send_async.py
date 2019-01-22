#!python3

import multiprocessing
import datetime
import logging
import time

from ping import Ping
from beacon import Beacon


class SendAsyncStrategy(multiprocessing.Process):
  def __init__(self, server, chronoId, workQueue):
    super(SendAsyncStrategy, self).__init__()  # super() will call Thread.__init__ for you
    self.workQueue = workQueue
    self.server = server
    self.chronoId = chronoId
    self.failures = []
    self.beacons = {}
    self.logger = logging.getLogger('SendStrategy')
    self.lastSend = datetime.datetime.now().timestamp()
    self.waitBeforeRetry = 10

  def run(self):
    while True:
      time.sleep(1)
      if not self.workQueue.empty():
        toSend = self.workQueue.get()
        self.send(toSend)
      else:
        self.sendFailures()

  def sendFailures(self):
    if self.failures and (self.lastSend + self.waitBeforeRetry < datetime.datetime.now().timestamp()):
      # Let's recover
      finallySent = []
      for failure in self.failures:
        try:
          self.logger.info('[SEND] Trying again to send Ping : ' + str(failure))
          self.sendone(failure)
          finallySent.append(failure)
        except:
          self.logger.error('Could not send again Ping : ' + str(failure))
      for failure in finallySent:
        self.failures.remove(failure)

  def send(self, sendme):
    try:
      self.sendone(sendme)
    except:
      self.failures.append(sendme)
      self.logger.error('Could not send for the moment Ping : ' + str(sendme))

  def sendone(self, sendme):
    p = Ping(self.server)
    self.lastSend = datetime.datetime.now().timestamp()
    d = datetime.datetime.fromtimestamp(sendme.scanDate)
    beaconNumber = sendme.major
    if beaconNumber not in self.beacons:
      self.beacons[beaconNumber] = Beacon(beaconNumber, self.server)
    beaconId = self.beacons[beaconNumber].id
    p.ping(d, beaconId, sendme.tx, self.chronoId)
    self.logger.info('[SEND] Ping sent : ' + str(sendme))