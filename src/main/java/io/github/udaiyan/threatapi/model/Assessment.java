package io.github.udaiyan.threatapi.model;

import java.time.Instant;

public record Assessment(String id, String threatId, String summary, Instant assessedAt) {
}
