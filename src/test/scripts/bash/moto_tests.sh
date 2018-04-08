#!/bin/sh
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

set -e 

python3 $DIR/../python/test.clean.py
python3 $DIR/../python/test.moto.loop.tt.1.py
python3 $DIR/../python/test.moto.loop.tt.n.py
python3 $DIR/../python/test.moto.loop.race.1.py
#python3 $DIR/../python/test.moto.loop.race.n.py
#python3 $DIR/../python/test.moto.rally.tt.1.py
#python3 $DIR/../python/test.moto.rally.tt.n.py
#python3 $DIR/../python/test.moto.rally.race.1.py
#python3 $DIR/../python/test.moto.rally.race.n.py