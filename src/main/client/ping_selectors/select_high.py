#!python3

import datetime
import logging


class SelectHighStrategy:
  def __init__(self, chronometer):
    self.scans = {}
    self.chronometer = chronometer
    self.logger = logging.getLogger('SelectStrategy')

  def select(self, current):
    toReturn = self.selectOneOld()
    if current is not None:
      if int(current.tx) > self.chronometer.txThreshold:
        if current.major in self.scans:
          # Negative values
          if int(current.tx) > int(self.scans[current.major].tx):
            # Update because it is higher
            self.logger.debug('Higher Ping for ' + str(current.major) + ' @ ' + current.tx + 'dB (before: ' + self.scans[current.major].tx + 'dB)')
            self.scans[current.major] = current
        else:
          # Update because it is a new lap
          self.logger.debug('New Ping for ' + str(current.major))
          self.scans[current.major] = current
    return toReturn

  def selectOneOld(self):
    for major, scan in self.scans.items():
      nowSeconds = datetime.datetime.now().timestamp()
      if scan.scanDate + self.chronometer.inactivityWindow < nowSeconds:
        self.logger.debug('Ping for ' + str(major) + ' (seen: ' + str(datetime.datetime.fromtimestamp(scan.scanDate)) + ')')
        return self.scans.pop(major)
    return None
