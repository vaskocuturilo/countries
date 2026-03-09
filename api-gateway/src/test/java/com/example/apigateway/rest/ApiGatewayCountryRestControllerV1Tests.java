package com.example.apigateway.rest;

import com.example.apigateway.client.ConsumerClient;
import com.example.apigateway.client.CountryClient;
import com.example.apigateway.dto.CountryDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import utils.DataUtils;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@ComponentScan({"com.example.reactiveproject.errorhandling"})
@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = {ApiGatewayCountryRestControllerV1.class})
class ApiGatewayCountryRestControllerV1Tests {

    @MockitoBean
    private CountryClient countryClient;

    @MockitoBean
    private ConsumerClient consumerClient;

    @Autowired
    private WebTestClient webTestClient;

    private static final String ENDPOINT_PATH = "/api/v1/countries";

    @Test
    @DisplayName("Test get all countries functionality")
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
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$[0].cca2").isNotEmpty()
                .jsonPath("$[0].cca3").isNotEmpty()
                .jsonPath("$[0].capital").isNotEmpty()
                .jsonPath("$[0].region").isNotEmpty()
                .jsonPath("$[0].subregion").isNotEmpty()
                .jsonPath("$[0].population").isNotEmpty()
                .jsonPath("$.size()").isEqualTo(1);

    }

    @Test
    @DisplayName("Test get country by alpha code functionality")
    void givenName_whenGetCountryByAlphaCode_thenSuccessResponse() {
        //given
        final CountryDto countryDto = DataUtils.getTuvaluDtoTransient();

        BDDMockito.given(countryClient.getCountryByAlphaCode(anyString())).willReturn((Mono.just(countryDto)));

        //when
        final WebTestClient.ResponseSpec result = webTestClient
                .get()
                .uri(ENDPOINT_PATH + "/" + countryDto.getAlpha2())
                .exchange();

        //then
        result.expectStatus().isOk()
                .expectBody().consumeWith(System.out::println)
                .jsonPath("$.cca2").isNotEmpty()
                .jsonPath("$.cca3").isNotEmpty()
                .jsonPath("$.cca2").isEqualTo("TU")
                .jsonPath("$.cca3").isEqualTo("TUV");
    }

    @Test
    @DisplayName("Test get country by alpha code with incorrect alpha code functionality")
    void givenIncorrectAlphaCode_whenGetCountryByAlphaCode_thenErrorResponse() {
        //given
        BDDMockito.given(countryClient.getCountryByAlphaCode(anyString()))
                .willReturn(Mono.error(new IllegalStateException("The country is not found")));

        //when
        final WebTestClient.ResponseSpec result = webTestClient.get().uri(ENDPOINT_PATH + "/ZZ").exchange();

        //then
        result.expectStatus().is5xxServerError()
                .expectBody().consumeWith(System.out::println)
                .jsonPath("$.error").isEqualTo("Internal Server Error");
    }

    @Test
    @DisplayName("Test init process - success")
    void whenInitProcess_thenSuccessResponse() {
        // given
        BDDMockito.given(countryClient.getProcess())
                .willReturn(Mono.just(Map.of("message", "Process completed successfully")));

        // when
        final WebTestClient.ResponseSpec result = webTestClient.post()
                .uri(ENDPOINT_PATH + "/process")
                .exchange();

        // then
        result.expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.message").isEqualTo("Process completed successfully");
    }

    @Test
    @DisplayName("Test aborted init process functionality")
    void givenInitProcessNoData_whenPostInitProcess_thenAbortedResponse() {
        //given
        BDDMockito.given(countryClient.getProcess())
                .willReturn(Mono.just(Map.of("message", "Process aborted: No data received from API")));


        //when
        final WebTestClient.ResponseSpec result = webTestClient.post()
                .uri(ENDPOINT_PATH + "/process")
                .exchange();

        //then
        result.expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.message").isEqualTo("Process aborted: No data received from API");
    }

    @Test
    @DisplayName("Test send country to Kafka - success")
    void givenCountryDto_whenSendToKafka_thenSuccessResponse() {
        // given
        final CountryDto country = DataUtils.getTuvaluDtoPersisted();

        BDDMockito.given(countryClient.sendAsyncKafkaMessage(any()))
                .willReturn(Mono.just(Map.of("message", "Message confirmed by Kafka")));

        // when
        final WebTestClient.ResponseSpec result = webTestClient.post()
                .uri(ENDPOINT_PATH + "/send")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(country)
                .exchange();

        // then
        result.expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.message").isEqualTo("Message confirmed by Kafka");
    }

    @Test
    @DisplayName("Test receive from Kafka - success")
    void whenReceiveFromKafka_thenSuccessResponse() {
        // given
        BDDMockito.given(consumerClient.receiveAsyncKafkaMessage())
                .willReturn(Mono.just(Map.of("message", "Received")));

        // when
        final WebTestClient.ResponseSpec result = webTestClient.get()
                .uri(ENDPOINT_PATH + "/receive")
                .exchange();

        // then
        result.expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.message").isEqualTo("Received");
    }
}