# salesorder-service

Intended home of **sales orders, quotes and invoices**. Today, only **quotes** are implemented. The sales-order endpoints are stubs and invoices have no endpoints.

> ⚠️ **Not reachable through the gateway yet**, for two reasons:
> 1. It never registers with Eureka on :8555. Its properties have no `eureka.client.serviceUrl.defaultZone`, so the client uses the default `localhost:8761`, and `/salesorder/**` returns 503.
> 2. The quotes and invoice controllers are mapped to `/quotes` and `/Invoice`, outside the service's `/salesorder` prefix.
>
> See [docs/KNOWN_ISSUES.md](../docs/KNOWN_ISSUES.md#routing-and-service-discovery).

## At a glance

| | |
|---|---|
| Port | **8568** |
| Eureka name | `salesorder-service` (not actually registered, see above) |
| Gateway prefix | `/salesorder` |
| Database | MySQL `ms_mybiz_salesorder` |
| Stack | Spring Boot 2.7.15 · Spring Cloud 2021.0.8 · Spring Data JPA · springdoc |
| Base package | `com.clinch.crm.salesorder` |
| Needs | MySQL schema, env var `GATEWAY_SECRET` |

## Run

There's no Maven wrapper in this module. Use a system `mvn`:

```bash
mvn spring-boot:run
```

## API

All of these need `X-Gateway-Secret`, so today they can only be called directly on port 8568 with that header.

### Sales orders: `/salesorder` (stubs)

| Method | Path | Returns |
|---|---|---|
| `GET` | `/salesorder/listSalesorder` | `"hello list"` |
| `POST` | `/salesorder/createSalesorder` | `"hello create"` |
| `PUT` | `/salesorder/updateSalesorder` | `"hello update"` |
| `DELETE` | `/salesorder/deleteSalesorder` | `"hello delete"` |

### Quotes: `/quotes`

| Method | Path | Body / params |
|---|---|---|
| `POST` | `/quotes/create-quotes` | `QuotesBO` |
| `PUT` | `/quotes/update-quotes` | `QuotesBO` |
| `GET` | `/quotes/getAllQuotes` | `?page=&size=` |
| `GET` | `/quotes/getQuotes/{id}` | — |
| `DELETE` | `/quotes/deleteQuotes/{id}` | — |

### Invoices: `/Invoice`

`InvoiceController` exists but has no endpoints.

## Data model

| Entity → table | Fields |
|---|---|
| `QuotesEntity` → `Quotes_table` | `quotesId`, `quotesUniNumber`, `accountName`, `pricebookName`, `assigntedTo`, `salutation`, `email`, `number`, `street`, `city`, `state`, `country`, `pincode`, `productTypeName`, `productName`, `orderedStocks`, `finalPrice`, `taxes`, `discount`, `total`, `isDelete`, `createdBy`, `createdOn`, `modifiedBy`, `modifiedOn` (audit fields are **strings** here) |
| `SalesOrderEntity` → `salesorder_table` | `saleorderId` only |
| `InvoiceEntity` → `Invoice_table` | `invoiceId` only |

## Calls and callers

No outgoing calls. The planned flow is that placing an order publishes `{productId, orderedUnit}` to RabbitMQ `FinalProductQueue`, which product-service consumes to reduce stock. That isn't implemented.

## Configuration

| Property | Value |
|---|---|
| `server.port` | 8568 |
| `gateway.secret` | `${GATEWAY_SECRET}` |
| `spring.datasource.*` | `jdbc:mysql://localhost:3306/ms_mybiz_salesorder`, `root` / `admin` |
| Eureka | **missing**. Add `eureka.client.serviceUrl.defaultZone=http://localhost:8555/eureka` |

## Code layout

```
com.clinch.crm.salesorder
├── controller/   SalesOrderController (stubs), QuotesController, InvoiceController (empty)
├── service/  repository/
├── entity/       QuotesEntity, SalesOrderEntity, InvoiceEntity
├── been/         QuotesBO (sic: "been" = bean)
└── gatewayauth/  GatewayAuthFilter
```

`bin/` contains stale IDE build output. Ignore it.

## Tests

`SalesorderServiceApplicationTests.contextLoads` only.

## Fixing reachability (checklist)

1. Add the Eureka `defaultZone` (and `eureka.instance.instance-id`) to `application.properties`.
2. Re-map `QuotesController` → `/salesorder/quotes`, `InvoiceController` → `/salesorder/invoice`.
3. Implement `SalesOrderController` and publish stock-reduction messages.

See [docs/KNOWN_ISSUES.md](../docs/KNOWN_ISSUES.md).
