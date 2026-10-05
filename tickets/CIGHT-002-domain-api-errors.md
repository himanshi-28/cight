---
id: CIGHT-002
title: Introduce domain types, API records, exceptions, and ProblemDetail
status: active
week: 1
roadmap: Second Java and Spring API foundation ticket
timebox: 90–120 minutes
---

# CIGHT-002 — Introduce domain types, API records, exceptions, and `ProblemDetail`

## Why this ticket is next

Ticket 1 proved that malformed input can stop at the Spring MVC boundary. The API still accepts arbitrary status strings, exposes the mutable JPA entity as its HTTP response, and has no explicit not-found contract. This ticket introduces the smallest set of Java and Spring types needed to separate the HTTP contract, domain vocabulary, persistence model, and error response.

## Learning objective

Use a Java enum and records to make valid states and API contracts explicit, then translate a service-level not-found exception into a stable RFC 9457 `ProblemDetail` response.

## Prediction checkpoint

Before changing code, predict what should happen after this ticket when a client sends `"status": "BROKEN"` to `POST /api/builds`: which component attempts the string-to-enum conversion, where should processing stop, and should `BuildEventService` be called?

Do not inspect the reference implementation before answering.

## In scope

- Introduce a `BuildStatus` enum containing the states CIght currently produces: `PENDING`, `SUCCESS`, `FAILURE`, and `UNKNOWN`.
- Replace the string status in the build request and entity with `BuildStatus`; persist the enum by name rather than ordinal.
- Convert `BuildEventRequest` from a mutable Lombok DTO to a Java record while preserving ticket 1 validation.
- Add an immutable `BuildEventResponse` record and return it from all build API endpoints instead of returning `BuildEvent`.
- Keep `errorLog` accepted on creation but omit it from `BuildEventResponse`; raw failure evidence is not part of this public summary contract.
- Add `GET /api/builds/{id}` and a service lookup that throws `BuildEventNotFoundException` when the ID does not exist.
- Add centralized exception handling that returns RFC 9457 `ProblemDetail` bodies for:
  - a missing build (`404 Not Found`);
  - unreadable JSON, including an unsupported enum value (`400 Bad Request`).
- Update the existing MVC-slice test for the new contracts and add focused cases for typed status, response mapping, unsupported status, and missing ID.
- Update `WebhookService` only as needed to compile against the typed request contract.

## Out of scope

- Flyway, schema migrations, UUID or timestamp redesign, database constraints, or indexes.
- A separate domain aggregate or a package-structure rewrite.
- Analytics, filtering, pagination, webhook contract redesign, or webhook security.
- Returning validation field maps or designing every future API error.
- Changing dependencies or fixing the full-context PostgreSQL test.
- Copying from `codex/reference-generated-v1` before the learner attempt.

## Acceptance criteria

### Behavior

- `POST /api/builds` accepts one of the four supported status names and returns `201 Created` with a `BuildEventResponse` JSON body.
- Build list, repository list, and ID lookup endpoints return response records; no endpoint serializes the JPA entity directly.
- The response contains `id`, `repoName`, `branch`, `status`, `commitSha`, `duration`, and `createdAt`, and does not contain `errorLog`.
- `GET /api/builds/{id}` returns `200 OK` for an existing build.
- A missing ID returns `404` with `application/problem+json` and stable `type`, `title`, `status`, `detail`, and `instance` fields.
- An unsupported status such as `BROKEN` returns `400` with `application/problem+json` and does not invoke `BuildEventService`.
- The persisted status uses the enum name, not its ordinal position.

### Tests

- Keep the existing ticket 1 validation cases passing after converting the request to a record.
- Extend the MVC slice with an existing-build ID lookup and a missing-build `ProblemDetail` case.
- Add an unsupported-status request case that verifies the service is not called.
- Assert the success response contract and that `errorLog` is absent.
- Assert the important `ProblemDetail` fields rather than only the status code.
- Keep the focused tests independent of PostgreSQL.

### Explanation

- Explain why an enum prevents invalid domain states better than a free-form string and why JPA should persist it with `EnumType.STRING`.
- Explain record-generated accessors, constructor, `equals`, `hashCode`, and immutability limits.
- Explain why API records and JPA entities change for different reasons and should not be the same serialized type.
- Trace an unsupported enum value from JSON conversion to the error handler and show why the service is not called.
- Explain why the service throws a domain/application exception while the web layer chooses HTTP `404` and `ProblemDetail`.

## Relevant files

- `src/main/java/com/cight/dto/BuildEventRequest.java`
- `src/main/java/com/cight/dto/BuildEventResponse.java` — create this file.
- `src/main/java/com/cight/model/BuildStatus.java` — create this file.
- `src/main/java/com/cight/model/BuildEvent.java`
- `src/main/java/com/cight/service/BuildEventService.java`
- `src/main/java/com/cight/service/WebhookService.java`
- `src/main/java/com/cight/controller/BuildController.java`
- `src/main/java/com/cight/exception/BuildEventNotFoundException.java` — create this file.
- `src/main/java/com/cight/exception/ApiExceptionHandler.java` — create this file.
- `src/test/java/com/cight/controller/BuildControllerTest.java`

## Primary references

- [Java 21 record classes](https://docs.oracle.com/en/java/javase/21/language/records.html)
- [Jakarta Persistence enumerated mapping](https://jakarta.ee/specifications/persistence/3.2/apidocs/jakarta.persistence/jakarta/persistence/enumerated)
- [Spring MVC error responses and `ProblemDetail`](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-ann-rest-exceptions.html)
- [Spring MVC controller advice](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-advice.html)
- [Spring MVC request-body validation](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-validation.html)

## Verification

Run:

```sh
sh ./mvnw -Dtest=BuildControllerTest test
```

Do not start PostgreSQL for this MVC-slice ticket. If Mockito fails before the tests execute because Byte Buddy cannot attach to the current JVM, record that runner limitation separately; do not weaken the test scope or change production behavior to hide it.

## Progressive hints

### Hint 1 — Concept

Treat the enum as domain vocabulary, records as HTTP data carriers, and `ProblemDetail` as the web representation of a failure. Each has one reason to change.

### Hint 2 — Layer or file

Start by tracing every use of `status` and every controller return type. Write down the compile-time changes before editing.

### Hint 3 — Pseudocode

Available only after the learner shares an attempt or a concrete blocker.

### Hint 4 — Partial code

Available only after the pseudocode-level hint is insufficient.

### Hint 5 — Complete solution after an attempt

Available only after a meaningful implementation attempt and review. The generated reference branch may then be inspected read-only if comparison is educational.

## PR review checklist

- Every status-bearing production path uses `BuildStatus`; no arbitrary string or ordinal persistence remains.
- API endpoints serialize response records, not the entity.
- Record components preserve the intended validation constraints and avoid Lombok boilerplate.
- Mapping is explicit and does not leak `errorLog`.
- The service is unaware of HTTP status codes or `ProblemDetail`.
- The advice returns safe, stable details without stack traces or internal exception text.
- Tests cover observable contracts and prove rejected JSON never reaches the service.
- No schema, dependency, analytics, or broad package refactor slipped into the diff.

## Debugging scenario

If `"status": "BROKEN"` reaches the service or produces a generic `500`, locate whether the request component is still a `String`, JSON conversion is being bypassed, or the conversion exception is not handled by the MVC advice. Use the failing HTTP test and exception type as evidence before changing code.

## Interview defence

Answer one question at a time during review:

1. What invalid state becomes impossible after replacing `String status` with `BuildStatus`?
2. Why is persisting an enum ordinal dangerous when constants are reordered?
3. A record is immutable; does that make every object referenced by its components deeply immutable?
4. Why should a controller return `BuildEventResponse` instead of the JPA entity?
5. Why does `BuildEventNotFoundException` belong below the web translation while `404` belongs at the HTTP boundary?

## Learning-record evidence

Do not create a learning record merely because the code compiles. Evidence requires focused behavior tests, a reviewed diff, a correct trace of both failure paths, and successful defence of the enum, record, entity-boundary, and exception-mapping tradeoffs.
