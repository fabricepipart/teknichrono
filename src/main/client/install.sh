#!/bin/bash
set -e

REST_SERVER_STAGING="https://staging.teknichrono.fr"
REST_SERVER_PRD="https://www.teknichrono.fr"
REST_SERVER_LOCAL="http://localhost:8080"
REST_SERVER_HOME="http://192.168.69.69:8080"

default_server=$REST_SERVER_PRD
default_name="Raspberry-0" 

base_dir=$(pwd)
configurationPath="$base_dir/teknichrono-data/configuration.sh"
if [ -f $configurationPath ]; then
    default_server=$(cat $configurationPath | grep TEKNICHRONO_SERVER |  awk -F= '{print $2}' | tr -d \")
    default_name=$(cat $configurationPath | grep CHRONO_NAME |  awk -F= '{print $2}' | tr -d \")
fi

echo -e "-- Please enter the Chronometer configuration:"
read -p "Server [$default_server]: " server
read -p "Chronometer name [$default_name]: " name
server=${server:-$REST_SERVER_HOME}
name=${name:-$default_name}
echo -e "\033[0;32m\xE2\x9C\x94 Success\033[0m"

echo -e "-- Downloading software from $server in $base_dir"
sudo rm -rf $base_dir/teknichrono || true
mkdir -p $base_dir/teknichrono
mkdir -p $base_dir/teknichrono-data/logs
curl -s "$server/downloads/teknichrono-client-updater.tar.gz" | tar xz
chmod +x $base_dir/teknichrono/start.sh
echo -e "\033[0;32m\xE2\x9C\x94 Success\033[0m"

echo -e "-- Setup OS"
sudo apt-get install bluetooth libbluetooth-dev
systemctl status systemd-timesyncd
echo -e "\033[0;32m\xE2\x9C\x94 Success\033[0m"

echo -e "-- Setup python"
pip3 install -r $base_dir/teknichrono/requirements.txt
sudo pip3 install -r $base_dir/teknichrono/requirements.txt
echo -e "\033[0;32m\xE2\x9C\x94 Success\033[0m"

echo -e "-- Saving configuration"
echo "export TEKNICHRONO_SERVER=\"$server\"" > $base_dir/teknichrono-data/configuration.sh
echo "export CHRONO_NAME=\"$name\"" >> $base_dir/teknichrono-data/configuration.sh
echo "export TEKNICHRONO_HOME=\"$base_dir\"" >> $base_dir/teknichrono-data/configuration.sh
echo -e "\033[0;32m\xE2\x9C\x94 Success\033[0m"

echo -e "-- Setup service"
serviceTempPath=$base_dir/teknichrono-data/teknichrono.service
echo "[Unit]" > $serviceTempPath
echo "Description=Teknichrono Client" >> $serviceTempPath
echo "After=multi-user.target" >> $serviceTempPath
echo "" >> $serviceTempPath
echo "[Service]" >> $serviceTempPath
echo "Type=simple" >> $serviceTempPath
echo "ExecStart=$base_dir/teknichrono/start.sh" >> $serviceTempPath
echo "Restart=always" >> $serviceTempPath
echo "" >> $serviceTempPath
echo "[Install]" >> $serviceTempPath
echo "WantedBy=multi-user.target" >> $serviceTempPath
echo "" >> $serviceTempPath
sudo systemctl stop teknichrono.service
sudo rm /lib/systemd/system/teknichrono.service
sudo cp $serviceTempPath /lib/systemd/system/teknichrono.service
sudo chmod 644 /lib/systemd/system/teknichrono.service
sudo systemctl daemon-reload
sudo systemctl enable teknichrono.service
sudo systemctl start teknichrono.service
echo -e "\033[0;32m\xE2\x9C\x94 Success\033[0m"

echo
echo -e "\033[0;34mInstallation finished\033[0m"
