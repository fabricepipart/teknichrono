#!python3

from tests.snowscoot.championship import ChampionshipTest

print("-------------------------------------")
print("Pre-event")
print("-------------------------------------")
championship = ChampionshipTest('Snowscoot championship')
championship.prepare()

print("-------------------------------------")
print("Thursday evening")
print("-------------------------------------")
championship.testThursdayEvening()

print("-------------------------------------")
print("Friday morning")
print("-------------------------------------")
championship.testFridayMorning()

print("-------------------------------------")
print("Friday afternoon")
print("-------------------------------------")
championship.testFridayAfternoon()

print("-------------------------------------")
print("Saturday morning")
print("-------------------------------------")
championship.testSaturdayMorning()

print("-------------------------------------")
print("Saturday afternoon")
print("-------------------------------------")
championship.testSaturdayAfternoon()

print("-------------------------------------")
print("Sunday morning")
print("-------------------------------------")
championship.testSundayMorning()
