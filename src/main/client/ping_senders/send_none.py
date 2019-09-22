#!python3

from threading import Thread, Event
import logging
import time


class SendNoneStrategy(Thread):
  def __init__(self):
    super(SendNoneStrategy, self).__init__()  # super() will call Thread.__init__ for you
    self.logger = logging.getLogger('SendStrategy')
    self.exit = Event()

  def append(self, toSend):
    if toSend is not None:
      self.logger.info('[NOSEND] Ping : ' + str(toSend))

  def stop(self):
    self.logger.info('Stopping send thread')
    self.exit.set()

  def run(self):
    while not self.exit.is_set():
      self.exit.wait(1)
