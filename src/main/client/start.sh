#!/bin/bash

DIR="$( cd "$( dirname "$0" )" && pwd )"

export REST_SERVER_STAGING="staging.teknichrono.fr"
export REST_SERVER_PRD="www.teknichrono.fr"

export TEKNICHRONO_SERVER=$REST_SERVER_PRD
export CHRONO_NAME="Raspberry-2"


sudo -E python3 $DIR/trd.py
