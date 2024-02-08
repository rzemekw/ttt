# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

WORKDIR /app

#TODO LEPSZY DOCKERFILE!!!
COPY build/libs/ttt-0.0.1-SNAPSHOT.jar /app/app.jar

# Expose the port the app runs on
EXPOSE 80

# Run the JAR file
CMD ["java", "-jar", "/app/app.jar"]
