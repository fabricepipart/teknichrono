from datetime import datetime
from api.session import addSession
from api.event import addSessionToEvent, addEvent

event = addEvent('event11')
session = addSession('testit11', datetime(2000, 1, 2, 10, 5), datetime(2000, 1, 2, 10, 10), 'rc')

#addSessionToLocation(31259, session['id'])
addSessionToEvent(event['id'], session['id'])
#addChronometerToSession(session['id'], 31254)