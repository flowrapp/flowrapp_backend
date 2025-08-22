# Multi-stage build for GraalVM native image
FROM ghcr.io/graalvm/native-image-community:21 AS native-build

# Install required packages for native compilation
RUN microdnf install -y findutils maven && microdnf clean all

WORKDIR /app

# Copy pom files for dependency caching
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

# Download dependencies (this layer can be cached)
RUN mvn dependency:go-offline -B

# Copy source code
COPY code .

# Build native executable
    # Note: This requires significant memory (16GB+ recommended)
    ENV JAVA_TOOL_OPTIONS="-Xmx16g"
RUN mvn clean package -Pnative -DskipTests -B

# Runtime stage - ultra minimal
FROM debian:bookworm-slim AS runtime

# Install minimal required packages
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    ca-certificates \
    curl && \
    rm -rf /var/lib/apt/lists/* && \
    apt-get clean

# Create a non-root user for security
RUN groupadd -r spring && useradd -r -g spring spring-user

WORKDIR /app

# Copy the native executable from build stage
COPY --from=native-build /app/boot/target/flowrapp-native ./flowrapp-native

# Make executable and change ownership
RUN chmod +x ./flowrapp-native && chown spring-user:spring ./flowrapp-native

# Switch to non-root user
USER spring-user

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=pro

# Expose the port
EXPOSE 8080

# Health check - using native executable's fast startup
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/flowrapp/actuator/health || exit 1

# Run the native application
ENTRYPOINT ["./flowrapp-native"]
