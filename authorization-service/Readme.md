# authorization-service

The platform's **role-based access control (RBAC) store**. It manages **roles**, the **privileges** each role grants, and the **access** entries each privilege covers. authentication-service calls it at sign-in to turn a user's roles into privileges, which are embedded in the JWT. employee-service calls it to check that a role exists.

> Privileges reach every service in the `X-User-Privileges` header, but **no endpoint enforces them yet**. See [docs/SECURITY.md](../docs/SECURITY.md#7-rbac-authorization-service).

## At a glance

| | |
|---|---|
| Port | **8778** |
| Eureka name | `authorization-service` |
| Gateway prefix | `/authorization` |
| Database | MySQL `ms_mybiz_authorization` |
| Stack | Spring Boot 2.7.15 · Spring Cloud 2021.0.8 · Spring Data JPA · Log4j2 (+ Splunk appender) |
| Base package | `com.microservices.authorizationservice` |
| Needs | MySQL schema, env var `GATEWAY_SECRET` |

## Run

```bash
mvn spring-boot:run
```

Start it **before** authentication-service, because sign-in fails if it can't be reached.

## Data model

```
RoleVo (role) ──many-to-many──► PrivilegeVo (privilege) ──many-to-many──► AccessVo (access)
```

| Entity → table | Fields |
|---|---|
| `RoleVo` → `role` | `roleId`, `roleName`, `isActive`, `isDeleted`, `privileges` |
| `PrivilegeVo` → `privilege` | `privilegeId`, `privilegeName`, `isDelete`, `accesses` |
| `AccessVo` → `access` | `accessId`, `accessName`, `isDeleted` |

Deletes are **soft deletes** (flags). `PATCH /role/switch-activity/{id}` toggles `isActive`. Inactive roles grant no privileges at sign-in.

## API

All paths are reached through the gateway at `http://localhost:8556` and need a JWT. Responses use the `ApiResponse` envelope. Validation and not-found errors are mapped by `common/GlobalExceptionHandler` (`DuplicateResourceException`, `ResourceNotFoundException`).

### Roles: `/authorization/role`

| Method | Path | Body / params | Notes |
|---|---|---|---|
| `GET` | `/check-duplicate` | `?roleName=` | `true` if the name is taken |
| `POST` | `/create` | `RoleBo {roleName, isActive, privileges:[PrivilegeBo]}` | |
| `GET` | `/view/{roleId}` | — | |
| `GET` | `/list/{pageIndex}/{pageSize}` | `?sortOrder=&searchText=` | paginated |
| `PUT` | `/update` | `RoleBo` (with `roleId`) | |
| `PATCH` | `/switch-activity/{roleId}` | — | toggles active (⚠️ the gateway's CORS config doesn't allow PATCH from browsers) |
| `DELETE` | `/delete/{roleId}` | — | soft delete |
| `POST` | `/bulk` | `[RoleBo]` | bulk create |
| `GET` | `/exists` | `?roleName=` | used by employee-service |

### Privileges: `/authorization/privilege`

| Method | Path | Body / params | Notes |
|---|---|---|---|
| `GET` | `/check-duplicate` | `?privilegeName=` | |
| `POST` | `/create` | `PrivilegeBo {privilegeName, accesses:[AccessBo]}` | |
| `GET` | `/list/{pageIndex}/{pageSize}` | `?sortOrder=&searchText=` | |
| `GET` | `/view/{privilegeId}` | — | |
| `PUT` | `/update` | `PrivilegeBo` | |
| `DELETE` | `/delete/{privilegeId}` | — | soft delete |
| `GET` | `/list-all` | — | |
| `POST` | `/bulk` | `[PrivilegeBo]` | |
| `POST` | `/privileges` | `["ROLE_A","ROLE_B"]` | **used at sign-in**: returns the set of privilege names granted by the *active* roles given |

### Access entries: `/authorization/access`

| Method | Path | Body / params | Notes |
|---|---|---|---|
| `POST` | `/create` | `AccessBo {accessName}` | |
| `GET` | `/get-access/{accessId}` | — | id must be ≥ 1 |
| `GET` | `/list` | `?page=0&size=10&searchText=&sortBy=accessId&direction=asc` | the cases tested are listed in [list test.md](list%20test.md) (size ≤ 0 → 400, size > 50 → capped) |
| `PUT` | `/update` | `AccessBo` | |
| `DELETE` | `/delete/{accessId}` | — | |
| `GET` | `/list-all` | — | |

`AuthorizationController` (`/authorization`) has no endpoints.

## Calls and callers

| Direction | Service | Endpoint |
|---|---|---|
| ← called by | authentication-service | `POST /authorization/privilege/privileges` (sign-in) |
| ← called by | employee-service | `GET /authorization/role/exists` |
| ← called by | clients via gateway | `/authorization/**` |

It makes no outgoing calls. `SyncCall/AuthorizationClient` is dead code: it points at itself and at a non-existent path, and Feign isn't enabled. Incoming requests need `X-Gateway-Secret` (`gatewayauth/GatewayAuthFilter`).

## Configuration

| Property | Value |
|---|---|
| `server.port` | 8778 |
| `gateway.secret` | `${GATEWAY_SECRET}` |
| `spring.datasource.*` | `jdbc:mysql://localhost:3306/ms_mybiz_authorization`, `root` / `admin` |
| `management.endpoints.web.exposure.include` | `*` (all actuator endpoints. They're behind the gateway-secret filter, but narrow this outside dev) |
| logging | `log4j2-spring.xml` (Splunk library on the classpath) |

## Role catalogue

The roles the platform is designed around. They're created as data through `/authorization/role`, not hard-coded.

| Role | Scope | Responsibilities |
|---|---|---|
| `SUPER_ADMIN` | whole platform | system-wide configuration, tenants (companies), role definitions, governance |
| `ADMIN` | one organisation | all operations in the company; creates employees, assigns roles |
| `SALES_MANAGER` | team | manages all leads, opportunities and sales data for the team; approvals, reporting |
| `SALES_EXECUTIVE` | own records | day-to-day sales: leads, opportunities, customers it owns |
| `SUPPORT_MANAGER` | support org | assigns and monitors support tickets/tasks |
| `SUPPORT_AGENT` | assigned records | works on assigned cases |
| `INVENTORY_MANAGER` | inventory | product catalogue, stock, availability |
| `PROJECT_MANAGER` | projects | project execution, task assignment, delivery |
| `ACCOUNT_MANAGER` | accounts | sales orders, transactions, account records |
| `EMPLOYEE` | self | own profile and assigned tasks |
| `SYSTEM` | internal | service-to-service identity, never assigned to people |

Data access is meant to be layered as **own data → team data → organisation-wide data**. That layering isn't implemented yet.

## Code layout

```
com.microservices.authorizationservice
├── controller/       RoleController, PrivilegeController, AccessController, AuthorizationController (empty)
├── service/          Role/Privilege/AccessService (+Impl)
├── repository/       RoleRepository (findActiveByRoleNames, …), PrivilegeRepository, AccessRepository
├── entity/           RoleVo, PrivilegeVo, AccessVo
├── model/            RoleBo, PrivilegeBo, AccessBo
├── common/           ApiResponse, PaginatedResponse, CursorResponse, AccessSpecification, GlobalExceptionHandler
├── customexception/  DuplicateResourceException, ResourceNotFoundException
├── gatewayauth/      GatewayAuthFilter
└── SyncCall/         AuthorizationClient (unused)
```

## Tests

Only `AuthorizationServiceApplicationTests.contextLoads`. The manual test matrix for `/access/list` is in [list test.md](list%20test.md).

## Known issues

* 🔴 Privileges aren't enforced anywhere.
* 🟡 Dead Feign client `SyncCall/AuthorizationClient`.
* 🟡 Actuator exposes everything (`*`).

See [docs/KNOWN_ISSUES.md](../docs/KNOWN_ISSUES.md).
