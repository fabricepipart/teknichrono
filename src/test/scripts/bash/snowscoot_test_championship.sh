#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

set -e 

python3 $DIR/../python/test_clean.py
python3 $DIR/../python/test_snowscoot_championship.py
