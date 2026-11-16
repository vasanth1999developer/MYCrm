# urlshortner

A **placeholder** for an internal URL-shortening utility. It's a freshly generated Spring Boot 4 application with only the main class, no endpoints yet.

## At a glance

| | |
|---|---|
| Port | 8080 (Spring default; `server.port` isn't set) |
| Eureka name | — (no Eureka client) |
| Gateway prefix | — (no route) |
| Database | none |
| Stack | **Spring Boot 4.0.3** (`jakarta.*`) · `spring-boot-starter-webmvc` · `spring-boot-starter-restclient` · Lombok |
| Base package | `com.clinch.crm.urlshortener` |
| Needs | nothing |

It **is** listed in the root `<modules>`, so `mvn install` from the root builds it, but it uses its own Spring Boot 4 parent instead of `clinch-parent`.

## Run

```bash
cd urlshortner && ./mvnw spring-boot:run
```

## What's there

```
com.clinch.crm.urlshortener
└── UrlshortnerApplication.java
```

`HELP.md` is the Spring Initializr default.

## Tests

`UrlshortnerApplicationTests.contextLoads` only.

## Before building features

* Decide on a Boot generation (see [project-service/README.md](../project-service/README.md#building-it-out-checklist)), and give it a port that doesn't collide with others (8080 is commonly taken).
* If it's exposed to users, register it in Eureka, add `GatewayAuthFilter` and a gateway route.
