# Build stage
FROM maven:3.9.5-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom.xml first for dependency caching
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the built JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Default environment variables (can be overridden)
ENV SERVER_PORT=8085
ENV DB_HOST=localhost
ENV DB_PORT=5432
ENV DB_NAME=medibook
ENV DB_USERNAME=postgres

# Expose the port (Render expects 8080)
EXPOSE 8085

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]