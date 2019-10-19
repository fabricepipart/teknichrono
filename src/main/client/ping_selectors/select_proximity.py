#!python3

import datetime
import logging

TRACE = False


class SelectProximityStrategy:
  def __init__(self, chronometer):
    self.windowStart = datetime.datetime.now().timestamp()
    self.windowHighest = None
    self.chronometer = chronometer
    self.logger = logging.getLogger('SelectStrategy')

  def select(self, current):
    toReturn = self.closeWindowIfNecessary()
    if current is not None:
      # Necessarily that's interesting
      if self.windowHighest is None:
        self.logger.debug('New Ping for %s', str(current.major))
        self.windowHighest = current
      else:
        if int(current.tx) > int(self.windowHighest.tx):
          self.logger.debug('Higher Ping for %s @ %s dB (previous was %s @ %s dB)', str(current.major), str(current.tx), self.windowHighest.major, self.windowHighest.tx)
          self.windowHighest = current
    return toReturn

  def closeWindowIfNecessary(self):
    toReturn = None
    nowSeconds = datetime.datetime.now().timestamp()
    if self.windowStart + self.chronometer.inactivityWindow < nowSeconds:
      self.windowStart = nowSeconds
      if self.windowHighest:
        self.logger.debug('Highest scan of the window received @ %s : %s @ %s', str(datetime.datetime.fromtimestamp(self.windowHighest.scanDate)), self.windowHighest.major,
                          self.windowHighest.tx)
        toReturn = self.windowHighest
        self.windowHighest = None
    return toReturn
