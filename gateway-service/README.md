# gateway-service

The **single entry point** for all client traffic. It's a Spring Cloud Gateway (reactive / WebFlux) that routes each path prefix to a service found through Eureka. It also **authenticates every request with a JWT**, sets trusted identity headers, and attaches the shared gateway secret that downstream services require.

For a detailed, learning-oriented walkthrough of every route, gateway response, class and method, see [ENDPOINTS.md](ENDPOINTS.md).

## At a glance

| | |
|---|---|
| Port | **8556** |
| Eureka name | `api-gateway-service` |
| Database | none |
| Stack | Spring Boot 2.7.15 · Spring Cloud 2021.0.2 · Spring Cloud Gateway · jjwt 0.11.5 |
| Base package | `com.microservices.apigatewayservice` |
| Needs | `eureka-service` running; env vars `JWT_SECRET`, `GATEWAY_SECRET` |

## Run

```bash
# JWT_SECRET and GATEWAY_SECRET must be set (same values as the other services)
mvn spring-boot:run          # or: java -jar target/gateway-service-0.0.1-SNAPSHOT.jar
```

It fails at startup with `Could not resolve placeholder 'JWT_SECRET'` / `'GATEWAY_SECRET'` if either is missing.

## Routes

Defined in `src/main/resources/application.properties` (`application.yml` is empty). The discovery locator is **off**, so only these routes exist, and **every** route has `filters=AuthenticationFilter`. A global `Retry=3` default filter applies to all of them. Prefixes are **not** stripped.

| Route id | Path | Target | Backed by |
|---|---|---|---|
| `authentication-core` | `/auth/**` | `lb://authentication-service` | authentication-service |
| `authorization-core` | `/authorization/**` | `lb://authorization-service` | authorization-service |
| `employee-core` | `/employee/**` | `lb://employee-service` | employee-service |
| `customer-core` | `/customer/**` | `lb://customer-service` | customer-service |
| `lead-core` | `/lead/**` | `lb://lead-service` | lead-service |
| `account-core` | `/account/**` | `lb://account-service` | account-service |
| `opportunity-core` | `/opportunity/**` | `lb://user-opportunity` | opportunity-service |
| `company-core` | `/company/**` | `lb://company-service` | company-service |
| `inventory-core` | `/inventory/**` | `lb://inventory-service` | inventory-service **and** product-service (name clash) |
| `salesorder-core` | `/salesorder/**` | `lb://salesorder-service` | not registered (503) |
| `project-core` | `/project/**` | `lb://project-service` | not registered (503) |
| `task-core` | `/task/**` | `lb://task-service` | no such service (503) |
| `campaign-core` | `/campaign/**` | `lb://campaign-service` | no such service (503) |
| `contact-core` | `/contact/**` | `lb://contacts-service` | no such service (503) |

## How a request is handled

`filter/AuthenticationFilter.java`:

1. `RouteValidator.isSecured` decides whether the path is public.
   * Exact paths: `/auth/signin`, `/auth/signup`, `/eureka`
   * Prefixes: `/auth/signupcheckEmailId/`, `/eureka/`
2. Strips client-sent `X-User-*`, `id` and `X-Gateway-Secret` headers.
3. On a non-public path, requires `Authorization: Bearer <jwt>` and verifies it with `utils/JwtUtil.parseClaims`. If that fails it returns **401** with `WWW-Authenticate: Bearer`, and the request isn't forwarded.
4. Sets `X-User-Id`, `id`, `X-User-Email`, `X-User-Name`, `X-User-Roles`, `X-User-Privileges` from the token's claims.
5. Adds `X-Gateway-Secret` and forwards the request.

Full description: [docs/SECURITY.md](../docs/SECURITY.md).

```bash
# public
curl -X POST http://localhost:8556/auth/signin -H "Content-Type: application/json" \
     -d '{"username":"admin@example.com","password":"..."}'   # "username" = email
# protected
curl http://localhost:8556/employee/list-employee -H "Authorization: Bearer <token>"
```

## Configuration

| Property | Value | Notes |
|---|---|---|
| `server.port` | 8556 | |
| `spring.main.web-application-type` | `reactive` | needed because `spring-boot-starter-web` is also on the classpath |
| `jwtSecret` | `${JWT_SECRET}` | Base64, must equal authentication-service's |
| `gateway.secret` | `${GATEWAY_SECRET}` | sent as `X-Gateway-Secret` |
| `spring.cloud.gateway.discovery.locator.enabled` | `false` | when enabled, it creates `/<service-id>/**` routes **without** the auth filter |
| `spring.cloud.gateway.default-filters[0]` | `Retry=3` | |
| `eureka.client.registry-fetch-interval-seconds` | 5 | picks up new instances quickly |

## Code layout

```
com.microservices.apigatewayservice
├── ApiGatewayServiceApplication.java   @EnableEurekaClient
├── config/
│   ├── CorsConfig.java                  allowed origins localhost:5173, localhost:3000; GET/POST/PUT/DELETE/OPTIONS
│   └── DnsResolutionFixer.java          Netty uses the OS DNS resolver for downstream calls
├── filter/
│   ├── AuthenticationFilter.java        JWT check, header stripping/setting, gateway secret
│   └── RouteValidator.java              public paths
└── utils/
    └── JwtUtil.java                     key from JWT_SECRET, parseClaims / validateToken
```

## Tests

```bash
mvn test -Dtest=AuthenticationFilterTest   # 8 unit tests, no infrastructure needed
```

They cover missing, non-Bearer, wrong-key and expired tokens, the "path only contains an open endpoint" trick, header spoofing (including `X-Gateway-Secret`), identity propagation, and public endpoints. `ApiGatewayServiceApplicationTests.contextLoads` needs the env vars and Eureka.

## Adding a route

Add a block with the next free index and **always** include the filter:

```properties
spring.cloud.gateway.routes[14].id=<name>-core
spring.cloud.gateway.routes[14].uri=lb://<spring.application.name>
spring.cloud.gateway.routes[14].predicates=Path=/<prefix>/**
spring.cloud.gateway.routes[14].filters=AuthenticationFilter
```

To make a new endpoint public, add it to `RouteValidator` and nowhere else.

## Known issues

* 5 routes lead nowhere: salesorder (not registered), project, task, campaign, contact. `/inventory/**` is split between two different apps.
* CORS: placeholder origin `https://yourdomain.com`, and no `PATCH`.
* The `jenkinsfile` targets another repository (see [docs/DEVELOPMENT.md](../docs/DEVELOPMENT.md#8-ci)).

See [docs/KNOWN_ISSUES.md](../docs/KNOWN_ISSUES.md).
