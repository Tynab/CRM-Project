pipeline {
    agent any
    
    stages {
        stage('Build') {
            steps {
                sh 'docker build -t crm:latest .'
            }
        }

        stage('Cleaning') {
            steps {
                script {
                    def containerId = sh(returnStdout: true, script: 'docker ps -aqf "name=crm"').trim()
                    if (containerId) {
                        sh "docker stop $containerId"
                        sh "docker rm $containerId"
                    }
                }
            }
        }

        stage('Run') {
            steps {
                sh 'docker run --name crm --network yan --restart=unless-stopped -d crm:latest'
            }
        }
    }
}
