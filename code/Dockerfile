# Build stage
FROM maven:3.9.4-eclipse-temurin-21 AS build
WORKDIR /app
# Set Maven to skip SSL certificate validation
ENV MAVEN_OPTS="-Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true"
# Copy pom.xml files for dependency resolution
COPY pom.xml .
COPY boot/pom.xml ./boot/
COPY domain/pom.xml ./domain/
COPY application/pom.xml ./application/
COPY infrastructure/pom.xml ./infrastructure/
COPY infrastructure/input-rest/pom.xml ./infrastructure/input-rest/
COPY infrastructure/input-rest/rest-config/pom.xml ./infrastructure/input-rest/rest-config/
COPY infrastructure/input-rest/rest-security/pom.xml ./infrastructure/input-rest/rest-security/
COPY infrastructure/input-rest/user-rest/pom.xml ./infrastructure/input-rest/user-rest/
COPY infrastructure/output-adapters/pom.xml ./infrastructure/output-adapters/
COPY infrastructure/jpa-main-postgres/pom.xml ./infrastructure/jpa-main-postgres/
# Download dependencies only (this layer can be cached)
RUN mvn dependency:go-offline -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true
# Copy source files and build
COPY . .
RUN mvn clean package -DskipTests -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true

# Runtime stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
# Copy the built jar from boot module's target directory
COPY --from=build /app/boot/target/*.jar app.jar
# Create a non-root user
RUN useradd -r -u 1000 -g root spring-user
USER spring-user
# Set environment variables
ENV SPRING_PROFILES_ACTIVE=pro
# Expose the port the app runs on
EXPOSE 8080
# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
