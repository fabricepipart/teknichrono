#!python3

import requests
import json
import datetime
from datetime import date

headers = {'Content-type': 'application/json'}
host = 'http://localhost:8080'
debug = True


def setHost(url):
  global host
  host = url


def post(dataString, url, params=[]):
  "This posts a json to a URL and returns a json"
  if debug:
    print('POST to ' + host + url)
  response = requests.post(host + url, data=dataString, params=params, headers=headers)
  if (not response.ok):
    response.raise_for_status()
  return


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
  return (datetime.datetime.utcfromtimestamp(secs) + datetime.timedelta(microseconds=(millisecs * 1000)))


def pretty_time_delta(milliseconds):
  minutes, milliseconds = divmod(milliseconds, 60000)
  seconds, milliseconds = divmod(milliseconds, 1000)
  if minutes > 0:
    return '%d:%02d.%03d' % (minutes, seconds, milliseconds)
  elif seconds > 0 or milliseconds > 0:
    return '%d.%03d' % (seconds, milliseconds)
  else:
    return ''
