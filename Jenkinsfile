def version = "1.0.${env.BUILD_NUMBER}"
def stashName = "buildpod.${env.JOB_NAME}.${env.BUILD_NUMBER}".replace('-', '_').replace('/', '_')


node { timestamps {
  // This step should not normally be used in your script. Consult the inline help for details.
  podTemplate(cloud: 'openshift', inheritFrom: 'maven', instanceCap: 0, label: 'maven', name: '', namespace: 'teknichrono', nodeSelector: '', podRetention: always(), serviceAccount: '', workspaceVolume: emptyDirWorkspaceVolume(false)) {
      sh "mvn version"
  }

} }

