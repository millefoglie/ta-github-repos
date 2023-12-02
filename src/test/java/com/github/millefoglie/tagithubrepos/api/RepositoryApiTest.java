package com.github.millefoglie.tagithubrepos.api;

import com.github.millefoglie.tagithubrepos.model.ErrorResponse;
import com.github.millefoglie.tagithubrepos.model.GetAllPublicReposResponse;
import com.github.millefoglie.tagithubrepos.model.GetAllPublicReposResponseReposInner;
import com.github.millefoglie.tagithubrepos.model.GetAllPublicReposResponseReposInnerBranchesInner;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.github.tomakehurst.wiremock.standalone.JsonFileMappingsSource;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureWebTestClient
public class RepositoryApiTest {

    @RegisterExtension
    static WireMockExtension wiremock = WireMockExtension
            .newInstance()
            .options(
                    wireMockConfig()
                            .port(17001)
                            .mappingSource(
                                    new JsonFileMappingsSource(
                                            wireMockConfig()
                                                    .filesRoot()
                                                    .child("wiremock")
                                                    .child("repository-api")
                                                    .child("mappings"),
                                            null
                                    )
                            )
            )
            .failOnUnmatchedRequests(true)
            .build();

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void failWith404onUnknownUsername() {
        wiremock.stubFor(
                get("/users/unknown/repos")
                        .willReturn(aResponse().withStatus(404)).atPriority(1)
        );

        var responseBody = webTestClient
                .get()
                .uri("/v1/users/unknown/repos")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody(ErrorResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(responseBody).isNotNull();

        var softly = new SoftAssertions();
        softly.assertThat(responseBody.getStatus()).isEqualTo(404);
        softly.assertThat(responseBody.getMessage()).startsWith("No unknown repositories found");
        softly.assertAll();
    }

    @Test
    void failWith404onNotFoundRepo() {
        wiremock.stubFor(
                get("/repos/millefoglie/js-photo_album/branches")
                        .willReturn(aResponse().withStatus(404)).atPriority(1)
        );

        var responseBody = webTestClient
                .get()
                .uri("/v1/users/millefoglie/repos")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody(ErrorResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(responseBody).isNotNull();

        var softly = new SoftAssertions();
        softly.assertThat(responseBody.getStatus()).isEqualTo(404);
        softly.assertThat(responseBody.getMessage()).startsWith("No millefoglie/js-photo_album repository found");
        softly.assertAll();
    }

    @Test
    void failWith500onGithubError() {
        wiremock.stubFor(
                get("/repos/millefoglie/js-photo_album/branches")
                        .willReturn(aResponse().withStatus(502)).atPriority(1)
        );

        var responseBody = webTestClient
                .get()
                .uri("/v1/users/millefoglie/repos")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                .expectBody(ErrorResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(responseBody).isNotNull();

        var softly = new SoftAssertions();
        softly.assertThat(responseBody.getStatus()).isEqualTo(500);
        softly.assertThat(responseBody.getMessage()).startsWith("Could not fetch data from GitHub:");
        softly.assertAll();
    }

    @Test
    void failWith404onUnkownEndpoint() {
        var responseBody = webTestClient
                .get()
                .uri("/v1/unknown-endpoint")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody(ErrorResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(responseBody).isNotNull();

        var softly = new SoftAssertions();
        softly.assertThat(responseBody.getStatus()).isEqualTo(404);
        softly.assertThat(responseBody.getMessage()).startsWith("Not Found");
        softly.assertAll();
    }

    @Test
    void failWith406onUnsupportedAccept() {
        var responseBody = webTestClient
                .get()
                .uri("/v1/users/millefoglie/repos")
                .accept(MediaType.APPLICATION_XML)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.NOT_ACCEPTABLE)
                .expectBody(ErrorResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(responseBody).isNotNull();

        var softly = new SoftAssertions();
        softly.assertThat(responseBody.getStatus()).isEqualTo(406);
        softly.assertThat(responseBody.getMessage()).startsWith("Not Acceptable");
        softly.assertAll();
    }

    @Test
    void happyPath() {
        var responseBody = webTestClient
                .get()
                .uri("/v1/users/millefoglie/repos")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(GetAllPublicReposResponse.class)
                .returnResult()
                .getResponseBody();

        var expected = new GetAllPublicReposResponse()
                .addReposItem(
                        new GetAllPublicReposResponseReposInner()
                                .name("js-photo_album")
                                .owner("millefoglie")
                                .addBranchesItem(
                                        new GetAllPublicReposResponseReposInnerBranchesInner()
                                                .name("master")
                                                .lastCommitSha("663f4175b5f0c14ce5e2a02282eba3785745e042")
                                )
                )
                .addReposItem(
                        new GetAllPublicReposResponseReposInner()
                                .name("latex-dom")
                                .owner("millefoglie")
                                .addBranchesItem(
                                        new GetAllPublicReposResponseReposInnerBranchesInner()
                                                .name("develop")
                                                .lastCommitSha("1893e8b7866accb5a2d4f1d3920b9e366ee2b73a")
                                )
                );

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getRepos()).containsExactlyInAnyOrderElementsOf(expected.getRepos());
    }
}
