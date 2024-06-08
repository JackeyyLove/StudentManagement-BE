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
        stage('Get Latest Tag') {
            steps {
                script {
                    env.TAG_NAME = sh(returnStdout: true, script: "git tag --sort version:refname | tail -1").trim()
                    echo "Latest Tag: ${env.TAG_NAME}"
                }
            }
        }
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
                    echo "Updating config repo with new image tag: ${env.TAG_NAME}"
                    withCredentials([usernamePassword(credentialsId: CONFIG_REPO_CREDENTIALS, usernameVariable: 'GIT_USERNAME', passwordVariable: 'GIT_PASSWORD')]) {
                        sh """
                        git clone https://${GIT_USERNAME}:${GIT_PASSWORD}@${CONFIG_REPO_URL} config-repo
                        cd config-repo
                        sed -i 's/tag: .*/tag: ${env.TAG_NAME}/' values.yaml
                        git config user.name 'JackeyyLove'
                        git config user.email 'loidao99@gmail.com'
                        git add values.yaml
                        git commit -m 'Update image tag to ${env.TAG_NAME}'
                        git push origin main
                        """
                    }
                }
            }
        }
    }
}
