package io.github.udaiyan.threatapi.app;

import io.github.udaiyan.threatapi.support.ApiUrls;
import io.github.udaiyan.threatapi.support.TestTokens;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

class PasetoAuthTest extends BaseAppTest {

    @Test
    void missingTokenIsRejected() {
        given()
                .when()
                .get(ApiUrls.ASSESSMENTS)
                .then()
                .statusCode(401);
    }

    @Test
    void invalidTokenIsRejected() {
        given()
                .header("Authorization", "Bearer " + TestTokens.INVALID_PASETO_TOKEN)
                .when()
                .get(ApiUrls.ASSESSMENTS)
                .then()
                .statusCode(401);
    }

    @Test
    void tokenWithoutBearerPrefixIsRejected() {
        given()
                .header("Authorization", TestTokens.VALID_PASETO_TOKEN)
                .when()
                .get(ApiUrls.ASSESSMENTS)
                .then()
                .statusCode(401);
    }

    @Test
    void validTokenReturnsAssessments() {
        given()
                .header("Authorization", "Bearer " + TestTokens.VALID_PASETO_TOKEN)
                .when()
                .get(ApiUrls.ASSESSMENTS)
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(1));
    }
}
