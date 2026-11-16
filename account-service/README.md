# account-service

Manages **accounts**: the companies or organisations you do business with. It covers the owner, parent account, industry, revenue and address, with search and CSV import/export.

## At a glance

| | |
|---|---|
| Port | **8558** |
| Eureka name | `account-service` |
| Gateway prefix | `/account` |
| Database | MySQL `ms_mybiz_account` |
| Stack | Spring Boot 2.7.15 · Spring Cloud 2021.0.8 · Spring Data JPA · OpenCSV / Commons CSV · springdoc |
| Base package | `com.clinch.crm.account` |
| Needs | MySQL schema, env var `GATEWAY_SECRET` |

## Run

There's no Maven wrapper in this module. Use a system `mvn`:

```bash
mvn spring-boot:run
```

Swagger UI: `http://localhost:8558/swagger-url.html`. API docs: `/account-url` (needs `X-Gateway-Secret`).

## API

Through the gateway at `http://localhost:8556`. Every endpoint needs a JWT. Responses are plain JSON.

| Method | Path | Body / params | Notes |
|---|---|---|---|
| `POST` | `/account/create-account` | `AccountBO` | `createdBy` from the `id` header |
| `GET` | `/account/getAllAccount` | — | |
| `GET` | `/account/get-account/{id}` | — | |
| `PUT` | `/account/update-account/{id}` | `AccountBO` | `modifiedBy` from the `id` header |
| `DELETE` | `/account/delete-account/{id}` | — | soft delete (`isdelete`) |
| `GET` | `/account/searchbyowner` | `?owner=` | |
| `GET` | `/account/searchbyaccountName` | `?accountName=` | |
| `GET` | `/account/list/{pageIndex}/{pageSize}` | `?sortOrder=&firstName=&accountName=&columnName=` | paginated |
| `GET` | `/account/export` | — | CSV download |
| `POST` | `/account/import` | multipart field `file` (CSV) | bulk import |

## Data model

| Entity → table | Fields |
|---|---|
| `AccountVO` → `account` | `accountId`, `accountOwner`, `accountName`, `parentAccount`, `salutation`, `type`, `industry`, `annualRevenue`, `noOfEmploye`, `email`, `contactNumber`, `employeeId`, `assigntedTo` (sic), `street`, `city`, `state`, `country`, `pincode`, `description`, `isdelete` |

## Calls and callers

No outgoing calls. It's called by clients through the gateway, and requests need `X-Gateway-Secret` (`gatewayauth/GatewayAuthFilter`).

## Configuration

| Property | Value |
|---|---|
| `server.port` | 8558 |
| `gateway.secret` | `${GATEWAY_SECRET}` |
| `spring.datasource.*` | `jdbc:mysql://localhost:3306/ms_mybiz_account`, `root` / `admin` |
| `springdoc.swagger-ui.path` / `api.docs.path` | `/swagger-url.html` / `/account-url` |
| logging | Logback (the default). `log4j2-spring.xml` is present but **ignored**, because the Log4j2 starter isn't a dependency. |

## Code layout

```
com.clinch.crm.account
├── controller/   AccountController
├── service/      AccountService(+Impl)
├── dao/          AccountDAO(+Impl)
├── repositry/    AccountRepository   (sic)
├── entity/       AccountVO, BaseVO
├── model/        AccountBO, BaseBO
├── export/       CsvExport (static CSV helper)
└── gatewayauth/  GatewayAuthFilter
```

`bin/` contains stale IDE build output. Ignore it (it's tracked in git and should be removed).

## Tests

`controller/AccountControllerTest` (5 tests) boots the full application, so it needs `GATEWAY_SECRET` and the MySQL schema.

## Known issues

* The controller injects the repository directly next to the service, and uses `try/catch(Exception)` → 500.

See [docs/KNOWN_ISSUES.md](../docs/KNOWN_ISSUES.md).
