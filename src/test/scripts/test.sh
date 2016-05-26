#!/bin/sh

# Add Beacons
curl -H "Content-Type: application/json" -X POST -d '{"number":1}' http://localhost:8080/teknichrono/rest/beacons
curl -H "Content-Type: application/json" -X POST -d '{"number":2}' http://localhost:8080/teknichrono/rest/beacons
curl -H "Content-Type: application/json" -X POST -d '{"number":3}' http://localhost:8080/teknichrono/rest/beacons
curl -H "Content-Type: application/json" -X POST -d '{"number":4}' http://localhost:8080/teknichrono/rest/beacons
curl http://localhost:8080/teknichrono/rest/beacons/number/4
curl http://localhost:8080/teknichrono/rest/beacons

curl -H "Content-Type: application/json" -X POST -d '{"firstName":"Jerome", "lastName":"Rousseau"}' http://localhost:8080/teknichrono/rest/pilots
curl -H "Content-Type: application/json" -X PUT -d '{"id":20,"firstName":"Jerome", "lastName":"Rousseau", "currentBeacon":2}' http://localhost:8080/teknichrono/rest/pilots/20
curl -H "Content-Type: application/json" -X POST -d '{"firstName":"Fabrice", "lastName":"Pipart", "currentBeacon":3}' http://localhost:8080/teknichrono/rest/pilots

# Add Pilots
curl http://localhost:8080/teknichrono/rest/pilots
curl "http://localhost:8080/teknichrono/rest/pilots?firstname=Jerome&lastname=Rousseau"
# Add Raspberries

# Add Event

# Add Chronopoints

# Add pings
