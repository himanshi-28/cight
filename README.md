# CIght

CIght is a learner-owned Java 21 Spring Boot project for becoming interview-ready in backend engineering, distributed systems, Docker, CI/CD, RAG, agentic AI, and MCP.

## Current baseline

The `codex/learning` branch starts from the original small application at commit `f58fad9`. It currently contains:

- basic build-event REST endpoints;
- a PostgreSQL JPA entity and repository;
- an early webhook payload and service;
- one Spring context test.

This baseline is intentionally incomplete. The learner will harden it one reviewed ticket at a time.

The previous Codex-generated full implementation is preserved locally on `codex/reference-generated-v1`. It is reference material, not learner-authored or portfolio-defensible work, and must not be copied before a learner attempt.

## Target architecture

```text
GitHub workflow failure
→ signed and idempotent webhook
→ PostgreSQL + transactional outbox
→ Kafka
→ baseline, RAG, or agentic analysis
→ PGvector evidence
→ REST API
→ read-only MCP
→ Codex
```

CIght remains a modular monolith during the six-week path. Package boundaries may suggest future services, but no microservice extraction is planned.

## Learning workflow

- [Mission](MISSION.md)
- [Curriculum](CURRICULUM.md)
- [Roadmap](ROADMAP.md)
- [Current teaching notes](NOTES.md)
- [Ticket format and lifecycle](tickets/README.md)
- [Active ticket](tickets/CIGHT-001-validate-build-api.md)
- [Trusted resources](RESOURCES.md)

Only demonstrated understanding is recorded under `learning-records/`.

## Tooling baseline

- Java 21
- Spring Boot 4
- Maven Wrapper: `sh ./mvnw`
- PostgreSQL

Docker, Kafka, PGvector, Valkey, Spring AI, MCP, security, and CI/CD are introduced by later tickets rather than added upfront.

## Safety

Never commit `.env`, credentials, API keys, raw prompts, private tool responses, or unsanitized logs. Live external calls are manual demonstrations; tests and CI remain deterministic.

