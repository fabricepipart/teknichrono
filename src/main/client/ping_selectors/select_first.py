#!python3

import datetime
import logging


class SelectFirstStrategy:
  def __init__(self, chronometer):
    self.scans = {}
    self.chronometer = chronometer
    self.logger = logging.getLogger('SelectStrategy')

  def select(self, current):
    toReturn = None
    if current is not None:
      if current.major not in self.scans:
        # First lap
        self.logger.debug('First Ping for ' + str(current.major))
        toReturn = current
      else:
        nowSeconds = datetime.datetime.now().timestamp()
        # The last one we saw was long enough ago. It is a new lap
        if self.scans[current.major].scanDate + self.chronometer.inactivityWindow < nowSeconds:
          toReturn = current
          self.logger.debug('New Ping for ' + str(current.major) + ' previous @ ' + str(datetime.datetime.fromtimestamp(self.scans[current.major].scanDate)))
      self.scans[current.major] = current
    return toReturn