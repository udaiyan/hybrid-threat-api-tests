package io.github.udaiyan.threatapi.app;

import io.github.udaiyan.threatapi.support.ApiUrls;
import io.github.udaiyan.threatapi.support.JsonTestData;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

class ThreatNegativeAppTest extends BaseAppTest {

    @Test
    void missingNameReturnsBadRequest() {
        given()
                .contentType(ContentType.JSON)
                .body(JsonTestData.missingNameCreateThreat())
                .when()
                .post(ApiUrls.THREATS)
                .then()
                .statusCode(400);
    }

    @Test
    void invalidSeverityReturnsBadRequest() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"name\":\"Synthetic threat\",\"severity\":\"NOT_A_SEVERITY\"}")
                .when()
                .post(ApiUrls.THREATS)
                .then()
                .statusCode(400);
    }

    @Test
    void malformedJsonReturnsBadRequest() {
        given()
                .contentType(ContentType.JSON)
                .body("{")
                .when()
                .post(ApiUrls.THREATS)
                .then()
                .statusCode(400);
    }

    @Test
    void invalidPageSizeReturnsBadRequest() {
        given()
                .queryParam("size", 0)
                .when()
                .get(ApiUrls.THREATS)
                .then()
                .statusCode(400);
    }

    @Test
    void negativePageReturnsBadRequest() {
        given()
                .queryParam("page", -1)
                .when()
                .get(ApiUrls.THREATS)
                .then()
                .statusCode(400);
    }
}
