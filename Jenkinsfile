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

        stage('Print Workspace') {
            steps {
                script {
                    echo "Current workspace: ${env.WORKSPACE}"
                    sh 'ls -al $WORKSPACE' // Corrected: List files in the workspace
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
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Run Tests') {
            steps {
                script {
                    def testResults = sh(script: 'mvn test', returnStatus: true)

                    if (testResults != 0) {
                        echo 'Tests échoués. Vérifiez les résultats.'
                    } else {
                        echo 'Tous les tests ont réussi.'
                    }
                }
            }
        }

        stage('Publish Test Outcomes') {
            steps {
                junit testResults: '**/target/surefire-reports/*.xml', allowEmptyResults: true
            }
        }

        stage('Archive Artifacts') {
            steps {
                script {
                    if (fileExists('target/*.jar')) {
                        archiveArtifacts artifacts: 'target/*.jar', allowEmptyArchive: false
                        echo 'Artifacts archived successfully.'
                    } else {
                        echo 'No artifacts found to archive.'
                    }
                }
            }
        }

        stage('Deploy to Vagrant') {
            steps {
                script {
                    sh 'scp -o StrictHostKeyChecking=no target/*.jar vagrant@192.168.33.10:/home/vagrant/' // Change the destination as needed
                }
            }
        }
    }

    post {
        failure {
            script {
                def errorDetails = currentBuild.rawBuild.getLog(10) // Get the last 10 lines of the log for the error
                def errorMessage = errorDetails.join("\n") // Join the lines into a single string for the email

                // Send failure email with error details
                mail to: "${MAIL_RECIPIENT}",
                     subject: "Build Failed: ${env.JOB_NAME} - Build #${env.BUILD_NUMBER}",
                     body: """
                     The build ${env.JOB_NAME} - Build #${env.BUILD_NUMBER} has failed.
                     Error Details:
                     ${errorMessage}
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
