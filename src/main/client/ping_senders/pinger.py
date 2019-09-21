#!python3

import datetime
import logging

from common.rest import post, formatDatetime
from datamodel.beacon import Beacon


class Pinger:
  def __init__(self, host, chronoId):
    self.server = host
    self.restApiUrl = host + '/rest/pings'
    self.chronoId = chronoId
    self.beacons = {}
    self.logger = logging.getLogger('Pinger')

  def sendAll(self, beaconScans):
    data = []
    self.logger.debug('[SEND] Trying to send pings: ' + str(len(beaconScans)))
    for toSend in beaconScans:
      d = datetime.datetime.fromtimestamp(toSend.scanDate)
      beaconId = self.getBeaconId(toSend.major)
      data.append({'instant': formatDatetime(d), 'power': str(toSend.tx), 'beacon': {'id': beaconId}, 'chronometer': {'id': self.chronoId}})
    self.pingMulti(data)
    for beaconScan in beaconScans:
      self.logger.info('[SEND] Ping sent : ' + str(beaconScan))

  def getBeaconId(self, beaconNumber):
    if beaconNumber not in self.beacons:
      self.beacons[beaconNumber] = Beacon(beaconNumber, self.server)
    return self.beacons[beaconNumber].id

  def pingMulti(self, data):
    "This adds several Pings"
    url = self.restApiUrl + '/create-multi'
    response = post(data, url)
    return response