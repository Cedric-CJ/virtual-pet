#
# Build stage
#

FROM gradle:jdk-21-and-22 AS build
COPY -- chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build

LABEL org.name="Cedric"
#
# Package stage
#

FROM eclipse-temurin:21-jdk-jammy
COPY -- from=build /home/gradle/src/build/libs/Web-app-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar","/app.jar"]