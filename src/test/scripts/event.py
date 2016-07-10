#!/usr/bin/python

from base import *

EventsUrl = 'http://localhost:8080/teknichrono/rest/events'

# ----------------------------------------------------------------------

def addEvent( name, start, end, loop=True ):
  "This adds a Event"
  data = '{"name":"' + name + '", "start":"' + str(start) + '", "end":"' + str(end) + '", "loop":"' + str(loop) + '"}'
  eventResponse = post(data, EventsUrl);
  print "Event " + name + " added"
  return;

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

# ----------------------------------------------------------------------
