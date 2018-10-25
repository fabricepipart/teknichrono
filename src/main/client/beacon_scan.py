#!python3

import datetime


class BeaconScan:
  def __init__(self):
    self.mac = None
    self.uuid = None
    self.major = None
    self.minor = None
    self.tx = None
    self.rssi = None
    self.scanDate = datetime.datetime.now().timestamp()

  def init(self, scanString):
    data = scanString.split(",")
    self.mac = data[0]
    self.uuid = data[1]
    self.major = int(data[2])
    self.minor = int(data[3])
    self.tx = data[5]
    self.rssi = data[4]

  def __str__(self):
    return '(' + str(datetime.datetime.fromtimestamp(self.scanDate)) + ') ' + str(self.major) + ' / ' + str(self.minor) + ' @ ' + self.tx + ' dB'
