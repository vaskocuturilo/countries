package com.example.apiconsumer.rest;

import com.example.apiconsumer.kafka.dto.CountryDto;
import com.example.apiconsumer.service.ConsumerServiceImplementation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/consumers")
public class ConsumerRestControllerV1 {

    private final ConsumerServiceImplementation serviceImplementation;

    public ConsumerRestControllerV1(ConsumerServiceImplementation serviceImplementation) {
        this.serviceImplementation = serviceImplementation;
    }

    @GetMapping("/receive")
    public ResponseEntity<String> receiveCountryEntityData(@RequestBody final CountryDto country) {
        serviceImplementation.triggerAsynchronousReceiveCountry(country);

        log.info("The message {} has been send to the pay system", country);

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body("The message to Kafka broker has been send successfully");
    }
}
