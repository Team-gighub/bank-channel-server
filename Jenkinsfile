pipeline {
    agent any

    environment {
        DOCKER_IMAGE = "rudska6/bank-channel"
        DOCKER_TAG = "latest"
        EC2_HOST = "ubuntu@${SERVER_IP}"        // 백엔드 서버 IP
        EC2_KEY = "deploy-key"                  // Jenkins SSH key ID
        COMPOSE_FILE = "docker-compose.yml"
        ENV_FILE = ".env"
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main',
                    credentialsId: 'github',
                    url: 'https://github.com/Team-gighub/bank-channel-server.git'
            }
        }

        stage('Build JAR') {
            steps {
                sh 'chmod +x gradlew'
                sh './gradlew clean build'
            }
        }

        stage('Docker Build') {
            steps {
                sh "docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} ."
            }
        }

        stage('Docker Login') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-login',
                                usernameVariable: 'DOCKER_USER',
                                passwordVariable: 'DOCKER_PASS')]) {
                    sh "echo \"$DOCKER_PASS\" | docker login -u \"$DOCKER_USER\" --password-stdin"
                }
            }
        }

        stage('Docker Push') {
            steps {
                sh "docker push ${DOCKER_IMAGE}:${DOCKER_TAG}"
            }
        }

        stage('Deploy to EC2 (docker-compose)') {
            steps {
                sshagent(credentials: ['deploy-key']) {

                    // 1) docker-compose.yml & .env 전송
                    sh """
                        scp -o ${COMPOSE_FILE} ${EC2_HOST}:~/bank-channel/${COMPOSE_FILE}
                        scp -o ${ENV_FILE} ${EC2_HOST}:~/bank-channel/${ENV_FILE}
                    """

                    // 2) 원격에서 docker compose 재배포
                    sh """
                    ssh -v -o ${EC2_HOST} '
                        mkdir -p ~/bank-channel && cd ~/bank-channel;

                        # 최신 이미지 pull
                        docker pull ${DOCKER_IMAGE}:${DOCKER_TAG};

                        docker compose down;

                        docker compose up -d;
                    '
                    """
                }
            }
        }
    }
}
