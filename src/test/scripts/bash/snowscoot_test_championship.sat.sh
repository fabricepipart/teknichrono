#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"

set -e 
export TEKNICHRONO_HOST=${1:-localhost:8080}

python3 $DIR/../python/test_clean.py
python3 $DIR/../python/test_snowscoot_championship.sat.py
