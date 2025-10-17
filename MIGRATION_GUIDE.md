# Migration Guide: Node.js to Java MCP Server

This guide explains how to migrate from the Node.js/TypeScript MCP server to the Java/Spring AI MCP server.

## Why Migrate to Java?

### Advantages of Java Implementation

1. **Production-Ready Framework**: Spring Boot is battle-tested for enterprise applications
2. **Better Integration**: Native Spring AI MCP support with official SDK
3. **Type Safety**: Java's strong typing provides compile-time safety
4. **Performance**: Better memory management and threading for high-load scenarios
5. **Ecosystem**: Access to vast Java/Spring ecosystem and libraries
6. **Enterprise Support**: Better tooling, monitoring, and observability options

### When to Use Each Version

**Use Node.js Version When:**
- Quick prototyping and testing
- Need web interface for manual testing
- JavaScript/TypeScript is your primary stack
- Simpler deployment requirements

**Use Java Version When:**
- Production deployment
- Enterprise environment
- Need better performance under load
- Integrating with existing Java infrastructure
- Want official Spring AI MCP support

## Architecture Comparison

### Node.js Implementation
```
Node.js Runtime
└── TypeScript Application
    ├── @modelcontextprotocol/sdk
    ├── Express.js (Web server)
    └── Axios (HTTP client)
```

### Java Implementation
```
JVM
└── Spring Boot Application
    ├── Spring AI MCP Server Starter
    ├── RestTemplate (HTTP client)
    └── Project Reactor (Reactive streams)
```

## Key Differences

| Aspect | Node.js | Java |
|--------|---------|------|
| **Configuration** | Environment variables | application.properties + Environment variables |
| **Tools Definition** | Manual JSON schema | @Tool annotations |
| **HTTP Client** | Axios | RestTemplate |
| **Error Handling** | try-catch + Axios interceptors | try-catch + @ExceptionHandler |
| **Logging** | console.error | SLF4J/Logback |
| **Transport** | STDIO + HTTP (port 3001) | STDIO only |

## Migration Steps

### Step 1: Build the Java Application

```bash
cd order-api-mcp-java

# Build with Maven
mvn clean package

# Verify JAR was created
ls -lh target/order-api-mcp-1.0.0.jar
```

### Step 2: Update Claude Desktop Configuration

Replace your Node.js configuration:

**Before (Node.js):**
```json
{
  "mcpServers": {
    "order-api": {
      "command": "docker",
      "args": ["exec", "-i", "order-api-mcp-container", "node", "dist/index.js"]
    }
  }
}
```

**After (Java):**
```json
{
  "mcpServers": {
    "order-api-java": {
      "command": "java",
      "args": [
        "-jar",
        "/Users/storos/workspace/chatbot_genai_mcp/order-api-mcp-java/target/order-api-mcp-1.0.0.jar"
      ],
      "env": {
        "ORDER_API_URL": "http://localhost:8080/api/orders"
      }
    }
  }
}
```

### Step 3: Restart Claude Desktop

Close and reopen Claude Desktop to load the new configuration.

### Step 4: Test the Integration

Try these commands in Claude:
- "Tüm siparişleri listele"
- "Yeni bir sipariş oluştur"
- "Sipariş 1'i göster"

## Docker Deployment

### Using Docker Compose

The `docker-compose.yml` now includes both implementations:

```bash
# Start all services including Java MCP
docker-compose up -d

# View logs
docker-compose logs -f order-api-mcp-java

# Stop services
docker-compose down
```

### Building Docker Image Manually

```bash
cd order-api-mcp-java

# Use the build script
./build-docker.sh

# Or manually
mvn clean package
docker build -t order-api-mcp-java:latest .
```

## Code Structure Comparison

### Tool Definition

**Node.js/TypeScript:**
```typescript
const tools: Tool[] = [
  {
    name: "create_order",
    description: "Yeni bir sipariş oluşturur...",
    inputSchema: {
      type: "object",
      properties: {
        customerName: { type: "string" },
        // ...
      }
    }
  }
];
```

**Java/Spring AI:**
```java
@Tool(description = "Yeni bir sipariş oluşturur...")
public String createOrder(Order order) {
    Order createdOrder = orderApiService.createOrder(order);
    return objectMapper.writeValueAsString(createdOrder);
}
```

### HTTP Client

**Node.js/TypeScript:**
```typescript
const response = await axios.post(ORDER_API_BASE_URL, orderData);
return {
  content: [{ type: "text", text: JSON.stringify(response.data, null, 2) }]
};
```

**Java/Spring AI:**
```java
HttpEntity<Order> request = new HttpEntity<>(order, createHeaders());
ResponseEntity<Order> response = restTemplate.exchange(
    config.getBaseUrl(),
    HttpMethod.POST,
    request,
    Order.class
);
return objectMapper.writeValueAsString(response.getBody());
```

## Configuration Management

### Node.js (.env or environment)
```bash
ORDER_API_URL=http://localhost:8080/api/orders
```

### Java (application.properties)
```properties
order.api.base-url=${ORDER_API_URL:http://localhost:8080/api/orders}
spring.ai.mcp.server.transport=stdio
spring.ai.mcp.server.name=order-api-mcp-server
spring.ai.mcp.server.version=1.0.0
```

## Troubleshooting

### Common Issues

#### 1. Maven Build Fails

**Problem:** Spring AI dependencies not found

**Solution:**
```bash
# Make sure Maven can access Spring repositories
mvn dependency:resolve -U

# Check pom.xml has correct repositories
cat pom.xml | grep -A 5 repositories
```

#### 2. Java Version Mismatch

**Problem:** Unsupported class file version

**Solution:**
```bash
# Check Java version
java -version

# Should be Java 17 or higher
# If not, install Java 17:
# macOS: brew install openjdk@17
# Linux: sudo apt install openjdk-17-jdk
```

#### 3. Connection Refused to Order API

**Problem:** Cannot connect to http://localhost:8080

**Solution:**
```bash
# Check if Order API is running
docker ps | grep order-api-container

# If not running, start it
cd order-api
docker build -t order-api:latest .
docker-compose up -d order-api
```

#### 4. Claude Desktop Not Detecting Server

**Problem:** Java MCP server doesn't appear in Claude

**Solution:**
1. Check the JAR file exists: `ls -lh target/order-api-mcp-1.0.0.jar`
2. Verify config path is absolute in `claude_desktop_config.json`
3. Check Claude logs: `~/Library/Logs/Claude/mcp*.log`
4. Completely quit and restart Claude Desktop

## Performance Comparison

### Startup Time
- **Node.js**: ~1-2 seconds
- **Java**: ~3-5 seconds (includes JVM startup + Spring context initialization)

### Memory Usage
- **Node.js**: ~100-150 MB
- **Java**: ~200-300 MB (JVM + Spring context)

### Request Latency (avg)
- **Node.js**: ~50-100ms
- **Java**: ~30-80ms (after warmup)

### Throughput
- **Node.js**: Good for moderate load
- **Java**: Better for high load and concurrent requests

## Rollback Plan

If you need to rollback to Node.js:

```bash
# 1. Stop Java version
docker-compose stop order-api-mcp-java

# 2. Revert Claude Desktop config to Node.js version

# 3. Restart Node.js MCP server
docker-compose up -d order-api-mcp

# 4. Restart Claude Desktop
```

## Next Steps

After migration:

1. **Monitor Performance**: Check logs and Claude Desktop performance
2. **Test All Tools**: Verify all MCP tools work correctly
3. **Update Documentation**: Update team docs with new configuration
4. **CI/CD Pipeline**: Update build/deployment scripts for Java
5. **Add Tests**: Write unit and integration tests for Java implementation

## Additional Resources

- [Spring AI MCP Documentation](https://docs.spring.io/spring-ai/reference/api/mcp/mcp-overview.html)
- [Java MCP SDK GitHub](https://github.com/modelcontextprotocol/java-sdk)
- [Spring Boot Reference](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Project README](README.md)
- [Java MCP README](order-api-mcp-java/README.md)

## Support

For issues or questions:
1. Check existing documentation
2. Review logs in `~/Library/Logs/Claude/`
3. Open an issue in the project repository
