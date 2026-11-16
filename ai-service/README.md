# ai-service

An AI integration microservice for MyCrm. Its first milestone is to **summarise a lead's activity history with Claude** (Anthropic Messages API).

> 🚧 **It doesn't currently build.** The module contains two parallel implementations, and the unused one doesn't compile (details below). It's also standalone: it's **not** in the root `<modules>`, has no Eureka client, no gateway route and no gateway-secret filter.

## At a glance

| | |
|---|---|
| Port | **8085** |
| Eureka name | — (not registered) |
| Gateway prefix | — (no route; call it directly) |
| Database | none. `spring.datasource` is configured, but no JPA or JDBC dependency is present, so it's ignored. |
| Stack | **Spring Boot 3.3.0** (`jakarta.*`) · WebFlux `WebClient` · resilience4j retry (+ AOP) · Lombok |
| Base package | `com.mycrm.aiservice` |
| Needs | `ANTHROPIC_API_KEY` |

## Run

```bash
export ANTHROPIC_API_KEY=sk-ant-...
cd ai-service && mvn spring-boot:run     # no mvnw in this module
```

## API

| Method | Path | Body | Returns |
|---|---|---|---|
| `POST` | `/ai/summarize-lead` | `SummarizeLeadRequest` | `SummarizeLeadResponse {leadId, summary, model}` |

```bash
curl -X POST http://localhost:8085/ai/summarize-lead \
  -H "Content-Type: application/json" \
  -d '{
    "leadId": 42,
    "leadName": "Acme Corp",
    "activities": [
      {"type": "CALL",  "note": "Discussed pricing, asked about enterprise tier", "timestamp": "2026-07-20T10:00:00Z"},
      {"type": "EMAIL", "note": "Sent follow-up with case study",               "timestamp": "2026-07-21T09:00:00Z"}
    ]
  }'
```

`leadId` (`@NotNull`) and `leadName` (`@NotBlank`) are validated. `activities` is a list of `{type, note, timestamp}`.

## How it works

The live path is `controller/AiController` → `service/LeadSummarizationService` → `client/ClaudeClient` → Anthropic API.

* **Isolated AI boundary.** All outbound calls go through `ClaudeClient`, so retries, timeouts, auth and logging live in one place.
* **Config-driven.** Model, token limit, timeout and key come from `application.yml` / env vars (`ClaudeProperties`, prefix `claude`), and `WebClientConfig` builds the `WebClient`.
* **Resilience.** `@Retry(name = "claudeApi")` on `ClaudeClient` retries up to 3 times, 500 ms apart, on `429 TooManyRequests` and timeouts. It needs `resilience4j-spring-boot3` **and** `spring-boot-starter-aop`; without AOP the annotation is silently ignored.
* **Clean error boundary.** `exception/GlobalExceptionHandler` turns `AiServiceException` and `IllegalArgumentException` into a consistent JSON error. Raw `WebClientResponseException`s that escape the retry aren't mapped yet.

## Configuration (`application.yml`)

| Key | Value |
|---|---|
| `server.port` | 8085 |
| `claude.api-key` | `${ANTHROPIC_API_KEY:}` |
| `claude.base-url` | `https://api.anthropic.com/v1/messages` |
| `claude.model` | `claude-sonnet-4-6` |
| `claude.max-tokens` / `timeout-seconds` | 1024 / 30 |
| `resilience4j.retry.instances.claudeApi` | `max-attempts: 3`, `wait-duration: 500ms` |
| `management.endpoints.web.exposure.include` | `health,info,metrics` |
| `spring.datasource.*` | Postgres `mycrm`. Has no effect today (no JPA dependency). |

## Why it doesn't build

There's a second, **unused** implementation: `client/AnthropicClient` + `config/AnthropicProperties` (prefix `anthropic`) + `service/LeadSummaryService`. `LeadSummaryService` has two compile errors:

* it calls `request.activityHistory()`, but `SummarizeLeadRequest` has `activities`
* it constructs `new SummarizeLeadResponse(leadId, summary)`, but the record has three components (`leadId, summary, model`)

The simplest fix is to **delete the unused trio** (`AnthropicClient`, `AnthropicProperties`, `LeadSummaryService`), because the controller only uses `LeadSummarizationService`.

## Code layout

```
com.mycrm.aiservice
├── controller/  AiController
├── service/     LeadSummarizationService (live) · LeadSummaryService (unused, broken)
├── client/      ClaudeClient (live), ClaudeMessageRequest/Response · AnthropicClient (unused)
├── config/      ClaudeProperties, WebClientConfig · AnthropicProperties (unused)
├── dto/         SummarizeLeadRequest, SummarizeLeadResponse
└── exception/   AiServiceException, GlobalExceptionHandler
```

## Tests

`LeadSummarizationServiceTest` is a unit test with a mocked `ClaudeClient`. It can't run until the module compiles.

## Integrating with the platform

1. Fix the build (see above).
2. Add `spring-cloud-starter-netflix-eureka-client` with a Spring Cloud release that supports Boot 3.3 (2023.0.x), and register as `ai-service`.
3. Add a `GatewayAuthFilter` (the `jakarta.servlet` variant from project-service) and `gateway.secret=${GATEWAY_SECRET}`.
4. Add a gateway route `/ai/**` → `lb://ai-service` with `AuthenticationFilter`.

## Next steps (product)

1. Add a `lead_embedding` table (`vector` column via `pgvector`, which needs the JPA + PostgreSQL dependencies) and a job that embeds new or updated lead records.
2. Add `POST /ai/search`. It embeds the query, does a cosine-similarity lookup against `lead_embedding`, and feeds the top matches into the Claude prompt as context (RAG).
3. Have lead-service call this service asynchronously (event/queue), so lead writes aren't blocked by AI latency.
