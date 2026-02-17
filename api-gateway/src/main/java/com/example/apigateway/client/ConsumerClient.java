package com.example.apigateway.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConsumerClient {

    private final WebClient webClient;

    @Value("${consumer.service.url}")
    private String consumerServiceUrl;

    public Mono<Object> receiveAsyncKafkaMessage() {
        return webClient.post().uri(consumerServiceUrl + "/api/v1/consumers/receive")
                .retrieve()
                .bodyToMono(Object.class);

    }
}
