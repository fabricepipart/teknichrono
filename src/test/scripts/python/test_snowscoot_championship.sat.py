#!python3

from tests.snowscoot.championship import ChampionshipTest

print("-------------------------------------")
print("Pre-event")
print("-------------------------------------")
championship = ChampionshipTest('Snowscoot championship - Sat', 200)
championship.prepare()
championship.testThursdayEvening()

print("-------------------------------------")
print("Saturday morning")
print("-------------------------------------")
championship.testSaturdayMorning()

print("-------------------------------------")
print("Saturday afternoon")
print("-------------------------------------")
championship.testSaturdayAfternoon()
