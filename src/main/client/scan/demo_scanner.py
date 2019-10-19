#!python3

import random
import logging
import time
from scan.beacon_scan import BeaconScan


class FakeBluetoothScanner:
  def __init__(self):
    self.logger = logging.getLogger('BluetoothScanner')
    self.beacon_nb = 1

  def init(self):
    self.logger.info("Loading Scanner ...")
    self.logger.info("Scanner loaded")

  def scan(self):
    time.sleep(0.1)
    rollDice = random.randint(1, 6)
    # 5/6 chances to have a signal from same
    if rollDice > 5:
      self.beacon_nb = random.randint(1, 10)
    tx = str(random.randint(50, 90))
    if random.randint(0, 10) == 0:
      scanned = BeaconScan()
      beacon_nb_str = str(self.beacon_nb)
      scanned.init("12:34:56:78:9A:DF,uuid," + beacon_nb_str + ',' + beacon_nb_str + ',rssi,-' + tx)
      self.logger.debug('Saw : ' + str(scanned))
      return scanned
    return None
