#!python3

import datetime


class SendNoneStrategy:
  def send(self, sendme):
    print(str(datetime.datetime.now()) + '[NOSEND]\tPing : ' + str(sendme))
