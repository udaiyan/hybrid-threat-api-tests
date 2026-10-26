package io.github.udaiyan.threatapi.mock;

import io.github.udaiyan.threatapi.support.ApiUrls;
import io.github.udaiyan.threatapi.support.TestTags;
import io.github.udaiyan.threatapi.support.WireMockThreatApi;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@Tag(TestTags.MOCK)
class ThreatPaginationMockTest {

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
    void firstPageReturnsPageMetadata() {
        given()
                .queryParam("page", 0)
                .queryParam("size", 2)
                .when()
                .get(ApiUrls.THREATS)
                .then()
                .statusCode(200)
                .body("page.page", equalTo(0))
                .body("page.totalElements", equalTo(1))
                .body("page.totalPages", equalTo(1))
                .body("threats.size()", equalTo(1));
    }

    @Test
    void invalidPageSizeIsRejected() {
        given()
                .queryParam("page", 0)
                .queryParam("size", 0)
                .when()
                .get(ApiUrls.THREATS)
                .then()
                .statusCode(400);
    }
}
