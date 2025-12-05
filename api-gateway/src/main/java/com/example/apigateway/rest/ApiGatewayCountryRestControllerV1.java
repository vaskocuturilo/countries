package com.example.apigateway.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/counties")
public class ApiGatewayCountryRestControllerV1 {


    @GetMapping("/{uid}")
    public ResponseEntity<?> getCountryByUid(@PathVariable("uid") String uid) {
        return ResponseEntity.ok("");
    }

    @GetMapping
    public ResponseEntity<List<?>> getCountries() {
        return ResponseEntity.ok(List.of(Optional.empty()));
    }
}
