from ping_selectors.select_first import SelectFirstStrategy
from ping_selectors.select_last import SelectLastStrategy
from ping_selectors.select_high import SelectHighStrategy
from ping_selectors.select_proximity import SelectProximityStrategy


def getSelectionStrategy(key, chronometer):
  switcher = {
      'FIRST': SelectFirstStrategy(chronometer),
      'LAST': SelectLastStrategy(chronometer),
      'HIGH': SelectHighStrategy(chronometer),
      'PROXIMITY': SelectProximityStrategy(chronometer),
  }
  # Get the function from switcher dictionary
  return switcher.get(key)
