pipeline {
    agent any 
    tools {
        maven 'maven'
    }
    environment {
        APP_NAME = "student-management-backend"
    }
    
    stages {
        
        stage('Unit test') {
            steps {
                script {
                    echo "Testing application with JUnit5"
                    dir('backend') {
                        sh 'mvn test'
                    }
                }
            }
        } 
        
        stage('Build image') {
            steps {
                script {
                    echo "Building the docker image"
                    dir('backend') {
                        withCredentials([usernamePassword(credentialsId: 'docker-hub-repo', passwordVariable: 'PASS', usernameVariable: 'USER')]) {
                            sh "docker build -t $USER/${APP_NAME}:latest ."
                            sh "echo $PASS | docker login -u $USER --password-stdin"
                            sh "docker push $USER/${APP_NAME}:latest"
                        }
                    }
                }
            }
        }
    }
}
