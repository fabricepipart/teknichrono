import os
import logging

LOGS_PATH = os.getenv('LOGS_PATH', os.getenv('TEKNICHRONO_HOME', '/home/pi/chrono') + '/teknichrono-data/logs')


def setupBackupFile():
  return open(LOGS_PATH + '/all_pings.log', 'a', buffering=1)


def setupLogging(debug, btDebug):
  # set up logging to file
  fh = logging.FileHandler(LOGS_PATH + '/teknichrono.log')
  # create console handler with a higher log level
  ch = logging.StreamHandler()
  # Set log levels
  if debug:
    fh.setLevel(logging.DEBUG)
    ch.setLevel(logging.DEBUG)
    logging.getLogger('').setLevel(logging.DEBUG)
  else:
    fh.setLevel(logging.INFO)
    ch.setLevel(logging.INFO)
    logging.getLogger('').setLevel(logging.INFO)
  if btDebug:
    logging.getLogger('BT').setLevel(logging.DEBUG)
  else:
    logging.getLogger('BT').setLevel(logging.INFO)
  # create formatter and add it to the handlers
  formatter = logging.Formatter('%(asctime)s %(name)-20s %(levelname)-8s %(message)s')
  formatter.default_msec_format = '%s.%03d'
  ch.setFormatter(formatter)
  fh.setFormatter(formatter)
  # add the handlers to logger
  logging.getLogger('').handlers = []
  logging.getLogger('').addHandler(ch)
  logging.getLogger('').addHandler(fh)
