#!/bin/bash

DIR="$( cd "$( dirname "$0" )" && pwd )"

export REST_SERVER_STAGING="frontend-fabrice-pipart-stage.b542.starter-us-east-2a.openshiftapps.com"
export REST_SERVER_PRD="frontend-fabrice-pipart-run.b542.starter-us-east-2a.openshiftapps.com"

export TEKNICHRONO_SERVER=$REST_SERVER_STAGING
export CHRONO_NAME="Raspberry-0"
# FIRST / MID / HIGH / LAST
export PING_SELECTION_STRATEGY="FIRST"
# NONE / STORE / RESEND / SYNC / ASYNC
export PING_SEND_STRATEGY="SYNC"
# Time after which we can take the MID or HIGH value of teh window
export WINDOW="5"
# Time during which we won't take a new value if it comes
export INACTIVITY_WINDOW="5"
export TEKNICHRONO_BT_DEBUG='false'
export TEKNICHRONO_DEBUG='false'

sudo -E python3 $DIR/trd.py