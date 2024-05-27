#
# Build stage
#

FROM gradle:jdk-21-and-22 AS build
COPY -- chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build -- no-daemon

LABEL org.name="Cedric"
#
# Package stage
#

FROM eclipse-temurin:21
COPY -- from=build /home/gradle/src/build/libs/virtual-pet-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar","/app.jar"]