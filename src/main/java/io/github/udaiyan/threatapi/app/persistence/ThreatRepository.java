package io.github.udaiyan.threatapi.app.persistence;

import io.github.udaiyan.threatapi.model.Indicator;
import io.github.udaiyan.threatapi.model.IndicatorType;
import io.github.udaiyan.threatapi.model.Severity;
import io.github.udaiyan.threatapi.model.Source;
import io.github.udaiyan.threatapi.model.Threat;
import io.github.udaiyan.threatapi.model.ThreatStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ThreatRepository {

    private final JdbcTemplate jdbc;

    public ThreatRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void save(Threat threat) {
        jdbc.update(
                "INSERT INTO threats (id, name, status, severity, detected_at, source_name) "
                        + "VALUES (?, ?, ?, ?, ?, ?)",
                UUID.fromString(threat.id()),
                threat.name(),
                threat.status().name(),
                threat.severity().name(),
                Timestamp.from(threat.detectedAt()),
                threat.source().name()
        );

        if (threat.indicators() == null) {
            return;
        }

        for (Indicator indicator : threat.indicators()) {
            jdbc.update(
                    "INSERT INTO indicators (id, threat_id, type, value, confidence, first_seen, last_seen) "
                            + "VALUES (?, ?, ?, ?, ?, ?, ?)",
                    UUID.fromString(indicator.id()),
                    UUID.fromString(threat.id()),
                    indicator.type().name(),
                    indicator.value(),
                    indicator.confidence(),
                    toTimestamp(indicator.firstSeen()),
                    toTimestamp(indicator.lastSeen())
            );
        }
    }

    public Optional<Threat> findById(String threatId) {
        UUID uuid = parseUuidOrReturnNull(threatId);
        if (uuid == null) {
            return Optional.empty();
        }

        List<Threat> rows = jdbc.query(
                "SELECT id, name, status, severity, detected_at, source_name FROM threats WHERE id = ?",
                (rs, rowNum) -> threatFromRow(rs),
                uuid
        );

        if (rows.isEmpty()) {
            return Optional.empty();
        }

        Threat threat = rows.get(0);
        List<Indicator> indicators = jdbc.query(
                "SELECT id, type, value, confidence, first_seen, last_seen "
                        + "FROM indicators WHERE threat_id = ? ORDER BY id",
                (rs, rowNum) -> indicatorFromRow(rs),
                uuid
        );

        return Optional.of(new Threat(
                threat.id(),
                threat.name(),
                threat.status(),
                threat.severity(),
                threat.detectedAt(),
                threat.source(),
                indicators
        ));
    }

    public List<Threat> search(int page, int size, String status, String severity) {
        StringBuilder sql = new StringBuilder(
                "SELECT id, name, status, severity, detected_at, source_name FROM threats WHERE 1=1"
        );
        List<Object> params = new ArrayList<>();

        appendFilter(sql, params, "status", status);
        appendFilter(sql, params, "severity", severity);

        sql.append(" ORDER BY detected_at DESC, id ASC LIMIT ? OFFSET ?");
        params.add(size);
        params.add(page * size);

        return jdbc.query(sql.toString(), (rs, rowNum) -> threatFromRow(rs), params.toArray());
    }

    public long count(String status, String severity) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM threats WHERE 1=1");
        List<Object> params = new ArrayList<>();

        appendFilter(sql, params, "status", status);
        appendFilter(sql, params, "severity", severity);

        Long result = jdbc.queryForObject(sql.toString(), Long.class, params.toArray());
        return result == null ? 0L : result;
    }

    private void appendFilter(StringBuilder sql, List<Object> params, String column, String value) {
        if (value != null && !value.isBlank()) {
            sql.append(" AND ").append(column).append(" = ?");
            params.add(value.toUpperCase());
        }
    }

    private Threat threatFromRow(ResultSet rs) throws SQLException {
        return new Threat(
                rs.getString("id"),
                rs.getString("name"),
                ThreatStatus.valueOf(rs.getString("status")),
                Severity.valueOf(rs.getString("severity")),
                toInstant(rs.getTimestamp("detected_at")),
                new Source(rs.getString("source_name")),
                List.of()
        );
    }

    private Indicator indicatorFromRow(ResultSet rs) throws SQLException {
        return new Indicator(
                rs.getString("id"),
                IndicatorType.valueOf(rs.getString("type")),
                rs.getString("value"),
                rs.getDouble("confidence"),
                toInstant(rs.getTimestamp("first_seen")),
                toInstant(rs.getTimestamp("last_seen"))
        );
    }

    private UUID parseUuidOrReturnNull(String value) {
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException | NullPointerException ex) {
            return null;
        }
    }

    private Instant toInstant(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toInstant();
    }

    private Timestamp toTimestamp(Instant instant) {
        return instant == null ? null : Timestamp.from(instant);
    }
}
