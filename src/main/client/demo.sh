#!/bin/bash

DIR="$( cd "$( dirname "$0" )" && pwd )"

export REST_SERVER_STAGING="staging.teknichrono.fr"
export REST_SERVER_PRD="www.teknichrono.fr"


export TEKNICHRONO_SERVER=$REST_SERVER_STAGING
export CHRONO_NAME="Raspberry-2"
# FIRST / (MID) / HIGH / LAST
export PING_SELECTION_STRATEGY="HIGH"
# NONE / STORE / RESEND / SYNC / ASYNC
export PING_SEND_STRATEGY="ASYNC"
# Minimum duration between the last ping to consider it is a new ping 
export INACTIVITY_WINDOW="5"
export TEKNICHRONO_BT_DEBUG='false'
export TEKNICHRONO_DEBUG='true'
export DEMO_MODE='true'
export LOGS_PATH='./target'

python3 $DIR/trd.py
