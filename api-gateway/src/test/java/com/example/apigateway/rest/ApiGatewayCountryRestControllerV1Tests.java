package com.example.apigateway.rest;

import com.example.apigateway.client.CountryClient;
import com.example.apigateway.dto.CountryDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Description;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import utils.DataUtils;

import static org.mockito.ArgumentMatchers.anyString;

@ComponentScan({"com.example.reactiveproject.errorhandling"})
@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = {ApiGatewayCountryRestControllerV1.class})
class ApiGatewayCountryRestControllerV1Tests {

    @Autowired
    private final CountryClient countryClient;

    @Autowired
    private WebTestClient webTestClient;

    private static final String ENDPOINT_PATH = "/api/v1/countries";

    ApiGatewayCountryRestControllerV1Tests(CountryClient countryClient) {
        this.countryClient = countryClient;
    }

    @Test
    @Description("Test get all countries functionality")
    void givenCountry_whenGetCountries_thenSuccessResponse() {
        //given
        final CountryDto countryDto = DataUtils.getTuvaluDtoPersisted();

        BDDMockito.given(countryClient.getCountries()).willReturn((Flux.just(countryDto)));

        //when
        final WebTestClient.ResponseSpec result = webTestClient.get()
                .uri(ENDPOINT_PATH)
                .exchange();

        //then
        result.expectStatus().isOk()
                .expectBody().consumeWith(System.out::println)
                .jsonPath("$[*].alpha2").isNotEmpty()
                .jsonPath("$[*].alpha3").isNotEmpty()
                .jsonPath("$.size()").isEqualTo(1);
    }

    @Test
    @Description("Test get country by alpha code functionality")
    void givenName_whenGetCountryByAlphaCode_thenSuccessResponse() {
        //given
        final CountryDto countryDto = DataUtils.getTuvaluDtoTransient();

        BDDMockito.given(countryClient.getCountryByName(anyString())).willReturn((Mono.just(countryDto)));

        //when
        final WebTestClient.ResponseSpec result = webTestClient
                .get()
                .uri(ENDPOINT_PATH + "/" + countryDto.getAlpha2())
                .exchange();

        //then
        result.expectStatus().isOk()
                .expectBody().consumeWith(System.out::println)
                .jsonPath("$.alpha2").isNotEmpty()
                .jsonPath("$.alpha3").isNotEmpty()
                .jsonPath("$.alpha2").isEqualTo("TU")
                .jsonPath("$.alpha3").isEqualTo("TUV");
    }

    @Test
    @Description("Test get country by alpha code with incorrect alpha code functionality")
    void givenIncorrectAlphaCode_whenGetCountryByAlphaCode_thenErrorResponse() {
        //given
        BDDMockito.given(countryClient.getCountryByName(anyString()))
                .willReturn(Mono.error(new IllegalStateException("The country is not found")));

        //when
        final WebTestClient.ResponseSpec result = webTestClient.get().uri(ENDPOINT_PATH + "/100").exchange();

        //then
        result.expectStatus().isNotFound()
                .expectBody().consumeWith(System.out::println)
                .jsonPath("$.errors.[0].message").isEqualTo("The country is not found");
    }
}