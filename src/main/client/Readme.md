# The client documentation

## Install

```
sudo apt-get install bluetooth libbluetooth-dev
sudo python3 -m pip install pybluez
systemctl info systemd-timesyncd
```

## Connect

```
ssh-copy-id pi@192.168.69.31
ssh-copy-id pi@192.168.69.32
ssh pi@192.168.69.31
```


## Sync code
```
scp -r src/main/client pi@192.168.69.32:/home/pi/scripts/
```

## Start manually
```
ssh pi@192.168.69.32 '/home/pi/scripts/client/start.sh'
```

## Start service

```
sudo nano /lib/systemd/system/teknichrono.service
sudo chmod 644 /lib/systemd/system/teknichrono.service
chmod +x /home/pi/scripts/client/start.sh
sudo systemctl daemon-reload
sudo systemctl enable teknichrono.service
sudo systemctl start teknichrono.service
sudo systemctl restart teknichrono.service
sudo systemctl stop teknichrono.service
sudo systemctl status teknichrono.service
```

## Backup / Restore SD

Backup
```
df -h
sudo diskutil unmount /dev/disk4s6
sudo diskutil unmount /dev/disk4s1
sudo dd if=/dev/rdisk4 of=/Users/fabricepipart/Documents/teknichrono-sd.img bs=1m
sudo diskutil eject /dev/rdisk3
```

Restore
```
df -h
sudo diskutil unmount /dev/disk3s5
sudo diskutil unmount /dev/disk3s1
sudo dd bs=1m if=2018-11-13-raspbian-stretch.img of=/dev/rdisk3 conv=sync
sudo diskutil eject /dev/rdisk3
```

## References

* http://www.diegoacuna.me/how-to-run-a-script-as-a-service-in-raspberry-pi-raspbian-jessie/
* https://www.raspberrypi.org/documentation/installation/installing-images/mac.md

## Bluetooth

```
rfkill unblock all
sudo hciconfig
sudo bluetoothctl
```