#!python3

from tests.snowscoot.championship import ChampionshipTest

print("-------------------------------------")
print("Pre-event")
print("-------------------------------------")
championship = ChampionshipTest('Snowscoot championship - Sun')
championship.prepare()
championship.testThursdayEvening()

print("-------------------------------------")
print("Sunday morning")
print("-------------------------------------")
championship.testSundayMorning()
