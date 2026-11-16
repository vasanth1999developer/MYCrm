# Known issues

These are open problems found while auditing the codebase (November 2026), grouped and ordered by impact. Each one names where it lives. When you fix one, delete it here and update the module's README.

**Severity:** 🔴 broken or exploitable · 🟠 wrong behaviour or a trap for developers · 🟡 cleanup

---

## Security

| | Issue | Where | Fix |
|---|---|---|---|
| 🔴 | **No privilege checks per endpoint.** Any authenticated user can call any endpoint, including admin-only ones like `POST /auth/users`. | all services | Check `X-User-Privileges` per endpoint (planned "step C") |
| 🔴 | **Soft-deleted privileges are still granted at sign-in.** `findActiveByRoleNames` filters roles on `isActive`/`isDeleted` but not their privileges on `isDelete`, so `DELETE /authorization/privilege/delete/{id}` hides a privilege from lists but it keeps appearing in new JWTs. | authorization-service `RoleRepository.findActiveByRoleNames`, `PrivilegeServiceImpl.getPrivilegeNamesByRoleNames` | Filter `p.isDelete = false` in the query (or the stream) |
| 🟠 | `GET /authorization/role/exists` uses `existsByRoleName`, which also matches **inactive and soft-deleted** roles, so employee-service can assign a role that grants nothing (sign-in then fails with no privileges). | authorization-service `RoleController.roleExists` | Check `isActive = true AND isDeleted = false` |
| 🟠 | **Company passwords are stored in plain text.** `CompanyBO.password` is copied unchanged into `CompanyVO` and saved. (They're no longer returned by the API.) It's also unclear what this password is for, since nothing in the platform logs in as a company. | company-service `entity/CompanyVO.java` | Remove the field if it's unused, or BCrypt-hash it before saving |
| 🟠 | `create-employee` has **no `@Valid`**, so the `@Pattern`/`@NotBlank` rules on `EmployeeBo` (password strength etc.) never run. | employee-service `EmployeeController.createEmployee` | Add `@Valid` |
| 🟠 | DB credentials (`root`/`admin`, `postgres`/`admin`) and RabbitMQ `guest`/`guest` are committed. | every `application.properties` | Use `${DB_PASSWORD}`-style placeholders |
| 🟠 | The **old JWT secret is in git history**. | history of `gateway-service/.../JwtUtil.java`, `authentication-service/.../application.properties` | Never reuse it. Each environment generates its own `JWT_SECRET`. |
| 🟡 | Demo endpoints `/api/test/{all,user,mod,admin,buyer}` are still in the code. They're unreachable (no route, gateway secret required). | authentication-service `controller/TestController.java` | Delete |
| 🟡 | CORS allows a placeholder origin `https://yourdomain.com` and no `PATCH` method, although `PATCH /authorization/role/switch-activity/{id}` exists. | gateway-service `config/CorsConfig.java` | Configure real origins, add PATCH |
| 🟡 | `ai-service`, `TaskManagement` and `urlshortner` have no gateway-secret filter and no auth. Today they're also not routed. | — | Add `GatewayAuthFilter` before routing to them |

## Routing and service discovery

| | Issue | Where | Fix |
|---|---|---|---|
| 🔴 | **Two apps register as `inventory-service`:** product-service sets `spring.application.name=inventory-service`. The gateway load-balances `/inventory/**` between inventory-service and product-service, so roughly half the requests go to an app that doesn't have the endpoint (404). | product-service `application.properties` | Give product-service its own name (`product-service`) and a route such as `/product/**`, or merge it into inventory-service |
| 🔴 | **salesorder-service never registers with Eureka.** Its properties have no `eureka.client.serviceUrl.defaultZone`, so it uses the client default `localhost:8761` and `/salesorder/**` returns 503. | salesorder-service `application.properties` | Add `eureka.client.serviceUrl.defaultZone=http://localhost:8555/eureka` |
| 🔴 | **salesorder-service paths aren't under its prefix:** `QuotesController` is mapped to `/quotes` and `InvoiceController` to `/Invoice`, so they can't be reached through the gateway (`/salesorder/**`). `InvoiceController` also has no endpoints. | salesorder-service `controller/` | Map to `/salesorder/quotes`, `/salesorder/invoice` |
| 🟠 | `/task/**` routes to `task-service`, but the module is named `TaskManagement` and has no Eureka client. | gateway routes, TaskManagement pom/properties | Add the Eureka client, then rename it or change the route |
| 🟠 | `/project/**` routes to `project-service`, which has no Eureka client, no controllers and no JPA. It's a skeleton. | project-service | Build it out, or remove the route |
| 🟠 | `/campaign/**` → `campaign-service` and `/contact/**` → `contacts-service` have no module behind them, so they always return 503. | gateway routes | Remove the routes until the modules exist |
| 🟠 | **Port clash 8566:** product-service and TaskManagement. | both `application.properties` | Move one (e.g. TaskManagement → 8571) |
| 🟡 | authorization-service has a Feign client `SyncCall/AuthorizationClient` that points at itself (`/authorization/role/privileges`, which doesn't exist) and is never enabled or used. | authorization-service | Delete |
| 🟡 | company-service and lead-service depend on `spring-cloud-starter-netflix-eureka-server` (the *server*) without using it. | their `pom.xml` | Remove the dependency |

## Build and versions

| | Issue | Where | Fix |
|---|---|---|---|
| 🔴 | **ai-service doesn't compile:** `LeadSummaryService` calls `request.activityHistory()`, but `SummarizeLeadRequest` has `activities`. The module also contains two parallel implementations (`AnthropicClient` + `LeadSummaryService` vs `ClaudeClient` + `LeadSummarizationService`, which is the one the controller uses). | ai-service `service/`, `client/` | Delete the unused pair, or adapt `activities` → prompt text |
| 🟠 | Mixed Spring Cloud release trains: 2021.0.1 (authentication, company, customer, eureka), 2021.0.2 (gateway), 2021.0.8 (rest). All accept Boot 2.7 but ship different library versions. lead-service also has a commented-out duplicate. | each module's `spring-cloud.version` | Align on 2021.0.8, ideally set once in the root pom |
| 🟡 | opportunity-service declares the obsolete `spring-cloud-starter-feign:1.4.7.RELEASE`. It only pulls in openfeign 3.1.8, and the service has no Feign clients. | opportunity-service `pom.xml` | Remove it, or replace with `spring-cloud-starter-openfeign` |
| 🟡 | Three Spring Boot generations: 2.7 (`javax.*`), 3.3 (ai-service), 4.0 (project-service, urlshortner). Code can't be copied between them unchanged. | — | Decide on one generation for new services |
| 🟡 | `eureka-service/Dockerfile` uses a Java 11 JRE, which can't run the Java 17 jar, and exposes 8761 instead of 8555. | eureka-service | Use a 17 JRE image, `EXPOSE 8555` |
| 🟡 | authorization-service's `mvnw`/`mvnw.cmd` don't work: the `.mvn/wrapper` folder is missing. Use a system `mvn`, or another module's wrapper with `-f ../authorization-service/pom.xml`. | authorization-service | Restore `.mvn/wrapper`, or delete the scripts |
| 🟡 | authentication-service declares `spring-cloud-openfeign-core` twice; Maven warns that future versions may reject the pom. | authentication-service `pom.xml` (around line 94) | Remove the duplicate |
| 🟡 | Every `jenkinsfile` targets a different repository (`techymeet/mysalesmicroservices2.0`) with hard-coded `F:/` paths and skipped tests. | all modules | Rewrite for this repo |
| 🟡 | Stale IDE build copies under `account-service/bin` and `salesorder-service/bin` (5 files are tracked in git). | — | `git rm -r` them and add `bin/` to `.gitignore` |

## Tests

| | Issue | Where |
|---|---|---|
| 🟠 | Unit-test coverage is thin. There are 6 real unit-test classes, covering the gateway filter, the gateway-secret filter, password serialization and authorization-service's role update. Almost no business logic is tested. | — |
| 🟠 | The account, customer, lead, opportunity and product controller tests boot the whole app, so they need `GATEWAY_SECRET` and a live MySQL schema. | `*/src/test/.../controller/*` |

## Data and behaviour

| | Issue | Where |
|---|---|---|
| 🟠 | **No compensation in employee creation:** if saving the employee fails after the login user was created in authentication-service, the exception is swallowed (`printStackTrace`). The user is left without an employee, and the client still gets a success response. | employee-service `EmployeeServiceImpl.createEmployee` |
| 🟠 | `POST /employee/create-employee` catches every exception and returns **HTTP 201** with `success:false` and the message "Employee creation failed", so an invalid role, a duplicate email or a service outage all look the same. | employee-service `EmployeeController.createEmployee` |
| 🟠 | Employee responses never include the employee's **id**, **role** or **manager**: `EmployeeBo` has no `employeeId` field, and `role`/`managerId` are `WRITE_ONLY`. A client can't tell which listed employee is which login user. | employee-service `model/EmployeeBo.java` |
| 🟠 | employee-service has no exception handler: a duplicate department/territory name throws `DuplicateResourceException` and the client gets **500** (Spring's default error JSON) instead of 409. | employee-service `common/` |
| 🟡 | `GET /employee/list-employee` answers **201** with the message "Employee is Created". | employee-service `EmployeeController.getAllEmployee` |
| 🟠 | Sign-in errors other than bad credentials (`AccountDeactivatedException`, `AuthorizationUnavailableException`) have no exception handler, so clients get a generic **500** instead of 403 / 503. | authentication-service (no `@RestControllerAdvice`) |
| 🟡 | Sign-in's `username` field is matched against the user's **email** (`UserDetailsServiceImpl.loadUserByUsername` → `findByEmail`). The name is misleading. | authentication-service |
| 🟠 | `GET /auth/signupcheckEmailId/{email}` answers with HTTP **201** and the message "User is created" for what is a read-only check. | authentication-service `AuthController.checkEmailId` |
| 🔴 | `GET /authorization/privilege/view/{id}` can never return a privilege: the check is inverted (`if (privilegeId > 0) return 400`), so real ids get 400 and the rest 404. | authorization-service `PrivilegeController.viewPrivilege` |
| 🟠 | `GET /authorization/privilege/list/{pageIndex}/{pageSize}` returns **204** for `/list/0/…` without query parameters, and for `sortOrder` without `searchText` (NPE in the service). `searchText` is only applied together with `sortOrder`. | authorization-service `PrivilegeController.listPrivilege`, `PrivilegeServiceImpl.listAllPrivileges` |
| 🟠 | `GET /authorization/access/list` with a `sortBy` other than `accessId`/`accessName` falls back to `"id"`, which isn't a property, so it answers **500**. | authorization-service `AccessServiceImpl.getAccess` |
| 🟠 | authorization-service reports "not found" inconsistently: missing roles/privileges/accesses on update/delete/switch give **500** (NPE caught → `false`), `ResourceNotFoundException` has no handler (500), privilege delete gives 204, privilege update gives 200 `false`, and `get-access` gives 200 `success:true`. Role endpoints also return raw bodies instead of `ApiResponse`, and the `list-all` endpoints return 500 for an empty table. | authorization-service controllers and `*ServiceImpl` |
| 🟡 | authorization-service `ApiResponse.error(message, errors)` drops `errors` (passes `null`). | authorization-service `common/ApiResponse.java` |
| 🟡 | Older controllers wrap everything in `try { … } catch (Exception e) { e.printStackTrace(); return 500; }`, which hides real errors. | account, lead, inventory, opportunity, company, product, salesorder |
| 🟡 | RabbitMQ: product-service consumes `FinalProductQueue`, but nothing publishes to it, and `productExchange` isn't bound to the queue. | product-service `config/` |
| 🟡 | Table name typo `campany`. | company-service `entity/CompanyVO.java` |
