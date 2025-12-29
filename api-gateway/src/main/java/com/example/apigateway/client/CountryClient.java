package com.example.apigateway.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class CountryClient {

    private final RestTemplate restTemplate;

    private final WebClient webClient;

    @Value("${country.service.url}")
    private String countryServiceUrl;

    public Mono<Object> getCountryByName(String name) {
        return webClient
                .get()
                .uri(countryServiceUrl + "/api/v1/countries/" + name)
                .retrieve().bodyToMono(Object.class)
                .doOnNext(body -> log.info("IN getCountryByName - country with name {} and body {}", name, body));
    }

    public Flux<Object> getCountries() {
        return webClient
                .get()
                .uri(countryServiceUrl + "/api/v1/countries/")
                .retrieve().bodyToFlux(Object.class)
                .switchIfEmpty(Mono.error(new IllegalStateException("The result cannot be null")))
                .doOnNext(countries -> log.debug("IN getCountries - country received {}", countries))
                .doOnComplete(() -> log.info("IN getCountries - countries fetched successfully"));
    }

    public Object getProcess() {
        final ResponseEntity<Void> response = restTemplate.exchange(countryServiceUrl + "/api/v1/countries/process",
                HttpMethod.POST, null, new ParameterizedTypeReference<>() {
                });

        final Object body = response.getBody();

        log.info("IN getProcess - {} ", body);

        return body;

    }
}
