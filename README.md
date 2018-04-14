# Teknichrono

* [Project home ](https://github.com/fabricepipart/teknichrono)
* [Alternative home](https://bitbucket.org/trdteam/teknichrono/overview)
* [Entry point](http://teknichrono-fabrice-pipart-stage.b542.starter-us-east-2a.openshiftapps.com/app.html#/Events)
* [OpenShift](https://console.starter-us-east-2a.openshift.com/console/project/fabrice-pipart/browse/pipelines)

## Description

### Purpose of the project

This project is the backend part of the Software: the REST API that sits on a server receiving calls from two types of clients:
* Chronometers that sends the pings to compute the lap times
* End users that consult the lap times or Administrators

### Architecture

The backend part is based on Wildfly that receives API calls implemented with Jax-RS. Persistence is implemented with Hibernate and JPA.
The testing part of the project is based on JUnits (very few) and Python end to end tests that simulate real activity.

## How to use

* You may need to adapt ```src/main/resources/project-defaults.yaml```
* Start the server : ```mvn wildfly-swarm:run```
* Run the E2E tests : ```./src/test/scripts/bash/moto_tests.sh ```

### Services

TODO List of the REST API services here

For the time being, you can have a look in: ```src/main/java/org/trd/app/teknichrono/rest```

## Interesting pointers

* Wildfly
  * wildfly-swarm documentation
    * [create-a-datasource](https://howto.wildfly-swarm.io/create-a-datasource/)
    * [command_line](https://wildfly-swarm.gitbooks.io/wildfly-swarm-users-guide/configuration/command_line.html)
  * JPA
    * https://www.thoughts-on-java.org/best-practices-many-one-one-many-associations-mappings/
    * http://meri-stuff.blogspot.fr/2012/03/jpa-tutorial.html#RelationshipsBidirectionalOneToManyManyToOneConsistency
  * Examples
    * [wildfly-swarm-examples](https://github.com/wildfly-swarm/wildfly-swarm-examples)
* Fabric8
  * [fabric8-pipeline-library](https://github.com/fabric8io/fabric8-pipeline-library)
  * https://github.com/fabric8io/docker-maven-plugin
  * https://dmp.fabric8.io/#build-configuration
  * https://maven.fabric8.io/
* OpenShift io
  * https://github.com/openshiftio/openshift.io/wiki/FAQ
  * https://github.com/openshiftio/openshift.io/issues/891
  * [WildFly booster](https://github.com/openshiftio/booster-parent/blob/master/pom.xml)