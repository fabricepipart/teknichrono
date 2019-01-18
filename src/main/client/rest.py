#!python3

import json
import requests
import logging

HEADERS = {'Content-type': 'application/json'}
LOGGER = logging.getLogger('REST')


def get(url, params=[]):
  "This sends a get to a URL and returns a json"
  response = requests.get(url, params=params, headers=HEADERS, timeout=2.0)
  if not response.ok:
    response.raise_for_status()
  jData = response.json()
  return jData


def post(data, url, params=[]):
  "This posts a json to a URL and returns a json"
  dataString = json.dumps(data, separators=(',', ':'))
  response = requests.post(url, data=dataString, params=params, headers=HEADERS, timeout=2.0)
  if (not response.ok):
    LOGGER.error("Request returned an invalid status. Text output : " + response.text)
    LOGGER.error("To reproduce : curl -X POST " + url + " --data '" + dataString + "' --header \"Content-Type:application/json\" with params " + str(params))
    response.raise_for_status()
  return response


def formatDatetime(d):
  "This formats a datetime in a format that can be understood on JAX RS side"
  string = d.strftime("%Y-%m-%dT%H:%M:%S")
  return "%s.%03dZ" % (string, d.microsecond / 1000.0)