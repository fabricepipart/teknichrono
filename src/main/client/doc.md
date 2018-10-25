

# Install

sudo apt-get install bluetooth libbluetooth-dev
sudo python3 -m pip install pybluez


# Start

ssh-copy-id pi@192.168.69.32
scp -r src/main/client pi@192.168.69.32:/home/pi/scripts/ ; ssh pi@192.168.69.32 '/home/pi/scripts/client/start.sh'