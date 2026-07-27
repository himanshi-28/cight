# CIght Ticket Format and Lifecycle

## Lifecycle

Each detailed ticket has one status:

```text
active → review → complete
             ↘ paused
```

- `active`: the learner is predicting, implementing, or debugging.
- `review`: the learner has submitted an attempt for tests, review, and defence.
- `paused`: work stopped intentionally; the reason and safe resumption point are recorded.
- `complete`: behavior, tests, review, explanation, and interview defence all passed.

At most one ticket may be `active` or `review`.

## Required format

```md
---
id: CIGHT-NNN
title: Short action-oriented title
status: active
week: N
roadmap: Short roadmap relationship
timebox: 90–120 minutes
---

# CIGHT-NNN — Title

## Why this ticket is next

## Learning objective

## Prediction checkpoint

## In scope

## Out of scope

## Acceptance criteria

### Behavior

### Tests

### Explanation

## Relevant files

## Primary references

## Verification

## Progressive hints

### Hint 1 — Concept
### Hint 2 — Layer or file
### Hint 3 — Pseudocode
### Hint 4 — Partial code
### Hint 5 — Complete solution after an attempt

## PR review checklist

## Debugging scenario

## Interview defence

## Learning-record evidence
```

## Assignment rules

- The roadmap may list future titles, but only the active file contains actionable work.
- Begin with the prediction checkpoint and wait for the learner's answer.
- Reveal progressive hints only when requested or when evidence shows the learner is stuck.
- Do not open the generated reference implementation until after an attempt.
- Mark complete only after the learner demonstrates understanding; passing code alone is insufficient.

