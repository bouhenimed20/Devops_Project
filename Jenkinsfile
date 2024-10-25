pipeline {
    agent any

   environment {
        GIT_REPO = 'https://github.com/benaissaislem/5ARCTIC6-G6-projet_devops.git'
        GIT_CREDENTIALS_ID = 'jenkins-vagran'
        MAIL_RECIPIENT = 'mohamed.bouheni@esprit.tn'
        MAIL_SENDER = 'support@darkmatter-corp.com'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'BouheniMohamed-5ARCTIC6-G6', credentialsId: "${GIT_CREDENTIALS_ID}", url: "${GIT_REPO}"
            }
        }

        stage ('Clean') {
            steps {
                script {
                    if  (fileExists('target')) {
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


       stage('Test') {
            steps {
                sh 'mvn test'
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
    }

    post {
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
