INSERT INTO threats (id, name, status, severity, detected_at, source_name)
VALUES (
    '11111111-1111-1111-1111-111111111111',
    'Synthetic phishing campaign',
    'OPEN',
    'HIGH',
    '2026-10-01T09:00:00Z',
    'Synthetic OSINT feed'
)
ON CONFLICT (id) DO NOTHING;

INSERT INTO threats (id, name, status, severity, detected_at, source_name)
VALUES (
    '22222222-2222-2222-2222-222222222222',
    'Synthetic infrastructure probing',
    'INVESTIGATING',
    'MEDIUM',
    '2026-10-02T09:30:00Z',
    'Synthetic sensor feed'
)
ON CONFLICT (id) DO NOTHING;

INSERT INTO indicators (id, threat_id, type, value, confidence, first_seen, last_seen)
VALUES (
    '33333333-3333-3333-3333-333333333333',
    '11111111-1111-1111-1111-111111111111',
    'DOMAIN',
    'synthetic.example.com',
    0.87,
    '2026-10-01T08:00:00Z',
    '2026-10-01T09:00:00Z'
)
ON CONFLICT (id) DO NOTHING;
