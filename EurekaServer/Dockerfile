# Use the OpenJDK 21 image as the base
FROM openjdk:21-jdk

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file of the application to the working directory
COPY target/EurekaServer-0.0.1-SNAPSHOT.jar app.jar

# Expose the port the application will run on
EXPOSE 8761

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]