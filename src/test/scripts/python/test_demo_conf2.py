#!python3
from api.chronometer import getChronometerByName, updateChronometer

chrono = getChronometerByName('Raspberry-2')
data = '{"bluetoothDebug": false, "debug": false, "id": ' + str(
    chrono['id']) + ',"inactivityWindow": "PT2S", "name": "Raspberry-2", "selectionStrategy": "LAST", "sendStrategy": "ASYNC"}'
updateChronometer(chrono['id'], data)
