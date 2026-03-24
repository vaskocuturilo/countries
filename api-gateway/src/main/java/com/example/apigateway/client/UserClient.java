package com.example.apigateway.client;

import com.example.apigateway.dto.CredentialsDto;
import com.example.apigateway.dto.SignUpDto;
import com.example.apigateway.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.DefaultUriBuilderFactory;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserClient {

    private final WebClient webClient;

    private final DefaultUriBuilderFactory uriFactory;

    private static final String GOAL_ENDPOINT = "/api/v1/users";

    public Mono<UserDto> registerNewUser(final SignUpDto sign) {
        return webClient
                .post()
                .uri(uriFactory.builder().path(GOAL_ENDPOINT).path("/register").build())
                .bodyValue(sign)
                .retrieve()
                .bodyToMono(UserDto.class)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NO_CONTENT)))
                .doOnNext(body -> log.info("IN registerNewUser - user with name {} and body {}", sign.firstName(), body));
    }

    public Mono<UserDto> loginUser(final CredentialsDto credentials) {
        return webClient.post()
                .uri(uriFactory.builder().path(GOAL_ENDPOINT).path("/login").build())
                .bodyValue(credentials)
                .retrieve()
                .bodyToMono(UserDto.class);
    }
}
