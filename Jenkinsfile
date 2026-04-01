pipeline {
    agent { label 'docker-medibook' }

    environment {
        ACR_REPO = 'medibookregistry.azurecr.io/medibook'
        ACR_SERVER = 'medibookregistry.azurecr.io'
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
                echo '🐳 Build Maven + Construction image Docker...'
                sh """
                    docker build -t ${ACR_REPO}:latest .
                    docker build -t ${ACR_REPO}:${BUILD_NUMBER} .
                """
                echo '✅ Image Docker construite !'
            }
        }

        stage('Push vers Azure Container Registry') {
            steps {
                echo '📤 Push de l\'image vers ACR...'
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
                echo '✅ Image pushée sur ACR !'
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
                        az login --service-principal \
                            -u \$CLIENT_ID \
                            -p \$CLIENT_SECRET \
                            --tenant \$TENANT_ID --output none

                        az account set --subscription \$SUBSCRIPTION_ID

                        az extension add --name containerapp --upgrade -y 2>/dev/null

                        az containerapp update \
                            --name ${CONTAINER_APP} \
                            --resource-group ${RESOURCE_GROUP} \
                            --image ${ACR_REPO}:${BUILD_NUMBER} \
                            --output table

                        echo "✅ Déploiement terminé !"
                        echo "🌐 https://medibook-app.ashyforest-850fd289.spaincentral.azurecontainerapps.io"
                    """
                }
            }
        }

        stage('Nettoyage Docker') {
            steps {
                echo '🧹 Nettoyage des images locales...'
                sh """
                    docker rmi ${ACR_REPO}:latest || true
                    docker rmi ${ACR_REPO}:${BUILD_NUMBER} || true
                    docker image prune -f || true
                """
            }
        }
    }

    post {
        success {
            echo '🎉 Pipeline réussi ! MediBook déployé sur Azure.'
        }
        failure {
            echo '❌ Pipeline échoué. Vérifier les logs ci-dessus.'
        }
        always {
            echo '📊 Pipeline terminé.'
        }
    }
}