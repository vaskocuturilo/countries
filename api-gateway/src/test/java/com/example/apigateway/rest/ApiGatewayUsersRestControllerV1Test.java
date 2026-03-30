package com.example.apigateway.rest;

import com.example.apigateway.client.UserClient;
import com.example.apigateway.dto.CredentialsDto;
import com.example.apigateway.dto.SignUpDto;
import com.example.apigateway.dto.UserDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import utils.DataUtils;

@ComponentScan({"com.example.reactiveproject.errorhandling"})
@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = {ApiGatewayUsersRestControllerV1.class})
class ApiGatewayUsersRestControllerV1Test {

    @MockitoBean
    private UserClient userClient;

    @Autowired
    private WebTestClient webTestClient;

    private static final String ENDPOINT_PATH = "/api/v1/users";

    @Test
    @DisplayName("Test register new user functionality")
    void givenSignUpData_whenRegisterUser_thenSuccessResponse() {
        //given
        final SignUpDto signUpDto = DataUtils.simpleSignBuilder();

        final UserDto userDto = DataUtils.simpleUserBuilder();

        BDDMockito.given(userClient.registerNewUser(signUpDto)).willReturn((Mono.just(userDto)));

        //when
        final WebTestClient.ResponseSpec result = webTestClient.post()
                .uri(ENDPOINT_PATH)
                .body(Mono.just(signUpDto), SignUpDto.class).exchange();


        //then
        result.expectStatus().isNotFound()
                .expectBody()
                .consumeWith(System.out::println);
    }

    @Test
    @DisplayName("Test register new user functionality")
    void givenUserData_whenLoginUser_thenSuccessResponse() {
        //given
        final CredentialsDto credentialsDto = DataUtils.simpleCredentialBuilder();

        final UserDto userDto = DataUtils.simpleUserBuilder();

        BDDMockito.given(userClient.loginUser(credentialsDto)).willReturn((Mono.just(userDto)));

        //when
        final WebTestClient.ResponseSpec result = webTestClient.post()
                .uri(ENDPOINT_PATH)
                .body(Mono.just(credentialsDto), CredentialsDto.class).exchange();


        //then
        result.expectStatus().isNotFound()
                .expectBody()
                .consumeWith(System.out::println);
    }

}