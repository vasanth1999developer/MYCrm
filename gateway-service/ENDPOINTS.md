# gateway-service: endpoints and methods (learning guide)

This guide explains **every endpoint** the gateway exposes and **every class and method** in its code, step by step, with real responses captured from a local run (port 8556, test secrets, no business services running). For the short reference, see [README.md](README.md). For the security design, see [docs/SECURITY.md](../docs/SECURITY.md).

---

## 1. The big picture

The gateway is the **only door** into the system. It has no controllers and no business endpoints of its own. Instead it:

1. **matches** the request path to a route (`/employee/**` → `employee-service`),
2. **authenticates** the caller (verifies the JWT),
3. **rewrites headers** (removes anything a client could fake, adds the trusted identity and the gateway secret),
4. **looks up** a live instance of the target service in its local copy of the Eureka registry,
5. **forwards** the request and streams the response back.

```
Browser / Postman
   │  GET /employee/list-employee
   │  Authorization: Bearer eyJ...
   ▼
┌───────────────────────────── gateway-service :8556 ─────────────────────────────┐
│ CorsWebFilter          answers preflight OPTIONS, adds CORS headers             │
│      ▼                                                                          │
│ RoutePredicateHandlerMapping   which route matches the path?   none → 404       │
│      ▼                                                                          │
│ Retry=3 (default filter)       re-sends GET on 5xx / IO errors                  │
│      ▼                                                                          │
│ AuthenticationFilter           JWT check → 401, header strip/set                │
│      ▼                                                                          │
│ ReactiveLoadBalancerClientFilter  lb://employee-service → 192.168.1.12:8562     │
│      ▼                                  no instance → 503                       │
│ NettyRoutingFilter             sends the HTTP request downstream                │
└─────────────────────────────────────────────────────────────────────────────────┘
   │  GET /employee/list-employee           (path unchanged, prefix NOT stripped)
   │  X-User-Id: 42, X-User-Roles: SALES_REP, ..., X-Gateway-Secret: <secret>
   ▼
employee-service  →  GatewayAuthFilter checks X-Gateway-Secret (403 if wrong)
```

**Reactive.** Spring Cloud Gateway runs on WebFlux + Netty, not the Servlet stack. Filters return `Mono<Void>` (a "promise" of completion) instead of blocking. That's why the code uses `exchange`, `chain.filter(...)` and `Mono`, not `HttpServletRequest`.

---

## 2. Endpoints

### 2.1 Proxied routes (the client-facing API)

Defined in `src/main/resources/application.properties`. Each route has an **id**, a **URI** (`lb://<eureka-name>`, where `lb` means "load-balance via Eureka"), a **predicate** (`Path=…`) and the **filter** `AuthenticationFilter`.

| Index | Route id | Path | Forwards to (Eureka name) | Real module | Status today |
|---|---|---|---|---|---|
| 0 | `authentication-core` | `/auth/**` | `authentication-service` | authentication-service (8557) | works |
| 1 | `inventory-core` | `/inventory/**` | `inventory-service` | inventory-service **and** product-service | split between two apps |
| 2 | `campaign-core` | `/campaign/**` | `campaign-service` | none | always 503 |
| 3 | `employee-core` | `/employee/**` | `employee-service` | employee-service (8562) | works |
| 4 | `opportunity-core` | `/opportunity/**` | `user-opportunity` | opportunity-service | works |
| 5 | `customer-core` | `/customer/**` | `customer-service` | customer-service | works |
| 6 | `lead-core` | `/lead/**` | `lead-service` | lead-service | works |
| 7 | `account-core` | `/account/**` | `account-service` | account-service | works |
| 8 | `contact-core` | `/contact/**` | `contacts-service` | none | always 503 |
| 9 | `salesorder-core` | `/salesorder/**` | `salesorder-service` | salesorder-service | 503 (doesn't register) |
| 10 | `task-core` | `/task/**` | `task-service` | none (TaskManagement isn't a Eureka client) | always 503 |
| 11 | `project-core` | `/project/**` | `project-service` | project-service (skeleton) | always 503 |
| 12 | `authorization-core` | `/authorization/**` | `authorization-service` | authorization-service (8778) | works |
| 13 | `company-core` | `/company/**` | `company-service` | company-service | works |

**Rules that apply to every route:**

* **All HTTP methods** are accepted (GET, POST, PUT, PATCH, DELETE…). The predicate only looks at the path.
* **The path is forwarded unchanged.** `/employee/territory/1` reaches employee-service as `/employee/territory/1`. That's why every controller's mapping must start with its prefix.
* **`/**` matches the prefix itself too:** `/employee` and `/employee/` also match `employee-core`.
* **Query string and body are forwarded as is.**
* **JWT required**, except for the public paths in 2.2.
* **Retry=3** (global default filter). With Spring Cloud Gateway's defaults this means: up to 3 retries, **only for `GET`**, when the downstream answers with a **5xx** or the connection fails (`IOException`, `TimeoutException`). POST/PUT/DELETE are never retried, so a create isn't run twice.

The endpoints *behind* each route are documented in that module's README (for example [employee-service/README.md](../employee-service/README.md)).

### 2.2 Public paths (no token needed)

From `RouteValidator`:

| Kind | Path | Endpoint it opens |
|---|---|---|
| exact | `/auth/signin` | log in, returns a JWT |
| exact | `/auth/signup` | self-registration |
| prefix | `/auth/signupcheckEmailId/…` | "is this email taken?" |
| exact | `/eureka` | nothing (no route points to Eureka) |
| prefix | `/eureka/…` | nothing (same) |

Everything else under a route needs `Authorization: Bearer <jwt>`.

### 2.3 Gateway-owned endpoints (not proxied)

| Method | Path | Response | Notes |
|---|---|---|---|
| GET | `/actuator` | links to exposed actuator endpoints | from `spring-boot-starter-actuator` |
| GET | `/actuator/health` | `{"status":"UP"}` | for health checks. **No JWT needed**: it's not a route, so `AuthenticationFilter` doesn't run. |
| OPTIONS | any path | CORS preflight answer | handled by `CorsWebFilter` before routing, so no JWT needed |

Only `health` is exposed by default. The useful `/actuator/gateway/routes` endpoint (lists live routes) is **not** exposed; to enable it locally, set `management.endpoints.web.exposure.include=health,gateway` and `management.endpoint.gateway.enabled=true`. Don't expose it in production.

### 2.4 Every response the gateway itself can produce

Captured from a real run:

| Status | When | Body |
|---|---|---|
| **401** | Secured path, and the token is missing, not `Bearer`, empty, has a bad signature, is expired, or has no `userId` claim | empty, header `WWW-Authenticate: Bearer` |
| **404** | Path matches **no route** (e.g. `/nothing/here`). Happens *before* auth, so you get 404 even without a token. | `{"timestamp":"…","path":"/nothing/here","status":404,"error":"Not Found","message":null,"requestId":"ca00a50"}` |
| **503** | Route matched, token OK (or path public), but Eureka has **no UP instance** for the target | `{"timestamp":"…","path":"/employee/list-employee","status":503,"error":"Service Unavailable","requestId":"1165f538"}` |
| **403** | CORS preflight for a disallowed origin or method (e.g. `PATCH`) | empty |
| **200** | CORS preflight allowed | empty, with `Access-Control-Allow-*` headers |
| 500 / 504 | Downstream connection error / timeout (after retries for GET) | JSON error like the above |

Anything else (200, 400, 403 with an `ApiResponse` body, …) comes from the downstream service, passed through unchanged. A downstream **403** with an empty body usually means the service's `GatewayAuthFilter` rejected the `X-Gateway-Secret`, i.e. `GATEWAY_SECRET` differs between the gateway and that service.

```bash
# 401: no token
curl -i http://localhost:8556/employee/list-employee
# 401: garbage token
curl -i http://localhost:8556/employee/list-employee -H "Authorization: Bearer garbage"
# public path: reaches authentication-service (or 503 if it isn't running)
curl -i -X POST http://localhost:8556/auth/signin -H "Content-Type: application/json" \
     -d '{"username":"admin@example.com","password":"..."}'
# protected path with a token from signin
curl -i http://localhost:8556/employee/list-employee -H "Authorization: Bearer <token>"
# preflight
curl -i -X OPTIONS http://localhost:8556/employee/list-employee \
     -H "Origin: http://localhost:5173" -H "Access-Control-Request-Method: POST"
```

---

## 3. The code: every class and method

```
com.clinch.crm.gateway
├── ApiGatewayServiceApplication.java
├── config/
│   ├── CorsConfig.java
│   └── DnsResolutionFixer.java
├── filter/
│   ├── AuthenticationFilter.java
│   └── RouteValidator.java
└── utils/
    └── JwtUtil.java
```

### 3.1 `ApiGatewayServiceApplication`

```java
@EnableEurekaClient
@SpringBootApplication
public class ApiGatewayServiceApplication {
    public static void main(String[] args) { SpringApplication.run(ApiGatewayServiceApplication.class, args); }
}
```

| Element | What it does |
|---|---|
| `@SpringBootApplication` | Auto-configuration + component scan of `com.clinch.crm.gateway`, which is how `AuthenticationFilter`, `RouteValidator`, `JwtUtil`, `CorsConfig` and `DnsResolutionFixer` get picked up. |
| `@EnableEurekaClient` | Registers the gateway with Eureka as `api-gateway-service` and keeps a local copy of the registry (refreshed every 5 s). That copy is what resolves `lb://…` URIs. Since Spring Cloud 2020 having the Eureka client on the classpath is already enough; the annotation is kept for clarity. |
| `main` | Starts Netty on port 8556. `spring.main.web-application-type=reactive` is needed because `spring-boot-starter-web` (Servlet/Tomcat) is also on the classpath, and Spring Cloud Gateway can't run on Servlet. |

Unused imports (`EnableDiscoveryClient`, `CrossOrigin`) are harmless leftovers.

### 3.2 `config/CorsConfig`

#### `corsWebFilter()` → `CorsWebFilter` (a `@Bean`)

Browsers block a page on `http://localhost:5173` from calling `http://localhost:8556` unless the server allows it (CORS). Before a "non-simple" request (JSON body, `Authorization` header, PUT/DELETE…), the browser sends a **preflight** `OPTIONS` request; this filter answers it.

| Setting | Value | Meaning |
|---|---|---|
| `setAllowedOrigins` | `http://localhost:5173` (Vite), `http://localhost:3000` (React/Next dev), `https://yourdomain.com` (placeholder) | Only pages from these origins may call the API from a browser. |
| `setAllowedMethods` | GET, POST, PUT, DELETE, OPTIONS | **PATCH is missing**, so a browser can't call `PATCH /authorization/role/switch-activity/{id}` (preflight gets 403). Postman/curl aren't affected; CORS is browser-only. |
| `setAllowedHeaders("*")` | any | Allows `Authorization`, `Content-Type`, etc. |
| `setAllowCredentials(true)` | | Allows cookies/auth headers. That's why origins must be listed explicitly; `*` isn't allowed with credentials. |
| `setMaxAge(3600)` | 1 h | Browser caches the preflight answer. |
| `registerCorsConfiguration("/**", …)` | | Applies to every path. |

It's a `WebFilter`, so it runs **before** route matching and before `AuthenticationFilter`. That's why preflights work without a token.

### 3.3 `config/DnsResolutionFixer`

#### `customize(HttpClient httpClient)` → `HttpClient`

Implements `HttpClientCustomizer`, a hook Spring Cloud Gateway calls when it builds the Reactor Netty `HttpClient` used to call downstream services. It swaps Netty's own asynchronous DNS resolver for `DefaultAddressResolverGroup.INSTANCE`, which uses the **JVM/OS resolver**. Netty's resolver sometimes fails on Windows, inside VPNs, or with hostnames that only exist in the hosts file, producing `UnknownHostException` / "Failed to resolve" errors. With the OS resolver, the gateway resolves names exactly as `ping` would. (With `prefer-ip-address` instances it rarely matters, but it prevents a confusing failure.)

### 3.4 `filter/RouteValidator`

Decides which paths are public.

| Member | Type | Purpose |
|---|---|---|
| `openExactPaths` | `Set<String>` | Public only on an **exact** match: `/auth/signin`, `/auth/signup`, `/eureka`. |
| `openPathPrefixes` | `List<String>` | Public for anything **under** them. Each ends in `/` so `/eureka/` doesn't also match `/eurekaXYZ`. |
| `isSecured` | `Predicate<ServerHttpRequest>` | Returns `true` (needs JWT) unless the path is in `openExactPaths` or starts with a prefix in `openPathPrefixes`. |

```java
public Predicate<ServerHttpRequest> isSecured = request -> {
    String path = request.getURI().getPath();
    return !openExactPaths.contains(path)
            && openPathPrefixes.stream().noneMatch(path::startsWith);
};
```

Why exact matching matters: an older version used `contains`, so `/employee/x/auth/signin` counted as public and skipped auth. Exact/prefix matching closes that hole (covered by the test `rejectsPathThatOnlyContainsAnOpenEndpoint`).

Examples:

| Path | `isSecured` |
|---|---|
| `/auth/signin` | false (public) |
| `/auth/signin/` | **true** (exact match fails, trailing slash) |
| `/auth/signupcheckEmailId/a@b.com` | false |
| `/auth/users` | true |
| `/employee/x/auth/signin` | true |

**To make a new endpoint public, add it here and nowhere else.**

### 3.5 `utils/JwtUtil`

#### Constructor `JwtUtil(@Value("${jwtSecret}") String jwtSecret)`

* `jwtSecret` comes from `jwtSecret=${JWT_SECRET}`. With no env var the app fails at startup (`Could not resolve placeholder 'JWT_SECRET'`); with a blank one it throws `IllegalStateException`.
* `Decoders.BASE64.decode(...)` turns the Base64 string into bytes, and `Keys.hmacShaKeyFor(...)` builds an HMAC-SHA key. It throws `WeakKeyException` if the key is shorter than 256 bits (32 bytes), so a weak secret fails at startup, not at request time.
* The key is built **once** and stored in `signKey` (final), so each request doesn't decode the secret again.
* It must be the **same** value authentication-service signs with (HS256 is symmetric: same key signs and verifies).

#### `parseClaims(String token)` → `Claims`

```java
return Jwts.parserBuilder().setSigningKey(signKey).build().parseClaimsJws(token).getBody();
```

In one call it:
1. splits `header.payload.signature`,
2. recomputes the HMAC of `header.payload` with `signKey` and compares it with the signature (tampered → `SignatureException`),
3. checks `exp` (expired → `ExpiredJwtException`),
4. returns the payload as `Claims` (a `Map<String,Object>`).

Throws `JwtException` (any of the above, or `MalformedJwtException`) or `IllegalArgumentException` (null/empty). `parseClaimsJws` (with the **s**) requires a signed token, so an unsigned `alg: none` token is rejected.

#### `validateToken(String token)` → `boolean`

Wraps `parseClaims` and returns `true`/`false`, logging the reason. **The filter doesn't use it**; it calls `parseClaims` directly because it needs the claims anyway. It's kept as a convenience.

### 3.6 `filter/AuthenticationFilter` (the heart of the gateway)

```java
@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config>
```

**Why a "factory"?** In Spring Cloud Gateway, each `GatewayFilterFactory` bean is known by its class name with any `GatewayFilterFactory` suffix removed (`RetryGatewayFilterFactory` → `Retry`). This class has no such suffix, so its name is simply `AuthenticationFilter`, which is what `filters=AuthenticationFilter` refers to. The factory's `apply(Config)` builds the actual `GatewayFilter` once per route.

#### Constants

| Constant | Value | Role |
|---|---|---|
| `BEARER_PREFIX` | `"Bearer "` | Expected start of the `Authorization` header (with the space). |
| `USER_HEADER_PREFIX` | `"X-User-"` | Any client header starting with this (any casing) is deleted. |
| `HEADER_USER_ID` | `X-User-Id` | from claim `userId` |
| `HEADER_USER_EMAIL` | `X-User-Email` | from claim `email` |
| `HEADER_USER_NAME` | `X-User-Name` | from claim `username` |
| `HEADER_USER_ROLES` | `X-User-Roles` | from claim `roles` (list → `A,B`) |
| `HEADER_USER_PRIVILEGES` | `X-User-Privileges` | from claim `privileges` (list → `A,B`) |
| `LEGACY_ID_HEADER` | `id` | also the user id; older controllers read `@RequestHeader("id")` for `createdBy` |
| `GATEWAY_SECRET_HEADER` | `X-Gateway-Secret` | proof for downstream `GatewayAuthFilter` |

#### Fields

* `gatewaySecret`: from `gateway.secret=${GATEWAY_SECRET}`.
* `validator` (`RouteValidator`) and `jwtUtil` (`JwtUtil`): injected with `@Autowired` field injection. (The tests set them with `ReflectionTestUtils`; constructor injection would be cleaner.)

#### Constructor `AuthenticationFilter()`

Calls `super(Config.class)` so the factory knows which class to bind route arguments into. `Config` is empty because this filter takes no arguments.

#### `apply(Config config)` → `GatewayFilter`

Returns a lambda `(exchange, chain) -> Mono<Void>`. `exchange` holds the request and response; `chain.filter(exchange)` passes the request on to the next filter (and finally to the downstream service). Returning without calling `chain.filter` **stops** the request. The lambda's logic:

```
                 ┌─ isSecured(path)? ─┐
                no                    yes
                 │                     │
  strip identity headers      Authorization header present and starts with "Bearer "?
  set X-Gateway-Secret          no ──► 401
  forward                      yes
                                 │
                       token after "Bearer " empty? ── yes ──► 401
                                 │ no
                       parseClaims(token) throws? ──── yes ──► 401 (logged)
                                 │ no
                       claim "userId" empty/missing? ─ yes ──► 401
                                 │ no
                       strip identity headers, then set:
                         X-User-Id, id, X-User-Email, X-User-Name,
                         X-User-Roles, X-User-Privileges, X-Gateway-Secret
                       forward
```

Things worth noticing:
* **Public paths still get the header treatment.** A client could otherwise send `X-User-Roles: ADMIN` to `/auth/signin`. Public requests also get `X-Gateway-Secret`, or the downstream service would reject them with 403.
* **The token is never forwarded as identity.** Downstream services trust only the `X-User-*` headers, which can only come from here, because they're always stripped first and a request without the right `X-Gateway-Secret` is rejected by the service.
* **`h.set(...)` replaces**, it doesn't append, so there's exactly one value per header.
* The `Authorization` header itself **is** forwarded, so a downstream service could verify the token again if it wanted.
* Requests are immutable in WebFlux, so the code uses `request.mutate()...build()` and `exchange.mutate().request(request).build()` to create modified copies.

#### `stripIdentityHeaders(ServerHttpRequest request)` → `ServerHttpRequest` (private)

Builds a copy of the request without:
* every header whose name, lower-cased, starts with `x-user-` (so `x-user-privileges` and `X-USER-ID` are caught too),
* `id`,
* `X-Gateway-Secret` (so a client can't send its own guess, and the real value is set later).

It collects the names first and removes them after the loop, because removing entries while iterating `keySet()` would throw `ConcurrentModificationException`.

#### `claimAsString(Object value)` → `String` (private static)

Turns a claim into a header value:

| Claim value | Result |
|---|---|
| `null` (missing) | `""` |
| `42` (Long) | `"42"` |
| `"rep@example.com"` | `"rep@example.com"` |
| `["LEAD_READ","LEAD_WRITE"]` (JSON array → `Collection`) | `"LEAD_READ,LEAD_WRITE"` |

Downstream services split roles/privileges on `,`.

#### `unauthorized(ServerWebExchange exchange, String reason)` → `Mono<Void>` (private)

Logs `Rejecting GET /employee/list: <reason>` at WARN, sets **401** and `WWW-Authenticate: Bearer` (the standard hint that a Bearer token is needed), and calls `response.setComplete()`, which ends the response with an empty body **without** calling `chain.filter`. The request never reaches a service. The reason is only logged, not sent to the client, so attackers don't learn why their token failed.

#### `Config` (static inner class)

Empty placeholder required by `AbstractGatewayFilterFactory<C>`. If you ever wanted per-route options (e.g. `filters=AuthenticationFilter=requiredRole:ADMIN`), you'd add fields here.

### 3.7 `application.properties` line by line

| Property | Effect |
|---|---|
| `server.port=8556` | gateway port |
| `spring.application.name=api-gateway-service` | name in Eureka |
| `eureka.client.service-url.defaultZone=http://localhost:8555/eureka` | where Eureka is |
| `eureka.client.registry-fetch-interval-seconds=5` | refresh the local registry copy every 5 s (default 30), so new/removed instances are noticed fast |
| `spring.main.web-application-type=reactive` | force WebFlux/Netty (see 3.1) |
| `jwtSecret=${JWT_SECRET}` | JWT key, read by `JwtUtil` |
| `gateway.secret=${GATEWAY_SECRET}` | read by `AuthenticationFilter` |
| `spring.cloud.gateway.discovery.locator.enabled=false` | **security-critical.** If `true`, the gateway auto-creates a route `/<service-id>/**` for every Eureka service, e.g. `/employee-service/employee/...`, and those routes **don't** have `AuthenticationFilter`. That would bypass JWT checks. |
| `spring.cloud.gateway.default-filters[0]=Retry=3` | applied to every route (see 2.1) |
| `spring.cloud.gateway.routes[N].id / uri / predicates / filters` | the 14 routes in 2.1 |

`application.yml` exists but is empty.

---

## 4. Tests

### `AuthenticationFilterTest` (8 unit tests, no Spring context, no network)

`setUp()` builds the filter by hand: `new AuthenticationFilter()`, then injects a real `RouteValidator`, a `JwtUtil` with a test key, and a test gateway secret via `ReflectionTestUtils`. The fake `chain` just records the request it receives in `forwarded`, so a test can check *what would have been sent downstream*, or that nothing was sent.

| Helper | Purpose |
|---|---|
| `token(byte[] key, long expiresInMs)` | builds a signed JWT with `userId=42`, email, username, roles `[SALES_REP]`, privileges `[LEAD_READ, LEAD_WRITE]` |
| `run(MockServerHttpRequest)` | runs the filter against a mock exchange and blocks until done |
| `assertRejected(exchange)` | status is 401 **and** nothing was forwarded |

| Test | Proves |
|---|---|
| `rejectsMissingAuthorizationHeader` | no header → 401 |
| `rejectsNonBearerHeader` | token without `Bearer ` → 401 |
| `rejectsTokenSignedWithOtherKey` | wrong signature → 401 |
| `rejectsExpiredToken` | expired → 401 |
| `rejectsPathThatOnlyContainsAnOpenEndpoint` | `/employee/x/auth/signin` isn't public |
| `validTokenForwardsIdentityFromClaimsAndIgnoresSpoofedHeaders` | spoofed `id`, `X-User-Id`, `x-user-privileges`, `X-Gateway-Secret` are replaced with values from the token and the real secret |
| `openEndpointIsForwardedWithoutTokenButSpoofedHeadersAreStripped` | `/auth/signin` passes without a token, spoofed headers removed, secret added |
| `openPrefixEndpointIsForwardedWithoutToken` | `/auth/signupcheckEmailId/a@b.com` passes |

```bash
mvn test -Dtest=AuthenticationFilterTest
```

### `ApiGatewayServiceApplicationTests.contextLoads()`

Boots the whole app; needs `JWT_SECRET`, `GATEWAY_SECRET` and ideally Eureka.

---

## 5. Try it yourself

```bash
# 1. Eureka first
cd eureka-service && mvn spring-boot:run
# 2. Gateway with test secrets (bash)
export JWT_SECRET=$(printf 'test-secret-test-secret-test-secret-123' | base64)
export GATEWAY_SECRET=gw-test
cd gateway-service && mvn spring-boot:run
# 3. Observe without any business service running:
curl -i http://localhost:8556/actuator/health          # 200 {"status":"UP"}
curl -i http://localhost:8556/nothing                  # 404 (no route, no auth needed)
curl -i http://localhost:8556/employee/list-employee   # 401 (route matched, no token)
curl -i -X POST http://localhost:8556/auth/signin      # 503 (public, but authentication-service is down)
# 4. Start authentication-service and employee-service with the SAME two env vars,
#    sign in, then call /employee/... with the token and watch X-User-* arrive
#    (log the headers in a controller, or use a request-dump endpoint).
```

## 6. Adding things

* **A new service route:** add `routes[14]` with id, `lb://<spring.application.name>`, `Path=/<prefix>/**` and **always** `filters=AuthenticationFilter`. Give the service a `GatewayAuthFilter` and `gateway.secret=${GATEWAY_SECRET}` (see [README.md](README.md#adding-a-route)).
* **A new public endpoint:** add it to `RouteValidator.openExactPaths` (exact) or `openPathPrefixes` (prefix ending in `/`).
* **A new identity header:** add a claim in authentication-service's `JwtUtils`, then a `HEADER_…` constant and an `h.set(...)` line in `apply`. If it starts with `X-User-`, stripping is automatic.
