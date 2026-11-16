# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Documentation

The project-level docs are in `docs/`: `ARCHITECTURE.md`, `SECURITY.md`, `DEVELOPMENT.md` and `KNOWN_ISSUES.md`. Every module has its own `README.md` (authorization-service's is `Readme.md`), with endpoints, config and data model. When you change a module's endpoints, config or behaviour, update its README, and add or remove entries in `docs/KNOWN_ISSUES.md`.

## Overview

MyCrm is a Spring Boot microservices CRM backend (Java 17, Maven). Each top-level directory is an independent Spring Boot app with its own `pom.xml`, database and port. All of them register with Eureka, and clients reach them through the Spring Cloud Gateway.

## Build & run

There is no Maven wrapper at the root. Most services have their own `mvnw`; `account-service`, `inventory-service`, `salesorder-service` and `ai-service` don't, so use a system `mvn` for those.

```bash
# Build every module listed in the root pom
mvn clean install

# Build / run a single service
mvn -pl employee-service -am clean package
cd employee-service && mvn spring-boot:run

# Run tests for one service / a single test class or method
mvn -pl employee-service test
mvn -pl employee-service test -Dtest=EmployeeServiceImplTest
mvn -pl employee-service test -Dtest=EmployeeServiceImplTest#createsEmployee
```

The only real unit tests are `AuthenticationFilterTest` (gateway-service), `GatewayAuthFilterTest` and `EmployeeBoJsonTest` (employee-service), `UserJsonTest` (authentication-service), `CompanyPasswordJsonTest` (company-service) and `RoleServiceImplTest` (authorization-service, whose `mvnw` is broken, so use a system `mvn`). They need no infrastructure. The other test classes boot the full app. The gateway and all routed services need the `JWT_SECRET` and `GATEWAY_SECRET` env vars set to start. There is no linter or formatter configured.

**Startup order:** `eureka-service` (8555) first, then `gateway-service` (8556), then any business services. Most services need a local MySQL at `localhost:3306` (`root`/`admin`), using one database per service (`ms_mybiz_<service>`). Hibernate runs with `ddl-auto=update`, which creates **tables** but not the **database**, so create each `ms_mybiz_*` schema first (the list is in `docs/DEVELOPMENT.md`). `TaskManagement` uses PostgreSQL (`taskdb`) instead.

## Build gotchas

- **Java 17 is set in one place.** The root `pom.xml` (`mycrm-parent`) inherits `spring-boot-starter-parent:2.7.15`, which turns `java.version=17` into `release=17` and supplies the plugin versions (JUnit 5-capable Surefire 2.22.2). **Don't add `<source>`/`<target>` to a module's `maven-compiler-plugin`**, because that overrides the parent. IntelliJ's "Set language level" quick-fix does exactly that, so decline it and reload the Maven project instead.
- **Mixed Spring Boot generations.** `mycrm-parent` pins **Spring Boot 2.7.15**. Most services inherit it, so they use `javax.*` (for example `javax.validation`), not `jakarta.*`, and Spring Cloud `2021.0.x`. The README says "Spring Boot 3.x", but that's wrong for these modules.
- Several modules don't use `mycrm-parent` at all: `ai-service` uses Boot 3.3.0, and `project-service` and `urlshortner` use Boot 4.0.3, so all three are on `jakarta.*`. `ai-service` and `project-service` are **not** listed in the root `<modules>`, so build them from their own directories.
- Spring Cloud versions differ between services (2021.0.1, 2021.0.2 and 2021.0.8). Check the service's own pom before adding cloud dependencies.

## Architecture

### Request flow
`Client → gateway-service (8556) → lb://<eureka-name> → service`

- Routes are defined in `gateway-service/src/main/resources/application.properties`, one `Path=/<prefix>/**` route per service. `application.yml` in the same folder is empty. Every route, including `/auth/**`, applies the `AuthenticationFilter` gateway filter. Public endpoints are exempted through `RouteValidator`, so a new public endpoint must be added there.
- `AuthenticationFilter` + `RouteValidator` (in `gateway-service/.../filter`) validate the Bearer JWT. `RouteValidator.openExactPaths` (exact match) and `openPathPrefixes` (prefixes ending in `/`) list the paths that skip validation. A missing or invalid token gets 401 at the gateway and is not forwarded.
- On every request, the filter removes any client-sent `X-User-*` and `id` headers. For a valid token it then sets `X-User-Id`, `X-User-Email`, `X-User-Name`, `X-User-Roles` and `X-User-Privileges` (comma-separated) from the JWT claims, and puts the user id in the legacy `id` header that older controllers read for `createdBy`. Downstream services should read the caller's identity only from these headers.
- **Direct calls are blocked.** The gateway also adds `X-Gateway-Secret` (from the `GATEWAY_SECRET` env var). Every routed service has `<base>.gatewayauth.GatewayAuthFilter`, which returns 403 when the header is missing or doesn't match. Services that call others through Feign (`authentication-service`, `employee-service`) send it with `GatewaySecretFeignInterceptor`. A new service needs a copy of the filter plus `gateway.secret=${GATEWAY_SECRET}` in its properties. A service that starts calling others through Feign also needs the interceptor. Not covered (not routed through the gateway): `eureka-service`, `TaskManagement`, `urlshortner`, `ai-service`.
- Discovery locator is **off** on purpose: its auto-generated `/<service-id>/**` routes skip `AuthenticationFilter`. Add new services as explicit routes. Every route gets a global `Retry=3`.

### Controller paths must include the gateway prefix
The gateway does not strip prefixes. Controllers map the full path themselves, for example `@RequestMapping("/employee/territory")` and `/authorization/privilege/...`. A new endpoint has to start with its service's gateway prefix.

### Eureka names vs. directory names
The gateway uses `spring.application.name` in routes and `@FeignClient(name=...)`, and that doesn't always match the directory name:
- `opportunity-service` → `user-opportunity`
- `gateway-service` → `api-gateway-service`
- `product-service` currently declares `spring.application.name=inventory-service` (it collides with the real inventory service), and its port 8566 is the same as `TaskManagement`'s
- The gateway also routes to `campaign-service`, `contacts-service` and `task-service`, but no module registers under those names.

### Auth / authorization
- `authentication-service` (8557) handles signup/signin and issues JWTs with jjwt (HS256). Both it and the gateway read the signing secret from the **`JWT_SECRET` environment variable** (`jwtSecret=${JWT_SECRET}`, no default), so set it to the same Base64 value (at least 32 bytes) for both, or they won't start. `jwtExpirationMs` stays in authentication-service's properties.
- `authorization-service` (8778) manages Roles, Privileges and Access (RBAC). At login, `authentication-service` calls it through Feign (`POST /authorization/privilege/privileges`) to resolve the user's privileges for their roles.
- `employee-service` (8562) calls `authentication-service` (`POST /auth/users`) to create login users for employees, and calls `authorization-service` (`GET /authorization/role/exists`) to validate roles.

### Inter-service calls
Synchronous calls use OpenFeign interfaces in a `SyncCall` package inside each service, addressed by Eureka name. The Feign client's response type mirrors the callee's `ApiResponse<T>` envelope, and each service has its own copy of that class.

### Per-service code conventions (newer services: employee, authorization, authentication)
- Packages: `controller`, `service` (interface + `*ServiceImpl`), `repository` (Spring Data JPA), `entity` (JPA entities, suffix **`Vo`**), `model` (DTOs, suffix **`Bo`**), `common` (`ApiResponse`, exceptions, `GlobalExceptionHandler`), `SyncCall` (Feign clients).
- Controllers return `ResponseEntity<ApiResponse<T>>` built with `ApiResponse.success(...)` / `ApiResponse.error(...)`. They use Lombok `@RequiredArgsConstructor` constructor injection and `@Valid` request bodies.
- The target base package is `com.clinch.crm.<service>` (the product is called Clinch). authentication-service is migrated (`com.clinch.crm.authentication`); the other modules still use `com.microservices.<service>` until they are renamed. Keep the database schema `ms_mybiz_authendication` as is.

### Other modules
- `ai-service`: calls the Claude API (through a `ClaudeClient` WebClient wrapper) to summarize lead activity. The key comes from `ANTHROPIC_API_KEY`. See `ai-service/README.md`.
- `bin/` directories inside `account-service` and `salesorder-service` are stale IDE build copies, so ignore them.

## Git workflow
Git Flow: `main` (production), `develop` (integration), `feature/*`, `hotfix/*`.