from ping_senders.send_none import SendNoneStrategy
from ping_senders.send_async import SendAsyncStrategy


def getSendStrategy(key, server, chronoId):
  switcher = {'NONE': SendNoneStrategy(), 'ASYNC': SendAsyncStrategy(server, chronoId)}
  # Get the function from switcher dictionary
  return switcher.get(key)
