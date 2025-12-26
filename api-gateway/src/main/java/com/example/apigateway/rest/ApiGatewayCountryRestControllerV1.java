package com.example.apigateway.rest;

import com.example.apigateway.client.CountryClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/countries")
public class ApiGatewayCountryRestControllerV1 {

    private final CountryClient countryClient;

    @GetMapping("/{name}")
    public Mono<ResponseEntity<Object>> getCountryByName(@PathVariable("name") String name) {
        return countryClient.getCountryByName(name).map(ResponseEntity::ok);
    }

    @GetMapping
    public ResponseEntity<List<Object>> getCountries() {
        final List<Object> persons = countryClient.getCountries();
        return ResponseEntity.ok(persons);
    }

    @PostMapping("/process")
    public ResponseEntity<Object> getProcess() {
        final Object process = countryClient.getProcess();

        return ResponseEntity.ok(process);
    }
}
