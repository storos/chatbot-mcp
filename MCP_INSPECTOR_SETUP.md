# MCP Inspector Setup Guide

## Problem
MCP Inspector expects STDIO transport, but the Java MCP server runs as a REST API on port 8081.

## Solutions

### Solution 1: Use REST API Directly (Recommended for Testing)

Since the Java MCP server is a REST API, you can test it directly without MCP Inspector:

#### Using curl
```bash
# List tools
curl http://localhost:8081/mcp/tools | jq

# Get all orders
curl http://localhost:8081/mcp/orders | jq

# Get order by ID
curl http://localhost:8081/mcp/orders/1 | jq

# Create order
curl -X POST http://localhost:8081/mcp/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "John Doe",
    "customerEmail": "john@example.com",
    "items": [{
      "itemName": "Laptop",
      "quantity": 1,
      "price": 999.99
    }],
    "totalAmount": 999.99,
    "status": "PENDING"
  }' | jq
```

#### Using Postman/Insomnia
Import this collection:

**Base URL**: `http://localhost:8081`

**Endpoints**:
- `GET /mcp/tools` - List available tools
- `GET /mcp/orders` - Get all orders
- `GET /mcp/orders/{id}` - Get order by ID
- `POST /mcp/orders` - Create order
- `PUT /mcp/orders/{id}` - Update order
- `DELETE /mcp/orders/{id}` - Delete order

#### Using Browser
Open http://localhost:8081/mcp/tools in your browser to see available tools.

### Solution 2: Use Docker Exec with STDIO Wrapper

If you need STDIO-based MCP Inspector connection:

```bash
# Run MCP Inspector with Docker exec
npx @modelcontextprotocol/inspector \
  docker exec -i order-api-mcp-container \
  sh -c "/app/mcp-stdio-wrapper.sh"
```

### Solution 3: Create Web UI for Testing

Create a simple HTML page for interactive testing:

```bash
# Open the test page
open /Users/storos/workspace/chatbot_genai_mcp/order-api-mcp/test-ui.html
```

### Solution 4: Use the Old Node.js Version for MCP Inspector

If you absolutely need MCP Inspector with STDIO:

```bash
# Go to the legacy Node.js directory
cd /Users/storos/workspace/chatbot_genai_mcp/order-api-mcp-nodejs-legacy

# Install dependencies
npm install

# Build
npm run build

# Run with MCP Inspector
npx @modelcontextprotocol/inspector node dist/index.js
```

This will open MCP Inspector at http://localhost:6274 and connect to the Node.js MCP server.

## Comparison

| Method | Pros | Cons | Best For |
|--------|------|------|----------|
| **REST API (curl)** | Direct, fast, simple | No MCP protocol | Quick testing |
| **REST API (Postman)** | Full-featured, collections | Requires Postman | API development |
| **STDIO Wrapper** | Works with Inspector | Complex setup | MCP protocol testing |
| **Node.js Legacy** | Native MCP Inspector | Not using Java version | MCP protocol validation |

## Recommended Approach

**For Development & Testing**: Use REST API directly with curl or Postman

```bash
# Quick test
curl http://localhost:8081/mcp/tools | jq
curl http://localhost:8081/mcp/orders | jq
```

**For MCP Protocol Testing**: Use the Node.js legacy version with MCP Inspector

```bash
cd order-api-mcp-nodejs-legacy
npm run inspector
```

**For Production**: The Java REST API is production-ready and doesn't need MCP Inspector

## Current Setup

- **Java MCP Server**: Running on http://localhost:8081 (REST API)
- **Order API**: Running on http://localhost:8080 (Backend)
- **MCP Inspector**: Port 6274 (for STDIO-based MCP servers)

## Why Java MCP is REST API

The Java implementation uses Spring Boot REST controllers instead of STDIO because:

1. ✅ **Better for Production**: REST APIs are standard, well-supported
2. ✅ **Easy Integration**: Can be called from any HTTP client
3. ✅ **Monitoring**: Easier to monitor and log
4. ✅ **Scalability**: Can be load-balanced
5. ✅ **Testing**: Standard tools (Postman, curl, etc.)

## Testing the Java MCP Server

### Quick Health Check
```bash
# Check if server is running
curl http://localhost:8081/mcp/tools

# Expected output:
# {
#   "tools": [
#     {"name": "create_order", ...},
#     {"name": "get_all_orders", ...},
#     ...
#   ]
# }
```

### Full Test Suite
```bash
# 1. List tools
echo "=== Tools ==="
curl -s http://localhost:8081/mcp/tools | jq '.tools[].name'

# 2. Get orders
echo -e "\n=== Orders ==="
curl -s http://localhost:8081/mcp/orders | jq 'length'

# 3. Get specific order
echo -e "\n=== Order #1 ==="
curl -s http://localhost:8081/mcp/orders/1 | jq '{id, customerName, status}'

# 4. Create order
echo -e "\n=== Create Order ==="
curl -s -X POST http://localhost:8081/mcp/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "Test User",
    "customerEmail": "test@example.com",
    "items": [{"itemName": "Product", "quantity": 1, "price": 100}],
    "totalAmount": 100,
    "status": "PENDING"
  }' | jq '{id, customerName, status}'
```

## Support

For issues, check:
1. Container status: `docker ps`
2. Logs: `docker logs order-api-mcp-container`
3. Health: `curl http://localhost:8081/mcp/tools`
