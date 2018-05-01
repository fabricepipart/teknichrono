#!python3

# ----------------------------------------------------------------------


def checkNumberLaps(laps, number):
  print('Checking that we have ' + str(number) + ' laps (actual=' + str(len(laps)) + ')')
  assert len(laps) == number


def checkPilotFilled(laps):
  print('Checking that all laps have a pilot')
  for lap in laps:
    pilot = str(lap['pilot']['firstName']) + ' ' + str(lap['pilot']['lastName'])
    assert pilot


def checkLaptimeFilled(laps, lastsCanBeEmpty=False):
  print('Checking that all laps have a laptime')
  atLeastOneBeforeWasEmpty = False
  for lap in laps:
    if not lastsCanBeEmpty:
      assert lap['duration'] > 0
    else:
      if lap['duration'] == 0:
        atLeastOneBeforeWasEmpty = True
      else:
        assert not atLeastOneBeforeWasEmpty


def checkCountWithLapIndex(laps, index, count):
  print('Checking that ' + str(count) + ' laps have a lap index of ' + str(index))
  increasingCount = 0
  for lap in laps:
    lapIndex = str(lap['lapIndex'])
    if (lapIndex == str(index)):
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
  atLeastOneBeforeWasEmpty = False
  for lap in laps:
    if firstLapEvaluated:
      gapWithBest = lap['gapWithBest']
      if not lastsCanBeEmpty:
        assert gapWithBest > maxFound
      else:
        if gapWithBest == 0:
          atLeastOneBeforeWasEmpty = True
        else:
          assert gapWithBest > maxFound
          assert not atLeastOneBeforeWasEmpty
    firstLapEvaluated = True


def checkDeltaPreviousFilled(laps, lastsCanBeEmpty=False):
  print('Checking that all laps have a delta with previous')
  firstLapEvaluated = False
  atLeastOneBeforeWasEmpty = False
  for lap in laps:
    if firstLapEvaluated:
      if not lastsCanBeEmpty:
        assert lap['gapWithPrevious'] > 0
      else:
        if lap['gapWithPrevious'] == 0:
          atLeastOneBeforeWasEmpty = True
        else:
          assert not atLeastOneBeforeWasEmpty
    firstLapEvaluated = True
