# Security model

Security is enforced in three layers:

1. **The gateway authenticates the user.** It checks the JWT and sets trusted identity headers.
2. **Services only accept traffic from the gateway** (or each other), proven by a shared secret header.
3. **RBAC data** (roles → privileges) lives in authorization-service and is copied into the JWT at sign-in. *It is not yet enforced per endpoint.*

## 1. Tokens (JWT)

| | |
|---|---|
| Issued by | `authentication-service` — `POST /auth/signin` |
| Algorithm | HS256, key = Base64-decoded `JWT_SECRET` (must be ≥ 32 bytes) |
| Lifetime | `jwtExpirationMs=900000` (15 minutes). There's no refresh token, so the client signs in again. |
| Issuer | `mycrm-auth` |
| Claims | `sub` (user id), `jti`, `iat`, `exp`, `userId`, `email`, `username`, `roles` (array), `privileges` (array) |
| Verified by | `gateway-service` `JwtUtil.parseClaims` (signature + expiry) |

At sign-in, authentication-service calls authorization-service (`POST /authorization/privilege/privileges`) to turn the user's roles into privileges. If authorization-service is down or returns no privileges, **sign-in fails** (`AuthorizationUnavailableException`) instead of issuing a token without privileges.

## 2. Gateway: `AuthenticationFilter`

`gateway-service/src/main/java/com/microservices/apigatewayservice/filter/AuthenticationFilter.java` is applied to **every route**, including `/auth/**`. For each request it:

1. **Decides whether the path is public** using `RouteValidator`:
   * exact matches: `/auth/signin`, `/auth/signup`, `/eureka`
   * prefixes: `/auth/signupcheckEmailId/`, `/eureka/`

   Matching is exact or prefix-only. A path that merely *contains* one of these (e.g. `/employee/x/auth/signin`) is **not** public.
2. **Strips headers a client must never control:** every `X-User-*` header, the legacy `id` header, and `X-Gateway-Secret`.
3. For non-public paths, **requires `Authorization: Bearer <jwt>`**. It returns `401` with `WWW-Authenticate: Bearer` when:
   * the header is missing or isn't `Bearer …`
   * the token is empty
   * the signature is bad, or the token is expired or malformed
   * the token has no `userId` claim

   Rejected requests are never forwarded, and the global `Retry=3` doesn't apply to them.
4. **Sets identity headers from the verified claims:**

   | Header | Value |
   |---|---|
   | `X-User-Id` | `userId` claim |
   | `id` | same as `X-User-Id`. Older controllers read it for `createdBy`. |
   | `X-User-Email` | `email` |
   | `X-User-Name` | `username` |
   | `X-User-Roles` | comma-separated `roles` |
   | `X-User-Privileges` | comma-separated `privileges` |
5. **Adds `X-Gateway-Secret`** (value of `GATEWAY_SECRET`) and forwards the request.

> **For service developers:** take the caller's identity **only** from these headers. They are trustworthy because the gateway overwrites anything a client sends and services reject traffic that didn't come through the gateway.

Covered by `gateway-service/src/test/java/.../filter/AuthenticationFilterTest.java`.

## 3. Services: `GatewayAuthFilter`

Each routed service has `<base-package>.gatewayauth.GatewayAuthFilter`, a servlet filter that runs first (`HIGHEST_PRECEDENCE`). It returns **`403`** unless `X-Gateway-Secret` matches `gateway.secret` (`${GATEWAY_SECRET}`), compared in constant time. This blocks callers that skip the gateway by hitting a service port (e.g. `localhost:8562`) and could otherwise set `id` / `X-User-*` freely.

* Present in: authentication, authorization, employee, customer, lead, account, opportunity, company, inventory, product, salesorder, project.
* **Not** present in: eureka-service, gateway-service, TaskManagement, ai-service, urlshortner. None of these has a gateway route, so nothing would reach them with the secret.
* Feign callers (authentication-service, employee-service) send the header with `GatewaySecretFeignInterceptor`.
* Actuator endpoints are behind the filter too, so `/actuator/health` on a service port returns 403 without the header.

Covered by `employee-service/src/test/java/.../gatewayauth/GatewayAuthFilterTest.java`.

## 4. Secrets and configuration

| Variable | Used by | Format | Notes |
|---|---|---|---|
| `JWT_SECRET` | gateway-service, authentication-service | Base64, ≥ 32 bytes decoded | Must be identical in both. Maps to `jwtSecret=${JWT_SECRET}`. |
| `GATEWAY_SECRET` | gateway-service + the 12 services listed above | any string (32 random bytes, Base64, recommended) | Must be identical everywhere. Maps to `gateway.secret=${GATEWAY_SECRET}`. |
| `ANTHROPIC_API_KEY` | ai-service | Anthropic API key | Optional for the rest of the platform |

Neither shared secret has a default. A service whose secret is missing **fails at startup** with `Could not resolve placeholder 'GATEWAY_SECRET'` (or `JWT_SECRET`). This is on purpose, so a service never falls back to a known key.

> **The old JWT secret is in git history** (it used to be hard-coded in `JwtUtil` and `application.properties`). Treat it as public, never reuse it, and generate a fresh `JWT_SECRET` for every environment.

Database credentials (`root`/`admin`, `postgres`/`admin`) are still committed in the `application.properties` files. They're fine for local development, but they must be overridden in any shared environment (e.g. `SPRING_DATASOURCE_PASSWORD`).

## 5. Passwords in API responses

Passwords must never appear in a response body. They're excluded at the JSON level, and a serialization unit test guards each class:

| Class | Annotation | Why | Test |
|---|---|---|---|
| authentication `models/User` | `@JsonIgnore` | the entity is returned by `GET /auth/get-loggedInUserById` | `UserJsonTest` |
| employee `model/EmployeeBo` | `@JsonProperty(access = WRITE_ONLY)` | request body **and** response of `create-employee` | `EmployeeBoJsonTest` |
| company `bean/CompanyBO` | `@JsonProperty(access = WRITE_ONLY)` | request body **and** response of create/update/get-by-id | `CompanyPasswordJsonTest` |
| company `entity/CompanyVO` | `@JsonIgnore` | returned by `GET /company/get-companyList` | `CompanyPasswordJsonTest` |

Use `WRITE_ONLY` when a class is also a request body (the password must still be *read*), and `@JsonIgnore` when it's only ever written out. Don't add either to outgoing Feign request DTOs such as employee-service `CreateUserRequest`, because the password has to be serialized there. When you add a new class with a secret field, add the annotation and a test like these.

## 6. CORS

`gateway-service` `config/CorsConfig.java`: allowed origins `http://localhost:5173`, `http://localhost:3000`, `https://yourdomain.com` (placeholder); methods GET, POST, PUT, DELETE, OPTIONS (**not PATCH**); all headers; credentials allowed; max age 1 h.

## 7. RBAC (authorization-service)

* **Roles** (`RoleVo`), **privileges** (`PrivilegeVo`) and **access** records (`AccessVo`) are managed under `/authorization/role`, `/authorization/privilege` and `/authorization/access`.
* The role catalogue (SUPER_ADMIN, ADMIN, SALES_MANAGER, SALES_EXECUTIVE, SUPPORT_MANAGER, SUPPORT_AGENT, INVENTORY_MANAGER, PROJECT_MANAGER, ACCOUNT_MANAGER, EMPLOYEE, SYSTEM) is described in [authorization-service/Readme.md](../authorization-service/Readme.md).
* Privileges reach services in `X-User-Privileges`, but **no endpoint checks them yet**. Any authenticated user can call any endpoint, including admin-only ones such as `POST /auth/users`. Per-endpoint enforcement is the next planned step (see KNOWN_ISSUES).

## 8. Known security gaps

The full list with locations is in [KNOWN_ISSUES.md](KNOWN_ISSUES.md#security). In short:

* there are no privilege checks per endpoint
* `POST /employee/create-employee` skips bean validation (no `@Valid`)
* company passwords are stored in plain text
* authentication-service still contains demo endpoints under `/api/test/*`. They have no gateway route and sit behind `GatewayAuthFilter`, so they're unreachable, but they should be deleted.
* DB credentials are committed
