CREATE TABLE build_events (
    id UUID PRIMARY KEY,
    repo_name VARCHAR(255) NOT NULL,
    branch VARCHAR(255) NOT NULL,
    status VARCHAR(32) NOT NULL,
    commit_sha VARCHAR(64),
    error_log TEXT,
    duration_ms BIGINT,
    github_run_id BIGINT,
    github_delivery_id VARCHAR(128),
    workflow_name VARCHAR(255),
    run_url TEXT,
    started_at TIMESTAMPTZ,
    completed_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT ck_build_status
        CHECK (status IN ('PENDING', 'SUCCESS', 'FAILURE', 'CANCELLED', 'UNKNOWN')),
    CONSTRAINT ck_duration_non_negative
        CHECK (duration_ms IS NULL OR duration_ms >= 0),
    CONSTRAINT uq_build_github_run UNIQUE (github_run_id),
    CONSTRAINT uq_build_github_delivery UNIQUE (github_delivery_id)
);

CREATE INDEX idx_build_repo ON build_events (repo_name);
CREATE INDEX idx_build_status ON build_events (status);
CREATE INDEX idx_build_commit_sha ON build_events (commit_sha);
CREATE INDEX idx_build_created_at ON build_events (created_at DESC);
CREATE INDEX idx_build_repo_created ON build_events (repo_name, created_at DESC);

CREATE TABLE failure_analyses (
    id UUID PRIMARY KEY,
    build_event_id UUID NOT NULL REFERENCES build_events(id) ON DELETE CASCADE,
    request_key VARCHAR(255) NOT NULL UNIQUE,
    mode VARCHAR(16) NOT NULL,
    status VARCHAR(16) NOT NULL,
    summary TEXT,
    likely_root_cause TEXT,
    suggested_actions JSONB,
    confidence VARCHAR(16),
    model VARCHAR(128),
    prompt_version VARCHAR(32) NOT NULL,
    tools_used JSONB,
    attempt_count INTEGER NOT NULL DEFAULT 0,
    latency_ms BIGINT,
    failure_reason TEXT,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT ck_analysis_mode CHECK (mode IN ('BASELINE', 'AGENTIC')),
    CONSTRAINT ck_analysis_status CHECK (status IN ('PENDING', 'RUNNING', 'COMPLETED', 'FAILED')),
    CONSTRAINT ck_analysis_confidence
        CHECK (confidence IS NULL OR confidence IN ('LOW', 'MEDIUM', 'HIGH'))
);

CREATE INDEX idx_analysis_build ON failure_analyses (build_event_id, created_at DESC);
CREATE INDEX idx_analysis_status ON failure_analyses (status);

CREATE TABLE outbox_events (
    id UUID PRIMARY KEY,
    aggregate_type VARCHAR(64) NOT NULL,
    aggregate_id UUID NOT NULL,
    event_type VARCHAR(128) NOT NULL,
    topic VARCHAR(255) NOT NULL,
    message_key VARCHAR(255) NOT NULL,
    payload JSONB NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    published_at TIMESTAMPTZ,
    publish_attempts INTEGER NOT NULL DEFAULT 0,
    last_error TEXT
);

CREATE INDEX idx_outbox_unpublished
    ON outbox_events (created_at)
    WHERE published_at IS NULL;
