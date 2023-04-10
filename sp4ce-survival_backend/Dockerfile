FROM gradle:7-jdk17 as build

# The source code of the application is copied; the current directory
COPY --chown=gradle:gradle . /home/sp4ce-survival_backend/gradle/src
WORKDIR /home/sp4ce-survival_backend/gradle/src
RUN gradle bootJar --no-daemon

# We generate the image that runs the application
FROM openjdk:17-jdk-slim-buster
EXPOSE 6969:6969

# Directory where the application is stored
RUN mkdir /app

# We copy the jar
COPY --from=build /home/sp4ce-survival_backend/gradle/src/build/libs/sp4ce-survival_backend-0.0.1.jar /app/sp4ceSurvival_backend.jar

# & Execute the application
ENTRYPOINT ["java", "-jar", "/app/sp4ceSurvival_backend.jar"]