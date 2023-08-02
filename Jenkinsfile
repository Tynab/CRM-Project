pipeline {
    agent any
    
    stages {
        stage('Build') {
            steps {
                sh 'docker build -t yamiannephilim/crm:latest .'
            }
        }

        stage('Clean') {
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
                sh 'docker container stop crm || echo "this container does not exist"'
                sh 'docker network create yan || echo "this network exist"'
                sh 'echo y | docker container prune'
                sh 'docker run --name crm --network yan --restart=unless-stopped -d yamiannephilim/crm:latest'
            }
        }
    }
}
