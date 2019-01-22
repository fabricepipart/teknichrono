#!python3

import datetime
import logging


class SendNoneStrategy:
  def __init__(self):
    self.logger = logging.getLogger('SendStrategy')

  def send(self, sendme):
    if sendme is not None:
      self.logger.info('[NOSEND] Ping : ' + str(sendme))
