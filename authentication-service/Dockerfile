# Multi-stage Docker build for CrediYa Authentication Service
# This Dockerfile creates an optimized image for the authentication microservice

# Stage 1: Build the application
FROM openjdk:17-jdk-slim as builder

# Set working directory
WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Make mvnw executable
RUN chmod +x ./mvnw

# Download dependencies (this layer will be cached if pom.xml doesn't change)
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN ./mvnw clean package -DskipTests

# Stage 2: Create runtime image
FROM openjdk:17-jre-slim

# Set working directory
WORKDIR /app

# Create non-root user for security
RUN groupadd -r crediya && useradd -r -g crediya crediya

# Copy the built JAR from builder stage
COPY --from=builder /app/target/authentication-service-*.jar app.jar

# Change ownership to non-root user
RUN chown -R crediya:crediya /app

# Switch to non-root user
USER crediya

# Expose port
EXPOSE 8081

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:8081/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
