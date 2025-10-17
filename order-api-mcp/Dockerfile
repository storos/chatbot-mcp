# Multi-stage build for Java MCP Server

# Stage 1: Build
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy built JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Set environment variables
ENV ORDER_API_URL=http://order-api-container:8080/api/orders

# Expose port (not used in STDIO mode, but good for documentation)
EXPOSE 8081

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
