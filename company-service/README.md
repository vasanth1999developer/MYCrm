# company-service

Manages **company (tenant) profiles**: name, GST number, contact person, contact details, website, industry and address.

## At a glance

| | |
|---|---|
| Port | **8570** |
| Eureka name | `company-service` |
| Gateway prefix | `/company` |
| Database | MySQL `ms_mybiz_company` |
| Stack | Spring Boot 2.7.15 · Spring Cloud 2021.0.1 · Spring Data JPA · springdoc · Jersey Apache client |
| Base package | `com.clinch.crm.company` |
| Needs | MySQL schema, env var `GATEWAY_SECRET` |

## Run

```bash
mvn spring-boot:run
```

Swagger UI: `http://localhost:8570/swagger-url.html` (needs `X-Gateway-Secret`).

## API

Through the gateway at `http://localhost:8556`. Every endpoint needs a JWT.

| Method | Path | Body / params | Notes |
|---|---|---|---|
| `POST` | `/company/create-company` | `CompanyBO` | `createdBy` from the `id` header |
| `GET` | `/company/get-companyList` | — | returns `CompanyVO` entities (`password` excluded) |
| `GET` | `/company/get-companyById/{id}` | — | returns `CompanyBO` (`password` excluded) |
| `PUT` | `/company/update-company` | `CompanyBO` (with `companyId`) | `modifiedBy` from the `id` header |
| `DELETE` | `/company/delete-company/{id}` | — | soft delete |

## Data model

| Entity → table | Fields |
|---|---|
| `CompanyVO` → `campany` (sic) | `companyId`, `companyName`, `contactPerson`, `companyGSTNo`, `companyEmail`, `contactNo`, `mobileNo`, `companyWebsite`, `industryType`, `address`, `city`, `state`, `country`, `postalCode`, `password`, `isActiveStatus`, `isDelete`, `created`, `modified`, `createdBy`, `modifiedBy` |

## Calls and callers

No outgoing calls. It's called by clients through the gateway, and requests need `X-Gateway-Secret` (`gatewayauth/GatewayAuthFilter`).

## Configuration

| Property | Value |
|---|---|
| `server.port` | 8570 |
| `gateway.secret` | `${GATEWAY_SECRET}` |
| `spring.datasource.*` | `jdbc:mysql://localhost:3306/ms_mybiz_company`, `root` / `admin` |
| `management.endpoints.web.exposure.include` | `*` |

## Code layout

```
com.clinch.crm.company
├── controller/   CompanyController
├── service/  repository/
├── entity/       CompanyVO
├── bean/         CompanyBO
└── gatewayauth/  GatewayAuthFilter
```

## Tests

* `CompanyPasswordJsonTest`: the password is accepted in requests, never serialized, and still copied BO → VO for saving. It's a unit test and needs no infrastructure.
* `CompanyServiceApplicationTests.contextLoads` needs MySQL and `GATEWAY_SECRET`.

## Known issues

* 🟠 The `password` field is stored in **plain text**. It's accepted on create/update (`CompanyBO` is `WRITE_ONLY`) and never returned (`CompanyVO` is `@JsonIgnore`). Nothing logs in as a company, so the field may be removable.
* 🟡 The table name is a typo (`campany`). Renaming it needs a migration.
* 🟡 Unused `spring-cloud-starter-netflix-eureka-server` dependency.

See [docs/KNOWN_ISSUES.md](../docs/KNOWN_ISSUES.md).
