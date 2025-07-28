# --- Stage 1: Build the application ---
FROM gradle:8.8-jdk17-alpine AS build

# Set the working directory
WORKDIR /home/gradle/src

# Copy only the necessary build files first to leverage Docker's layer cache.
# If these files don't change, Docker won't re-download dependencies.
COPY build.gradle settings.gradle ./

# First, resolve and download dependencies. This creates a cached layer.
RUN gradle dependencies --no-daemon

# Then, copy the application source code. Changes here won't invalidate the dependency layer.
COPY src ./src

# Build the application, creating a layered JAR.
# --no-daemon is recommended for CI/CD environments.
RUN gradle bootJar --no-daemon

# --- Stage 2: Create the final, optimized production image ---
FROM eclipse-temurin:17-jre-jammy

# Set the working directory inside the final container
WORKDIR /app

# Set the active Spring profile to 'prod'.
# This is the standard way to configure the profile for a containerized app.
ENV SPRING_PROFILES_ACTIVE=prod

# Copy the layered JAR from the build stage.
# Spring Boot's layered mode separates the JAR into logical layers,
# which improves Docker's caching and speeds up subsequent builds.
COPY --from=build /home/gradle/src/build/libs/*.jar app.jar

# Expose the port the application runs on
EXPOSE 8080

ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC"

# The entrypoint command to run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]