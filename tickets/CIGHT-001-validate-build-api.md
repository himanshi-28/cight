---
id: CIGHT-001
title: Validate the build-creation HTTP boundary
status: active
week: 1
roadmap: First Java and Spring API foundation ticket
timebox: 90–120 minutes
---

# CIGHT-001 — Validate the build-creation HTTP boundary

## Why this ticket is next

CIght already accepts `POST /api/builds`, but malformed input can reach the service and persistence layer. This is the smallest useful ticket for learning the Spring MVC request boundary before changing domain types, database schema, or infrastructure.

## Learning objective

Explain and enforce Bean Validation at the Spring MVC boundary so invalid JSON is rejected before `BuildEventService` is invoked.

## Prediction checkpoint

Before changing code, predict the execution order for a valid request and for a request whose `repoName` is blank:

```text
JSON deserialization
Bean Validation
controller method
service method
repository call
```

State where the invalid request should stop and why. Do not inspect the reference implementation before answering.

## In scope

- Add Bean Validation constraints to `BuildEventRequest`:
  - `repoName`: nonblank and at most 255 characters;
  - `branch`: nonblank and at most 255 characters;
  - `status`: nonblank;
  - `duration`: null or non-negative.
- Trigger request validation from `BuildController`.
- Add an MVC-slice `BuildControllerTest`.
- Keep the production change limited to the HTTP boundary.

## Out of scope

- A `BuildStatus` enum.
- Request or response records.
- A global exception handler or custom error payload.
- Entity, repository, schema, PostgreSQL, or Docker changes.
- Webhook changes.
- Copying validation or tests from `codex/reference-generated-v1`.

## Acceptance criteria

### Behavior

- A valid build request reaches the service and returns HTTP `201 Created`.
- Blank repository, blank branch, blank status, and negative duration each return HTTP `400 Bad Request`.
- Invalid requests do not invoke `BuildEventService`.

### Tests

- Use an MVC-slice rather than a full application context.
- Include one valid-request test.
- Include invalid cases for repository, branch, status, and duration.
- Verify the service interaction for valid and invalid cases.
- The focused Maven command passes.

### Explanation

- Trace deserialization, validation, controller, service, and repository order.
- Explain why HTTP validation belongs at the boundary.
- Explain why service or domain invariants may still need separate enforcement.
- Explain the difference between `@NotNull`, `@NotEmpty`, and `@NotBlank`.
- Explain what an MVC-slice test loads and excludes.

## Relevant files

- `src/main/java/com/cight/dto/BuildEventRequest.java`
- `src/main/java/com/cight/controller/BuildController.java`
- `src/test/java/com/cight/controller/BuildControllerTest.java` — create this file.

## Primary references

- [Spring MVC validation](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-validation.html)
- [Spring Boot testing](https://docs.spring.io/spring-boot/how-to/testing.html)
- [Jakarta Bean Validation constraints](https://jakarta.ee/specifications/bean-validation/)

## Verification

Run:

```sh
sh ./mvnw -Dtest=BuildControllerTest test
```

Do not use the full test suite as a substitute for understanding the focused test.

## Progressive hints

### Hint 1 — Concept

Validation metadata belongs on the request model, and the controller must opt into validating that method parameter.

### Hint 2 — Layer or file

Start in `BuildEventRequest` and then inspect the `@RequestBody` parameter in `BuildController`.

### Hint 3 — Pseudocode

Available only after the learner shares an attempt or a concrete blocker.

### Hint 4 — Partial code

Available only after the pseudocode-level hint is insufficient.

### Hint 5 — Complete solution after an attempt

Available only after a meaningful implementation attempt and review. The generated reference branch may then be inspected read-only if comparison is educational.

## PR review checklist

- Constraints match the stated HTTP contract without adding unrelated rules.
- Validation is activated on the request body.
- Tests assert observable HTTP behavior and service interaction.
- The test slice does not require PostgreSQL.
- No entity, schema, webhook, or dependency changes slipped into the diff.

## Debugging scenario

If an invalid request still returns `201`, identify whether the constraint metadata is missing, validation was not activated on the controller parameter, or the test is bypassing Spring MVC by calling the method directly.

## Interview defence

Answer one question at a time during review:

1. Why is `@NotBlank` more appropriate than `@NotNull` for `repoName`?
2. What happens between MockMvc sending JSON and the controller method running?
3. Why should invalid input be rejected before repository access?
4. If another caller invokes the service directly, what protection does controller validation provide?
5. What does an MVC-slice test deliberately avoid loading?

## Learning-record evidence

Do not create a learning record merely because the code passes. Evidence requires the focused test result, a reviewed diff, a correct request-flow explanation, and successful interview defence.

