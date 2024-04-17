pipeline {
    agent {label 'sfaxit-agent-1'}

    stages {
        stage('Build') {
            steps {
                bat 'make'
                archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
            }
        }
    }
}