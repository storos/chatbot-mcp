# Order Management System - MCP Project

Spring Boot Order API ve MCP (Model Context Protocol) Server içeren tam entegre sipariş yönetim sistemi.

## 🏗️ Proje Yapısı

```
chatbot_genai_mcp/
├── order-api/              # Spring Boot REST API
│   ├── src/
│   ├── Dockerfile
│   └── pom.xml
├── order-api-mcp/          # MCP Server (Node.js/TypeScript) - Legacy
│   ├── src/
│   ├── Dockerfile
│   ├── web-server.js       # Browser arayüzü
│   └── package.json
├── order-api-mcp-java/     # MCP Server (Java/Spring AI) - Production
│   ├── src/
│   ├── Dockerfile
│   ├── pom.xml
│   └── build-docker.sh
└── docker-compose.yml      # Tüm servisler
```

## 🚀 Hızlı Başlangıç

### Tüm Servisleri Docker Compose ile Başlatın

```bash
# Tüm servisleri başlat
docker-compose up -d

# Durumu kontrol et
docker-compose ps

# Logları görüntüle
docker-compose logs -f
```

### Servislere Erişim

- **Order API (Spring Boot)**: http://localhost:8080/api/orders
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **MCP Web Interface (Node.js)**: http://localhost:3001
- **MCP API**: http://localhost:3001/api/tools
- **MCP Server (Java)**: STDIO mode (Claude Desktop entegrasyonu için)

## 📦 Servisler

### 1. Order API (Spring Boot)
Java 17 ve Spring Boot 3.2.0 ile geliştirilmiş RESTful API.

**Özellikler:**
- CRUD operasyonları
- Bean Validation
- Global exception handling
- OpenAPI/Swagger dokümantasyonu
- Mock data

**Endpoints:**
- `GET /api/orders` - Tüm siparişleri listele
- `GET /api/orders/{id}` - ID'ye göre sipariş getir
- `POST /api/orders` - Yeni sipariş oluştur
- `PUT /api/orders/{id}` - Sipariş güncelle
- `DELETE /api/orders/{id}` - Sipariş sil

### 2. MCP Server (Node.js/TypeScript) - Legacy
Order API için Model Context Protocol sunucusu. Web arayüzü ile test için ideal.

**Özellikler:**
- Browser-based web interface
- REST API wrapper
- Claude Desktop entegrasyonu
- MCP Inspector desteği

**MCP Araçları:**
- `create_order` - Yeni sipariş oluşturur
- `get_all_orders` - Tüm siparişleri listeler
- `get_order_by_id` - ID'ye göre sipariş getirir
- `update_order` - Sipariş günceller
- `delete_order` - Sipariş siler

### 3. MCP Server (Java/Spring AI) - Production Ready
Spring AI MCP SDK kullanarak geliştirilmiş production-ready MCP sunucusu.

**Özellikler:**
- Spring AI MCP Server Starter
- STDIO transport (Claude Desktop native entegrasyon)
- Type-safe Java implementation
- RestTemplate based HTTP client
- Comprehensive error handling
- Structured logging

**Teknoloji Stack:**
- Java 17
- Spring Boot 3.4.1
- Spring AI MCP
- Maven 3.9+
- Project Lombok

**MCP Araçları:**
Aynı araçlar Node.js versiyonu ile uyumlu şekilde implement edilmiştir.

## 🌐 Browser'dan Kullanım

### Web Interface
http://localhost:3001 adresinde interaktif bir arayüz:

- 📋 Siparişleri listele
- ➕ Yeni sipariş oluştur
- 🔍 Sipariş ara
- ✏️ Sipariş güncelle
- 🗑️ Sipariş sil
- 🧪 Hızlı test butonları

## 🤖 AI ile Kullanım

### Claude Desktop Konfigürasyonu

#### Seçenek 1: Java MCP Server (Önerilen)

`~/Library/Application Support/Claude/claude_desktop_config.json`:

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

#### Seçenek 2: Node.js MCP Server (Docker)

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

Claude Desktop'ı yeniden başlatın ve şu komutları kullanın:
- "Tüm siparişleri listele"
- "Ahmet Yılmaz için yeni bir sipariş oluştur"
- "Sipariş 1'i CONFIRMED durumuna güncelle"

## 🛠️ Geliştirme

### Order API'yi Geliştirmek

```bash
cd order-api
mvn spring-boot:run
```

### MCP Server'ı Geliştirmek

#### Node.js MCP Server

```bash
cd order-api-mcp

# TypeScript derle
npm run build

# Web interface başlat
npm run web

# MCP Inspector
npm run inspector
```

#### Java MCP Server

```bash
cd order-api-mcp-java

# Maven ile derle ve çalıştır
mvn spring-boot:run

# veya JAR oluştur
mvn clean package

# JAR'ı çalıştır
java -jar target/order-api-mcp-1.0.0.jar
```

### Docker Image'larını Yeniden Build Etmek

```bash
# Order API
docker build -t order-api:latest ./order-api

# MCP Server (Node.js)
docker build -t order-api-mcp:latest ./order-api-mcp

# MCP Server (Java)
cd order-api-mcp-java && ./build-docker.sh

# Veya tümü için
docker-compose build
```

## 🧪 Test

### API Testleri

```bash
# Tüm siparişleri listele
curl http://localhost:8080/api/orders

# Yeni sipariş oluştur
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "Ahmet Yılmaz",
    "customerEmail": "ahmet@example.com",
    "items": [{
      "itemName": "Laptop",
      "quantity": 1,
      "price": 15000
    }],
    "totalAmount": 15000,
    "status": "PENDING"
  }'
```

### MCP Web API Testleri

```bash
# MCP araçlarını listele
curl http://localhost:3001/api/tools

# Siparişleri MCP üzerinden getir
curl http://localhost:3001/api/orders
```

## 📊 Container Yönetimi

### Durumu Kontrol Et
```bash
docker-compose ps
```

### Logları Görüntüle
```bash
# Tüm servisler
docker-compose logs -f

# Sadece Order API
docker-compose logs -f order-api

# Sadece MCP Server
docker-compose logs -f order-api-mcp
```

### Servisleri Durdur
```bash
docker-compose down
```

### Servisleri Yeniden Başlat
```bash
docker-compose restart
```

### Container'ları Temizle
```bash
# Container'ları durdur ve sil
docker-compose down

# Volume'ları da sil
docker-compose down -v

# Image'ları da sil
docker-compose down --rmi all
```

## 🔧 Konfigürasyon

### Ortam Değişkenleri

**Order API:**
- `SPRING_PROFILES_ACTIVE`: Spring profile (default: default)
- Port: 8080

**MCP Server (Node.js):**
- `ORDER_API_URL`: Order API base URL (default: http://order-api:8080/api/orders)
- Port: 3001

**MCP Server (Java):**
- `ORDER_API_URL`: Order API base URL (default: http://localhost:8080/api/orders)
- Mode: STDIO (no web port)

### docker-compose.yml Özelleştirme

```yaml
services:
  order-api:
    environment:
      - SPRING_PROFILES_ACTIVE=production
    ports:
      - "8081:8080"  # Farklı port kullan

  order-api-mcp:
    environment:
      - ORDER_API_URL=http://order-api:8080/api/orders
    ports:
      - "3002:3001"  # Farklı port kullan
```

## 📚 Dokümantasyon

- **Order API README**: [order-api/README.md](order-api/README.md)
- **MCP Server (Node.js) README**: [order-api-mcp/README.md](order-api-mcp/README.md)
- **MCP Server (Java) README**: [order-api-mcp-java/README.md](order-api-mcp-java/README.md)
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI Spec**: http://localhost:8080/v3/api-docs

## 🔄 Node.js vs Java Karşılaştırması

| Özellik | Node.js MCP | Java MCP |
|---------|-------------|----------|
| **Language** | TypeScript | Java 17 |
| **Framework** | Express.js | Spring Boot |
| **MCP SDK** | @modelcontextprotocol/sdk | Spring AI MCP |
| **Build Tool** | npm | Maven |
| **Startup Time** | ~1-2s | ~3-5s |
| **Memory Usage** | ~100-150MB | ~200-300MB |
| **Web Interface** | Yes (port 3001) | No (STDIO only) |
| **Production Ready** | Yes | Yes (Recommended) |
| **Type Safety** | Strong | Strong |
| **Use Case** | Quick testing, Web UI | Production, Enterprise |

## 🐛 Troubleshooting

### Container başlamıyor
```bash
# Logları kontrol et
docker-compose logs

# Health check durumu
docker inspect order-api-container | jq '.[0].State.Health'
```

### Port çakışması
```bash
# Portları değiştir (docker-compose.yml'de)
# Veya çalışan servisleri durdur
lsof -ti:8080 | xargs kill -9
lsof -ti:3001 | xargs kill -9
```

### Network sorunları
```bash
# Network'ü yeniden oluştur
docker-compose down
docker network prune
docker-compose up -d
```

## 📝 Lisans

MIT

## 🤝 Katkıda Bulunma

1. Fork edin
2. Feature branch oluşturun (`git checkout -b feature/amazing-feature`)
3. Commit edin (`git commit -m 'Add amazing feature'`)
4. Push edin (`git push origin feature/amazing-feature`)
5. Pull Request açın

## 📞 İletişim

Sorularınız için issue açabilirsiniz.
