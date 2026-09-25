# ============================================
# Stage 1: Build the application
# ============================================
FROM eclipse-temurin:23-jdk AS builder

WORKDIR /app

# Copy Gradle wrapper & build files first (layer caching)
COPY gradlew gradlew
COPY gradle/ gradle/
COPY build.gradle settings.gradle ./

# Make gradlew executable
RUN chmod +x gradlew

# Download dependencies (cached layer)
RUN ./gradlew dependencies --no-daemon || true

# Copy source code
COPY src/ src/

# Build the JAR (skip tests for faster builds)
RUN ./gradlew bootJar --no-daemon -x test

# ============================================
# Stage 2: Run the application
# ============================================
FROM eclipse-temurin:23-jre

WORKDIR /app

# Create a non-root user for security
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Copy the built JAR from builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Set ownership
RUN chown -R appuser:appuser /app

USER appuser

# Expose the application port
EXPOSE 8080

# JVM tuning for containers
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:InitialRAMPercentage=50.0"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
