#!python3

import random
import logging
import time
from scan.beacon_scan import BeaconScan

LAST_BEACON = 1


def fake_scan():
  global LAST_BEACON
  time.sleep(0.1)
  rollDice = random.randint(1, 6)
  # 4/6 chances to have a signal from same
  if rollDice > 4:
    LAST_BEACON = random.randint(1, 10)
  tx = str(random.randint(30, 90))
  beacon_nb_str = str(LAST_BEACON)
  scan = "12:34:56:78:9A:DF,uuid," + beacon_nb_str + ',' + beacon_nb_str + ',rssi,-' + tx
  return [scan]
