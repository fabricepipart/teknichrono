#!python3
from api.chronometer import getChronometerByName, updateChronometer

chrono = getChronometerByName('Raspberry-2')
data = '{"bluetoothDebug": false, "debug": true, "id": ' + str(chrono['id']) + ',"name": "Raspberry-2", "orderToExecute": "RESTART"}'
updateChronometer(chrono['id'], data)
