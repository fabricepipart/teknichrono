import os
import logging
from restapi_logging_handler import RestApiHandler

LOGS_PATH = os.getenv('LOGS_PATH', os.getenv('TEKNICHRONO_HOME', '/home/pi/chrono') + '/teknichrono-data/logs')


def setupBackupFile():
  return open(LOGS_PATH + '/all_pings.log', 'a', buffering=1)


def setupLogging(chronometer, server):
  debug = False
  btDebug = False
  sendLogs = False
  if chronometer:
    debug = chronometer.debug
    btDebug = chronometer.bluetoothDebug
    sendLogs = chronometer.sendLogs
  logger = logging.getLogger('')
  # set up logging to file
  fh = logging.FileHandler('{}/teknichrono.log'.format(LOGS_PATH))
  # create console handler with a higher log level
  ch = logging.StreamHandler()
  # Set log levels
  if debug:
    fh.setLevel(logging.DEBUG)
    ch.setLevel(logging.DEBUG)
    logger.setLevel(logging.DEBUG)
  else:
    fh.setLevel(logging.INFO)
    ch.setLevel(logging.INFO)
    logger.setLevel(logging.INFO)
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
  logger.handlers = []
  logger.addHandler(ch)
  logger.addHandler(fh)
  if sendLogs:
    restApiUrl = '{server}/rest/logs/create?chronoId={cid}'.format(server=server, cid=chronometer.id)
    #restapiHandler = RestApiHandler(restApiUrl, 'text')
    restapiHandler = RestApiHandler(restApiUrl)
    logger.addHandler(restapiHandler)