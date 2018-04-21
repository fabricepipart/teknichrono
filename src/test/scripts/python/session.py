#!python3

from base import *

SessionsUrl = '/rest/sessions'

# ----------------------------------------------------------------------


def addSession(name, start, end, sessionType):
  "This adds a Session"
  data = '{"type":"' + sessionType + '", "name":"' + name + '", "start":"' + formatDatetime(
      start) + '", "end":"' + formatDatetime(end) + '"}'
  post(data, SessionsUrl)
  print("Session " + name + " added")
  sessionResponse = getSessionByName(name)
  return sessionResponse


def getSessionByName(name):
  "This gets a Session by name and returns a json"
  url = SessionsUrl + '/name'
  params = {'name': name}
  sessionResponse = get(url, params)
  return sessionResponse


def deleteSession(id):
  "This deletes a Session by id"
  url = SessionsUrl + '/' + str(id)
  delete(url)
  print("Deleted Session id " + str(id))
  return


def getSessions():
  "This gets all Sessions"
  SessionResponse = get(SessionsUrl)
  return SessionResponse


def deleteSessions():
  "Deletes all Sessions"
  Sessions = getSessions()
  for Session in Sessions:
    deleteSession(Session['id'])
  return


def addChronometerToSession(sessionId, chronoId, index=-1):
  "Associate Session and Chrono"
  url = SessionsUrl + '/' + str(sessionId) + '/addChronometer?chronoId=' + str(chronoId)
  if index >= 0:
    url = url + '&index=' + str(index)
  post('', url)
  print("Associate Session id " + str(sessionId) + " and chrono id " + str(chronoId))
  return


def addPilotToSession(sessionId, pilotId):
  "Associate Session and Chrono"
  url = SessionsUrl + '/' + str(sessionId) + '/addPilot?pilotId=' + str(pilotId)
  post('', url)
  print("Associate Session id " + str(sessionId) + " and pilot id " + str(pilotId))
  return


def startSession(sessionId, dateTime):
  "Starts the session"
  data = '{"dateTime":"' + formatDatetime(dateTime) + '"}'
  url = SessionsUrl + '/' + str(sessionId) + '/start'
  post(data, url)
  print("Start the session " + str(sessionId))
  return


# ----------------------------------------------------------------------
