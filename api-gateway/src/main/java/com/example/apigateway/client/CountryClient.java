package com.example.apigateway.client;

import com.example.apigateway.dto.CountryDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.DefaultUriBuilderFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class CountryClient {

    private final WebClient webClient;

    private final DefaultUriBuilderFactory uriFactory;

    private static final String GOAL_ENDPOINT = "/api/v1/countries";

    public Mono<CountryDto> getCountryByAlphaCode(String alphaCode) {
        return webClient
                .get()
                .uri(uriFactory.builder().path(GOAL_ENDPOINT).path("/" + alphaCode).build())
                .retrieve().bodyToMono(CountryDto.class)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NO_CONTENT)))
                .doOnNext(body -> log.info("IN getCountryByName - country with name {} and body {}", alphaCode, body));
    }

    public Flux<CountryDto> getCountries() {
        return webClient
                .get()
                .uri(uriFactory.builder().path(GOAL_ENDPOINT).build())
                .retrieve().bodyToFlux(CountryDto.class)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NO_CONTENT)))
                .doOnNext(countries -> log.debug("IN getCountries - country received {}", countries))
                .doOnComplete(() -> log.info("IN getCountries - countries fetched successfully"));
    }

    public Mono<Object> getProcess() {
        return webClient.post()
                .uri(uriFactory.builder().path(GOAL_ENDPOINT).path("/process").build())
                .retrieve()
                .bodyToMono(Object.class);
    }

    public Mono<Object> sendAsyncKafkaMessage(final CountryDto country) {
        return webClient.post()
                .uri(uriFactory.builder().path(GOAL_ENDPOINT).path("/send").build())
                .bodyValue(country)
                .retrieve()
                .bodyToMono(Object.class);
    }
}
