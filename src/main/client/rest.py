#!python3

import json
import requests

HEADERS = {'Content-type': 'application/json'}


def get(url, params=[]):
  "This sends a get to a URL and returns a json"
  response = requests.get(url, params=params, headers=HEADERS)
  if not response.ok:
    response.raise_for_status()
  jData = response.json()
  return jData
