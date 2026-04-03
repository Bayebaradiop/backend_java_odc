pipeline {

    agent { label 'docker-medibook' }

    environment {
        ACR_REPO = 'medibookregistry.azurecr.io/medibook'
        ACR_SERVER = 'medibookregistry.azurecr.io'

        DOCKERHUB_REPO = 'abdoulayely777/medibook'

        CONTAINER_APP = 'medibook-app'
        RESOURCE_GROUP = 'medibook-rg'
    }

    stages {

        stage('Checkout') {
            steps {
                echo '📥 Récupération du code source...'
                checkout scm
            }
        }

        stage('Build Docker Image') {
            steps {
                echo '🐳 Build + Docker image...'
                sh """
                    docker build -t ${ACR_REPO}:latest .
                    docker build -t ${ACR_REPO}:${BUILD_NUMBER} .

                    # tag Docker Hub
                    docker tag ${ACR_REPO}:latest ${DOCKERHUB_REPO}:latest
                    docker tag ${ACR_REPO}:${BUILD_NUMBER} ${DOCKERHUB_REPO}:${BUILD_NUMBER}
                """
                echo '✅ Image Docker construite !'
            }
        }

        // 🔥 PUSH ACR
        stage('Push vers ACR') {
            steps {
                echo '📤 Push vers Azure Container Registry...'
                withCredentials([usernamePassword(
                    credentialsId: 'acr-credentials',
                    usernameVariable: 'ACR_USER',
                    passwordVariable: 'ACR_PASS'
                )]) {
                    sh """
                        echo "\$ACR_PASS" | docker login ${ACR_SERVER} -u "\$ACR_USER" --password-stdin
                        docker push ${ACR_REPO}:latest
                        docker push ${ACR_REPO}:${BUILD_NUMBER}
                    """
                }
                echo '✅ Push ACR OK'
            }
        }

        // 🔥 PUSH DOCKER HUB
        stage('Push vers Docker Hub') {
            steps {
                echo '📤 Push vers Docker Hub...'
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-credentials',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    sh """
                        echo "\$DOCKER_PASS" | docker login -u "\$DOCKER_USER" --password-stdin
                        docker push ${DOCKERHUB_REPO}:latest
                        docker push ${DOCKERHUB_REPO}:${BUILD_NUMBER}
                    """
                }
                echo '✅ Push Docker Hub OK'
            }
        }

        stage('Deploy sur Azure Container Apps') {
            steps {
                echo '🚀 Déploiement sur Azure...'
                withCredentials([
                    string(credentialsId: 'AZURE_CLIENT_ID', variable: 'CLIENT_ID'),
                    string(credentialsId: 'AZURE_CLIENT_SECRET', variable: 'CLIENT_SECRET'),
                    string(credentialsId: 'AZURE_TENANT_ID', variable: 'TENANT_ID'),
                    string(credentialsId: 'AZURE_SUBSCRIPTION_ID', variable: 'SUBSCRIPTION_ID')
                ]) {
                    sh """
                        az login --service-principal \\
                            -u \$CLIENT_ID \\
                            -p \$CLIENT_SECRET \\
                            --tenant \$TENANT_ID --output none

                        az account set --subscription \$SUBSCRIPTION_ID

                        az extension add --name containerapp --upgrade -y 2>/dev/null

                        az containerapp update \\
                            --name ${CONTAINER_APP} \\
                            --resource-group ${RESOURCE_GROUP} \\
                            --image ${ACR_REPO}:${BUILD_NUMBER}

                        echo "✅ Déploiement terminé !"
                    """
                }
            }
        }

        stage('Nettoyage Docker') {
            steps {
                echo '🧹 Nettoyage...'
                sh """
                    docker rmi ${ACR_REPO}:latest || true
                    docker rmi ${ACR_REPO}:${BUILD_NUMBER} || true
                    docker rmi ${DOCKERHUB_REPO}:latest || true
                    docker rmi ${DOCKERHUB_REPO}:${BUILD_NUMBER} || true
                    docker image prune -f || true
                """
            }
        }
    }

    post {
        success {
            echo '🎉 Pipeline réussi !'
        }
        failure {
            echo '❌ Pipeline échoué.'
        }
        always {
            echo '📊 Pipeline terminé.'
        }
    }
}