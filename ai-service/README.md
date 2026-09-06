# ai-service

AI integration microservice for MyCrm. First milestone: summarize a lead's
activity history using Claude.

## What this demonstrates (enterprise AI integration patterns)

- **Isolated AI boundary** — all outbound calls to the AI provider go through
  one client (`ClaudeClient`), so retries, timeouts, auth, and logging live
  in a single place instead of being scattered across services.
- **Config-driven, not hardcoded** — model name, token limits, and API key
  are externalized in `application.yml` / env vars, so you can swap models
  or providers without touching code.
- **Resilience** — `resilience4j` retry wraps the Claude call for transient
  failures (rate limits, timeouts).
- **Clean error boundary** — `GlobalExceptionHandler` turns AI-provider
  failures into a consistent JSON error shape instead of leaking raw
  WebClient exceptions to callers.
- **Room to grow into RAG** — `postgresql` + `spring-boot-starter-data-jpa`
  are already wired up so you can add `pgvector`, an embeddings table, and
  a retrieval step ahead of the Claude call once summarization works.

## Setup

1. Set your API key:
   ```bash
   export ANTHROPIC_API_KEY=sk-ant-...
   ```
2. Point `spring.datasource` at your existing MyCrm Postgres instance (or
   leave defaults if running locally with matching credentials).
3. Run it:
   ```bash
   ./mvnw spring-boot:run
   ```
   (or drop this module into your MyCrm monorepo's parent POM as another
   `<module>` and build from the root)

## Try it

```bash
curl -X POST http://localhost:8085/ai/summarize-lead \
  -H "Content-Type: application/json" \
  -d '{
    "leadId": 42,
    "leadName": "Acme Corp",
    "activities": [
      {"type": "CALL", "note": "Discussed pricing, asked about enterprise tier", "timestamp": "2026-07-20T10:00:00Z"},
      {"type": "EMAIL", "note": "Sent follow-up with case study", "timestamp": "2026-07-21T09:00:00Z"}
    ]
  }'
```

## Next steps (once this is working end-to-end)

1. Add a `lead_embedding` table (`vector` column via `pgvector`) and a job
   that embeds new/updated lead records.
2. Add `POST /ai/search` that embeds the query, does a cosine-similarity
   lookup against `lead_embedding`, and feeds the top matches into the
   Claude prompt as context — this is the RAG step.
3. Have `lead-service` (or wherever leads live today) call this service
   asynchronously (event/queue) instead of synchronously, so lead writes
   aren't blocked on AI latency.
