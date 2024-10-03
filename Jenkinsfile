pipeline {
    agent any

    environment {
        GIT_REPO = 'https://github.com/benaissaislem/5ARCTIC6-G6-projet_devops.git'
        GIT_CREDENTIALS_ID = 'jenkins-vagran'
        MAIL_RECIPIENT = 'mohamed.bouheni@esprit.tn'
        MAIL_SENDER = 'support@darkmatter-corp.co'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', credentialsId: "${GIT_CREDENTIALS_ID}", url: "${GIT_REPO}"
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
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Archive Artifacts') {
            steps {
                archiveArtifacts artifacts: 'target/*.jar', allowEmptyArchive: true
            }
        }
    }

    post {
        failure {
            script {
                def errorDetails = currentBuild.rawBuild.getLog(10).join("\n") // Get last 10 lines of the log for the error

                // Send failure email with error details
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
            // Send success email notification
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
