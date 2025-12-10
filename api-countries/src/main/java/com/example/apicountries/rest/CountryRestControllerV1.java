package com.example.apicountries.rest;

import com.example.apicountries.client.CountryApiClient;
import com.example.apicountries.dto.CountryDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/countries")
public class CountryRestControllerV1 {

    private final CountryApiClient countryApiClient;

    public CountryRestControllerV1(CountryApiClient countryApiClient) {
        this.countryApiClient = countryApiClient;
    }

    @GetMapping("/{name}")
    public ResponseEntity<CountryDto> getCountryByName(@PathVariable("name") String name) {
        return ResponseEntity.ok(new CountryDto());
    }

    @GetMapping
    public ResponseEntity<List<CountryDto>> getCountries() {
        return ResponseEntity.ok(countryApiClient.getCountries());
    }
}
