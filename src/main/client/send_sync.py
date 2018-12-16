#!python3

import datetime
import logging
from ping import Ping
from beacon import Beacon


class SendSyncStrategy:
  def __init__(self, server, chronoId):
    self.server = server
    self.chronoId = chronoId
    self.failures = []
    self.beacons = {}
    self.logger = logging.getLogger('SendStrategy')

  def send(self, sendme):
    try:
      self.sendone(sendme)
      # We succeeded with one let's recover
      for failure in self.failures:
        try:
          self.sendone(failure)
        except:
          self.logger.error('Ping : ' + str(sendme))
    except:
      self.failures.append(sendme)
      self.logger.error('Ping : ' + str(sendme))

  def sendone(self, sendme):
    p = Ping(self.server)
    d = datetime.datetime.fromtimestamp(sendme.scanDate)
    beaconNumber = sendme.major
    if beaconNumber not in self.beacons:
      self.beacons[beaconNumber] = Beacon(beaconNumber, self.server)
    beaconId = self.beacons[beaconNumber].id
    p.ping(d, beaconId, sendme.tx, self.chronoId)
    self.logger.info('[SYNC] Ping : ' + str(sendme))