package com.example.apigateway.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConsumerClient {

    private final RestTemplate restTemplate;

    @Value("${consumer.service.url}")
    private String consumerServiceUrl;

    public Object receiveAsyncKafkaMessage() {
        final ResponseEntity<Void> response = restTemplate.exchange(consumerServiceUrl + "/api/v1/consumers/receive",
                HttpMethod.GET, null, new ParameterizedTypeReference<>() {
                });

        final Object body = response.getBody();

        log.info("IN getProcess - {} ", body);

        return body;

    }
}
