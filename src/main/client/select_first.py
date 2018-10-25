#!python3

import datetime
import os


class SelectFirstStrategy:
  def __init__(self, debug, inactivityWindow):
    self.scans = {}
    self.debug = debug
    self.inactivityWindow = inactivityWindow

  def select(self, current):
    toReturn = None
    if current is not None:
      if current.major not in self.scans:
        if self.debug:
          print(str(datetime.datetime.now()) + '\t New Ping for ' + str(current.major))
        toReturn = current
      else:
        nowSeconds = datetime.datetime.now().timestamp()
        if self.scans[current.major][0].scanDate + self.inactivityWindow < nowSeconds:
          toReturn = current
          if self.debug:
            print(str(datetime.datetime.now()) + '\t Discard Ping for ' + str(current.major))
      if toReturn is not None:
        self.scans[current.major] = [current]
    return toReturn