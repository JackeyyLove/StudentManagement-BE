Java application using Springboot framework
Use "docker build -t username/application-name:tag ." in backend folder to build an image
For running container, use command "docker run -p 8080:8080 -d --network your-network --name backend username/application-name:tag"
