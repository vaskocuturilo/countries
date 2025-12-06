package com.example.apicountries.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/countries")
public class CountryRestControllerV1 {

    @GetMapping("/{name}")
    public ResponseEntity<?> getPersonByUid(@PathVariable("name") String name) {
        return ResponseEntity.ok("Works by %s".formatted(name));
    }

    @GetMapping
    public ResponseEntity<List<?>> getPersons() {
        return ResponseEntity.ok(List.of("List works"));
    }

    @PostMapping("/exit")
    public ResponseEntity<?> exit() {
        System.exit(1);
        return ResponseEntity.ok().build();
    }
}
