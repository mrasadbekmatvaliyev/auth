@echo off
set SPRING_PROFILES_ACTIVE=prod
set PORT=10000
echo Starting Spring Boot with production profile...
java -jar build/libs/auth-0.0.1-SNAPSHOT.jar
