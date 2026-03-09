package com.example.apigateway.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = {ApiGatewayRestControllerV1.class})
class ApiGatewayRestControllerV1Test {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private ApplicationShutdownManager applicationShutdownManager;

    private static final String ENDPOINT_PATH = "/api/v1/gateway";

    @Test
    @DisplayName("Test exit endpoint - success")
    void whenExit_thenSuccessResponse() {
        Mockito.doNothing().when(applicationShutdownManager).shutDown();

        final WebTestClient.ResponseSpec result = webTestClient.post()
                .uri(ENDPOINT_PATH + "/exit")
                .exchange();

        result.expectStatus().isOk();
        verify(applicationShutdownManager, times(1)).shutDown();
    }
}