package com.example.apicountries.rest;

import com.example.apicountries.dto.CountryDto;
import com.example.apicountries.service.CountryServiceImplementation;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/countries")
public class CountryRestControllerV1 {

    private final CountryServiceImplementation countryService;

    public CountryRestControllerV1(CountryServiceImplementation countryService) {
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

    @GetMapping("/{alphaCode}")
    public ResponseEntity<CountryDto> getCountryByAlphaCode(@PathVariable("alphaCode") String alphaCode) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(countryService.getCountryByAlphaCode(alphaCode));
    }

    @GetMapping
    public ResponseEntity<List<CountryDto>> getCountries() {
        return ResponseEntity.status(HttpStatus.OK).body(countryService.getAllCountries());
    }
}
