#!python3

from rest import get


class Beacon:
  def __init__(self, number, host):
    self.number = number
    self.restApiUrl = 'http://' + host + '/rest/beacons'
    self.id = self.getBeaconByNumber(number)['id']

  def getBeaconByNumber(self, number):
    "This gets a Beacon by number and returns a json"
    url = self.restApiUrl + '/number/' + str(number)
    response = get(url)
    return response