package com.example.apigateway.rest;

import com.example.apigateway.client.CountryClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/countries")
public class ApiGatewayCountryRestControllerV1 {

    private final CountryClient countryClient;

    @GetMapping("/{name}")
    public ResponseEntity<?> getCountryByName(@PathVariable("name") String name) {
        final Object personByUid = countryClient.getCountryByName(name);

        return ResponseEntity.ok(personByUid);
    }

    @GetMapping
    public ResponseEntity<List<?>> getCountries() {
        final List<?> persons = countryClient.getCountries();
        return ResponseEntity.ok(persons);
    }
}
