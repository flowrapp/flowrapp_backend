# Build stage
FROM maven:3.9.4-eclipse-temurin-21 AS build
WORKDIR /app

# Copy all pom.xml files preserving directory structure
COPY code/pom.xml .
COPY code/boot/pom.xml ./boot/
COPY code/domain/pom.xml ./domain/
COPY code/application/pom.xml ./application/
COPY code/infrastructure/pom.xml ./infrastructure/
COPY code/infrastructure/input-rest/pom.xml ./infrastructure/input-rest/
COPY code/infrastructure/input-rest/rest-config/pom.xml ./infrastructure/input-rest/rest-config/
COPY code/infrastructure/input-rest/rest-main-api/pom.xml ./infrastructure/input-rest/rest-main-api/
COPY code/infrastructure/output-adapters/pom.xml ./infrastructure/output-adapters/
COPY code/infrastructure/jpa-business-bbdd/pom.xml ./infrastructure/jpa-business-bbdd/
COPY code/infrastructure/thymeleaf-mail/pom.xml ./infrastructure/thymeleaf-mail/

# Download dependencies only (this layer can be cached)
RUN mvn dependency:go-offline -B

# Copy source files and build
COPY code .
RUN mvn clean package -DskipTests -B

# Runtime stage  
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Install curl for health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Copy the built jar from boot module's target directory
COPY --from=build /app/boot/target/*.jar app.jar

# Create a non-root user for security
RUN groupadd -r spring && useradd -r -g spring spring-user
USER spring-user

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=pro
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC -XX:+UseContainerSupport"

# Expose the port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
