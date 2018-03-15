#!python3

from base import *

EventsUrl = '/teknichrono/rest/events'

# ----------------------------------------------------------------------


def addEvent(name):
  "This adds a Event"
  data = '{"name":"' + name + '"}'
  post(data, EventsUrl)
  print("Event " + name + " added")
  eventResponse = getEventByName(name)
  return eventResponse


def getEventByName(name):
  "This gets a Event by name and returns a json"
  url = EventsUrl + '/name'
  params = {'name': name}
  eventResponse = get(url, params)
  return eventResponse


def deleteEvent(id):
  "This deletes a Event by id"
  url = EventsUrl + '/' + str(id)
  delete(url)
  print("Deleted Event id " + str(id))
  return


def getEvents():
  "This gets all Events"
  EventResponse = get(EventsUrl)
  return EventResponse


def deleteEvents():
  "Deletes all Events"
  Events = getEvents()
  for Event in Events:
    deleteEvent(Event['id'])
  return


def addSessionToEvent(eventId, sessionId):
  "Associate Event and Session"
  url = EventsUrl + '/' + str(eventId) + '/addSession?sessionId=' + str(sessionId)
  post('', url)
  print("Associate Event id " + str(eventId) + " and session id " + str(sessionId))
  return


# ----------------------------------------------------------------------
