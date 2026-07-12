# CIght Four-Week Roadmap

## Purpose

Deliver a portfolio-ready CI/CD observability backend within four weeks at approximately 12–15 focused hours per week.

This roadmap tracks verified project implementation. It does not claim what the user understands; demonstrated learning belongs in `learning-records/`.

## How to use this roadmap

Work in dependency order and complete one small learner-owned ticket at a time.

Mark a ticket complete only after its acceptance criteria have been verified. Existing code may reduce the implementation work, but it does not establish learner understanding. When code already exists, use review, testing, debugging, tracing, or explanation work to build and verify understanding.

Agentic AI and MCP are required outcomes. Frontend work, microservice extraction, and optional polish are outside this four-week critical path.

## Week 1 — Reliable API and database foundation

- [ ] **CIGHT-003 — Analytics**
  - Dependencies: existing build persistence
  - Deliver `GET /api/analytics` with repository filtering, database-side aggregation, correct success-rate semantics, and nullable undefined metrics.

- [ ] **CIGHT-004 — Validation and error contracts**
  - Dependencies: CIGHT-003 API conventions
  - Add request and response DTOs, validation, `ProblemDetail` errors, and consistent not-found, conflict, and malformed-payload handling.

- [ ] **CIGHT-005 — Domain and schema hardening**
  - Dependencies: CIGHT-004 contracts
  - Add Flyway-owned PostgreSQL schema, UUID identifiers, UTC `Instant` timestamps, build statuses, indexes, and package-by-feature boundaries.

- [ ] **CIGHT-006 — Tests, pagination, and API documentation**
  - Dependencies: CIGHT-003 through CIGHT-005
  - Add unit, repository, and controller tests; OpenAPI documentation; and build-list pagination capped at 100 records per page.

### Week 1 milestone

- [ ] A clean PostgreSQL database migrates successfully.
- [ ] Analytics edge cases, validation errors, and pagination are tested.
- [ ] The API is documented and exposes no credentials.

## Week 2 — Secure ingestion and event-driven processing

- [ ] **CIGHT-007 — Correct GitHub workflow ingestion**
  - Dependencies: CIGHT-004 and CIGHT-005
  - Verify webhook signatures, handle `ping`, reject duplicate delivery IDs, map completed `workflow_run` payloads, and request analysis only for failures.

- [ ] **CIGHT-008 — Local infrastructure**
  - Dependencies: Week 1 database contract
  - Provide Docker Compose services for PostgreSQL, Kafka in KRaft mode, and Valkey, with separate local, test, and cloud configuration.

- [ ] **CIGHT-009 — Transactional outbox and Kafka**
  - Dependencies: CIGHT-007 and CIGHT-008
  - Persist build and outbox data atomically, publish failure events with at-least-once delivery, consume idempotently, retry twice, and route terminal failures to a dead-letter topic.

### Week 2 milestone

- [ ] A signed GitHub failure creates one build despite duplicate webhook delivery.
- [ ] The failure reaches Kafka and can be replayed without duplicate analysis work.
- [ ] Retry and dead-letter behavior is tested and observable.

## Week 3 — Baseline and agentic AI

- [ ] **CIGHT-010 — Analysis model and baseline Gemini**
  - Dependencies: CIGHT-005 and CIGHT-009
  - Persist analysis mode, lifecycle status, structured findings, model metadata, attempts, latency, and sanitized failures.

- [ ] **CIGHT-011 — Safe agent tools**
  - Dependencies: CIGHT-010
  - Add bounded read-only tools for past failures, commit context, and repository instability; sanitize inputs and outputs; and enforce GitHub timeouts.

- [ ] **CIGHT-012 — Async baseline and agentic analysis APIs**
  - Dependencies: CIGHT-009 through CIGHT-011
  - Automatically request agentic analysis for failures and expose create, list, status, comparison, and idempotent retry APIs.

### Week 3 milestone

- [ ] One failed build can produce both baseline and agentic Gemini analyses.
- [ ] The stored result shows which tools the agent selected.
- [ ] Model orchestration and tool limits are tested without live Gemini or GitHub calls.

## Week 4 — MCP, security, caching, CI, and delivery

- [ ] **CIGHT-013 — Local MCP server**
  - Dependencies: CIGHT-003 and CIGHT-012
  - Expose local read-only MCP tools for builds, recent failures, analytics, and failure analysis by delegating to application services.

- [ ] **CIGHT-014 — Cache and JWT security**
  - Dependencies: CIGHT-004, CIGHT-008, and CIGHT-012
  - Cache analytics in Valkey with invalidation and secure APIs with Auth0 JWT issuer, audience, expiry, and scope validation.

- [ ] **CIGHT-015 — CI and deployment**
  - Dependencies: CIGHT-006, CIGHT-009, and CIGHT-014
  - Run deterministic unit and integration tests, package the service, build its Docker image, and document or verify the free-cloud deployment path without live AI calls in CI.

- [ ] **CIGHT-016 — Portfolio handoff**
  - Dependencies: CIGHT-013 through CIGHT-015
  - Complete architecture, setup, security, delivery-semantics, agentic comparison, MCP, deployment, limitations, and scripted-demo documentation.

### Week 4 milestone

- [ ] `sh ./mvnw test` passes.
- [ ] Docker Compose starts the complete local system.
- [ ] A real GitHub workflow failure completes the Kafka and agentic-analysis path.
- [ ] Baseline and agentic results can be compared.
- [ ] Codex can call CIght through local read-only MCP.
- [ ] JWT authorization, cache behavior, CI, and deployment or wake-up instructions are verified.
- [ ] No secrets are committed.

## Final completion criteria

- [ ] The end-to-end demonstration traces GitHub failure → signed webhook → PostgreSQL and outbox → Kafka → agentic tools → stored analysis → REST → MCP.
- [ ] The user can explain idempotency, outbox delivery, Kafka retries, tool calling, MCP, JWT validation, caching, and modular-monolith tradeoffs without reading notes.
- [ ] Known free-tier, cold-start, security, and production limitations are documented honestly.
