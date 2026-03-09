package com.example.apigateway.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ApplicationShutdownManager {

    private final ApplicationContext applicationContext;

    public ApplicationShutdownManager(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void shutDown() {
        SpringApplication.exit(applicationContext, () -> 0);
    }
}
