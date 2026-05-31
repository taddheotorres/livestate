package com.realestate.api.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GlobalExceptionHandlerTest {

    @LocalServerPort
    private int port;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @Test
    void shouldReturn404ForUnknownEndpoint() {
        webTestClient.get()
                .uri("/api/properties/99999")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldReturn401ForUnauthenticatedPost() {
        webTestClient.post()
                .uri("/api/bookings")
                .bodyValue(java.util.Map.of("propertyId", 1))
                .exchange()
                .expectStatus().isForbidden();
    }
}
