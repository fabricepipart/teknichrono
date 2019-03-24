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
    
      stage('Build') {
        sh "mvn -B -U versions:set -DnewVersion=${version}"
        sh "mvn -B -U clean -e install -P openshift"
      }

      stage('Start staging'){
        sh "mvn -B thorntail:start"
      }

      stage('End to End tests'){
        container('python') {
          sh "python -m pip install --user --no-cache-dir -r requirements.txt"
          sh "./src/test/scripts/bash/all_tests.sh localhost:8080"
        }
      }
      
      stage('Stop staging'){
        sh "mvn -B thorntail:stop"
      }
    
      stage('Deploy Production'){
        if(env.BRANCH_NAME.equals("master")){
          echo "Deploying to production"
        }
        sh "mvn -B fabric8:apply -P openshift -D fabric8.namespace=teknichrono"
      }

    }
  }

}