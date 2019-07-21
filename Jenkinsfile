timeout(60) {

  String STAGING_HOST = "teknichrono-teknichrono-staging.router.default.svc.cluster.local"
  String STAGING_HOST_EXT = "teknichrono-staging.h-y.fr"
  String HOST_DOT_COM = "teknichrono.hand-you.com"
  String HOST_DOT_FR = "teknichrono.h-y.fr"

  podTemplate(cloud: 'openshift', label:'all-in-one', containers: [
      containerTemplate(name: 'jnlp', image: 'fabricepipart/all-in-one-build-image:1.0.0', 
        envVars: [envVar(key: 'MAVEN_MIRROR_URL', value: "http://nexus.ci:8081/nexus/content/groups/public/")],
        args: '${computer.jnlpmac} ${computer.name}')
    ]) {
    node('all-in-one') {
      try {

        checkout scm
        def version = "2.0." + (env.BRANCH_NAME.equals("master") ? '' : "0-${env.BRANCH_NAME.replaceAll('[^A-Za-z0-9]+', '-')}.") + env.BUILD_NUMBER

        stage('Version') {
          currentBuild.description = version
          sh "mvn -B -U versions:set -DnewVersion=${version}"
        }

        stage('Build') {
          sh "mvn -B -U clean verify"
        }

        stage('Post build'){
          parallel 'CodeCov': {
            withCredentials([string(credentialsId: 'CODECOV_TOKEN', variable: 'CODECOV_TOKEN')]) {
              echo "NB: Codecov requires curl and git"
              sh "curl -s https://codecov.io/bash > codecov.sh"
              sh "bash codecov.sh"
            }
          },
          'Package': {
            openshift.withCluster() {
              openshift.withProject('teknichrono-staging'){
                openshift.delete("all", "-l", "app=teknichrono-staging", "--ignore-not-found=true")
              }
              openshift.withProject('ci'){
                openshift.delete("bc/teknichrono", "--ignore-not-found=true")
                openshift.newBuild("--binary", "--name", "teknichrono", "--to=teknichrono:${version}")
                openshift.patch("bc/teknichrono", '\'{"spec":{"strategy":{"dockerStrategy":{"dockerfilePath":"src/main/docker/Dockerfile"}}}}\'')
                openshift.startBuild("teknichrono", "--from-dir=.", "--follow")
              }
            }
          }
        }

        stage('Start staging'){
          openshift.withCluster() {
            openshift.withProject('teknichrono-staging'){
              openshift.newApp("--image-stream=ci/teknichrono:${version}", "--name=teknichrono-staging")
              if(!openshift.selector("route", "teknichrono-staging-route").exists()){
                openshift.expose("service", "teknichrono-staging", "--name=teknichrono-staging-route", "--port=8080", "--hostname=${STAGING_HOST}")
              }
              if(!openshift.selector("route", "teknichrono-staging-route-ext").exists()){
                openshift.expose("service", "teknichrono-staging", "--name=teknichrono-staging-route-ext", "--port=8080", "--hostname=${STAGING_HOST_EXT}")
              }
            }
          }
          sh "while ! curl -fs http://${STAGING_HOST} > /dev/null; do echo 'Not started yet ...'; sleep 5; done"
        }

        stage('End to End tests'){
          sh "python3 -m pip install --user --no-cache-dir -r requirements.txt"
          sh "./src/test/scripts/bash/all_tests.sh ${STAGING_HOST}"
        }
        
        stage('Production'){
          if(env.BRANCH_NAME.equals("master")){
            echo "Deploying to production"
            openshift.withCluster() {
              openshift.withProject('teknichrono'){
                openshift.newApp("--image-stream=ci/teknichrono:${version}", "--name=teknichrono")
                if(!openshift.selector("route", "teknichrono").exists()){
                  openshift.expose("service", "teknichrono", "--name=teknichrono", "--port=8080", "--hostname=${HOST_DOT_FR}")
                }
                if(!openshift.selector("route", "teknichrono-dot-com").exists()){
                  openshift.expose("service", "teknichrono", "--name=teknichrono-dot-com", "--port=8080", "--hostname=${HOST_DOT_COM}")
                }
              }
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