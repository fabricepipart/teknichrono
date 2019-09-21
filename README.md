# Teknichrono

[![Build Status](https://travis-ci.org/fabricepipart/teknichrono.svg?branch=master)](https://travis-ci.org/fabricepipart/teknichrono)
[![codecov](https://codecov.io/gh/fabricepipart/teknichrono/branch/master/graph/badge.svg)](https://codecov.io/gh/fabricepipart/teknichrono)
![](https://github.com/fabricepipart/teknichrono/workflows/.github/workflows/workflow.yml/badge.svg)


* [Project home](https://github.com/fabricepipart/teknichrono)
* [Alternative home](https://bitbucket.org/trdteam/teknichrono/overview)

## Description

### Purpose of the project

This project is the backend part of the Software: the REST API that sits on a server receiving calls from two types of clients:

* Chronometers that sends the pings to compute the lap times
* End users that consult the lap times or Administrators

### Architecture

The backend part is based on Quarkus that receives API calls implemented with Jax-RS. Persistence is implemented with Hibernate and JPA.
The testing part of the project is based on JUnits (very few), QuarkusTest and Python end to end tests that simulate real activity.

### Requirements

* Java 8
* Maven 3.6

## How to build

### How to build locally

```mvn clean verify```

### How to build via OpenShift

```bash
oc new-build --binary --name teknichrono-quarkus --to='teknichrono-quarkus:local'
oc patch bc/teknichrono-quarkus -p "{\"spec\":{\"strategy\":{\"dockerStrategy\":{\"dockerfilePath\":\"src/main/docker/Dockerfile\"}}}}"
oc start-build teknichrono-quarkus --from-dir=. --follow
```

### CI Build and Kubernetes build images

The project relies on the Kubernetes plugin to run its CI in Jenkins.
The podTemplate referenced here has the following settings:

```groovy
  podTemplate(label:label , cloud: 'openshift', serviceAccount:'jenkins', containers: [	
    containerTemplate(name: 'maven', image: 'maven:3.6-jdk-8-alpine',	ttyEnabled: true, command: 'cat'),	
    containerTemplate(name: 'python', image: 'docker.io/python:3.6-slim',	ttyEnabled: true, command: 'cat')]) {
      ...
  }
```

I did not provide the configuration inline because, I integrated a Nexus proxy to speed up the builds in my case. It requires providing a custom build image in order to customize the ```settings.xml``` .

## How to run

There are three profiles. See in ```src/main/resources/application.properties```.

### How to run locally with dev profile

```mvn quarkus:dev```

Note: you can connect your IDE for debugging on port 5005.

### How to run locally with localmariadb profile

First:
```oc port-forward mariadb-3-qgvwj 3306:3306```
then
```mvn quarkus:dev -Dquarkus-profile=localmariadb```
or
```java -jar target/teknichrono-runner.jar -Dquarkus-profile=localmariadb```

### How to run on OpenShift

```bash
oc new-app --image-stream=teknichrono-quarkus:local
oc expose service teknichrono-quarkus --name=teknichrono-quarkus-route --port=8080 --hostname=teknichrono-quarkus.h-y.fr
```

## How to test

### JUnit

```mvn clean test```

### QuarkusTest

```mvn clean verify```

### End to End

Run the E2E tests : ```./src/test/scripts/bash/moto_tests.sh``` (or any bash script in this folder)

## Interact with Raspberry

See [the client documentation](src/main/client/Readme.md)

## How to re-generate scaffhold:

* Delete ```src/main/webapp```
* In Ecipse run Forge (Command + ' or Ctrl + 4) with default settings and AngularJS

## Services

TODO List of the REST API services here

For the time being, you can have a look in: ```src/main/java/org/trd/app/teknichrono/rest```

## Interesting pointers

* Thorntail
  * thorntail documentation
    * [create-a-datasource](https://howto.thorntail.io/create-a-datasource/)
    * [command_line](https://thorntail.gitbooks.io/thorntail-users-guide/configuration/command_line.html)
    * [thorntail-examples](https://github.com/thorntail/thorntail-examples)
    * https://docs.thorntail.io/
    * https://docs.thorntail.io/2.2.0.Final/#configuring-a-thorntail-application-using-yaml-files_thorntail
    * https://docs.thorntail.io/2.2.0.Final/#using-thorntail-maven-plugin_thorntail
    * https://github.com/thorntail/thorntail/blob/master/docs/index.adoc
  * JPA
    * https://www.thoughts-on-java.org/best-practices-many-one-one-many-associations-mappings/
    * http://meri-stuff.blogspot.fr/2012/03/jpa-tutorial.html#RelationshipsBidirectionalOneToManyManyToOneConsistency
  * Examples
    * [thorntail-examples](https://github.com/thorntail/thorntail-examples)
* Fabric8
  * [fabric8-pipeline-library](https://github.com/fabric8io/fabric8-pipeline-library)
  * https://github.com/fabric8io/docker-maven-plugin
  * https://dmp.fabric8.io/
  * https://maven.fabric8.io/
* OpenShift io
  * https://github.com/openshiftio/openshift.io/wiki/FAQ
  * https://github.com/openshiftio/openshift.io/issues/891
  * https://github.com/openshiftio/openshift.io/issues/4504
  * [WildFly booster](https://github.com/openshiftio/booster-parent/blob/master/pom.xml)
* GitHub setup
  * https://embeddedartistry.com/blog/2017/12/21/jenkins-kick-off-a-ci-build-with-github-push-notifications
* Jenkins
  * https://github.com/jenkinsci/kubernetes-plugin
  * https://github.com/openshift/jenkins/tree/master/agent-maven-3.5
  * https://github.com/openshift/jenkins-client-plugin
* Database
  * https://webmasters.stackexchange.com/questions/2242/how-to-create-separate-users-in-phpmyadmin-each-one-cant-see-others-databases
* Python
  * https://stackoverflow.com/questions/9190169/threading-and-information-passing-how-to
  * https://pymotw.com/2/threading/

## Quarkus migration todo list
 
 * Proper health check as per https://quarkus.io/guides/health-guide
 * Remote dev mode
