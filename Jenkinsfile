#!/usr/bin/groovy
@Library('github.com/fabric8io/fabric8-pipeline-library@master')
import io.fabric8.Fabric8Commands

def canaryVersion = "1.0.${env.BUILD_NUMBER}"
def utils = new io.fabric8.Utils()
def stashName = "buildpod.${env.JOB_NAME}.${env.BUILD_NUMBER}".replace('-', '_').replace('/', '_')
def envStage = utils.environmentNamespace('stage')
def envProd = utils.environmentNamespace('run')

def flow = new Fabric8Commands()
def cloud = flow.getCloudConfig()




mavenNode {
  checkout scm
  if (utils.isCI()){

    mavenCI{}
    
  } else if (utils.isCD()){
    echo 'NOTE: running pipelines for the first time will take longer as build and base docker images are pulled onto the node'
    container(name: 'maven') {

      stage('Build Release'){
        mavenCanaryRelease {
          version = canaryVersion
        }
        //stash deployment manifests
        stash includes: '**/*.yml', name: stashName
      }

      stage('Rollout to Stage'){
        apply{
          environment = envStage
        }
      }
    }

  }
}

mavenTemplate(label: 'pythontestslabel', mavenImage: 'python:3.6-slim') {
    node('pythontestslabel') {
        checkout scm
        container(name: 'maven') {
          stage('End to End tests'){
            sh "python -m pip install --user --no-cache-dir -r requirements.txt"
            sh "./src/test/scripts/bash/all_tests.sh frontend-fabrice-pipart-stage.b542.starter-us-east-2a.openshiftapps.com"

          }
        }
    }
}

if (utils.isCD()){
  // node {
  //   stage('Approve'){
  //      approve {
  //        room = null
  //        version = canaryVersion
  //        environment = 'Stage'
  //      }
  //    }
  // }

  clientsNode{
    container(name: 'clients') {
      stage('Rollout to Run'){
        unstash stashName
        apply{
          environment = envProd
        }
      }
    }
  }
}