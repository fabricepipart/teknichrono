#!python3

import datetime


class SendNoneStrategy:
  def send(self, sendme):
    print(str(datetime.datetime.now()) + '\t Send Ping : ' + str(sendme))
