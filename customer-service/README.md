# customer-service

Manages **customers**: people or organisations that bought a product, including contact details, the assigned employee and warranty information.

## At a glance

| | |
|---|---|
| Port | **8561** |
| Eureka name | `customer-service` |
| Gateway prefix | `/customer` (all endpoints are under `/customer/v1/…`) |
| Database | MySQL `ms_mybiz_customer` |
| Stack | Spring Boot 2.7.15 · Spring Cloud 2021.0.1 · Spring Data JPA (+ Data JDBC) · springdoc |
| Base package | `com.clinch.crm.customer` (main class `CustomerApplication`) |
| Needs | MySQL schema, env var `GATEWAY_SECRET` |

## Run

```bash
mvn spring-boot:run
```

Swagger UI (direct port, needs the `X-Gateway-Secret` header): `http://localhost:8561/swagger-url.html`.

## API

Through the gateway at `http://localhost:8556`. Every endpoint needs a JWT. Responses are the BO / plain JSON (no `ApiResponse` envelope).

| Method | Path | Body / params | Notes |
|---|---|---|---|
| `POST` | `/customer/v1/create-customer` | `CustomerBo` | sets `createdBy` from the `id` header |
| `GET` | `/customer/v1/view-customer` | — | list all customers |
| `GET` | `/customer/v1/edit-customer/{id}` | — | fetch one for editing |
| `PUT` | `/customer/v1/update-customer` | `CustomerBo` (`@Valid`) | sets `modifiedBy` from the `id` header |
| `DELETE` | `/customer/v1/delete-customer/{id}` | — | soft delete (`isDelete`) |
| `GET` | `/customer/v1/checkEmailId/{emailAddress}` | — | duplicate-email check |
| `GET` | `/customer/v1/checkMobileNo/{mobileNo}` | — | duplicate-mobile check |
| `GET` | `/customer/v1/pageRecord` | `?page=&size=` | paginated list |

**Who is the caller?** `createdBy` / `modifiedBy` come from the `id` request header. The gateway always sets it from the verified JWT and overwrites anything the client sends (see [docs/SECURITY.md](../docs/SECURITY.md)).

## Data model

| Entity → table | Fields |
|---|---|
| `CustomerVo` → `customer` | `customerId`, `assignEmployee`, `firstName`, `lastName`, `gender`, `emailId`, `contactNo`, `mobileNo`, `primaryMobileNo`, `company`, `industry`, `webSite`, `address`, `productName`, `warrantyDate`, `isDelete`, `createdBy`, `modifiedBy`, `createdTime`, `modifiedTime` |

## Calls and callers

No outgoing calls. It's called by clients through the gateway. Incoming requests need `X-Gateway-Secret` (`com.clinch.crm.customer.gatewayauth.GatewayAuthFilter`).

## Configuration

| Property | Value |
|---|---|
| `server.port` | 8561 |
| `gateway.secret` | `${GATEWAY_SECRET}` |
| `spring.datasource.*` | `jdbc:mysql://localhost:3306/ms_mybiz_customer`, `root` / `admin` |
| `springdoc.swagger-ui.path` / `api.docs.path` | `/swagger-url.html` / `/api-docs` |
| `management.endpoints.web.exposure.include` | `*` |

## Code layout

```
com.clinch.crm.customer
├── CustomerApplication.java
├── gatewayauth/  GatewayAuthFilter
├── controller/   CustomerController
├── service/      CustomerService(+Impl)
├── dao/          repository
├── entity/       CustomerVo
└── model/        CustomerBo
```

## Tests

`controller/CustomerTestController` (5 tests) boots the full application, so it needs `GATEWAY_SECRET` and the MySQL schema. `CustomerApplicationTests.contextLoads` has the same needs.

## Known issues

* `create-customer` takes a `BindingResult` but no `@Valid`, so the create payload isn't validated (update is).
* Customers reference employees and products **by name** (`assignEmployee`, `productName`), not by id.

See [docs/KNOWN_ISSUES.md](../docs/KNOWN_ISSUES.md).
