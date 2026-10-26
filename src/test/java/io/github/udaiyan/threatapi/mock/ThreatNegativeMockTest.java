package io.github.udaiyan.threatapi.mock;

import io.github.udaiyan.threatapi.support.ApiUrls;
import io.github.udaiyan.threatapi.support.JsonTestData;
import io.github.udaiyan.threatapi.support.TestTags;
import io.github.udaiyan.threatapi.support.WireMockThreatApi;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@Tag(TestTags.MOCK)
class ThreatNegativeMockTest {

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
    void missingNameIsRejected() {
        given()
                .header("X-Test-Case", "invalid-request")
                .contentType(ContentType.JSON)
                .body(JsonTestData.missingNameCreateThreat())
                .when()
                .post(ApiUrls.THREATS)
                .then()
                .statusCode(400);
    }

    @Test
    void invalidSeverityIsRejected() {
        given()
                .header("X-Test-Case", "invalid-request")
                .contentType(ContentType.JSON)
                .body(JsonTestData.invalidSeverityCreateThreat())
                .when()
                .post(ApiUrls.THREATS)
                .then()
                .statusCode(400);
    }

    @Test
    void unknownThreatIdIsNotFound() {
        given()
                .when()
                .get(ApiUrls.THREAT_BY_ID, "unknown-threat")
                .then()
                .statusCode(404);
    }
}
