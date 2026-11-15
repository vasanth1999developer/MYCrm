# opportunity-service

Manages the **sales opportunity pipeline**: potential deals with an amount, sales stage, probability, expected closing date and the linked account, lead and product.

> It registers in Eureka as **`user-opportunity`**, not `opportunity-service`. Gateway routes and any Feign client must use that name.

## At a glance

| | |
|---|---|
| Port | **8564** |
| Eureka name | `user-opportunity` |
| Gateway prefix | `/opportunity` |
| Database | MySQL `ms_mybiz_opportunity` |
| Stack | Spring Boot 2.7.15 · Spring Cloud 2021.0.8 · Spring Data JPA · Log4j2 · OpenCSV / Commons CSV · springdoc |
| Base package | `com.microservice.opportunity` (main class `UserOpportunityApplication`) |
| Needs | MySQL schema, env var `GATEWAY_SECRET` |

## Run

```bash
mvn spring-boot:run
```

Swagger UI: `http://localhost:8564/swagger-url.html` (needs `X-Gateway-Secret`).

## API

Through the gateway at `http://localhost:8556`. Every endpoint needs a JWT.

| Method | Path | Body / params | Notes |
|---|---|---|---|
| `POST` | `/opportunity/create-opportunity` | `OpportunityBo` | `createdBy` from the `id` header |
| `GET` | `/opportunity/view-opportunity/{id}` | — | |
| `PUT` | `/opportunity/update-opportunity` | `OpportunityVo` (the **entity**, not the BO) | `modifiedBy` from the `id` header |
| `DELETE` | `/opportunity/delete-opportunity/{id}` | — | soft delete |
| `GET` | `/opportunity/checkEmailId/{emailAddress}` | — | duplicate-email check |
| `GET` | `/opportunity/pageRecord` | Spring `Pageable` (`?page=&size=&sort=`) | |
| `GET` | `/opportunity/list/{pageIndex}/{pageSize}` | `?sortOrder=&searchName=&searchEmailId=&columnName=` | |
| `GET` | `/opportunity/export` | — | CSV download |
| `POST` | `/opportunity/import` | multipart field `file` (CSV) | |

## Data model

| Entity → table | Fields |
|---|---|
| `OpportunityVo` → `opportunity_table` | `opportunityId`, `salutation`, `firstName`, `lastName`, `emailAddress`, `mobileNumber`, `companyName`, `amount`, `salesStage`, `probability`, `expectedClosingDate`, `nextStep`, `opportunityType`, `leadSource`, `leadCampId`, `accountId`, `accountName`, `productId`, `productName`, `assignEmpId`, `assignto`, `street`, `city`, `state`, `country`, `postalCode`, `description`, `isActive`, `isDelete`, `createdBy`, `modifiedBy`, `createdTime`, `modifiedTime` |

Accounts, products and employees are referenced by id **and** a copied name. There's no foreign key, because they live in other services' databases.

## Calls and callers

No outgoing calls, even though the pom has a Feign starter. It's called by clients through the gateway, and requests need `X-Gateway-Secret` (`gatewayauth/GatewayAuthFilter`).

## Configuration

| Property | Value |
|---|---|
| `server.port` | 8564 (written as `server.port:8564`) |
| `spring.application.name` | `user-opportunity` |
| `gateway.secret` | `${GATEWAY_SECRET}` |
| `spring.datasource.*` | `jdbc:mysql://localhost:3306/ms_mybiz_opportunity`, `root` / `admin` |

## Code layout

```
com.microservice.opportunity
├── controller/      OpportunityController
├── service/  dao/  repository/
├── models/entity/   OpportunityVo
├── bo/              OpportunityBo
├── export/          CSV helpers
└── gatewayauth/     GatewayAuthFilter
```

## Tests

Both test classes are in `com.techymeet.opportunity…`, a package copied from another project, so they fail with "Unable to find a @SpringBootConfiguration". Move them under `com.microservice.opportunity`.

## Known issues

* The obsolete `spring-cloud-starter-feign:1.4.7.RELEASE` dependency is unused.
* `update-opportunity` binds the JPA entity directly from the request body.

See [docs/KNOWN_ISSUES.md](../docs/KNOWN_ISSUES.md).
