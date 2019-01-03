#!/bin/bash

DIR="$( cd "$( dirname "$0" )" && pwd )"

export REST_SERVER_STAGING="frontend-fabrice-pipart-stage.b542.starter-us-east-2a.openshiftapps.com"
export REST_SERVER_PRD="frontend-fabrice-pipart-run.b542.starter-us-east-2a.openshiftapps.com"
export REST_SERVER_IO="teknichrono-fabrice-pipart.b542.starter-us-east-2a.openshiftapps.com"

export TEKNICHRONO_SERVER=$REST_SERVER_IO
export CHRONO_NAME="Raspberry-0"
# FIRST / (MID) / HIGH / LAST
export PING_SELECTION_STRATEGY="HIGH"
# NONE / STORE / RESEND / SYNC / ASYNC
export PING_SEND_STRATEGY="SYNC"
# Minimum duration between the last ping to consider it is a new ping 
export INACTIVITY_WINDOW="10"
export TEKNICHRONO_BT_DEBUG='false'
export TEKNICHRONO_DEBUG='false'

sudo -E python3 $DIR/trd.py