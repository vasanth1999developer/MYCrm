# TaskManagement

A simple **task tracker**: tasks with a title, description, status and due date. It's the only business module on **PostgreSQL**.

> ⚠️ **Not reachable through the gateway.** The gateway routes `/task/**` to `lb://task-service`, but this module registers as nothing: it's named `TaskManagement` and has **no Eureka client**. It also uses port **8566**, the same as product-service, and has no gateway-secret filter. See [docs/KNOWN_ISSUES.md](../docs/KNOWN_ISSUES.md#routing-and-service-discovery).

## At a glance

| | |
|---|---|
| Port | **8566** (clashes with product-service) |
| Eureka name | — (no Eureka client; `spring.application.name=TaskManagement`) |
| Gateway prefix | `/task` (route target `task-service` doesn't exist) |
| Database | PostgreSQL `localhost:5432/taskdb`, `postgres` / `admin` |
| Stack | Spring Boot 2.7.15 · Spring Cloud 2021.0.8 (BOM only) · Spring Data JPA · Validation · springdoc · `spring-boot-starter-amqp` (unused) |
| Base package | `com.fynxt.taskmanagement` |
| Needs | PostgreSQL with database `taskdb` |

## Run

```bash
createdb -U postgres taskdb      # once
mvn spring-boot:run              # http://localhost:8566
```

Swagger UI: `http://localhost:8566/swagger-ui.html` (springdoc default path).

## API

Called directly on port 8566. There's no gateway route that works and no auth.

| Method | Path | Body | Notes |
|---|---|---|---|
| `POST` | `/task/create-task` | `TaskBo` (`@Valid`) | |
| `GET` | `/task/get-all-task` | — | |
| `GET` | `/task/get-task/{id}` | — | |
| `PUT` | `/task/update-task/{id}` | `TaskBo` | not validated |
| `DELETE` | `/task/delete-task/{id}` | — | **hard delete** (repository `delete`) |

Errors are handled by `CustomGlobalException/GlobalExceptionHandler`.

## Data model

| Entity → table | Fields |
|---|---|
| `TaskVo` → `task` | `id`, `title`, `description`, `status`, `dueDate`, `createdAt`, `updatedAt` |

A task isn't linked to an employee, project or customer yet.

## Configuration

| Property | Value |
|---|---|
| `server.port` | 8566 |
| `spring.datasource.*` | `jdbc:postgresql://localhost:5432/taskdb`, `postgres` / `admin` |
| `spring.jpa.hibernate.ddl-auto` | `update` |

## Code layout

```
com.fynxt.taskmanagement
├── controller/            TaskController
├── service/  repository/
├── VO/                    TaskVo
├── BO/                    TaskBo
└── CustomGlobalException/ GlobalExceptionHandler
```

The package and folder naming (`com.fynxt`, upper-case `VO`/`BO`) differ from the rest of the platform.

## Tests

`TaskManagementApplicationTests.contextLoads` only. It needs PostgreSQL.

## Joining the platform (checklist)

1. Add `spring-cloud-starter-netflix-eureka-client` and `eureka.client.serviceUrl.defaultZone=http://localhost:8555/eureka`.
2. Set `spring.application.name=task-service` (to match the existing route), or change the route to the new name.
3. Move to a free port (e.g. 8571).
4. Copy `GatewayAuthFilter` into `com.fynxt.taskmanagement.gatewayauth` and add `gateway.secret=${GATEWAY_SECRET}`.
5. Remove the unused AMQP starter, or use it.
