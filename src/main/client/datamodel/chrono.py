#!python3


class Chronometer:
  def __init__(self):
    self.name = None
    self.id = 0
    self.selectionStrategy = None
    self.sendStrategy = None
    self.inactivityWindow = 5
    self.txThreshold = -100
    self.bluetoothDebug = False
    self.debug = False
    self.orderToExecute = None
