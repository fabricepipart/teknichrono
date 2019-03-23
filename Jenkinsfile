timeout(60) {

  def version = "1.1." + (env.BRANCH_NAME.equals("master") ? '' : "0-${env.BRANCH_NAME}.") + env.BUILD_NUMBER
  currentBuild.description = version


  String label = "teknichrono-${version}"
  /*
  podTemplate(label:label , cloud: 'openshift', serviceAccount:'jenkins', containers: [
    containerTemplate(name: 'jnlp', image: 'docker.io/openshift/jenkins-slave-base-centos7:v3.11',
      args: '${computer.jnlpmac} ${computer.name}', workingDir: '/home/jenkins/'),
    containerTemplate(name: 'mvn', image: 'docker.io/openshift/jenkins-agent-maven-35-centos7:v3.11', ttyEnabled: true, command: 'cat'),
    containerTemplate(name: 'python', image: 'docker.io/python:3.6-slim', ttyEnabled: true, command: 'cat')]) {
*/
  podTemplate(label:label , cloud: 'openshift', serviceAccount:'jenkins', containers: [
    containerTemplate(name: 'jnlp', image: 'docker.io/openshift/jenkins-agent-maven-35-centos7:v3.11', args: '${computer.jnlpmac} ${computer.name}', workingDir: '/home/jenkins/'),
    containerTemplate(name: 'python', image: 'docker.io/python:3.6-slim', ttyEnabled: true, command: 'cat')]) {
    node(label) {
      
      checkout scm
    
      stage('Build and Deploy') {
        sh "mvn -B -U versions:set -DnewVersion=${version}"
        sh "mvn clean -B -e -U install -P openshift"
      }
      
      stage('Start staging'){
        sh "mvn thorntail:start"
      }

      stage('End to End tests'){
        container('python') {
          sh "python -m pip install --user --no-cache-dir -r requirements.txt"
          sh "./src/test/scripts/bash/all_tests.sh localhost:8080"
        }
      }
      
      stage('Stop staging'){
        sh "mvn thorntail:stop"
      }
    
      stage('Deploy Production'){
        echo "Not yet"
      }

    }
  }














/*
  String label = "teknichrono-${version}"
  String buildLabel = label+"-build"
  podTemplate(label:buildLabel , cloud: 'openshift', serviceAccount:'jenkins', containers: [
    containerTemplate(
        name: 'jnlp', image: 'docker.io/openshift/jenkins-agent-maven-35-centos7:v3.11',
        workingDir: '/tmp')]) {
    node(buildLabel) {

    }
  }


  String testLabel = label+"-test"
  podTemplate(label:testLabel , cloud: 'openshift', serviceAccount:'jenkins', containers: [
    containerTemplate(name: 'jnlp', image: 'docker.io/jenkinsci/jnlp-slave:2.62',
      args: '${computer.jnlpmac} ${computer.name}', workingDir: '/home/jenkins/'),
    containerTemplate(name: 'python', image: 'docker.io/python:3.6-slim', workingDir: '/tmp/',
      ttyEnabled: true, command: 'cat')]) {
    node(testLabel) {
      container('python') {
      checkout scm
        stage('End to End tests'){
          sh "python -m pip install --user --no-cache-dir -r requirements.txt"
          sh "./src/test/scripts/bash/all_tests.sh teknichrono-teknichrono.193b.starter-ca-central-1.openshiftapps.com"
        }
      }
    }
  }
*/
}