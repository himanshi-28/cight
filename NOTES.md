# Teaching Notes

- The learner has two years of software experience but limited backend production exposure.
- Target Java/Spring Boot roles at the two-to-three-year interview level.
- Six-week plan at approximately 12–15 focused hours per week.
- Use one learner-owned 90–120-minute ticket at a time.
- Keep Core Java practice inside CIght rather than assigning a separate DSA track.
- Explain the purpose and relevant concept, ask for a prediction, let the learner attempt, then review and defend.
- Do not treat Codex-generated reference code as learned or portfolio-defensible work.
- Treat RAG, agentic AI, and MCP as connected differentiators after the backend and distributed-system foundations.
- Current active ticket: `tickets/CIGHT-002-domain-api-errors.md`.
- Baseline verification on 2026-07-12: `sh ./mvnw test` compiles the project but the original `CightApplicationTests` fails because the full context attempts to connect to PostgreSQL at `localhost:5432` and no database is running. Preserve this as known baseline evidence; CIGHT-001 uses a database-independent MVC slice, and later persistence/test-infrastructure tickets will make the full suite deterministic.
