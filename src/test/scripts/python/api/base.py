#!python3

import os
import requests
import json
from datetime import date, datetime, timedelta
import datetime as dt
import isodate

headers = {'Content-type': 'application/json'}
debug = True

host = 'http://localhost:8080'
specificTarget = os.environ.get('TEKNICHRONO_HOST')
if specificTarget:
  host = 'http://' + specificTarget


def setHost(url):
  global host
  host = url


def post(dataString, url, params=[]):
  "This posts a json to a URL and returns a json"
  if debug:
    print('POST to ' + host + url)
  response = requests.post(host + url, data=dataString, params=params, headers=headers)
  if (not response.ok):
    print("Request returned an invalid status. Text output : " + response.text)
    print("To reproduce : curl -X POST '" + host + url + "' --data '" + dataString + "' --header \"Content-Type:application/json\" with params " + str(params))
    response.raise_for_status()
  return


#curl -X POST http://localhost:8080/rest/sessions --data '{"type":"tt", name":"name", "start":"0", "end":"0"}' --header "Content-Type:application/json"


def put(dataString, url):
  "This send in a PUT a json to a URL"
  if debug:
    print('PUT to ' + host + url)
  response = requests.put(host + url, data=dataString, headers=headers)
  if (not response.ok):
    response.raise_for_status()
  return


def delete(url):
  "This sends a DELETE to a URL"
  if debug:
    print('DELETE to ' + host + url)
  response = requests.delete(host + url, headers=headers)
  if (not response.ok):
    print("Request returned an invalid status. Text output : " + response.text)
    print("To reproduce : curl -X DELETE " + host + url + "' --header \"Content-Type:application/json\"")
    response.raise_for_status()
  return


def get(url, params=[]):
  "This sends a get to a URL and returns a json"
  if debug:
    print('GET to ' + host + url)
  response = requests.get(host + url, params=params, headers=headers)
  if (not response.ok):
    response.raise_for_status()
  jData = json.loads(response.content)
  return jData


def formatDatetime(d):
  "This formats a datetime in a format that can be understood on JAX RS side"
  string = d.strftime("%Y-%m-%dT%H:%M:%S")
  return "%s.%03dZ" % (string, d.microsecond / 1000.0)


def timestampToDate(t):
  "This creates a date from a timestamp in secs from 1970"
  secs = t / 1000
  millisecs = t - (secs * 1000)
  return (datetime.utcfromtimestamp(secs) + timedelta(milliseconds=millisecs))


def pretty_hour(t):
  "This creates a String from a timestamp in secs from 1970"
  dateToPrint = timestampToDate(t)
  return dateToPrint.strftime('%H:%M:%S.%f')[:-3]


def pretty_time_delta(milliseconds):
  minutes, milliseconds = divmod(milliseconds, 60000)
  seconds, milliseconds = divmod(milliseconds, 1000)
  if minutes > 0:
    return '%d:%02d.%03d' % (minutes, seconds, milliseconds)
  elif seconds > 0 or milliseconds > 0:
    return '%d.%03d' % (seconds, milliseconds)
  else:
    return ''


def pretty_time_delta_iso(time_iso):
  return pretty_time_delta(iso_to_millis(time_iso))

def iso_to_millis(time_iso):
  timedeltaObject = isodate.parse_duration(time_iso)
  milliseconds = (timedeltaObject / timedelta(microseconds=1000))
  return milliseconds

def iso_date_to_millis(time_iso):
  dateTimeObject = isodate.parse_datetime(time_iso)
  return int(dateTimeObject.timestamp() * 1000)
    
