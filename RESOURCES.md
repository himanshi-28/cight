# Spring Boot Backend Engineering Resources

## Knowledge

- [Spring Boot Reference Documentation](https://docs.spring.io/spring-boot/index.html)
  Primary reference for configuration, application structure, testing, production features, and supported integrations. Use when CIght behavior depends on Spring Boot rather than plain Spring.
- [Spring Data JPA: Query Methods](https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html)
  Official guide to derived queries and `@Query`. Use when deciding whether a repository operation should be inferred from a method name or expressed as JPQL.
- [Spring Data JPA: Repository Query Keywords](https://docs.spring.io/spring-data/jpa/reference/repositories/query-keywords-reference.html)
  Authoritative keyword list, including count projections. Use before inventing or guessing a derived repository method.
- [PostgreSQL: Aggregate Functions](https://www.postgresql.org/docs/current/functions-aggregate.html)
  Defines `count`, `avg`, null handling, and aggregate return types. Use for CIGHT-003 and future analytics work.
- [Spring Boot: Testing](https://docs.spring.io/spring-boot/how-to/testing.html)
  Official testing techniques and test slices. Use when choosing between unit, MVC-slice, JPA-slice, and full-context tests.
- [GitHub Actions: Understanding GitHub Actions](https://docs.github.com/en/actions/get-started/understanding-github-actions)
  Primary introduction to CI/CD workflows, events, jobs, runners, steps, and actions. Use before editing CIght's workflow.
- [GitHub Actions: Workflow syntax](https://docs.github.com/en/actions/reference/workflows-and-actions/workflow-syntax)
  Authoritative YAML syntax reference for triggers, jobs, steps, dependencies, and execution settings.
- [Model Context Protocol Specification](https://modelcontextprotocol.io/specification/)
  Primary protocol reference. Use when CIght exposes stable backend capabilities as MCP tools and resources.
- [Spring AI: Model Context Protocol](https://docs.spring.io/spring-ai/reference/api/mcp/mcp-overview.html)
  Official Spring integration guide for MCP clients and servers. Use during CIght's AI integration phase.

## Wisdom (Communities)

- [Spring: Stack Overflow guidance](https://spring.io/questions)
  Spring's official help page routes technical questions to the established Stack Overflow community. Use for minimal reproducible questions after consulting documentation.
- [PostgreSQL Community](https://www.postgresql.org/community/)
  Official directory of PostgreSQL mailing lists and user groups. Use for database behavior and operational questions that require practitioner experience.

## Gaps

- Add a high-quality Java performance resource when CIght reaches profiling and optimization.
- Add deployment-provider operational references when a hosting target is selected.
