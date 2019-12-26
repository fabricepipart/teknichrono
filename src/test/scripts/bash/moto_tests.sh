#!/bin/sh
DIR="$( cd "$( dirname "$0" )" && pwd )"

set -e 

python3 $DIR/../python/test_clean.py
python3 $DIR/../python/create_beacons.py
python3 $DIR/../python/test_moto_loop_tt_1.py
python3 $DIR/../python/test_moto_loop_tt_n.py
python3 $DIR/../python/test_moto_loop_race_1.py
#python3 $DIR/../python/test.moto.loop.race.n.py
#python3 $DIR/../python/test.moto.rally.tt.1.py
#python3 $DIR/../python/test.moto.rally.tt.n.py
#python3 $DIR/../python/test.moto.rally.race.1.py
#python3 $DIR/../python/test.moto.rally.race.n.py