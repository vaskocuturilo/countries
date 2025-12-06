package com.example.apigateway.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class CountryClient {

    private final RestTemplate restTemplate;

    @Value("${country.service.url}")
    private String countryServiceUrl;

    public Object getCountryByName(String name) {
        ResponseEntity<String> response = restTemplate.exchange(
                countryServiceUrl + "/api/v1/countries/" + name, HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {
                });

        Object body = response.getBody();
        log.info("IN getPersonByUid - person with uid {} obtained", name);

        return body;
    }

    public List<?> getCountries() {
        ResponseEntity<List<?>> response = restTemplate.exchange(
                countryServiceUrl + "/api/v1/countries", HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {
                });

        final List<?> body = response.getBody();
        if (Objects.isNull(body)) {
            throw new IllegalStateException("The result cannot be null");
        }
        log.info("IN getPersons - {} persons", body.size());

        return body;
    }
}
