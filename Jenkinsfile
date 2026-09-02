pipeline {

    agent { label 'docker-medibook' }

    environment {
        ACR_REPO = 'medibookregistry.azurecr.io/medibook'
        ACR_SERVER = 'medibookregistry.azurecr.io'

        DOCKERHUB_REPO = 'abdoulayely777/medibook'

        CONTAINER_APP = 'medibook-app'
        RESOURCE_GROUP = 'medibook-rg'
    }

    options {
        timestamps() // logs avec heure
    }

    stages {

        // =========================
        // 1. CHECKOUT
        // =========================
        stage('Checkout') {
            steps {
                echo ' Checkout du code...'
                checkout scm
            }
        }

        // =========================
        // 2. BUILD IMAGE
        // =========================
        stage('Build Docker Image') {
            steps {
                sh 'docker info'
            }
        }

        stage('Build Image') {
            steps {
                script {
                    def commit = sh(
                        script: "git rev-parse --short HEAD",
                        returnStdout: true
                    ).trim()

                    env.COMMIT_TAG = commit
                    echo "Commit utilisé : ${COMMIT_TAG}"
                }

                sh """
                    docker build -t ${ACR_REPO}:${COMMIT_TAG} .
                    docker tag ${ACR_REPO}:${COMMIT_TAG} ${ACR_REPO}:latest

                    docker tag ${ACR_REPO}:${COMMIT_TAG} ${DOCKERHUB_REPO}:${COMMIT_TAG}
                    docker tag ${ACR_REPO}:${COMMIT_TAG} ${DOCKERHUB_REPO}:latest
                """
                echo '✅ Build terminé'
            }
        }

        // =========================
        // 3. LOGIN REGISTRIES
        // =========================
        stage('Login Registries') {
            steps {
                echo '🔐 Connexion aux registries...'
                withCredentials([
                    usernamePassword(
                        credentialsId: 'acr-credentials',
                        usernameVariable: 'ACR_USER',
                        passwordVariable: 'ACR_PASS'
                    ),
                    usernamePassword(
                        credentialsId: 'dockerhub-credentials',
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_PASS'
                    )
                ]) {
                    sh """
                        for i in 1 2 3; do
                            echo "\$ACR_PASS" | docker login ${ACR_SERVER} -u "\$ACR_USER" --password-stdin && break || sleep 5
                        done

                        for i in 1 2 3; do
                            echo "\$DOCKER_PASS" | docker login -u "\$DOCKER_USER" --password-stdin && break || sleep 5
                        done
                    """
                }
                echo '✅ Connexion OK'
            }
        }

        stage('Push Images') {
            parallel {
                stage('Push ACR') {
                    steps {
                        sh """
                            docker push ${ACR_REPO}:${COMMIT_TAG}
                        """
                    }
                }
                stage('Push DockerHub') {
                    steps {
                        sh """
                            docker push ${DOCKERHUB_REPO}:${COMMIT_TAG}
                        """
                    }
                }
            }
        }

        // =========================
        // 6. DEPLOY AZURE
        // =========================
        stage('Deploy Azure') {
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

                        az containerapp update \
                            --name ${CONTAINER_APP} \
                            --resource-group ${RESOURCE_GROUP} \
                            --image ${ACR_REPO}:${COMMIT_TAG}
                    """
                }
            }
        }

        // =========================
        // 7. CLEAN
        // =========================
        stage('Cleanup') {
            steps {
                echo '🧹 Nettoyage Docker...'
                sh """
                    docker logout ${ACR_SERVER} || true
                    docker logout || true

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
            echo '🎉 Pipeline SUCCESS'
        }
        failure {
            echo '❌ Pipeline FAILED'
        }
        always {
            echo '📊 Pipeline terminé'
        }
    }
} 