FROM mcr.microsoft.com/openjdk/jdk:21-ubuntu

WORKDIR /apps

COPY target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
