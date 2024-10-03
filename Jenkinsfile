pipeline {
    agent any

    environment {
        GIT_REPO = 'https://github.com/benaissaislem/5ARCTIC6-G6-projet_devops.git'
        GIT_CREDENTIALS_ID = 'jenkins-vagran'
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
                        echo 'Nettoyage du répertoire target...'
                        deleteDir()
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
            mail to: 'mohamed.bouheni@esprit.tn',
                 subject: "Échec du build : ${env.JOB_NAME} - Build #${env.BUILD_NUMBER}",
                 body: "Le build ${env.JOB_NAME} - Build #${env.BUILD_NUMBER} a échoué. Vérifiez Jenkins pour plus de détails : ${env.BUILD_URL}"
        }

        success {
            echo 'Build terminé avec succès !'
        }
    }
}
