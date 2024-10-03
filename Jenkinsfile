pipeline {
    agent any

    environment {
        GIT_REPO = 'https://github.com/benaissaislem/5ARCTIC6-G6-projet_devops.git'
        GIT_CREDENTIALS_ID = 'jenkins-vagran'
    }

    stages {
        stage('Checkout') {
            steps {
                // Checkout the repository
                git branch: 'main', credentialsId: "${GIT_CREDENTIALS_ID}", url: "${GIT_REPO}"
            }
        }

        stage('Clean') {
            steps {
                script {
                    // Remove the target directory, if it exists
                    if (fileExists('target')) {
                        echo 'Cleaning target directory...'
                        sh 'rm -rf target'
                    }
                }
            }
        }

        stage('Build') {
            steps {
                // Build the project using Maven, which will read the pom.xml by default
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Archive Artifacts') {
            steps {
                // Archive the generated JAR file(s) from the target directory
                archiveArtifacts artifacts: 'target/*.jar', allowEmptyArchive: true
            }
        }
    }

    post {
        failure {
            // Send an email if the build fails
            mail to: 'mohamed.bouheni@esprit.tn',
                 subject: "Build Failed: ${env.JOB_NAME} - Build #${env.BUILD_NUMBER}",
                 body: "The build ${env.JOB_NAME} - Build #${env.BUILD_NUMBER} has failed. Check Jenkins for details: ${env.BUILD_URL}"
        }

        success {
            // Output message for successful builds
            echo 'Build completed successfully!'
        }
    }
}
