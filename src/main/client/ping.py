#!python3

from rest import post, formatDatetime


class Ping:
  def __init__(self, host):
    self.restApiUrl = 'https://' + host + '/rest/pings'

  def ping(self, instant, pilotBeaconId, power, chronoId):
    "This adds a Ping"
    data = {'instant': formatDatetime(instant), 'power': str(power)}
    url = self.restApiUrl + '/create?chronoId=' + str(chronoId) + '&beaconId=' + str(pilotBeaconId)
    response = post(data, url)
    return response