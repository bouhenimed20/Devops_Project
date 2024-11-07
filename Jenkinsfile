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
        NEXUS_URL = 'http://192.168.33.10:8081'
        NEXUS_CREDENTIALS_ID = 'nexus_credentials'
        NEXUS_PROTOCOL = 'http'
        NEXUS_VERSION = 'nexus3'
        NEXUS_REPOSITORY = 'nexus-repo-foyer'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'BouheniMohamed-5ARCTIC6-G6', credentialsId: GIT_CREDENTIALS_ID, url: GIT_REPO
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
                        error("Stopping the pipeline due to build failure.")
                    }
                }
            }
        }

        stage('JaCoCo Report') {
            steps {
                sh 'mvn jacoco:report'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                script {
                    sh """
                    mvn clean verify sonar:sonar \
                      -Dsonar.projectKey=${SONAR_PROJECT_KEY} \
                      -Dsonar.projectName=${SONAR_PROJECT_NAME} \
                      -Dsonar.host.url=${SONAR_HOST_URL} \
                      -Dsonar.token=${SONAR_TOKEN}
                    """
                }
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }

        stage('Publish Test Results') {
            steps {
                junit skipPublishingChecks: true, allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
            }
        }

        stage('Archive Artifacts') {
            steps {
                archiveArtifacts artifacts: 'target/*.jar', allowEmptyArchive: true
            }
        }

        stage('Build_Docker_Image') {
            steps {
                script {
                    sh "docker build -t ${DOCKER_IMAGE_NAME}:latest ."
                }
            }
        }

        stage('Push_Docker_Image') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: DOCKER_HUB_CREDENTIALS_ID, usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD')]) {
                        sh "echo \$DOCKER_PASSWORD | docker login -u \$DOCKER_USERNAME --password-stdin"
                        sh "docker push ${DOCKER_IMAGE_NAME}:latest"
                    }
                }
            }
        }


       stage('docker_compose') {
           steps {
               script {
                   def foyerAppRunning = sh(script: "docker ps -q --filter 'name=foyer-app'", returnStdout: true).trim()
                   def mysqlRunning = sh(script: "docker ps -q --filter 'name=mysqldb'", returnStdout: true).trim()

                   if (!foyerAppRunning || !mysqlRunning) {
                       echo 'Some containers are not running; starting containers...'
                       sh "docker-compose -f docker-compose.yml up -d"
                       sh "docker-compose -f docker-compose-monitoring.yml up -d"
                   } else {
                       echo 'Containers are already running; skipping startup.'
                   }
               }
           }
       }


        stage('Deploy_Nexus') {
                    steps {
                        script {
                            def repositoryUrl = isSnapshot() ? "${NEXUS_URL}/repository/maven-snapshots/" : "${NEXUS_URL}/repository/maven-releases/"
                            try {
                                sh "mvn deploy -DskipTests -DaltDeploymentRepository=deploymentRepo::default::${repositoryUrl}"
                            } catch (Exception e) {
                                currentBuild.result = 'FAILURE'
                                error("Maven deploy failed: ${e.message}")
                            }
                        }
                    }
                }
}

    post {
        success {
            mail to: MAIL_RECIPIENT,
                 subject: "Build Success: ${env.JOB_NAME} - Build #${env.BUILD_NUMBER}",
                 body: """
                 The build ${env.JOB_NAME} - Build #${env.BUILD_NUMBER} has completed successfully.
                 Check Jenkins for more details: ${env.BUILD_URL}
                 """,
                 replyTo: MAIL_SENDER
        }
        failure {
            mail to: MAIL_RECIPIENT,
                 subject: "Build Failure: ${env.JOB_NAME} - Build #${env.BUILD_NUMBER}",
                 body: """
                 The build ${env.JOB_NAME} - Build #${env.BUILD_NUMBER} has failed.
                 Check Jenkins for more details: ${env.BUILD_URL}
                 """,
                 replyTo: MAIL_SENDER
        }
    }
}
def isSnapshot() {
    return sh(script: 'mvn help:evaluate -Dexpression=project.version -q -DforceStdout', returnStdout: true).trim().endsWith('-SNAPSHOT')
}