package com.example.apicountries.client;

import com.example.apicountries.dto.CountryDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class CountryApiClient {

    private static final Logger LOGGER = Logger.getLogger(CountryApiClient.class.getName());

    private final RestTemplate restTemplate;

    @Value("${country.url}")
    private String countryUrl;

    public CountryApiClient(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public List<CountryDto> getCountries() {
        Objects.requireNonNull(this.countryUrl, "The countryUrl cannot be null or empty");

        if (this.countryUrl.isEmpty()) {
            LOGGER.log(Level.WARNING, "The URL is not defined");

            return Collections.emptyList();
        }

        final CountryDto[] countries = restTemplate.getForObject(countryUrl, CountryDto[].class);

        if (Objects.nonNull(countries)) {

            LOGGER.log(Level.INFO, "Countries list contains {0}", countries.length);

            return Arrays.asList(countries);
        }

        LOGGER.log(Level.WARNING, "The countries list is empty");

        return Collections.emptyList();
    }
}
