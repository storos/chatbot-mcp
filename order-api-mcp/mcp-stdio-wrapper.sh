#!/bin/bash
# MCP STDIO Wrapper for Java Spring Boot MCP Server
# This script bridges STDIO communication to REST API

# Server URL
SERVER_URL="${MCP_SERVER_URL:-http://localhost:8081}"

# Read JSON-RPC request from stdin
while IFS= read -r line; do
    # Parse the JSON-RPC request
    echo "$line" | jq -c '.' > /tmp/mcp-request.json

    # Extract method
    method=$(echo "$line" | jq -r '.method')

    case "$method" in
        "initialize")
            # Return server capabilities
            cat <<EOF
{
  "jsonrpc": "2.0",
  "id": $(echo "$line" | jq '.id'),
  "result": {
    "protocolVersion": "1.0.0",
    "serverInfo": {
      "name": "order-api-mcp-server",
      "version": "1.0.0"
    },
    "capabilities": {
      "tools": {}
    }
  }
}
EOF
            ;;
        "tools/list")
            # Get tools from REST API
            tools=$(curl -s "${SERVER_URL}/mcp/tools" | jq '.tools')
            cat <<EOF
{
  "jsonrpc": "2.0",
  "id": $(echo "$line" | jq '.id'),
  "result": {
    "tools": $tools
  }
}
EOF
            ;;
        "tools/call")
            # Call tool via REST API
            tool_name=$(echo "$line" | jq -r '.params.name')
            arguments=$(echo "$line" | jq -c '.params.arguments')

            # Map tool calls to REST endpoints
            case "$tool_name" in
                "get_all_orders")
                    result=$(curl -s "${SERVER_URL}/mcp/orders")
                    ;;
                "get_order_by_id")
                    order_id=$(echo "$arguments" | jq -r '.orderId')
                    result=$(curl -s "${SERVER_URL}/mcp/orders/${order_id}")
                    ;;
                "create_order")
                    result=$(curl -s -X POST "${SERVER_URL}/mcp/orders" \
                        -H "Content-Type: application/json" \
                        -d "$arguments")
                    ;;
                "update_order")
                    order_id=$(echo "$arguments" | jq -r '.orderId')
                    result=$(curl -s -X PUT "${SERVER_URL}/mcp/orders/${order_id}" \
                        -H "Content-Type: application/json" \
                        -d "$arguments")
                    ;;
                "cancel_order")
                    order_id=$(echo "$arguments" | jq -r '.orderId')
                    result=$(curl -s -X DELETE "${SERVER_URL}/mcp/orders/${order_id}")
                    ;;
            esac

            cat <<EOF
{
  "jsonrpc": "2.0",
  "id": $(echo "$line" | jq '.id'),
  "result": {
    "content": [
      {
        "type": "text",
        "text": $(echo "$result" | jq -Rs '.')
      }
    ]
  }
}
EOF
            ;;
    esac
done
