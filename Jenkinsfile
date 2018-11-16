def version = "1.0.${env.BUILD_NUMBER}"

// Uses registry.access.redhat.com/openshift3/jenkins-agent-maven-35-rhel7:v3.11
podTemplate(cloud: 'openshift', label: 'maven') {
  node('maven') {
    checkout scm
    stage('Build qnd Deploy') {
      sh "mvn -B -U versions:set -DnewVersion=${version}"
      sh "mvn clean -B -e -U install -P openshift"
    }
    stage('Rollout'){
      sh "mvn -B fabric8:apply -P openshift"
    }
  }
}

podTemplate(cloud: 'openshift', label: 'python') {
  node('python') {
    container('python') {
    checkout scm
      stage('End to End tests'){
        sh "python -m pip install --user --no-cache-dir -r requirements.txt"
        sh "./src/test/scripts/bash/all_tests.sh teknichrono-teknichrono.193b.starter-ca-central-1.openshiftapps.com"
      }
    }
  }
}