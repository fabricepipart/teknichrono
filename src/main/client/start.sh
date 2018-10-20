#!/bin/bash
export REST_SERVER_STAGING="frontend-fabrice-pipart-stage.b542.starter-us-east-2a.openshiftapps.com"
export REST_SERVER_PRD="frontend-fabrice-pipart-run.b542.starter-us-east-2a.openshiftapps.com"
export CHRONO_NAME="Raspberry-0"
export PING_SELECTION_STRATEGY=""
export PING_POST_STRATEGY=""

sudo python /home/pi/scripts/iBeacon/trd.py
