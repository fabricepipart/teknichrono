#!python3

from tests.snowscoot.worldmasters import WorldMasters

print("-------------------------------------")
print("Pre-event")
print("-------------------------------------")
championship = WorldMasters('Isola WorldMasters')
championship.prepare()

# Elite
championship.addElitePilot('Bruce', 'Rolfo', 246)

# Open

# Woman

# Junior
championship.addJuniorPilot('Fabrice', 'Pipart', 122)

print("-------------------------------------")
print("Friday morning")
print("-------------------------------------")
championship.prepareFridayMorning()

print("-------------------------------------")
print("Friday afternoon")
print("-------------------------------------")
championship.prepareFridayAfternoon()

print("-------------------------------------")
print("Saturday morning")
print("-------------------------------------")
championship.prepareSaturdayMorning()

print("-------------------------------------")
print("Saturday afternoon")
print("-------------------------------------")
championship.prepareSaturdayAfternoon()

print("-------------------------------------")
print("Sunday morning")
print("-------------------------------------")
championship.prepareSundayMorning()
