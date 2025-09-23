pipeline {
    agent any

    environment {
        IMAGE_NAME = "demo-micro"
        DOKCERHUB_NAMESPACE = "julanih"
        REGISTRY = "index.docker.io/v1/"
        JAVA_HOME = tool name: 'JDK17', type: 'hudson.model.JDK'
        MAVEN_HOME = tool name: 'M3', type: 'hudson.tasks.Maven$MavenInstallation'
        PATH = "${JAVA_HOME}/bin:${MAVEN_HOME}/bin:${env.PATH}"
    }

    options {
        disableConcurrentBuilds()
        ansiColor('xterm')
        timestamps()
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build JAR') {
            steps {
                sh 'mvn -B clean package -DskipTests'
            }
            post {
                success {
                    archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                }
            }
        }

        stage('Build & Push Image') {
            steps {
                script {
                    def tag = env.BUILD_NUMBER
                    def image = docker.build("${DOKCERHUB_NAMESPACE}/${IMAGE_NAME}:$BUILD_ID")
                    docker.withRegistry("https://${REGISTRY}", 'dockerhub-creds') {
                        image.push($BUILD_ID)
                    }
                }
            }
        }
    }

    stage('Deploy') {
        steps {
            script {
                dir('docker') {
                    // Reemplazar TAG en docker-compose.yaml con el número de build
                    sh "sed -i '' 's#TAG#${env.BUILD_NUMBER}#g' docker-compose.yaml"

                    // Reemplazar MESSAGE según la rama
                    if (env.BRANCH_NAME == 'main') {
                        sh "sed -i '' 's|^[[:space:]]*- MESSAGE=.*|      - MESSAGE=${URL_PRD}|' docker-compose.yaml"
                    } else {
                        sh "sed -i '' 's|^[[:space:]]*- MESSAGE=.*|      - MESSAGE=${URL_DEV}|' docker-compose.yaml"
                    }

                    // Mostrar el archivo modificado
                    sh "cat docker-compose.yaml"

                    // Levantar los servicios con docker-compose
                    sh "docker compose up -d > /dev/null"
                }
            }
        }
    }

    stage('Limpiar workspace') {
        steps {
            cleanWs()
        }
    }
}