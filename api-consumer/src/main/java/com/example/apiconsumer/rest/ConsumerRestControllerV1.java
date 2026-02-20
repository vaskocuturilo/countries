package com.example.apiconsumer.rest;

import com.example.apiconsumer.dto.CountryDto;
import com.example.apiconsumer.service.ConsumerServiceImplementation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/api/v1/consumers")
public class ConsumerRestControllerV1 {

    private final ConsumerServiceImplementation serviceImplementation;

    public ConsumerRestControllerV1(ConsumerServiceImplementation serviceImplementation) {
        this.serviceImplementation = serviceImplementation;
    }

    @GetMapping("/receive")
    public ResponseEntity<CountryDto> pullData() {
        CountryDto country = serviceImplementation.pullFromBroker();

        if (Objects.isNull(country)) {
            log.info("IN pullData: The message has not been pulled from the broker");
            return ResponseEntity.noContent().build();
        }
        log.info("IN pullData: The message has been pulled from the broker");

        return ResponseEntity.ok(country);
    }
}
