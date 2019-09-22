#!python3
from api.chronometer import getChronometerByName, updateChronometer

chrono = getChronometerByName('Raspberry-2')
data = '{"bluetoothDebug": false, "debug": true, "id": ' + str(
    chrono['id']) + ',"inactivityWindow": "PT5S", "name": "Raspberry-2", "selectionStrategy": "FIRST", "sendStrategy": "NONE"}'
updateChronometer(chrono['id'], data)
