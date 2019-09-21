#!python3

from threading import Thread, Event
import multiprocessing
import logging
import time
import datetime
from collections import deque

from ping_senders.pinger import Pinger


class SendAsyncStrategy(Thread):

  MAX_PINGS_TO_SEND = 100
  WAIT_BEFORE_RETRY = 10

  def __init__(self, server, chronoId):
    super(SendAsyncStrategy, self).__init__()  # super() will call Thread.__init__ for you
    self.logger = logging.getLogger('SendStrategy')
    self.pinger = Pinger(server, chronoId)
    self.lastFailure = 0
    self.q = deque()
    self.exit = Event()

  def append(self, toSend):
    self.q.append(toSend)

  def pop(self):
    fromQueue = None
    if len(self.q) > 0:
      fromQueue = self.q.popleft()
    return fromQueue

  def stop(self):
    self.exit.set()

  def run(self):
    while not self.exit.is_set():
      self.exit.wait(1)
      toSend = []
      if self.lastFailure + self.WAIT_BEFORE_RETRY < datetime.datetime.now().timestamp():
        while len(toSend) < self.MAX_PINGS_TO_SEND and len(self.q) > 0:
          fromQueue = self.pop()
          if fromQueue:
            toSend.append(fromQueue)
      if toSend:
        self.sendAll(toSend)

  def sendAll(self, toSend):
    try:
      self.pinger.sendAll(toSend)
    except Exception as e:
      print(e)
      self.lastFailure = datetime.datetime.now().timestamp()
      failureMessage = 'Could not send for the moment Pings :'
      for oneToSend in toSend:
        self.append(oneToSend)
        failureMessage += ('\n\t' + str(oneToSend))
      self.logger.error(failureMessage)
