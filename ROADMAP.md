# CIght Learner-Owned Roadmap

## Status rules

- A checked ticket has passed behavior, test, explanation, and interview-defence acceptance criteria.
- Existing or reference code does not complete a ticket.
- Only one detailed ticket may be active or under review at a time.
- Detailed ticket specifications live under `tickets/`; this file records dependency order and verified progress.

## Week 1 — Java and Spring API foundations

- [x] **CIGHT-001 — Validate the build-creation HTTP boundary**
- [ ] **CIGHT-002 — Introduce domain types, API records, exceptions, and `ProblemDetail`**
- [ ] **CIGHT-003 — Implement the analytics contract with database aggregation**
- [ ] **CIGHT-004 — Build the unit, MVC-slice, and repository-test foundation**

### Milestone

- [ ] The learner can trace and test a request across the API layers and defend each responsibility.

## Week 2 — PostgreSQL, JPA, and Docker

- [ ] **CIGHT-005 — Own the schema with Flyway, UUIDs, UTC timestamps, constraints, and indexes**
- [ ] **CIGHT-006 — Add bounded filtering and pagination and inspect query plans**
- [ ] **CIGHT-007 — Build a multi-stage, non-root Docker image**
- [ ] **CIGHT-008 — Run PostgreSQL with Compose and integration tests with Testcontainers**

### Milestone

- [ ] A clean database migrates successfully and the learner can defend persistence and container choices.

## Week 3 — Distributed ingestion and Kafka

- [ ] **CIGHT-009 — Model the real GitHub `workflow_run` webhook contract**
- [ ] **CIGHT-010 — Verify HMAC signatures and enforce delivery idempotency**
- [ ] **CIGHT-011 — Persist builds and analysis requests through a transactional outbox**
- [ ] **CIGHT-012 — Publish and consume Kafka events with retry, idempotency, and dead-letter handling**

### Milestone

- [ ] A duplicate signed failure produces one build and replay-safe asynchronous work.

## Week 4 — Baseline AI and RAG

- [ ] **CIGHT-013 — Model asynchronous analysis and introduce a fake baseline engine**
- [ ] **CIGHT-014 — Add safe Gemini structured analysis**
- [ ] **CIGHT-015 — Add PGvector and asynchronous failure-knowledge indexing**
- [ ] **CIGHT-016 — Add repository-filtered RAG, evidence citations, and evaluation**

### Milestone

- [ ] A failed build can produce baseline and grounded RAG results without live model calls in tests.

## Week 5 — Agentic AI and MCP

- [ ] **CIGHT-017 — Build bounded relational, semantic, commit, and instability tools**
- [ ] **CIGHT-018 — Compare baseline, RAG, and agent-selected tool execution**
- [ ] **CIGHT-019 — Expose application services through read-only MCP tools**
- [ ] **CIGHT-020 — Connect MCP Inspector and Codex and verify the tool flow**

### Milestone

- [ ] The learner can distinguish and demonstrate RAG, agent tool use, and MCP boundaries.

## Week 6 — Security, caching, delivery, and interview defence

- [ ] **CIGHT-021 — Secure APIs with JWT validation and scopes**
- [ ] **CIGHT-022 — Cache analytics in Valkey with explicit invalidation behavior**
- [ ] **CIGHT-023 — Build deterministic CI and design controlled image promotion**
- [ ] **CIGHT-024 — Complete observability, failure injection, portfolio handoff, and system-design defence**

### Milestone

- [ ] The end-to-end local demonstration works and the learner can defend the architecture without reading a solution.
