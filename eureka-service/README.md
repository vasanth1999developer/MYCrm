# eureka-service

The **service registry** (Netflix Eureka server). Every service registers under its `spring.application.name`, and the gateway and Feign clients resolve `lb://<name>` addresses through it. Start it **first**. For background on why the platform uses service discovery, see [Purpose.md](Purpose.md).

For a detailed, learning-oriented walkthrough of every REST endpoint (register, heartbeat, registry queries, status overrides…) with real requests and responses, see [ENDPOINTS.md](ENDPOINTS.md).

## At a glance

| | |
|---|---|
| Port | **8555** |
| Dashboard | http://localhost:8555 |
| Registry API | `GET http://localhost:8555/eureka/apps` |
| Stack | Spring Boot 2.7.15 · Spring Cloud 2021.0.1 · `spring-cloud-starter-netflix-eureka-server` |
| Base package | `com.microservices.eurekaservice` (one class, `EurekaServiceApplication`, `@EnableEurekaServer`) |
| Needs | nothing. No database, no secrets. |

## Run

```bash
mvn spring-boot:run          # or: java -jar target/eureka-service-0.0.1-SNAPSHOT.jar
```

## Configuration (`src/main/resources/application.yml`)

| Key | Value | Why |
|---|---|---|
| `server.port` | 8555 | every client points `eureka.client.serviceUrl.defaultZone` at `http://localhost:8555/eureka` |
| `eureka.client.register-with-eureka` / `fetch-registry` | `false` | it's the server, not a client |
| `eureka.instance.prefer-ip-address` | `true` | |
| `eureka.server.enable-self-preservation` | `false` | dead instances are evicted instead of kept (good for local dev, not for production) |
| `eureka.server.eviction-interval-timer-in-ms` | 5000 | evict every 5 s |
| `eureka.server.wait-time-in-ms-when-sync-empty` | 0 | serve immediately on startup |

## Registering a service

A client module needs `spring-cloud-starter-netflix-eureka-client` and:

```properties
spring.application.name=<name used in gateway routes and @FeignClient>
eureka.client.serviceUrl.defaultZone=http://localhost:8555/eureka
eureka.instance.instance-id=${spring.application.name}:${random.value}
```

Without `defaultZone` the client tries the Eureka default `localhost:8761` and never shows up here. That's what currently happens to salesorder-service.

**Currently registered names:** `api-gateway-service`, `authentication-service`, `authorization-service`, `employee-service`, `customer-service`, `lead-service`, `account-service`, `user-opportunity` (opportunity-service), `company-service`, `inventory-service` (×2: inventory-service **and** product-service). TaskManagement, project-service, ai-service and urlshortner have no Eureka client.

## Security

It's unauthenticated and has no `GatewayAuthFilter`. The gateway treats `/eureka` and `/eureka/**` as public paths, but no gateway route points at Eureka, so the registry is only reachable on port 8555. Don't expose that port outside the internal network.

## Docker / CI

* `Dockerfile` is **outdated**. It uses `adoptopenjdk/openjdk11` (which can't run this Java 17 jar) and `EXPOSE 8761` (the service listens on 8555). To use it, switch to a Java 17 JRE image and port 8555:
  ```dockerfile
  FROM eclipse-temurin:17-jre-alpine
  WORKDIR /app
  COPY target/eureka-service-0.0.1-SNAPSHOT.jar app.jar
  EXPOSE 8555
  ENTRYPOINT ["java", "-jar", "app.jar"]
  ```
* `jenkinsfile` checks out a different repository (see [docs/DEVELOPMENT.md](../docs/DEVELOPMENT.md#8-ci)).

## Tests

`EurekaServiceApplicationTests.contextLoads` only.
