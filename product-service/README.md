# product-service

A **product catalogue with stock control**. It provides CRUD for products, a stock-availability check, and a RabbitMQ consumer that reduces stock when an order message arrives.

> ⚠️ **It registers as `inventory-service`** (`spring.application.name=inventory-service`), the same name as the real inventory-service. Its endpoints live under `/inventory/product/**`, so the gateway's `/inventory/**` route sends traffic to *both* apps at random. It also uses port **8566**, like TaskManagement. See [docs/KNOWN_ISSUES.md](../docs/KNOWN_ISSUES.md#routing-and-service-discovery).

## At a glance

| | |
|---|---|
| Port | **8566** (clashes with TaskManagement) |
| Eureka name | `inventory-service` (!) |
| Gateway prefix | `/inventory/product` (through the `/inventory/**` route) |
| Database | MySQL `product_service` |
| Messaging | RabbitMQ `localhost:5672`, `guest` / `guest`, consumes queue `FinalProductQueue` |
| Stack | Spring Boot 2.7.15 · Spring Cloud 2021.0.8 · Spring Data JPA · Spring AMQP · LoadBalancer · springdoc |
| Base package | `com.microservice.product` |
| Needs | MySQL schema `product_service`, RabbitMQ (optional: the listener retries, but HTTP still works), env var `GATEWAY_SECRET` |

## Run

```bash
mvn spring-boot:run
# while the clash exists, run it on another port if TaskManagement is also up:
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8567
```

## API

Through the gateway at `http://localhost:8556`. Every endpoint needs a JWT.

| Method | Path | Body / params | Notes |
|---|---|---|---|
| `POST` | `/inventory/product/create-product` | `ProductBo` | `createdBy` from the `id` header |
| `GET` | `/inventory/product/get-all-product` | — | |
| `GET` | `/inventory/product/get-single-id/{productId}` | — | |
| `PUT` | `/inventory/product/update-product` | `ProductBo` | |
| `DELETE` | `/inventory/product/delete-product/{id}` | — | soft delete (`isDelete=true, isActive=false`) |
| `GET` | `/inventory/product/check-stock` | `?productId=&requiredUnits=` | `true` if enough stock is available |

## Messaging

`config/RabbitMQConfig` declares a durable queue **`FinalProductQueue`** and a topic exchange **`productExchange`** (routing key constant `productRoutingKey`). **No binding** between them is declared.

`config/ProductMessageListener` consumes `FinalProductQueue`. It expects a JSON string:

```json
{ "productId": 42, "orderedUnit": 3 }
```

It calls `ProductServiceImpl.reduceStock(productId, orderedUnit)`. Failures are only printed, and there's no dead-letter queue. **Nothing in this repository publishes to the queue yet.** The order flow (salesorder-service) is the natural producer.

## Data model

| Entity → table | Fields |
|---|---|
| `ProductVo` → `product` | `productId`, `productName`, `productSpecification`, `productType`, `minStocks`, `maxStocks`, `avaliablesStocks`, `startDate`, `endDate`, `isActive`, `isDelete` |

It's the same shape as inventory-service's `ProductVo`, but a separate table in a separate database.

## Calls and callers

No outgoing HTTP calls. Incoming HTTP requests need `X-Gateway-Secret` (`gatewayauth/GatewayAuthFilter`). AMQP messages aren't affected by that filter.

## Configuration

| Property | Value |
|---|---|
| `server.port` | 8566 |
| `spring.application.name` | `inventory-service` (should be `product-service`) |
| `gateway.secret` | `${GATEWAY_SECRET}` |
| `spring.datasource.*` | `jdbc:mysql://localhost:3306/product_service`, `root` / `admin` |
| `spring.rabbitmq.*` | `localhost:5672`, `guest` / `guest` |

## Code layout

```
com.microservice.product
├── controller/   ProductController
├── service/      ProductServiceImpl (incl. reduceStock)
├── repository/   ProductRepository (soft-delete @Modifying queries)
├── vo/  bo/      ProductVo, ProductBo, BaseBo
├── config/       RabbitMQConfig, ProductMessageListener
└── gatewayauth/  GatewayAuthFilter
```

## Tests

Both test classes are in `com.softtwig.product…`, a package copied from another project, so they fail with "Unable to find a @SpringBootConfiguration".

## Known issues

* 🔴 Eureka name and port clashes (see above).
* 🟡 The exchange isn't bound to the queue, and there's no producer and no dead-letter handling.
* 🟡 It duplicates inventory-service's product catalogue. Decide which one owns products.

See [docs/KNOWN_ISSUES.md](../docs/KNOWN_ISSUES.md).
