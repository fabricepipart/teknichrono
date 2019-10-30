#!python3

import datetime
import logging


class SelectLastStrategy:
  def __init__(self, chronometer):
    self.scans = {}
    self.chronometer = chronometer
    self.logger = logging.getLogger('SelectStrategy')

  def select(self, current):
    toReturn = self.selectOneOld()
    if current is not None:
      if int(current.tx) > self.chronometer.txThreshold:
        self.scans[current.major] = current
    return toReturn

  def selectOneOld(self):
    for major, scan in self.scans.items():
      nowSeconds = datetime.datetime.now().timestamp()
      if scan.scanDate + self.chronometer.inactivityWindow < nowSeconds:
        self.logger.debug('Ping for ' + str(major) + ' (last seen: ' + str(datetime.datetime.fromtimestamp(scan.scanDate)) + ')')
        return self.scans.pop(major)
    return None