# MyCrm — CRM Backend Platform

A **CRM (Customer Relationship Management)** backend built as Java / Spring Boot microservices. Each business domain (leads, customers, accounts, opportunities, inventory, sales orders, employees, …) is its own Spring Boot application with its own database, registered in **Eureka** and exposed to clients only through a **Spring Cloud Gateway** that authenticates every request with a **JWT**.

```
Client ──► gateway-service :8556 ──(lb://<name>, via Eureka :8555)──► business service ──► its own DB
              │  JWT check, identity headers,
              │  X-Gateway-Secret
              ▼
        authentication-service (issues JWTs) ──Feign──► authorization-service (roles → privileges)
```

| Document | What's in it |
|---|---|
| [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) | Components, request flow, service map, inter-service calls, data stores |
| [docs/SECURITY.md](docs/SECURITY.md) | JWT, gateway filter, identity headers, gateway secret, RBAC, secrets |
| [docs/DEVELOPMENT.md](docs/DEVELOPMENT.md) | Prerequisites, env vars, build/test/run, conventions, adding a service or endpoint |
| [docs/KNOWN_ISSUES.md](docs/KNOWN_ISSUES.md) | Open problems, prioritised, with where they live |
| `<module>/README.md` | Per-module details — endpoints, config, data model, dependencies, issues |

---

## Modules

**Status key:** ✅ reachable through the gateway · ⚠️ runs, but has a routing/registration problem · 🚧 skeleton or doesn't build · 🧩 infrastructure

| Module                                                     | Status | Port | Eureka name              | Gateway prefix             | Database                        | Stack              | Purpose                                                          |
|------------------------------------------------------------|--------|------|--------------------------|----------------------------|---------------------------------|--------------------|------------------------------------------------------------------|
| [eureka-service](eureka-service/README.md)                 | 🧩     | 8555 | — (server)               | —                          | —                               | Boot 2.7           | Service registry                                                 |
| [gateway-service](gateway-service/README.md)               | 🧩     | 8556 | `api-gateway-service`    | —                          | —                               | Boot 2.7, Gateway  | Single entry point, JWT auth                                     |
| [authentication-service](authentication-service/README.md) | ✅      | 8557 | `authentication-service` | `/auth`                    | MySQL `ms_mybiz_authendication` | Boot 2.7, Security | Sign-in, JWT issuing, login users                                |
| [authorization-service](authorization-service/Readme.md)   | ✅      | 8778 | `authorization-service`  | `/authorization`           | MySQL `ms_mybiz_authorization`  | Boot 2.7           | Roles, privileges, access (RBAC)                                 |
| [employee-service](employee-service/README.md)             | ✅      | 8562 | `employee-service`       | `/employee`                | MySQL `ms_mybiz_employee`       | Boot 2.7, Feign    | Employees, departments, territories                              |
| [customer-service](customer-service/README.md)             | ✅      | 8561 | `customer-service`       | `/customer`                | MySQL `ms_mybiz_customer`       | Boot 2.7           | Customers                                                        |
| [lead-service](lead-service/README.md)                     | ✅      | 8563 | `lead-service`           | `/lead`                    | MySQL `ms_mybiz_lead`           | Boot 2.7           | Leads, CSV import/export                                         |
| [account-service](account-service/README.md)               | ✅      | 8558 | `account-service`        | `/account`                 | MySQL `ms_mybiz_account`        | Boot 2.7           | Accounts, CSV import/export                                      |
| [opportunity-service](opportunity-service/README.md)       | ✅      | 8564 | `user-opportunity`       | `/opportunity`             | MySQL `ms_mybiz_opportunity`    | Boot 2.7           | Sales opportunities                                              |
| [company-service](company-service/README.md)               | ✅      | 8570 | `company-service`        | `/company`                 | MySQL `ms_mybiz_company`        | Boot 2.7           | Company profiles                                                 |
| [inventory-service](inventory-service/README.md)           | ⚠️     | 8777 | `inventory-service`      | `/inventory`               | MySQL `ms_mybiz_inventory`      | Boot 2.7           | Product types, products, price books, suppliers, purchase orders |
| [product-service](product-service/README.md)               | ⚠️     | 8566 | `inventory-service` (!)  | `/inventory/product`       | MySQL `product_service`         | Boot 2.7, RabbitMQ | Product catalogue & stock                                        |
| [salesorder-service](salesorder-service/README.md)         | ⚠️     | 8568 | `salesorder-service`     | `/salesorder`              | MySQL `ms_mybiz_salesorder`     | Boot 2.7           | Sales orders, quotes, invoices                                   |
| [TaskManagement](TaskManagement/README.md)                 | ⚠️     | 8566 | not registered           | (`/task` → `task-service`) | PostgreSQL `taskdb`             | Boot 2.7           | Tasks                                                            |
| [project-service](project-service/README.md)               | 🚧     | 8580 | not registered           | `/project`                 | —                               | Boot **4.0**       | Placeholder                                                      |
| [ai-service](ai-service/README.md)                         | 🚧     | 8085 | not registered           | —                          | —                               | Boot **3.3**       | Claude-powered lead summaries                                    |
| [urlshortner](urlshortner/README.md)                       | 🚧     | 8080 | not registered           | —                          | —                               | Boot **4.0**       | Placeholder                                                      |

The gateway also routes `/campaign/**` → `campaign-service` and `/contact/**` → `contacts-service`; **no module exists for either**, so those prefixes always return 503. See [docs/KNOWN_ISSUES.md](docs/KNOWN_ISSUES.md) for every ⚠️ / 🚧 item.

---

## Tech stack

| Layer | Technology |
|---|---|
| Language | Java 17 (all modules compile with `release=17`) |
| Framework | Spring Boot **2.7.15** for everything under `clinch-parent`; `ai-service` is Boot 3.3.0; `project-service` and `urlshortner` are Boot 4.0.3 |
| Cloud | Spring Cloud 2021.0.x — Netflix Eureka, Spring Cloud Gateway, OpenFeign, LoadBalancer |
| Security | JWT (jjwt 0.11.5, HS256), Spring Security (authentication-service only), shared gateway secret |
| Persistence | Spring Data JPA / Hibernate (`ddl-auto=update`); **MySQL** for most services, **PostgreSQL** for TaskManagement |
| Messaging | RabbitMQ (product-service listener only) |
| API docs | springdoc-openapi 1.7 (Swagger UI at `/swagger-url.html` on services that include it) |
| Build | Maven, one root aggregator POM (`clinch-parent`) |

> There is no Redis in the codebase, and PostgreSQL is used only by TaskManagement — earlier versions of this README said otherwise.

---

## Quick start

Prerequisites: **JDK 17**, **Maven 3.6+**, **MySQL 8** on `localhost:3306` (`root` / `admin`), and — only for TaskManagement / product-service — PostgreSQL and RabbitMQ. Full details in [docs/DEVELOPMENT.md](docs/DEVELOPMENT.md).

**1. Create the two shared secrets** (same values for every service) in a git-ignored `local-secrets.properties` at the repo root. Every service imports it. In PowerShell, from the repo root:

```powershell
$r=[Security.Cryptography.RandomNumberGenerator]::Create(); $j=New-Object byte[] 64; $r.GetBytes($j); $g=New-Object byte[] 32; $r.GetBytes($g)
"JWT_SECRET=$([Convert]::ToBase64String($j))`nGATEWAY_SECRET=$([Convert]::ToBase64String($g))" | Set-Content -Encoding ascii local-secrets.properties
```

No restart is needed. Services refuse to start if either secret is missing. On servers, set `JWT_SECRET` / `GATEWAY_SECRET` as environment variables instead; they override the file. Details: [docs/DEVELOPMENT.md](docs/DEVELOPMENT.md#2-environment-variables). **Never commit this file, or put the values in `application.properties`.**

**2. Build** everything from the repo root:

```bash
mvn clean install -DskipTests
```

**3. Start in order** — each in its own terminal:

```bash
cd eureka-service         && mvn spring-boot:run   # 1. registry   → http://localhost:8555
cd gateway-service        && mvn spring-boot:run   # 2. gateway    → http://localhost:8556
cd authorization-service  && mvn spring-boot:run   # 3. needed by sign-in
cd authentication-service && mvn spring-boot:run   # 4. sign-in
cd employee-service       && mvn spring-boot:run   # 5+. any business services you need
```

Databases are created on first start (`ddl-auto=update`) as long as the MySQL schema exists — create the `ms_mybiz_*` schemas listed above first.

**4. Sign in and call a service through the gateway:**

```bash
curl -X POST http://localhost:8556/auth/signin \
  -H "Content-Type: application/json" \
  -d '{"username":"admin@example.com","password":"<password>"}'   # "username" must be the user's EMAIL
# → { "success": true, "data": { "accessToken": "eyJ…", "tokenType": "Bearer", "id": 1, "roles": [...], ... } }

curl http://localhost:8556/employee/list-employee -H "Authorization: Bearer eyJ…"
```

Always call services **through the gateway (`:8556`)**. Calling a service's own port directly returns `403` — see [docs/SECURITY.md](docs/SECURITY.md).

---

## Repository layout

```
MyCrm/
├── pom.xml                  # clinch-parent: aggregator + Spring Boot 2.7.15 parent + java.version=17
├── docs/                    # project-level documentation
├── eureka-service/          # infrastructure
├── gateway-service/
├── authentication-service/  # security
├── authorization-service/
├── employee-service/        # business services (one DB each)
├── customer-service/  lead-service/  account-service/  opportunity-service/
├── company-service/   inventory-service/  product-service/  salesorder-service/
├── TaskManagement/
├── ai-service/              # standalone, Boot 3.3, not in the root <modules>
├── project-service/         # standalone, Boot 4.0, not in the root <modules>
└── urlshortner/             # Boot 4.0 (listed in <modules> but has its own Boot parent)
```

---

## Branch strategy (Git Flow)

```
main        → production-ready code
develop     → integration branch
feature/*   → individual feature development
hotfix/*    → emergency production fixes
```

---

## Author

**Vasanth S** — Java Backend Developer · [GitHub](https://github.com/vasanth1999developer)
