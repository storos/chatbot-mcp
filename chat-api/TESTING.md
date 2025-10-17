# Chat API Testing Guide

## Current Status

✅ **chat-api successfully built and running on port 8082**

The service is operational but requires an OpenAI API key to function properly.

## Error Diagnosis

When testing without a valid OpenAI API key, you'll see:
- HTTP 200 response with error message
- Response: `"Üzgünüm, bir hata oluştu. Lütfen daha sonra tekrar deneyin."`
- Container logs show: `401 Unauthorized from POST https://api.openai.com/v1/chat/completions`

This is **expected behavior** - the service needs a valid OpenAI API key to communicate with GPT-4.

## Setup Instructions

### 1. Get OpenAI API Key

1. Visit [OpenAI Platform](https://platform.openai.com/api-keys)
2. Sign in or create an account
3. Generate a new API key
4. Copy the key (it starts with `sk-...`)

### 2. Configure the API Key

#### Option A: Using .env file (Recommended)

```bash
# Create .env file in project root
cd /Users/storos/workspace/chatbot_genai_mcp
echo "OPENAI_API_KEY=sk-your-actual-key-here" > .env
```

#### Option B: Export environment variable

```bash
export OPENAI_API_KEY=sk-your-actual-key-here
```

### 3. Restart the Container

```bash
cd /Users/storos/workspace/chatbot_genai_mcp
docker-compose restart chat-api
```

### 4. Verify the Configuration

Check that the API key is loaded:

```bash
docker logs chat-api-container | grep "Started ChatApiApplication"
```

## Testing the Chat API

### 1. Health Check (Works Without API Key)

```bash
curl http://localhost:8082/api/chat/health
```

Expected response:
```
Chat API is running
```

### 2. Chat Endpoint (Requires Valid API Key)

#### Test 1: List Orders
```bash
curl -X POST http://localhost:8082/api/chat \
  -H "Content-Type: application/json" \
  -d '{
    "message": "Siparişlerimi göster",
    "sessionId": "test-session-001"
  }'
```

Expected flow:
1. GPT-4 receives the message
2. GPT-4 decides to call `get_all_orders` function
3. chat-api calls order-api-mcp
4. order-api-mcp fetches orders from order-api
5. Result returned to GPT-4
6. GPT-4 formats response in Turkish
7. Response sent back to user

#### Test 2: View Order Details
```bash
curl -X POST http://localhost:8082/api/chat \
  -H "Content-Type: application/json" \
  -d '{
    "message": "1 numaralı siparişimin detaylarını göster",
    "sessionId": "test-session-001"
  }'
```

#### Test 3: Cancel Order
```bash
curl -X POST http://localhost:8082/api/chat \
  -H "Content-Type: application/json" \
  -d '{
    "message": "2 numaralı siparişimi iptal etmek istiyorum",
    "sessionId": "test-session-001"
  }'
```

#### Test 4: General Conversation
```bash
curl -X POST http://localhost:8082/api/chat \
  -H "Content-Type: application/json" \
  -d '{
    "message": "Merhaba, nasıl yardımcı olabilirsin?",
    "sessionId": "test-session-001"
  }'
```

## Expected Response Format

### Success Response
```json
{
  "response": "İşte siparişleriniz: [GPT-4 generated response in Turkish]",
  "sessionId": "test-session-001",
  "functionCalled": true,
  "functionName": "get_all_orders"
}
```

### Error Response (No API Key)
```json
{
  "response": "Üzgünüm, bir hata oluştu. Lütfen daha sonra tekrar deneyin.",
  "sessionId": "test-session-001",
  "functionCalled": false,
  "functionName": null
}
```

## Monitoring

### Check All Containers
```bash
docker ps
```

Expected output:
```
CONTAINER ID   IMAGE                  PORTS                    NAMES
...            chat-api:latest        0.0.0.0:8082->8082/tcp   chat-api-container
...            order-api-mcp:latest   0.0.0.0:8081->8081/tcp   order-api-mcp-container
...            order-api:latest       0.0.0.0:8080->8080/tcp   order-api-container
```

### Check Logs
```bash
# Chat API logs
docker logs -f chat-api-container

# Look for these success indicators:
# - "Started ChatApiApplication"
# - "Calling OpenAI API"
# - "Function call detected"
# - "Function result"
```

## Troubleshooting

### Issue: 401 Unauthorized
**Cause**: Invalid or missing OpenAI API key
**Solution**: Set a valid API key and restart container

### Issue: Connection refused to order-api-mcp
**Cause**: order-api-mcp container not running
**Solution**:
```bash
docker-compose up -d order-api-mcp
```

### Issue: "Üzgünüm, bir hata oluştu"
**Cause**: Various errors (check logs)
**Solution**:
```bash
docker logs chat-api-container | tail -50
```

## Architecture Verification

Verify the complete flow:

```bash
# 1. Check order-api
curl http://localhost:8080/api/orders

# 2. Check order-api-mcp
curl http://localhost:8081/mcp/tools

# 3. Check chat-api health
curl http://localhost:8082/api/chat/health

# 4. Test chat-api (requires OpenAI key)
curl -X POST http://localhost:8082/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "Siparişlerimi göster", "sessionId": "test"}'
```

## Next Steps

1. ✅ chat-api built and running
2. ⏳ Configure OpenAI API key
3. ⏳ Test complete integration flow
4. ⏳ Verify function calling works correctly
5. ⏳ Test session management
6. ⏳ Test error handling

## Notes

- The chatbot responds in Turkish (as configured in system prompt)
- Session management is implemented but conversation history is not persisted
- GPT-4 model is used by default (can be changed to gpt-3.5-turbo for lower cost)
- Function calling is automatic - GPT-4 decides when to call MCP functions
- All MCP functions are discovered dynamically (no hard-coded function logic)
