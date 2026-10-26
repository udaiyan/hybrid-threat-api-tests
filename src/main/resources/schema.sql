CREATE TABLE IF NOT EXISTS threats (
    id UUID PRIMARY KEY,
    name TEXT NOT NULL,
    status TEXT NOT NULL,
    severity TEXT NOT NULL,
    detected_at TIMESTAMPTZ NOT NULL,
    source_name TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS indicators (
    id UUID PRIMARY KEY,
    threat_id UUID NOT NULL REFERENCES threats(id),
    type TEXT NOT NULL,
    value TEXT NOT NULL,
    confidence DOUBLE PRECISION NOT NULL CHECK (confidence >= 0 AND confidence <= 1),
    first_seen TIMESTAMPTZ,
    last_seen TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_threats_severity ON threats (severity);
CREATE INDEX IF NOT EXISTS idx_threats_status ON threats (status);
CREATE INDEX IF NOT EXISTS idx_indicators_threat_id ON indicators (threat_id);
