#!python3

from rest import get


class Chronometer:
  def __init__(self, name, host):
    self.name = name
    self.restApiUrl = host + '/rest/chronometers'
    self.id = self.getChronometerByName(name)['id']

  def getChronometerByName(self, name):
    "This gets a Chronometer by name and returns a json"
    url = self.restApiUrl + '/name?name=' + name
    response = get(url)
    return response