#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"

set -e

export PYTHONIOENCODING=UTF-8
export TEKNICHRONO_HOST=${1}

mkdir -p test_results

echo "\nPreparing tests ..."

python3 $DIR/../python/test_clean.py > test_results/0_clean.log
python3 $DIR/../python/create_beacons.py > test_results/0_create_beacons.log

python3 $DIR/../python/test_moto_loop_tt_1.py &> test_results/test1.log &
P1=$!

python3 $DIR/../python/test_moto_loop_tt_n.py &> test_results/test2.log &
P2=$!

python3 $DIR/../python/test_moto_loop_race_1.py &> test_results/test3.log &
P3=$!

python3 $DIR/../python/test_snowscoot.py &> test_results/test4.log  &
P4=$!

#python3 $DIR/../python/test_snowscoot_championship.py > test_results/test5.log &
#P5=$!

python3 $DIR/../python/test_snowscoot_championship.fri.py &> test_results/test5.log &
P5=$!

python3 $DIR/../python/test_snowscoot_championship.sat.py &> test_results/test6.log &
P6=$!

python3 $DIR/../python/test_snowscoot_championship.sun.py &> test_results/test7.log &
P7=$!

echo "Running tests...\n"


wait $P1 || cat test_results/test1.log
echo "Test 1 succeeded"

wait $P2 || cat test_results/test2.log
echo "Test 2 succeeded"

wait $P3 || cat test_results/test3.log
echo "Test 3 succeeded"

wait $P4 || cat test_results/test4.log
echo "Test 4 succeeded"

wait $P5 || cat test_results/test5.log
echo "Test 5 succeeded"

wait $P6 || cat test_results/test6.log
echo "Test 6 succeeded"

wait $P7 || cat test_results/test7.log
echo "Test 7 succeeded"
