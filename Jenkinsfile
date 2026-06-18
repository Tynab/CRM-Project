pipeline {
    // Tom tat: Pipeline build image CRM, push Docker Hub, restart container va
    // gui thong bao Telegram theo tung stage.
    agent any
    
    environment {
        // Cau hinh Telegram lay tu Jenkins credentials.
        TOKEN = credentials('telegram_token')
        CHAT_ID = credentials('telegram_chatid')

        // Noi dung thong bao duoc tao tu commit hien tai.
        GIT_MESSAGE = sh(returnStdout: true, script: "git log -n 1 --format=%s ${GIT_COMMIT}").trim()
        GIT_AUTHOR = sh(returnStdout: true, script: "git log -n 1 --format=%ae ${GIT_COMMIT}").trim()
        GIT_COMMIT_SHORT = sh(returnStdout: true, script: "git rev-parse --short ${GIT_COMMIT}").trim()
        GIT_INFO = "Branch: ${GIT_BRANCH}\nLast Message: ${GIT_MESSAGE}\nAuthor: ${GIT_AUTHOR}\nCommit: ${GIT_COMMIT_SHORT}"
        TEXT_BREAK = '----------------------------------------'
        TEXT_PRE = "${TEXT_BREAK}\n${GIT_INFO}"
        TEXT_BUILD = "${JOB_NAME} is Building"
        TEXT_PUSH = "${JOB_NAME} is Pushing"
        TEXT_CLEAN = "${JOB_NAME} is Cleaning"
        TEXT_RUN = "${JOB_NAME} is Running"

        // Telegram parameters
        TEXT_SUCCESS_BUILD = "${JOB_NAME} is Success"
        TEXT_FAILURE_BUILD = "${JOB_NAME} is Failure"
    }

    stages {
        stage('Build') {
            steps {
                // Build Docker image tu source hien tai.
                sh "curl --location --request POST 'https://api.telegram.org/bot${TOKEN}/sendMessage' --form text='${TEXT_PRE}' --form chat_id='${CHAT_ID}'"
                sh "curl --location --request POST 'https://api.telegram.org/bot${TOKEN}/sendMessage' --form text='${TEXT_BUILD}' --form chat_id='${CHAT_ID}'"
                sh 'docker build -t yamiannephilim/crm:latest .'
            }
        }

        stage('Push') {
            steps {
                // Push image len Docker Hub voi credential da cau hinh trong Jenkins.
                sh "curl --location --request POST 'https://api.telegram.org/bot${TOKEN}/sendMessage' --form text='${TEXT_PUSH}' --form chat_id='${CHAT_ID}'"

                withDockerRegistry(credentialsId: 'docker_hub', url: 'https://index.docker.io/v1/') {
                    sh 'docker push yamiannephilim/crm'
                }
            }
        }

        stage('Clean') {
            steps {
                // Dung va xoa container crm cu neu dang ton tai.
                sh "curl --location --request POST 'https://api.telegram.org/bot${TOKEN}/sendMessage' --form text='${TEXT_CLEAN}' --form chat_id='${CHAT_ID}'"

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
                // Tao network neu can va chay container moi tu image latest.
                sh "curl --location --request POST 'https://api.telegram.org/bot${TOKEN}/sendMessage' --form text='${TEXT_RUN}' --form chat_id='${CHAT_ID}'"
                sh 'docker container stop crm || echo "this container does not exist"'
                sh 'docker network create yan || echo "this network exist"'
                sh 'echo y | docker container prune'
                sh 'docker run --name crm --network yan --restart=unless-stopped -d yamiannephilim/crm:latest'
            }
        }
    }

    post {
        always {
            cleanWs()
        }

        success {
            script {
                sh "curl --location --request POST 'https://api.telegram.org/bot${TOKEN}/sendMessage' --form text='${TEXT_SUCCESS_BUILD}' --form chat_id='${CHAT_ID}'"
            }
        }

        failure {
            script {
                sh "curl --location --request POST 'https://api.telegram.org/bot${TOKEN}/sendMessage' --form text='${TEXT_FAILURE_BUILD}' --form chat_id='${CHAT_ID}'"
            }
        }
    }
}
