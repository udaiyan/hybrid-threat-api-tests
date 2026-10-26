package io.github.udaiyan.threatapi.app;

import io.github.udaiyan.threatapi.model.Threat;
import io.github.udaiyan.threatapi.support.ApiUrls;
import io.github.udaiyan.threatapi.support.JsonMapper;
import io.github.udaiyan.threatapi.support.JsonTestData;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

class ThreatApiAppTest extends BaseAppTest {

    @Test
    void healthReturnsUp() {
        given()
                .when()
                .get(ApiUrls.HEALTH)
                .then()
                .statusCode(200)
                .body("status", equalTo("UP"));
    }

    @Test
    void threatListReturnsSeededData() {
        given()
                .queryParam("page", 0)
                .queryParam("size", 10)
                .when()
                .get(ApiUrls.THREATS)
                .then()
                .statusCode(200)
                .body("threats.size()", greaterThanOrEqualTo(2))
                .body("page.page", equalTo(0));
    }

    @Test
    void severityFilterReturnsOnlyMatchingThreats() {
        given()
                .queryParam("severity", "HIGH")
                .when()
                .get(ApiUrls.THREATS)
                .then()
                .statusCode(200)
                .body("threats.severity.every { it == 'HIGH' }", equalTo(true));
    }

    @Test
    void createdThreatCanBeRetrievedById() {
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

        given()
                .when()
                .get(ApiUrls.THREAT_BY_ID, created.id())
                .then()
                .statusCode(200)
                .body("id", equalTo(created.id()))
                .body("name", equalTo(created.name()));
    }

    @Test
    void malformedUuidIsNotFound() {
        given()
                .when()
                .get(ApiUrls.THREAT_BY_ID, "not-a-uuid")
                .then()
                .statusCode(404);
    }
}
