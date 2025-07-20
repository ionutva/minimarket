# Mini Market Order Service

## Setup

Run the service using Docker Compose:

```bash
docker-compose up --build
```

## Sample Requests

```bash
curl -X POST http://localhost:8080/orders -H "Content-Type: application/json" -d '{"accountId":"acc-123","symbol":"AAPL","side":"BUY","quantity":10}'
curl http://localhost:8080/orders?accountId=acc-123
```
