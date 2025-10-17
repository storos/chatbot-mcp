# Chat API - E-commerce Customer Support Chatbot

E-ticaret müşteri destek chatbot API'si. OpenAI GPT-4 kullanarak müşterilere sipariş listeleme, görüntüleme ve iptal etme konularında destek sağlar.

## Özellikler

- **OpenAI GPT-4 Entegrasyonu**: Doğal dil işleme ile akıllı müşteri desteği
- **MCP (Model Context Protocol) İle Dinamik Fonksiyon Çağrıları**: Sipariş işlemleri için order-api-mcp'yi dinamik olarak kullanır
- **Session Yönetimi**: Her kullanıcı için session bazlı konuşma geçmişi
- **Türkçe Destek**: Müşteri desteği Türkçe olarak yapılır
- **Generic MCP Client**: Fonksiyonları MCP sunucusundan dinamik olarak keşfeder, hard-coded fonksiyon mantığı içermez

## Tech Stack

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring WebFlux** (Reactive HTTP Client)
- **OpenAI API** (GPT-4)
- **Lombok**
- **Jackson** (JSON Processing)
- **Maven**

## API Endpoints

### POST /api/chat
Chatbot ile konuşma endpoint'i.

**Request Body:**
```json
{
  "message": "Siparişlerimi göster",
  "sessionId": "optional-session-id"
}
```

**Response:**
```json
{
  "response": "İşte siparişleriniz: ...",
  "sessionId": "session-uuid",
  "functionCalled": false
}
```

### GET /api/chat/health
Servis sağlık kontrolü.

## Yapılandırma

### Environment Variables

- `OPENAI_API_KEY`: OpenAI API anahtarı (zorunlu)
- `MCP_SERVER_URL`: MCP sunucu URL'i (varsayılan: http://order-api-mcp:8081)

### application.properties

```properties
server.port=8082
openai.api.key=${OPENAI_API_KEY}
openai.api.url=https://api.openai.com/v1/chat/completions
openai.model=gpt-4
mcp.server.url=${MCP_SERVER_URL:http://order-api-mcp:8081}
```

## Çalıştırma

### Docker ile Çalıştırma

1. OpenAI API anahtarınızı environment variable olarak ayarlayın:
```bash
export OPENAI_API_KEY=your-api-key-here
```

2. Docker imajını oluşturun:
```bash
docker build -t chat-api:latest .
```

3. Docker Compose ile tüm servisleri başlatın:
```bash
cd ..
docker-compose up -d
```

### Maven ile Çalıştırma

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--openai.api.key=your-api-key"
```

## Test Etme

### cURL ile Test

```bash
# Chatbot ile konuşma
curl -X POST http://localhost:8082/api/chat \
  -H "Content-Type: application/json" \
  -d '{
    "message": "Siparişlerimi göster",
    "sessionId": "test-session-123"
  }'

# Health check
curl http://localhost:8082/api/chat/health
```

### Örnek Konuşmalar

**Sipariş Listeleme:**
```
Kullanıcı: "Siparişlerimi göster"
Bot: "İşte siparişleriniz: [sipariş listesi]"
```

**Sipariş Detayı:**
```
Kullanıcı: "1 numaralı siparişimin detaylarını göster"
Bot: "İşte 1 numaralı siparişinizin detayları: [detaylar]"
```

**Sipariş İptali:**
```
Kullanıcı: "2 numaralı siparişimi iptal et"
Bot: "2 numaralı siparişiniz başarıyla iptal edildi."
```

## Mimari

```
┌─────────────┐         ┌──────────────┐         ┌─────────────────┐
│   Client    │────────▶│   Chat API   │────────▶│   OpenAI API    │
└─────────────┘         └──────────────┘         └─────────────────┘
                              │
                              │ MCP Protocol
                              ▼
                        ┌──────────────┐
                        │ Order-API-MCP│
                        └──────────────┘
                              │
                              ▼
                        ┌──────────────┐
                        │  Order API   │
                        └──────────────┘
```

## Fonksiyon Çağrı Akışı

1. Kullanıcı mesajı chat-api'ye gelir
2. OpenAI GPT-4'e gönderilir (MCP fonksiyonları ile birlikte)
3. GPT-4 uygun fonksiyonu seçer (örn: get_all_orders)
4. Chat-api, MCP client üzerinden order-api-mcp'yi çağırır
5. Sonuç GPT-4'e geri gönderilir
6. GPT-4 kullanıcıya doğal dilde cevap üretir
7. Cevap kullanıcıya döndürülür

## Geliştirme Notları

- **Generic MCP Integration**: `McpClientService` MCP sunucusundan fonksiyonları dinamik olarak keşfeder
- **OpenAI Function Calling**: GPT-4'ün function calling özelliği kullanılarak MCP fonksiyonları çağrılır
- **Session Management**: Her session için konuşma geçmişi tutulur
- **Error Handling**: Hataları yakalayarak kullanıcıya anlamlı mesajlar döndürür
- **Türkçe System Prompt**: Chatbot Türkçe müşteri destek elemanı olarak yapılandırılmıştır

## Port Bilgileri

- **Chat API**: 8082
- **Order API MCP**: 8081
- **Order API**: 8080

## Bağımlılıklar

Chat API'nin çalışması için aşağıdaki servisler gereklidir:
1. Order API (port 8080)
2. Order API MCP (port 8081)
3. OpenAI API (harici)

Docker Compose bu bağımlılıkları otomatik olarak yönetir.
