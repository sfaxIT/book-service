pipeline {
    agent {label 'sfaxit-agent-1'}

    stages {
        stage('Build') {
        when {
            branch "sfaxit-*"
        }
        steps {
                bat 'mvn clean install'
                archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
            }
        }
    }
}