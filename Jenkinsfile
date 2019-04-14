timeout(60) {

  String STAGING_HOST = "teknichrono-teknichrono-staging.router.default.svc.cluster.local"

  podTemplate(label:'teknichrono' , cloud: 'openshift') {
    node('teknichrono') {
      try {

        checkout scm
      
        container('maven') {
          stage('Version') {
            def version = "1.1." + (env.BRANCH_NAME.equals("master") ? '' : "0-${env.BRANCH_NAME}.") + env.BUILD_NUMBER
            currentBuild.description = version
            sh "mvn -B -U versions:set -DnewVersion=${version}"
          }

          stage('Build') {
            sh "mvn -B -U clean install -Popenshift"
          }

          stage('Start staging'){
            sh "mvn -B fabric8:undeploy -Popenshift -Dfabric8.namespace=teknichrono-staging"
            sh "mvn -B fabric8:apply -Popenshift -Dfabric8.namespace=teknichrono-staging"
            sh "while ! curl -fs http://${STAGING_HOST} > /dev/null; do echo 'Not started yet ...'; sleep 5; done"
          }
        }

        container('python') {
          stage('End to End tests'){
            sh "python -m pip install --user --no-cache-dir -r requirements.txt"
            sh "./src/test/scripts/bash/all_tests.sh ${STAGING_HOST}"
          }
        }
        
        container('maven') {
          
          stage('Promote to Production'){
            parallel Cleanup: {
              sh "mvn -B fabric8:undeploy -Popenshift -Dfabric8.namespace=teknichrono-staging"
            },
            Deploy: {
              if(env.BRANCH_NAME.equals("master")){
                echo "Deploying to production"
              }
              sh "mvn -B fabric8:apply -Popenshift -Dfabric8.namespace=teknichrono"
            }
          }
        }
      }
      catch (e) {
          echo 'Pipeline failed : ' + e
          sleep 10
          throw e
      }
    }
  }

}