package io.github.udaiyan.threatapi.app;

import io.github.udaiyan.threatapi.model.Threat;
import io.github.udaiyan.threatapi.support.ApiUrls;
import io.github.udaiyan.threatapi.support.JsonMapper;
import io.github.udaiyan.threatapi.support.JsonTestData;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

class ThreatSqlValidationTest extends BaseAppTest {

    @Autowired
    private JdbcTemplate jdbc;

    @Test
    void createdThreatAndIndicatorsArePersisted() {
        String body = given()
                .contentType(ContentType.JSON)
                .body(JsonTestData.validCreateThreat())
                .when()
                .post(ApiUrls.THREATS)
                .then()
                .statusCode(201)
                .extract()
                .asString();

        Threat created = JsonMapper.fromString(body, Threat.class);

        Map<String, Object> row = jdbc.queryForMap(
                "SELECT id, name, status, severity, source_name, detected_at "
                        + "FROM threats WHERE id = ?",
                UUID.fromString(created.id())
        );

        assertThat(row.get("name")).isEqualTo(created.name());
        assertThat(row.get("status")).isEqualTo(created.status().name());
        assertThat(row.get("severity")).isEqualTo(created.severity().name());
        assertThat(row.get("source_name")).isEqualTo(created.source().name());
        assertThat(row.get("detected_at")).isNotNull();

        Integer indicatorCount = jdbc.queryForObject(
                "SELECT COUNT(*) FROM indicators WHERE threat_id = ?",
                Integer.class,
                UUID.fromString(created.id())
        );

        assertThat(indicatorCount).isEqualTo(created.indicators().size());

        String indicatorValue = jdbc.queryForObject(
                "SELECT value FROM indicators WHERE threat_id = ?",
                String.class,
                UUID.fromString(created.id())
        );

        assertThat(indicatorValue).isEqualTo(created.indicators().get(0).value());
    }

    @Test
    void seededThreatExistsInDatabase() {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM threats WHERE id = ?",
                Integer.class,
                UUID.fromString(ApiUrls.VALID_THREAT_ID)
        );

        assertThat(count).isEqualTo(1);
    }
}
