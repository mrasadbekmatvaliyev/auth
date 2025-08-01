FROM gradle:8.5-jdk17 AS build

WORKDIR /home/gradle/src

# Copy build files
COPY --chown=gradle:gradle build.gradle settings.gradle ./

# Copy source code
COPY --chown=gradle:gradle src ./src

# Build the application
RUN gradle build -x test --no-daemon

# Runtime stage
FROM openjdk:17-jre-slim

WORKDIR /app

# Create app user
RUN addgroup --system --gid 1001 app && \
    adduser --system --uid 1001 --gid 1001 app

# Copy the built jar from build stage
COPY --from=build /home/gradle/src/build/libs/*.jar app.jar

# Change ownership
RUN chown app:app app.jar

# Switch to app user
USER app

# Expose port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
