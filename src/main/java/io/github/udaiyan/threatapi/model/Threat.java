package io.github.udaiyan.threatapi.model;

import java.time.Instant;
import java.util.List;

public record Threat(
        String id,
        String name,
        ThreatStatus status,
        Severity severity,
        Instant detectedAt,
        Source source,
        List<Indicator> indicators
) {
}
