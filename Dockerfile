# Use a minimal base image with Java 17 support
FROM eclipse-temurin:17-jdk-alpine

# Set environment variables
ENV APP_HOME=/app
WORKDIR $APP_HOME

# Copy the jar file into the container
COPY ./target/cityinfo-0.0.1-SNAPSHOT.jar app.jar

# Expose the default Spring Boot port
EXPOSE 8080

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]