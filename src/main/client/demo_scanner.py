#!python3

import random
import logging
import time
from beacon_scan import BeaconScan


class FakeBluetoothScanner:
  def __init__(self):
    self.logger = logging.getLogger('BluetoothScanner')

  def init(self):
    self.logger.info("Loading Scanner ...")

  def scan(self):
    time.sleep(0.1)
    beacon_nb = str(random.randint(1, 10))
    tx = str(random.randint(50, 90))
    if random.randint(0, 10) == 0:
      scanned = BeaconScan()
      scanned.init("12:34:56:78:9A:DF,uuid," + beacon_nb + ',' + beacon_nb + ',rssi,-' + tx)
      self.logger.debug('Saw : ' + str(scanned))
      return scanned
    return None
