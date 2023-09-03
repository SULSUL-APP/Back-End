FROM openjdk:11-jdk-slim

WORKDIR /app

COPY build/libs/*.jar application.jar

CMD ["java", "-jar", "-Dspring.profiles.active=prod" ,"application.jar"]