#!python3

from api.base import pretty_time_delta, iso_to_millis, iso_date_to_millis

# ----------------------------------------------------------------------


def checkNumberLaps(laps, number):
  print('Checking that we have ' + str(number) + ' laps (actual=' + str(len(laps)) + ')')
  assert len(laps) == number


def checkPilotFilled(laps):
  print('Checking that all laps have a pilot')
  for lap in laps:
    pilot = str(lap['pilot']['firstName']) + ' ' + str(lap['pilot']['lastName'])
    assert pilot


def checkCategory(laps, categoryName):
  print('Checking that all laps have category ' + categoryName)
  for lap in laps:
    category = str(lap['pilot']['lastName'])
    assert categoryName == category


def checkLaptimeFilled(laps, lastsCanBeEmpty=False):
  print('Checking that all laps have a laptime')
  atLeastOneBeforeWasEmpty = False
  for lap in laps:
    if not lastsCanBeEmpty:
      assert iso_to_millis(lap['duration']) > 0
    else:
      if not 'duration' in lap:
        atLeastOneBeforeWasEmpty = True
      else:
        assert not atLeastOneBeforeWasEmpty


def checkLaptimeBetween(laps, fromMillis, toMillis):
  print('Checking that all laptimes are between ' + pretty_time_delta(fromMillis) + ' and ' + pretty_time_delta(toMillis))
  for lap in laps:
    assert iso_to_millis(lap['duration']) <= toMillis, 'Lap duration ' + lap['duration'] + ' is not < to ' + str(toMillis)
    assert iso_to_millis(lap['duration']) >= fromMillis, 'Lap duration ' + lap['duration'] + ' is not > to ' + str(fromMillis)


def checkCountWithLapIndex(laps, index, count):
  print('Checking that ' + str(count) + ' laps have a lap index of ' + str(index))
  increasingCount = 0
  for lap in laps:
    lapIndex = str(lap['lapIndex'])
    if lapIndex == str(index):
      increasingCount += 1
  assert increasingCount == count


def checkCountWithLapNumber(laps, number, count):
  print('Checking that ' + str(count) + ' laps have a lap number of ' + str(number))
  increasingCount = 0
  for lap in laps:
    lapNumber = str(lap['lapNumber'])
    if (lapNumber == str(number)):
      increasingCount += 1
  assert increasingCount == count


def checkDeltaBestInIncreasingOrder(laps, lastsCanBeEmpty=False):
  print('Checking that all laps have an increasing delta with best')
  firstLapEvaluated = False
  maxFound = 0
  lastLapDuration = 0
  atLeastOneBeforeWasEmpty = False
  for lap in laps:
    if firstLapEvaluated:
      if not lastsCanBeEmpty:
        gapWithBest = iso_to_millis(lap['gapWithBest'])
        if gapWithBest == maxFound:
          assert lastLapDuration == iso_to_millis(lap['duration'])
        else:
          assert gapWithBest > maxFound
      else:
        if not 'duration' in lap:
          atLeastOneBeforeWasEmpty = True
        else:
          gapWithBest = iso_to_millis(lap['gapWithBest'])
          if gapWithBest == maxFound:
            assert lastLapDuration == iso_to_millis(lap['duration'])
          else:
            assert gapWithBest > maxFound
            assert not atLeastOneBeforeWasEmpty
      maxFound = gapWithBest
    firstLapEvaluated = True
    if 'duration' in lap:
      lastLapDuration = iso_to_millis(lap['duration'])


def checkStartsOrdered(laps):
  print('Checking that all laps have increasing start dates')
  maxFound = 0
  for lap in laps:
    start = iso_date_to_millis(lap['startDate'])
    assert start >= maxFound
    maxFound = start


def checkEndsOrdered(laps):
  print('Checking that all laps have increasing end dates')
  maxFound = 0
  for lap in laps:
    start = iso_date_to_millis(lap['endDate'])
    assert start >= maxFound
    maxFound = start


def checkDeltaPreviousFilled(laps, lastsCanBeEmpty=False):
  print('Checking that all laps have a delta with previous')
  firstLapEvaluated = False
  lastLapDuration = 0
  atLeastOneBeforeWasEmpty = False
  for lap in laps:
    if firstLapEvaluated:
      if not lastsCanBeEmpty:
        if lastLapDuration == iso_to_millis(lap['duration']):
          assert iso_to_millis(lap['gapWithPrevious']) == 0
        else:
          assert iso_to_millis(lap['gapWithPrevious']) > 0
      else:
        if not 'gapWithPrevious' in lap:
          duration = 0
          if 'duration' in lap:
            duration = iso_to_millis(lap['duration'])
          if lastLapDuration != duration:
            atLeastOneBeforeWasEmpty = True
        else:
          assert not atLeastOneBeforeWasEmpty
    firstLapEvaluated = True
    if 'duration' in lap:
      lastLapDuration = iso_to_millis(lap['duration'])


def checkLaps(laps, total, indexMap, numberMap, category=None, durationFrom=None, durationTo=None):
  checkNumberLaps(laps, total)
  for index, count in indexMap.items():
    checkCountWithLapIndex(laps, index, count)
  for number, count in numberMap.items():
    checkCountWithLapNumber(laps, number, count)
  if category != None:
    checkCategory(laps, category)
  checkPilotFilled(laps)
  checkLaptimeFilled(laps)
  checkStartsOrdered(laps)
  checkEndsOrdered(laps)
  if durationFrom and durationTo:
    checkLaptimeBetween(laps, durationFrom, durationTo)


def checkBestLaps(laps, total, indexMap, numberMap, category=None, durationFrom=None, durationTo=None):
  checkNumberLaps(laps, total)
  for index, count in indexMap.items():
    checkCountWithLapIndex(laps, index, count)
  for number, count in numberMap.items():
    checkCountWithLapNumber(laps, number, count)
  if category != None:
    checkCategory(laps, category)
  checkPilotFilled(laps)
  checkLaptimeFilled(laps)
  checkDeltaBestInIncreasingOrder(laps)
  checkDeltaPreviousFilled(laps)
  if durationFrom and durationTo:
    checkLaptimeBetween(laps, durationFrom, durationTo)


def checkResults(laps, total, indexMap, numberMap, category=None, durationFrom=None, durationTo=None):
  checkNumberLaps(laps, total)
  for index, count in indexMap.items():
    checkCountWithLapIndex(laps, index, count)
  for number, count in numberMap.items():
    checkCountWithLapNumber(laps, number, count)
  checkPilotFilled(laps)
  checkLaptimeFilled(laps, True)
  checkDeltaBestInIncreasingOrder(laps, True)
  checkDeltaPreviousFilled(laps, True)
  if durationFrom and durationTo:
    checkLaptimeBetween(laps, durationFrom, durationTo)