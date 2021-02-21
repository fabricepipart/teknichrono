#!/bin/sh

DIR="$( cd "$( dirname "$0" )" && pwd )"

REST_SERVER_STAGING="https://staging.teknichrono.fr"
REST_SERVER_PRD="https://www.teknichrono.fr"
REST_SERVER_LOCAL="http://localhost:8080"
REST_SERVER_HOME="http://192.168.69.69:8080"

export CHRONO_NAME="Raspberry-2"
export TEKNICHRONO_SERVER=$REST_SERVER_LOCAL
export DEMO_MODE='true'
export LOGS_PATH='./target'
export WAIT_BETWEEN_UPDATES='5'

sh src/test/scripts/bash/demo_test.sh

coverage run --source src/main/client $DIR/trd.py
coverage report -m --omit=src/main/client/scan/bluetooth_scanner.py,src/main/client/scan/blescan.py