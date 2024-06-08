pipeline {
    agent any 
    tools {
        maven 'maven'
    }
    environment {
        DOCKERHUB_CREDENTIALS = credentials('docker-hub-repo')
        CONFIG_REPO_URL = 'https://github.com/JackeyyLove/VDT24-Config-API.git'
        CONFIG_REPO_CREDENTIALS = credentials('Github')
        REPO_NAME = 'louisdevops/student-management-backend'
    }
    
    stages {
        stage('Build image') {
            steps {
                script {
                    echo "Building the docker image"
                    dir('backend') {
                        withCredentials([usernamePassword(credentialsId: 'docker-hub-repo', passwordVariable: 'PASS', usernameVariable: 'USER')]) {
                            sh "docker build -t ${REPO_NAME}:${env.TAG_NAME} ."
                            sh "echo $PASS | docker login -u $USER --password-stdin"
                            sh "docker push ${REPO_NAME}:${env.TAG_NAME}"
                        }
                    }
                }
            }
        }
         stage('Update Config Repo') {
            steps {
                script {
                    echo "Cloning config repo"
                    git branch: 'master', credentialsId: 'Github', url: "${CONFIG_REPO_URL}"

                    echo "Updating values.yaml with new image tag"
                    sh """
                    sed -i 's/tag: .*/tag: ${env.TAG_NAME}/' values.yaml
                    git add values.yaml
                    git commit -m "Update image tag to ${env.TAG_NAME}"
                    git push origin master
                    """
                }
            }
        }
    }
}
