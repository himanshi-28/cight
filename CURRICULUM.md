# CIght Six-Week Curriculum

## Purpose

This curriculum defines the learning dependency order. `ROADMAP.md` tracks project tickets, while `learning-records/` records only demonstrated understanding.

Each week combines four 90–120-minute implementation tickets with explanation, retrieval practice, debugging, review, and system-design work. Future ticket details may adapt to demonstrated ability, but the competency order remains stable.

## Learning loop

Every ticket follows the same loop:

1. Recall the relevant concept without looking at a solution.
2. Predict where the behavior belongs and what should happen.
3. Make a learner-owned implementation attempt.
4. Run a focused check and debug from evidence.
5. Receive a production-minded review.
6. Defend the design and failure behavior.
7. Record learning only when understanding is demonstrated.

## Week 1 — Java and Spring API foundations

### Outcomes

- Trace request deserialization, validation, controller invocation, service work, repository access, and response serialization.
- Use records, enums, null handling, exceptions, collections, interfaces, and immutability in real application code.
- Separate HTTP contracts, business rules, and persistence models.
- Select focused unit, MVC-slice, and repository tests.

### Design checkpoint

Trace one request end to end and defend which layer owns validation, orchestration, domain rules, queries, transactions, and response formatting.

## Week 2 — PostgreSQL, JPA, and Docker

### Outcomes

- Explain JPA entity identity, lifecycle, transaction boundaries, projections, pagination, and common `equals`/`hashCode` risks.
- Own schema evolution with Flyway and use UUIDs, UTC timestamps, constraints, and purposeful indexes.
- Inspect generated SQL and a PostgreSQL query plan.
- Explain Docker images, containers, layers, networks, ports, volumes, health checks, and Testcontainers.

### Design checkpoint

Defend the schema, indexes, expected data growth, query complexity, transaction boundary, and database failure behavior.

## Week 3 — Distributed ingestion and Kafka

### Outcomes

- Parse the real GitHub `workflow_run` contract and verify webhook authenticity.
- Handle retry and duplicate-delivery races through persistent idempotency.
- Explain and implement the transactional outbox pattern.
- Publish and consume Kafka messages with bounded retries, idempotent processing, and dead-letter handling.

### Design checkpoint

Draw the failure matrix between GitHub, the application, PostgreSQL, the outbox publisher, Kafka, and the consumer. Explain ordering, partitions, backpressure, at-least-once delivery, and why exactly-once claims require precise boundaries.

## Week 4 — Baseline AI and RAG

### Outcomes

- Model asynchronous AI work with explicit lifecycle states and an engine interface.
- Parse structured Gemini output, sanitize inputs, bound context, apply timeouts, and store safe failures.
- Explain embeddings, chunking, vector similarity, metadata filtering, retrieval, grounding, and citations.
- Index sanitized failure knowledge in PGvector and evaluate repository-filtered retrieval deterministically.

### RAG target

- PostgreSQL with PGvector, not a separate vector database.
- Spring AI PGvector and Google GenAI embedding integrations.
- `text-embedding-004` with 768 dimensions.
- Flyway-owned vector schema; destructive automatic schema initialization disabled.
- Idempotent chunks keyed by source analysis and chunk index.
- Metadata for repository, build ID, analysis ID, creation time, source type, and chunk index.
- Configurable retrieval with `topK=5`; similarity threshold chosen from evaluation evidence.
- Sanitization before embedding and source IDs on generated answers.

### Design checkpoint

Defend the ingestion consistency boundary, re-embedding strategy, retrieval-quality measurement, empty-context behavior, and hallucination limits.

## Week 5 — Agentic AI and MCP

### Outcomes

- Build bounded tools for relational history, semantic history, commit context, and repository instability.
- Compare deterministic baseline analysis, automatic RAG, and model-selected tool execution.
- Keep tool and MCP boundaries read-only, sanitized, bounded, auditable, and separated from services.
- Connect MCP Inspector and Codex to the local server.

### Runtime contracts

- `AnalysisMode`: `BASELINE`, `RAG`, and `AGENTIC`.
- Analysis responses expose retrieval sources and tools used.
- A shared read-only `FailureKnowledgeSearchService` backs semantic retrieval.
- The agent may call semantic failure search.
- MCP exposes `search_failure_knowledge` without mutation or raw evidence leakage.

### Design checkpoint

Distinguish workflows, RAG, agents, tools, MCP hosts, clients, and servers. Defend when model-controlled decisions add enough value to justify their cost and nondeterminism.

## Week 6 — Security, caching, delivery, and interview defence

### Outcomes

- Validate JWT signature, issuer, audience, expiry, and scopes.
- Explain cache-aside behavior, TTL, invalidation, stale data, and cache failure.
- Build deterministic GitHub Actions for tests, packaging, and Docker image creation.
- Explain artifacts, image promotion, secrets, deployment gates, rollback, CI, continuous delivery, and continuous deployment.
- Operate the local system, inject failures, inspect telemetry, and present the architecture.

### Final defence

Trace and defend:

```text
GitHub failure
→ signed/idempotent webhook
→ PostgreSQL + outbox
→ Kafka
→ baseline/RAG/agentic analysis
→ PGvector evidence
→ stored result
→ REST
→ MCP
→ Codex
```

The final interview covers requirements, rough capacity, API contracts, schema, consistency, scaling, bottlenecks, failures, security, observability, and tradeoffs.

