package io.github.udaiyan.threatapi.support;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import io.restassured.RestAssured;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;

public class WireMockThreatApi {

    private final WireMockServer server = new WireMockServer(
            WireMockConfiguration.wireMockConfig().dynamicPort()
    );

    public void start() {
        server.start();
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = server.port();
        RestAssured.reset();
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = server.port();
        stubEndpoints();
    }

    public void stop() {
        server.stop();
    }

    private void stubEndpoints() {
        server.stubFor(get(urlEqualTo(ApiUrls.HEALTH))
                .willReturn(json(200, "/wiremock/health-response.json")));

        server.stubFor(get(urlPathEqualTo(ApiUrls.THREATS))
                .withQueryParam("size", equalTo("0"))
                .atPriority(1)
                .willReturn(json(400, "/wiremock/error-response.json")));

        server.stubFor(get(urlPathEqualTo(ApiUrls.THREATS))
                .atPriority(10)
                .willReturn(json(200, "/wiremock/threat-list-response.json")));

        server.stubFor(get(urlEqualTo(ApiUrls.THREATS + "/" + ApiUrls.VALID_THREAT_ID))
                .atPriority(1)
                .willReturn(json(200, "/wiremock/threat-response.json")));

        server.stubFor(get(urlEqualTo(ApiUrls.THREATS + "/unknown-threat"))
                .atPriority(1)
                .willReturn(json(404, "/wiremock/error-response.json")));

        server.stubFor(post(urlEqualTo(ApiUrls.THREATS))
                .withHeader("X-Test-Case", equalTo("invalid-request"))
                .atPriority(1)
                .willReturn(json(400, "/wiremock/error-response.json")));

        server.stubFor(post(urlEqualTo(ApiUrls.THREATS))
                .atPriority(10)
                .willReturn(json(201, "/wiremock/threat-response.json")));
    }

    private com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder json(
            int status, String resource) {
        return aResponse()
                .withStatus(status)
                .withHeader("Content-Type", "application/json")
                .withBody(read(resource));
    }

    private String read(String path) {
        try (InputStream in = WireMockThreatApi.class.getResourceAsStream(path)) {
            if (in == null) {
                throw new IllegalStateException("Missing WireMock resource: " + path);
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to read WireMock resource: " + path, ex);
        }
    }
}
