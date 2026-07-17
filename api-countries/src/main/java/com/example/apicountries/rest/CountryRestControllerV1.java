package com.example.apicountries.rest;

import com.example.apicountries.annotation.RateLimiter;
import com.example.apicountries.dto.CountryDto;
import com.example.apicountries.dto.PageResponse;
import com.example.apicountries.service.CountryServiceImplementation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@RestController
@RequestMapping("/api/v1/countries")
@Tag(name = "CountryApi", description = "Country management")
public class CountryRestControllerV1 {

    private final CountryServiceImplementation countryService;

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "alpha2",
            "alpha3",
            "capital",
            "region",
            "subregion",
            "area",
            "population",
            "independent"
    );

    public CountryRestControllerV1(CountryServiceImplementation countryService) {
        this.countryService = countryService;
    }

    @PostMapping("/process")
    @Operation(summary = "Get all countries from external resource")
    @RateLimiter(key = "process")
    public ResponseEntity<Map<String, String>> initProcess() {
        return ResponseEntity.ok(countryService.initProcess());
    }

    @GetMapping("/{alphaCode}")
    @Operation(summary = "Get country by alphaCode")
    @ApiResponse(responseCode = "200", description = "Country found")
    @ApiResponse(responseCode = "404", description = "Country not found")
    public ResponseEntity<CountryDto> getCountryByAlphaCode(@PathVariable("alphaCode") String alphaCode) {
        return ResponseEntity.ok(countryService.getCountryByAlphaCode(alphaCode));
    }

    @GetMapping
    @Operation(summary = "Get all countries")
    @ApiResponse(responseCode = "200", description = "Country found")
    @ApiResponse(responseCode = "204", description = "Empty country list")
    public ResponseEntity<PageResponse<CountryDto>> getCountries(@RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "10") int size,
                                                                 @RequestParam(defaultValue = "alpha2") String sortBy,
                                                                 @RequestParam(defaultValue = "asc") String direction) {

        final int safePage = Math.max(page, 0);

        final int safeSize = Math.clamp(size, 1, 100);

        String safeSortBy = ALLOWED_SORT_FIELDS.contains(sortBy) ? sortBy : "alpha2";

        final Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(safeSortBy).descending()
                : Sort.by(safeSortBy).ascending();

        final Pageable pageable = PageRequest.of(safePage, safeSize, sort);

        log.debug("Get All events page={}, size={}, sortBy={}, direction={}",
                page, size, sortBy, direction);


        return ResponseEntity.ok(countryService.getAllCountries(pageable));
    }

    @PostMapping("/send")
    @Operation(summary = "Send country to Kafka")
    @ApiResponse(responseCode = "200", description = "Success send")
    @ApiResponse(responseCode = "500", description = "Error send")
    @RateLimiter(key = "send country data")
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
