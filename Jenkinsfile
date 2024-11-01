pipeline {
    agent any

    environment {
        GIT_REPO = 'https://github.com/benaissaislem/5ARCTIC6-G6-projet_devops.git'
        GIT_CREDENTIALS_ID = 'jenkins-vagran'
        MAIL_RECIPIENT = 'mohamed.bouheni@esprit.tn'
        MAIL_SENDER = 'support@darkmatter-corp.com'
        DOCKER_HUB_CREDENTIALS_ID = 'dockerhub_credentials'
        DOCKER_IMAGE_NAME = 'bouheni/foyer-app'
        SONAR_PROJECT_KEY = '5ARCTIC6-G6-projet_devops'
        SONAR_PROJECT_NAME = '5ARCTIC6-G6-projet_devops'
        SONAR_HOST_URL = 'http://192.168.33.10:9000'
        SONAR_TOKEN = 'sqp_025708b4238e562854193537a342b8f1611abbab'
    }

    stages {
        stage('Checkout') {
            steps {
                script {
                    try {
                        git branch: 'BouheniMohamed-5ARCTIC6-G6', credentialsId: "${GIT_CREDENTIALS_ID}", url: "${GIT_REPO}"
                    } catch (Exception e) {
                        echo "Checkout failed: ${e.message}"
                        currentBuild.result = 'FAILURE'
                        error("Stopping pipeline due to checkout failure.")
                    }
                }
            }
        }

        stage('Clean') {
            steps {
                script {
                    if (fileExists('target')) {
                        echo 'Cleaning target directory...'
                        sh 'rm -rf target'
                    }
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    try {
                        sh 'mvn clean package -DskipTests'
                    } catch (Exception e) {
                        echo "Build failed: ${e.message}"
                        currentBuild.result = 'FAILURE'
                        error("Stopping pipeline due to build failure.")
                    }
                }
            }
        }

        stage('JaCoCo Report') {
            steps {
                script {
                    try {
                        sh 'mvn jacoco:report'
                    } catch (Exception e) {
                        echo "JaCoCo report generation failed: ${e.message}"
                        currentBuild.result = 'FAILURE'
                        error("Stopping pipeline due to JaCoCo report failure.")
                    }
                }
            }
        }

        stage('Publish JaCoCo Report') {
            steps {
                jacoco execPattern: '**/target/jacoco.exec',
                        classPattern: '**/target/classes',
                        sourcePattern: '**/src/main/java',
                        exclusionPattern: '**/test/**',
                        changeBuildStatus: true
            }
        }

        stage('SonarQube Analysis') {
            steps {
                script {
                    try {
                        sh """
                        mvn clean verify sonar:sonar \
                          -Dsonar.projectKey=${SONAR_PROJECT_KEY} \
                          -Dsonar.projectName=${SONAR_PROJECT_NAME} \
                          -Dsonar.host.url=${SONAR_HOST_URL} \
                          -Dsonar.token=${SONAR_TOKEN}
                        """
                    } catch (Exception e) {
                        echo "SonarQube analysis failed: ${e.message}"
                        currentBuild.result = 'FAILURE'
                        error("Stopping pipeline due to SonarQube analysis failure.")
                    }
                }
            }
        }

        stage('Test') {
            steps {
                script {
                    try {
                        sh 'mvn test'
                    } catch (Exception e) {
                        echo "Tests failed: ${e.message}"
                        currentBuild.result = 'FAILURE'
                        error("Stopping pipeline due to test failure.")
                    }
                }
            }
        }

        stage('Publish Test Results') {
            steps {
                junit skipPublishingChecks: true, allowEmptyResults: true, testResults: '**/*.xml'
            }
        }

        stage('Archive Artifacts') {
            steps {
                archiveArtifacts artifacts: 'target/*.jar', allowEmptyArchive: true
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    try {
                        sh "docker build -t ${DOCKER_IMAGE_NAME}:latest ."
                    } catch (Exception e) {
                        echo "Docker build failed: ${e.message}"
                        currentBuild.result = 'FAILURE'
                        error("Stopping pipeline due to Docker build failure.")
                    }
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: "${DOCKER_HUB_CREDENTIALS_ID}", usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD')]) {
                        try {
                            sh "echo \$DOCKER_PASSWORD | docker login -u \$DOCKER_USERNAME --password-stdin"
                            sh "docker push ${DOCKER_IMAGE_NAME}:latest"
                        } catch (Exception e) {
                            echo "Docker push failed: ${e.message}"
                            currentBuild.result = 'FAILURE'
                            error("Stopping pipeline due to Docker push failure.")
                        }
                    }
                }
            }
        }
    }

    post {
        always {
            junit '**/target/surefire-reports/*.xml' // Publishing test results
            archiveArtifacts artifacts: '**/target/site/jacoco/*', fingerprint: true // Archive the JaCoCo report
        }

        failure {
            script {
                def errorDetails = currentBuild.rawBuild.getLog(10).join("\n")
                mail to: "${MAIL_RECIPIENT}",
                     subject: "Build Failed: ${env.JOB_NAME} - Build #${env.BUILD_NUMBER}",
                     body: """
                     The build ${env.JOB_NAME} - Build #${env.BUILD_NUMBER} has failed.
                     Error Details:
                     ${errorDetails}
                     Check Jenkins for more details: ${env.BUILD_URL}
                     """,
                     replyTo: "${MAIL_SENDER}"
            }
        }

        success {
            mail to: "${MAIL_RECIPIENT}",
                 subject: "Build Success: ${env.JOB_NAME} - Build #${env.BUILD_NUMBER}",
                 body: """
                 The build ${env.JOB_NAME} - Build #${env.BUILD_NUMBER} has completed successfully.
                 Check Jenkins for more details: ${env.BUILD_URL}
                 """,
                 replyTo: "${MAIL_SENDER}"
        }
    }
}
