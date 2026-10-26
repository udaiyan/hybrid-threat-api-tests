package io.github.udaiyan.threatapi.app.service;

import io.github.udaiyan.threatapi.app.InvalidRequestException;
import io.github.udaiyan.threatapi.app.persistence.ThreatRepository;
import io.github.udaiyan.threatapi.model.Indicator;
import io.github.udaiyan.threatapi.model.IndicatorType;
import io.github.udaiyan.threatapi.model.PageMeta;
import io.github.udaiyan.threatapi.model.Severity;
import io.github.udaiyan.threatapi.model.Source;
import io.github.udaiyan.threatapi.model.Threat;
import io.github.udaiyan.threatapi.model.ThreatListResponse;
import io.github.udaiyan.threatapi.model.ThreatStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ThreatService {

    private final ThreatRepository repository;

    public ThreatService(ThreatRepository repository) {
        this.repository = repository;
    }

    public Threat create(Threat input) {
        if (input == null) {
            throw new InvalidRequestException("Request body is required");
        }
        if (input.name() == null || input.name().isBlank()) {
            throw new InvalidRequestException("name is required");
        }
        if (input.severity() == null) {
            throw new InvalidRequestException("severity is required and must be a known value");
        }
        if (input.status() == null) {
            throw new InvalidRequestException("status is required and must be a known value");
        }
        if (input.source() == null || input.source().name() == null || input.source().name().isBlank()) {
            throw new InvalidRequestException("source.name is required");
        }
        if (input.detectedAt() == null) {
            throw new InvalidRequestException("detectedAt is required");
        }

        List<Indicator> indicators = input.indicators() == null
                ? List.of()
                : input.indicators().stream().map(this::validateIndicator).toList();

        Threat threat = new Threat(
                UUID.randomUUID().toString(),
                input.name(),
                input.status(),
                input.severity(),
                input.detectedAt(),
                input.source(),
                indicators
        );

        repository.save(threat);
        return threat;
    }

    public Optional<Threat> findById(String threatId) {
        return repository.findById(threatId);
    }

    public ThreatListResponse search(int page, int size, String status, String severity) {
        if (page < 0) {
            throw new InvalidRequestException("page must be 0 or greater");
        }
        if (size < 1 || size > 100) {
            throw new InvalidRequestException("size must be between 1 and 100");
        }
        validateEnumOrThrow(status, "status", ThreatStatus.values());
        validateEnumOrThrow(severity, "severity", Severity.values());

        List<Threat> threats = repository.search(page, size, status, severity);
        long totalElements = repository.count(status, severity);
        int totalPages = size == 0 ? 0 : (int) Math.ceil((double) totalElements / size);

        return new ThreatListResponse(threats, new PageMeta(page, size, totalElements, totalPages));
    }

    private Indicator validateIndicator(Indicator indicator) {
        if (indicator == null || indicator.type() == null || indicator.value() == null
                || indicator.value().isBlank() || indicator.confidence() == null) {
            throw new InvalidRequestException("Each indicator requires type, value and confidence");
        }
        if (indicator.confidence() < 0.0 || indicator.confidence() > 1.0) {
            throw new InvalidRequestException("indicator confidence must be between 0 and 1");
        }
        if (indicator.type() == IndicatorType.DOMAIN && indicator.value().contains(" ")) {
            throw new InvalidRequestException("indicator domain value must not contain spaces");
        }

        String id = indicator.id() == null || indicator.id().isBlank()
                ? UUID.randomUUID().toString()
                : indicator.id();

        return new Indicator(
                id,
                indicator.type(),
                indicator.value(),
                indicator.confidence(),
                indicator.firstSeen(),
                indicator.lastSeen()
        );
    }

    private void validateEnumOrThrow(String value, String field, Object[] allowed) {
        if (value == null || value.isBlank()) {
            return;
        }
        for (Object option : allowed) {
            if (option.toString().equalsIgnoreCase(value)) {
                return;
            }
        }
        throw new InvalidRequestException("Unknown " + field + ": " + value);
    }
}
