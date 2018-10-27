#!python3

from rest import post, formatDatetime


class Ping:
  def __init__(self, host):
    self.restApiUrl = 'http://' + host + '/rest/pings'

  def ping(self, dateTime, pilotBeaconId, power, chronoId):
    "This adds a Ping"
    data = {'dateTime': formatDatetime(dateTime), 'power': str(power)}
    url = self.restApiUrl + '/create?chronoId=' + str(chronoId) + '&beaconId=' + str(pilotBeaconId)
    response = post(data, url)
    return response