# employee-service

Manages **employees**, **departments** and **territories**. Creating an employee also creates their **login user** in authentication-service, after validating the role in authorization-service. That makes this service the main example of cross-service orchestration in the platform.

## At a glance

| | |
|---|---|
| Port | **8562** |
| Eureka name | `employee-service` |
| Gateway prefix | `/employee` |
| Database | MySQL `ms_mybiz_employee` |
| Stack | Spring Boot 2.7.15 · Spring Cloud 2021.0.8 · OpenFeign · LoadBalancer · Lombok |
| Base package | `com.microservices.employee` |
| Needs | MySQL schema, authentication-service + authorization-service (for employee creation), env var `GATEWAY_SECRET` |

## Run

```bash
mvn spring-boot:run
```

## API

Base URL through the gateway: `http://localhost:8556`. All endpoints need a JWT and return `ApiResponse<T>`.

| Method | Path | Body | Notes |
|---|---|---|---|
| `POST` | `/employee/create-employee` | `EmployeeBo` (below) | creates the login user and the employee (see flow) |
| `GET` | `/employee/list-employee` | — | non-deleted employees |
| `POST` | `/employee/department/create` | `DepartmentBo {departmentName}` | validated (`@Valid`) |
| `GET` | `/employee/department/list` | — | |
| `POST` | `/employee/territory/create` | `TerritoryBo {territoryName}` | validated (`@Valid`) |
| `GET` | `/employee/territory/list` | — | |

`EmployeeBo`:

```json
{
  "username": "jdoe",
  "email": "jane@acme.com",
  "password": "Secret123",
  "firstName": "Jane", "lastName": "Doe", "phone": "9876543210",
  "role": "SALES_EXECUTIVE",
  "managerId": 12,
  "department": "Sales", "territory": "South", "designation": "Executive",
  "dateOfJoining": "2026-11-01"
}
```

`username` and `password` are **write-only** (`@JsonProperty(access = WRITE_ONLY)`). They're accepted in the request but never included in responses. `managerId` may be null for top-level employees. `department` and `territory` are stored as **names (strings)**, not foreign keys.

### Employee creation flow

`service/EmployeeServiceImpl.createEmployee`:

1. `AuthorizationClient.roleExists(role)` → `GET /authorization/role/exists?roleName=`. If false: `IllegalArgumentException("Invalid role: …")`. The controller catches every exception and answers **`201` with `success:false`, "Employee creation failed"**, so the real cause (invalid role, email taken, a service down) isn't reported.
2. `AuthClient.createUser({username, email, password, roles:[role]})` → `POST /auth/users`. Returns the new **user id**.
3. Saves `EmployeeVo` with `employeeId = user id`, so the employee and the login share one id.

⚠️ If step 3 fails, the exception is swallowed and the login user from step 2 stays behind. There's no compensation (see Known issues).

## Data model

| Entity → table | Fields |
|---|---|
| `EmployeeVo` → `employee` | `employeeId` (= auth user id), `firstName`, `lastName`, `email`, `phone`, `role`, `managerId`, `department`, `territory`, `designation`, `dateOfJoining`, `isActive`, `isDeleted`, `createdAt`, `updatedAt` |
| `DepartmentVo` → `department` | `departmentId`, `departmentName`, `isActive`, `isDeleted`, `createdAt` |
| `TerritoryVo` → `territory` | `territoryId`, `territoryName`, `isActive`, `isDeleted`, `createdAt` |

## Calls and callers

| Direction | Service | Endpoint | Feign interface |
|---|---|---|---|
| calls → | authorization-service | `GET /authorization/role/exists` | `SyncCall/AuthorizationClient` |
| calls → | authentication-service | `POST /auth/users` | `SyncCall/AuthClient` |
| ← called by | clients via gateway | `/employee/**` | |

`gatewayauth/GatewaySecretFeignInterceptor` adds `X-Gateway-Secret` to both outgoing calls. `gatewayauth/GatewayAuthFilter` rejects incoming calls without it (403).

## Configuration

| Property | Value |
|---|---|
| `server.port` | 8562 |
| `gateway.secret` | `${GATEWAY_SECRET}` |
| `spring.datasource.*` | `jdbc:mysql://localhost:3306/ms_mybiz_employee`, `root` / `admin` |
| `spring.jpa.hibernate.ddl-auto` | `update` |
| `management.endpoints.web.exposure.include` | `*` |

## Code layout

This module follows the platform's reference conventions (see [docs/DEVELOPMENT.md](../docs/DEVELOPMENT.md#6-code-conventions)):

```
com.microservices.employee
├── controller/   EmployeeController, DepartmentController, TerritoryController
├── service/      *Service + *ServiceImpl
├── repository/   Spring Data JPA repositories
├── entity/       EmployeeVo, DepartmentVo, TerritoryVo
├── model/        EmployeeBo, DepartmentBo, TerritoryBo, CreateUserRequest
├── common/       ApiResponse, DuplicateResourceException (no GlobalExceptionHandler yet)
├── SyncCall/     AuthClient, AuthorizationClient (Feign)
└── gatewayauth/  GatewayAuthFilter, GatewaySecretFeignInterceptor
```

## Tests

```bash
mvn test -Dtest=GatewayAuthFilterTest   # 5 unit tests: filter accepts/rejects, fail-fast, Feign interceptor
mvn test -Dtest=EmployeeBoJsonTest      # password is accepted but never serialized
```

`com.microservices.accountservice.AccountServiceApplicationTests` is in a package copied from another module and fails ("Unable to find a @SpringBootConfiguration"). `controller/EmployeeTestController` has no tests.

## Known issues

* 🟠 `create-employee` lacks `@Valid`, so the password-strength and other rules on `EmployeeBo` don't run.
* 🟠 There's no compensation when the employee save fails after the login user was created. The error is also swallowed, so the client sees success.
* 🟠 Every creation failure is reported as HTTP 201 + `success:false` with a generic message.
* 🟡 Only create and list exist. There's no update, get-by-id or delete.

See [docs/KNOWN_ISSUES.md](../docs/KNOWN_ISSUES.md).
