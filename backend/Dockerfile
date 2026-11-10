# ============================================================
# CineBook Backend - Production Multi-Stage Dockerfile
# ============================================================

# Stage 1: Build the Spring Boot JAR with Maven
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

# Copy pom.xml and pre-fetch dependencies for caching
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and package
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime Image (Lightweight Temurin 17 JRE)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Non-root user for security
RUN addgroup -S cinebook && adduser -S cinebook -G cinebook
USER cinebook

# Copy built JAR from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose server port
EXPOSE 8080

# Environment variables with sensible fallbacks
ENV PORT=8080 \
    JAVA_OPTS="-Xms256m -Xmx512m"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

