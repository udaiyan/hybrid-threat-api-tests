package io.github.udaiyan.threatapi.model;

import java.util.List;

public record ThreatListResponse(List<Threat> threats, PageMeta page) {
}
