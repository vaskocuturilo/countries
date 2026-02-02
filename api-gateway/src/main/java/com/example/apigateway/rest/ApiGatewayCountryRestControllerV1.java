package com.example.apigateway.rest;

import com.example.apigateway.client.ConsumerClient;
import com.example.apigateway.client.CountryClient;
import com.example.apigateway.dto.CountryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/countries")
public class ApiGatewayCountryRestControllerV1 {

    private final CountryClient countryClient;
    private final ConsumerClient consumerClient;

    @GetMapping("/{name}")
    public Mono<ResponseEntity<CountryDto>> getCountryByName(@PathVariable("name") String name) {
        return countryClient.getCountryByName(name).map(ResponseEntity::ok);
    }

    @GetMapping
    public Flux<CountryDto> getCountries() {
        return countryClient.getCountries();
    }

    @PostMapping("/process")
    public ResponseEntity<Object> getProcess() {
        final Object process = countryClient.getProcess();

        return ResponseEntity.ok(process);
    }

    @PostMapping("/send")
    public ResponseEntity<Object> sendCountryEntityToKafka() {
        final Object process = countryClient.sendAsyncKafkaMessage();

        return ResponseEntity.ok(process);
    }

    @PostMapping("/receive")
    public ResponseEntity<Object> receiveCountryEntityFromKafka() {
        final Object process = consumerClient.receiveAsyncKafkaMessage();

        return ResponseEntity.ok(process);
    }
}
