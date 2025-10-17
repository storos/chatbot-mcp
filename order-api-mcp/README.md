# Order API MCP Server (Java/Spring AI)

Order Management API için Model Context Protocol (MCP) sunucusu - Java/Spring Boot implementasyonu.

## Açıklama

Bu MCP sunucusu, Docker container'da çalışan Spring Boot Order API'ye bağlanır ve AI modellerinin (Claude gibi) sipariş yönetimi işlemlerini gerçekleştirmesini sağlar. Spring AI MCP SDK kullanılarak geliştirilmiştir.

## Teknoloji Stack

### Core Technologies
- **Java 17** - Programming language
- **Spring Boot 3.4.1** - Application framework
- **Spring AI MCP** - Model Context Protocol implementation
- **Maven 3.9+** - Build tool

### Dependencies
- **Spring AI MCP Server Starter** - MCP protocol implementation
- **Spring Boot Starter Web** - REST client (RestTemplate)
- **Lombok** - Boilerplate code reduction
- **Jackson** - JSON serialization/deserialization
- **SLF4J/Logback** - Logging

## Özellikler

MCP sunucusu aşağıdaki araçları (tools) sağlar:

- **create_order**: Yeni sipariş oluşturur
- **get_all_orders**: Tüm siparişleri listeler
- **get_order_by_id**: ID'ye göre sipariş getirir
- **update_order**: Mevcut siparişi günceller
- **delete_order**: Sipariş siler

## Kurulum

### Gereksinimler

- Java 17 veya üzeri
- Maven 3.9 veya üzeri
- Docker (container'larda çalıştırmak için)

### Maven ile Build

```bash
# Bağımlılıkları yükle ve derleme yap
mvn clean package

# Test'leri atlayarak hızlı build
mvn clean package -DskipTests
```

## Kullanım

### 1. Order API'nin Çalıştığından Emin Olun

Order API Docker container'ının çalıştığından emin olun:

```bash
docker ps | grep order-api-container
curl http://localhost:8080/api/orders
```

### 2. MCP Server'ı Çalıştırın

#### Yerel Olarak Çalıştırma

```bash
# Maven ile
mvn spring-boot:run

# veya JAR dosyası ile
java -jar target/order-api-mcp-1.0.0.jar
```

#### Docker ile Çalıştırma

```bash
# Docker image oluştur
docker build -t order-api-mcp-java:latest .

# Container'ı çalıştır
docker run --name order-api-mcp-java \
  -e ORDER_API_URL=http://host.docker.internal:8080/api/orders \
  order-api-mcp-java:latest
```

#### Docker Compose ile Çalıştırma

```bash
# Tüm servisleri başlat
docker-compose up -d

# Sadece Java MCP server'ı başlat
docker-compose up -d order-api order-api-mcp-java
```

### 3. Claude Desktop'ta Yapılandırın

Claude Desktop yapılandırma dosyanıza (`claude_desktop_config.json`) aşağıdaki konfigürasyonu ekleyin:

**macOS**: `~/Library/Application Support/Claude/claude_desktop_config.json`

**Windows**: `%APPDATA%\Claude\claude_desktop_config.json`

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

### 4. Claude Desktop'ı Yeniden Başlatın

Yapılandırmayı yüklemek için Claude Desktop'ı kapatıp yeniden açın.

## Yapılandırma

### Ortam Değişkenleri

| Değişken | Açıklama | Varsayılan Değer |
|----------|----------|------------------|
| `ORDER_API_URL` | Order API'nin base URL'i | `http://localhost:8080/api/orders` |

### Application Properties

[application.properties](src/main/resources/application.properties) dosyasında aşağıdaki ayarları yapabilirsiniz:

```properties
# Order API URL
order.api.base-url=${ORDER_API_URL:http://localhost:8080/api/orders}

# MCP Server Configuration
spring.ai.mcp.server.transport=stdio
spring.ai.mcp.server.name=order-api-mcp-server
spring.ai.mcp.server.version=1.0.0

# Logging
logging.level.com.example.orderapimcp=DEBUG
```

## Kullanım Örnekleri

Claude ile aşağıdaki gibi komutlar kullanabilirsiniz:

### Sipariş Oluşturma
```
Yeni bir sipariş oluştur:
- Müşteri: Ahmet Yılmaz
- Email: ahmet@example.com
- Ürün: Laptop, 1 adet, 15000 TL
- Durum: PENDING
```

### Tüm Siparişleri Listeleme
```
Tüm siparişleri listele
```

### ID'ye Göre Sipariş Getirme
```
ID'si 1 olan siparişi göster
```

### Sipariş Güncelleme
```
Sipariş 1'i CONFIRMED durumuna güncelle
```

### Sipariş Silme
```
Sipariş 1'i sil
```

## Proje Yapısı

```
order-api-mcp-java/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/orderapimcp/
│   │   │       ├── config/
│   │   │       │   ├── AppConfig.java          # RestTemplate, ObjectMapper beans
│   │   │       │   └── OrderApiConfig.java     # Configuration properties
│   │   │       ├── mcp/
│   │   │       │   └── OrderMcpTools.java      # MCP Tools (@Tool annotations)
│   │   │       ├── model/
│   │   │       │   ├── Order.java              # Order entity
│   │   │       │   ├── OrderItem.java          # OrderItem entity
│   │   │       │   └── OrderStatus.java        # Order status enum
│   │   │       ├── service/
│   │   │       │   └── OrderApiService.java    # HTTP client for Order API
│   │   │       └── OrderApiMcpApplication.java # Main Spring Boot application
│   │   └── resources/
│   │       └── application.properties          # Application configuration
│   └── test/
│       └── java/
├── Dockerfile                                   # Docker image definition
├── .dockerignore                                # Docker ignore file
├── .gitignore                                   # Git ignore file
├── pom.xml                                      # Maven configuration
└── README.md                                    # This file
```

## API Referansı

### MCP Tools

#### create_order
Yeni sipariş oluşturur.

**Parametreler:**
- `customerName` (String): Müşteri adı
- `customerEmail` (String): Müşteri e-posta adresi
- `items` (List<OrderItem>): Sipariş kalemleri
  - `itemName` (String): Ürün adı
  - `quantity` (Integer): Miktar
  - `price` (BigDecimal): Birim fiyat
- `totalAmount` (BigDecimal): Toplam tutar
- `status` (OrderStatus): Sipariş durumu (PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED)

**Dönen Değer:** JSON formatında sipariş bilgisi

#### get_all_orders
Tüm siparişleri getirir.

**Parametreler:** Yok

**Dönen Değer:** JSON formatında sipariş listesi

#### get_order_by_id
ID'ye göre sipariş getirir.

**Parametreler:**
- `orderId` (Long): Sipariş ID'si

**Dönen Değer:** JSON formatında sipariş bilgisi

#### update_order
Mevcut siparişi günceller.

**Parametreler:**
- `orderId` (Long): Güncellenecek sipariş ID'si
- Diğer parametreler `create_order` ile aynı

**Dönen Değer:** JSON formatında güncellenmiş sipariş bilgisi

#### delete_order
Sipariş siler.

**Parametreler:**
- `orderId` (Long): Silinecek sipariş ID'si

**Dönen Değer:** Başarı mesajı

## Geliştirme

### IDE Setup

#### IntelliJ IDEA
1. Import as Maven project
2. Enable Lombok plugin
3. Enable annotation processing

#### VS Code
1. Install Java Extension Pack
2. Install Lombok Annotations Support
3. Maven will auto-configure

### Loglama

Uygulama SLF4J/Logback kullanır. Log seviyelerini `application.properties` dosyasından ayarlayabilirsiniz:

```properties
logging.level.root=INFO
logging.level.com.example.orderapimcp=DEBUG
logging.level.org.springframework.ai.mcp=DEBUG
```

### Testing

```bash
# Tüm testleri çalıştır
mvn test

# Belirli bir test sınıfını çalıştır
mvn test -Dtest=OrderMcpToolsTest
```

## Spring AI MCP Hakkında

Bu proje Spring AI MCP framework'ünü kullanır. Spring AI MCP, Model Context Protocol'ü Spring ekosistemi ile entegre eder:

- **@Tool Annotation**: MCP araçlarını tanımlamak için
- **STDIO Transport**: Claude Desktop ile iletişim için
- **Reactive Support**: Project Reactor tabanlı asenkron işlemler
- **Auto-configuration**: Spring Boot starter ile otomatik yapılandırma

Daha fazla bilgi için: https://docs.spring.io/spring-ai/reference/api/mcp/mcp-overview.html

## Troubleshooting

### MCP Server bağlanamıyor

1. Order API container'ının çalıştığından emin olun:
   ```bash
   docker ps | grep order-api-container
   curl http://localhost:8080/api/orders
   ```

2. MCP server loglarını kontrol edin:
   ```bash
   # Docker container logları
   docker logs order-api-mcp-java-container

   # Yerel çalıştırma
   java -jar target/order-api-mcp-1.0.0.jar
   ```

3. Claude Desktop yapılandırmasını kontrol edin

### Maven build hataları

```bash
# Cache'i temizle
mvn clean

# Dependency'leri yeniden indir
mvn dependency:purge-local-repository

# Build
mvn clean package -U
```

### Port çakışması

Order API varsayılan olarak 8080 portunu kullanır. Farklı bir port kullanıyorsanız `ORDER_API_URL` ortam değişkenini güncelleyin.

### Spring AI MCP dependency çözümlenemiyor

Spring AI milestone repository'lerinin `pom.xml`'de tanımlı olduğundan emin olun:

```xml
<repositories>
    <repository>
        <id>spring-milestones</id>
        <url>https://repo.spring.io/milestone</url>
    </repository>
</repositories>
```

## Node.js vs Java Karşılaştırması

| Özellik | Node.js (TypeScript) | Java (Spring AI) |
|---------|---------------------|------------------|
| **Runtime** | Node.js | JVM |
| **Language** | TypeScript | Java 17 |
| **Framework** | Express.js | Spring Boot |
| **MCP SDK** | @modelcontextprotocol/sdk | Spring AI MCP |
| **Build Tool** | npm | Maven |
| **Package Size** | ~50MB (with node_modules) | ~30MB (JAR) |
| **Startup Time** | ~1-2s | ~3-5s |
| **Memory Usage** | ~100-150MB | ~200-300MB |
| **Production Ready** | Yes | Yes |
| **Type Safety** | Strong (TypeScript) | Strong (Java) |
| **Ecosystem** | NPM | Maven Central |
| **Web Interface** | Yes (port 3001) | No (STDIO only) |

## Lisans

MIT

## Katkıda Bulunma

Pull request'ler kabul edilir. Büyük değişiklikler için lütfen önce bir issue açarak ne değiştirmek istediğinizi tartışın.

## İletişim

Sorularınız için issue açabilirsiniz.
