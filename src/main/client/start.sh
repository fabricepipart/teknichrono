#!/bin/bash

DIR="$( cd "$( dirname "$0" )" && pwd )"

BASE_FOLDER=$(dirname $DIR)
. $BASE_FOLDER/teknichrono-data/configuration.sh

if [ x$(basename $DIR) != "xteknichrono" ]; then
    echo "Application must be started from a folder named teknichrono"
    exit 1
fi

echo "-- Waiting for server to be reachable"
while ! curl -fs "${TEKNICHRONO_SERVER}"
do
    echo "Waiting for server to be reachable"
    sleep 10
done
echo "Connected to ${TEKNICHRONO_SERVER}"

# --------- Update ---------
echo "-- Checking if new version is available:"
updaterArchive="teknichrono-client-updater.tar.gz"
updaterArchivePath="$BASE_FOLDER/teknichrono-data/$updaterArchive"
curl -fs $TEKNICHRONO_SERVER/downloads/$updaterArchive > $updaterArchivePath
if [ -f "$updaterArchivePath" ]; then
    newMd5=$(openssl md5 $updaterArchivePath | awk '{print $2}')
    currentMd5=''
    if [ -f "$updaterArchivePath".md5 ]; then
        currentMd5=$(cat $updaterArchivePath.md5)
    fi
    if [ "x$newMd5" != "x$currentMd5" ]; then
        echo "New version available ($newMd5), let's update ($currentMd5)"
        tar -zxf $updaterArchivePath -C $BASE_FOLDER/teknichrono-data
        mv $BASE_FOLDER/teknichrono $BASE_FOLDER/teknichrono.old
        mv $BASE_FOLDER/teknichrono-data/teknichrono $BASE_FOLDER/teknichrono
        rm $updaterArchivePath.md5 &> /dev/null || true
        echo $newMd5 > $updaterArchivePath.md5
        chmod +x $BASE_FOLDER/teknichrono/*.sh
        sudo rm -rf $BASE_FOLDER/teknichrono.old 
        echo "Update done"
    else
        echo "Version $currentMd5 is up to date"
    fi
else
    echo "Server is not reachable for udpates"
fi

echo "-- Starting Bluetooth"
#sudo hciconfig hci0 down
#sudo hciconfig hci0 up

echo "-- Checking configuration"
while ! curl -fs "${TEKNICHRONO_SERVER}/rest/chronometers/name?name=${CHRONO_NAME}"
do
    echo "Chronometer ${CHRONO_NAME} is not registered @ ${TEKNICHRONO_SERVER}. Waiting ..."
    sleep 10
done
echo "Chronometer ${CHRONO_NAME} configured @ ${TEKNICHRONO_SERVER}. Starting ..."

echo "-- Starting Application"
sudo -E python3 $DIR/trd.py


