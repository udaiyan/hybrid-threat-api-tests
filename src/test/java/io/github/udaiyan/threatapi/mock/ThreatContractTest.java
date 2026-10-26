package io.github.udaiyan.threatapi.mock;

import com.atlassian.oai.validator.restassured.OpenApiValidationFilter;
import io.github.udaiyan.threatapi.support.ApiUrls;
import io.github.udaiyan.threatapi.support.JsonTestData;
import io.github.udaiyan.threatapi.support.TestTags;
import io.github.udaiyan.threatapi.support.WireMockThreatApi;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.net.URL;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@Tag(TestTags.MOCK)
class ThreatContractTest {

    private static final WireMockThreatApi MOCK = new WireMockThreatApi();
    private static OpenApiValidationFilter CONTRACT;

    @BeforeAll
    static void startMock() {
        MOCK.start();

        // Resolve the classpath resource to a proper file URL so the parser can read it
        URL resource = ThreatContractTest.class.getClassLoader().getResource("openapi/threat-api.yaml");
        assertThat(resource)
                .as("OpenAPI specification must be present on the classpath")
                .isNotNull();

        CONTRACT = new OpenApiValidationFilter(resource.toString());
    }

    @AfterAll
    static void stopMock() {
        MOCK.stop();
    }

    @Test
    void threatListResponseMatchesOpenApiContract() {
        given()
                .filter(CONTRACT)
                .accept(ContentType.JSON)
                .when()
                .get(ApiUrls.THREATS)
                .then()
                .statusCode(200);
    }

    @Test
    void singleThreatResponseMatchesOpenApiContract() {
        given()
                .filter(CONTRACT)
                .accept(ContentType.JSON)
                .when()
                .get(ApiUrls.THREAT_BY_ID, ApiUrls.VALID_THREAT_ID)
                .then()
                .statusCode(200);
    }

    @Test
    void createThreatRequestAndResponseMatchOpenApiContract() {
        given()
                .filter(CONTRACT)
                .contentType(ContentType.JSON)
                .body(JsonTestData.validCreateThreat())
                .when()
                .post(ApiUrls.THREATS)
                .then()
                .statusCode(201);
    }
}