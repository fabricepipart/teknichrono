#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"

set -e 
export TEKNICHRONO_HOST=${1:-localhost:8080}

python3 $DIR/../python/test_clean.py
python3 $DIR/../python/create_beacons.py
python3 $DIR/../python/prepare_snowscoot_worldmasters.py
