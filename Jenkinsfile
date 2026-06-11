pipeline {
    agent any

    tools {
        maven 'maven:3.9.10'
    }

    environment {
        // On ajoute les chemins standards de macOS (/usr/local/bin et /opt/homebrew/bin pour les puces Apple Silicon)
        PATH = "/usr/local/bin:/usr/bin:/bin:/usr/sbin:/sbin:/opt/homebrew/bin:${env.PATH}"
        DOCKER_IMAGE = "nandraina/ebooking-backend"
    }

    stages {
        stage('Test') {
            steps {
                sh 'mvn clean test'
            }
        }

        stage('Docker Build'){
            steps {
                sh "docker build -t ${DOCKER_IMAGE}:${env.BUILD_NUMBER} -t ${DOCKER_IMAGE}:latest ."
            }
        }

        stage('Docker Push'){
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-secret', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh '''
                        echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
                    '''

                    sh """
                        docker push ${DOCKER_IMAGE}:${env.BUILD_NUMBER}
                        docker push ${DOCKER_IMAGE}:latest
                        docker logout
                    """
                }
            }
        }

        stage('Trigger Frontend Pipeline') {
            steps {
                build job: 'ebooking-pipeline-frontend', wait: true  // attend que le pipeline front soit terminé
            }
        }
    }

    post {
        success {
            echo "Pipeline backend terminé avec succès - image: ${DOCKER_IMAGE}:${env.BUILD_NUMBER}"
        }
        failure {
            echo "Pipeline backend en échec"
        }
    }
}