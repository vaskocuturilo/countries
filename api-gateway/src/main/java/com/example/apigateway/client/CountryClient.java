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
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

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
                .doOnNext(body -> log.info("IN getPersonByUid - person with uid {} obtained", name));
    }

    public List<Object> getCountries() {
        ResponseEntity<List<Object>> response = restTemplate.exchange(
                countryServiceUrl + "/api/v1/countries", HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {
                });

        final List<Object> body = response.getBody();
        if (Objects.isNull(body)) {
            throw new IllegalStateException("The result cannot be null");
        }
        log.info("IN getPersons - {} persons", body.size());

        return body;
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
