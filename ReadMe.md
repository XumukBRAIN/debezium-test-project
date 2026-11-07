# Order Processing System (CDC via Debezium & Kafka)

Тестовый проект, демонстрирующий реализацию паттерна **CDC (Change Data Capture)**.
Изменения из базы данных PostgreSQL (сервис заказов) в реальном времени транслируются через Debezium и Kafka в сервис уведомлений.

## Архитектура
* **order-service** (Порт `8081`) — генерирует заказы в БД.
* **notification-service** (Порт `8082`) — слушает топик Kafka и обрабатывает события.
* **Инфраструктура** — PostgreSQL 15, Kafka + Zookeeper, Debezium Connect (Порт `8083`).

---

## Быстрый запуск

### 1. Сборка и запуск контейнеров
Скомпилируйте приложения и поднимите всю инфраструктуру одной командой:
```bash
mvn clean package -DskipTests && docker compose up -d --build
```
*Liquibase автоматически создаст необходимую структуру таблиц при старте сервисов.*

### 2. Регистрация коннектора Debezium
Debezium запускается пустым. Чтобы он начал слушать PostgreSQL, отправьте POST-запрос с конфигурацией:

**Для PowerShell (Windows):**
```powershell
Invoke-RestMethod -Method Post -Uri http://localhost:8083/connectors -Headers @{"Content-Type"="application/json"} -Body '{
  "name": "postgres-orders-connector",
  "config": {
    "connector.class": "io.debezium.connector.postgresql.PostgresConnector",
    "tasks.max": "1",
    "database.hostname": "postgres",
    "database.port": "5432",
    "database.user": "user",
    "database.password": "password",
    "database.dbname": "orders_db",
    "topic.prefix": "orders.db",
    "table.include.list": "public.order",
    "plugin.name": "pgoutput",
    "decimal.handling.mode": "double"
  }
}'
```

**Для Linux / Git Bash (cURL):**
```bash
curl -X POST http://localhost:8083/connectors \
  -H "Content-Type: application/json" \
  -d '{
    "name": "postgres-orders-connector",
    "config": {
      "connector.class": "io.debezium.connector.postgresql.PostgresConnector",
      "tasks.max": "1",
      "database.hostname": "postgres",
      "database.port": "5432",
      "database.user": "user",
      "database.password": "password",
      "database.dbname": "orders_db",
      "topic.prefix": "orders.db",
      "table.include.list": "public.order",
      "plugin.name": "pgoutput",
      "decimal.handling.mode": "double"
    }
  }'
```

---

## Тестирование работы

1. **Сгенерируйте тестовые заказы** через API сервиса заказов:
   ```bash
   curl -X POST http://localhost:8081/api/test/generate-orders
   ```

2. **Проверьте логи сервиса уведомлений**, чтобы убедиться, что CDC-ивенты успешно долетают и десериализуются:
   ```bash
   docker logs notification-service --tail 50 -f
   ```

---

## Полезные команды при разработке

* **Посмотреть статус коннектора Debezium:**
  ```bash
  curl http://localhost:8083/connectors/postgres-orders-connector/status
  ```
* **Полный сброс данных и кэша сборки (для чистой пересборки):**
  ```bash
  docker compose down -v && docker builder prune -f && docker compose up -d --build
  ```