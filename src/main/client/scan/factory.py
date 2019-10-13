import os
if os.getenv('DEMO_MODE', 'false') == 'false':
  from scan.bluetooth_scanner import BluetoothScanner
else:
  from scan.demo_scanner import FakeBluetoothScanner


def getScanner():
  scanner = None
  if os.getenv('DEMO_MODE', 'false') == 'false':
    scanner = BluetoothScanner()
  else:
    scanner = FakeBluetoothScanner()
  scanner.init()
  return scanner
