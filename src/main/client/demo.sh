#!/bin/bash

DIR="$( cd "$( dirname "$0" )" && pwd )"

export REST_SERVER_STAGING="https://staging.teknichrono.fr"
export REST_SERVER_PRD="https://www.teknichrono.fr"
export REST_SERVER_LOCAL="http://localhost:8080"

export TEKNICHRONO_SERVER=$REST_SERVER_LOCAL
export CHRONO_NAME="Raspberry-2"

export DEMO_MODE='true'
export LOGS_PATH='./target'
export WAIT_BETWEEN_UPDATES='10'

python3 $DIR/trd.py
