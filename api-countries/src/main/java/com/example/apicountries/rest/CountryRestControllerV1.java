package com.example.apicountries.rest;

import com.example.apicountries.dto.CountryDto;
import com.example.apicountries.service.CountryServiceImplementation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@RestController
@RequestMapping("/api/v1/countries")
public class CountryRestControllerV1 {

    private final CountryServiceImplementation countryService;

    public CountryRestControllerV1(CountryServiceImplementation countryService) {
        this.countryService = countryService;
    }

    @PostMapping("/process")
    public ResponseEntity<Map<String, String>> initProcess() {
        return ResponseEntity.ok(countryService.initProcess());
    }

    @GetMapping("/{alphaCode}")
    public ResponseEntity<CountryDto> getCountryByAlphaCode(@PathVariable("alphaCode") String alphaCode) {
        return ResponseEntity.ok(countryService.getCountryByAlphaCode(alphaCode));
    }

    @GetMapping
    public ResponseEntity<List<CountryDto>> getCountries() {
        return ResponseEntity.ok(countryService.getAllCountries());
    }

    @PostMapping("/send")
    public ResponseEntity<Map<String, String>> sendCountryEntityData(@RequestBody final CountryDto country) {
        try {
            countryService.triggerSend(country).get(5, TimeUnit.SECONDS);

            log.info("The message {} has been send to the Kafka", country);

            return ResponseEntity.ok(Map.of("message", "Message confirmed by Kafka"));

        } catch (ExecutionException | InterruptedException | TimeoutException exception) {
            log.error("Kafka delivery failed for country: {}", country.getAlpha2(), exception);

            if (exception instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Kafka failed: " + exception.getMessage()));
        }
    }
}
