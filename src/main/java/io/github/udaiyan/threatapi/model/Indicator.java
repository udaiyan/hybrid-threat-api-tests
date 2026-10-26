package io.github.udaiyan.threatapi.model;

import java.time.Instant;

public record Indicator(
        String id,
        IndicatorType type,
        String value,
        Double confidence,
        Instant firstSeen,
        Instant lastSeen
) {
}
