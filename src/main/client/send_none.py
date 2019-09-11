#!python3

from threading import Thread
import logging
import time


class SendNoneStrategy(Thread):
  def __init__(self):
    self.logger = logging.getLogger('SendStrategy')
    self.alive = True

  def append(self, toSend):
    if toSend is not None:
      self.logger.info('[NOSEND] Ping : ' + str(toSend))

  def stop(self):
    self.alive = False

  def run(self):
    while self.alive:
      time.sleep(1)
