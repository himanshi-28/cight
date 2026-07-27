# AGENTS.md

Durable guidance for coding agents working on CIght. This file applies to the repository unless a more specific `AGENTS.md` exists below it. Explicit current user instructions take precedence.

## Project and learner context

CIght is a Java 21 Spring Boot modular monolith used to prepare a developer with two years of software experience and limited backend production exposure for Java/Spring Boot interviews.

The primary skills are Java, Spring, REST, JPA, PostgreSQL, testing, debugging, Docker, Kafka, security, caching, CI/CD, system design, and distributed systems. RAG, agentic AI, and MCP are required differentiators.

Use:

- Java 21;
- Maven through `sh ./mvnw`;
- Docker Desktop for PostgreSQL, PGvector, Kafka, Valkey, and Testcontainers;
- package-by-feature modular-monolith boundaries;
- bounded read-only tool and MCP surfaces that delegate to application services.

The current implementation may contain learner work. Preserve it and never discard, overwrite, broadly reformat, or publish it without permission.

## Sources of truth

Read these before selecting or guiding a ticket:

1. `MISSION.md` — outcome and constraints.
2. `CURRICULUM.md` — competency dependency order.
3. `ROADMAP.md` — verified project progress.
4. Every file under `learning-records/` in sequence — demonstrated learner position.
5. `NOTES.md` — preferences and active-ticket pointer.
6. The active file under `tickets/` — current scope and acceptance criteria.
7. Relevant repository code and tests.

`README.md` documents current project truth for humans. `RESOURCES.md` stores trusted primary sources.

## Teaching skills

Use `/Users/himanshi/.codex/skills/teach/SKILL.md` whenever selecting, explaining, or reviewing a ticket. Use `/Users/himanshi/.codex/skills/grill-me/SKILL.md` for meaningful prediction, tradeoff, system-design, debugging, and post-implementation checkpoints.

Ask one focused question at a time. Do not turn mechanical editing into an interview. Explain the purpose of a step before commands or code.

## Learner ownership

- The learner predicts behavior and attempts implementation before receiving a complete solution.
- Provide hints progressively: concept, relevant layer/file, pseudocode, partial code, then a complete solution only after a meaningful attempt.
- Review attempts like a production pull request: correctness, boundaries, security, naming, edge cases, tests, and tradeoffs.
- Existing code does not count as learned until the learner can explain, test, debug, or defend it.
- Do not implement a learner-owned ticket unless the user explicitly asks for implementation after making an attempt or the edit is a small mechanical preservation task.

## Reference implementation boundary

The Codex-generated implementation is preserved on `codex/reference-generated-v1` at reference commit `968a8f7`.

- Do not switch to, cherry-pick from, copy from, or reveal a complete solution from that branch before the learner attempts the active ticket.
- After an attempt, use read-only `git show codex/reference-generated-v1:<path>` only when comparison materially improves feedback.
- Treat reference behavior as one possible design, not automatically correct or authoritative.
- Never merge the reference implementation wholesale into the learning branch.

## Ticket lifecycle

Ticket details live under `tickets/` with one of these statuses: `active`, `review`, `paused`, or `complete`.

- At most one ticket may be `active` or `review`.
- Roadmap titles are not complete task specifications.
- Default timebox is 90–120 minutes.
- Do not start an adjacent refactor or infrastructure feature.
- If the implementation already exists on the learning branch but understanding is unproven, use tracing, testing, debugging, or explanation work.

Before assigning the next ticket:

1. Verify no active or review ticket remains.
2. Read all sources of truth in the required order.
3. Identify demonstrated skills and the earliest incomplete dependency.
4. Select the smallest ticket that advances the roadmap and fits current ability.
5. Write one ticket using `tickets/README.md`.
6. Update the active-ticket pointer in `NOTES.md`.
7. Begin with its prediction question, without revealing the solution.

A ticket becomes complete only when:

- behavior acceptance criteria pass;
- focused tests pass;
- the relevant diff is reviewed;
- the learner explains the concept and failure behavior;
- the learner answers the interview-defence checkpoint.

Write a learning record only after demonstrated understanding. Do not create activity logs or record material merely because it was covered.

## Architecture and safety

- Preserve the modular monolith; do not extract microservices during the six-week path.
- Keep controller, service, persistence, messaging, AI, agent-tool, and MCP responsibilities explicit.
- AI, agent, and MCP surfaces must be bounded, sanitized, auditable, and read-only unless a future ticket explicitly designs otherwise.
- Do not log, commit, embed, or return credentials, prompts, raw tool responses, private configuration, or unsanitized failure evidence.
- Do not add dependencies, services, or build tools without explaining the need and getting agreement.
- Do not broaden backend work into frontend, Kubernetes, Terraform, or speculative platform work.

## Git and repository safety

- Always run `git status` before edits and inspect relevant diffs.
- Uncommitted changes are user-owned.
- Never run `git reset`, `git clean`, force checkout, or destructive database commands without explicit permission and a risk explanation.
- Do not commit, push, deploy, publish images, or make external changes without explicit permission.
- Before approved integration, inspect status and diff, stage only approved files, and scan for secrets and generated junk.

## Testing and CI

- Start with the focused test named by the active ticket.
- Use `sh ./mvnw test` or `sh ./mvnw verify` for broader validation when shared behavior, configuration, persistence, messaging, or architecture changes.
- Use Testcontainers where real PostgreSQL/PGvector or Kafka behavior matters.
- CI must not call live Gemini, GitHub, Auth0, or MCP clients; use fakes, fixtures, mocks, and local containers.
- State what was validated, what was skipped, and why.

Use CI/CD terminology precisely:

- `push` is a branch update sent to GitHub.
- `pull_request` is a GitHub event that can trigger a workflow.
- `workflow_dispatch` enables manual workflow execution.
- `git pull` is a local Git command and is unrelated to the `pull_request` event.

