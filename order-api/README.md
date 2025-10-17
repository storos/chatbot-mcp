# Order API

A RESTful API for order management built with Spring Boot. This is a mock implementation that returns dummy data for demonstration purposes.

## Technology Stack

- **Java 17**
- **Spring Boot 3.2.0**
- **Maven 3.9+**
- **Docker**

## Features

- RESTful API with CRUD operations
- Request validation with Bean Validation
- Global exception handling
- Dockerized application
- Mock/dummy data responses
- RESTful best practices
- **OpenAPI/Swagger documentation with interactive UI**

## Project Structure

```
order-api/
├── src/
│   ├── main/
│   │   ├── java/com/example/orderapi/
│   │   │   ├── controller/      # REST controllers
│   │   │   ├── service/         # Business logic
│   │   │   ├── model/           # Domain models
│   │   │   ├── dto/             # Data Transfer Objects
│   │   │   ├── exception/       # Custom exceptions & handlers
│   │   │   └── OrderApiApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
├── Dockerfile
├── pom.xml
└── README.md
```

## API Documentation

### Swagger UI (Interactive Documentation)
Once the application is running, access the interactive API documentation at:
```
http://localhost:8080/swagger-ui.html
```

### OpenAPI Specification (JSON)
```
http://localhost:8080/v3/api-docs
```

## API Endpoints

### Base URL
```
http://localhost:8080/api/orders
```

### Endpoints

| Method | Endpoint | Description | Status Code |
|--------|----------|-------------|-------------|
| POST | `/api/orders` | Create a new order | 201 Created |
| GET | `/api/orders` | Get all orders | 200 OK |
| GET | `/api/orders/{id}` | Get order by ID | 200 OK |
| PUT | `/api/orders/{id}` | Update an order | 200 OK |
| DELETE | `/api/orders/{id}` | Delete an order | 204 No Content |

### Sample Request/Response

#### Create Order (POST /api/orders)
**Request:**
```json
{
  "customerName": "John Doe",
  "customerEmail": "john.doe@example.com",
  "items": [
    {
      "itemName": "Laptop",
      "quantity": 1,
      "price": 999.99
    },
    {
      "itemName": "Mouse",
      "quantity": 2,
      "price": 25.50
    }
  ],
  "totalAmount": 1050.99,
  "status": "PENDING"
}
```

**Response (201 Created):**
```json
{
  "id": 1234,
  "customerName": "John Doe",
  "customerEmail": "john.doe@example.com",
  "items": [
    {
      "itemName": "Laptop",
      "quantity": 1,
      "price": 999.99
    },
    {
      "itemName": "Mouse",
      "quantity": 2,
      "price": 25.50
    }
  ],
  "totalAmount": 1050.99,
  "status": "PENDING",
  "orderDate": "2025-01-15T10:30:00"
}
```

### Order Status Values
- `PENDING`
- `CONFIRMED`
- `SHIPPED`
- `DELIVERED`
- `CANCELLED`

## Running the Application

### Prerequisites
- Java 17 or higher
- Maven 3.9 or higher
- Docker (for containerized deployment)

### Option 1: Run with Maven

1. **Navigate to project directory:**
   ```bash
   cd order-api
   ```

2. **Build the project:**
   ```bash
   mvn clean package
   ```

3. **Run the application:**
   ```bash
   mvn spring-boot:run
   ```

4. **Access the API:**
   - API: `http://localhost:8080/api/orders`
   - Swagger UI: `http://localhost:8080/swagger-ui.html`

### Option 2: Run with Docker

1. **Navigate to project directory:**
   ```bash
   cd order-api
   ```

2. **Build Docker image:**
   ```bash
   docker build -t order-api:latest .
   ```

3. **Run Docker container:**
   ```bash
   docker run -p 8080:8080 order-api:latest
   ```

4. **Access the API:**
   - API: `http://localhost:8080/api/orders`
   - Swagger UI: `http://localhost:8080/swagger-ui.html`

### Option 3: Run JAR directly

1. **Build the project:**
   ```bash
   mvn clean package
   ```

2. **Run the JAR:**
   ```bash
   java -jar target/order-api-1.0.0.jar
   ```

## Testing the API

### Using cURL

**Create an order:**
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "John Doe",
    "customerEmail": "john.doe@example.com",
    "items": [
      {
        "itemName": "Laptop",
        "quantity": 1,
        "price": 999.99
      }
    ],
    "totalAmount": 999.99,
    "status": "PENDING"
  }'
```

**Get all orders:**
```bash
curl http://localhost:8080/api/orders
```

**Get order by ID:**
```bash
curl http://localhost:8080/api/orders/1
```

**Update an order:**
```bash
curl -X PUT http://localhost:8080/api/orders/1 \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "Jane Smith",
    "customerEmail": "jane.smith@example.com",
    "items": [
      {
        "itemName": "Keyboard",
        "quantity": 1,
        "price": 75.00
      }
    ],
    "totalAmount": 75.00,
    "status": "CONFIRMED"
  }'
```

**Delete an order:**
```bash
curl -X DELETE http://localhost:8080/api/orders/1
```

## Error Handling

The API returns consistent error responses:

**Example Error Response (400 Bad Request):**
```json
{
  "timestamp": "2025-01-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/orders",
  "details": [
    "customerName: Customer name is required",
    "customerEmail: Invalid email format"
  ]
}
```

**Example Error Response (404 Not Found):**
```json
{
  "timestamp": "2025-01-15T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Order with ID 999 not found",
  "path": "/api/orders/999"
}
```

## Docker Commands

**Stop running container:**
```bash
docker ps  # Find container ID
docker stop <container_id>
```

**Remove container:**
```bash
docker rm <container_id>
```

**Remove image:**
```bash
docker rmi order-api:latest
```

**View logs:**
```bash
docker logs <container_id>
```

## Configuration

Application configuration is located in `src/main/resources/application.properties`:

- **Server Port:** 8080 (default)
- **Application Name:** order-api
- **Logging Level:** DEBUG for application, INFO for root

## Notes

- This is a **mock API** that returns dummy data
- No actual database is used
- All operations return success responses with dummy/generated data
- IDs are generated based on current timestamp
- Perfect for testing, demos, and integration testing

## License

This project is for demonstration purposes.
