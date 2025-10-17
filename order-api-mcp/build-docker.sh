#!/bin/bash

# Build script for Order API MCP Java Server

set -e

echo "======================================"
echo "Building Order API MCP Java Server"
echo "======================================"

# Build with Maven
echo "Step 1: Building with Maven..."
mvn clean package -DskipTests

# Build Docker image
echo "Step 2: Building Docker image..."
docker build -t order-api-mcp-java:latest .

echo "======================================"
echo "Build completed successfully!"
echo "======================================"
echo ""
echo "To run the container:"
echo "  docker run --name order-api-mcp-java \\"
echo "    -e ORDER_API_URL=http://host.docker.internal:8080/api/orders \\"
echo "    order-api-mcp-java:latest"
echo ""
echo "Or use docker-compose:"
echo "  docker-compose up -d order-api-mcp-java"
