FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy gradle wrapper and build files
COPY gradlew gradlew.bat ./
COPY gradle ./gradle
COPY build.gradle settings.gradle ./

# Make gradlew executable
RUN chmod +x gradlew

# Copy source code
COPY src ./src

# Build the application
RUN ./gradlew build -x test

# Expose port
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "build/libs/auth-0.0.1-SNAPSHOT.jar"]
