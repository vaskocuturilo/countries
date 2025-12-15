package com.example.apicountries.rest;

import com.example.apicountries.client.CountryApiClient;
import com.example.apicountries.dto.CountryDto;
import com.example.apicountries.service.CountryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/countries")
public class CountryRestControllerV1 {

    private final CountryApiClient countryApiClient;

    private final CountryService countryService;

    public CountryRestControllerV1(CountryApiClient countryApiClient, CountryService countryService) {
        this.countryApiClient = countryApiClient;
        this.countryService = countryService;
    }

    @PostMapping("/process")
    public ResponseEntity<String> initProcess() {
        countryService.initProcess();
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body("Init process finished");
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
