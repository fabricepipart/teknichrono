#!python3

import sys
import os
import logging
import random
import time
from scan.beacon_scan import BeaconScan
from scan.blescan import parse_events, connect
from scan.demo_scanner import fake_scan


class BluetoothScanner:
  def __init__(self):
    self.dev_id = 0
    self.sock = None
    self.demo_mode = (os.getenv('DEMO_MODE', 'false') == 'true')
    self.logger = logging.getLogger('BluetoothScanner')
    self.bt_logger = logging.getLogger('BT')

  def init(self):
    try:
      self.logger.info("Loading Scanner ...")
      if not self.demo_mode:
        self.sock = connect(self.dev_id)
    except:
      self.logger.error("No bluetooth controller found ...")
      sys.exit(1)
    self.logger.info("Scanner loaded")

  def scan(self):
    try:
      if self.demo_mode:
        current = fake_scan()
      else:
        current = parse_events(self.sock, 1)
    except Exception as ex:
      self.logger.debug("No bluetooth device found : " + type(ex).__name__)
      print(ex)
      current = []
    if len(current) > 0:
      scanned = BeaconScan()
      scanned.init(current[0])
      if isinstance(scanned.major, int) and isinstance(scanned.minor, int) and scanned.minor == scanned.major and scanned.minor > 0:
        self.bt_logger.debug('Saw : ' + str(scanned))
        return scanned
    return None