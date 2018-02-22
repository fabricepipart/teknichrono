#!/bin/sh
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

python3 $DIR/../python/test.moto.py
python3 $DIR/../python/test.snowscoot.py