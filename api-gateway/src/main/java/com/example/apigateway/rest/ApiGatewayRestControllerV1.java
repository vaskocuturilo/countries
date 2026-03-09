package com.example.apigateway.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/gateway")
public class ApiGatewayRestControllerV1 {

    private final ApplicationShutdownManager applicationShutdownManager;

    public ApiGatewayRestControllerV1(ApplicationShutdownManager applicationShutdownManager) {
        this.applicationShutdownManager = applicationShutdownManager;
    }

    @PostMapping("/exit")
    public ResponseEntity<?> exit() {
        applicationShutdownManager.shutDown();

        return ResponseEntity.ok().build();
    }
}
