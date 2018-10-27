#!python3

import datetime
from ping import Ping
from beacon import Beacon


class SendSyncStrategy:
  def __init__(self, server, chronoId):
    self.server = server
    self.chronoId = chronoId
    self.beacons = {}

  def send(self, sendme):
    p = Ping(self.server)
    d = datetime.datetime.fromtimestamp(sendme.scanDate)
    beaconNumber = sendme.major
    if beaconNumber not in self.beacons:
      self.beacons[beaconNumber] = Beacon(beaconNumber, self.server)
    beaconId = self.beacons[beaconNumber].id
    p.ping(d, beaconId, sendme.tx, self.chronoId)
    print(str(datetime.datetime.now()) + '[SYNC]\tPing : ' + str(sendme))
