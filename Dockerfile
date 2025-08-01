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

# Debug: Check files before build
RUN ls -la && ls -la gradle && ls -la src

# Build the application with verbose output
RUN ./gradlew build -x test --no-daemon --info --stacktrace

# Runtime stage
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy the built jar from build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Expose port
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "app.jar"]
