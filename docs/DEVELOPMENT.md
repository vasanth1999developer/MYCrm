# Development guide

## 1. Prerequisites

| Tool | Version | Needed for |
|---|---|---|
| JDK | 17 | everything (it's the only version the build targets) |
| Maven | 3.6+ (3.8.4 is what the wrappers download) | building; there's no wrapper at the repo root |
| MySQL | 8.x on `localhost:3306`, user `root` / `admin` | most services |
| PostgreSQL | `localhost:5432`, user `postgres` / `admin`, database `taskdb` | TaskManagement only |
| RabbitMQ | `localhost:5672`, `guest` / `guest` | product-service only (it fails to connect without it, but the HTTP API still starts) |

### MySQL schemas

Hibernate creates the **tables**, but not the **schema**. Create the schemas once:

```sql
CREATE DATABASE IF NOT EXISTS ms_mybiz_authendication;  -- sic: spelling matches authentication-service config
CREATE DATABASE IF NOT EXISTS ms_mybiz_authorization;
CREATE DATABASE IF NOT EXISTS ms_mybiz_employee;
CREATE DATABASE IF NOT EXISTS ms_mybiz_customer;
CREATE DATABASE IF NOT EXISTS ms_mybiz_lead;
CREATE DATABASE IF NOT EXISTS ms_mybiz_account;
CREATE DATABASE IF NOT EXISTS ms_mybiz_opportunity;
CREATE DATABASE IF NOT EXISTS ms_mybiz_company;
CREATE DATABASE IF NOT EXISTS ms_mybiz_inventory;
CREATE DATABASE IF NOT EXISTS ms_mybiz_salesorder;
CREATE DATABASE IF NOT EXISTS product_service;
```

Without the schema, a service stops at startup with `Unknown database 'ms_mybiz_…'`.

## 2. Environment variables

| Variable | Required by | How to create |
|---|---|---|
| `JWT_SECRET` | gateway-service, authentication-service | 64 random bytes, Base64 |
| `GATEWAY_SECRET` | gateway-service and every routed service | 32 random bytes, Base64 |
| `ANTHROPIC_API_KEY` | ai-service | from the Anthropic console |

PowerShell (persists for your Windows user; restart the terminal and IntelliJ afterwards):

```powershell
$b = New-Object byte[] 64; [Security.Cryptography.RandomNumberGenerator]::Create().GetBytes($b); setx JWT_SECRET ([Convert]::ToBase64String($b))
$b = New-Object byte[] 32; [Security.Cryptography.RandomNumberGenerator]::Create().GetBytes($b); setx GATEWAY_SECRET ([Convert]::ToBase64String($b))
```

bash / macOS / Linux:

```bash
export JWT_SECRET=$(openssl rand -base64 64 | tr -d '\n')
export GATEWAY_SECRET=$(openssl rand -base64 32 | tr -d '\n')
```

Alternatively, set them per run configuration in IntelliJ (*Run → Edit Configurations → Environment variables*). Every service must get the **same** values.

## 3. Build

```bash
# Whole platform (parent + 15 modules in <modules>)
mvn clean install                 # add -DskipTests for a fast build

# One module
mvn -f employee-service/pom.xml clean package

# Modules NOT in the root <modules> list — build from their own folder
cd ai-service      && mvn clean package     # Boot 3.3 — currently fails, see KNOWN_ISSUES
cd project-service && ./mvnw clean package  # Boot 4.0
```

### How versions are controlled

* The root `pom.xml` (`mycrm-parent`) inherits `spring-boot-starter-parent:2.7.15`. That gives every child module the Java level, plugin versions (compiler, Surefire 2.22.2 with JUnit 5) and Spring Boot dependency versions.
* `java.version=17` in the parent becomes `release=17`. **Never add `<source>`/`<target>` to a module's `maven-compiler-plugin`**, because that overrides the parent. IntelliJ's *"Set language level to …"* quick-fix does exactly that, so decline it and use *Maven → Reload All Projects* instead.
* Spring Cloud is imported per module through `spring-cloud.version`. Check the module's own pom before adding cloud dependencies, because the versions differ (2021.0.1 / .2 / .8).
* `ai-service`, `project-service` and `urlshortner` use their own Spring Boot parent (3.3 / 4.0) and are on `jakarta.*`. All the other modules are on `javax.*`.

## 4. Run

Start order matters:

1. `eureka-service` (8555): wait until its dashboard loads at http://localhost:8555.
2. `gateway-service` (8556).
3. `authorization-service` (8778). Sign-in depends on it.
4. `authentication-service` (8557).
5. Any business services.

```bash
cd <module> && mvn spring-boot:run
# or
java -jar <module>/target/<artifact>-0.0.1-SNAPSHOT.jar
```

**Port clash:** `product-service` and `TaskManagement` both use **8566**, so you can't run both at once. Override one: `mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8567`.

Swagger UI is at `http://localhost:<port>/swagger-url.html` on account, company, customer, inventory, lead, opportunity, product and salesorder. TaskManagement uses springdoc's default `/swagger-ui.html`. Since the gateway-secret change, direct port calls return 403, so the browser needs the `X-Gateway-Secret` header (use a header-injecting browser extension or Postman).

## 5. Test

```bash
mvn -f gateway-service/pom.xml test -Dtest=AuthenticationFilterTest   # 8 tests, no infrastructure needed
mvn -f employee-service/pom.xml test -Dtest=GatewayAuthFilterTest     # 5 tests, no infrastructure needed
```

The state of the other test classes:

| Kind | Where | Status |
|---|---|---|
| Pure unit tests | `AuthenticationFilterTest` (gateway), `GatewayAuthFilterTest` + `EmployeeBoJsonTest` (employee), `UserJsonTest` (authentication), `CompanyPasswordJsonTest` (company), `RoleServiceImplTest` (authorization) | ✅ pass, no infrastructure needed |
| Full-context controller tests (start Tomcat + JPA) | account `AccountControllerTest`, customer `CustomerTestController`, lead `LeadControllerTest` | ❌ need `GATEWAY_SECRET` set **and** the MySQL schema. They're integration tests, not unit tests. |
| Mis-packaged tests | opportunity `com.techymeet…OpportunityTest`, product `com.softtwig…ProductControllerTest`, employee `com.microservices.accountservice…` | ❌ fail with "Unable to find a @SpringBootConfiguration" because they sit in packages copied from other projects |
| `*ApplicationTests.contextLoads` | every module | need the module's full infrastructure (DB, secrets, Eureka optional) |

Running `mvn test` from the root therefore needs MySQL with the schemas and the env vars set, or `-DskipTests`.

## 6. Code conventions

These are the conventions of the newer services (employee, authorization, authentication). Follow them in new code.

* **Base package** `com.microservices.<service>`. The authentication module keeps the historical spelling `authendicationservice`.
* **Packages:** `controller`, `service` (interface + `*ServiceImpl`), `repository` (Spring Data JPA), `entity` (JPA entities, suffix **`Vo`**), `model` (DTOs, suffix **`Bo`**), `common` (`ApiResponse`, exceptions, `GlobalExceptionHandler`), `SyncCall` (Feign clients), `gatewayauth` (gateway-secret filter).
* **Controllers** return `ResponseEntity<ApiResponse<T>>` built with `ApiResponse.success(...)` / `ApiResponse.error(...)`, use Lombok `@RequiredArgsConstructor` constructor injection, and take `@Valid @RequestBody`.
* **Errors** go through a `@RestControllerAdvice` `GlobalExceptionHandler`. Don't `catch (Exception e) { e.printStackTrace(); }`.
* **Caller identity** comes from the gateway headers (`X-User-Id`, …). Never take it from the request body or a client-chosen header.

Older services (account, lead, customer, inventory, opportunity, salesorder, company) use `bo`/`bean`/`model` + `VO` naming, field injection and no envelope. Don't copy that style into new code.

## 7. Recipes

### Add an endpoint to an existing service

1. Map the **full** public path, starting with the service's gateway prefix (the gateway doesn't strip it): `@RequestMapping("/employee/skills")`.
2. If it must be public (no login), add it to `RouteValidator.openExactPaths` or `openPathPrefixes` in gateway-service. Otherwise do nothing, because it's protected by default.
3. Read the caller from `@RequestHeader("X-User-Id") Long userId`.

### Add a new service

1. Create the module and inherit `mycrm-parent` (`<relativePath>../pom.xml</relativePath>`). Add it to the root `<modules>`. Don't configure `maven-compiler-plugin`.
2. Add `spring-cloud-starter-netflix-eureka-client` and the `spring-cloud-dependencies` import (use 2021.0.8).
3. In `application.properties`, set `spring.application.name`, a free `server.port`, `eureka.client.serviceUrl.defaultZone=http://localhost:8555/eureka` and `gateway.secret=${GATEWAY_SECRET}`.
4. Copy `gatewayauth/GatewayAuthFilter.java` from another service into `<base>.gatewayauth`. If the service calls others through Feign, also copy `GatewaySecretFeignInterceptor.java`.
5. Add a gateway route in `gateway-service/src/main/resources/application.properties`, with `filters=AuthenticationFilter`:
   ```properties
   spring.cloud.gateway.routes[N].id=<name>-core
   spring.cloud.gateway.routes[N].uri=lb://<spring.application.name>
   spring.cloud.gateway.routes[N].predicates=Path=/<prefix>/**
   spring.cloud.gateway.routes[N].filters=AuthenticationFilter
   ```
6. Add the module to the table in the root README and give it a `README.md` in the same layout as the others.

### Call another service

Add a Feign interface in `SyncCall`, `@FeignClient(name = "<callee spring.application.name>")`, whose return type mirrors the callee's `ApiResponse<T>`. Enable Feign with `@EnableFeignClients` and copy `GatewaySecretFeignInterceptor`.

## 8. CI

Each module has a `jenkinsfile`, but they're **legacy and won't work for this repo**. They check out `github.com/techymeet/mysalesmicroservices2.0` (`main` branch, sparse path `mysales-microservice/<service>`), use hard-coded Windows paths (`F:/Softwares/JenkinsHome/...`, `C:/Program Files/Java/jdk-17.0.2`), build with `-Dmaven.test.skip`, and run the jar without the `JWT_SECRET` / `GATEWAY_SECRET` env vars. Treat them as a template to rewrite.

`eureka-service/Dockerfile` is the only Dockerfile. It's also outdated: it uses a Java 11 JRE image, which can't run the Java 17 jar, and it exposes 8761 while the service listens on 8555.

## 9. Git workflow

Git Flow: `main` (production), `develop` (integration), `feature/*`, `hotfix/*`.
