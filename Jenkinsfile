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
                    def image = docker.build("${DOKCERHUB_NAMESPACE}/${IMAGE_NAME}:${tag}")
                    docker.withRegistry("https://${REGISTRY}", 'dockerhub-creds') {
                        image.push()
                        image.push('latest')
                    }
                }
            }
        }
    }

    post {
        success {
            echo 'Imagen publicada: ${env.REGISTRY}/${env.DOKCERHUB_NAMESPACE}/${env.IMAGE_NAME}:${env.BUILD_NUMBER}'
        }
        failure {
            echo 'Build fallido. Revisar logs.'
        }
    }
}