#!python3

import bluetooth._bluetooth as bluez
import sys
import os
import logging
from scan.beacon_scan import BeaconScan
from scan.blescan import hci_le_set_scan_parameters, hci_enable_le_scan, parse_events


class BluetoothScanner:
  def __init__(self):
    self.dev_id = 0
    self.failuresCount = 0
    self.sock = None
    self.logger = logging.getLogger('BluetoothScanner')
    self.bt_logger = logging.getLogger('BT')

  def init(self):
    try:
      self.logger.info("Loading Scanner ...")
      self.sock = bluez.hci_open_dev(self.dev_id)
    except:
      self.logger.error("No bluetooth controller found ...")
      sys.exit(1)
    hci_le_set_scan_parameters(self.sock)
    hci_enable_le_scan(self.sock)
    self.logger.info("Scanner loaded")

  def scan(self):
    try:
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
