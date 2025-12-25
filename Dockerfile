# Use Java JDK base image
FROM eclipse-temurin:21-jdk

# Copy your Spring Boot JAR into the container
COPY target/AdvanceRestApi-0.0.1-SNAPSHOT.jar /usr/app/AdvanceRestApi-0.0.1-SNAPSHOT.jar

# Set working directory
WORKDIR /usr/app

# Expose port
EXPOSE 8080

# Run the JAR
ENTRYPOINT ["java","-jar","AdvanceRestApi-0.0.1-SNAPSHOT.jar"]
