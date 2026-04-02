FROM ubuntu:latest
LABEL authors="Leo Bulatewicz"

WORKDIR /app

COPY target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]