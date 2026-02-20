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

    @GetMapping("/{alphaCode}")
    public Mono<ResponseEntity<CountryDto>> getCountryByName(@PathVariable("alphaCode") String alphaCode) {
        return countryClient.getCountryByAlphaCode(alphaCode).map(ResponseEntity::ok);
    }

    @GetMapping
    public Flux<CountryDto> getCountries() {
        return countryClient.getCountries().flatMap(Mono::just);
    }

    @PostMapping("/process")
    public ResponseEntity<Object> getProcess() {
        final Object process = countryClient.getProcess();

        return ResponseEntity.ok(process);
    }

    @PostMapping("/send")
    public ResponseEntity<Object> sendEntityToKafka(@RequestBody final CountryDto country) {
        return ResponseEntity.ok(countryClient.sendAsyncKafkaMessage(country));
    }

    @GetMapping("/receive")
    public ResponseEntity<Object> receiveEntityFromKafka() {
        return ResponseEntity.ok(consumerClient.receiveAsyncKafkaMessage());
    }
}
