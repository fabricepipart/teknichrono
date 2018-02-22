#!python3

from ping import ping
from datetime import datetime
from random import randint


def testPing(year=2018, month=1, day=1, hour=12, min=12, sec=12, pilotBeaconId=1, chronoId=0):
  ping(datetime(year, month, day, hour, min, sec, randint(0, 100000)), pilotBeaconId, randint(-100, -70), chronoId)
