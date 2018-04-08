#!/bin/sh
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

set -e

$DIR/moto_tests.sh
$DIR/snowscoot_tests.sh