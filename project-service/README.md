# project-service

A **placeholder** for project tracking. Right now it's an empty Spring Boot 4 application. There are no controllers, entities or persistence, only the main class and the gateway-secret filter.

> 🚧 The gateway routes `/project/**` to `lb://project-service`, but this module has **no Eureka client**, so the route returns 503. It isn't in the root `<modules>`, so build it from its own folder.

## At a glance

| | |
|---|---|
| Port | **8580** |
| Eureka name | — (not registered) |
| Gateway prefix | `/project` |
| Database | none in practice. MySQL `ms_mybiz_project` is configured, but there's no JPA or JDBC dependency. |
| Stack | **Spring Boot 4.0.3** (`jakarta.*`) · `spring-boot-starter-webmvc` · `spring-boot-starter-restclient` · Lombok |
| Base package | `com.clinch.crm.project` |
| Needs | env var `GATEWAY_SECRET` |

## Run

```bash
cd project-service && ./mvnw spring-boot:run
```

## What's there

```
com.clinch.crm.project
├── ProjectServiceApplication.java
└── gatewayauth/GatewayAuthFilter.java   jakarta.servlet variant: 403 without X-Gateway-Secret
```

`application.properties` also has leftovers copied from the Boot 2.7 services: `spring.jpa.*`, `spring.cloud.loadbalancer.ribbon.enabled` and `springdoc.*`. None of them has any effect without the matching dependencies.

## Tests

`ProjectServiceApplicationTests.contextLoads` only.

## Building it out (checklist)

1. **Pick a generation.** Either move it under `mycrm-parent` (Boot 2.7, `javax.*`, like the other services), or stay on Boot 4 and add Spring Cloud **2025.1.x** for Eureka. The 2021.0.x trains the other services use don't support Boot 4.
2. Add the Eureka client and `eureka.client.serviceUrl.defaultZone=http://localhost:8555/eureka`.
3. Add `spring-boot-starter-data-jpa` + `mysql-connector-j`, and create schema `ms_mybiz_project`.
4. Map controllers under `/project/...` (the gateway doesn't strip the prefix).
5. Write a README section per endpoint here, following the other modules.
