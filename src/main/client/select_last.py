#!python3

import datetime
import logging


class SelectLastStrategy:
  def __init__(self, inactivityWindow):
    self.scans = {}
    self.inactivityWindow = inactivityWindow
    self.logger = logging.getLogger('SelectStrategy')

  def select(self, current):
    toReturn = self.selectOneOld()
    if current is not None:
      self.scans[current.major] = current
    return toReturn

  def selectOneOld(self):
    for major, scan in self.scans.items():
      nowSeconds = datetime.datetime.now().timestamp()
      if scan.scanDate + self.inactivityWindow < nowSeconds:
        self.logger.debug('Ping for ' + str(major) + ' (last seen: ' + str(datetime.datetime.fromtimestamp(scan.scanDate)) + ')')
        return self.scans.pop(major)
    return None