FROM eclipse-temurin:17-jdk AS build

WORKDIR /app

# Copy gradle files
COPY gradlew gradlew.bat ./
COPY gradle ./gradle
COPY build.gradle settings.gradle ./

# Make gradlew executable
RUN chmod +x ./gradlew

# Copy source code
COPY src ./src

# Download dependencies first for better caching
RUN ./gradlew dependencies --no-daemon

# Build the application
RUN ./gradlew build -x test --no-daemon

# Verify the JAR was created
RUN ls -la /app/build/libs/

# Runtime stage
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy the built jar from build stage
COPY --from=build /app/build/libs/*.jar /app/app.jar

# Expose port
EXPOSE 8080

# Add health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application with production JVM settings
CMD ["java", \
     "-Xms256m", \
     "-Xmx512m", \
     "-XX:+UseG1GC", \
     "-XX:MaxGCPauseMillis=200", \
     "-Djava.security.egd=file:/dev/./urandom", \
     "-jar", "/app/app.jar"]
