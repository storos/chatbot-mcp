# E-commerce Post-Sales Support Assistant 🤖

An intelligent AI-powered customer support assistant for e-commerce platforms, built with **Model Context Protocol (MCP)** for dynamic function discovery and **OpenAI GPT-4** for natural language understanding.

## 🌟 Overview

This project implements a complete post-sales support chatbot system that helps customers with their order management through natural conversation. The system uses MCP to dynamically discover available functions from backend services, making it extensible and maintainable without hardcoded function definitions.

### Key Features

- **🔍 Dynamic Function Discovery**: Automatically discovers available operations from MCP server
- **🤖 Generic Parameter Collection**: Universal rules for gathering required parameters before function calls
- **💬 Conversational AI**: Natural language processing with OpenAI GPT-4
- **📝 Session-based History**: Maintains conversation context across interactions
- **🌐 Modern Web UI**: React TypeScript interface with real-time updates
- **🐳 Full Docker Support**: Containerized microservices architecture
- **🔄 CORS-enabled**: Browser-friendly API with proper CORS configuration

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                       Chat UI (React)                        │
│                    http://localhost:3000                     │
└───────────────────────────┬─────────────────────────────────┘
                            │ REST API
┌───────────────────────────▼─────────────────────────────────┐
│                    Chat API (Spring Boot)                    │
│              OpenAI Integration + MCP Client                 │
│                    http://localhost:8082                     │
└───────────────────────────┬─────────────────────────────────┘
                            │ MCP Protocol
┌───────────────────────────▼─────────────────────────────────┐
│              Order API MCP Server (Spring Boot)              │
│              Dynamic Tool Discovery + Execution              │
│                    http://localhost:8081                     │
└───────────────────────────┬─────────────────────────────────┘
                            │ REST API
┌───────────────────────────▼─────────────────────────────────┐
│                Order API (Spring Boot)                       │
│                   Order Management CRUD                      │
│                    http://localhost:8080                     │
└─────────────────────────────────────────────────────────────┘
```

## 🚀 Quick Start

### Prerequisites

- Docker & Docker Compose
- OpenAI API Key

### 1. Set Your OpenAI API Key

```bash
export OPENAI_API_KEY="your-openai-api-key-here"
```

### 2. Start All Services

```bash
docker-compose up -d
```

### 3. Access the Chat Interface

Open your browser and navigate to: **http://localhost:3000**

### 4. Start Chatting

Try these examples:
- "Show me my orders"
- "I want to cancel my order" (the assistant will ask which one if you have multiple)
- "Cancel order number 1"
- "What's the status of order 2?"

## 📦 Components

### 1. **Chat UI** (React + TypeScript)
A modern, responsive chat interface with:
- Real-time message updates
- Conversation history display
- Session management
- Function call indicators
- Beautiful animations

**Location**: `chat-ui/`
**Port**: 3000
**Tech Stack**: React 18, TypeScript, Vite, Axios

### 2. **Chat API** (Spring Boot)
AI-powered chatbot backend that:
- Integrates with OpenAI GPT-4
- Maintains conversation history per session
- Dynamically discovers MCP tools
- Applies generic parameter collection rules
- Handles CORS for browser access

**Location**: `chat-api/`
**Port**: 8082
**Tech Stack**: Java 17, Spring Boot 3.2.0, WebClient

**Key Features**:
- **Generic Prompt Rules**: Automatically collects all required parameters before calling any function
- **Dynamic Tool Discovery**: Builds OpenAI function definitions from MCP server at runtime
- **Session Management**: Maintains conversation context across multiple interactions

### 3. **Order API MCP Server** (Spring Boot)
MCP server that exposes order management operations as AI-callable tools:

**Location**: `order-api-mcp/`
**Port**: 8081
**Tech Stack**: Java 17, Spring Boot 3.2.0

**Available Tools**:
- `get_all_orders` - Retrieves all orders
- `get_order_by_id` - Gets a specific order by ID
- `create_order` - Creates a new order
- `update_order` - Updates an existing order
- `cancel_order` - Cancels an order

Each tool includes JSON schema for parameter validation.

### 4. **Order API** (Spring Boot)
Core order management REST API with:
- CRUD operations for orders
- Bean validation
- Exception handling
- Swagger documentation
- In-memory data storage

**Location**: `order-api/`
**Port**: 8080
**Tech Stack**: Java 17, Spring Boot 3.2.0, OpenAPI 3

## 🎯 How It Works

### Dynamic Function Discovery Flow

1. **Tool Discovery**: Chat API queries MCP Server for available tools
2. **Schema Parsing**: Converts MCP tool definitions to OpenAI function format
3. **Runtime Binding**: OpenAI receives function definitions dynamically
4. **Parameter Collection**: Generic rules ensure all required params are collected
5. **Function Execution**: MCP Client calls the appropriate MCP tool
6. **Response Processing**: AI formats the result for natural conversation

### Generic Parameter Collection

The system uses **universal rules** instead of function-specific instructions:

```
1. Collect ALL required parameters before calling any function
2. NEVER assume values - always ask the user explicitly
3. When multiple options exist, ALWAYS ask user to specify
4. For ambiguous requests:
   a) First list available options
   b) Then ask user to make specific choice
5. Only call functions with complete parameters
```

This approach makes the system **extensible** - any new MCP function automatically benefits from these rules without updating the system prompt.

## 🛠️ Development

### Run Services Locally

#### Chat UI
```bash
cd chat-ui
npm install
npm run dev
```

#### Chat API
```bash
cd chat-api
mvn spring-boot:run
```

#### Order API MCP
```bash
cd order-api-mcp
mvn spring-boot:run
```

#### Order API
```bash
cd order-api
mvn spring-boot:run
```

### Rebuild Docker Images

```bash
# Build all services
docker-compose build

# Or build individually
docker build -t order-api:latest ./order-api
docker build -t order-api-mcp:latest ./order-api-mcp
docker build -t chat-api:latest ./chat-api
docker build -t chat-ui:latest ./chat-ui
```

## 🧪 Testing

### Test Chat API
```bash
curl -X POST http://localhost:8082/api/chat \
  -H "Content-Type: application/json" \
  -d '{
    "message": "Show me my orders",
    "sessionId": "test-session-001"
  }'
```

### Test MCP Server
```bash
# List available tools
curl http://localhost:8081/mcp/tools

# Get all orders via MCP
curl -X POST http://localhost:8081/mcp/call \
  -H "Content-Type: application/json" \
  -d '{
    "name": "get_all_orders",
    "arguments": {}
  }'
```

### Test Order API
```bash
# Get all orders
curl http://localhost:8080/api/orders

# Create order
curl -X POST http://localhost:8080/api/orders \
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
    "status": "PENDING",
    "address": "123 Main St"
  }'
```

## 📊 Container Management

### Check Status
```bash
docker-compose ps
```

### View Logs
```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f chat-api
```

### Stop Services
```bash
docker-compose down
```

### Restart Services
```bash
docker-compose restart
```

## 🔧 Configuration

### Environment Variables

**Chat API**:
- `OPENAI_API_KEY`: Your OpenAI API key (required)
- `MCP_SERVER_URL`: MCP server endpoint (default: http://order-api-mcp:8081)

**Order API MCP**:
- `ORDER_API_URL`: Order API endpoint (default: http://order-api:8080/api/orders)

**Chat UI**:
- `VITE_API_URL`: Chat API endpoint (default: http://localhost:8082)

### Customizing docker-compose.yml

```yaml
services:
  chat-api:
    environment:
      - OPENAI_API_KEY=your-key-here
      - MCP_SERVER_URL=http://order-api-mcp:8081
    ports:
      - "8082:8082"
```

## 📚 API Documentation

- **Order API Swagger**: http://localhost:8080/swagger-ui.html
- **Order API OpenAPI Spec**: http://localhost:8080/v3/api-docs
- **Chat API Health**: http://localhost:8082/api/chat/health

## 🎨 Features Highlight

### 1. Dynamic Tool Discovery
No hardcoded function definitions. The system automatically:
- Queries MCP server for available tools
- Parses tool schemas and descriptions
- Generates OpenAI function definitions
- Updates capabilities list in system prompt

### 2. Generic Parameter Collection
Universal rules apply to all functions:
- Prevents assumptions about missing parameters
- Handles ambiguous requests intelligently
- Lists options before asking for selection
- Ensures complete data before execution

### 3. Conversation History
Session-based memory:
- Maintains context across interactions
- Remembers previous function calls
- Provides coherent multi-turn conversations
- Isolated sessions per user

### 4. Modern UI/UX
Beautiful chat interface with:
- User messages on the right (blue)
- AI messages on the left (white)
- Loading indicators
- Function call badges
- Smooth animations
- Responsive design

## 🐛 Troubleshooting

### CORS Errors
- Ensure `chat-api` has `WebConfig.java` with proper CORS settings
- Check browser console for specific CORS issues
- Verify allowed origins include `http://localhost:3000`

### OpenAI API Errors
- Verify `OPENAI_API_KEY` is set correctly
- Check API key has sufficient credits
- Review chat-api logs: `docker logs chat-api-container`

### Container Won't Start
```bash
# Check logs
docker-compose logs

# Check health status
docker inspect order-api-container | jq '.[0].State.Health'

# Restart specific service
docker-compose restart chat-api
```

### Port Conflicts
```bash
# Find process using port
lsof -ti:8080

# Kill process
lsof -ti:8080 | xargs kill -9
```

## 🚧 Roadmap

- [ ] Add authentication and user management
- [ ] Implement persistent database (PostgreSQL)
- [ ] Add more MCP tools (shipping, returns, refunds)
- [ ] Implement rate limiting
- [ ] Add comprehensive test coverage
- [ ] Deploy to production environment
- [ ] Add monitoring and observability
- [ ] Multi-language support

## 📝 License

MIT

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📞 Contact

For questions or issues, please open a GitHub issue.

---

**Built with** ❤️ **using Model Context Protocol, OpenAI GPT-4, Spring Boot, and React**
