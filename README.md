# Teknichrono

* [Project home](https://github.com/fabricepipart/teknichrono)
* [Alternative home](https://bitbucket.org/trdteam/teknichrono/overview)
* [Staging](http://teknichrono-fabrice-pipart-stage.b542.starter-us-east-2a.openshiftapps.com/)
* [Prod](http://frontend-fabrice-pipart-run.b542.starter-us-east-2a.openshiftapps.com/)
* [Jenkins](https://jenkins.openshift.io/job/fabricepipart/job/teknichrono/)
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

### How to run locally

* You may need to adapt ```src/main/resources/project-defaults.yaml```
* Start the server:
  * Normally : ```mvn thorntail:run``` 
  * With MariaDB Datasource : ```java -jar target/teknichrono-thorntail.jar -Smariadb```
  * Package ```mvn clean package``` and run ```java -jar target/teknichrono-thorntail.jar -Sh2```
  * In debug mode : ```mvn thorntail:run -Dswarm.debug.port=5555```
  * With debug logs : ```mvn thorntail:run -Dswarm.logging=DEBUG```
* Run the E2E tests : ```./src/test/scripts/bash/moto_tests.sh``` (or any bash script in this folder)

### How to run on OpenShift

* ```oc login https://api.starter-us-east-2a.openshift.com --token=******```
* ```mvn clean install -P openshift -Dimage.namespace=teknichrono-staging```
* ```mvn -B fabric8:apply -P openshift -Dimage.namespace=teknichrono-staging```

## CI Build

### Kubernetes build images
The project relies on the Kubernetes plugin to run its CI in Jenkins.
The podTemplate referenced here has the following settings:
```
  podTemplate(label:label , cloud: 'openshift', serviceAccount:'jenkins', containers: [	
    containerTemplate(name: 'maven', image: 'maven:3.6-jdk-8-alpine',	ttyEnabled: true, command: 'cat'),	
    containerTemplate(name: 'python', image: 'docker.io/python:3.6-slim',	ttyEnabled: true, command: 'cat')]) {
      ...
  }
```
I did not provide the configuration inline because, I integrated a Nexus proxy to speed up the builds in my case. It requires providing a custom build image in order to customize the ```settings.xml``` .

### Interact with Raspberry

See [the client documentation](src/main/client/Readme.md)

### How to re-generate scaffhold:

* Delete ```src/main/webapp```
* In Ecipse run Forge (Command + ' or Ctrl + 4) with default settings and AngularJS

### Services

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
  * https://github.com/jenkinsci/kubernetes-plugin
* Database
  * https://webmasters.stackexchange.com/questions/2242/how-to-create-separate-users-in-phpmyadmin-each-one-cant-see-others-databases
* Python
  * https://stackoverflow.com/questions/9190169/threading-and-information-passing-how-to
  * https://pymotw.com/2/threading/