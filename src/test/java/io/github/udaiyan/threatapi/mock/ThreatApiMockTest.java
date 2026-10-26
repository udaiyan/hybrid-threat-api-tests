package io.github.udaiyan.threatapi.mock;

import io.github.udaiyan.threatapi.model.Severity;
import io.github.udaiyan.threatapi.model.Threat;
import io.github.udaiyan.threatapi.model.ThreatStatus;
import io.github.udaiyan.threatapi.support.ApiUrls;
import io.github.udaiyan.threatapi.support.JsonMapper;
import io.github.udaiyan.threatapi.support.JsonTestData;
import io.github.udaiyan.threatapi.support.TestTags;
import io.github.udaiyan.threatapi.support.WireMockThreatApi;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Tag(TestTags.MOCK)
class ThreatApiMockTest {

    private static final WireMockThreatApi MOCK = new WireMockThreatApi();

    @BeforeAll
    static void startMock() {
        MOCK.start();
    }

    @AfterAll
    static void stopMock() {
        MOCK.stop();
    }

    @Test
    void healthReturnsUp() {
        given()
                .accept(ContentType.JSON)
                .when()
                .get(ApiUrls.HEALTH)
                .then()
                .statusCode(200)
                .body("status", equalTo("UP"));
    }

    @Test
    void creatingAValidThreatReturnsCreated() {
        given()
                .contentType(ContentType.JSON)
                .body(JsonTestData.validCreateThreat())
                .when()
                .post(ApiUrls.THREATS)
                .then()
                .statusCode(201)
                .body("name", equalTo("Synthetic phishing campaign"))
                .body("severity", equalTo("HIGH"));
    }

    @Test
    void threatCanBeMappedToAJavaObject() {
        String body = given()
                .accept(ContentType.JSON)
                .when()
                .get(ApiUrls.THREAT_BY_ID, ApiUrls.VALID_THREAT_ID)
                .then()
                .statusCode(200)
                .extract()
                .asString();

        Threat threat = JsonMapper.fromString(body, Threat.class);

        assertThat(threat.id()).isEqualTo(ApiUrls.VALID_THREAT_ID);
        assertThat(threat.name()).isEqualTo("Synthetic phishing campaign");
        assertThat(threat.status()).isEqualTo(ThreatStatus.OPEN);
        assertThat(threat.severity()).isEqualTo(Severity.HIGH);
        assertThat(threat.source().name()).isEqualTo("Synthetic OSINT feed");
        assertThat(threat.indicators()).hasSize(1);
        assertThat(threat.indicators().get(0).value()).isEqualTo("synthetic.example.com");
    }
}
