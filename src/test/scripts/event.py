#!/usr/bin/python

from base import *
import variables

EventsUrl = '/teknichrono/rest/events'

# ----------------------------------------------------------------------

def addEvent( name, start, end, loop=True ):
  "This adds a Event"
  data = '{"name":"' + name + '", "start":"' + str(start) + '", "end":"' + str(end) + '", "loopTrack":"' + str(loop) + '"}'
  eventResponse = post(data, EventsUrl);
  print "Event " + name + " added"
  return;

def getEventByName( name ):
  "This gets a Event by name and returns a json"
  url = EventsUrl + '/name?name=' + name
  eventResponse = get(url);
  return eventResponse;

def deleteEvent( id ):
  "This deletes a Event by id"
  url = EventsUrl + '/' + str(id)
  delete(url);
  print "Deleted Event id " + str(id)
  return;

def getEvents():
  "This gets all Events"
  EventResponse = get(EventsUrl);
  return EventResponse;

def deleteEvents():
  "Deletes all Events"
  Events = getEvents();
  for Event in Events:
    deleteEvent(Event['id'])
  return;


def addChronometerToEvent(eventId, chronoId, index=-1):
    "Associate Event and Chrono"
    url = EventsUrl + '/' + str(eventId) + '/addChronometer?chronoId=' + str(chronoId)
    if index >= 0:
        url = url + '&index=' + str(index)
    post('', url);
    print "Associate Event id " + str(eventId) + " and chrono id " + str(chronoId)
    return;

# ----------------------------------------------------------------------
