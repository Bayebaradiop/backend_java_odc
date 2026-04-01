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

        stage('Build Spring Boot') {
            agent {
                docker {
                    image 'maven:3.9.5-eclipse-temurin-17'
                    args '-v $HOME/.m2:/root/.m2'
                    reuseNode true
                }
            }
            steps {
                echo '🔨 Build Maven (sans tests)...'
                sh 'mvn clean package -DskipTests'
                echo '✅ Build Maven terminé !'
            }
        }

        stage('Build Docker Image') {
            steps {
                echo '🐳 Construction de l\'image Docker...'
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
                        # Installer Azure CLI si absent
                        if ! command -v az &> /dev/null; then
                            curl -sL https://aka.ms/InstallAzureCLIDeb | bash
                        fi

                        # Login Azure avec service principal
                        az login --service-principal \
                            -u \$CLIENT_ID \
                            -p \$CLIENT_SECRET \
                            --tenant \$TENANT_ID

                        az account set --subscription \$SUBSCRIPTION_ID

                        # Installer extension Container Apps
                        az extension add --name containerapp --upgrade -y

                        # Déployer la nouvelle image
                        az containerapp update \
                            --name ${CONTAINER_APP} \
                            --resource-group ${RESOURCE_GROUP} \
                            --image ${ACR_REPO}:${BUILD_NUMBER}

                        echo "✅ Déploiement terminé !"
                        echo "🌐 URL: https://medibook-app.ashyforest-850fd289.spaincentral.azurecontainerapps.io"
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
                    docker system prune -f || true
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