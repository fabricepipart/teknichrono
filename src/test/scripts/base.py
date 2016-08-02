#!/usr/bin/python

import requests
import json
import datetime
from datetime import date

headers = {'Content-type': 'application/json'}

def post( dataString, url ):
  "This posts a json to a URL and returns a json"
  response = requests.post(url, data=dataString, headers=headers)
  if(not response.ok):
    response.raise_for_status();
  return;

def put( dataString, url ):
    "This send in a PUT a json to a URL"
    response = requests.put(url, data=dataString, headers=headers)
    if(not response.ok):
      response.raise_for_status();
    return;

def delete(url ):
  "This sends a DELETE to a URL"
  response = requests.delete(url, headers=headers)
  if(not response.ok):
    response.raise_for_status();
  return;

def get(url ):
  "This sends a get to a URL and returns a json"
  response = requests.get(url, headers=headers)
  if(not response.ok):
    response.raise_for_status();
  jData = json.loads(response.content)
  return jData;

def formatDatetime(d):
  "This formats a datetime in a format that can be understood on JAX RS side"
  string = d.strftime("%Y-%m-%dT%H:%M:%S")
  return "%s.%03dZ" % (string, d.microsecond / 1000.0);
