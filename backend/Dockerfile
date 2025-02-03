# Use an official Java runtime as a parent image
FROM openjdk:21-jdk-slim

# Use Bash as the default shell
SHELL ["/bin/bash", "-c"]

# Set the working directory inside the container
WORKDIR /app

# Copy the application JAR file
COPY build/libs/weight-assistant-mobile-app-backend-*.jar app.jar

COPY src/main/resources/application.properties /app/config/application.properties

# Expose the application's port
EXPOSE 5000

# Run the application
CMD ["bash", "-c", "java -jar app.jar"]