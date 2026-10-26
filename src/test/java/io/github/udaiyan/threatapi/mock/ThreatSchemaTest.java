package io.github.udaiyan.threatapi.mock;

import io.github.udaiyan.threatapi.support.ApiUrls;
import io.github.udaiyan.threatapi.support.TestTags;
import io.github.udaiyan.threatapi.support.WireMockThreatApi;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

@Tag(TestTags.MOCK)
class ThreatSchemaTest {

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
    void singleThreatMatchesJsonSchema() {
        given()
                .accept(ContentType.JSON)
                .when()
                .get(ApiUrls.THREAT_BY_ID, ApiUrls.VALID_THREAT_ID)
                .then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/threat-response.json"));
    }

    @Test
    void threatListMatchesJsonSchema() {
        given()
                .accept(ContentType.JSON)
                .when()
                .get(ApiUrls.THREATS)
                .then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/threat-list-response.json"));
    }
}
