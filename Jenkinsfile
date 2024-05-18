pipeline {
    agent any 
    tools {
        maven 'maven-3.9'
    }
    environment {
        APP_NAME = "student-management-backend"
    }
    
    stages {
        stage('Clone') {
            steps {
                git credentialsId: 'Git-credentials', url: 'https://github.com/JackeyyLove/StudentManagement-BE.git'
            }
        }
        stage('Unit test') {
            steps {
                script {
                    echo "Testing application with JUnit5"
                    sh 'mvn test'
                }
            }
        } 
        stage('Build jar file') {
            steps {
                script {
                    echo "Building the application"
                    sh "mvn clean package -DskipTests"
                }
            }
        }
        stage('Build image') {
            steps {
                script {
                    echo "Building the docker image"
                    withCredentials([usernamePassword(credentialsId: 'docker', passwordVariable: 'PASS', usernameVariable: 'USER')]) {
                        sh "docker build -t $USER/${APP_NAME}:latest ."
                        sh "echo $PASS | docker login -u $USER --password-stdin"
                        sh "docker push $USER/${APP_NAME}:latest"
                    }
                }
            }
        }
    }
}