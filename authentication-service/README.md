# authentication-service

Owns **login users** and **issues JWTs**. When a user signs in, it checks the password, asks authorization-service for the privileges of the user's roles, and returns a signed token that the gateway accepts on every later request. It also creates login users on behalf of employee-service.

> The Java base package is `com.clinch.crm.authentication`. The old misspelling survives only in the Maven artifact (`authendication-service`) and the MySQL schema (`ms_mybiz_authendication`); renaming the schema would cut the service off from existing data.

## At a glance

| | |
|---|---|
| Port | **8557** |
| Eureka name | `authentication-service` |
| Gateway prefix | `/auth` |
| Database | MySQL `ms_mybiz_authendication` |
| Stack | Spring Boot 2.7.15 · Spring Cloud 2021.0.1 · Spring Security · OpenFeign · jjwt 0.11.5 |
| Base package | `com.clinch.crm.authentication` |
| Needs | MySQL schema, authorization-service (for sign-in), env vars `JWT_SECRET`, `GATEWAY_SECRET` |

## Run

```bash
mvn spring-boot:run
```

## API

Base URL through the gateway: `http://localhost:8556`. Responses use the `ApiResponse` envelope (`success`, `message`, `data`, `errors`, `timestamp`).

| Method | Path | Auth | Body / params | Returns |
|---|---|---|---|---|
| `POST` | `/auth/signin` | **public** | `{"username": "<email>", "password": "…"}` | `JwtResponse` (JSON keys follow the getters): `accessToken`, `tokenType` = `Bearer`, `id`, `username`, `email`, `roles`, `active` |
| `GET` | `/auth/signupcheckEmailId/{email}` | **public** | — | `data: true` if the email is already registered |
| `POST` | `/auth/users` | JWT (intended admin-only, not enforced) | `{"username","email","password","role": ["ROLE_NAME"]}` | `201`, `data:` new user id. `400` if the email exists or no role was given |
| `GET` | `/auth/get-loggedInUserById/{userId}` | JWT | — | the `User` entity: `id`, `username`, `email`, `isDelete`, `roles` (the password hash is excluded by `@JsonIgnore`) |
| `GET` | `/api/test/{all,user,mod,admin,buyer}` | — | — | demo endpoints, unreachable (no route) |

`/auth/signup` is listed as public in the gateway, but **no such endpoint exists** here.

### Sign-in flow

1. `AuthenticationManager` authenticates with `DaoAuthenticationProvider` + `BCryptPasswordEncoder(12)`. `UserDetailsServiceImpl` looks the user up **by email** (`username` in the request = email).
2. Deactivated users (`isDelete = true`) are rejected with `AccountDeactivatedException`.
3. `AuthorizationClient` → authorization-service `POST /authorization/privilege/privileges` with the role names. If that call fails or returns no privileges, sign-in fails with `AuthorizationUnavailableException`, so no token is issued without privileges.
4. `JwtUtils.generateToken` signs an HS256 token with claims `sub`, `userId`, `email`, `username`, `roles`, `privileges`, `iss=mycrm-auth`, `jti`, valid for `jwtExpirationMs` (15 min).

Error mapping today: wrong credentials → `401` (`AuthEntryPointJwt`). Deactivated user or authorization-service down → `500`, because there's no exception handler yet.

```bash
curl -X POST http://localhost:8556/auth/signin -H "Content-Type: application/json" \
     -d '{"username":"jane@acme.com","password":"Secret123"}'
```

## Data model

| Entity → table | Fields |
|---|---|
| `User` → `users` | `id`, `username`, `email` (login key), `password` (bcrypt), `isDelete`, `roles` (`Set<String>`, role names) |
| `Role` → `roles` | `id`, `name` (`ERole` enum, legacy, not used by the current sign-in) |

User ids are shared with employee-service: an employee's `employeeId` **is** its login user's id.

## Calls and callers

| Direction | Service | Endpoint |
|---|---|---|
| calls → | authorization-service | `POST /authorization/privilege/privileges` (Feign, `SyncCall/AuthorizationClient`) |
| ← called by | employee-service | `POST /auth/users` (Feign) |
| ← called by | clients via gateway | `/auth/**` |

Outgoing Feign calls carry `X-Gateway-Secret` (`gatewayauth/GatewaySecretFeignInterceptor`). Incoming requests without it get **403** (`gatewayauth/GatewayAuthFilter`).

## Configuration

| Property | Value |
|---|---|
| `jwtSecret` | `${JWT_SECRET}`: Base64, ≥ 32 bytes, **same as the gateway** |
| `jwtExpirationMs` | `900000` (15 min) |
| `gateway.secret` | `${GATEWAY_SECRET}` |
| `spring.datasource.*` | `jdbc:mysql://localhost:3306/ms_mybiz_authendication`, `root` / `admin` |
| `spring.jpa.hibernate.ddl-auto` | `update` |

Spring Security (`security/WebSecurityConfig.java`) is stateless, has CSRF off, and permits `/auth/**`. Real access control happens at the gateway.

## Code layout

```
com.clinch.crm.authentication
├── controller/   AuthController, TestController (demo)
├── services/     AuthService(+Impl) sign-in logic, UserDetailsServiceImpl, UserDetailsImpl
├── jwt/          JwtUtils (sign/validate), AuthEntryPointJwt (401 entry point)
├── security/     WebSecurityConfig (BCrypt 12, stateless, permit /auth/**)
├── SyncCall/     AuthorizationClient (Feign → authorization-service)
├── gatewayauth/  GatewayAuthFilter, GatewaySecretFeignInterceptor
├── models/       User, Role, ERole, LoginRequestDto, SignupRequestDto
├── response/     ApiResponse, JwtResponse, MessageResponse
├── repository/   UserRepository, RoleRepository
└── common/       AccountDeactivatedException, AuthorizationUnavailableException
```

## Tests

* `models/UserJsonTest`: checks that the password hash never appears in `User` JSON. It's a unit test and needs no infrastructure.
* `AuthenticationServiceApplicationTests.contextLoads` needs MySQL and the env vars.

## Known issues

* 🔴 `POST /auth/users` is admin-only by comment only. No privilege check.
* 🟠 No exception handler, so deactivated users and an unavailable authorization-service produce 500s.
* 🟠 `signupcheckEmailId` returns `201 "User is created"` for a read-only check.
* 🟡 `TestController` demo endpoints should be removed.

See [docs/KNOWN_ISSUES.md](../docs/KNOWN_ISSUES.md).
