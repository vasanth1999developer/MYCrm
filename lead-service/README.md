# lead-service

Manages **sales leads**: prospects that haven't become customers yet. It covers their source, owner, company details and rating, with CSV import/export.

## At a glance

| | |
|---|---|
| Port | **8563** |
| Eureka name | `lead-service` |
| Gateway prefix | `/lead` |
| Database | MySQL `ms_mybiz_lead` |
| Stack | Spring Boot 2.7.15 · Spring Cloud 2021.0.8 · Spring Data JPA · Log4j2 · OpenCSV / Commons CSV · springdoc |
| Base package | `com.microservice.leadservice` |
| Needs | MySQL schema, env var `GATEWAY_SECRET` |

## Run

```bash
mvn spring-boot:run
```

Swagger UI: `http://localhost:8563/swagger-url.html` (needs `X-Gateway-Secret`).

## API

Through the gateway at `http://localhost:8556`. Every endpoint needs a JWT. Responses are plain JSON (no envelope).

| Method | Path | Body / params | Notes |
|---|---|---|---|
| `POST` | `/lead/create-lead` | `LeadBO` | `createdBy` from the `id` header |
| `PUT` | `/lead/update-lead/{id}` | `LeadBO` | `modifiedBy` from the `id` header |
| `GET` | `/lead/get-leadById/{id}` | — | |
| `DELETE` | `/lead/delete-lead/{id}` | — | soft delete |
| `GET` | `/lead/leadList/{pageIndex}/{pageSize}` | `?column=&order=&text=` | paginated, sortable, searchable |
| `GET` | `/lead/checkedEmail/{email}` | — | duplicate-email check |
| `GET` | `/lead/export` | — | downloads all leads as CSV |
| `POST` | `/lead/import` | multipart field `file` (CSV) | bulk import |

`GET /lead/get-all-leads` is commented out in the controller.

## Data model

| Entity → table | Fields |
|---|---|
| `LeadVO` → `leadTable` | `leadId`, `salutation`, `firstName`, `lastName`, `email`, `mobileNo`, `designation`, `leadType`, `leadOwner`, `leadSourse` (sic), `leadSourceId`, `status`, `rating`, `companyName`, `industry`, `website`, `annualRevenue`, `noOfEmployee`, `street`, `city`, `state`, `postalCode`, `country`, `employeeId`, `employeeName`, `productId`, `productName`, `descriptions`, `isDelete`, `created`, `modified`, `createdBy`, `modifiedBy` |

Entities are scanned from `models.entity` (`@EntityScan` on `LeadServiceApplication`).

## Calls and callers

No outgoing calls. It's called by clients through the gateway, and requests need `X-Gateway-Secret` (`gatewayauth/GatewayAuthFilter`). The planned ai-service lead summary would read lead activity from here, but that isn't wired up.

## Configuration

| Property | Value |
|---|---|
| `server.port` | 8563 |
| `gateway.secret` | `${GATEWAY_SECRET}` |
| `spring.datasource.*` | `jdbc:mysql://localhost:3306/ms_mybiz_lead`, `root` / `admin` |
| `spring-cloud.version` | 2021.0.8 (a commented-out 2021.0.1 line remains in the pom) |
| logging | `log4j2-spring.xml` |

## Code layout

```
com.microservice.leadservice
├── controllers/     LeadController
├── services/        lead service
├── repository/
├── models/entity/   LeadVO
├── bean/            LeadBO
├── export/          CSV export/import helpers
└── gatewayauth/     GatewayAuthFilter
```

## Tests

`controllers/LeadControllerTest` (5 tests) boots the full application, so it needs `GATEWAY_SECRET` and the MySQL schema. `com.microservice.lead.LeadServiceApplicationTests` is in a different package (`lead` vs `leadservice`).

## Known issues

* The pom has an unused `spring-cloud-starter-netflix-eureka-server` dependency.
* Controllers catch `Exception` and return 500 after `printStackTrace()`.
* Typos are part of the schema: `leadSourse`, table `leadTable`.

See [docs/KNOWN_ISSUES.md](../docs/KNOWN_ISSUES.md).
