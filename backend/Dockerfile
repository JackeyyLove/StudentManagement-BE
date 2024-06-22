# Stage 1: Build
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY . .

# Stage 2: Run
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar back-end.jar
RUN addgroup -S mygroup && adduser -S myuser -G mygroup 
USER myuser
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "back-end.jar"]
