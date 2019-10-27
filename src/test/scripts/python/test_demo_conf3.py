#!python3
from api.chronometer import getChronometerByName, updateChronometer

chrono = getChronometerByName('Raspberry-2')
data = '{"bluetoothDebug": false, "debug": true, "id": ' + str(
    chrono['id']) + ',"inactivityWindow": "PT2S", "name": "Raspberry-2", "selectionStrategy": "PROXIMITY", "sendStrategy": "ASYNC"}'
updateChronometer(chrono['id'], data)
